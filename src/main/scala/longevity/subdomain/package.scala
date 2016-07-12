package longevity

import longevity.subdomain.persistent.Persistent

/** provides tools for constructing your subdomain */
package object subdomain {

  /** an arbitrary [[KeyVal key value]] type for a given persistent type `P` */
  type AnyKeyVal[P <: Persistent] = KeyVal[P, V] forSome { type V <: KeyVal[P, V] }

}

