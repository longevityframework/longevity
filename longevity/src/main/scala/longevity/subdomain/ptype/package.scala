package longevity.subdomain

/** provides tools for defining the types for your persistent classes */
package object ptype {

  /** an arbitrary [[Key key]] type for a given persistent type `P` */
  private[longevity] type AnyKey[P] = Key[P, V] forSome { type V <: KeyVal[P] }

  /** an arbitrary [[PartitionKey partition key]] type for a given persistent type `P` */
  private[longevity] type AnyPartitionKey[P] = PartitionKey[P, V] forSome { type V <: KeyVal[P] }

}
