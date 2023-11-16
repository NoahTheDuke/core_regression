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

## Set-up / Requirements

I developed this on two different computers: Ubuntu 23.04, and 2018 Macbook Pro.
I've done my best to avoid or disable the clojurescript and javascript test
suites, but some still happen.

* javac version: 11.0.20.1
* Clojure CLI version: 1.11.1.1347

## How to use

Fork or clone the repo to some folder. In an adjacent folder, fork or clone
[clojure-local-dev][clojure-local-dev]. Follow the instructions in that repo to
set it up and then make a new branch you wish to test against (for example,
you've called `./new-branch CLJ-2160-no-op-macro`). Now your base folder should
look like:

[clojure-local-dev]: https://github.com/frenchy64/clojure-local-dev

```
$ ls -a
...
clojure-local-dev
core_regression
...
```

`core_regression` uses `tools.cli`, so the options should be fairly obvious. Run
with `clojure -M:run [opts]*`. Opts can be:

* `--[no-]build`: Recompile and install clojure snapshot jar. Defaults to
    `false.`
* `--[no-]test-out`: Print test out to `stdout`. Defaults to `false`.
* `-b`, `--branch`: clojure-local-dev branch to use. Defaults to `master`.
* `-l`, `--library`: Specific libraries to check. (Can be given multiple times.)
* `-n`, `--namespace`: Namespace of libraries to check.
* `-p`, `--[no-]parallel`: Run the test suites in parallel.
* `-h`, `--help`: The help string.

`--build` will call `mvn -ntp -q -Dmaven.test.skip=true clean package`. The
resulting jar will be copied directly into `~/.m2`.

## License

Copyright © Noah

Licensed under MPL v2
