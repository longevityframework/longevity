package longevity.subdomain

import emblem.typeKey

case class WithSimpleNatKey(
  uri: String,
  scores: List[Int],
  topScore: Int)
extends RootEntity

object WithSimpleNatKey extends RootEntityType[WithSimpleNatKey]()(
  typeKey[WithSimpleNatKey],
  ShorthandPool.empty)
