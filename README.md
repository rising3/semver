# SemVer [![CI](https://github.com/rising3/semver/actions/workflows/build.yml/badge.svg)](https://github.com/rising3/semver/actions/workflows/build.yml)

SemVer is a set of Java libraries from the [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

**Prerequisites:**
* Java 8 or higher

## Adding SemVer to your build

SemVer Maven group ID is com.github.rising3, and its artifact ID is semver.

To add a dependency on Guava using Maven, use the following:
``` xml
<dependency>
  <groupId>com.github.rising3</groupId>
  <artifactId>semver</artifactId>
  <version>{version}</version>
</dependency>
```

To add a dependency using Gradle:
``` groovy
dependencies {
    implementation("com.github.rising3:semver:{version}")
}
```

## License

The `semver` library is released under version 2.0 of the [Apache License](/LICENSE).

