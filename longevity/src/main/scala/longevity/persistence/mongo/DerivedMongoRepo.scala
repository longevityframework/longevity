package longevity.persistence.mongo

import com.mongodb.casbah.commons.Implicits.wrapDBObj
import com.mongodb.casbah.commons.MongoDBObject
import longevity.persistence.PState
import longevity.subdomain.KeyVal
import longevity.subdomain.Persistent
import longevity.subdomain.ptype.QueryFilter

private[mongo] trait DerivedMongoRepo[P <: Persistent, Poly >: P <: Persistent] extends MongoRepo[P] {

  protected val polyRepo: MongoRepo[Poly]

  override protected[mongo] lazy val mongoCollection = polyRepo.mongoCollection

  override protected def createIndex(paths: Seq[String], indexName: String, unique: Boolean): Unit =
    super.createIndex("discriminator" +: paths, indexName, unique)

  override protected def translate(p: P): MongoDBObject = {
    // we use the poly type key here so we get the discriminator in the casbah
    anyToMongoDBObject(persistentToCasbahTranslator.translate[Poly](p, false)(polyRepo.pTypeKey))
  }

  override protected def keyValQuery[V <: KeyVal[P, V]](keyVal: V): MongoDBObject = {
    super.keyValQuery(keyVal) ++ MongoDBObject("_discriminator" -> discriminatorValue)
  }

  override protected def mongoQuery(query: QueryFilter[P]): MongoDBObject = {
    super.mongoQuery(query) ++ MongoDBObject("_discriminator" -> discriminatorValue)
  }

  override protected def deleteQuery(state: PState[P]): MongoDBObject = {
    super.deleteQuery(state) ++ MongoDBObject("_discriminator" -> discriminatorValue)
  }

  private def discriminatorValue = pTypeKey.name

}
