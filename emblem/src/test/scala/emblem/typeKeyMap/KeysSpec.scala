package emblem.typeKeyMap

import emblem.TypeKey
import emblem.TypeKeyMap
import emblem.typeKey
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** specifications for [[TypeKeyMap.keys]] */
class KeysSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "TypeKeyMap.keys"

  it should "return the keys of this map as an iterable" in {
    import emblem.testData.computerParts._

    var partLists = TypeKeyMap[ComputerPart, List]()
    var keys: Iterable[TypeKey[_ <: ComputerPart]] = partLists.keys
    keys.toSet should equal (Set())

    partLists += Memory(2) :: Memory(4) :: Memory(8) :: Nil
    keys = partLists.keys
    keys.toSet should equal (Set(typeKey[Memory]))

    partLists += CPU(2.2) :: CPU(2.4) :: CPU(2.6) :: Nil
    keys = partLists.keys
    keys.toSet should equal (Set(typeKey[Memory], typeKey[CPU]))

    partLists += Display(720) :: Display(1080) :: Nil
    keys = partLists.keys
    keys.toSet should equal (Set(typeKey[Memory], typeKey[CPU], typeKey[Display]))
  }  
 
}
