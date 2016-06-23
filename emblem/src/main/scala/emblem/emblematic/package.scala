package emblem

/** a collection of tools for describing collections of related data types in a
 * generic and traversable way
 */
package object emblematic {

  /** a [[TypeKeyMap]] of [[Emblem emblems]] */
  type EmblemPool = TypeKeyMap[Any, Emblem]

  /** a no-arg function with return type `A` */
  type Function0[A] = () => A

  /** A [[TypeKeyMap]] of `Any` to [[Union]] */
  type UnionPool = TypeKeyMap[Any, Union]

}
