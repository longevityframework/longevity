package longevity.persistence.mongo

import longevity.persistence.BasePolyRepo

private[mongo] trait PolyMongoPRepo[M, P] extends MongoPRepo[M, P] with BasePolyRepo[M, P] {

  override protected[persistence] def createSchemaBlocking(): Unit = {
    super.createSchemaBlocking()

    // i could add an index on discriminator here. it would only be used to
    // support derived queries. im choosing not to, since populating the index
    // will increase write time, and queries on derived types seems pretty
    // special case. such queries would probably be using derived keys and
    // indexes, which are already prefixed with discriminator

    //createIndex(Seq("discriminator"), false)
  }

}
