package longevity.persistence.inmem

import longevity.exceptions.persistence.WriteConflictException
import longevity.exceptions.persistence.DuplicateKeyValException
import longevity.persistence.PState
import longevity.model.realized.RealizedKey

/** support for InMemPRepo methods that modify persistent collection. used by
 * [[InMemCreate]], [[InMemDelete]] and [[InMemUpdate]].
 */
private[inmem] trait InMemWrite[M, P] {
  repo: InMemPRepo[M, P] =>

  private var idCounter = 0

  /** caller must wrap this call in synchronized block! */
  protected[inmem] def nextId: Int = {
    val id = idCounter
    idCounter += 1
    id
  }

  protected[inmem] def keys: Seq[RealizedKey[M, _ >: P, _]] = myKeys

  protected def myKeys: Seq[RealizedKey[M, _ >: P, _]] = realizedPType.keySet.toSeq

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

  protected[inmem] def registerByKeyVal(keyVal: Any, state: PState[P]) =
    keyValToPStateMap += keyVal -> state

  protected[inmem] def assertUniqueKeyVals(state: PState[P]): Unit = keys.foreach { key =>
    assertUniqueKeyVal(key, key.keyValForP(state.get), state)
  }

  protected[inmem] def assertUniqueKeyVal(
    realizedKey: RealizedKey[M, _ >: P, _],
    keyVal: Any,
    state: PState[P]): Unit = {
    if (keyValToPStateMap.contains(keyVal) && keyValToPStateMap(keyVal).id != state.id) {
      throw new DuplicateKeyValException(state.get, realizedKey.key)
    }
  }

  protected def unregisterByKeyVals(p: P) = keys.foreach { key =>
    unregisterByKeyVal(key.keyValForP(p))
  }

  protected[inmem] def unregisterByKeyVal(keyVal: Any) = keyValToPStateMap -= keyVal

}
