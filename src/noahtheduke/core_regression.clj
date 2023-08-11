(ns noahtheduke.core-regression
  (:require
    [babashka.process :refer [shell]]
    [clojure.tools.gitlibs :as gl]
    [clojure.string :as str]))

(def libraries
  [
   {::name 'Engelberg/instaparse
    ::definition :lein
    ::test-cmd "lein test"
    :git/url "https://github.com/Engelberg/instaparse.git"
    :git/tag "v1.4.12"}
   {::name 'plumatic/schema
    ::definition :lein
    ::test-cmd "lein test"
    :git/url "https://github.com/plumatic/schema.git"
    :git/tag "1.4.1"}
   {::name 'marick/midje
    ::definition :lein
    ::test-cmd "lein midje"
    :git/url "https://github.com/marick/Midje.git"
    ; version 1.10.9, untagged on github
    :git/sha "34819ae8d24a11b0f953d461f94e09a2638ff385"}
   {::name 'cerner/clara-rules
    ::definition :lein
    ::test-cmd "lein test"
    :git/url "https://github.com/cerner/clara-rules.git"
    :git/tag "0.22.1"}
   {::name 'seancorfield/honeysql
    ::definition :deps.edn
    ::test-cmd "-T:build test"
    :git/url "https://github.com/seancorfield/honeysql.git"
    :git/tag "v2.4.1045"}

   ;; clojure core libraries
   {::name 'clojure/test.check
    ::definition :mvn
    :git/url "https://github.com/clojure/test.check.git"
    :git/tag "v1.1.1"}
   {::name 'clojure/core.async
    ::definition :mvn
    :git/url "https://github.com/clojure/core.async.git"
    :git/tag "v1.6.681"}
   {::name 'clojure/tools.build
    ::definition :deps.edn
    ::test-cmd "-X:test"
    :git/url "https://github.com/clojure/tools.build.git"
    :git/tag "v0.9.4"}
   #_{::name 'clojure/data.xml
    ::definition :mvn
    :git/url "https://github.com/clojure/data.xml.git"
    :git/tag "v0.2.0-alpha8"}
   {::name 'clojure/data.json
    ::definition :mvn
    :git/url "https://github.com/clojure/data.json.git"
    :git/tag "v2.4.0"}
   {::name 'clojure/data.fressian
    ::definition :mvn
    :git/url "https://github.com/clojure/data.fressian.git"
    :git/tag "data.fressian-1.0.0"}
   {::name 'clojure/core.memoize
    ::definition :mvn
    :git/url "https://github.com/clojure/core.memoize.git"
    :git/tag "v1.0.257"}
   {::name 'clojure/java.jdbc
    ::definition :mvn
    :git/url "https://github.com/clojure/java.jdbc.git"
    :git/tag "java.jdbc-0.7.12"}
   {::name 'clojure/data.zip
    ::definition :mvn
    :git/url "https://github.com/clojure/data.zip.git"
    :git/tag "data.zip-1.0.0"}
   {::name 'clojure/tools.deps
    ::definition :mvn
    :git/url "https://github.com/clojure/tools.deps.git"
    :git/tag "v0.18.1354"}
   {::name 'clojure/tools.gitlibs
    ::definition :mvn
    :git/url "https://github.com/clojure/tools.gitlibs.git"
    :git/tag "v2.5.197"}
   {::name 'clojure/tools.namespace
    ::definition :mvn
    :git/url "https://github.com/clojure/tools.namespace.git"
    :git/tag "v1.4.4"}
   {::name 'clojure/spec.alpha
    ::definition :mvn
    :git/url "https://github.com/clojure/spec.alpha.git"
    :git/tag "v0.3.218"}
   #_{::name 'clojure/spec.alpha2
    ::definition :mvn
    :git/url "https://github.com/clojure/spec-alpha2.git"
    :git/sha "4cbfa677c4cd66339f18e1c122222c05c69e0d8e"}
   {::name 'clojure/core.rrb-vector
    ::definition :mvn
    :git/url "https://github.com/clojure/core.rrb-vector.git"
    :git/tag "core.rrb-vector-0.1.2"}
   {::name 'clojure/tools.trace
    ::definition :mvn
    :git/url "https://github.com/clojure/tools.trace.git"
    :git/tag "tools.trace-0.7.11"}
   {::name 'clojure/tools.reader
    ::definition :mvn
    :git/url "https://github.com/clojure/tools.reader.git"
    :git/tag "v1.3.6"}
   {::name 'clojure/tools.macro
    ::definition :mvn
    :git/url "https://github.com/clojure/tools.macro.git"
    :git/tag "tools.macro-0.1.5"}
   {::name 'clojure/tools.logging
    ::definition :mvn
    :git/url "https://github.com/clojure/tools.logging.git"
    :git/tag "v1.2.4"}
   {::name 'clojure/tools.deps.graph
    ::definition :mvn
    :git/url "https://github.com/clojure/tools.deps.graph.git"
    :git/tag "v1.1.76"}
   {::name 'clojure/tools.cli
    ::definition :mvn
    :git/url "https://github.com/clojure/tools.deps.graph.git"
    :git/tag "v1.0.219"}
   {::name 'clojure/tools.analyzer
    ::definition :mvn
    :git/url "https://github.com/clojure/tools.analyzer.git"
    :git/tag "v1.1.1"}
   {::name 'clojure/tools.generative
    ::definition :mvn
    :git/url "https://github.com/clojure/tools.generative.git"
    :git/tag "test.generative-1.0.0"}
   {::name 'clojure/math.numeric-tower
    ::definition :mvn
    :git/url "https://github.com/clojure/math.numeric-tower.git"
    :git/tag "math.numeric-tower-0.0.5"}
   {::name 'clojure/math.combinatorics
    ::definition :mvn
    :git/url "https://github.com/clojure/math.combinatorics.git"
    :git/tag "math.combinatorics-0.2.0"}
   {::name 'clojure/java.jmx
    ::definition :mvn
    :git/url "https://github.com/clojure/java.jmx.git"
    :git/tag "java.jmx-1.0.0"}
   {::name 'clojure/java.data
    ::definition :mvn
    :git/url "https://github.com/clojure/java.data.git"
    :git/tag "v1.0.95"}
   {::name 'clojure/data.priority-map
    ::definition :mvn
    :git/url "https://github.com/clojure/data.priority-map.git"
    :git/tag "v1.1.0"}
   {::name 'clojure/data.int-map
    ::definition :mvn
    :git/url "https://github.com/clojure/data.int-map.git"
    :git/tag "v1.2.1"}
   {::name 'clojure/data.generators
    ::definition :mvn
    :git/url "https://github.com/clojure/data.generators.git"
    :git/tag "data.generators-1.0.0"}
   {::name 'clojure/data.finger-tree
    ::definition :mvn
    :git/url "https://github.com/clojure/data.finger-tree.git"
    :git/tag "data.finger-tree-0.0.3"}
   {::name 'clojure/data.csv
    ::definition :mvn
    :git/url "https://github.com/clojure/data.csv.git"
    :git/tag "v1.0.1"}
   {::name 'clojure/data.codec
    ::definition :mvn
    :git/url "https://github.com/clojure/data.codec.git"
    :git/tag "data.codec-0.1.1"}
   {::name 'clojure/data.avl
    ::definition :mvn
    :git/url "https://github.com/clojure/data.avl.git"
    :git/tag "data.avl-0.1.0"}
   {::name 'clojure/core.contracts
    ::definition :mvn
    :git/url "https://github.com/clojure/core.contracts.git"
    :git/tag "core.contracts-0.0.6"}
   {::name 'clojure/core.cache
    ::definition :mvn
    :git/url "https://github.com/clojure/core.cache.git"
    :git/tag "v1.0.225"}
   {::name 'clojure/core.unify
    ::definition :mvn
    :git/url "https://github.com/clojure/core.unify.git"
    :git/tag "core.unify-0.5.7"}
   {::name 'clojure/core.match
    ::definition :mvn
    :git/url "https://github.com/clojure/core.match.git"
    :git/tag "v1.0.1"}
   {::name 'clojure/core.logic
    ::definition :mvn
    :git/url "https://github.com/clojure/core.logic.git"
    :git/tag "v1.0.1"}
   {::name 'clojure/algo.monads
    ::definition :mvn
    :git/url "https://github.com/clojure/algo.monads.git"
    :git/tag "algo.monads-0.1.6"}
   {::name 'clojure/algo.generic
    ::definition :mvn
    :git/url "https://github.com/clojure/algo.generic.git"
    :git/tag "algo.generics-0.1.3"}
   ]
  )

