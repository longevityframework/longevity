package longevity.persistence

import emblem.imports._
import longevity.subdomain.Root
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** a collection of repositories
 * 
 * @param typeKeyMap a `TypeKeyMap` of [[longevity.subdomain.Root Root]] to [[Repo]]
 */
class RepoPool (val typeKeyMap: TypeKeyMap[Root, Repo]) {

  /** select a repository by root type */
  def apply[R <: Root : TypeKey]: Repo[R] = typeKeyMap[R]

  /** iterate over the repositories */
  def values: collection.Iterable[Repo[_ <: Root]] = typeKeyMap.values

  private type KeyedRoot = RootWithTypeKey[_ <: Root]
  private type PStateSeq = Seq[PState[_ <: Root]]
  private case class CreateManyState(cache: CreatedCache, pstates: PStateSeq)

  // TODO scaladoc
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
      apply(keyedRoot.typeKey).createWithCache(keyedRoot.root, cache)
    }

    acc.flatMap { state =>
      create(keyedRoot, state.cache).map {
        case (pstate, cache) => CreateManyState(cache, state.pstates :+ pstate)
      }
    }

  }

}
