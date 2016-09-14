package longevity.context

/** configuration for persistence that is [[PersistenceStrategy persistence strategy]] agnostic */
private[longevity] trait PersistenceConfig {

  /** should longevity autogenerate schema when the repositories are created? */
  val autogenerateSchema: Boolean

  /** is optimistic locking turned on? */
  val optimisticLocking: Boolean

}
