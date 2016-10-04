package longevity.subdomain

/** contains subdomain realizations of PTypes, Props and Keys */
package object realized {

  private[longevity] type AnyRealizedKey[P <: Persistent] = RealizedKey[P, V] forSome { type V <: KeyVal[P, V] }

}
