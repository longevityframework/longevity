package longevity.persistence.sqlite

import longevity.persistence.jdbc.DerivedJdbcPRepo

private[sqlite] trait DerivedSQLitePRepo[F[_], M, P, Poly >: P] extends DerivedJdbcPRepo[F, M, P, Poly]
