package longevity.persistence

import emblem.imports._
import longevity.exceptions.persistence.AssocIsUnpersistedException
import longevity.subdomain._
import longevity.subdomain.root._
import longevity.context.LongevityContext
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

/** an in-memory repository for aggregate roots of type `R`
 * 
 * @param rootType the entity type for the aggregate roots this repository handles
 * @param subdomain the subdomain containing the root that this repo persists
 */
class InMemRepo[R <: Root : TypeKey] private[persistence] (
  rootType: RootType[R],
  subdomain: Subdomain)
extends BaseRepo[R](rootType, subdomain) {
  repo =>

  private case class IntId(i: Int) extends PersistedAssoc[R] {
    private[longevity] val _lock = 0
  }

  private var nextId = 0
  private var idToEntityMap = Map[PersistedAssoc[R], PState[R]]()
  
  private var keyValToEntityMap = Map[KeyVal[R], PState[R]]()

  def create(unpersisted: R) = Future {
    val id = repo.synchronized {
      val id = IntId(nextId)
      nextId += 1
      id
    }
    persist(id, unpersisted)
  }

  def update(persisted: PState[R]) = Future {
    dumpKeys(persisted.orig)
    persist(persisted.passoc, persisted.get)
  }

  def delete(persisted: PState[R]) = {
    repo.synchronized { idToEntityMap -= persisted.passoc }
    dumpKeys(persisted.orig)
    val deleted = new Deleted(persisted.get, persisted.assoc)
    Future.successful(deleted)
  }

  override protected def retrieveByPersistedAssoc(assoc: PersistedAssoc[R])
  : Future[Option[PState[R]]] = {
    Future.successful(idToEntityMap.get(assoc))
  }

  override protected def retrieveByKeyVal(keyVal: KeyVal[R]): Future[Option[PState[R]]] = {
    keyVal.propVals.foreach { case (prop, value) =>
      if (prop.typeKey <:< typeKey[Assoc[_]]) {
        val assoc = value.asInstanceOf[Assoc[_ <: Root]]
        if (!assoc.isPersisted) throw new AssocIsUnpersistedException(assoc)
      }
    }
    val optionR = keyValToEntityMap.get(keyVal)
    Future.successful(optionR)
  }

  protected def retrieveByValidatedQuery(query: ValidatedQuery[R]): Future[Seq[PState[R]]] = Future {
    idToEntityMap.values.view.toSeq.filter { s => InMemRepo.queryMatches(query, s.get) }
  }

  private def persist(assoc: PersistedAssoc[R], root: R): PState[R] = {
    val persisted = new PState[R](assoc, root)
    repo.synchronized {
      idToEntityMap += (assoc -> persisted)
      rootType.keySet.foreach { key =>
        val keyVal = key.keyValForRoot(root)
        keyValToEntityMap += keyVal -> persisted
      }
    }
    persisted
  }

  private def dumpKeys(root: R) = repo.synchronized {
    rootType.keySet.foreach { key =>
      val keyVal = key.keyValForRoot(root)
      keyValToEntityMap -= keyVal
    }
  }

}

object InMemRepo {

  private[longevity] def queryMatches[R <: Root](query: ValidatedQuery[R], root: R): Boolean = {
    import Query._
    query match {
      case VEqualityQuery(prop, op, value) => op match {
        case EqOp => prop.propVal(root) == value
        case NeqOp => prop.propVal(root) != value
      }
      case VOrderingQuery(prop, op, value) => op match {
        case LtOp => prop.ordering.lt(prop.propVal(root), value)
        case LteOp => prop.ordering.lteq(prop.propVal(root), value)
        case GtOp => prop.ordering.gt(prop.propVal(root), value)
        case GteOp => prop.ordering.gteq(prop.propVal(root), value)
      }
      case VConditionalQuery(lhs, op, rhs) => op match {
        case AndOp => queryMatches(lhs, root) && queryMatches(rhs, root)
        case OrOp => queryMatches(lhs, root) || queryMatches(rhs, root)
      }
    }
  }

}
