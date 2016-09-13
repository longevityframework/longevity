package longevity.context

/** configuration for persistence that is [[PersistenceStrategy persistence strategy]] agnostic */
private[longevity] trait PersistenceConfig {

  /** is optimistic locking turned on? */
  val optimisticLocking: Boolean

}
