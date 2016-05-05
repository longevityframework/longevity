package longevity.persistence.cassandra

import longevity.subdomain.persistent.Persistent

private[cassandra] trait DerivedCassandraRepo[P <: Persistent] extends CassandraRepo[P] {

}
