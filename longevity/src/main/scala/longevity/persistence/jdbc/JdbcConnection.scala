package longevity.persistence.jdbc

import java.sql.Connection
import java.sql.PreparedStatement

private[persistence] class JdbcConnection(
  private val connection: () => Connection,
  private val sync: Boolean) {

  def prepareStatement(sql: String) = {
    val c = connection()
    run(c, c.prepareStatement(sql))
  }

  def executeUpdate(ps: PreparedStatement) = {
    val c = connection()
    run(c, ps.executeUpdate())
  }

  def executeQuery(ps: PreparedStatement) = {
    val c = connection()
    run(c, ps.executeQuery())
  }

  def execute(ps: PreparedStatement) = {
    val c = connection()
    run(c, ps.execute())
  }

  private def run[A](c: Connection, f: => A) = if (sync) c.synchronized(f) else f

}
