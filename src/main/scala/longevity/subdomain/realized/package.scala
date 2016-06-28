package longevity.subdomain

import longevity.subdomain.persistent.Persistent

/** contains subdomain realizations of PTypes, Props and Keys */
package object realized {

  // TODO this is used in one place do i really want it? maybe it is elsewhere and i havent realized it?
  type AnyRealizedKey[P <: Persistent] = RealizedKey[P, V] forSome { type V <: KeyVal[P] }

}
