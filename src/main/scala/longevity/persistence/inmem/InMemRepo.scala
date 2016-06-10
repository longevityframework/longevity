package longevity.persistence.inmem

import akka.NotUsed
import akka.stream.scaladsl.Source
import emblem.typeKey
import longevity.exceptions.persistence.AssocIsUnpersistedException
import longevity.exceptions.persistence.DuplicateKeyValException
import longevity.persistence.BaseRepo
import longevity.persistence.Deleted
import longevity.persistence.PState
import longevity.persistence.PersistedAssoc
import longevity.subdomain.Assoc
import longevity.subdomain.Subdomain
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.ConditionalQuery
import longevity.subdomain.ptype.EqualityQuery
import longevity.subdomain.ptype.Key
import longevity.subdomain.ptype.KeyVal
import longevity.subdomain.ptype.OrderingQuery
import longevity.subdomain.ptype.PType
import longevity.subdomain.ptype.DerivedPType
import longevity.subdomain.ptype.PolyPType
import longevity.subdomain.ptype.Query
import longevity.subdomain.ptype.Query.AndOp
import longevity.subdomain.ptype.Query.EqOp
import longevity.subdomain.ptype.Query.GtOp
import longevity.subdomain.ptype.Query.GteOp
import longevity.subdomain.ptype.Query.LtOp
import longevity.subdomain.ptype.Query.LteOp
import longevity.subdomain.ptype.Query.NeqOp
import longevity.subdomain.ptype.Query.OrOp
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
  private var assocToPStateMap = Map[PersistedAssoc[_ <: Persistent], PState[P]]()
  private var keyValToPStateMap = Map[KeyVal[_ <: Persistent], PState[P]]()

  def create(unpersisted: P)(implicit context: ExecutionContext) = Future {
    persist(IntId[P](nextId), unpersisted)
  }

  def retrieveByQuery(query: Query[P])(implicit context: ExecutionContext)
  : Future[Seq[PState[P]]] =
    Future.successful(queryResults(query))

  def streamByQuery(query: Query[P]): Source[PState[P], NotUsed] =
    Source.fromIterator { () => queryResults(query).iterator }

  private def queryResults(query: Query[P]): Seq[PState[P]] =
    allPStates.filter { s => InMemRepo.queryMatches(query, s.get) }

  def update(state: PState[P])(implicit context: ExecutionContext) = Future {
    repo.synchronized {
      dumpKeys(state.orig)
    }
    try {
      persist(state.passoc, state.get)
    } catch {
      case e: DuplicateKeyValException[_] =>
        keys.foreach { key =>
          val keyVal = key.keyValForP(state.orig)
          keyValToPStateMap += keyVal -> state
        }
        throw e
    }
  }

  def delete(state: PState[P])(implicit context: ExecutionContext) = {
    repo.synchronized {
      unregisterPStateByAssoc(state.passoc)
      dumpKeys(state.orig)
    }
    val deleted = new Deleted(state.get, state.assoc)
    Future.successful(deleted)
  }

  private def dumpKeys(p: P) = keys.foreach { key =>
    val keyVal = key.keyValForP(p)
    keyValToPStateMap -= keyVal
  }

  override protected def retrieveByPersistedAssoc(
    assoc: PersistedAssoc[P])(
    implicit context: ExecutionContext)
  : Future[Option[PState[P]]] =
    Future.successful(lookupPStateByAssoc(assoc))

  override protected def retrieveByKeyVal(
    keyVal: KeyVal[P])(
    implicit context: ExecutionContext)
  : Future[Option[PState[P]]] = {
    keyVal.propVals.foreach { case (prop, value) =>
      if (prop.propTypeKey <:< typeKey[Assoc[_]]) {
        val assoc = value.asInstanceOf[Assoc[_ <: Persistent]]
        if (!assoc.isPersisted) throw new AssocIsUnpersistedException(assoc)
      }
    }
    Future.successful(lookupPStateByKeyVal(keyVal))
  }

  private def persist(assoc: PersistedAssoc[P], p: P): PState[P] = {
    val state = new PState[P](assoc, p)
    repo.synchronized {
      keys.foreach { key =>
        assertUniqueKeyVal(key.keyValForP(p), state)
      }
      registerPStateByAssoc(assoc, state)
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

  protected[inmem] val keys: Seq[Key[_ >: P <: Persistent]] = pType.keySet.toSeq

  protected[inmem] def assertUniqueKeyVal(keyVal: KeyVal[_ <: Persistent], state: PState[P]): Unit = {
    if (keyValToPStateMap.contains(keyVal)) {
      throw new DuplicateKeyValException[P](state.get, keyVal.key.asInstanceOf[Key[P]])
    }
  }

  protected[inmem] def registerPStateByAssoc(assoc: PersistedAssoc[_ <: Persistent], state: PState[P]): Unit = {
    assocToPStateMap += (assoc -> state)
  }

  protected[inmem] def registerPStateByKeyVal(keyVal: KeyVal[_ <: Persistent], state: PState[P]): Unit = {
    keyValToPStateMap += (keyVal -> state)
  }

  protected[inmem] def lookupPStateByAssoc(assoc: PersistedAssoc[_ <: Persistent]): Option[PState[P]] = {
    assocToPStateMap.get(assoc)
  }

  protected[inmem] def lookupPStateByKeyVal(keyVal: KeyVal[_ <: Persistent]): Option[PState[P]] =
    keyValToPStateMap.get(keyVal)

  protected[inmem] def allPStates: Seq[PState[P]] = assocToPStateMap.values.view.toSeq

  protected[inmem] def unregisterPStateByAssoc(assoc: PersistedAssoc[_ <: Persistent]): Unit =
    assocToPStateMap -= assoc

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

  private[longevity] def queryMatches[P <: Persistent](query: Query[P], p: P): Boolean = {
    query match {
      case EqualityQuery(prop, op, value) => op match {
        case EqOp => prop.propVal(p) == value
        case NeqOp => prop.propVal(p) != value
      }
      case OrderingQuery(prop, op, value) => op match {
        case LtOp => prop.ordering.lt(prop.propVal(p), value)
        case LteOp => prop.ordering.lteq(prop.propVal(p), value)
        case GtOp => prop.ordering.gt(prop.propVal(p), value)
        case GteOp => prop.ordering.gteq(prop.propVal(p), value)
      }
      case ConditionalQuery(lhs, op, rhs) => op match {
        case AndOp => queryMatches(lhs, p) && queryMatches(rhs, p)
        case OrOp => queryMatches(lhs, p) || queryMatches(rhs, p)
      }
    }
  }

}
