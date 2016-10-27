package longevity.persistence.mongo

import com.mongodb.client.model.Filters
import emblem.TypeKey
import longevity.persistence.PState
import longevity.subdomain.KeyVal
import longevity.subdomain.Persistent
import longevity.subdomain.query.QueryFilter
import org.bson.BsonDocument
import org.bson.BsonString
import org.bson.conversions.Bson

private[mongo] trait DerivedMongoRepo[P <: Persistent, Poly >: P <: Persistent] extends MongoRepo[P] {

  protected val polyRepo: MongoRepo[Poly]

  override protected[mongo] lazy val mongoCollection = polyRepo.mongoCollection

  override protected def createIndex(
    paths: Seq[String],
    indexName: String,
    unique: Boolean,
    hashed: Boolean = false): Unit =
    super.createIndex("discriminator" +: paths, indexName, unique, hashed)

  override protected def translate(p: P): BsonDocument = {
    // we use the poly type key here so we get the discriminator in the BSON
    subdomainToBsonTranslator.translate[Poly](p, false)(polyRepo.pTypeKey).asDocument
  }

  override protected def keyValQuery[V <: KeyVal[P, V] : TypeKey](keyVal: V): BsonDocument = {
    super.keyValQuery(keyVal).append("_discriminator", discriminatorValue)
  }

  override protected def mongoFilter(query: QueryFilter[P]): Bson = {
    Filters.and(
      super.mongoFilter(query),
      Filters.eq("_discriminator", discriminatorValue))
  }

  override protected def deleteQuery(state: PState[P]): Bson = {
    Filters.and(
      super.deleteQuery(state),
      Filters.eq("_discriminator", discriminatorValue))
  }

  private def discriminatorValue = new BsonString(pTypeKey.name)

}
