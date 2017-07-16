package longevity

import longevity.config.BackEnd

case class ConfigMatrixKey(
  backEnd: BackEnd,
  autoCreateSchema: Boolean,
  optimisticLocking: Boolean,
  writeTimestamps: Boolean)

object ConfigMatrixKey {

  // toggle to test a single back end at a time:
  // private val backEnds = Seq(longevity.config.SQLite)
  private val backEnds = BackEnd.values

  /** all config combos per back end */
  def values = for {
    backEnd           <- backEnds
    autoCreateSchema  <- true :: false :: Nil
    optimisticLocking <- true :: false :: Nil
    writeTimestamps   <- true :: false :: Nil
  } yield {
    ConfigMatrixKey(backEnd, autoCreateSchema, optimisticLocking, writeTimestamps)
  }

  /** one config per back end */
  def sparseValues = backEnds.map(ConfigMatrixKey(_, false, false, false))

}
