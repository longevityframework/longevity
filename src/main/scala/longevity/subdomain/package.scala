package longevity

import longevity.subdomain.persistent.Persistent

/** provides support for constructing your subdomain */
package object subdomain {

  // TODO scaladoc
  // TODO name this better
  type AnyKeyVal[P <: Persistent] = KeyVal[P, V] forSome { type V <: KeyVal[P, V] }

}

