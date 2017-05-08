# Longevity Changelog

## [0.23.0] - TODO

- 2017.05.08 - Merge `longevity.persistence.Repo` and `longevity.persistence.RepoPool` APIs. There
  is now a single repository, and the create/retrieve/update/delete/query methods now all take the
  persistent type as a type parameter. To migrate, code that used to look like this:

  `longevityContext.repoPool[User].create(user)`

  now looks like this:

  `longevityContext.repo.create[User](user)`

  In most cases, you can leave off type parameter, as the compiler can easily infer it:

  `longevityContext.repo.create(user)`

## [0.22.0] - 2017.03.25 - Stream Queries to Multiple Streaming Libraries

- 2017.03.24 - Rename `Repo.retrieveByQuery` to `Repo.queryToFutureVec`. The return type of this
  method has also been adjusted from `Future[Seq[PState[P]]]` to `Future[Vector[PState[P]]]`.
- 2017.03.24 - Add method `Repo.queryToItereator`.
- 2017.03.24 - Rename `Repo.streamByQuery` to `Repo.queryToAkkaStream`.
- 2017.03.24 - Add method `Repo.queryToFS2`.
- 2017.03.24 - Add method `Repo.queryToIterateeIo`.
- 2017.03.24 - Add method `Repo.queryToPlay`.

## [0.21.0] - 2017.03.04 - JDBC Back End and Timestamps

- 2017.02.08 - Add new `JDBC` back end.
- 2017.02.06 - Rename `longevity.config.SQLiteConfig` to
  `JdbcConfig`. Rename `longevity.config.LongevityConfig.sqlite` to
  `jdbc`. Rename `longevity.config.TestConfig.sqlite` to `jdbc`.
- 2017.02.15 - Add configuration flag `longevity.writeTimestamps`.

## [0.20.0] - 2017.01.16 - SQLite Back End

- 2017.01.12 - Rename dependency bundle artifact from
  `longevity-mongo-deps` to `longevity-mongodb-deps`.
- 2017.01.12 - Rename `longevity.config.Mongo` to
  `longevity.config.MongoDB`. Rename `longevity.config.MongoConfig` to
  `longevity.config.MongoBDConfig`.
- 2017.01.14 - Add SQLite back end. See `longevity.config.SQLite` and
  `longevity.config.SQLiteConfig`.
- 2017.01.14 - Rename `longevity.model.PType.partitionKey` to
  `primaryKey`.

## [0.19.0] - 2016.12.09 - Rename Subdomain to Domain Model

- 2016.12.09 - Rename package `longevity.subdomain` to
  `longevity.model`. Also `longevity.exceptions.subdomain` to
  `longevity.exceptions.model`.
- 2016.12.09 - Rename `longevity.model.Subdomain` to `DomainModel`.
- 2016.12.09 - Rename annotation
  `longevity.model.annotations.subdomain` to `domainModel`.
- 2016.12.09 - Move config classes `LongevityConfig`, `BackEnd`,
  `MongoDBConfig`, `CassandraConfig`, `TestConfig`, and
  `PersistenceConfig` from package `longevity.context` to new package
  `longevity.config`.

## [0.18.0] - 2016.12.07 - Annotation Macros

- 2016.12.06 - Prevent emblem leakage of `TestDataGenerator` and
  `CustomGeneratorPool` by wrapping them in longevity
  classes. Slightly simplified the API for adding a custom test data
  generators.
- 2016.12.06 - Add `LongevityContext` constructors and `apply` methods
  that take either a `LongevityConfig` or a Typesafe config.
- 2016.12.06 - Add annotation macro `@subdomain` in package
  `longevity.subdomain.annotations`.
- 2016.12.06 - Add `Subdomain` constructor and `apply` method that
  collect all the `PTypes` and `CTypes` by package scanning.
- 2016.11.30 - Add annotation macros `@component`,
  `@derivedComponent`, `@derivedPersistent`, `@keyVal`, `@mprops`,
  `@persistent`, `@polyComponent`, and `@polyPersistent` in package
  `longevity.subdomain.annotations`.
- 2016.11.30 - Remove scanning of `PType` inner objects `keys` and
  `indexes` to look for keys and indexes. Users must now define
  `PType.keySet`, and override `PType.indexSet`, to declare keys and
  indexes. We made this change since the object scanning was
  superfluous, and complicates the API. (In contrast, scanning for
  properties is useful, as users need to be able to call properties by
  name. However, users never really need to call keys and indexes by
  name, so there is no advantage to naming them in an inner object.)
- 2016.11.23 - Add method `PType.emptyKeySet`.
- 2016.11.23 - Make `PType.partitionKey` methods return `Key[P]`
  instead of `PartitionKey[P]`. this is for convenience of Scala 2.11
  users, so they dont have to declare the full type of their keySet.
