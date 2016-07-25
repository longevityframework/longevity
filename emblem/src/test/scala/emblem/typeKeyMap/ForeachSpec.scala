package emblem.typeKeyMap

import emblem.typeBound.TypeBoundPair
import emblem.TypeKey
import emblem.TypeKeyMap
import emblem.typeKey
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** specifications for [[TypeKeyMap.foreach]] */
class ForeachSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "TypeKeyMap.foreach"

  it should "iterator over all the TypeBoundPairs in the map" in {
    import emblem.testData.computerParts._
    val memoryList = Memory(2) :: Memory(4) :: Memory(8) :: Nil
    val cpuList = CPU(2.2) :: CPU(2.4) :: CPU(2.6) :: Nil
    val displayList = Display(720) :: Display(1080) :: Nil

    var range = Set[TypeBoundPair[ComputerPart, TypeKey, List, _ <: ComputerPart]]()

    var partLists = TypeKeyMap[ComputerPart, List]()
    partLists.foreach { pair => range += pair }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, Memory](typeKey[Memory], memoryList))
    } should be {
      false
    }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, CPU](typeKey[CPU], cpuList))
    } should be {
      false
    }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, Display](typeKey[Display], displayList))
    } should be {
      false
    }

    range = Set[TypeBoundPair[ComputerPart, TypeKey, List, _ <: ComputerPart]]()
    partLists += memoryList
    partLists.foreach { pair => range += pair }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, Memory](typeKey[Memory], memoryList))
    } should be {
      true
    }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, CPU](typeKey[CPU], cpuList))
    } should be {
      false
    }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, Display](typeKey[Display], displayList))
    } should be {
      false
    }

    range = Set[TypeBoundPair[ComputerPart, TypeKey, List, _ <: ComputerPart]]()
    partLists += cpuList
    partLists.foreach { pair => range += pair }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, Memory](typeKey[Memory], memoryList))
    } should be {
      true
    }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, CPU](typeKey[CPU], cpuList))
    } should be {
      true
    }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, Display](typeKey[Display], displayList))
    } should be {
      false
    }

    range = Set[TypeBoundPair[ComputerPart, TypeKey, List, _ <: ComputerPart]]()
    partLists += displayList
    partLists.foreach { pair => range += pair }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, Memory](typeKey[Memory], memoryList))
    } should be {
      true
    }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, CPU](typeKey[CPU], cpuList))
    } should be {
      true
    }

    { range.contains(
        TypeBoundPair[ComputerPart, TypeKey, List, Display](typeKey[Display], displayList))
    } should be {
      true
    }
  }  
 
}
