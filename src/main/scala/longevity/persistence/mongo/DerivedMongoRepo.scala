package longevity.persistence.mongo

import com.mongodb.casbah.commons.Implicits.wrapDBObj
import com.mongodb.casbah.commons.MongoDBObject
import longevity.persistence.PState
import longevity.persistence.PersistedAssoc
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.KeyVal
import longevity.subdomain.ptype.Query

private[mongo] trait DerivedMongoRepo[P <: Persistent, Poly >: P <: Persistent] extends MongoRepo[P] {

  protected val polyRepo: MongoRepo[Poly]

  override protected[mongo] def collectionName = polyRepo.collectionName

  override protected def createIndex(paths: Seq[String], unique: Boolean): Unit =
    super.createIndex("discriminator" +: paths, unique)

  override protected def casbahForP(p: P): MongoDBObject = {
    // we use the poly type key here so we get the discriminator in the casbah
    persistentToCasbahTranslator.translate[Poly](p)(polyRepo.pTypeKey)
  }

  override protected def persistedAssocQuery(assoc: PersistedAssoc[P]): MongoDBObject = {
    super.persistedAssocQuery(assoc) ++ MongoDBObject("_discriminator" -> discriminatorValue)
  }

  override protected def keyValQuery(keyVal: KeyVal[P]): MongoDBObject = {
    super.keyValQuery(keyVal) ++ MongoDBObject("_discriminator" -> discriminatorValue)
  }

  override protected def mongoQuery(query: Query[P]): MongoDBObject = {
    super.mongoQuery(query) ++ MongoDBObject("_discriminator" -> discriminatorValue)
  }

  override protected def deleteQuery(state: PState[P]): MongoDBObject = {
    super.deleteQuery(state) ++ MongoDBObject("_discriminator" -> discriminatorValue)
  }

  private def discriminatorValue = pTypeKey.name

}
