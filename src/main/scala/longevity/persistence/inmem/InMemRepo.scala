package longevity.persistence.inmem

import longevity.persistence.BaseRepo
import longevity.persistence.PState
import longevity.persistence.DatabaseId
import longevity.subdomain.Subdomain
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.ConditionalQuery
import longevity.subdomain.ptype.EqualityQuery
import longevity.subdomain.ptype.OrderingQuery
import longevity.subdomain.ptype.PType
import longevity.subdomain.ptype.DerivedPType
import longevity.subdomain.ptype.PolyPType
import longevity.subdomain.ptype.Query
import longevity.subdomain.ptype.Query.All
import longevity.subdomain.ptype.Query.AndOp
import longevity.subdomain.ptype.Query.EqOp
import longevity.subdomain.ptype.Query.GtOp
import longevity.subdomain.ptype.Query.GteOp
import longevity.subdomain.ptype.Query.LtOp
import longevity.subdomain.ptype.Query.LteOp
import longevity.subdomain.ptype.Query.NeqOp
import longevity.subdomain.ptype.Query.OrOp
import longevity.subdomain.ptype.Prop
import longevity.subdomain.realized.RealizedPType

/** an in-memory repository for persistent entities of type `P`
 * 
 * @param pType the persistent type for the entities this repository handles
 * @param subdomain the subdomain containing the entities that this repo persists
 */
private[longevity] class InMemRepo[P <: Persistent] private[persistence] (
  pType: PType[P],
  subdomain: Subdomain)
extends BaseRepo[P](pType, subdomain)
with InMemCreate[P]
with InMemDelete[P]
with InMemQuery[P]
with InMemRead[P]
with InMemRetrieve[P]
with InMemUpdate[P]
with InMemWrite[P] {
  repo =>

  protected var idToPStateMap = Map[DatabaseId[_ <: Persistent], PState[P]]()
  protected var keyValToPStateMap = Map[Any, PState[P]]()

  override def toString = s"InMemRepo[${pTypeKey.name}]"

}

private[longevity] object InMemRepo {

  private[persistence] def apply[P <: Persistent](
    pType: PType[P],
    subdomain: Subdomain,
    polyRepoOpt: Option[InMemRepo[_ >: P <: Persistent]])
  : InMemRepo[P] = {
    val repo = pType match {
      case pt: PolyPType[_] =>
        new InMemRepo(pType, subdomain) with PolyInMemRepo[P]
      case pt: DerivedPType[_, _] =>
        def withPoly[Poly >: P <: Persistent](poly: InMemRepo[Poly]) = {
          class DerivedRepo extends {
            override protected val polyRepo: InMemRepo[Poly] = poly
          }
          with InMemRepo(pType, subdomain) with DerivedInMemRepo[P, Poly]
          new DerivedRepo
        }
        withPoly(polyRepoOpt.get)
      case _ =>
        new InMemRepo(pType, subdomain)
    }
    repo
  }

  private[longevity] def queryMatches[P <: Persistent](
    query: Query[P],
    p: P,
    realizedPType: RealizedPType[P])
  : Boolean = {

    def toRealized[A](prop: Prop[_ >: P <: Persistent, A]) = realizedPType.realizedProps(prop)

    def orderingQueryMatches[A](query: OrderingQuery[_ >: P <: Persistent, A]) = {
      val realizedProp = toRealized(query.prop)
      query.op match {
        case LtOp => realizedProp.ordering.lt(realizedProp.propVal(p), query.value)
        case LteOp => realizedProp.ordering.lteq(realizedProp.propVal(p), query.value)
        case GtOp => realizedProp.ordering.gt(realizedProp.propVal(p), query.value)
        case GteOp => realizedProp.ordering.gteq(realizedProp.propVal(p), query.value)
      }
    }

    query match {
      case All() => true
      case EqualityQuery(prop, op, value) => op match {
        case EqOp => toRealized(prop).propVal(p) == value
        case NeqOp => toRealized(prop).propVal(p) != value
      }
      case q: OrderingQuery[_, _] => orderingQueryMatches(q)
      case ConditionalQuery(lhs, op, rhs) => op match {
        case AndOp => queryMatches(lhs, p, realizedPType) && queryMatches(rhs, p, realizedPType)
        case OrOp => queryMatches(lhs, p, realizedPType) || queryMatches(rhs, p, realizedPType)
      }
    }
  }

}
