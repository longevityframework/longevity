package longevity.persistence.sqlite

import longevity.config.PersistenceConfig
import longevity.model.DerivedPType
import longevity.model.DomainModel
import longevity.model.PType
import longevity.model.PolyPType
import longevity.persistence.jdbc.JdbcRepo

/** a SQLite repository for persistent entities of type `P`.
 *
 * @param pType the type of the persistent entities this repository handles
 * @param domainModel the domain model containing the persistent that this repo persists
 * @param session the connection to the sqlite database
 * @param persistenceConfig persistence configuration that is back end agnostic
 */
private[longevity] class SQLiteRepo[P] private (
  pType: PType[P],
  domainModel: DomainModel,
  sessionInfo: JdbcRepo.JdbcSessionInfo,
  persistenceConfig: PersistenceConfig)
extends JdbcRepo[P](pType, domainModel, sessionInfo, persistenceConfig) {

  override def toString = s"SQLiteRepo[${pTypeKey.name}]"

}

private[persistence] object SQLiteRepo {

  def apply[P](
    pType: PType[P],
    domainModel: DomainModel,
    session: JdbcRepo.JdbcSessionInfo,
    config: PersistenceConfig,
    polyRepoOpt: Option[SQLiteRepo[_ >: P]])
  : SQLiteRepo[P] = {
    val repo = pType match {
      case pt: PolyPType[_] =>
        new SQLiteRepo(pType, domainModel, session, config) with PolySQLiteRepo[P]
      case pt: DerivedPType[_, _] =>
        def withPoly[Poly >: P](poly: SQLiteRepo[Poly]) = {
          class DerivedRepo extends {
            override protected val polyRepo: SQLiteRepo[Poly] = poly
          }
          with SQLiteRepo(pType, domainModel, session, config) with DerivedSQLiteRepo[P, Poly]
          new DerivedRepo
        }
        withPoly(polyRepoOpt.get)
      case _ =>
        new SQLiteRepo(pType, domainModel, session, config)
    }
    repo
  }

}
