package longevity.persistence.sqlite

import longevity.persistence.jdbc.PolyJdbcRepo

private[sqlite] trait PolySQLiteRepo[M, P] extends SQLiteRepo[M, P] with PolyJdbcRepo[M, P]
