package longevity.subdomain

/** TODO */
package object persistent {

  // TODO either delete this or get it to work
  private[longevity] type PersistentUpperBound[P <: Persistent] = PP forSome { type PP >: P <: Persistent }

}
