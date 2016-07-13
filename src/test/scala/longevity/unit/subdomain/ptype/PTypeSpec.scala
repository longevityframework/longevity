package longevity.unit.subdomain.ptype

import longevity.exceptions.subdomain.ptype.NoPropsForPTypeException
import longevity.exceptions.subdomain.ptype.NoKeysForPTypeException
import longevity.exceptions.subdomain.ptype.NoIndexesForPTypeException
import longevity.subdomain.ptype.PType
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** unit tests for the proper construction of a [[PType persistent type]] */
class PTypeSpec extends FlatSpec with GivenWhenThen with Matchers {

  import longevity.unit.blogCore._

  behavior of "PType construction"

  it should "throw exception when the `propSet` is not overridden, and there is no `object props`" in {
    object User extends PType[User] {
      object keys {
      }
      object indexes {
      }
    }
    intercept[NoPropsForPTypeException[_]] {
      User.propSet
    }
  }

  it should "produce an empty `propSet` when `object props` is empty" in {
    object User extends PType[User] {
      object props {
      }
      object keys {
      }
      object indexes {
      }
    }
    User.propSet should equal (Set())
  }

  it should "produce a non-empty `propSet` when `object props` holds props of the right type" in {
    object User extends PType[User] {
      object props {
        val username = prop[Username]("username")
        val email = prop[Email]("email")
      }
      object keys {
      }
      object indexes {
      }
    }
    User.propSet should equal (Set(User.props.username, User.props.email))
  }

  it should "throw exception when the `keySet` is not overridden, and there is no `object keys`" in {
    object User extends PType[User] {
      object props {
      }
      object indexes {
      }
    }
    intercept[NoKeysForPTypeException[_]] {
      User.keySet
    }
  }

  it should "produce an empty `keySet` when `object keys` is empty" in {
    object User extends PType[User] {
      object props {
      }
      object keys {
      }
      object indexes {
      }
    }
    User.keySet should equal (Set())
  }

  it should "produce an empty `keySet` when `object keys` is non-empty but holds no keys" in {
    object User extends PType[User] {
      object props {
      }
      object keys {
        val x = 7
      }
      object indexes {
      }
    }
    User.keySet should equal (Set())
  }

  it should "produce an empty `keySet` when `object keys` is holds no keys of the right type" in {
    object User extends PType[User] {
      object props {
      }
      object keys {
        val x = Blog.keys.uri
      }
      object indexes {
      }
    }
    User.keySet should equal (Set())
  }

  it should "produce a non-empty `keySet` when `object keys` holds keys of the right type" in {
    object User extends PType[User] {
      object props {
        val username = prop[Username]("username")
        val email = prop[Email]("email")
      }
      object keys {
        val username = key(props.username)
        val email = key(props.email)
      }
      object indexes {
      }
    }
    User.keySet should equal (Set(User.keys.username, User.keys.email))
  }

  it should "throw exception when the `indexSet` is not overridden, and there is no `object indexes`" in {
    object User extends PType[User] {
      object props {
      }
      object keys {
      }
    }
    intercept[NoIndexesForPTypeException[_]] {
      User.indexSet
    }
  }

  it should "produce an empty `indexSet` when `object indexes` is empty" in {
    object User extends PType[User] {
      object props {
      }
      object keys {
      }
      object indexes {
      }
    }
    User.indexSet should equal (Set())
  }

  it should "produce an empty `indexSet` when `object indexes` is non-empty but holds no indexes" in {
    object User extends PType[User] {
      object props {
      }
      object keys {
      }
      object indexes {
        val x = 7
      }
    }
    User.indexSet should equal (Set())
  }

  it should "produce an empty `indexSet` when `object indexes` is holds no indexes of the right type" in {
    object User extends PType[User] {
      object props {
      }
      object keys {
      }
      object indexes {
        val x = Blog.index(Blog.props.uri)
      }
    }
    User.indexSet should equal (Set())
  }

  it should "produce a non-empty `indexSet` when `object indexes` holds indexes of the right type" in {
    object User extends PType[User] {
      object props {
        val username = prop[String]("username")
        val email = prop[Email]("email")
      }
      object keys {
      }
      object indexes {
        val username = index(props.username)
        val email = index(props.email)
      }
    }
    User.indexSet should equal (Set(User.indexes.username, User.indexes.email))
  }

}
