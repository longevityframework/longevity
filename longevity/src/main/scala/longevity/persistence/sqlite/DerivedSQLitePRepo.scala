package longevity.persistence.sqlite

import longevity.persistence.jdbc.DerivedJdbcPRepo

private[sqlite] trait DerivedSQLitePRepo[M, P, Poly >: P] extends DerivedJdbcPRepo[M, P, Poly]
