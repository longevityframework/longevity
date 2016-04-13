package emblem.traversors

import emblem.TypeKeyMap

/** holds types and zero values used by synchronous traversors */
package object sync {

  /** a [[TypeKeyMap]] for [[CustomGenerator generator functions]] */
  type CustomGeneratorPool = TypeKeyMap[Any, CustomGenerator]

  object CustomGeneratorPool {

    /** an empty map of [[CustomGenerator generator functions]] */
    val empty: CustomGeneratorPool = TypeKeyMap[Any, CustomGenerator]()
  }

}
