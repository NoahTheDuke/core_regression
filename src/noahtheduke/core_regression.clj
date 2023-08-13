(ns noahtheduke.core-regression
  (:require
    [babashka.process :refer [shell]]
    [babashka.process.pprint]
    [clojure.tools.gitlibs :as gl]
    [clojure.java.io :as io]
    [clojure.data.xml :as xml]
    [clojure.string :as str]
    [clojure.tools.cli :as cli]
    [clojure.pprint :as pprint]))

(def clojure-libraries
  [
   {:name 'clojure/algo.generic
    :definition :mvn
    :git/url "https://github.com/clojure/algo.generic.git"
    :git/tag "algo.generic-0.1.3"}
   {:name 'clojure/algo.monads
    :definition :mvn
    :git/url "https://github.com/clojure/algo.monads.git"
    :git/tag "algo.monads-0.1.6"}
   {:name 'clojure/core.async
    :definition :mvn
    :git/url "https://github.com/clojure/core.async.git"
    :git/tag "v1.6.681"}
   {:name 'clojure/core.cache
    :definition :mvn
    :git/url "https://github.com/clojure/core.cache.git"
    :git/tag "v1.0.225"}
   {:name 'clojure/core.contracts
    :definition :mvn
    :git/url "https://github.com/clojure/core.contracts.git"
    :git/tag "core.contracts-0.0.6"}
   {:name 'clojure/core.logic
    :definition :mvn
    :git/url "https://github.com/clojure/core.logic.git"
    :git/tag "v1.0.1"}
   {:name 'clojure/core.match
    :definition :mvn
    :git/url "https://github.com/clojure/core.match.git"
    :git/tag "v1.0.1"}
   {:name 'clojure/core.memoize
    :definition :mvn
    :git/url "https://github.com/clojure/core.memoize.git"
    :git/tag "v1.0.257"}
   {:name 'clojure/core.rrb-vector
    :definition :mvn
    :git/url "https://github.com/clojure/core.rrb-vector.git"
    :git/tag "core.rrb-vector-0.1.2"}
   {:name 'clojure/core.unify
    :definition :mvn
    :git/url "https://github.com/clojure/core.unify.git"
    :git/tag "core.unify-0.5.7"}
   {:name 'clojure/data.avl
    :definition :mvn
    :git/url "https://github.com/clojure/data.avl.git"
    :git/tag "data.avl-0.1.0"}
   {:name 'clojure/data.codec
    :definition :mvn
    :git/url "https://github.com/clojure/data.codec.git"
    :git/tag "data.codec-0.1.1"}
   {:name 'clojure/data.csv
    :definition :mvn
    :git/url "https://github.com/clojure/data.csv.git"
    :git/tag "v1.0.1"}
   {:name 'clojure/data.finger-tree
    :definition :mvn
    :git/url "https://github.com/clojure/data.finger-tree.git"
    :git/tag "data.finger-tree-0.0.3"}
   {:name 'clojure/data.fressian
    :definition :mvn
    :git/url "https://github.com/clojure/data.fressian.git"
    :git/tag "data.fressian-1.0.0"}
   {:name 'clojure/data.generators
    :definition :mvn
    :git/url "https://github.com/clojure/data.generators.git"
    :git/tag "data.generators-1.0.0"}
   {:name 'clojure/data.int-map
    :definition :mvn
    :git/url "https://github.com/clojure/data.int-map.git"
    :git/tag "v1.2.1"}
   {:name 'clojure/data.json
    :definition :mvn
    :git/url "https://github.com/clojure/data.json.git"
    :git/tag "v2.4.0"}
   {:name 'clojure/data.priority-map
    :definition :mvn
    :git/url "https://github.com/clojure/data.priority-map.git"
    :git/tag "v1.1.0"}
   {:name 'clojure/data.xml
    :definition :mvn
    :git/url "https://github.com/clojure/data.xml.git"
    :git/tag "v0.2.0-alpha8"}
   {:name 'clojure/data.zip
    :definition :mvn
    :git/url "https://github.com/clojure/data.zip.git"
    :git/tag "data.zip-1.0.0"}
   {:name 'clojure/java.data
    :definition :mvn
    :git/url "https://github.com/clojure/java.data.git"
    :git/tag "v1.0.95"}
   {:name 'clojure/java.jdbc
    :definition :mvn
    :git/url "https://github.com/clojure/java.jdbc.git"
    :git/tag "java.jdbc-0.7.12"}
   {:name 'clojure/java.jmx
    :definition :mvn
    :git/url "https://github.com/clojure/java.jmx.git"
    :git/tag "java.jmx-1.0.0"}
   {:name 'clojure/math.combinatorics
    :definition :mvn
    :git/url "https://github.com/clojure/math.combinatorics.git"
    :git/tag "math.combinatorics-0.2.0"}
   {:name 'clojure/math.numeric-tower
    :definition :mvn
    :git/url "https://github.com/clojure/math.numeric-tower.git"
    :git/tag "math.numeric-tower-0.0.5"}
   {:name 'clojure/spec.alpha
    :definition :mvn
    :git/url "https://github.com/clojure/spec.alpha.git"
    :git/tag "v0.3.218"}
   {:name 'clojure/spec.alpha2
    :definition :mvn
    :git/url "https://github.com/clojure/spec-alpha2.git"
    :git/sha "4cbfa677c4cd66339f18e1c122222c05c69e0d8e"}
   {:name 'clojure/test.check
    :definition :mvn
    :git/url "https://github.com/clojure/test.check.git"
    :git/tag "v1.1.1"}
   {:name 'clojure/tools.analyzer
    :definition :mvn
    :git/url "https://github.com/clojure/tools.analyzer.git"
    :git/tag "v1.1.1"}
   {:name 'clojure/tools.build
    :definition :deps.edn
    :test-cmd "-X:test"
    :git/url "https://github.com/clojure/tools.build.git"
    :git/tag "v0.9.5"}
   {:name 'clojure/tools.cli
    :definition :mvn
    :git/url "https://github.com/clojure/tools.cli.git"
    :git/tag "v1.0.219"}
   {:name 'clojure/tools.deps
    :definition :mvn
    :git/url "https://github.com/clojure/tools.deps.git"
    :git/tag "v0.18.1354"}
   {:name 'clojure/tools.deps.graph
    :definition :mvn
    :git/url "https://github.com/clojure/tools.deps.graph.git"
    :git/tag "v1.1.76"}
   {:name 'clojure/test.generative
    :definition :mvn
    :git/url "https://github.com/clojure/test.generative.git"
    :git/tag "test.generative-1.0.0"}
   {:name 'clojure/tools.gitlibs
    :definition :mvn
    :git/url "https://github.com/clojure/tools.gitlibs.git"
    :git/tag "v2.5.197"}
   {:name 'clojure/tools.logging
    :definition :mvn
    :git/url "https://github.com/clojure/tools.logging.git"
    :git/tag "v1.2.4"}
   {:name 'clojure/tools.macro
    :definition :mvn
    :git/url "https://github.com/clojure/tools.macro.git"
    :git/tag "tools.macro-0.1.5"}
   {:name 'clojure/tools.namespace
    :definition :mvn
    :git/url "https://github.com/clojure/tools.namespace.git"
    :git/tag "v1.4.4"}
   {:name 'clojure/tools.reader
    :definition :mvn
    :git/url "https://github.com/clojure/tools.reader.git"
    :git/tag "v1.3.6"}
   {:name 'clojure/tools.trace
    :definition :mvn
    :git/url "https://github.com/clojure/tools.trace.git"
    :git/tag "tools.trace-0.7.11"}
   ])

