package longevity.persistence.inmem

import longevity.persistence.BasePolyRepo
import longevity.subdomain.persistent.Persistent

private[inmem] trait PolyInMemRepo[P <: Persistent] extends InMemRepo[P] with BasePolyRepo[P] {

}
