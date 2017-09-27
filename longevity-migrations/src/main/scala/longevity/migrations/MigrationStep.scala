package longevity.migrations

import longevity.model.PEv

// TODO: scaladocs

/** a single step in a [[Migration]]
 * 
 * @tparam M1 the initial version of the model
 * @tparam M2 the final version of the model
 */
sealed trait MigrationStep[M1, M2]

/** drops a persistent type from the model
 * 
 * @tparam M1 the initial version of the model
 * @tparam M2 the final version of the model
 * @tparam P1 the persistent type to drop from the model
 */
case class DropStep[M1, M2, P1 : PEv[M1, ?]]() extends MigrationStep[M1, M2] {
  private[migrations] val pEv1 = implicitly[PEv[M1, P1]]
}

/** adds a new persistent type to the model
 * 
 * @tparam M1 the initial version of the model
 * @tparam M2 the final version of the model
 * @tparam P2 the persistent type to add to the model
 */
case class CreateStep[M1, M2, P2 : PEv[M2, ?]]() extends MigrationStep[M1, M2] {
  private[migrations] val pEv2 = implicitly[PEv[M2, P2]]
}

/** migrates a persistent type from the initial to the final model
 * 
 * @tparam M1 the initial version of the model
 * @tparam M2 the final version of the model
 * @tparam P1 the persistent type from the initial model
 * @tparam P2 the persistent type from the final model
 * @param f a function to migrate a single persistent object from type `P1` to type `P2`
 */
case class UpdateStep[M1, M2, P1 : PEv[M1, ?], P2 : PEv[M2, ?]](f: P1 => P2) extends MigrationStep[M1, M2] {
  private[migrations] val pEv1 = implicitly[PEv[M1, P1]]
  private[migrations] val pEv2 = implicitly[PEv[M2, P2]]
}
