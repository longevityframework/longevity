# Longevity Change Log

## [0.5-SNAPSHOT]
- 2016.01.12 - rename `RootType.keys` to `RootType.keySet`
- 2016.01.12 - rename `RootType.indexes` to `RootType.indexSet`
- 2016.03.01 - add Cassandra back end
- 2016.03.01 - deprecate all methods that allow for use of a string
               property path in place of a Prop. affected methods are
               RootEntity.{key, index}, Query.{ eqs, neq, lt, lte, gt,
               gte }, and the corresponging methods in the QueryDsl.
- 2016.03.01 - rework QuerySpec

## [0.4.1] - 2016.01.12
- remove printlns
