package longevity.persistence.sqlite

import longevity.persistence.jdbc.DerivedJdbcRepo

private[sqlite] trait DerivedSQLiteRepo[M, P, Poly >: P] extends DerivedJdbcRepo[M, P, Poly]
