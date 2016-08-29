# Longevity Change Log

## [0.11-SNAPSHOT]

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
