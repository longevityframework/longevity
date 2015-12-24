package longevity.persistence

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import emblem.imports._
import longevity.exceptions.subdomain.AssocIsUnpersistedException
import longevity.subdomain._
import longevity.subdomain.root._
import longevity.context.LongevityContext

/** an in-memory repository for aggregate roots of type `R`
 * 
 * @param rootType the entity type for the aggregate roots this repository handles
 * @param subdomain the subdomain containing the root that this repo persists
 */
class InMemRepo[R <: Root : TypeKey] private[persistence] (
  rootType: RootType[R],
  subdomain: Subdomain)
extends Repo[R](rootType, subdomain) {
  repo =>

  private case class IntId(i: Int) extends PersistedAssoc[R] {
    val associateeTypeKey = repo.rootTypeKey
    private[longevity] val _lock = 0
    def retrieve = repo.retrieve(this).map(_.get)
  }

  private var nextId = 0
  private var idToEntityMap = Map[PersistedAssoc[R], PState[R]]()
  
  private var keyValToEntityMap = Map[KeyVal[R], PState[R]]()

  def create(unpersisted: R) = getSessionCreationOrElse(unpersisted, {
    patchUnpersistedAssocs(unpersisted).map { e =>
      val id = repo.synchronized {
        val id = IntId(nextId)
        nextId += 1
        id
      }
      persist(id, e)
    }
  })

  def retrieve(keyValForRoot: KeyVal[R]): Future[Option[PState[R]]] = {
    keyValForRoot.propVals.foreach { case (prop, value) =>
      if (prop.typeKey <:< typeKey[Assoc[_]]) {
        val assoc = value.asInstanceOf[Assoc[_ <: Root]]
        if (!assoc.isPersisted) throw new AssocIsUnpersistedException(assoc)
      }
    }
    val optionR = keyValToEntityMap.get(keyValForRoot)
    Future.successful(optionR)
  }


  def update(persisted: PState[R]) = {
    dumpKeys(persisted.orig)
    patchUnpersistedAssocs(persisted.get) map {
      persist(persisted.passoc, _)
    }
  }

  def delete(persisted: PState[R]) = {
    repo.synchronized { idToEntityMap -= persisted.passoc }
    dumpKeys(persisted.orig)
    val deleted = new Deleted(persisted.get)
    Future.successful(deleted)
  }

  protected def retrieveByValidatedQuery(query: ValidatedQuery[R]): Future[Seq[PState[R]]] = Future {
    idToEntityMap.values.view.toSeq.filter { pstate => queryMatches(query, pstate.get) }
  }

  private def queryMatches(query: ValidatedQuery[R], root: R): Boolean = {
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

  private def retrieve(assoc: PersistedAssoc[R]) = {
    val optionR = idToEntityMap.get(assoc)
    Future.successful(optionR)
  }

  private def persist(assoc: PersistedAssoc[R], root: R): PState[R] = {
    val persisted = new PState[R](assoc, root)
    repo.synchronized {
      idToEntityMap += (assoc -> persisted)
      rootType.keys.foreach { key =>
        val keyValForRoot = key.keyValForRoot(root)
        keyValToEntityMap += keyValForRoot -> persisted
      }
    }
    persisted
  }

  private def dumpKeys(root: R) = repo.synchronized {
    rootType.keys.foreach { key =>
      val keyValForRoot = key.keyValForRoot(root)
      keyValToEntityMap -= keyValForRoot
    }
  }

}
