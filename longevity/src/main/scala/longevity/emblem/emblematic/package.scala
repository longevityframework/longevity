package longevity.emblem

import typekey.TypeKeyMap

package object emblematic {

  /** a [[TypeKeyMap]] of [[Emblem emblems]] */
  private[longevity] type EmblemPool = TypeKeyMap[Any, Emblem]

  /** a no-arg function with return type `A` */
  private[longevity] type Function0[A] = () => A

  /** A [[TypeKeyMap]] of `Any` to [[Union]] */
  private[longevity] type UnionPool = TypeKeyMap[Any, Union]

}
