package longevity.persistence.mongo

import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import longevity.config.MongoDBConfig
import longevity.config.PersistenceConfig
import longevity.exceptions.persistence.ConnectionClosedException
import longevity.exceptions.persistence.ConnectionOpenException
import longevity.model.ModelType
import longevity.model.PType
import longevity.persistence.Repo

private[persistence] class MongoRepo[M] private[persistence](
  modelType: ModelType[M],
  persistenceConfig: PersistenceConfig,
  private val mongoConfig: MongoDBConfig)
extends Repo[M](modelType, persistenceConfig) {

  type R[P] = MongoPRepo[M, P]

  private var sessionOpt: Option[MongoSession] = None

  private lazy val session = () => sessionOpt match {
    case Some(s) => s
    case None => throw new ConnectionClosedException
  }

  protected def buildPRepo[P](pType: PType[M, P], polyRepoOpt: Option[R[_ >: P]] = None): R[P] =
    MongoPRepo[M, P](pType, modelType, persistenceConfig, polyRepoOpt, session)

  protected def openBaseConnectionBlocking(): Unit = synchronized {
    if (sessionOpt.nonEmpty) throw new ConnectionOpenException
    val client = new MongoClient(new MongoClientURI(mongoConfig.uri))
    val db = client.getDatabase(mongoConfig.db)
    sessionOpt = Some(MongoSession(client, db))
  }

  protected def createBaseSchemaBlocking(): Unit = ()

  protected def closeConnectionBlocking(): Unit = synchronized {
    if (sessionOpt.isEmpty) throw new ConnectionClosedException
    sessionOpt.get.client.close()
    sessionOpt = None
  }

}
