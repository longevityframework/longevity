package longevity.persistence.sqlite

import longevity.persistence.jdbc.PolyJdbcPRepo

private[sqlite] trait PolySQLitePRepo[M, P] extends SQLitePRepo[M, P] with PolyJdbcPRepo[M, P]
