package longevity.persistence.mongo

import com.mongodb.client.model.Filters
import longevity.persistence.PState
import longevity.model.query.QueryFilter
import longevity.model.ptype.Key
import org.bson.BsonDocument
import org.bson.BsonString
import org.bson.conversions.Bson

private[mongo] trait DerivedMongoPRepo[F[_], M, P, Poly >: P] extends MongoPRepo[F, M, P] {

  protected val polyRepo: MongoPRepo[F, M, Poly]

  override protected[mongo] lazy val mongoCollection = polyRepo.mongoCollection

  override protected def createIndex(
    paths: Seq[String],
    indexName: String,
    unique: Boolean,
    hashed: Boolean = false): Unit =
    super.createIndex("discriminator" +: paths, indexName, unique, hashed)

  override protected def translate(p: P): BsonDocument = {
    // we use the poly type key here so we get the discriminator in the BSON
    domainModelToBsonTranslator.translate[Poly](p, false)(polyRepo.pTypeKey).asDocument
  }

  override protected def keyValQuery[V : Key[M, P, ?]](keyVal: V): Bson = {
    Filters.and(
      super.keyValQuery(keyVal),
      Filters.eq("_discriminator", discriminatorValue))
  }

  override protected def mongoFilter(query: QueryFilter[P]): Bson = {
    Filters.and(
      super.mongoFilter(query),
      Filters.eq("_discriminator", discriminatorValue))
  }

  override protected def writeQuery(state: PState[P]): Bson = {
    Filters.and(
      super.writeQuery(state),
      Filters.eq("_discriminator", discriminatorValue))
  }

  private def discriminatorValue = new BsonString(pTypeKey.name)

}