(def clj-commons-libraries
  [
   {:name 'clj-commons/aleph
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/aleph.git"
    :git/tag "0.6.3"}
   {:name 'clj-commons/kibit
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/kibit.git"
    :git/sha "93a9ce52aaf7761a97528d0b4d412408d09d12a1"}
   {:name 'clj-commons/seesaw
    :definition :script
    :test-cmd "./lazytest.sh"
    :git/url "https://github.com/clj-commons/seesaw.git"
    :git/sha "38695ea1a590d84d877a50df8792f58e04fcbd02"}
   {:name 'clj-commons/manifold
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/manifold.git"
    :git/tag "0.4.1"}
   {:name 'clj-commons/etaoin
    :definition :script
    :test-cmd "bb test:jvm --suites unit --launch-virtual-display"
    :skip? true ;; can't do browser stuff
    :git/url "https://github.com/clj-commons/etaoin.git"
    :git/tag "v1.0.40"}
   {:name 'clj-commons/hickory
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/hickory.git"
    :git/tag "Release-0.7.3"}
   {:name 'clj-commons/claypoole
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/claypoole.git"
    :git/tag "Release-1.2.2"}
   {:name 'clj-commons/pretty
    :definition :deps.edn
    :test-cmd "-X:test"
    :git/url "https://github.com/clj-commons/pretty.git"
    :git/tag "2.1"}
   {:name 'clj-commons/rewrite-clj
    :definition :deps.edn
    :test-cmd "-M:test-common:kaocha --reporter documentation"
    :git/url "https://github.com/clj-commons/rewrite-clj.git"
    :git/tag "v1.1.47"}
   {:name 'clj-commons/potemkin
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/potemkin.git"
    :git/tag "0.4.6"}
   {:name 'clj-commons/pomegranate
    :definition :script
    :test-cmd "bb test"
    :git/url "https://github.com/clj-commons/pomegranate.git"
    :git/tag "v1.2.23"}
   {:name 'clj-commons/gloss
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/gloss.git"
    :git/tag "Release-0.3.6"}
   {:name 'clj-commons/camel-snake-kebab
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/camel-snake-kebab.git"
    :git/tag "version-0.4.3"}
   {:name 'clj-commons/byte-stream
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/byte-streams.git"
    :git/tag "Release-0.3.4"}
   {:name 'clj-commons/cljss
    :definition :lein
    :test-cmd "test"
    :skip? true ;; requires phantomjs
    :git/url "https://github.com/clj-commons/cljss.git"
    :git/tag "v1.6.3"}
   {:name 'clj-commons/durable-queue
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/durable-queue.git"
    :git/tag "0.1.5"}
   {:name 'clj-commons/useful
    :definition :lein
    :test-cmd "test"
    :skip? true ;; using sun.misc.* fails to compile under java 11+
    :git/url "https://github.com/clj-commons/useful.git"
    :git/tag "0.11.6"}
   {:name 'clj-commons/citrus
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/citrus.git"
    :git/tag "Release-3.3.0"}
   {:name 'clj-commons/ordered
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/ordered.git"
    :git/tag "Release-1.15.11"}
   {:name 'clj-commons/dirigiste
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/dirigiste.git"
    :git/tag "1.0.3"}
   {:name 'clj-commons/primitive-math
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/primitive-math.git"
    :git/tag "Release-1.0.1"}
   {:name 'clj-commons/iapetos
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/iapetos.git"
    :git/tag "Release-0.1.13"}
   {:name 'clj-commons/humanize
    :definition :deps.edn
    :test-cmd "-X:test"
    :git/url "https://github.com/clj-commons/humanize.git"
    :git/tag "1.0"}
   {:name 'clj-commons/digest
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/digest.git"
    :git/tag "Release-1.4.100"}
   {:name 'clj-commons/clj-yaml
    :definition :deps.edn
    :setup "clojure -T:build compile-java"
    :test-cmd "-M:test"
    :git/url "https://github.com/clj-commons/clj-yaml.git"
    :git/tag "v1.0.27"}
   {:name 'clj-commons/byte-transforms
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/byte-transforms.git"
    :git/tag "Release-0.2.2"}
   {:name 'clj-commons/ring-buffer
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/ring-buffer.git"
    :git/tag "1.3.1"}
   {:name 'clj-commons/tentacles
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/tentacles.git"
    :git/tag "0.6.9"}
   ])

