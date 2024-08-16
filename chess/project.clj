(defproject chess "0.1.0-SNAPSHOT"
  :description "A simple chess engine"
  :url "https://github.com/thornjad/code/tree/main/chess"
  :license {:name "ISC License"
            :url "https://jmthornton.net/LICENSE"}
  :dependencies [[org.clojure/clojure "1.10.1"]]
  :plugins [[lein-cljfmt "0.9.2"]]
  :main ^:skip-aot chess.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
