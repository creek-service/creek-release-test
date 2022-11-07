[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Coverage Status](https://coveralls.io/repos/github/creek-service/creek-release-test/badge.svg?branch=main)](https://coveralls.io/github/creek-service/creek-release-test?branch=main)
[![build](https://github.com/creek-service/creek-release-test/actions/workflows/build.yml/badge.svg)](https://github.com/creek-service/creek-release-test/actions/workflows/build.yml)
[![CodeQL](https://github.com/creek-service/creek-release-test/actions/workflows/codeql.yml/badge.svg)](https://github.com/creek-service/creek-release-test/actions/workflows/codeql.yml)

# Creek release test

Hack space for release testing

Current version: 0.18.8

### Gradle commands

* `./gradlew format` will format the code using [Spotless][1].
* `./gradlew static` will run static code analysis, i.e. [Spotbugs][2] and [Checkstyle][3].
* `./gradlew check` will run all checks and tests.
* `./gradlew coverage` will generate a cross-module [Jacoco][5] coverage report.

[1]: https://github.com/diffplug/spotless
[2]: https://spotbugs.github.io/
[3]: https://checkstyle.sourceforge.io/
[4]: https://github.com/allegro/axion-release-plugin
[5]: https://www.jacoco.org/jacoco/trunk/doc/
[6]: https://coveralls.io/
[7]: https://junit.org/junit5/docs/current/user-guide/
[8]: https://site.mockito.org/
[9]: http://hamcrest.org/JavaHamcrest/index
[10]: https://github.com/google/guava/tree/master/guava-testlib
[11]: https://logging.apache.org/log4j/2.x/
[12]: https://coveralls.io/
