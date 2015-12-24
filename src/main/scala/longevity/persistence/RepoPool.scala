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

  // TODO scaladoc
  def createMany(keyedRoots: RootWithTypeKey[_ <: Root]*): Future[Seq[PState[_ <: Root]]] = {

    def create[R <: Root](keyedRoot: RootWithTypeKey[R]): Future[PState[R]] =
      apply(keyedRoot.typeKey).create(keyedRoot.root)

    def create0(keyedRoot: RootWithTypeKey[_ <: Root]) = create(keyedRoot)

    val many: Seq[Future[PState[_ <: Root]]] = keyedRoots.map(create0 _)

    Future.sequence(many)
  }

}
