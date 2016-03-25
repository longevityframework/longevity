package longevity.persistence

import emblem.imports._
import longevity.subdomain.persistent.Persistent
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** a collection of repositories */
class RepoPool (private[longevity] val baseRepoMap: TypeKeyMap[Persistent, BaseRepo]) {

  /** a `TypeKeyMap` of [[longevity.subdomain.Persistent]] to [[Repo]] */
  val typeKeyMap: TypeKeyMap[Persistent, Repo] = baseRepoMap.widen

  /** select a repository by the type of persistent entity */
  def apply[P <: Persistent : TypeKey]: Repo[P] = typeKeyMap[P]

  /** iterate over the repositories */
  def values: collection.Iterable[Repo[_ <: Persistent]] = typeKeyMap.values

  private type KeyedP = PWithTypeKey[_ <: Persistent]
  private type PStateSeq = Seq[PState[_ <: Persistent]]
  private case class CreateManyState(cache: CreatedCache, pstates: PStateSeq)

  /** creates many entities at once. this method is the only way to persist
   * aggregates with embedded unpersisted associations. any aggregates embedded
   * this way must be present in the argument list `keyedPs`.
   *
   * because [[PWithTypeKey]] is an implicit class, you can call this method
   * using just aggregate roots, and the roots will be converted to
   * `PWithTypeKey` implicitly:
   *
   * {{{
   * repoPool.createMany(user1, user2, user2, blogPost1, blogPost2, blog)
   * }}}
   *
   * @param keyedPs the persistent entities to persist, wrapped with their `TypeKeys`.
   *
   * @param executionContext the execution context
   * 
   * @see [Assoc.apply]
   */
  def createMany(
    keyedPs: PWithTypeKey[_ <: Persistent]*)(
    implicit executionContext: ExecutionContext)
  : Future[Seq[PState[_ <: Persistent]]] = {
    val empty = Future.successful(CreateManyState(CreatedCache(), Seq[PState[_ <: Persistent]]()))
    val foldResult = keyedPs.foldLeft(empty)(createOne _)
    foldResult.map(_.pstates)
  }

  private def createOne(
    acc: Future[CreateManyState],
    keyedP: KeyedP)(
    implicit context: ExecutionContext)
  : Future[CreateManyState] = {

    def create[P <: Persistent](
      keyedP: PWithTypeKey[P],
      cache: CreatedCache)
    : Future[(PState[P], CreatedCache)] = {
      baseRepoMap(keyedP.pTypeKey).createWithCache(keyedP.p, cache)
    }

    acc.flatMap { state =>
      create(keyedP, state.cache).map {
        case (pstate, cache) => CreateManyState(cache, state.pstates :+ pstate)
      }
    }

  }

}
