package longevity.persistence.inmem

import longevity.exceptions.persistence.DuplicateKeyValException
import longevity.persistence.PState
import longevity.persistence.DatabaseId
import longevity.subdomain.AnyKeyVal
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.realized.AnyRealizedKey

/** support for InMemRepo methods that modify persistent collection. used by
 * [[InMemCreate]], [[InMemDelete]] and [[InMemUpdate]].
 */
private[inmem] trait InMemWrite[P <: Persistent] {
  repo: InMemRepo[P] =>

  private var idCounter = 0

  protected def dumpKeys(p: P) = keys.foreach { key =>
    unregisterKeyVal(key.keyValForP(p))
  }

  protected def persist(id: DatabaseId[P], p: P): PState[P] = {
    val state = new PState[P](id, p)
    repo.synchronized {
      keys.foreach { key =>
        assertUniqueKeyVal(key.keyValForP(p), state)
      }
      registerPStateById(state)
      keys.foreach { key =>
        registerPStateByKeyVal(key.keyValForP(p), state)
      }
    }
    state
  }

  protected[inmem] def nextId: Int = repo.synchronized {
    val id = idCounter
    idCounter += 1
    id
  }

  protected[inmem] def keys: Seq[AnyRealizedKey[_ >: P <: Persistent]] = myKeys

  protected def myKeys: Seq[AnyRealizedKey[_ >: P <: Persistent]] = realizedPType.keySet.toSeq

  protected[inmem] def assertUniqueKeyVal(keyVal: AnyKeyVal[_ <: Persistent], state: PState[P]): Unit = {
    if (keyValToPStateMap.contains(keyVal)) {
      throw new DuplicateKeyValException[P](state.get, keyVal.key)
    }
  }

  protected[inmem] def registerPStateById(state: PState[P]): Unit =
    idToPStateMap += (state.id -> state)

  protected[inmem] def unregisterPStateById(state: PState[P]): Unit =
    idToPStateMap -= state.id

  protected[inmem] def registerPStateByKeyVal(keyVal: Any, state: PState[P]): Unit =
    keyValToPStateMap += keyVal -> state

  protected[inmem] def unregisterKeyVal(keyVal: Any): Unit = keyValToPStateMap -= keyVal

}
