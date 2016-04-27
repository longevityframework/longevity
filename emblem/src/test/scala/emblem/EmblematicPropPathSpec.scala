package emblem

import org.scalatest._
import emblem.exceptions._

/** [[EmblematicPropPath emblem property path]] specifications */
class EmblematicPropPathSpec extends FlatSpec with GivenWhenThen with Matchers {

  import testData.computerParts._

  val computerEmblem = Emblem[Computer]

  behavior of "EmblematicPropPath.unbounded[A](String) factory method"

  it should "fail on an empty path" in {
    intercept[EmptyPropPathException] {
      EmblematicPropPath.unbounded[Computer]("")
    }
  }

  it should "fail when a segment of the path does is not an emblem prop" in {
    intercept[NoSuchPropertyException] {
      EmblematicPropPath.unbounded[Computer]("cpu.gb")
    }
  }

  it should "fail when a non-leaf member of the path does not have an emblem" in {
    intercept[NonEmblemInPropPathException[_]] {
      EmblematicPropPath.unbounded[Computer]("cpu.mhz.mps")
    }
  }

  it should "return an equivalent result as the fully specd EmblematicPropPath factory method" in {
    { EmblematicPropPath.unbounded[Computer]("cpu.mhz")
    } should equal {
      EmblematicPropPath[Computer, Double](computerEmblem, "cpu.mhz")
    }
  }

  behavior of "EmblematicPropPath.unbounded(Emblem,String) factory method"

  it should "fail on an empty path" in {
    intercept[EmptyPropPathException] {
      EmblematicPropPath.unbounded(computerEmblem, "")
    }
  }

  it should "fail when a segment of the path does is not an emblem prop" in {
    intercept[NoSuchPropertyException] {
      EmblematicPropPath.unbounded(computerEmblem, "cpu.gb")
    }
  }

  it should "return an equivalent result as the fully specd EmblematicPropPath factory method" in {
    { EmblematicPropPath.unbounded(computerEmblem, "cpu.mhz")
    } should equal {
      EmblematicPropPath[Computer, Double](computerEmblem, "cpu.mhz")
    }
  }

  behavior of "EmblematicPropPath.apply[A,B](String) factory method"

  it should "fail on an empty path" in {
    intercept[EmptyPropPathException] {
      EmblematicPropPath[Computer, Nothing]("")
    }
  }

  it should "fail when a segment of the path does is not an emblem prop" in {
    intercept[NoSuchPropertyException] {
      EmblematicPropPath[Computer, Nothing]("cpu.gb")
    }
  }

  it should "fail when the specified type does not match the actual type" in {
    intercept[EmblematicPropPathTypeMismatchException] {
      EmblematicPropPath[Computer, Nothing]("cpu.mhz")
    }
    intercept[EmblematicPropPathTypeMismatchException] {
      EmblematicPropPath[Computer, Int]("cpu.mhz")
    }
    intercept[EmblematicPropPathTypeMismatchException] {
      EmblematicPropPath[Computer, Any]("cpu.mhz")
    }
  }

  it should "return an equivalent result as the fully specd EmblematicPropPath factory method" in {
    { EmblematicPropPath[Computer, Double]("cpu.mhz")
    } should equal {
      EmblematicPropPath[Computer, Double](computerEmblem, "cpu.mhz")
    }
  }

  behavior of "EmblematicPropPath.apply[A,B](Emblem,String) factory method"

  it should "fail on an empty path" in {
    intercept[EmptyPropPathException] {
      EmblematicPropPath[Computer, Nothing](computerEmblem, "")
    }
  }

  it should "fail when a segment of the path does is not an emblem prop" in {
    intercept[NoSuchPropertyException] {
      EmblematicPropPath[Computer, Nothing](computerEmblem, "cpu.gb")
    }
  }

  it should "fail when the specified type does not match the actual type" in {
    intercept[EmblematicPropPathTypeMismatchException] {
      EmblematicPropPath[Computer, Nothing](computerEmblem, "cpu.mhz")
    }
  }

  behavior of "EmblematicPropPath.name"

  it should "match the (string) path provided when the path was constructed" in {
    val epp = EmblematicPropPath[Computer, Double]("cpu.mhz")
    epp.name should equal ("cpu.mhz")
  }

  behavior of "EmblematicPropPath.typeKey"

  it should "match the type of the lead element of the path" in {
    val epp = EmblematicPropPath[Computer, Double]("cpu.mhz")
    (epp.typeKey =:= typeKey[Double]) should be (true)
  }

  behavior of "EmblematicPropPath.get"

  it should "produce the right value for the instance and the path" in {
    val epp = EmblematicPropPath[Computer, Double]("cpu.mhz")
    val actualMhz = 3000000000.0D
    val computer = Computer(Memory(16), CPU(actualMhz), Display(780))
    epp.get(computer) should equal (actualMhz)
  }

  behavior of "EmblematicPropPath.props"

  it should "produce a new object with the value along the path reset" in {
    val epp = EmblematicPropPath[Computer, Double]("cpu.mhz")
    val props = epp.props
    props.size should equal (2)
    props.head.name should equal ("cpu")
    props.tail.head.name should equal ("mhz")
  }

}
