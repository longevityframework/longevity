package longevity.context

import org.joda.time.DateTime

/** configuration for persistence that is [[PersistenceStrategy persistence strategy]] agnostic */
private[longevity] trait PersistenceConfig {

  /** is optimistic locking turned on? */
  val optimisticLocking: Boolean

  /** retrieve a value to fill `PState.modifiedDate` based on the `optimisticLocking` flag */
  def modifiedDate = if (optimisticLocking) Some(DateTime.now) else None

}