- 2016.11.22 - Remove second type parameter from
  `longevity.subdomain.Key` and `longevity.subdomain.PartitionKey`.
  Remove types `longevity.subdomain.AnyKey` and
  `longevity.subdomain.AnyPartitionKey`, as they no longer serve any
  purpose.

## [0.17.0] - 2016.11.15 - API Simplifications

- 2016.11.13 - Remove second type parameter from
  `longevity.subdomain.KeyVal`.
- 2016.11.12 - Rename `longevity.subdomain.EType` (for "embeddable
  type") to `longevity.subdomain.CType` (for "component type").
- 2016.11.12 - Remove traits `longevity.subdomain.Embeddable` and
  `longevity.subdomain.Persistent`. Users no longer need to extend
  their subdomain classes with these empty marker traits.
- 2016.11.12 - Change `RepoCrudSpec` from a `FeatureSpec` to a
  `FlatSpec`. Users using `LongevityContext.repoCrudSpec` will notice
  significantly less verbose test output.

## [0.16.0] - 2016.11.10 - Partition Keys

- 2016.11.09 - Uniformly convert `DateTime` to UTC time zone. This
  seems like the best approach right now, as both Cassandra and
  MongoDB back ends support timestamps without time zone
  information. It also resolves issues with sorting dates.
- 2016.11.09 - Implement partition keys. Please see the [user
  manual](http://longevityframework.github.io/longevity/manual/ptype/partition-keys.html)
  for details.
- 2016.10.27 - Replace Casbah with Java driver in MongoDB back end. We
  are now using the [vanilla Java driver for
  Mongo](http://mongodb.github.io/mongo-java-driver/3.2/driver/). This
  change should not affect users.
- 2016.10.27 - Fix MongoDB URIs so they work in a sharded environment.
- 2016.10.27 - Disallow keys and indexes that duplicate the properties
  of other keys or indexes. This may cause existing code to break. Fix
  is to root out the duplicates. If you have a key and an index that
  duplicate each other, you can safely remove the index, as it is
  redundant with the key.

## [0.15.0] - Query Enhancements

- 2016.10.08 - Remove `KeyVal.key` and change `KeyVal` from an
  abstract class into a trait. To migrate existing code, you will need
  to remove the `Key` argument supplied to each of your `KeyVal` types.
- 2016.10.12 - Queries have been extended with "order-by", "offset",
  and "limit" clauses. The query DSL has likewise been extended.
- 2016.10.12 - Many of the classes used to build queries have been
  re-organized into new package `longevity.subdomain.query`. This
  should not affect you if you are only using the query DSL.

Please refer to the [user
manual](http://longevityframework.github.io/longevity/manual/query/)
for the latest on the query API.

## [0.14.0] - Subdomain Repackaging

- 2016.10.04 - Rename `DerivedType` to `DerivedEType`. Rename
  `PolyType` to `PolyEType`.
- 2016.10.04 - Move `CoreDomain`, `GenericSubdomain`, and
  `SupportingSubdomain` from package `longevity.subdomain` to
  `longevity.ddd.subdomain`.
- 2016.10.04 - Move `Entity`, `EntityType`, `ValueObject`, and
  `ValueType` from package `longevity.subdomain.embeddable` to
  `longevity.ddd.subdomain`.
- 2016.10.04 - Move `Event`, `Root`, and `ViewItem` from package
  `longevity.subdomain.persistent` to package
  `longevity.ddd.subdomain`.
- 2016.10.04 - Move `EventType`, `RootType`, and `View` from package
  `longevity.subdomain.ptype` to package `longevity.ddd.subdomain`.
- 2016.10.04 - Move `DerivedEType`, `EType`, `ETypePool`,
  `Embeddable`, and `PolyEType` from package
  `longevity.subdomain.embeddable` to `longevity.subdomain`.
- 2016.10.04 - Move `Persistent` from package
  `longevity.subdomain.persistent` to `longevity.subdomain`.
- 2016.10.04 - Move `DerivedPType`, `PType`, `PTypePool`, and
  `PolyPType` from package `longevity.subdomain.ptype` to
  `longevity.subdomain`.
- 2016.10.04 - Move package `longevity.ddd.subdomain` into a separate
  project called `longevity-ddd`. If you want to continue using the
  wrapper classes found there, please add the following dependency to
  your project: `libraryDependencies += "org.longevityframework" %%
  "longevity-ddd" % "x.y.z"`.

## [0.13.1] - 2016.10.03 - Reflection Bugfix

There was a bug in our use of Scala reflection. In brief, we were
using the class loader (i.e., scala reflection `Mirror`) that was used
to load the longevity library. This is bogus, as we are reflecting
against user classes! We changed things to reflect on the mirror of
the `TypeTags` (they get wrapped in `TypeKeys`) that the user library
provides to use.

End result is that projects that do funky things with class loaders
will not get reflection exceptions when using longevity.

## [0.13.0] - 2016.09.22 - Jetsam

Some odds and ends that have been accumulating in the backlog.

- 2016.09.22 - Rename `PersistenceStrategy` to `BackEnd`. Move
  `BackEnd` from being a separate argument to `LongevityContext`
  creator methods, to being part of the config, under config property
  `longevity.backEnd`.
- 2016.09.19 - Add `OPState` to go along with `FPState` and `FOPState`.
- 2016.09.19 - Add JSON marshallers at
  `LongevityContext.jsonMarshaller` and
  `LongevityContext.jsonUnmarshaller`.

## [0.12.0] - 2016.09.13 - Flotsam

Some odds and ends that have been accumulating in the backlog.

- 2016.09.15 - Remove `PState.dirty`. We are taking this out because
  there we may decide to stop keeping track of the original version
  of the persistent object, in order to reduce memory usage.
- 2016.09.14 - Add `RepoPool.createSchema()` and configuration flag
  `autogenerateSchema`.
- 2016.09.13 - Add logging for all `Repo` methods and database calls.
- 2016.09.13 - Add API method `RepoPool.closeSession()`. This was
  added because leaving the Cassandra session open can cause user
  programs to fail to terminate under certain circumstances, If your
  main program is hanging when using Cassandra, please call this
  method at the end of your program.

## [0.11.0] - 2016.08.29 - API Simplifications

- 2016.08.29 - Add factory methods for `EType` and all its
  descendents. The older pattern of making embeddable companion
  objects into `ETypes` (e.g., `case class Email extends
  EType[Email]`) still works, but now you can just mention the `EType`
  directly, when building your subdomain. (E.g., `Subdomain(???, ???,
  ETypePool(EType[Email]))`).
- 2016.08.28 - Get rid of `DerivedType.polyType` and
  `DerivedPType.polyPType`. `DerivedType` and `DerivedPType` are now
  abstract classes instead of traits, so users may need to reorder
  their inheritance `with` clauses. (It's highly unlikely a user would
  have been be using these traits to extend a class.)

## [0.10.0] - 2016.08.25 - Optimistic Locking

- 2016.08.25 - Fix JSON translation of DateTimes to use time zone
  codes instead of offsets. Fix JSON parser to respect the time zone
  in the string representation of the DateTime.
- 2016.08.24 - Add optimistic locking. To turn it on, you will need to
  set `longevity.optimisticLocking = true` in your typesafe config.
- 2016.08.23 - Add `LongevityContext.testDataGenerator`.
- 2016.08.17 - Add support for `Persistents` and `Embeddables` that
  are case objects.
- 2016.07.29 - Add `LongevityConfig` for well-typed
  configuration. Users can use `LongevityConfig` instead of a Typesafe
  Config to configure their context. Just use the `LongevityContext`
  constructor instead of the `LongevityContext.apply` factor method.
- 2016.07.29 - Make `PType.indexes` optional. You used to have to
  declare an empty `indexes` singleton object within your `PType` if
  you had no indexes. Now, you can just leave it out. This should have
  no effect on existing code, but you can go back and remove empty
  `indexes` objects if you want.
- 2016.07.22 - Make [Akka
  Streams](http://doc.akka.io/docs/akka/2.4.8/scala.html) an optional
  dependency. If you are using `Repo.streamByQuery`, you must now
  declare a dependency on Akka Streams yourself: `libraryDependencies
  += "com.typesafe.akka" %% "akka-stream" % "2.4.9"`.

## [0.9.1] - 2016.08.24 - Bug Fix Release

- 2016.08.24 - There was an error in the build that caused longevity
  poms to refer to the emblem GitHub project, instead of the longevity
  GitHub project. This release fixes that. There are no code changes
  whatsoever.

## [0.9.0] - 2016.07.15 - Streamlined API

- 2016.07.12 - Completely rework `KeyVal` and `Key` for improved
  understandability and ease of use. please see the manual for
  details.
- 2016.07.12 - `Assoc`s are gone. please use `KeyVal`s and `Key`s
  instead.
- 2016.07.12 - a `PType`'s properties and keys are now only realized
  when the `Subdomain` is constructed. this should have no affect on
  the user, except that some exceptions for malformed properties will
  be delayed until `Subdomain` initialization. also, properties
  created outside of `PType.propSet` will no longer work.
- 2016.06.23 - shorthands are gone. please use single-property
  embeddables such as `ValueObject` instead.
- 2016.06.21 - single-property embeddables are now inlined. this has
  no affect on the user other than how the persistents are translated
  into JSON/BSON.
- 2016.06.20 - add parent types `Embeddable` and `EType` for `Entity`,
  `ValueObject`, `EntityType`, and `ValueType`.
- 2016.06.10 - add noop query `Query.All()`.
- 2016.06.07 - add method `Deleted.get`.
- 2016.06.07 - fix method names `LiftFPState.mapRoot`,
  `LiftFPState.flatMapRoot`, `LiftFOPState.mapRoot`, and
  `LiftFOPState.flatMapRoot` by replacing `Root` with `P`.
- 2016.06.01 - `CoreDomain`, `SupportingSubdomain` and
  `GenericSubdomain` are now actual traits that extend `Subdomain`
  (instead of just type aliases). this allows users to directly
  subclass these three types if they wish.

## [0.8.1] - 2016.06.01 - Bug Fix Release

- 2016.06.01 - make Akka streams non-optional
  dependency. difficult-to-resolve linking problems occur when this is
  optional. we might revisit this later but for now the best solution
  is to make it non-optional.

## [0.8.0] - 2016.05.24 - Streaming Queries

- 2016.05.24 - add API method `Repo.streamByQuery(query: Query[P]):
  Source[PState[P], NotUsed]`.

## [0.7.0] - 2016.05.18 - Entity Polymorphism

- 2016.05.18 - users can now subclass `Shorthand`.
- 2016.05.12 - `PType` and sub-classes no longer take an implicit
  `ShorthandPool` argument.
- 2016.05.12 - `Subdomain`, `CoreDomain`, `SupportingSubdomain`, and
  `GenericSubdomain` factory method signatures have changed. They now
  have a single parameter list, and the `ShorthandPool` parameter is
  no longer implicit.
- 2016.05.12 - `Persistent` no longer inherits from `Entity`. `PType`
  no longer inherits from `EntityType`. these changes should not
  affect user code.
- 2016.05.12 - modify `Subdomain.apply` to separate out
  `entityTypePool` into `pTypePool` and `entityTypePool`.
- 2016.05.12 - add `PolyType`, `DerivedType`, `PolyPType`, and
  `DerivedPType`. see [user
  manual](http://longevityframework.github.io/longevity/manual/poly/).
- 2016.05.12 - move the following classes from package
  longevity.subdomain to package longevity.subdomain.entity:
  - `EntityTypePool`
  - `EntityType`
  - `Entity`
  - `ValueObject`
  - `ValueType`

## [0.6.0] - 2016.03.20 - API Improvements

- 2016.03.02 - add implicit execution context parameter to: all `Repo`
  methods; `RepoPool.createMany`; and `LongevityContext.repoCrudSpec`
  and `inMemTestRepoCrudSpec`. users now need to provide execution
  contexts to use all these methods. the easiest way to do this is to
  include `import scala.concurrent.ExecutionContext.Implicits.global`
  at the top of the file.
- 2016.03.08 - update to latest version of library dependencies
  casbah (3.1.1) and cassandra (3.0.0).
- 2016.03.08 - add sub-projects `longevity-cassandra-deps` and
  `longevity-mongo-deps`.
- 2016.03.10 - replace `Root` with `Persistent`. give `Persistent`
  three child traits: `Root`, `ViewItem`, and `Event`. these changes
  should not affect existing code that uses `Root`.
- 2016.03.10 - replace `RootType` with `PType`. give `PType` three
  child traits: `RootType`, `View`, and `EventType`. these changes
  should not affect existing code that uses `RootType`.
- 2016.03.25 - rework `PType` API for `keySet` and `indexSet`. please
  see the latest documentation for a review of the new API.

## [0.5.0] - 2016.03.02 - Cassandra Back End

- 2016.01.12 - rename `RootType.keys` to `RootType.keySet`.
- 2016.01.12 - rename `RootType.indexes` to `RootType.indexSet`.
- 2016.03.01 - add Cassandra back end.
- 2016.03.01 - deprecate all methods that allow for use of a string
  property path in place of a `Prop`. affected methods are
  `RootEntity.{ key, index }`, `Query.{ eqs, neq, lt, lte, gt, gte }`,
  and the corresponging methods in the `QueryDsl`.
- 2016.03.01 - rework `QuerySpec`.
- 2016.03.02 - provide a parent trait `PRef` for `Assoc` and
  `KeyVal`. merge the two versions of the `Repo` methods `retrieve`
  and `retrieveOne` so that they take a `PRef`. this shouldn't affect
  any client code.

## [0.4.1] - 2016.01.12 - Remove Printlns

## [0.4.0] - 2016.01.12 - Initial Public Release
