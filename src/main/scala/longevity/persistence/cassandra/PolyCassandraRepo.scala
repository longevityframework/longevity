package longevity.persistence.cassandra

import longevity.subdomain.persistent.Persistent

private[cassandra] trait PolyCassandraRepo[P <: Persistent] extends CassandraRepo[P] {

}
