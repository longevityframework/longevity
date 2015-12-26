package longevity.persistence

import emblem.imports._
import longevity.subdomain.Root
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** a collection of repositories */
class RepoPool (private[longevity] val baseRepoMap: TypeKeyMap[Root, BaseRepo]) {

  /** a `TypeKeyMap` of [[longevity.subdomain.Root Root]] to [[Repo]] */
  val typeKeyMap: TypeKeyMap[Root, Repo] = baseRepoMap.widen

  /** select a repository by root type */
  def apply[R <: Root : TypeKey]: Repo[R] = typeKeyMap[R]

  /** iterate over the repositories */
  def values: collection.Iterable[Repo[_ <: Root]] = typeKeyMap.values

  private type KeyedRoot = RootWithTypeKey[_ <: Root]
  private type PStateSeq = Seq[PState[_ <: Root]]
  private case class CreateManyState(cache: CreatedCache, pstates: PStateSeq)

  /** creates many aggregates at once. this method is the only way to persist aggregates with
   * embedded [[UnpersistedAssoc]]. any embedded [[UnpersistedAssoc]] must be present in the
   * argument list `keyedRoots`. because [RootWithTypeKey] is an implicit class, you can call
   * this method using just aggregate roots, and the roots will be converted to `RootWithTypeKey`
   * implicitly:
   *
   * {{{
   * repoPool.createMany(user1, user2, user2, blogPost1, blogPost2, blog)
   * }}}
   *
   * @param keyedRoots the roots of the aggregates to persist, wrapped with their `TypeKeys`.
   * 
   * @see [Assoc.apply]
   */
  def createMany(keyedRoots: RootWithTypeKey[_ <: Root]*): Future[Seq[PState[_ <: Root]]] = {
    val empty = Future.successful(CreateManyState(CreatedCache(), Seq[PState[_ <: Root]]()))
    val foldResult = keyedRoots.foldLeft(empty)(createOne _)
    foldResult.map(_.pstates)
  }

  private def createOne(acc: Future[CreateManyState], keyedRoot: KeyedRoot): Future[CreateManyState] = {

    def create[R <: Root](
      keyedRoot: RootWithTypeKey[R],
      cache: CreatedCache)
    : Future[(PState[R], CreatedCache)] = {
      baseRepoMap(keyedRoot.rootTypeKey).createWithCache(keyedRoot.root, cache)
    }

    acc.flatMap { state =>
      create(keyedRoot, state.cache).map {
        case (pstate, cache) => CreateManyState(cache, state.pstates :+ pstate)
      }
    }

  }

}
