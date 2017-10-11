package longevity.config

/** configuration for persistence that is [[BackEnd back end]] agnostic */
private[longevity] trait PersistenceConfig {

  /** the domain model version. `None` whenever the model is unversioned */
  val modelVersion: Option[String]

  /** should longevity automatically open the connection when the repositories are created? */
  val autoOpenConnection: Boolean

  /** should longevity automatically create schema when the connection is opened? */
  val autoCreateSchema: Boolean

  /** is optimistic locking turned on? */
  val optimisticLocking: Boolean

  /** stamp rows with `createdTimestamp` and `updatedTimestamp`? */
  val writeTimestamps: Boolean

}
