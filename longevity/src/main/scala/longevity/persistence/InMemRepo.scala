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
 * @param entityType the entity type for the aggregate roots this repository handles
 * @param subdomain the subdomain containing the root that this repo persists
 */
class InMemRepo[R <: RootEntity : TypeKey](
  entityType: RootEntityType[R],
  subdomain: Subdomain)
extends Repo[R](entityType, subdomain) {
  repo =>

  private case class IntId(i: Int) extends PersistedAssoc[R] {
    val associateeTypeKey = repo.entityTypeKey
    private[longevity] val _lock = 0
    def retrieve = repo.retrieve(this).map(_.get)
  }

  private var nextId = 0
  private var idToEntityMap = Map[PersistedAssoc[R], Persisted[R]]()
  
  case class NKV(val key: Key[R], val keyVal: Key[R]#Val)
  private var nkvToEntityMap = Map[NKV, Persisted[R]]()

  def create(unpersisted: Unpersisted[R]) = getSessionCreationOrElse(unpersisted, {
    patchUnpersistedAssocs(unpersisted.get).map { e =>
      val id = repo.synchronized {
        val id = IntId(nextId)
        nextId += 1
        id
      }
      persist(id, e)
    }
  })

  def retrieve(key: Key[R])(keyVal: key.Val): Future[Option[Persisted[R]]] = {
    key.props.foreach { prop =>
      if (prop.typeKey <:< typeKey[Assoc[_]]) {
        val assoc = keyVal(prop).asInstanceOf[Assoc[_ <: RootEntity]]
        if (!assoc.isPersisted) throw new AssocIsUnpersistedException(assoc)
      }
    }
    val optionR = nkvToEntityMap.get(NKV(key, keyVal))
    Future.successful(optionR)
  }


  def update(persisted: Persisted[R]) = {
    dumpKeys(persisted.orig)
    patchUnpersistedAssocs(persisted.get) map {
      persist(persisted.assoc, _)
    }
  }

  def delete(persisted: Persisted[R]) = {
    repo.synchronized { idToEntityMap -= persisted.assoc }
    dumpKeys(persisted.orig)
    val deleted = new Deleted(persisted)
    Future.successful(deleted)
  }

  protected def retrieveByValidatedQuery(query: ValidatedQuery[R]): Future[Seq[Persisted[R]]] = Future {
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

  private def persist(assoc: PersistedAssoc[R], root: R): Persisted[R] = {
    val persisted = new Persisted[R](assoc, root)
    repo.synchronized {
      idToEntityMap += (assoc -> persisted)
      entityType.keys.foreach { key =>
        val keyVal = key.keyVal(root)
        nkvToEntityMap += (NKV(key, keyVal) -> persisted)
      }
    }
    persisted
  }

  private def dumpKeys(root: R) = repo.synchronized {
    entityType.keys.foreach { key =>
      val keyVal = key.keyVal(root)
      nkvToEntityMap -= NKV(key, keyVal)
    }
  }

}
