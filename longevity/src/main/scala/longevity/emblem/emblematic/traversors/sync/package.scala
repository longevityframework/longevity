package longevity.emblem.emblematic.traversors

import typekey.TypeKeyMap

package object sync {

  /** a [[TypeKeyMap]] for [[CustomGenerator generator functions]] */
  private[longevity] type CustomGeneratorPool = TypeKeyMap[Any, CustomGenerator]

  private[longevity] object CustomGeneratorPool {

    /** an empty map of [[CustomGenerator generator functions]] */
    val empty: CustomGeneratorPool = TypeKeyMap[Any, CustomGenerator]()
  }

}
