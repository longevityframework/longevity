package longevity.unit.model.annotations.testExamples

import longevity.model.annotations.component
import longevity.model.annotations.derivedComponent
import longevity.model.annotations.polyComponent

// normally i would put these test examples directly in the test. or, if they
// needed to be stable, i would put them in the companion object for the test.
// unfortunately, the macro mechanism seems to choke for all my @component
// macros when they are used within a companion object. they are only working
// top-level for me. problem is, the annotation is on the definition of the type
// that i need to reflect on to generate the mprops. the mprops macro can pull
// up the type just fine when things are declared top level, but not within a
// stable object.

@component[DomainModel] class ComponentNoCompanionClass

@component[DomainModel] class ComponentWithCompanion

object ComponentWithCompanion { val y = 7 }

@component[DomainModel] case class ComponentWithCompanionCaseClass()

@polyComponent[DomainModel] sealed trait PolyComponent

@polyComponent[DomainModel] sealed trait PolyComponentWithCompanion

object PolyComponentWithCompanion { val y = 7 }

@derivedComponent[DomainModel, PolyComponent] class DerivedComponentNoCompanionClass extends PolyComponent

@derivedComponent[DomainModel, PolyComponent] object DerivedComponentNoCompanionObject extends PolyComponent

@derivedComponent[DomainModel, PolyComponent] class DerivedComponentWithCompanion extends PolyComponent

object DerivedComponentWithCompanion { val y = 7 }

@derivedComponent[DomainModel, PolyComponent]
case class DerivedComponentWithCompanionCaseClass() extends PolyComponent

object DerivedComponentWithCompanionCaseClass { val y = 7 }
