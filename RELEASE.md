# steps to follow to do longevity / emblem release x.y.0:

- make sure `sbt test` and `sbt doc` are both passing before you start
- update the version from `x.y-SNAPSHOT` to `x.y.0` in `LongevityBuild`
  - update documentation at three points:
    - https://github.com/longevityframework/emblem/wiki/Setting-up-a-Library-Dependency-on-emblem
    - manual/project-setup.md on longevity branch gh-pages
    - src/test/scala/longevity/integration/quickStart/QuickStartSpec.scala on longevity master branch
- create a `longevity-x.y.0` tag [here](https://github.com/longevityframework/longevity/releases)
- `sbt publishSigned`
- go here: [https://oss.sonatype.org/#stagingRepositories](https://oss.sonatype.org/#stagingRepositories)
  - find and select the longevity repo under Staging Repositories
  - click the close button
  - click the release button
- test it out in a project that uses longevity and emblem dependencies
- up the version to `x.y+1-SNAPSHOT` in `LongevityBuild`




