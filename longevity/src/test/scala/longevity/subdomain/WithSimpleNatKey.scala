package longevity.subdomain

case class WithSimpleNatKey(
  uri: String,
  scores: List[Int],
  topScore: Int)
extends RootEntity

object WithSimpleNatKey extends RootEntityType[WithSimpleNatKey]
