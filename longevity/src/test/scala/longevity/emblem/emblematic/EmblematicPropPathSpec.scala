package longevity.emblem.emblematic

import longevity.emblem.exceptions.EmblematicPropPathTypeMismatchException
import longevity.emblem.exceptions.EmptyPropPathException
import longevity.emblem.exceptions.NoSuchPropertyException
import longevity.emblem.exceptions.NonEmblematicInPropPathException
import typekey.typeKey
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** [[EmblematicPropPath emblem property path]] specifications */
class EmblematicPropPathSpec extends FlatSpec with GivenWhenThen with Matchers {

  import longevity.emblem.testData.computerParts._

  val computerEmblem = Emblem[Computer]

  behavior of "EmblematicPropPath.unbounded factory method"

  it should "fail on an empty path" in {
    intercept[EmptyPropPathException] {
      EmblematicPropPath.unbounded[Computer](emblematic, "")
    }
  }

  it should "fail when a segment of the path does is not an emblem prop" in {
    intercept[NoSuchPropertyException] {
      EmblematicPropPath.unbounded[Computer](emblematic, "cpu.gb")
    }
  }

  it should "fail when a non-leaf member of the path does not have an emblem" in {
    intercept[NonEmblematicInPropPathException[_]] {
      EmblematicPropPath.unbounded[Computer](emblematic, "cpu.mhz.mps")
    }
  }

  it should "return an equivalent result as the fully specd EmblematicPropPath factory method" in {
    { EmblematicPropPath.unbounded[Computer](emblematic, "cpu.mhz")
    } should equal {
      EmblematicPropPath[Computer, Double](emblematic, "cpu.mhz")
    }
  }

  behavior of "EmblematicPropPath.apply[A,B](String) factory method"

  it should "fail on an empty path" in {
    intercept[EmptyPropPathException] {
      EmblematicPropPath[Computer, Nothing](emblematic, "")
    }
  }

  it should "fail when a segment of the path does is not an emblem prop" in {
    intercept[NoSuchPropertyException] {
      EmblematicPropPath[Computer, Nothing](emblematic, "cpu.gb")
    }
  }

  it should "fail when the specified type does not match the actual type" in {
    intercept[EmblematicPropPathTypeMismatchException] {
      EmblematicPropPath[Computer, Nothing](emblematic, "cpu.mhz")
    }
    intercept[EmblematicPropPathTypeMismatchException] {
      EmblematicPropPath[Computer, Int](emblematic, "cpu.mhz")
    }
    intercept[EmblematicPropPathTypeMismatchException] {
      EmblematicPropPath[Computer, Any](emblematic, "cpu.mhz")
    }
  }

  behavior of "EmblematicPropPath.name"

  it should "match the (string) path provided when the path was constructed" in {
    val epp = EmblematicPropPath[Computer, Double](emblematic, "cpu.mhz")
    epp.name should equal ("cpu.mhz")
  }

  behavior of "EmblematicPropPath.typeKey"

  it should "match the type of the lead element of the path" in {
    val epp = EmblematicPropPath[Computer, Double](emblematic, "cpu.mhz")
    (epp.typeKey =:= typeKey[Double]) should be (true)
  }

  behavior of "EmblematicPropPath.get"

  it should "produce the right value for the instance and the path" in {
    val epp = EmblematicPropPath[Computer, Double](emblematic, "cpu.mhz")
    val actualMhz = 3000000000.0D
    val computer = Computer(Memory(16), CPU(actualMhz), Display(780))
    epp.get(computer) should equal (actualMhz)
  }

  behavior of "EmblematicPropPath.props"

  it should "produce a new object with the value along the path reset" in {
    val epp = EmblematicPropPath[Computer, Double](emblematic, "cpu.mhz")
    val props = epp.props
    props.size should equal (2)
    props.head.name should equal ("cpu")
    props.tail.head.name should equal ("mhz")
  }

}
