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
    :skip true ;; fails
    :git/url "https://github.com/clj-commons/aleph.git"
    :git/tag "0.6.3"}
   {:name 'clj-commons/byte-stream
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/byte-streams.git"
    :git/tag "Release-0.3.4"}
   {:name 'clj-commons/byte-transforms
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/byte-transforms.git"
    :git/tag "Release-0.2.2"}
   {:name 'clj-commons/camel-snake-kebab
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/camel-snake-kebab.git"
    :git/tag "version-0.4.3"}
   {:name 'clj-commons/citrus
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/citrus.git"
    :git/tag "Release-3.3.0"}
   {:name 'clj-commons/claypoole
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/claypoole.git"
    :git/tag "Release-1.2.2"}
   {:name 'clj-commons/clj-yaml
    :definition :deps.edn
    :setup "clojure -T:build compile-java"
    :test-cmd "-M:test"
    :git/url "https://github.com/clj-commons/clj-yaml.git"
    :git/tag "v1.0.27"}
   {:name 'clj-commons/cljss
    :definition :lein
    :test-cmd "test"
    :skip true
    :git/url "https://github.com/clj-commons/cljss.git"
    :git/tag "v1.6.3"}
   {:name 'clj-commons/digest
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/digest.git"
    :git/tag "Release-1.4.100"}
   {:name 'clj-commons/dirigiste
    :definition :lein
    :test-cmd "test"
    :skip true
    :git/url "https://github.com/clj-commons/dirigiste.git"
    :git/sha "e18f94e50f286c6614ffacc25607164bcbba57a7"}
   {:name 'clj-commons/durable-queue
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/durable-queue.git"
    :git/tag "0.1.5"}
   {:name 'clj-commons/etaoin
    :definition :script
    :test-cmd "bb test:jvm --suites unit --launch-virtual-display"
    :skip true ;; frontend/cljs app
    :git/url "https://github.com/clj-commons/etaoin.git"
    :git/tag "v1.0.40"}
   {:name 'clj-commons/gloss
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/gloss.git"
    :git/tag "Release-0.3.6"}
   {:name 'clj-commons/hickory
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/hickory.git"
    :git/tag "Release-0.7.3"}
   {:name 'clj-commons/humanize
    :definition :deps.edn
    :test-cmd "-X:test"
    :git/url "https://github.com/clj-commons/humanize.git"
    :git/tag "1.0"}
   {:name 'clj-commons/iapetos
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/iapetos.git"
    :git/tag "Release-0.1.13"}
   {:name 'clj-commons/kibit
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/kibit.git"
    :git/sha "93a9ce52aaf7761a97528d0b4d412408d09d12a1"}
   {:name 'clj-commons/manifold
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/manifold.git"
    :git/tag "0.4.1"}
   {:name 'clj-commons/ordered
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/ordered.git"
    :git/tag "Release-1.15.11"}
   {:name 'clj-commons/pomegranate
    :definition :deps.edn
    :test-cmd "-M:test"
    :git/url "https://github.com/clj-commons/pomegranate.git"
    :git/tag "v1.2.23"}
   {:name 'clj-commons/potemkin
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/potemkin.git"
    :git/tag "0.4.6"}
   {:name 'clj-commons/pretty
    :definition :deps.edn
    :test-cmd "-X:test"
    :git/url "https://github.com/clj-commons/pretty.git"
    :git/tag "2.1"}
   {:name 'clj-commons/primitive-math
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/primitive-math.git"
    :git/tag "Release-1.0.1"}
   {:name 'clj-commons/rewrite-clj
    :definition :deps.edn
    :test-cmd "-M:test-common:kaocha --reporter documentation"
    :git/url "https://github.com/clj-commons/rewrite-clj.git"
    :git/tag "v1.1.47"}
   {:name 'clj-commons/ring-buffer
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/ring-buffer.git"
    :git/tag "1.3.1"}
   {:name 'clj-commons/seesaw
    :definition :lein
    :setup "lein javac"
    :test-cmd "run -m lazytest.main test"
    :skip true ;; ns-form spec fails
    :git/url "https://github.com/clj-commons/seesaw.git"
    :git/sha "38695ea1a590d84d877a50df8792f58e04fcbd02"}
   {:name 'clj-commons/tentacles
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clj-commons/tentacles.git"
    :git/tag "0.6.9"}
   {:name 'clj-commons/useful
    :definition :lein
    :test-cmd "test"
    :skip true
    :git/url "https://github.com/clj-commons/useful.git"
    :git/tag "0.11.6"}
  ])

