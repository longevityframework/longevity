package longevity.persistence.sqlite

import longevity.persistence.jdbc.DerivedJdbcRepo

private[sqlite] trait DerivedSQLiteRepo[P, Poly >: P] extends DerivedJdbcRepo[P, Poly] {
}
