package longevity.integration.subdomain.controlledVocab

import longevity.subdomain.embeddable.DerivedType
import longevity.subdomain.embeddable.EType
import longevity.subdomain.embeddable.Embeddable
import longevity.subdomain.embeddable.PolyType

sealed trait ControlledVocab extends Embeddable

object ControlledVocab extends EType[ControlledVocab] with PolyType[ControlledVocab]

// empty case class version:
case class VocabTerm1() extends ControlledVocab

object VocabTerm1 extends DerivedType[VocabTerm1, ControlledVocab] {
  val polyType = ControlledVocab
}

// case object version:
case object VocabTerm2 extends ControlledVocab

object VocabTerm2Type extends DerivedType[VocabTerm2.type, ControlledVocab] {
  val polyType = ControlledVocab
}

// illegal cyclic reference:
// case object VocabTerm2 extends ControlledVocab with DerivedType[VocabTerm2.type, ControlledVocab] {
//   val polyType = ControlledVocab
// }
