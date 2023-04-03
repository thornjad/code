(defproject lightrail "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://gitlab.com/thornjad/code/-/tree/main/lightrail"
  :license {:name "ISC"
            :url "https://jmthornton.net/LICENSE"}
  :dependencies [[org.clojure/clojure "1.10.3"]]
  :main ^:skip-aot lightrail.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
