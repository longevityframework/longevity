package longevity.persistence.inmem

import longevity.exceptions.persistence.WriteConflictException
import longevity.exceptions.persistence.DuplicateKeyValException
import longevity.persistence.PState
import longevity.subdomain.AnyKeyVal
import longevity.subdomain.realized.AnyRealizedKey

/** support for InMemRepo methods that modify persistent collection. used by
 * [[InMemCreate]], [[InMemDelete]] and [[InMemUpdate]].
 */
private[inmem] trait InMemWrite[P] {
  repo: InMemRepo[P] =>

  private var idCounter = 0

  /** caller must wrap this call in synchronized block! */
  protected[inmem] def nextId: Int = {
    val id = idCounter
    idCounter += 1
    id
  }

  protected[inmem] def keys: Seq[AnyRealizedKey[_ >: P]] = myKeys

  protected def myKeys: Seq[AnyRealizedKey[_ >: P]] = realizedPType.keySet.toSeq

  protected[inmem] def assertNoWriteConflict(state: PState[P]) = {
    if (persistenceConfig.optimisticLocking) {
      val id = state.id.get
      if (!idToPStateMap.contains(id) || idToPStateMap(id).rowVersion != state.rowVersion) {
        throw new WriteConflictException(state)
      }
    }
  }

  protected[inmem] def registerById(state: PState[P]): Unit =
    idToPStateMap += (state.id.get -> state)

  protected[inmem] def unregisterById(state: PState[P]): Unit =
    idToPStateMap -= state.id.get

  protected def registerByKeyVals(state: PState[P]) = keys.foreach { key =>
    registerByKeyVal(key.keyValForP(state.get), state)
  }

  protected[inmem] def registerByKeyVal(keyVal: AnyKeyValAtAll, state: PState[P]) =
    keyValToPStateMap += ((keyVal, state)) // Scala compiler gripes on -> pair syntax here

  protected[inmem] def assertUniqueKeyVals(state: PState[P]): Unit = keys.foreach { key =>
    assertUniqueKeyVal(key, key.keyValForP(state.get), state)
  }

  protected[inmem] def assertUniqueKeyVal(
    realizedKey: AnyRealizedKey[_ >: P],
    keyVal: AnyKeyVal[_],
    state: PState[P]): Unit = {
    if (keyValToPStateMap.contains(keyVal) && keyValToPStateMap(keyVal).id != state.id) {
      throw new DuplicateKeyValException[P](state.get, realizedKey.key)
    }
  }

  protected def unregisterByKeyVals(p: P) = keys.foreach { key =>
    unregisterByKeyVal(key.keyValForP(p))
  }

  protected[inmem] def unregisterByKeyVal(keyVal: AnyKeyValAtAll) = keyValToPStateMap -= keyVal

}
