package longevity.persistence.jdbc

import java.sql.Connection
import java.sql.DriverManager
import longevity.config.JdbcConfig
import longevity.config.PersistenceConfig
import longevity.context.Effect
import longevity.exceptions.persistence.ConnectionClosedException
import longevity.exceptions.persistence.ConnectionOpenException
import longevity.model.ModelType
import longevity.persistence.Repo
import scala.collection.mutable.WeakHashMap

private[persistence] abstract class BaseJdbcRepo[F[_], M] private[persistence](
  effect: Effect[F],
  modelType: ModelType[M],
  persistenceConfig: PersistenceConfig,
  jdbcConfig: JdbcConfig)
extends Repo[F, M](effect, modelType, persistenceConfig) {

  type R[P] <: JdbcPRepo[F, M, P]

  private var connectionOpt: Option[Connection] = None

  protected lazy val connection = () => connectionOpt match {
    case Some(c) => c
    case None => throw new ConnectionClosedException
  }

  protected def openBaseConnectionBlocking(): Unit = synchronized {
    if (connectionOpt.nonEmpty) throw new ConnectionOpenException
    connectionOpt = Some(BaseJdbcRepo.acquireSharedConn(jdbcConfig))
  }

  protected def createBaseSchemaBlocking(): Unit = ()

  protected def closeConnectionBlocking(): Unit = synchronized {
    if (connectionOpt.isEmpty) throw new ConnectionClosedException
    BaseJdbcRepo.releaseSharedConn(jdbcConfig)
    connectionOpt = None
  }

}

private[persistence] object BaseJdbcRepo {

  // it's bad news to create multiple connections against a single JDBC database. this
  // is not a problem for typical programatic usage, where there is one LongevityContext,
  // and thus one JDBC connection. but in my test suites it is a different story, as we
  // have several tests running in parallel, hitting the same test database.
  //
  // ideally, we would craft the test suite to share LongevityContexts when possible, but
  // even this would not be enough, because in the test suite, multiple contexts actually
  // target the same database (e.g., contexts with optimistic locking turned on and off).
  // so we would have to track the connections themselves, and figure out when to actually
  // close the connection. aside from being a real pain, this would make for some
  // convoluted tests. instead of doing this, we share the conns here. its a bit of an
  // overhead, but it might actually come in useful for some user somewhere. and we could
  // always hide it behind a configuration setting if any users complain about the
  // overhead.

  private case class SharedConn(numHolders: Int, conn: Connection)
  private val sharedConns = WeakHashMap[JdbcConfig, SharedConn]()

  private def acquireSharedConn(config: JdbcConfig): Connection = {
    BaseJdbcRepo.synchronized {
      if (sharedConns.contains(config)) {
        val sc = sharedConns(config)
        sharedConns += config -> sc.copy(numHolders = sc.numHolders + 1)
        sc.conn
      } else {
        Class.forName(config.driverClass)
        val conn = DriverManager.getConnection(config.url)
        sharedConns += config -> SharedConn(1, conn)
        conn
      }
    }
  }

  private def releaseSharedConn(config: JdbcConfig): Unit = {
    BaseJdbcRepo.synchronized {
      if (sharedConns.contains(config)) {
        val sc = sharedConns(config)
        if (sc.numHolders == 1) {
          sc.conn.close()
          sharedConns -= config
        } else {
          sharedConns += config -> sc.copy(numHolders = sc.numHolders - 1)
        }
      }
    }
  }

}
