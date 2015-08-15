package longevity.subdomain

import emblem.typeKey

case class WithSimpleNatKey(
  uri: String,
  topScore: Int,
  scoreList: List[Int],
  scoreOption: Option[Int],
  scoreSet: Set[Int])
extends RootEntity

object WithSimpleNatKey extends RootEntityType[WithSimpleNatKey]()(
  typeKey[WithSimpleNatKey],
  ShorthandPool.empty)
