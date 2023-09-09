# core-regression

Like Rust's [crater][crater] or mypy's [mypy_primer][mypy_primer], this is an
experiment with clojure.core regression testing: build a large corpus of stable
libraries and apps that run with the latest version of clojure, and then test
Clojure updates to master or patches to see if anything new breaks. The Clojure
core test suite is janky and imo pretty poor, but we've had 15+ years of stable
usage by the wider community, so this could be an automated way to both ensure
that stability going forward and maybe provide some safety for the core team to
iterate faster with less manual work.

[crater]: https://github.com/rust-lang/crater
[mypy_primer]: https://github.com/hauntsaninja/mypy_primer

As of 2023-09-09, there are 271 libraries tested. Takes roughly an hour on my
2019 Macbook Pro to run all of the non-skipped test suites. There are quite
a few failing tests, which require manual intervention to either skip or fix
somehow (removing `:javac-options` from `project.clj` or calling `clojure
-T:build ...` etc). Once those are verified as permanently failing (and thus
skippable) or just need some manual support, the suite should be reliable.

I have been unable to figure out an automatic method of determining the
necessary setup and test commands for a given clojure repo, so until then, we'll
have to continue to manually add libraries and specify working tags or shas.

## License

Copyright © Noah

Licensed under MPL v2
