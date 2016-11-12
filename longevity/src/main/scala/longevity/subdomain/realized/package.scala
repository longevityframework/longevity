package longevity.subdomain

/** contains subdomain realizations of PTypes, Props and Keys */
package object realized {

  private[longevity] type AnyRealizedKey[P] =
    RealizedKey[P, V] forSome { type V <: KeyVal[P, V] }

  private[longevity] type AnyRealizedPartitionKey[P] =
    RealizedPartitionKey[P, V] forSome { type V <: KeyVal[P, V] }

}
