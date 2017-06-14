package longevity.persistence.inmem

import longevity.persistence.BasePolyRepo

private[inmem] trait PolyInMemPRepo[M, P] extends InMemPRepo[M, P] with BasePolyRepo[M, P]
