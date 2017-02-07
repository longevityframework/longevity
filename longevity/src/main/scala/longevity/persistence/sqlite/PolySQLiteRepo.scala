package longevity.persistence.sqlite

import longevity.persistence.jdbc.PolyJdbcRepo

private[sqlite] trait PolySQLiteRepo[P] extends SQLiteRepo[P] with PolyJdbcRepo[P] {
}
