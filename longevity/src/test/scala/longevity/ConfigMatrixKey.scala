package longevity

import longevity.config.BackEnd

case class ConfigMatrixKey(
  backEnd: BackEnd,
  autocreateSchema: Boolean,
  optimisticLocking: Boolean,
  writeTimestamps: Boolean)

object ConfigMatrixKey {

  /** all config combos per back end */
  def values = for {
    backEnd           <- BackEnd.values
    autocreateSchema  <- true :: false :: Nil
    optimisticLocking <- true :: false :: Nil
    writeTimestamps   <- true :: false :: Nil
  } yield {
    ConfigMatrixKey(backEnd, autocreateSchema, optimisticLocking, writeTimestamps)
  }

  /** one config per back end */
  def sparseValues = BackEnd.values.map(ConfigMatrixKey(_, false, false, false))

}
