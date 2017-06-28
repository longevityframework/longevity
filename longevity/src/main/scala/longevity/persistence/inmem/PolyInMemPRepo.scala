package longevity.persistence.inmem

import longevity.persistence.BasePolyRepo

private[inmem] trait PolyInMemPRepo[F[_], M, P] extends InMemPRepo[F, M, P] with BasePolyRepo[F, M, P]