(def popular-libraries
  [
   {:name 'aphyr/dom-top
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/aphyr/dom-top.git"
    :git/tag "v1.0.8"}
   {:name 'aphyr/less-awful-ssl
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/aphyr/less-awful-ssl.git"
    :git/tag "1.0.6"}
   {:name 'athos/sweet-array
    :definition :deps.edn
    :test-cmd "-M:test"
    :git/url "https://github.com/athos/sweet-array.git"
    :git/tag "0.2.0"}
   {:name 'brandonbloom/backtick
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/brandonbloom/backtick.git"
    :git/sha "0463b49ddb0863653231fc6c922bb124ff5f7d25"}
   {:name 'brandonbloom/fipp
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/brandonbloom/fipp.git"
    :git/sha "a4cb207f2fec08219a1bba115a69819b2c7a256c"}
   {:name 'borkdude/dynaload
    :definition :deps.edn
    :test-cmd "-M:clj-test"
    :git/url "https://github.com/borkdude/dynaload.git"
    :git/sha "ca924290a19a34238ffa7ec2b1817dd55fa202f8"}
   {:name 'cerner/clara-rules
    :definition :lein
    :setup ["sed -i -e 's/leiningen.cljsbuild//g' project.clj"]
    :test-cmd "test"
    :git/url "https://github.com/cerner/clara-rules.git"
    :git/tag "0.22.1"}
   {:name 'clj-time/clj-time
    :definition :lein
    :test-cmd "with-profile dev,midje test"
    :git/url "https://github.com/clj-time/clj-time.git"
    :git/tag "v0.15.2"}
   {:name 'cemerick/url
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/cemerick/url.git"
    :git/tag "0.1.1"}
   {:name 'cgrand/enlive
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/cgrand/enlive.git"
    :git/sha "119ab97dd683681683354b60e356ed748bad5e78"}
   {:name 'cgrand/macrovich
    :definition :lein
    :test-cmd "test"
    :skip true ;; no tests
    :git/url "https://github.com/cgrand/macrovich.git"
    :git/sha "95f0fa924ec774a9fa6845f474c2aba7322fea14"}
   {:name 'cgrand/parsley
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/cgrand/parsley.git"
    :git/sha "c5c4783b5976b9412c80f35cf48ce79e77c9fee5"}
   {:name 'cgrand/regex
    :definition :lein
    :test-cmd "test"
    :skip true ;; no tests
    :git/url "https://github.com/cgrand/regex.git"
    :git/sha "200ce13e07b7c4da67b2d2dafc9f708cffeec66c"}
   {:name 'cgrand/seqexp
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/cgrand/seqexp.git"
    :git/sha "b2831c0b48f0e6fe364d3714f56bb224e43d0be1"}
   {:name 'cgrand/sjacket
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/cgrand/sjacket.git"
    :git/sha "4aa60211cfac0268aa4faa6c3955d48d00c5436d"}
   {:name 'cgrand/xforms
    :definition :deps.edn
    :test-cmd "-M:test:kaocha"
    :git/url "https://github.com/cgrand/xforms.git"
    :git/tag "v0.19.5"}
   {:name 'clj-kondo/clj-kondo
    :definition :deps.edn
    :test-cmd "-M:test"
    :git/url "https://github.com/clj-kondo/clj-kondo.git"
    :git/tag "v2023.09.07"}
   {:name 'clojure-lsp/clojure-lsp
    :definition :deps.edn
    :test-cmd "-M:test"
    :dir "lib"
    :git/url "https://github.com/clojure-lsp/clojure-lsp.git"
    :git/tag "2023.08.06-00.28.06"}
   {:name 'clojurewerkz/archimedes
    :definition :lein
    :setup "lein with-profile +dev javac"
    :test-cmd "test"
    :git/url "https://github.com/clojurewerkz/archimedes.git"
    :git/sha "f3300d3d71d35534acf7cc6f010e3fa503be0fba"}
   {:name 'clojurewerkz/balagan
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clojurewerkz/balagan.git"
    :git/sha "22ac86d676ed2b85aac723c0fa262fb0c5ec5ec7"}
   {:name 'clojurewerkz/buffy
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clojurewerkz/buffy.git"
    :git/sha "459a4ba194c66d8426cdbdf4cea6e4ccd6239428"}
   {:name 'clojurewerkz/cassaforte
    :definition :lein
    :test-cmd "test"
    :skip true ;; requires cassandra
    :git/url "https://github.com/clojurewerkz/cassaforte.git"
    :git/sha "bd0b3ff44c5d7f993798270032aa41be0e8209c2"}
   {:name 'clojurewerkz/chash
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/michaelklishin/chash.git"
    :git/sha "0ba3abb76d1f6cb62e000a0dacd8f72705db70b2"}
   {:name 'clojurewerkz/elastisch
    :definition :lein
    :test-cmd "test"
    :skip true ;; requires elasticsearch
    :git/url "https://github.com/clojurewerkz/elastisch.git"
    :git/sha "61f882d673bce7a73cb1ce864796ae3826f9747e"}
   {:name 'clojurewerkz/langohr
    :definition :lein
    :test-cmd "test"
    :skip true ;; requires rabbitmq
    :git/url "https://github.com/clojurewerkz/langohr.git"
    :git/sha "a04f076ce254ca5a94f918891e3eb2aa5ce06b94"}
   {:name 'clojurewerkz/mailer
    :definition :lein
    :test-cmd "test"
    :skip true ;; outdated dependencies rely on removed java package
    :git/url "https://github.com/clojurewerkz/mailer.git"
    :git/sha "89049ccdc955730b0093068b7c41da18abcde468"}
   {:name 'clojurewerkz/meltdown
    :definition :lein
    :setup ["sed -i -e 's/1.1.6.BUILD-SNAPSHOT/1.1.6.RELEASE/g' project.clj"
            "sed -i -e 's/:javac-options/#_#_:javac-options/g' project.clj"
            "sed -i -e 's/\"-XX:+UseFastAccessorMethods\"//g' project.clj"]
    :test-cmd "test"
    :git/url "https://github.com/clojurewerkz/meltdown.git"
    :git/sha "58d50141bb35b4a5abf59dcb13db9f577b6b3b9f"}
   {:name 'clojurewerkz/money
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clojurewerkz/money.git"
    :git/sha "15a154033e57c68de1de16bdbde578ee20839065"}
   {:name 'clojurewerkz/monger
    :definition :lein
    :test-cmd "test"
    :skip true ;; requires mongodb
    :git/url "https://github.com/clojurewerkz/monger.git"
    :git/sha "84170f7c51973e3d789736cdeb3cd8ad013071c3"}
   {:name 'clojurewerkz/neocons
    :definition :lein
    :test-cmd "test"
    :skip true ;; requires neo4j
    :git/url "https://github.com/clojurewerkz/neocons.git"
    :git/sha "30f30e95686a01f7a34082600bc1221877c2acbd"}
   {:name 'clojurewerkz/ogre
    :definition :lein
    :setup "lein with-profile dev javac"
    :test-cmd "test"
    :git/url "https://github.com/clojurewerkz/ogre.git"
    :git/sha "fd0d50402fe35590c8c42137374c38c1d7b435a8"}
   {:name 'clojurewerkz/pantomime
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/michaelklishin/pantomime.git"
    :git/sha "3781d72fdf957e57952b02f39dbe7e0aee19f43e"}
   {:name 'clojurewerkz/propertied
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/michaelklishin/propertied.git"
    :git/sha "5fb3b81818a4071a9d041060aa87c0dee34eb551"}
   {:name 'clojurewerkz/quartzite
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/michaelklishin/quartzite.git"
    :git/sha "21a69c6339491c3a015c17db4b2d0a1649029c44"}
   {:name 'clojurewerkz/route-one
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clojurewerkz/route-one.git"
    :git/tag "v1.2.0"}
   {:name 'clojurewerkz/scrypt
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clojurewerkz/scrypt.git"
    :git/sha "a67101b5fc0afb95e9af05ea31727cef2a8398bc"}
   {:name 'clojurewerkz/serialism
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clojurewerkz/serialism.git"
    :git/sha "94bdbfd3d8bd385d9e500a695567650490500cd9"}
   {:name 'clojurewerkz/support
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/clojurewerkz/support.git"
    :git/sha "33d28084829f6ccb6aed870a357fa6b53c57d2cc"}
   {:name 'clojurewerkz/validateur
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/michaelklishin/validateur.git"
    :git/tag "v2.6.0"}
   {:name 'clojurewerkz/welle
    :definition :lein
    :test-cmd "test"
    :skip true ;; requires riak
    :git/url "https://github.com/clojurewerkz/welle.git"
    :git/sha "3f3cd24af7c0d95489298e4096b362b6943f85ef"}
   {:name 'cnuerber/dtype-next
    :definition :deps.edn
    :setup ["rm -rf target/classes"
            {:deps.edn "-T:build compile"}]
    :test-cmd "-M:dev-mac-m1:test" ;; personal machine specific...
    :git/url "https://github.com/cnuernber/dtype-next.git"
    :git/sha "bc08804e07a084cbd21056892d20e9b7c85ab937"}
   {:name 'cognitect-labs/aws-api
    :definition :deps.edn
    :test-cmd "-M:dev:test"
    :skip true ;; broken test: https://github.com/cognitect-labs/aws-api/issues/242
    :git/url "https://github.com/cognitect-labs/aws-api.git"
    :git/tag "v0.8.686"}
   {:name 'cognitect-labs/fern
    :definition :lein
    :test-cmd "test"
    :skip true ;; broken test: https://github.com/cognitect-labs/fern/issues/9
    :git/url "https://github.com/cognitect-labs/fern.git"
    :git/tag "0.1.6"}
   {:name 'cognitect-labs/test-runner
    :definition :deps.edn
    :test-cmd "-M:test"
    :git/url "https://github.com/cognitect-labs/test-runner.git"
    :git/sha "7284cda41fb9edc0f3bc6b6185cfb7138fc8a023"}
   {:name 'cognitect-labs/vase
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/cognitect-labs/vase.git"
    :git/sha "d882bc8f28e8af2077b55c80e069aa2238f646b7"}
   {:name 'dakrone/cd-client
    :definition :lein
    :test-cmd "test"
    :skip true ;; no tests
    :git/url "https://github.com/dakrone/clojuredocs-client.git"
    :git/sha "e556a5d7e3b6611246c04c5a07c9312289b6e807"}
   {:name 'dakrone/cheshire
    :definition :lein
    :test-cmd "test :all"
    :git/url "https://github.com/dakrone/cheshire.git"
    :git/tag "5.11.0"}
   {:name 'dakrone/clj-http
    :definition :lein
    :test-cmd "test :all"
    :git/url "https://github.com/dakrone/clj-http.git"
    :git/tag "3.12.3"}
   {:name 'dakrone/tigris
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/dakrone/tigris.git"
    :git/tag "3.12.3"}
   {:name 'davidsantiago/pathetic
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/davidsantiago/pathetic.git"
    :git/tag "0.5.1"}
   {:name 'davidsantiago/quoin
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/davidsantiago/quoin.git"
    :git/tag "0.1.2"}
   {:name 'davidsantiago/stencil
    :definition :lein
    :test-cmd "test"
    :skip true ;; pulls moustache's specs and runs them without specifying a hash lol
    :git/url "https://github.com/davidsantiago/stencil.git"
    :git/tag "0.5.0"}
   {:name 'de.kotka/lazymap
    :definition :lein
    :test-cmd "test"
    :skip true ;; repo has been deleted
    :git/url "https://bitbucket.org/kotarak/lazymap.git"
    :git/tag "0.5.0"}
   {:name 'droitfintech/clj-diff
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/droitfintech/clj-diff.git"
    :git/tag "9a26f88fe9d2295b9472b996d7df6fefb133888b"}
   {:name 'duckyuck/flare
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/duckyuck/flare.git"
    :git/sha "4d983fda75fab718b5ec0bf4e3f8ab4bfa1a2080"}
   {:name 'duct-framework/core
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/duct-framework/core.git"
    :git/tag "0.8.0"}
   {:name 'engelberg/instaparse
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/Engelberg/instaparse.git"
    :git/tag "v1.4.12"}
   {:name 'frenchy64/fully-satisfies
    :definition :lein
    :test-cmd "test"
    :skip true ;; 1.11.1 broke tests
    :git/url "https://github.com/frenchy64/fully-satisfies.git"
    :git/sha "aa0a873e67dd34078a2c58e638d7c5c73e1cd224"}
   {:name 'frenchy64/typedclojure
    :definition :deps.edn
    :test-cmd "-M:kaocha:test -m kaocha.runner"
    :git/url "https://github.com/typedclojure/typedclojure.git"
    :git/tag "1.1.1"}
   {:name 'funcool/buddy-auth
    :definition :deps.edn
    :test-cmd "-X:dev:test"
    :git/url "https://github.com/funcool/buddy-auth.git"
    :git/tag "3.0.323"}
   {:name 'funcool/buddy-core
    :definition :deps.edn
    :test-cmd "-X:dev:test"
    :git/url "https://github.com/funcool/buddy-core.git"
    :git/tag "1.11.423"}
   {:name 'funcool/buddy-hashers
    :definition :deps.edn
    :setup ["sed -i -e 's/:javac-opts/#_#_:javac-options/g' build.clj"
            "clojure -T:build compile"]
    :test-cmd "-X:dev:test"
    :git/url "https://github.com/funcool/buddy-hashers.git"
    :git/tag "2.0.167"}
   {:name 'funcool/buddy-sign
    :definition :deps.edn
    :test-cmd "-X:dev:test"
    :git/url "https://github.com/funcool/buddy-sign.git"
    :git/tag "2.0.167"}
   {:name 'funcool/cats
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/funcool/cats.git"
    :git/sha "73b4ecc6e2e0c7cb72e2d57ea6ff5471a0ab17f3"}
   {:name 'funcool/cuerdas
    :definition :deps.edn
    :test-cmd "-X:dev:test"
    :git/url "https://github.com/funcool/cuerdas.git"
    :git/tag "2022.06.16-403"}
   {:name 'funcool/promesa
    :definition :deps.edn
    :test-cmd "-J-Djdk.tracePinnedThreads=full -J--enable-preview -X:dev:test"
    :git/url "https://github.com/funcool/promesa.git"
    :git/tag "11.0.671"}
   {:name 'gfredericks/test.chuck
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/gfredericks/test.chuck.git"
    :git/tag "test.chuck-0.2.14"}
   {:name 'geoffsalmon/bytebuffer
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/geoffsalmon/bytebuffer.git"
    :git/sha "dee223c57456fa1afd600f447114622fa333efd3"}
   {:name 'greglook/alphabase
    :definition :lein
    :test-cmd "kaocha :unit-clj"
    :git/url "https://github.com/greglook/alphabase.git"
    :git/tag "2.1.1"}
   {:name 'greglook/blocks
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/greglook/blocks.git"
    :git/tag "2.0.4"}
   {:name 'greglook/clj-arrangement
    :definition :lein
    :test-cmd "kaocha :unit-clj"
    :git/url "https://github.com/greglook/clj-arrangement.git"
    :git/tag "2.1.0"}
   {:name 'greglook/clj-cbor
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/greglook/clj-cbor.git"
    :git/tag "1.1.1"}
   {:name 'greglook/clj-multiformats
    :definition :deps.edn
    :test-cmd "-M:dev:test"
    :git/url "https://github.com/greglook/clj-multiformats.git"
    :git/tag "0.15.0"}
   {:name 'greglook/clj-pgp
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/greglook/clj-pgp.git"
    :git/tag "1.1.0"}
   {:name 'greglook/cljstyle
    :definition :lein
    :test-cmd "kaocha"
    :git/url "https://github.com/greglook/cljstyle.git"
    :git/tag "0.15.0"}
   {:name 'greglook/merkledag-core
    :definition :lein
    :test-cmd "test"
    :skip true ;; interop requires missing type hint
    :git/url "https://github.com/greglook/merkledag-core.git"
    :git/tag "0.4.1"}
   {:name 'greglook/puget
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/greglook/puget.git"
    :git/tag "1.3.4"}
   {:name 'hcarvalhoalves/raven-clj
    :definition :lein
    :test-cmd "midje"
    :skip true ;; 1.9 spec fails (symbol instead of keyword in ns form)
    :git/url "https://github.com/hcarvalhoalves/raven-clj.git"
    :git/sha "2f9914d8629d878ca12a33a469edef346c21d42d"}
   {:name 'hiredman/clj-http-lite
    :definition :lein
    :test-cmd "test :all"
    :git/url "https://github.com/hiredman/clj-http-lite.git"
    :git/tag "0.2.0"}
   {:name 'honeysql/honeysql
    :definition :deps.edn
    :test-cmd "-M:test:runner"
    :git/url "https://github.com/seancorfield/honeysql.git"
    :git/tag "v1.0.461"}
   {:name 'http-kit/http-kit
    :definition :lein
    :test-cmd "test"
    :skip true ;; 1.9 spec fails (symbol instead of keyword in ns form)
    :git/url "https://github.com/http-kit/http-kit.git"
    :git/tag "v2.7.0"}
   {:name 'ibdknox/colorize
    :definition :lein
    :test-cmd "test"
    :skip true ;; no tests
    :git/url "https://github.com/ibdknox/colorize.git"
    :git/sha "d89a13db5cc3e2c59cf397fab266f886f5ee9f7c"}
   {:name 'ibdknox/watchtower
    :definition :lein
    :test-cmd "test"
    :skip true ;; no tests
    :git/url "https://github.com/ibdknox/watchtower.git"
    :git/sha "dbbfd612e1b626146628b0bda5572479da6994c4"}
   {:name 'igjoshua/americano
    :definition :deps.edn
    :test-cmd "-M:test:runner"
    :git/url "https://github.com/IGJoshua/americano.git"
    :git/tag "v1.2.0"}
   {:name 'igjoshua/farolero
    :definition :deps.edn
    :setup {:deps.edn "-X:build"}
    :test-cmd "-M:dev:test:runner"
    :git/url "https://github.com/IGJoshua/farolero.git"
    :git/tag "v1.5.0"}
   {:name 'juji-io/datalevin
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/juji-io/datalevin.git"
    :git/tag "0.8.19"}
   {:name 'juxt/aero
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/juxt/aero.git"
    :git/tag "1.1.6"}
   {:name 'juxt/bidi
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/juxt/bidi.git"
    :git/tag "2.1.6"}
   {:name 'juxt/clip
    :definition :deps.edn
    :test-cmd "-M:dev:test"
    :skip true
    :git/url "https://github.com/juxt/clip.git"
    :git/tag "v0.28.0"}
   {:name 'juxt/dirwatch
    :definition :lein
    :test-cmd "test"
    :skip true ;; `lein test` reports "Error: Could not find or load main class clojure.main"
    :git/url "https://github.com/juxt/dirwatch.git"
    :git/tag "0.2.6"}
   {:name 'juxt/iota
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/juxt/iota.git"
    :git/tag "0.3.0-alpha2"}
   {:name 'juxt/jinx
    :definition :deps.edn
    :test-cmd "-M:dev:test"
    :git/url "https://github.com/juxt/jinx.git"
    :git/tag "0.1.6"}
   {:name 'juxt/pull
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/juxt/pull.git"
    :git/tag "0.3.0"}
   {:name 'juxt/reap
    :definition :deps.edn
    :test-cmd "-M:dev:test"
    :git/url "https://github.com/juxt/reap.git"
    :git/sha "29ffc8664df26041ebd93a53f009d2606d1a5b6c"}
   {:name 'juxt/tick
    :definition :deps.edn
    :test-cmd "-M:test-clj -m kaocha.runner"
    :git/url "https://github.com/juxt/tick.git"
    :git/tag "0.6.1"}
   {:name 'juxt/xtdb
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/xtdb/xtdb.git"
    :git/tag "1.24.0"}
   {:name 'juxt/yada
    :definition :lein
    :test-cmd "test"
    :skip true
    :git/url "https://github.com/juxt/yada.git"
    :git/tag "1.2.15.1"}
   {:name 'kkinnear/zprint
    :definition :deps.edn
    :test-cmd "-M:cljtest:test"
    :git/url "https://github.com/kkinnear/zprint.git"
    :git/tag "1.2.7"}
   {:name 'lambdaisland/clj-diff
    :definition :deps.edn
    :test-cmd "-M:test -m kaocha.runner"
    :git/url "https://github.com/lambdaisland/clj-diff.git"
    :git/sha "bea9affa8549b1513d92aa7812062cdbd10e2ff5"}
   {:name 'lambdaisland/deep-diff
    :definition :deps.edn
    :test-cmd "-M:test -m kaocha.runner"
    :git/url "https://github.com/lambdaisland/deep-diff2.git"
    :git/tag "v0.0-47"}
   {:name 'lambdaisland/deep-diff2
    :definition :deps.edn
    :test-cmd "-M:test -m kaocha.runner :clj"
    :git/url "https://github.com/lambdaisland/deep-diff2.git"
    :git/tag "v2.9.202"}
   {:name 'lambdaisland/facai
    :definition :deps.edn
    :test-cmd "-M:dev:test -m kaocha.runner"
    :skip true ;; has notifier plugin that requires notify-send
    :git/url "https://github.com/lambdaisland/facai.git"
    :git/tag "v0.8.68-alpha"}
   {:name 'lambdaisland/kaocha
    :definition :deps.edn
    :test-cmd "-M:test -m kaocha.runner :unit"
    :git/url "https://github.com/lambdaisland/kaocha.git"
    ;; needs to be sha instead of tag? i don't get it
    :git/sha "df39ac3f98807b1aa92ace7794dd25a6128b950b"}
   {:name 'lambdaisland/ornament
    :definition :deps.edn
    :setup "npm install ws"
    :test-cmd "-M:test -m kaocha.runner"
    :git/url "https://github.com/lambdaisland/ornament.git"
    :git/tag "v1.10.94"}
   {:name 'lambdaisland/uniontypes
    :definition :lein
    :test-cmd "kaocha"
    :git/url "https://github.com/lambdaisland/uniontypes.git"
    :git/sha "d1b847098284130fb0ff3ed786ade8cf83dfa399"}
   {:name 'lambdaisland/uri
    :definition :deps.edn
    :test-cmd "-M:test -m kaocha.runner :clj"
    :git/url "https://github.com/lambdaisland/uri.git"
    :git/tag "v1.15.125"}
   {:name 'lilactown/cascade
    :definition :deps.edn
    :test-cmd "-M:test -m kaocha.runner :clj"
    :git/url "https://github.com/lilactown/cascade.git"
    :git/tag "v2.0.1"}
   {:name 'lilactown/pyramid
    :definition :deps.edn
    :test-cmd "-M:test -m kaocha.runner :clj"
    :git/url "https://github.com/lilactown/pyramid.git"
    :git/tag "3.3.0"}
   {:name 'liquidz/antq
    :definition :deps.edn
    :test-cmd "-M:dev:test"
    :skip true ;; requires gradle, i'm not setting that up
    :git/url "https://github.com/liquidz/antq.git"
    :git/tag "2.5.1109"}
   {:name 'liquidz/build.edn
    :definition :deps.edn
    :test-cmd "-M:dev:test"
    :skip true ;; requires actually shelling out to get the tests to run
    :git/url "https://github.com/liquidz/build.edn.git"
    :git/tag "0.10.227"}
   {:name 'liquidz/clj-jwt
    :definition :lein
    :test-cmd "midje"
    :skip true ;; interop requires missing type hint
    :git/url "https://github.com/liquidz/clj-jwt.git"
    :git/sha "8d4778a20a243ad6e0a8deae6500877c6c9de09f"}
   {:name 'liquidz/rewrite-indented
    :definition :deps.edn
    :test-cmd "-M:dev:test"
    :git/url "https://github.com/liquidz/rewrite-indented.git"
    :git/tag "0.2.36"}
   {:name 'liquidz/testdoc
    :definition :deps.edn
    :test-cmd "-M:dev:test"
    :git/url "https://github.com/liquidz/testdoc.git"
    :git/tag "1.5.109"}
   {:name 'magnars/prone
    :definition :lein
    :test-cmd "kaocha"
    :skip true ;; broken assumptions in 1.10+: https://github.com/magnars/prone/issues/56
    :git/url "https://github.com/magnars/prone.git"
    :git/tag "2021-04-23"}
   {:name 'marick/midje
    :definition :lein
    :test-cmd "midje"
    :skip true ;; dev set-up relies on pre-1.10 error messages
    :git/url "https://github.com/marick/Midje.git"
    :git/sha "34819ae8d24a11b0f953d461f94e09a2638ff385"}
   {:name 'marick/suchwow
    :definition :lein
    :test-cmd "midje"
    :git/url "https://github.com/marick/suchwow.git"
    :git/sha "111cd4aa21ee23552742701bfe52e593b65fb0f8"}
   {:name 'metosin/compojure-api
    :definition :lein
    :test-cmd "midje"
    :git/url "https://github.com/metosin/compojure-api.git"
    :git/tag "2.0.0-alpha31"}
   {:name 'metosin/jsonista
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/metosin/jsonista.git"
    :git/tag "0.3.7"}
   {:name 'metosin/kekkonen
    :definition :lein
    :test-cmd "midje"
    :git/url "https://github.com/metosin/kekkonen.git"
    :git/tag "0.5.2"}
   {:name 'metosin/malli
    :definition :deps.edn
    :test-cmd "-M:test -m kaocha.runner"
    :git/url "https://github.com/metosin/malli.git"
    :git/tag "0.11.0"}
   {:name 'metosin/muuntaja
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/metosin/muuntaja.git"
    :git/tag "0.6.8"}
   {:name 'metosin/porsas
    :definition :lein
    :setup "docker-compose up -d"
    :test-cmd "test"
    :teardown "docker-compose down"
    :skip true ;; "connection refused" errors on my machine
    :git/url "https://github.com/metosin/porsas.git"
    :git/sha "016256bd44e996d6b642c4c037b97ff4b885787f"}
   {:name 'metosin/potpuri
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/metosin/potpuri.git"
    :git/tag "0.5.3"}
   {:name 'metosin/reitit
    :definition :lein
    :test-cmd "bat-test"
    :git/url "https://github.com/metosin/reitit.git"
    :git/tag "0.7.0-alpha5"}
   {:name 'metosin/ring-http-response
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/metosin/ring-http-response.git"
    :git/tag "0.9.3"}
   {:name 'metosin/ring-swagger
    :definition :lein
    :test-cmd "midje"
    :skip true
    :git/url "https://github.com/metosin/ring-swagger.git"
    :git/tag "0.26.2"}
   {:name 'metosin/schema-tools
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/metosin/schema-tools.git"
    :git/tag "0.13.1"}
   {:name 'metosin/sieppari
    :definition :lein
    :setup "npm ci"
    :test-cmd "kaocha"
    :git/url "https://github.com/metosin/sieppari.git"
    :git/sha "bfc76d46d7fa01d85178e5454a4274c0461fb7c4"}
   {:name 'metosin/spec-tools
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/metosin/spec-tools.git"
    :git/tag "0.10.5"}
   {:name 'metosin/tilakone
    :definition :lein
    :test-cmd "eftest"
    :git/url "https://github.com/metosin/tilakone.git"
    :git/sha "616410a17f26ebd83a7ee20d6b0cd49bc5b89c64"}
   {:name 'mmcgrana/clj-stacktraces
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/mmcgrana/clj-stacktrace.git"
    :git/tag "0.2.8"}
   {:name 'mtgred/netrunner
    :definition :lein
    :setup "lein fetch --no-db --no-card-images"
    :test-cmd "eftest"
    :git/url "https://github.com/mtgred/netrunner.git"
    :git/sha "dffc1fabd2d5ba8c2cf44a8d6b30d14a3c2daef0"}
   {:name 'nextjournal/clerk
    :definition :deps.edn
    :test-cmd "-X:test"
    :skip true ;; flaky test?
    :git/url "https://github.com/nextjournal/clerk.git"
    :git/tag "v0.14.919"}
   {:name 'ninjudd/clojure-complete
    :definition :lein
    :test-cmd "test"
    :skip true ;; outdated expectations about clojure.core
    :git/url "https://github.com/ninjudd/clojure-complete.git"
    :git/tag "0.2.5"}
   {:name 'nrepl/bencode
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/nrepl/bencode.git"
    :git/sha "4286ceef3cc7e9531b8e1650d0a1ff243b150dcd"}
   {:name 'nrepl/nrepl
    :definition :lein
    :test-cmd "kaocha"
    :git/url "https://github.com/nrepl/nrepl.git"
    :git/sha "5839006c5f522fd5e6cf2adbcc5b59e4bd0677dd"}
   {:name 'nubank/matcher-combinators
    :definition :deps.edn
    :test-cmd "-M:dev:clj-test:test-runner"
    :git/url "https://github.com/nubank/matcher-combinators.git"
    :git/tag "3.8.8"}
   {:name 'nubank/state-flow
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/nubank/state-flow.git"
    :git/tag "5.14.4"}
   {:name 'pangloss/fermor
    :definition :deps.edn
    :test-cmd "-M:test"
    :skip true ;; no test runner?
    :git/url "https://github.com/pangloss/fermor.git"
    :git/sha "0c5ea61360b82faf3fbd91b64051922587960f81"}
   {:name 'pangloss/genera
    :definition :deps.edn
    :test-cmd "-M:test:runner"
    :skip true ;; no tests
    :git/url "https://github.com/pangloss/genera.git"
    :git/sha "71c577971b1d23404171555d87613c41ace6bf4a"}
   {:name 'pangloss/pattern
    :definition :deps.edn
    :test-cmd "-M:test:runner"
    :skip true ;; test runner url is incorrect: https://github.com/pangloss/pattern/issues/23
    :git/url "https://github.com/pangloss/pattern.git"
    :git/sha "79cc57792fcb6b72d9ad9036ad29c7fca7e85ca3"}
   {:name 'pangloss/pure-conditioning
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/pangloss/pure-conditioning.git"
    :git/sha "61fa43215e0fce0fe83808b9e30c2bb4c170ffbd"}
   {:name 'pangloss/system.check
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/pangloss/system.check.git"
    :git/sha "61fa43215e0fce0fe83808b9e30c2bb4c170ffbd"}
   {:name 'pangloss/transducers
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/pangloss/transducers.git"
    :git/sha "08ea05da3893b439fd3b1f961ad1616ac95030c2"}
   {:name 'paraseba/faker
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/paraseba/faker.git"
    :git/sha "514c1f5b3169cbb88b0eba5dd8c85ac049055ab8"}
   {:name 'pedestal/pedestal
    :definition :deps.edn
    :setup [{:deps.edn "-X:deps:local prep"
             :dir "service"}
            "sed -i -e 's/:javac-options/#_#_:javac-options/g' service/build.clj"
            {:deps.edn "-T:build compile-java :aliases '[:local :servlet-api]'"
             :dir "service"}]
    :test-cmd "-X:test"
    :dir "tests"
    :git/url "https://github.com/pedestal/pedestal.git"
    :git/sha "9b02e4c4a9b87c718ed7f3f7bcdab032e84336a7"}
   {:name 'plumatic/hiphip
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/plumatic/hiphip.git"
    :git/tag "57d8d4130b50985a1ea24dff1df7d715e80447a5"}
   {:name 'plumatic/plumbing
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/plumatic/plumbing.git"
    :git/tag "plumbing-0.6.0"}
   {:name 'plumatic/schema
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/plumatic/schema.git"
    :git/tag "1.4.1"}
   {:name 'plumatic/schema-generators
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/plumatic/schema-generators.git"
    :git/tag "schema-generators-0.1.5"}
   {:name 'pjstadig/humane-test-output
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/pjstadig/humane-test-output.git"
    :git/tag "0.11.0"}
   {:name 'quoll/asami
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/quoll/asami.git"
    :git/tag "v2.3.3"}
   {:name 'raynes/fs
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/Raynes/fs.git"
    :git/tag "147f7b30d2c3c7e773a3650bf16a7b5720acfde8"}
   {:name 'redplanetlabs/defexception
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/redplanetlabs/defexception.git"
    :git/sha "9bf08095c260c5187381a9e3e27c9e40dd99e188"}
   {:name 'redplanetlabs/proxy-plus
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/redplanetlabs/proxy-plus.git"
    :git/sha "839106fb9b98a3916f551a53f24b36ab4e6c5916"}
   {:name 'redplanetlabs/specter
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/redplanetlabs/specter.git"
    :git/tag "1.1.4"}
   {:name 'redplanetlabs/vector-backed-sorted-map
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/redplanetlabs/vector-backed-sorted-map.git"
    :git/sha "1a63e6ab4ddd2f580873be1823937868f2513e08"}
   {:name 'replikativ/datahike
    :definition :deps.edn
    :test-cmd "-M:test -m kaocha.runner"
    :git/url "https://github.com/ring-clojure/ring.git"
    :git/tag "0.6.1545"}
   {:name 'ring-clojure/ring
    :definition :lein
    :test-cmd "sub test"
    :git/url "https://github.com/ring-clojure/ring.git"
    :git/tag "1.10.0"}
   {:name 'ring-clojure/ring-anti-forgery
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/ring-clojure/ring-anti-forgery.git"
    :git/tag "1.3.0"}
   {:name 'ring-clojure/ring-codec
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/ring-clojure/ring-codec.git"
    :git/tag "1.2.0"}
   {:name 'ring-clojure/ring-defaults
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/ring-clojure/ring-defaults.git"
    :git/tag "0.4.0"}
   {:name 'ring-clojure/ring-headers
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/ring-clojure/ring-headers.git"
    :git/tag "0.3.0"}
   {:name 'ring-clojure/ring-mock
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/ring-clojure/ring-mock.git"
    :git/tag "0.4.0"}
   {:name 'ring-clojure/ring-ssl
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/ring-clojure/ring-ssl.git"
    :git/tag "0.3.0"}
   {:name 'sattvik/leinjacker
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/sattvik/leinjacker.git"
    :git/tag "0.4.3"}
   {:name 'scgilardi/slingshot
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/scgilardi/slingshot.git"
    :git/tag "0.12.2"}
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
   {:name 'stuartsierra/component
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/stuartsierra/component.git"
    :git/tag "component-1.1.0"}
   {:name 'stuartsierra/dependency
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/stuartsierra/dependency.git"
    :git/tag "dependency-1.0.0"}
   {:name 'taoensso/carmine
    :definition :lein
    :test-cmd "test"
    :skip true
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
   {:name 'technomancy/robert-hooke
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/technomancy/robert-hooke.git"
    :git/tag "1.3.0"}
   {:name 'technomancy/slamhound
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/technomancy/slamhound.git"
    :git/sha "68bad50282b8b559eb012d6574ef6a0e1485b3a2"}
   {:name 'tobereplaced/lettercase
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/ToBeReplaced/lettercase.git"
    :git/sha "90cdf47de653c5b9be433e17b87cbda6433122ac"}
   {:name 'teropa/hiccups
    :definition :lein
    :test-cmd "test"
    :skip true ;; cljs library
    :git/url "https://github.com/teropa/hiccups.git"
    :git/sha "v0.3.0"}
   {:name 'tonsky/compact-uuids
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/tonsky/compact-uuids.git"
    :git/tag "0.2.1"}
   {:name 'tonsky/datascript
    :definition :lein
    :test-cmd "with-profile test run -m datascript.test/test-clj"
    :git/url "https://github.com/tonsky/datascript.git"
    :git/tag "1.5.2"}
   {:name 'tonsky/tongue
    :definition :lein
    :test-cmd "test-clj"
    :git/url "https://github.com/tonsky/tongue.git"
    :git/tag "0.4.4"}
   {:name 'tonsky/uberdeps
    :definition :deps.edn
    :test-cmd "-M:test -m uberdeps.test"
    :git/url "https://github.com/tonsky/uberdeps.git"
    :git/tag "1.3.0"}
   {:name 'trptcolin/versioneer
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/trptcolin/versioneer.git"
    :git/sha "fca12c87420f663f832ac0687a482a487d2e2e4b"}
   {:name 'walmartlabs/lacinia
    :definition :deps.edn
    :test-cmd "-X:dev:test"
    :git/url "https://github.com/walmartlabs/lacinia.git"
    :git/tag "v1.2.1"}
   {:name 'weavejester/ataraxy
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/weavejester/ataraxy.git"
    :git/tag "0.4.3"}
   {:name 'weavejester/cljfmt
    :definition :lein
    :test-cmd "test"
    :dir "cljfmt"
    :git/url "https://github.com/weavejester/cljfmt.git"
    :git/tag "0.11.2"}
   {:name 'weavejester/clout
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/weavejester/clout.git"
    :git/tag "2.2.1"}
   {:name 'weavejester/compojure
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/weavejester/compojure.git"
    :git/tag "1.7.0"}
   {:name 'weavejester/crypto-equality
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/weavejester/crypto-equality.git"
    :git/tag "1.0.1"}
   {:name 'weavejester/crypto-password
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/weavejester/crypto-password.git"
    :git/tag "0.3.0"}
   {:name 'weavejester/crypto-random
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/weavejester/crypto-random.git"
    :git/tag "1.2.1"}
   {:name 'weavejester/eclair
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/weavejester/eclair.git"
    :git/sha "51f7a298081ab0525d3a6d8999fbba91ebb11c55"}
   {:name 'weavejester/eftest
    :definition :lein
    :test-cmd "test"
    :dir "eftest"
    :git/url "https://github.com/weavejester/eftest.git"
    :git/tag "0.6.0"}
   {:name 'weavejester/environ
    :definition :lein
    :test-cmd "test"
    :dir "environ"
    :git/url "https://github.com/weavejester/environ.git"
    :git/tag "1.2.0"}
   {:name 'weavejester/euclidean
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/weavejester/euclidean.git"
    :git/tag "0.2.0"}
   {:name 'weavejester/hashp
    :definition :lein
    :test-cmd "test"
    :skip true ;; no tests
    :git/url "https://github.com/weavejester/hashp.git"
    :git/tag "0.2.2"}
   {:name 'weavejester/haslett
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/weavejester/haslett.git"
    :git/tag "0.1.7"}
   {:name 'weavejester/hiccup
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/weavejester/hiccup.git"
    :git/tag "2.0.0-RC1"}
   {:name 'weavejester/integrant
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/weavejester/integrant.git"
    :git/tag "0.9.0-alpha1"}
   {:name 'weavejester/medley
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/weavejester/medley.git"
    :git/tag "1.7.0"}
   {:name 'weavejester/meta-merge
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/weavejester/meta-merge.git"
    :git/tag "1.0.0"}
   {:name 'weavejester/ns-tracker
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/weavejester/ns-tracker.git"
    :git/sha "e693225c6f920f633aa383c11bc80171e1aa53aa"}
   {:name 'weavejester/progrock
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/weavejester/progrock.git"
    :git/sha "9c277a3244c52bfde19c21add327d6e20b94fdf5"}
   {:name 'weavejester/ragtime
    :definition :lein
    :test-cmd "sub test"
    :git/url "https://github.com/weavejester/ragtime.git"
    :git/tag "0.9.3"}
   {:name 'weavejester/ring-oauth2
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/weavejester/ring-oauth2.git"
    :git/tag "0.2.0"}
   {:name 'weavejester/snowball-stemmer
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/weavejester/snowball-stemmer.git"
    :git/tag "0.1.1"}
   {:name 'wkf/hawk
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/wkf/hawk.git"
    :git/sha "a5c21e1305dbae8b8511090cc017faf99ada3b04"}
   {:name 'xsc/pandect
    :definition :lein
    :test-cmd "kaocha"
    :git/url "https://github.com/xsc/pandect.git"
    :git/tag "v1.0.2"}
   {:name 'yogthos/clj-rss
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/yogthos/clj-rss.git"
    :git/sha "1c2f0e6fb4156f0a07b5a4b93ec3deee139871d5"}
   {:name 'yogthos/config
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/yogthos/config.git"
    :git/sha "62f39a5a7adc934787d6a57f4829791f54fe01ba"}
   {:name 'yogthos/json-html
    :definition :lein
    :test-cmd "test-clj"
    :git/url "https://github.com/yogthos/json-html.git"
    :git/sha "53f84f1f00498d04d69b26a760c4fe673b08642e"}
   {:name 'yogthos/maestro
    :definition :deps.edn
    :test-cmd "-M:test"
    :git/url "https://github.com/yogthos/maestro.git"
    :git/sha "6064135b7304f06bc91bf0d3e7cd96a505445075"}
   {:name 'yogthos/markdown-clj
    :definition :deps.edn
    :test-cmd "-X:test"
    :git/url "https://github.com/yogthos/markdown-clj.git"
    :git/sha "288a19b983b06fa1b12a99f0773956e420f998e4"}
   {:name 'yogthos/migratus
    :definition :deps.edn
    :test-cmd "-J-Dclojure.main.report=stderr -Sforce -M:test-runner:dev"
    :git/url "https://github.com/yogthos/migratus.git"
    :git/sha "b61f9bcbce7acd2156a0adffbd9946f9702a4acd"}
   ;; any later commit requires java 19+. should I disable this?
   {:name 'yogthos/selmer
    :definition :deps.edn
    :test-cmd "-X:dev:test"
    :git/url "https://github.com/yogthos/Selmer.git"
    :git/sha "151b81a0c904b07444f37382132bd9e39add01f2"}
   {:name 'ztellman/automat
    :definition :lein
    :test-cmd "do clean, javac, compile, test"
    :git/url "https://github.com/ztellman/automat.git"
    :git/sha "eacbb36184e26f633da5018e28af5755acb50123"}
   {:name 'ztellman/cambrian-collections
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/ztellman/cambrian-collections.git"
    :git/sha "35c80f0afde9f5bf33e63f93c859d4baa9981f4c"}
   {:name 'ztellman/clj-radix
    :definition :lein
    :test-cmd "test"
    :setup "sed -i -e 's/:javac-options/#_#_:javac-options/g' project.clj"
    :git/url "https://github.com/ztellman/clj-radix.git"
    :git/tag "0.1.0"}
   {:name 'ztellman/clj-tuple
    :definition :lein
    :test-cmd "test"
    :setup "sed -i -e 's/:javac-options/#_#_:javac-options/g' project.clj"
    :git/url "https://github.com/ztellman/clj-tuple.git"
    :git/tag "0.2.2"}
   {:name 'ztellman/collections-check
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/ztellman/collections-check.git"
    :git/tag "0.1.7"}
   {:name 'ztellman/narrator
    :definition :lein
    :test-cmd "test"
    :setup "sed -i -e 's/:javac-options/#_#_:javac-options/g' project.clj"
    :git/url "https://github.com/ztellman/narrator.git"
    :git/tag "0.1.2"}
   {:name 'ztellman/proteus
    :definition :lein
    :test-cmd "test"
    :setup "sed -i -e 's/:javac-options/#_#_:javac-options/g' project.clj"
    :git/url "https://github.com/ztellman/proteus.git"
    :git/tag "0.1.6"}
   {:name 'ztellman/penumbra
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/ztellman/penumbra.git"
    :git/sha "db43d01c280305beab26d1004ae78b1777ab3fc7"}
   {:name 'ztellman/riddley
    :definition :lein
    :test-cmd "test"
    :setup "sed -i -e 's/:javac-options/#_#_:javac-options/g' project.clj"
    :git/url "https://github.com/ztellman/riddley.git"
    :git/tag "0.2.0"}
   {:name 'ztellman/sleight
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/ztellman/sleight.git"
    :git/tag "0.2.2"}
   {:name 'ztellman/vertigo
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/ztellman/vertigo.git"
    :git/tag "0.1.4"}
   {:name 'ztellman/virgil
    :definition :lein
    :test-cmd "test"
    :git/url "https://github.com/ztellman/virgil.git"
    :git/sha "0aaf05a69a3d1639622be48a766eac372231047c"}
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
  (format "clojure -Srepro -Sdeps '{:deps {org.clojure/clojure {:mvn/version \"%s\"}}}' %s"
          (:version lib)
          (:test-cmd lib)))

(defmethod test-cmd :lein [lib]
  (if (str/starts-with? (:test-cmd lib) "lein")
    (:test-cmd lib)
    (str "lein with-profile +latest " (:test-cmd lib))))

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

(defn remove-pedantic [dir]
  (-> (io/file dir "project.clj")
      (slurp)
      (str/replace ":pedantic? :abort" "")
      (str/replace "http://" "https://")
      (->> (spit (io/file dir "project.clj")))))

(defn clone-lib [lib]
  (binding [*out* (java.io.StringWriter.)]
    (gl/procure (:git/url lib)
                (:name lib)
                (or (:git/tag lib) (:git/sha lib)))))

(defn run-impl [{:keys [options]}]
  (println (.toString (java.time.LocalDateTime/now)))
  (let [branch (:branch options)
        {:keys [path version jar]} (compile-clojure
                                     options
                                     (io/file ".." "clojure-local-dev" branch))
        profile (-> (io/resource "profiles.clj")
                    slurp
                    (str/replace "CLOJURE_VERSION" version))
        filter-fn (if-let [libraries (not-empty (set (:library options)))]
                    #(libraries (:name %))
                    (if-let [chosen-ns (:namespace options)]
                      #(= chosen-ns (namespace (:name %)))
                      identity))
        results (atom {:failure [] :success []})]
    (install-clojure path version jar)
    (time
      (doseq [lib (all-libraries)
              :when (filter-fn lib)]
        (newline)
        (if (:skip lib)
          (println "Skipping" (:name lib))
          (do (println "Testing" (:name lib))
              (let [lib-dir (try (clone-lib lib)
                                 (catch Throwable _
                                   (println "Failed to clone" (:name lib))
                                   nil))
                    test-dir (when lib-dir
                               (if (:dir lib)
                                 (io/file lib-dir (:dir lib))
                                 lib-dir))]
                (when test-dir
                  (when (= :lein (:definition lib))
                    (copy-profiles test-dir profile)
                    (remove-pedantic test-dir))
                  (let [lib (assoc lib :version version)
                        shell-opts (merge {:dir test-dir :continue true}
                                          (when-not (:test-out options)
                                            {:out :string
                                             :err :string}))
                        setup (:setup lib)
                        setup (cond (vector? setup) setup
                                    (map? setup) [setup]
                                    (string? setup) [setup])
                        cmd (test-cmd lib)]
                    (doseq [setup-cmd setup
                            :let [cmd
                                  (cond
                                    (string? setup-cmd) setup-cmd
                                    (and (map? setup-cmd)
                                         (:deps.edn setup-cmd))
                                    (test-cmd {:version version
                                               :definition :deps.edn
                                               :test-cmd (:deps.edn setup-cmd)})
                                    :else nil)]]
                      (if cmd
                        (try
                          (println "Running setup command:" cmd)
                          (let [shell-opts
                                (if (and (map? setup-cmd)
                                         (:dir setup-cmd))
                                  (assoc shell-opts :dir (io/file lib-dir (:dir setup-cmd)))
                                  shell-opts)]
                            (shell shell-opts cmd))
                          (catch Throwable _
                            (println "Setup command failed")))
                        (println "Setup command nil:" setup)))
                    (try
                      (println "Running test command:" cmd)
                      (let [test-result (shell shell-opts cmd)
                            k (if (zero? (:exit test-result)) :success :failure)]
                        (swap! results update k conj (:name lib))
                        (when (= k :failure)
                          (println (str (:name lib) " tests did not pass"))))
                      (catch Throwable e
                        (prn e)
                        (swap! results update :failure conj (:name lib))))
                    (let [teardown (:teardown lib)
                          teardown (if (string? teardown) [teardown] teardown)]
                      (doseq [td teardown]
                        (try
                          (println "Running teardown command:" td)
                          (shell shell-opts td)
                          (catch Throwable _
                            (println (format "Teardown '%s' failed" td)))))))))))))
    (newline)
    (when-let [failures (seq (:failure @results))]
      (pprint/pprint failures))))

(def cli-options
  [[nil "--[no-]build" "Recompile and install clojure snapshot jar"
    :default false]
   ["-b" "--branch BRANCH" "clojure-local-dev branch to use"
    :default "master"]
   ["-l" "--library LIBRARY" "Specific library to check"
    :multi true
    :default #{}
    :update-fn conj
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

;; Full run:
;; real 537m16.090s
;; user 152m8.895s
;; sys  458m27.996s

;; current failures (not skipped):
;;
;; magnars/prone
;; metosin/porsas
;; pangloss/genera
;; pangloss/pattern
;; pedestal/pedestal
;; plumatic/hiphip
;; plumatic/plumbing
;; raynes/fs
;; sattvik/leinjacker
;; scgilardi/slingshot
;; taoensso/faraday
;; technomancy/robert-hooke
;; technomancy/slamhound
;; wkf/hawk
;; yogthos/json-html
;; yogthos/migratus
;; ztellman/cambrian-collections
;; ztellman/clj-tuple
;; ztellman/narrator
;; ztellman/penumbra
;; ztellman/vertigo
