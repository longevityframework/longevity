package longevity.persistence.mongo

import com.mongodb.MongoWriteException
import longevity.effect.Effect.Syntax
import longevity.persistence.PState
import org.bson.BsonDocument
import org.bson.types.ObjectId
import org.joda.time.DateTime

/** implementation of MongoPRepo.create */
private[mongo] trait MongoCreate[F[_], M, P] {
  repo: MongoPRepo[F, M, P] =>

  def create(p: P) = {
    val fp = effect.pure(p)
    val fsd = fp.map { p =>
      logger.debug(s"executing MongoPRepo.create: $p")
      val id = if (hasPrimaryKey) None else Some(MongoId(new ObjectId()))
      val rowVersion = if (persistenceConfig.optimisticLocking) Some(0L) else None
      val createdTimestamp = if (persistenceConfig.writeTimestamps) Some(DateTime.now) else None
      val s = PState(id, rowVersion, createdTimestamp, createdTimestamp, p)
      val d = bsonForState(s)
      logger.debug(s"executing MongoCollection.insertOne: $d")
      (s, d)
    }
    val fs = fsd.mapBlocking { case (s, d) => createStateBlocking(s, d) }
    fs.map { s =>
      logger.debug(s"done executing MongoPRepo.create: $s")
      s
    }
  }

  private[persistence] def createState(state: PState[P]): F[PState[P]] =
    effect.pure(state).mapBlocking { s => createStateBlocking(s, bsonForState(s)) }

  private def createStateBlocking(s: PState[P], d: BsonDocument): PState[P] = {
    try {
      mongoCollection.insertOne(d)
    } catch {
      case e: MongoWriteException => throwDuplicateKeyValException(s.get, e)
    }
    s
  }

}
