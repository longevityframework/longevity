package longevity.subdomain

/** provides tools for defining the types for your persistent classes */
package object ptype {

  /** an arbitrary [[Key key]] type for a given persistent type `P` */
  type AnyKey[P <: Persistent] = Key[P, V] forSome { type V <: KeyVal[P, V] }

  /** an arbitrary [[PartitionKey partition key]] type for a given persistent type `P` */
  type AnyPartitionKey[P <: Persistent] = PartitionKey[P, V] forSome { type V <: KeyVal[P, V] }

}
