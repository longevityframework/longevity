package longevity.persistence.inmem

import longevity.persistence.BasePolyRepo

private[inmem] trait PolyInMemRepo[P] extends InMemRepo[P] with BasePolyRepo[P] {

}
