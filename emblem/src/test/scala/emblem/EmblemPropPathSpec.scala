package emblem

import org.scalatest._
import emblem.exceptions._

/** [[EmblemPropPath emblem property path]] specifications */
class EmblemPropPathSpec extends FlatSpec with GivenWhenThen with Matchers {

  import testData.computerParts._

  val computerEmblem = Emblem[Computer]

  behavior of "EmblemPropPath.unbounded[A](String) factory method"
  it should "fail on an empty path" in {
    intercept[EmptyPropPathException] {
      EmblemPropPath.unbounded[Computer]("")
    }
  }
  it should "fail when a segment of the path does is not an emblem prop" in {
    intercept[NoSuchPropertyException] {
      EmblemPropPath.unbounded[Computer]("cpu.gb")
    }
  }
  it should "fail when a non-leaf member of the path does not have an emblem" in {
    intercept[NonEmblemInPropPathException] {
      EmblemPropPath.unbounded[Computer]("cpu.mhz.mps")
    }
  }
  it should "return an equivalent result as the fully specd EmblemPropPath factory method" in {
    { EmblemPropPath.unbounded[Computer]("cpu.mhz")
    } should equal {
      EmblemPropPath[Computer, Double](computerEmblem, "cpu.mhz")
    }
  }

  behavior of "EmblemPropPath.unbounded(Emblem,String) factory method"
  it should "fail on an empty path" in {
    intercept[EmptyPropPathException] {
      EmblemPropPath.unbounded(computerEmblem, "")
    }
  }
  it should "fail when a segment of the path does is not an emblem prop" in {
    intercept[NoSuchPropertyException] {
      EmblemPropPath.unbounded(computerEmblem, "cpu.gb")
    }
  }
  it should "fail when a non-leaf member of the path does not have an emblem" in {
    intercept[NonEmblemInPropPathException] {
      EmblemPropPath.unbounded(computerEmblem, "cpu.mhz.mps")
    }
  }
  it should "return an equivalent result as the fully specd EmblemPropPath factory method" in {
    { EmblemPropPath.unbounded(computerEmblem, "cpu.mhz")
    } should equal {
      EmblemPropPath[Computer, Double](computerEmblem, "cpu.mhz")
    }
  }

  behavior of "EmblemPropPath.apply[A,B](String) factory method"
  it should "fail on an empty path" in {
    intercept[EmptyPropPathException] {
      EmblemPropPath[Computer, Nothing]("")
    }
  }
  it should "fail when a segment of the path does is not an emblem prop" in {
    intercept[NoSuchPropertyException] {
      EmblemPropPath[Computer, Nothing]("cpu.gb")
    }
  }
  it should "fail when a non-leaf member of the path does not have an emblem" in {
    intercept[NonEmblemInPropPathException] {
      EmblemPropPath[Computer, Nothing]("cpu.mhz.mps")
    }
  }
  it should "fail when the specified type does not match the actual type" in {
    intercept[EmblemPropPathTypeMismatchException] {
      EmblemPropPath[Computer, Nothing]("cpu.mhz")
    }
    intercept[EmblemPropPathTypeMismatchException] {
      EmblemPropPath[Computer, Int]("cpu.mhz")
    }
    intercept[EmblemPropPathTypeMismatchException] {
      EmblemPropPath[Computer, Any]("cpu.mhz")
    }
  }
  it should "return an equivalent result as the fully specd EmblemPropPath factory method" in {
    { EmblemPropPath[Computer, Double]("cpu.mhz")
    } should equal {
      EmblemPropPath[Computer, Double](computerEmblem, "cpu.mhz")
    }
  }

  behavior of "EmblemPropPath.apply[A,B](Emblem,String) factory method"
  it should "fail on an empty path" in {
    intercept[EmptyPropPathException] {
      EmblemPropPath[Computer, Nothing](computerEmblem, "")
    }
  }
  it should "fail when a segment of the path does is not an emblem prop" in {
    intercept[NoSuchPropertyException] {
      EmblemPropPath[Computer, Nothing](computerEmblem, "cpu.gb")
    }
  }
  it should "fail when a non-leaf member of the path does not have an emblem" in {
    intercept[NonEmblemInPropPathException] {
      EmblemPropPath[Computer, Nothing](computerEmblem, "cpu.mhz.mps")
    }
  }
  it should "fail when the specified type does not match the actual type" in {
    intercept[EmblemPropPathTypeMismatchException] {
      EmblemPropPath[Computer, Nothing](computerEmblem, "cpu.mhz")
    }
  }

  behavior of "EmblemPropPath.name"
  it should "match the (string) path provided when the path was constructed" in {
    val epp = EmblemPropPath[Computer, Double]("cpu.mhz")
    epp.name should equal ("cpu.mhz")
  }

  behavior of "EmblemPropPath.typeKey"
  it should "match the type of the lead element of the path" in {
    val epp = EmblemPropPath[Computer, Double]("cpu.mhz")
    (epp.typeKey =:= typeKey[Double]) should be (true)
  }

  behavior of "EmblemPropPath.get"
  it should "produce the right value for the instance and the path" in {
    val epp = EmblemPropPath[Computer, Double]("cpu.mhz")
    val actualMhz = 3000000000.0D
    val computer = Computer(Memory(16), CPU(actualMhz), Display(780))
    epp.get(computer) should equal (actualMhz)
  }

  behavior of "EmblemPropPath.set"
  it should "produce a new object with the value along the path reset" in {
    val epp = EmblemPropPath[Computer, Double]("cpu.mhz")
    val oldMhz = 3000000000.0D
    val newMhz = 3200000000.0D
    val oldComputer = Computer(Memory(16), CPU(oldMhz), Display(780))
    val newComputer = epp.set(oldComputer, newMhz)
    newComputer.cpu.mhz should equal (newMhz)
  }

  behavior of "EmblemPropPath.props"
  it should "produce a new object with the value along the path reset" in {
    val epp = EmblemPropPath[Computer, Double]("cpu.mhz")
    val props = epp.props
    props.size should equal (2)
    props.head.name should equal ("cpu")
    props.tail.head.name should equal ("mhz")
  }

}
