package longevity.persistence.mongo

import longevity.persistence.BasePolyRepo
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

private[mongo] trait PolyMongoRepo[P] extends MongoRepo[P] with BasePolyRepo[P] {

  override protected[persistence] def createSchema()(implicit context: ExecutionContext): Future[Unit] = {
    super.createSchema()

    // i could add an index on discriminator here. it would only be used to
    // support derived queries. im choosing not to, since populating the index
    // will increase write time, and queries on derived types seems pretty
    // special case. such queries would probably be using derived keys and
    // indexes, which are already prefixed with discriminator

    //createIndex(Seq("discriminator"), false)
  }

}
