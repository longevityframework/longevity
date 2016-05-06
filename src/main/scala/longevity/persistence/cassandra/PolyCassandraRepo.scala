package longevity.persistence.cassandra

import emblem.TypeKey
import emblem.Union
import longevity.persistence.PState
import longevity.subdomain.persistent.Persistent
import scala.concurrent.ExecutionContext

private[cassandra] trait PolyCassandraRepo[P <: Persistent] extends CassandraRepo[P] {

  private val union: Union[P] = subdomain.emblematic.unions(pTypeKey)

  // Repo.createSchema overrides:

  override protected def createTable(): Unit = {
    super.createTable()
    addColumn("discriminator", "text")
  }

  // Repo.create overrides:

  override def create(p: P)(implicit context: ExecutionContext) = {
    def createDerived[D <: P : TypeKey] = repoPool[D].create(p.asInstanceOf[D])(context)
    implicit val derivedTypeKey: TypeKey[_ <: P] = union.typeKeyForInstance(p).getOrElse {
      throw new RuntimeException // TODO: exception type for attempting to create a non-derived poly
    }
    createDerived.map(widenPState)
  }

  private def widenPState(pstate: PState[_ <: P]): PState[P] = {
    val passoc = CassandraId[P](pstate.passoc.asInstanceOf[CassandraId[_]].uuid)
    new PState[P](passoc, pstate.orig, pstate.get)
  }

  // Repo.retrieveByPersistedAssoc overrides:

  // TODO

  // Repo.retrieveByKeyVal overrides:

  // TODO

  // Repo.retrieveByQuery overrides:

  // TODO

  // Repo.update overrides:

  // TODO

  // Repo.delete overrides:

  // TODO

}

/*
* - schema
*   - poly:
*     - constructs the table, plus support for poly keys and indexes
*     - same as super, but adds discrimintator column
*   - derived:
*     - constructs support for poly derived and indexes
* - create:
*   - poly:
*     - identifies the derived type, delegates to the derived repo
*   - derived:
*     - same as super, but adds discriminator values
*     - also, sets any realized props from both poly and derived
* - retrieve Assoc
*   - poly:
*     - same as super
*   - derived:
*     - same as super, but optionally filters on the discriminator as well
* - retrieve KeyVal
*   - poly:
*     - same as super
*   - derived:
*     - same as super, but filters on the discriminator as well
* - retrieve Query
*   - poly:
*     - same as super
*   - derived:
*     - same as super, but filters on the discriminator as well
* - update
*   - poly:
*     - identifies the derived type, delegates to the derived repo
*   - derived:
*     - same as super, but adds discriminator values
*     - also, sets any realized props from both poly and derived
* - delete
*   - poly:
*     - same as super
*   - derived:
*     - same as super, but optionally filters on the discriminator as well
*
* in those "optionally filters on the discriminator" cases, i say optional because
* there is (presently) no chance that the assoc will match a non-intended persistent.
* but this may change once we get to partitionKey user story
*/
