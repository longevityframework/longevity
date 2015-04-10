longevity
=========

A Domain Driven Design persistence framework for Scala and MongoDB.

Story board is here: https://www.pivotaltracker.com/n/projects/1231978

longevity currently contains three subprojects:

- **emblem** - a metaprogramming library for managing types and reflecting case classes
- **longevity** - a persistence library for Scala and Mongo
- **musette** - a content resource management system

Out of the three, emblem is the only one close to ready for general
use. There isn't any external documentation yet, but there are some
good examples in the Scaladocs and in the test suite.

To use emblem, first clone the repository:

    git clone https://github.com/sullivan-/longevity.git

Then, compile the project and publish locally:

    cd longevity
    sbt "project emblem" publish-local

Now you can use the project my including the following dependency in
your project:

    libraryDependencies += "net.jsmscs" %% "emblem" % "0.0.0-SNAPSHOT"

