package longevity.ddd.subdomain

import longevity.subdomain.Persistent

/** a domain entity that serves as an aggregate root. this is an empty, marker
 * trait.
 */
trait Root extends Persistent