(defn test-cmd-impl [lib] (::definition lib))

(defmulti test-cmd #'test-cmd-impl)

(defmethod test-cmd :deps.edn [lib]
  (str "clojure -Sdeps '{:deps {org.clojure/clojure {:mvn/version \"1.12.0-master-SNAPSHOT\"}}}'"
       " "
       (::test-cmd lib)))

(defmethod test-cmd :mvn [lib]
  (or (::test-cmd lib)
      "mvn -ntp -q -Dclojure.version=1.12.0-master-SNAPSHOT clean test"))

(defn get-target-jar [dir]
  (-> (shell {:dir dir :out :string} "ls" "target/clojure-1.12.0-master-SNAPSHOT.jar")
      :out
      (str/trim)))

(defn compile-clojure [path]
  (let [dir (or path
                (gl/procure "https://github.com/clojure/clojure.git"
                            'org.clojure/clojure
                            "1.11.1"))
    jar (get-target-jar dir)]
    (if (str/includes? jar "no such file")
      (do (shell {:dir dir} "mvn" "-Dmaven.test.skip=true" "package")
          (get-target-jar dir))
      jar)))

(defn -main
  [& args]
  (let [clojure-dir (compile-clojure "/Users/noah/personal/clojure-local-dev/master")]
    (prn clojure-dir)
    (time
      (doseq [lib libraries
              :when (= "clojure" (namespace (::name lib)))]
        (let [dir (gl/procure (:git/url lib) (::name lib) (:git/tag lib))]
          (:exit (shell {:dir dir} (test-cmd lib)))))))
  )
