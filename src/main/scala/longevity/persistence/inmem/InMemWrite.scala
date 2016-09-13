package longevity.persistence.inmem

import longevity.exceptions.persistence.WriteConflictException
import longevity.exceptions.persistence.DuplicateKeyValException
import longevity.persistence.PState
import longevity.subdomain.AnyKeyVal
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.realized.AnyRealizedKey

/** support for InMemRepo methods that modify persistent collection. used by
 * [[InMemCreate]], [[InMemDelete]] and [[InMemUpdate]].
 */
private[inmem] trait InMemWrite[P <: Persistent] {
  repo: InMemRepo[P] =>

  private var idCounter = 0

  /** caller must wrap this call in synchronized block! */
  protected[inmem] def nextId: Int = {
    val id = idCounter
    idCounter += 1
    id
  }

  protected[inmem] def keys: Seq[AnyRealizedKey[_ >: P <: Persistent]] = myKeys

  protected def myKeys: Seq[AnyRealizedKey[_ >: P <: Persistent]] = realizedPType.keySet.toSeq

  protected[inmem] def assertNoWriteConflict(state: PState[P]) = {
    if (persistenceConfig.optimisticLocking) {
      val id = state.id
      if (!idToPStateMap.contains(id) || idToPStateMap(id).rowVersion != state.rowVersion) {
        throw new WriteConflictException(state)
      }
    }
  }

  protected[inmem] def registerById(state: PState[P]): Unit =
    idToPStateMap += (state.id -> state)

  protected[inmem] def unregisterById(state: PState[P]): Unit =
    idToPStateMap -= state.id

  protected def registerByKeyVals(state: PState[P]) = keys.foreach { key =>
    registerByKeyVal(key.keyValForP(state.get), state)
  }

  protected[inmem] def registerByKeyVal(keyVal: AnyKeyValAtAll, state: PState[P]) =
    keyValToPStateMap += ((keyVal, state)) // Scala compiler gripes on -> pair syntax here

  protected[inmem] def assertUniqueKeyVals(state: PState[P]): Unit = keys.foreach { key =>
    assertUniqueKeyVal(key.keyValForP(state.get), state)
  }

  protected[inmem] def assertUniqueKeyVal(keyVal: AnyKeyVal[_ <: Persistent], state: PState[P]): Unit = {
    if (keyValToPStateMap.contains(keyVal) &&
        keyValToPStateMap(keyVal).id != state.id) {
      throw new DuplicateKeyValException[P](state.get, keyVal.key)
    }
  }

  protected def unregisterByKeyVals(p: P) = keys.foreach { key =>
    unregisterByKeyVal(key.keyValForP(p))
  }

  protected[inmem] def unregisterByKeyVal(keyVal: AnyKeyValAtAll) = keyValToPStateMap -= keyVal

}
