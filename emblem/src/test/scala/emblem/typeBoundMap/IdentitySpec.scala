package emblem.typeBoundMap

import emblem.typeBound.TypeBoundMap
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** specifications for [[TypeBoundMap]] when the type bound, key, and value types are all the same */
class IdentitySpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "a TypeBoundMap where the type bound, key, and value types are all the same"

  import emblem.testData.computerParts._
  type Identity[Part <: ComputerPart] = Part

  it should "only allow key/value pairs with matching type param" in {
    var partsUpgradeMap = TypeBoundMap[ComputerPart, Identity, Identity]()
    "partsUpgradeMap += Memory(4) -> Memory(8)" should compile
    "partsUpgradeMap += Memory(4) -> CPU(2.4)" shouldNot compile
    "partsUpgradeMap += CPU(2.4) -> Memory(4)" shouldNot compile
  }

  it should "store multiple key/value pairs for a given type param" in {
    var partsUpgradeMap = TypeBoundMap[ComputerPart, Identity, Identity]()
    partsUpgradeMap += Memory(2) -> Memory(0)
    partsUpgradeMap += Memory(2) -> Memory(4) // overwrites value Memory(0)
    partsUpgradeMap += Memory(4) -> Memory(8)
    partsUpgradeMap(Memory(2)) should equal (Memory(4))
    partsUpgradeMap(Memory(4)) should equal (Memory(8))
    partsUpgradeMap += CPU(2.2) -> CPU(2.6)
    partsUpgradeMap += CPU(2.4) -> CPU(2.6)
    partsUpgradeMap += CPU(2.6) -> CPU(2.8)
    partsUpgradeMap += Display(720) -> Display(1080)

    def upgradeComputer(computer: Computer): Computer = Computer(
      partsUpgradeMap.getOrElse(computer.memory, computer.memory),
      partsUpgradeMap.getOrElse(computer.cpu, computer.cpu),
      partsUpgradeMap.getOrElse(computer.display, computer.display))

    val superComputer = Computer(Memory(32), CPU(5.0), Display(7000))
    upgradeComputer(superComputer) should equal (superComputer)

    val decentComputer = Computer(Memory(4), CPU(2.4), Display(1040))
    upgradeComputer(decentComputer) should equal {
      decentComputer.copy(memory = Memory(8), cpu = CPU(2.6))
    }

    val lousyComputer = Computer(Memory(2), CPU(2.0), Display(576))
    upgradeComputer(lousyComputer) should equal {
      lousyComputer.copy(memory = Memory(4))
    }
  }
 
}
