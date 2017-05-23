package longevity.unit.manual

import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** code samples found in the key value section of the user manual */
object KeyValSpec {

  // duplicated at http://longevityframework.github.io/longevity/manual/key-values.html
  object keyValues1 {

    import longevity.model.ModelEv
    import longevity.model.ModelType
    import longevity.model.PTypePool

    trait DomainModel
    object DomainModel {
      implicit object modelType extends ModelType[DomainModel](PTypePool(User))
      private[keyValues1] implicit object modelEv extends ModelEv[DomainModel]
    }

    import longevity.model.KeyVal

    case class Username(username: String) extends KeyVal[User]

    case class User(
      username: Username,
      firstName: String,
      lastName: String)

    import longevity.model.PType

    object User extends PType[DomainModel, User] {
      object props {
        val username = prop[Username]("username")
      }
      override val keySet = Set(key(props.username))
    }

  }

  // duplicated at http://longevityframework.github.io/longevity/manual/key-values.html
  object keyValues2 {

    import longevity.model.ModelEv
    import longevity.model.ModelType
    import longevity.model.PTypePool

    trait DomainModel
    object DomainModel {
      implicit object modelType extends ModelType[DomainModel](PTypePool(User))
      private[keyValues2] implicit object modelEv extends ModelEv[DomainModel]
    }

    import longevity.model.KeyVal

    case class Username(username: String) extends KeyVal[User]

    case class User(
      username: Username,
      firstName: String,
      lastName: String,
      sponsor: Option[Username])   

    import longevity.model.PType

    object User extends PType[DomainModel, User] {
      object props {
        val username = prop[Username]("username")
      }
      override val keySet = Set(key(props.username))
    }

  }

}

/** exercises code samples found in the key value section of the user manual.
 *
 * @see http://longevityframework.github.io/longevity/manual/key-values.html
 */
class KeyValSpec extends FlatSpec with GivenWhenThen with Matchers {

  import KeyValSpec._

  "user manual example code" should "produce correct domain models" in {

    {
      keyValues1.DomainModel.modelType.pTypePool.size should equal (1)
      keyValues1.DomainModel.modelType.pTypePool.values.head should equal (keyValues1.User)
      keyValues1.DomainModel.modelType.cTypePool should be ('empty)
      keyValues1.User.keySet.size should equal (1)
    }

    {
      keyValues2.DomainModel.modelType.pTypePool.size should equal (1)
      keyValues2.DomainModel.modelType.pTypePool.values.head should equal (keyValues2.User)
      keyValues2.DomainModel.modelType.cTypePool should be ('empty)
      keyValues2.User.keySet.size should equal (1)
    }

  }
}
