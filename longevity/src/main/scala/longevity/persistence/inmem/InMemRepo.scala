package longevity.persistence.inmem

import com.typesafe.scalalogging.LazyLogging
import longevity.context.PersistenceConfig
import longevity.persistence.BaseRepo
import longevity.persistence.PState
import longevity.persistence.DatabaseId
import longevity.subdomain.AnyKeyVal
import longevity.subdomain.Subdomain
import longevity.subdomain.Persistent
import longevity.subdomain.ptype.ConditionalFilter
import longevity.subdomain.ptype.RelationalFilter
import longevity.subdomain.PType
import longevity.subdomain.DerivedPType
import longevity.subdomain.PolyPType
import longevity.subdomain.ptype.QueryFilter
import longevity.subdomain.ptype.QueryFilter.All
import longevity.subdomain.ptype.QueryFilter.AndOp
import longevity.subdomain.ptype.QueryFilter.EqOp
import longevity.subdomain.ptype.QueryFilter.GtOp
import longevity.subdomain.ptype.QueryFilter.GteOp
import longevity.subdomain.ptype.QueryFilter.LtOp
import longevity.subdomain.ptype.QueryFilter.LteOp
import longevity.subdomain.ptype.QueryFilter.NeqOp
import longevity.subdomain.ptype.QueryFilter.OrOp
import longevity.subdomain.ptype.Prop
import longevity.subdomain.realized.RealizedPType
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** an in-memory repository for persistent entities of type `P`
 * 
 * @param pType the persistent type for the entities this repository handles
 * @param subdomain the subdomain containing the entities that this repo persists
 * @param persistenceConfig persistence configuration that is back end agnostic
 */
private[longevity] class InMemRepo[P <: Persistent] private[persistence] (
  pType: PType[P],
  subdomain: Subdomain,
  protected val persistenceConfig: PersistenceConfig)
extends BaseRepo[P](pType, subdomain)
with InMemCreate[P]
with InMemDelete[P]
with InMemQuery[P]
with InMemRead[P]
with InMemRetrieve[P]
with InMemUpdate[P]
with InMemWrite[P]
with LazyLogging {
  repo =>

  // i wish i could type this tighter, but compiler is giving me problems..
  protected type AnyKeyValAtAll = AnyKeyVal[P] forSome { type P <: Persistent }

  protected var idToPStateMap = Map[DatabaseId[_ <: Persistent], PState[P]]()
  protected var keyValToPStateMap = Map[AnyKeyValAtAll, PState[P]]()

 protected[persistence] def close()(implicit context: ExecutionContext): Future[Unit] =
    Future.successful(())

  protected[persistence] def createSchema()(implicit context: ExecutionContext): Future[Unit] =
    Future.successful(())

  override def toString = s"InMemRepo[${pTypeKey.name}]"

}

private[longevity] object InMemRepo {

  private[persistence] def apply[P <: Persistent](
    pType: PType[P],
    subdomain: Subdomain,
    persistenceConfig: PersistenceConfig,
    polyRepoOpt: Option[InMemRepo[_ >: P <: Persistent]])
  : InMemRepo[P] = {
    val repo = pType match {
      case pt: PolyPType[_] =>
        new InMemRepo(pType, subdomain, persistenceConfig) with PolyInMemRepo[P]
      case pt: DerivedPType[_, _] =>
        def withPoly[Poly >: P <: Persistent](poly: InMemRepo[Poly]) = {
          class DerivedRepo extends {
            override protected val polyRepo: InMemRepo[Poly] = poly
          }
          with InMemRepo(pType, subdomain, persistenceConfig) with DerivedInMemRepo[P, Poly]
          new DerivedRepo
        }
        withPoly(polyRepoOpt.get)
      case _ =>
        new InMemRepo(pType, subdomain, persistenceConfig)
    }
    repo
  }

  private[longevity] def queryFilterMatches[P <: Persistent](
    filter: QueryFilter[P],
    p: P,
    realizedPType: RealizedPType[P])
  : Boolean = {

    def toRealized[A](prop: Prop[_ >: P <: Persistent, A]) = realizedPType.realizedProps(prop)

    def relationalQueryMatches[A](filter: RelationalFilter[_ >: P <: Persistent, A]) = {
      val realizedProp = toRealized(filter.prop)
      filter.op match {
        case EqOp => realizedProp.propVal(p) == filter.value
        case NeqOp => realizedProp.propVal(p) != filter.value
        case LtOp => realizedProp.ordering.lt(realizedProp.propVal(p), filter.value)
        case LteOp => realizedProp.ordering.lteq(realizedProp.propVal(p), filter.value)
        case GtOp => realizedProp.ordering.gt(realizedProp.propVal(p), filter.value)
        case GteOp => realizedProp.ordering.gteq(realizedProp.propVal(p), filter.value)
      }
    }

    filter match {
      case All() => true
      case q: RelationalFilter[_, _] => relationalQueryMatches(q)
      case ConditionalFilter(lhs, op, rhs) => op match {
        case AndOp => queryFilterMatches(lhs, p, realizedPType) && queryFilterMatches(rhs, p, realizedPType)
        case OrOp => queryFilterMatches(lhs, p, realizedPType) || queryFilterMatches(rhs, p, realizedPType)
      }
    }
  }

}
