package longevity.persistence.inmem

import akka.NotUsed
import akka.stream.scaladsl.Source
import longevity.exceptions.persistence.DuplicateKeyValException
import longevity.persistence.BaseRepo
import longevity.persistence.Deleted
import longevity.persistence.PState
import longevity.persistence.DatabaseId
import longevity.subdomain.AnyKeyVal
import longevity.subdomain.KeyVal
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
import longevity.subdomain.realized.AnyRealizedKey
import longevity.subdomain.realized.RealizedPType
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** an in-memory repository for persistent entities of type `P`
 * 
 * @param pType the persistent type for the entities this repository handles
 * @param subdomain the subdomain containing the entities that this repo persists
 */
class InMemRepo[P <: Persistent] private[persistence] (
  pType: PType[P],
  subdomain: Subdomain)
extends BaseRepo[P](pType, subdomain) {
  repo =>

  private var idCounter = 0
  private var idToPStateMap = Map[DatabaseId[_ <: Persistent], PState[P]]()
  private var keyValToPStateMap = Map[Any, PState[P]]()

  def create(unpersisted: P)(implicit context: ExecutionContext) = Future {
    persist(IntId[P](nextId), unpersisted)
  }

  override def retrieve[V <: KeyVal[P, V]](keyVal: V)(implicit context: ExecutionContext) =
    Future.successful(lookupPStateByKeyVal(keyVal))

  def retrieveByQuery(query: Query[P])(implicit context: ExecutionContext)
  : Future[Seq[PState[P]]] =
    Future.successful(queryResults(query))

  def streamByQuery(query: Query[P]): Source[PState[P], NotUsed] =
    Source.fromIterator { () => queryResults(query).iterator }

  private def queryResults(query: Query[P]): Seq[PState[P]] =
    allPStates.filter { s => InMemRepo.queryMatches(query, s.get, realizedPType) }

  def update(state: PState[P])(implicit context: ExecutionContext) = Future {
    repo.synchronized {
      dumpKeys(state.orig)
    }
    try {
      persist(state.id, state.get)
    } catch {
      case e: DuplicateKeyValException[_] =>
        repo.synchronized {
          keys.foreach { key =>
            registerPStateByKeyVal(key.keyValForP(state.orig), state)
          }
          throw e
        }
    }
  }

  def delete(state: PState[P])(implicit context: ExecutionContext) = {
    repo.synchronized {
      unregisterPStateById(state)
      dumpKeys(state.orig)
    }
    val deleted = new Deleted(state.get)
    Future.successful(deleted)
  }

  private def dumpKeys(p: P) = keys.foreach { key =>
    unregisterKeyVal(key.keyValForP(p))
  }

  private def persist(id: DatabaseId[P], p: P): PState[P] = {
    val state = new PState[P](id, p)
    repo.synchronized {
      keys.foreach { key =>
        assertUniqueKeyVal(key.keyValForP(p), state)
      }
      registerPStateById(state)
      keys.foreach { key =>
        registerPStateByKeyVal(key.keyValForP(p), state)
      }
    }
    state
  }

  protected[inmem] def nextId: Int = repo.synchronized {
    val id = idCounter
    idCounter += 1
    id
  }

  protected[inmem] def keys: Seq[AnyRealizedKey[_ >: P <: Persistent]] = myKeys

  protected def myKeys: Seq[AnyRealizedKey[_ >: P <: Persistent]] = realizedPType.keySet.toSeq

  protected[inmem] def assertUniqueKeyVal(keyVal: AnyKeyVal[_ <: Persistent], state: PState[P]): Unit = {
    if (keyValToPStateMap.contains(keyVal)) {
      throw new DuplicateKeyValException[P](state.get, keyVal.key)
    }
  }

  protected[inmem] def allPStates: Seq[PState[P]] = idToPStateMap.values.view.toSeq

  protected[inmem] def registerPStateById(state: PState[P]): Unit =
    idToPStateMap += (state.id -> state)

  protected[inmem] def unregisterPStateById(state: PState[P]): Unit =
    idToPStateMap -= state.id

  protected[inmem] def registerPStateByKeyVal(keyVal: Any, state: PState[P]): Unit =
    keyValToPStateMap += keyVal -> state

  protected[inmem] def lookupPStateByKeyVal(keyVal: Any): Option[PState[P]] =
    keyValToPStateMap.get(keyVal)

  protected[inmem] def unregisterKeyVal(keyVal: Any): Unit = keyValToPStateMap -= keyVal

  override def toString = s"InMemRepo[${pTypeKey.name}]"

}

object InMemRepo {

  def apply[P <: Persistent](
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
