package longevity.persistence.sqlite

import longevity.persistence.jdbc.PolyJdbcPRepo

private[sqlite] trait PolySQLitePRepo[F[_], M, P] extends SQLitePRepo[F, M, P] with PolyJdbcPRepo[F, M, P]
