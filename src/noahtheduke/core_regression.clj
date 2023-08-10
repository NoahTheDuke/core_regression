(ns noahtheduke.core-regression
  (:require
    [babashka.process :refer [shell]]
    [clojure.tools.gitlibs :as gl]))

(def libraries
  [
   #_#_#_#_
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
    ::test-cmd "clojure -T:build test"
    :git/url "https://github.com/seancorfield/honeysql.git"
    :git/tag "v2.4.1045"}
   ])

(defn -main
  [& args]
  (doseq [lib libraries]
    (let [dir (gl/procure (:git/url lib) (::name lib) (:git/tag lib))]
      (prn dir)
      (prn (:exit (shell {:dir dir} (::test-cmd lib))))))
  )