(def popular-libraries
  [
   {:name 'engelberg/instaparse
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/Engelberg/instaparse.git"
    :git/tag "v1.4.12"}
   {:name 'cerner/clara-rules
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/cerner/clara-rules.git"
    :git/tag "0.22.1"}
   {:name 'duct-framework/core
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/duct-framework/core.git"
    :git/tag "0.8.0"}
   {:name 'http-kit/http-kit
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/http-kit/http-kit.git"
    :git/tag "v2.7.0"}
   {:name 'marick/midje
    :definition :lein
    :test-cmd "midje"
    :git/url "https://github.com/marick/Midje.git"
    :git/sha "34819ae8d24a11b0f953d461f94e09a2638ff385"}
   {:name 'metosin/reitit
    :definition :lein
    :test-cmd "bat-test"
    :git/url "https://github.com/metosin/reitit.git"
    :git/tag "0.7.0-alpha5"}
   {:name 'metosin/malli
    :definition :deps.edn
    :test-cmd "-M:test -m kaocha.runner"
    :git/url "https://github.com/metosin/malli.git"
    :git/tag "0.11.0"}
   {:name 'metosin/compojure-api
    :definition :lein
    :test-cmd "midje"
    :git/url "https://github.com/metosin/compojure-api.git"
    :git/tag "2.0.0-alpha31"}
   {:name 'metosin/spec-tools
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/metosin/spec-tools.git"
    :git/tag "0.10.5"}
   {:name 'metosin/muuntaja
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/metosin/muuntaja.git"
    :git/tag "0.6.8"}
   {:name 'metosin/jsonista
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/metosin/jsonista.git"
    :git/tag "0.3.7"}
   {:name 'metosin/ring-swagger
    :definition :lein
    :test-cmd "midje"
    :git/url "https://github.com/metosin/ring-swagger.git"
    :git/tag "0.26.2"}
   {:name 'metosin/kekkonen
    :definition :lein
    :test-cmd "midje"
    :git/url "https://github.com/metosin/kekkonen.git"
    :git/tag "0.5.2"}
   {:name 'metosin/tilakone
    :definition :lein
    :test-cmd "eftest"
    :git/url "https://github.com/metosin/tilakone.git"
    :git/sha "616410a17f26ebd83a7ee20d6b0cd49bc5b89c64"}
   {:name 'metosin/sieppari
    :definition :lein
    :test-cmd "kaocha"
    :git/url "https://github.com/metosin/sieppari.git"
    :git/sha "bfc76d46d7fa01d85178e5454a4274c0461fb7c4"}
   {:name 'metosin/ring-http-response
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/metosin/ring-http-response.git"
    :git/tag "0.9.3"}
   {:name 'metosin/schema-tools
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/metosin/schema-tools.git"
    :git/tag "0.13.1"}
   {:name 'metosin/porsas
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/metosin/porsas.git"
    :git/sha "016256bd44e996d6b642c4c037b97ff4b885787f"}
   {:name 'metosin/potpuri
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/metosin/potpuri.git"
    :git/tag "0.5.3"}
   {:name 'nextjournal/clerk
    :definition :deps.edn
    :test-cmd "-X:test"
    :git/url "https://github.com/nextjournal/clerk.git"
    :git/tag "v0.14.919"}
   {:name 'pedestal/pedestal
    :definition :deps.edn
    :setup ["clojure -X:deps:local prep"
            "clojure -T:build compile-java :aliases '[:local :servlet-api]'"]
    :test-cmd "-X:test"
    :git/url "https://github.com/pedestal/pedestal.git"
    :git/tag "0.6.0"}
   {:name 'plumatic/schema
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/plumatic/schema.git"
    :git/tag "1.4.1"}
   {:name 'ring-clojure/ring
    :definition :lein
    :test-cmd "sub test"
    :git/url "https://github.com/ring-clojure/ring.git"
    :git/tag "1.10.0"}
   {:name 'seancorfield/honeysql
    :definition :deps.edn
    :test-cmd "-T:build test"
    :git/url "https://github.com/seancorfield/honeysql.git"
    :git/tag "v2.4.1045"}
   {:name 'seancorfield/next-jdbc
    :definition :deps.edn
    :test-cmd "-X:test"
    :git/url "https://github.com/seancorfield/next-jdbc.git"
    :git/tag "v1.3.883"}
   {:name 'taoensso/carmine
    :definition :lein
    :test-cmd "test"
    :skip? true ;; requires a running instance of redis
    :git/url "https://github.com/taoensso/carmine.git"
    :git/tag "v3.2.0"}
   {:name 'taoensso/encore
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/taoensso/encore.git"
    :git/tag "v3.63.0"}
   {:name 'taoensso/faraday
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/taoensso/faraday.git"
    :git/tag "v1.12.0"}
   {:name 'taoensso/nippy
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/taoensso/nippy.git"
    :git/tag "v3.3.0-RC1"}
   {:name 'taoensso/tempura
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/taoensso/tempura.git"
    :git/tag "v1.5.3"}
   {:name 'taoensso/tengen
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/taoensso/tengen.git"
    :git/tag "v1.1.0"}
   {:name 'taoensso/timre
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/taoensso/timbre.git"
    :git/tag "v6.2.2"}
   {:name 'taoensso/touchstone
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/taoensso/touchstone.git"
    :git/tag "v2.1.0"}
   {:name 'taoensso/tower
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/taoensso/tower.git"
    :git/tag "v3.1.0-beta5"}
   {:name 'taoensso/truss
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/taoensso/truss.git"
    :git/tag "v1.11.0"}
   {:name 'taoensso/tufte
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/taoensso/tufte.git"
    :git/tag "v2.5.1"}
   {:name 'tonsky/datascript
    :definition :script
    :test-cmd "./script/test_clj.sh"
    :git/url "https://github.com/tonsky/datascript.git"
    :git/tag "1.5.2"}
   {:name 'tonsky/rum
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/tonsky/rum.git"
    :git/tag "0.12.11"}
   {:name 'walmartlabs/lacinia
    :definition :deps.edn
    :test-cmd "-X:dev:test"
    :git/url "https://github.com/walmartlabs/lacinia.git"
    :git/tag "v1.2.1"}
   {:name 'weavejester/compojure
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/weavejester/compojure.git"
    :git/tag "1.7.0"}
   {:name 'weavejester/hiccup
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/weavejester/hiccup.git"
    :git/tag "2.0.0-RC1"}
   {:name 'weavejester/ragtime
    :definition :lein
    :test-cmd "sub test"
    :git/url "https://github.com/weavejester/ragtime.git"
    :git/tag "0.9.3"}
   {:name 'weavejester/medley
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/weavejester/medley.git"
    :git/tag "1.7.0"}
   {:name 'weavejester/integrant
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/weavejester/integrant.git"
    :git/tag "0.9.0-alpha1"}
   {:name 'weavejester/cljfmt
    :definition :script
    :test-cmd "cd cljfmt && lein test"
    :git/url "https://github.com/weavejester/cljfmt.git"
    :git/tag "0.11.2"}
   {:name 'weavejester/hashp
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/weavejester/hashp.git"
    :git/tag "0.2.2"}
   {:name 'weavejester/crypto-password
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/weavejester/crypto-password.git"
    :git/tag "0.3.0"}
   {:name 'weavejester/ring-oauth2
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/weavejester/ring-oauth2.git"
    :git/tag "0.2.0"}
   {:name 'weavejester/eftest
    :definition :script
    :test-cmd "cd eftest && lein test"
    :git/url "https://github.com/weavejester/eftest.git"
    :git/tag "0.6.0"}
   {:name 'weavejester/eclair
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/weavejester/eclair.git"
    :git/sha "51f7a298081ab0525d3a6d8999fbba91ebb11c55"}
   {:name 'weavejester/ataraxy
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/weavejester/ataraxy.git"
    :git/tag "0.4.3"}
   {:name 'weavejester/environ
    :definition :script
    :test-cmd "cd environ && lein test"
    :git/url "https://github.com/weavejester/environ.git"
    :git/tag "1.2.0"}
   {:name 'weavejester/snowball-stemmer
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/weavejester/snowball-stemmer.git"
    :git/tag "0.1.1"}
   {:name 'weavejester/haslett
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/weavejester/haslett.git"
    :git/tag "0.1.7"}
   {:name 'weavejester/euclidean
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/weavejester/euclidean.git"
    :git/tag "0.2.0"}
   {:name 'weavejester/crypto-equality
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/weavejester/crypto-equality.git"
    :git/tag "1.0.1"}
   {:name 'juxt/bidi
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/juxt/bidi.git"
    :git/tag "2.1.6"}
   {:name 'juxt/yada
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/juxt/yada.git"
    :git/tag "1.2.15.1"}
   {:name 'juxt/aero
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/juxt/aero.git"
    :git/tag "1.1.6"}
   {:name 'juxt/tick
    :definition :deps.edn
    :test-cmd "-M:test-clj -m kaocha.runner"
    :git/url "https://github.com/juxt/tick.git"
    :git/tag "0.6.1"}
   {:name 'juxt/joplin
    :definition :lein
    :test-cmd "sub test"
    :git/url "https://github.com/juxt/joplin.git"
    :git/tag "0.3.11"}
   {:name 'juxt/juxt-accounting
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/juxt/juxt-accounting.git"
    :git/tag "0.1.4"}
   {:name 'juxt/clip
    :definition :deps.edn
    :test-cmd "-M:dev:test"
    :git/url "https://github.com/juxt/clip.git"
    :git/tag "v0.28.0"}
   {:name 'juxt/iota
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/juxt/iota.git"
    :git/tag "0.3.0-alpha2"}
   {:name 'juxt/pull
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/juxt/pull.git"
    :git/tag "0.3.0"}
   {:name 'juxt/jinx
    :definition :deps.edn
    :test-cmd "-M:dev:test"
    :git/url "https://github.com/juxt/jinx.git"
    :git/tag "0.1.6"}
   {:name 'juxt/dirwatch
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/juxt/dirwatch.git"
    :git/tag "0.2.6"}
   {:name 'juxt/reap
    :definition :deps.edn
    :test-cmd "-M:dev:test"
    :git/url "https://github.com/juxt/reap.git"
    :git/sha "29ffc8664df26041ebd93a53f009d2606d1a5b6c"}
   ])

