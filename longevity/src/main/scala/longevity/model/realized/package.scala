package longevity.model

/** contains domain model realizations of PTypes, Props and Keys */
package object realized {

  private[longevity] type AnyRealizedKey[P] = RealizedKey[P, V] forSome { type V <: KeyVal[P] }

  private[longevity] type AnyRealizedPartitionKey[P] = RealizedPartitionKey[P, V] forSome { type V <: KeyVal[P] }

}
