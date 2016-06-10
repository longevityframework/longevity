# Longevity Change Log

## [0.9-SNAPSHOT]

- 2016.06.10 - add noop query `Query.All()`
- 2016.06.07 - add method `Deleted.get`
- 2016.06.07 - fix method names `LiftFPState.mapRoot`,
  `LiftFPState.flatMapRoot`, `LiftFOPState.mapRoot`, and
  `LiftFOPState.flatMapRoot` by replacing `Root` with `P`.
- 2016.06.01 - `CoreDomain`, `SupportingSubdomain` and
  `GenericSubdomain` are not actual traits that extend `Subdomain`
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
