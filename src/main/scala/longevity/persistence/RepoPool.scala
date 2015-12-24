package longevity.persistence

import emblem.imports._
import longevity.subdomain.Root

/** a collection of repositories
 * 
 * @param typeKeyMap a `TypeKeyMap` of [[longevity.subdomain.Root Root]] to [[Repo]]
 */
class RepoPool (val typeKeyMap: TypeKeyMap[Root, Repo]) {

  /** select a repository by root type */
  def apply[R <: Root : TypeKey]: Repo[R] = typeKeyMap[R]

  /** iterate over the repositories */
  def values: collection.Iterable[Repo[_ <: Root]] = typeKeyMap.values

}
