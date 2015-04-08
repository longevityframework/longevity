package emblem

// TODO pt-86950360: put in more of the map api

/** an abstract base class for shared functionality of [[TypeKeyMap]] and [[TypeBoundMap]] */
private[emblem] abstract class BaseTypeBoundMap[
  TypeBound,
  Key[_ <: TypeBound],
  Val[_ <: TypeBound]] (
  protected val underlying: Map[Any, Any]) {

  /** tests whether this map contains a binding for a key.
   * @param key the key
   * @return `true` if there is a binding for key in this map, `false` otherwise.
   */
  def contains(key: Key[_ <: TypeBound]) = underlying.contains(key)

  /** applies a function `f` to all elements of this type bound map.
   *
   * @param f the function that is applied for its side-effect to every element. the result of function `f` is
   * discarded
   * 
   * @usecase def foreach(f: TypeBoundPair[TypeBound, Key, Val, _ <: TypeBound] => Unit): Unit
   * @inheritdoc
   */
  def foreach[U](f: TypeBoundPair[TypeBound, Key, Val, _ <: TypeBound] => U): Unit = iterator.foreach(f)

  override def hashCode = underlying.hashCode

  /** tests whether the map is empty.
   * @return `true` if the map does not contain any key/value binding, `false` otherwise.
   */
  def isEmpty = underlying.isEmpty

  /** creates a new iterator over all key/value pairs of this map
   * @return the new iterator
   */
  def iterator: Iterator[TypeBoundPair[TypeBound, Key, Val, TypeParam]
                         forSome { type TypeParam <: TypeBound }] = {
    underlying.iterator.map { pair =>

      // this is a lie: TypeParam is not the same as TypeBound. but the TypeParam type will be discarded before
      // this loop iter completes, so nobody will ever know the difference.
      type TypeParam = TypeBound

      TypeBoundPair[TypeBound, Key, Val, TypeParam](
        pair._1.asInstanceOf[Key[TypeParam]],
        pair._2.asInstanceOf[Val[TypeParam]])
    }
  }

  /** collects all keys of this map in an iterable collection.
   * @return the keys of this map as an iterable.
   */
  def keys: Iterable[Key[TypeParam] forSome { type TypeParam <: TypeBound }] =
    underlying.keys.asInstanceOf[Iterable[Key[_ <: TypeBound]]]

  /** the number of key/value bindings in this map */
  def size = underlying.size

  /** collects all values of this map in an iterable collection.
   * @return the values of this map as an iterable.
   */
  def values: collection.Iterable[Val[TypeParam] forSome { type TypeParam <: TypeBound }] =
    underlying.values.asInstanceOf[Iterable[Val[TypeParam] forSome { type TypeParam <: TypeBound }]]

  protected def mapValuesUnderlying[
    TypeBound2 >: TypeBound,
    Val2[_ <: TypeBound2]](
    f: WideningTypeBoundFunction[TypeBound, TypeBound2, Val, Val2])
  : Map[Any, Any] = {
    def mapValue[TypeParam <: TypeBound](value1: Val[TypeParam]): Val2[TypeParam] = {
      f.apply[TypeParam](value1)
    }
    underlying.mapValues { value1 =>
      mapValue(value1.asInstanceOf[Val[_ <: TypeBound]])
    }
  }

}

