package longevity.persistence.inmem

import longevity.persistence.BasePolyRepo

private[inmem] trait PolyInMemRepo[M, P] extends InMemRepo[M, P] with BasePolyRepo[M, P]