(defn all-libraries []
  (vec (concat clojure-libraries
               clj-commons-libraries
               popular-libraries)))

(comment
  (count (all-libraries)))

(defn test-cmd-impl [lib] (:definition lib))

(defmulti test-cmd #'test-cmd-impl)

(defmethod test-cmd :mvn [lib]
  (format "mvn -ntp -q -Dclojure.version=%s clean test"
          (:version lib)))

(defmethod test-cmd :deps.edn [lib]
  (format "clojure -Sdeps '{:deps {org.clojure/clojure {:mvn/version \"%s\"}}}' %s"
          (:version lib)
          (:test-cmd lib)))

(defmethod test-cmd :lein [lib]
  (str "lein with-profile +latest do clean, " (:test-cmd lib)))

(defmethod test-cmd :script [lib]
  (:test-cmd lib))

(xml/alias-uri 'pom "http://maven.apache.org/POM/4.0.0")

(defn get-version-from-pom [pom]
  (->> (xml/parse-str (slurp pom))
       :content
       (filter #(= (:tag %) ::pom/version))
       first
       :content
       first))

(defn compile-clojure [options path]
  (let [path (or path
                 (gl/procure "https://github.com/clojure/clojure.git"
                             'org.clojure/clojure
                             "1.11.1"))
        version (get-version-from-pom (io/file path "pom.xml"))
        jar (format "clojure-%s.jar" version)]
    (when (:build options)
      (shell {:dir path} "mvn" "-ntp" "-q" "-Dmaven.test.skip=true" "clean" "package"))
    {:version version
     :jar jar
     :path (str path "/target/" jar)}))

(defn install-clojure [path version jar]
  (let [m2 (io/file (System/getProperty "user.home") ".m2" "repository" "org" "clojure" "clojure" version)]
    (io/make-parents (io/file m2 jar))
    (io/copy (io/file path)
             (io/file m2 jar))))

(defn copy-profiles [dir profile]
  (spit (io/file dir "profiles.clj") profile))

;; clojure core libraries only: 13 min

(defn lib-dir [lib]
  (gl/procure (:git/url lib)
              (:name lib)
              (or (:git/tag lib) (:git/sha lib))))

(defn run-impl [{:keys [options]}]
  (println (.toString (java.time.LocalDateTime/now)))
  (let [branch (:branch options)
        {:keys [path version jar]} (compile-clojure
                                     options
                                     (str (System/getProperty "user.home")
                                          "/personal/clojure-local-dev/"
                                          branch))
        profile (-> (io/resource "profiles.clj")
                    slurp
                    (str/replace "CLOJURE_VERSION" version))
        filter-fn (if-let [chosen-library (:library options)]
                    #(= chosen-library (:name %))
                    (if-let [chosen-ns (:namespace options)]
                      #(= chosen-ns (namespace (:name %)))
                      identity))
        results (atom {:failure [] :success []})]
    (install-clojure path version jar)
    (time
      (doseq [lib (all-libraries)
              :when (filter-fn lib)]
        (newline)
        (if (:skip? lib)
          (println "Skipping" (:name lib))
          (do (println "Testing" (:name lib))
              (when-let [dir (lib-dir lib)]
                (when (= :lein (:definition lib))
                  (copy-profiles dir profile))
                (let [lib (assoc lib :version version)
                      shell-opts (merge {:dir dir :continue true}
                                        (when-not (:test-out options)
                                          {:out :string}))
                      setup (:setup lib)
                      setup (if (string? setup) [setup] setup)
                      cmd (test-cmd lib)]
                  (doseq [setup setup]
                    (println "Running setup command:" setup)
                    (try
                      (shell shell-opts setup)
                      (catch Throwable _
                        (println "Setup command failed"))))
                  (try
                    (println "Running test command:" cmd)
                    (let [test-result (shell shell-opts cmd)
                          k (if (zero? (:exit test-result)) :success :failure)]
                      (swap! results update k conj (:name lib))
                      (when (= k :failure)
                        (println (str (:name lib) " tests did not pass"))))
                    (catch Throwable e
                      (prn e)
                      (swap! results update :failure conj (:name lib))))))))))
    (pprint/pprint (:failure @results))))

(def cli-options
  [[nil "--[no-]build" "Recompile and install clojure snapshot jar"
    :default true]
   ["-b" "--branch" "clojure-local-dev branch to use"
    :default "master"]
   ["-l" "--library LIBRARY" "Specific library to check"
    :parse-fn symbol]
   ["-n" "--namespace NAMESPACE" "Namespace of libraries to check"]
   [nil "--[no-]test-out" "Print test out to STOUT"
    :default false]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["Run "
        ""
        "Usage: core-regression [options]"
        ""
        "Options:"
        (#'cli/summarize options-summary)
        ""
        "If no libraries are selected, all will be run."]
       (str/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (str/join \newline errors)))

(defn validate-args
  [args]
  (let [{:keys [options arguments errors summary]}
        (cli/parse-opts args cli-options :summary-fn identity)]
    (cond
      (:help options)
      {:exit-message (usage summary) :ok? true}
      errors
      {:exit-message (error-msg errors)}
      (and (:library options)
           (:namespaces options))
      {:exit-message (error-msg ["Can't select by namespace and specific library."])}
      :else
      {:options options
       :arguments arguments})))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn -main [& args]
  (let [{:keys [exit-message ok?] :as opts} (validate-args args)]
    (if exit-message
      (exit (if ok? 0 1) exit-message)
      (run-impl opts))))

(comment
  (-main "--no-build" "--library" "clj-commons/seesaw"))

;; all-libraries: 36 min
;; core libraries: 13 min
;;
;; current failures (not skipped):
;;
;; cerner/clara-rules
;; clj-commons/dirigiste
;; engelberg/instaparse
;; http-kit/http-kit
;; juxt/clip
;; juxt/joplin
;; juxt/juxt-accounting
;; juxt/yada
;; marick/midje
;; metosin/compojure-api
;; metosin/porsas
;; metosin/ring-swagger
;; metosin/sieppari
;; nextjournal/clerk
;; pedestal/pedestal
;; tonsky/rum
;; weavejester/cljfmt
;; weavejester/eftest
;; weavejester/environ
;; weavejester/hashp
