package longevity.persistence.cassandra

import longevity.persistence.BasePolyRepo
import longevity.persistence.PState
import longevity.subdomain.persistent.Persistent

private[cassandra] trait PolyCassandraRepo[P <: Persistent] extends CassandraRepo[P] with BasePolyRepo[P] {

  override protected def createTable(): Unit = {
    super.createTable()
    addColumn("discriminator", "text")
    createIndex(s"${tableName}_discriminator", "discriminator")
  }

}

// TODO: either put this on the wiki or just delete it
/*
* - schema
*   - poly:
*     - constructs the table, plus support for poly keys and indexes
*     - same as super, but adds discriminator column
*   - derived:
*     - constructs support for derived keys and indexes
*       - all indexes should have 'discriminator' as initial column. for cassandra this doesnt matter since
*         indexes are all single-column
*       - note for inmem, this means updating keyValToEntityMap with derived keys on create/update
* - create:
*   - poly:
*     - identifies the derived type, delegates to the derived repo
*   - derived:
*     - same as super, but adds discriminator values
*     - for cassandra, sets any realized props from both poly and derived
* - retrieve Assoc
*   - poly:
*     - same as super
*   - derived:
*     - same as super, but:
*       - uses the poly collection instead of its own collection
*       - optionally filters on the discriminator as well
* - retrieve KeyVal
*   - poly:
*     - same as super
*   - derived:
*     - same as super, but:
*       - uses the poly collection instead of its own collection
*       - filters on the discriminator as well
*         - this is not necessary for InMem since the key vals are only put in by the appropriate derived repo
* - retrieve Query
*   - poly:
*     - same as super
*   - derived:
*     - same as super, but:
*       - uses the poly collection instead of its own collection
*       - filters on the discriminator as well
* - update
*   - poly:
*     - identifies the derived type, delegates to the derived repo
*   - derived:
*     - same as super, but adds discriminator values
*     - for cassandra, sets any realized props from both poly and derived
* - delete
*   - poly:
*     - same as super
*   - derived:
*     - same as super, but:
*       - uses the poly collection instead of its own collection
*       - optionally filters on the discriminator as well
*         - NOTE in cassandra not able to do this due to "Non PRIMARY KEY discriminator found in where clause"
*
* in those "optionally filters on the discriminator" cases, i say optional because
* there is (presently) no chance that the assoc will match a non-intended persistent.
* but this may change once we get to partitionKey user story
*/
