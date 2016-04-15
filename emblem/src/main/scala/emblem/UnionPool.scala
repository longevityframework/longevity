package emblem

import emblem.exceptions.DuplicateUnionsException

object UnionPool {

  /** collects a sequence of [[Union unions]] into an [[UnionPool]].
   * 
   * @param unions the sequence of unions to store in the pool
   * @throws emblem.exceptions.DuplicateUnionsException when two or more of the
   * `Unions` have the same `HasEmblem` type
   */
  def apply(unions: Union[_]*): UnionPool = {
    val map: UnionPool = unions.foldLeft(TypeKeyMap[Any, Union]()) {
      case (map, union) => map + (union.typeKey -> union)
    }
    if (unions.size != map.size) throw new DuplicateUnionsException
    map
  }

  /** an empty union pool */
  val empty: UnionPool = TypeKeyMap[Any, Union]

}
