#!/usr/bin/env lumo
(ns mastodon-bot.core
  (:require
   [cljs.core :refer [*command-line-args*]]
   [cljs.reader :as edn]
   [clojure.set :refer [rename-keys]]
   [clojure.string :as string]
   ["deasync" :as deasync]
   ["request" :as request]
   ["fs" :as fs]
   ["mastodon-api" :as mastodon]
   ["rss-parser" :as rss]
   ["twitter" :as twitter]))

(defn exit-with-error [error]
  (js/console.error error)
  (js/process.exit 1))

(defn find-config []
  (or (first *command-line-args*)
      (-> js/process .-env .-MASTODON_BOT_CONFIG)
      "config.edn"))

(def config (-> (find-config) (fs/readFileSync #js {:encoding "UTF-8"}) edn/read-string))

(def mastodon-config (:mastodon config))

(def mastodon-client (or (some-> mastodon-config clj->js mastodon.)
                         (exit-with-error "missing Mastodon client configuration!")))

(def content-filter-regexes (mapv re-pattern (:content-filters mastodon-config)))

(def append-screen-name? (boolean (:append-screen-name? mastodon-config)))

(def max-post-length (:max-post-length mastodon-config))

(defn blocked-content? [text]
 (boolean (some #(re-find % text) content-filter-regexes)))

(defn js->edn [data]
  (js->clj data :keywordize-keys true))

(defn trim-text [text]
  (cond

    (nil? max-post-length)
    text

    (> (count text) max-post-length)
    (reduce
     (fn [text word]
       (if (> (+ (count text) (count word)) (- max-post-length 3))
         (reduced (str text "..."))
         (str text " " word)))
     ""
     (clojure.string/split text #" "))

    :else text))

(defn delete-status [status]
  (.delete mastodon-client (str "statuses/" status) #js {}))

(defn resolve-url [[uri]]
  (try
    (or
      (some-> ((deasync request)
               #js {:method "GET"
                    :uri (if (string/starts-with? uri "https://") uri (str "https://" uri))
                    :followRedirect false})
              (.-headers)
              (.-location)
              (string/replace "?mbid=social_twitter" ""))
      uri)
    (catch js/Error _ uri)))

(def shortened-url-pattern #"(https?://)?(?:\S+(?::\S*)?@)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,}))\.?)(?::\d{2,5})?(?:[/?#]\S*)?")

(defn resolve-urls [text]
  (if (:resolve-urls? mastodon-config)
    (string/replace text shortened-url-pattern resolve-url)
    text))

(defn set-signature [text]
  (if-let [signature (:signature mastodon-config )]
    (str text "\n" signature)
    text))

(defn post-status
  ([status-text]
   (post-status status-text nil))
  ([status-text media-ids]
   (let [{:keys [sensitive signature visibility]} mastodon-config]
     (.post mastodon-client "statuses"
          (clj->js (merge {:status (-> status-text resolve-urls set-signature)}
                          (when media-ids {:media_ids media-ids})
                          (when sensitive {:sensitive sensitive})
                          (when visibility {:visibility visibility})))))))

(defn post-image [image-stream description callback]
  (-> (.post mastodon-client "media" #js {:file image-stream :description description})
      (.then #(-> % .-data .-id callback))))

(defn post-status-with-images
  ([status-text urls]
   (post-status-with-images status-text urls []))
  ([status-text [url & urls] ids]
   (if url
     (-> request
         (.get url)
         (.on "response"
           (fn [image-stream]
             (post-image image-stream status-text #(post-status-with-images status-text urls (conj ids %))))))
     (post-status status-text (not-empty ids)))))

(defn get-mastodon-timeline [callback]
  (.then (.get mastodon-client "timelines/home" #js {}) #(-> % .-data js->edn callback)))

(defn post-items [last-post-time items]
  (doseq [{:keys [text media-links]} (->> items
                                          (remove #(blocked-content? (:text %)))
                                          (filter #(> (:created-at %) last-post-time)))]
    (if media-links
      (post-status-with-images text media-links)
      (post-status text))))

(defn parse-tweet [{created-at            :created_at
                    text                  :full_text
                    {:keys [media]}       :extended_entities
                    {:keys [screen_name]} :user :as tweet}]
  {:created-at (js/Date. created-at)
   :text (trim-text (if append-screen-name? (str text "\n - " screen_name) text))
   :media-links (keep #(when (= (:type %) "photo") (:media_url_https %)) media)})

(defn post-tweets [last-post-time]
  (fn [error tweets response]
    (->> (js->edn tweets)
         (map parse-tweet)
         (post-items last-post-time))))

(defn strip-utm [news-link]
  (first (string/split news-link #"\?utm")))

(defn parse-feed [last-post-time parser [title url]]
  (-> (.parseURL parser url)
      (.then #(post-items
               last-post-time
               (for [{:keys [title isoDate pubDate content link]} (-> % js->edn :items)]
                 {:created-at (js/Date. (or isoDate pubDate))
                  :text (str (trim-text title) "\n\n" (strip-utm link))})))))

(defn twitter-client [access-keys]
  (try
    (twitter. (clj->js access-keys))
    (catch js/Error e
      (exit-with-error
       (str "failed to connect to Twitter: " (.-message e))))))

(get-mastodon-timeline
 (fn [timeline]
   (let [last-post-time (-> timeline first :created_at (js/Date.))]
     ;;post from Twitter
     (when-let [twitter-config (:twitter config)]
       (let [{:keys [access-keys accounts include-replies? include-rts?]} twitter-config
             client (twitter-client access-keys)]
         (doseq [account accounts]
           (.get client
                 "statuses/user_timeline"
                 #js {:screen_name account
                      :tweet_mode "extended"
                      :include_rts (boolean include-rts?)
                      :exclude_replies (not (boolean include-replies?))}
                 (post-tweets last-post-time)))))
     ;;post from RSS
     (when-let [feeds (some-> config :rss)]
       (let [parser (rss.)]
         (doseq [feed feeds]
           (parse-feed last-post-time parser feed)))))))

