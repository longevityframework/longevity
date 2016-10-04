package longevity

import longevity.context.BackEnd

case class ConfigMatrixKey(
  backEnd: BackEnd,
  autocreateSchema: Boolean,
  optimisticLocking: Boolean)

object ConfigMatrixKey {

  // this version gives 4x configs per back end:
  def values = for {
    backEnd <- BackEnd.values
    autocreateSchema <- true :: false :: Nil
    optimisticLocking <- true :: false :: Nil
  } yield {
    ConfigMatrixKey(backEnd, autocreateSchema, optimisticLocking)
  }

  // cheat here and give 2x configs per back end. each config flag is still tested
  def sparseValues = for {
    backEnd <- BackEnd.values
    key <- Seq(
      ConfigMatrixKey(backEnd, true, false),
      ConfigMatrixKey(backEnd, false, true))
  } yield {
    key
  }

}
