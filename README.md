# core-regression

Like Rust's crater or mypy's mypy-primer, this is an experiment with clojure.core
regression testing: build a large corpus of stable libraries and apps that run with the
latest version of clojure, and then test Clojure updates to master or patches to see if
anything new breaks. The Clojure core test suite is janky and imo pretty poor, but we've
had 15+ years of stable usage by the wider community, so this could be an automated way
to both ensure that stability going forward and maybe provide some safety for the core
team to iterate faster with less manual work.

## License

Copyright © 2023 Noah
