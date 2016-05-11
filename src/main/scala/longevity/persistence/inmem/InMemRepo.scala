package longevity.persistence.inmem

import emblem.TypeKey
import emblem.typeKey
import longevity.context.LongevityContext
import longevity.exceptions.persistence.AssocIsUnpersistedException
import longevity.persistence.BaseRepo
import longevity.persistence.Deleted
import longevity.persistence.PState
import longevity.persistence.PersistedAssoc
import longevity.subdomain.Assoc
import longevity.subdomain.Subdomain
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.ConditionalQuery
import longevity.subdomain.ptype.EqualityQuery
import longevity.subdomain.ptype.KeyVal
import longevity.subdomain.ptype.OrderingQuery
import longevity.subdomain.ptype.PType
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

  private var nextId = 0
  private var idToEntityMap = Map[PersistedAssoc[P], PState[P]]()
  
  private var keyValToEntityMap = Map[KeyVal[P], PState[P]]()

  def create(unpersisted: P)(implicit context: ExecutionContext) = Future {
    val id = repo.synchronized {
      val id = IntId[P](nextId)
      nextId += 1
      id
    }
    persist(id, unpersisted)
  }

  def retrieveByQuery(query: Query[P])(implicit context: ExecutionContext)
  : Future[Seq[PState[P]]] = Future {
    idToEntityMap.values.view.toSeq.filter { s => InMemRepo.queryMatches(query, s.get) }
  }

  def update(pState: PState[P])(implicit context: ExecutionContext) = Future {
    dumpKeys(pState.orig)
    persist(pState.passoc, pState.get)
  }

  def delete(pState: PState[P])(implicit context: ExecutionContext) = {
    repo.synchronized { idToEntityMap -= pState.passoc }
    dumpKeys(pState.orig)
    val deleted = new Deleted(pState.get, pState.assoc)
    Future.successful(deleted)
  }

  override protected def retrieveByPersistedAssoc(
    assoc: PersistedAssoc[P])(
    implicit context: ExecutionContext)
  : Future[Option[PState[P]]] = {
    Future.successful(idToEntityMap.get(assoc))
  }

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
    val optionR = keyValToEntityMap.get(keyVal)
    Future.successful(optionR)
  }

  private def persist(assoc: PersistedAssoc[P], p: P): PState[P] = {
    val pState = new PState[P](assoc, p)
    repo.synchronized {
      idToEntityMap += (assoc -> pState)
      pType.keySet.foreach { key =>
        val keyVal = key.keyValForP(p)
        keyValToEntityMap += keyVal -> pState
      }
    }
    pState
  }

  private def dumpKeys(p: P) = repo.synchronized {
    pType.keySet.foreach { key =>
      val keyVal = key.keyValForP(p)
      keyValToEntityMap -= keyVal
    }
  }

}

object InMemRepo {

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
