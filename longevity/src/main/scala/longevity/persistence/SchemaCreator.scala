package longevity.persistence

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** non-destructively creates any top-level schema for the chosen persistence
 * strategy, such as a cassandra keyspace. table-level schema is created by the
 * individual repositories via `BaseRepo.createSchema`
 */
private[persistence] trait SchemaCreator {

  def createSchema()(implicit context: ExecutionContext): Future[Unit]

}

private[persistence] object SchemaCreator {

  val empty: SchemaCreator = new SchemaCreator {
    def createSchema()(implicit context: ExecutionContext) = Future.successful(())
  }

}
