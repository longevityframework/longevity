package longevity.unit.model.annotations

import longevity.model.annotations.packscanToList

package object packscanExample {

  trait AType[A]

  trait BType[B]

  trait CType[C]

  val aTypes = packscanToList[AType[_]]
  val bTypes = packscanToList[BType[_]]
  val cTypes = packscanToList[CType[_]]

}
