package longevity.persistence.mongo

import com.mongodb.MongoWriteException
import longevity.persistence.PState
import org.bson.types.ObjectId
import org.joda.time.DateTime

/** implementation of MongoPRepo.create */
private[mongo] trait MongoCreate[F[_], M, P] {
  repo: MongoPRepo[F, M, P] =>

  def create(p: P) = {
    val fp = effect.pure(p)
    val fpsd = effect.map(fp) { p =>
      logger.debug(s"executing MongoPRepo.create: $p")
      val id = if (hasPrimaryKey) None else Some(MongoId[P](new ObjectId()))
      val rowVersion = if (persistenceConfig.optimisticLocking) Some(0L) else None
      val createdTimestamp = if (persistenceConfig.writeTimestamps) Some(DateTime.now) else None
      val s = PState(id, rowVersion, createdTimestamp, createdTimestamp, p)
      val d = bsonForState(s)
      logger.debug(s"executing MongoCollection.insertOne: $d")
      (p, s, d)
    }
    val fs = effect.mapBlocking(fpsd) { case (p, s, d) =>
      try {
        mongoCollection.insertOne(d)
      } catch {
        case e: MongoWriteException => throwDuplicateKeyValException(p, e)
      }
      s
    }
    effect.map(fs) { s =>
      logger.debug(s"done executing MongoPRepo.create: $s")
      s
    }
  }

}
