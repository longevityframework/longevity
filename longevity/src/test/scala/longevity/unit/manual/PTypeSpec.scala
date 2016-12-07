package longevity.unit.manual

/** code samples found in the persistent types section of the user manual
 *
 * @see http://longevityframework.github.io/longevity/manual/ptype
 */
package PTypeSpec {

  // used in http://longevityframework.github.io/longevity/manual/ptype/properties.html
  package properties1 {
    import longevity.subdomain.annotations.component
    import longevity.subdomain.annotations.persistent

    @component case class Email(email: String)
    @component case class Markdown(markdown: String)
    @component case class Uri(uri: String)

    @component
    case class UserProfile(
      tagline: String,
      imageUri: Uri,
      description: Markdown)

    @persistent(keySet = emptyKeySet)
    case class User(
      username: String,
      email: Email,
      profile: UserProfile)
  }

  // used in http://longevityframework.github.io/longevity/manual/ptype/properties.html
  package properties2 {
    import longevity.subdomain.annotations.component

    @component case class Email(email: String)
    @component case class Markdown(markdown: String)
    @component case class Uri(uri: String)

    @component
    case class UserProfile(
      tagline: String,
      imageUri: Uri,
      description: Markdown)

    case class User(
      username: String,
      email: Email,
      profile: UserProfile)

    import longevity.subdomain.PType
    import longevity.subdomain.ptype.Prop

    object User extends PType[User] {
      object props {
        object username extends Prop[User, String]("username")
        object email extends Prop[User, Email]("email")
        object profile extends Prop[User, UserProfile]("profile") {
          object tagline extends Prop[User, String]("tagline")
          object imageUri extends Prop[User, Uri]("imageUri")
          object markdown extends Prop[User, Markdown]("markdown")
        }
      }
      val keySet = emptyKeySet
    }
  }

  // used in http://longevityframework.github.io/longevity/manual/ptype/properties.html
  package properties3 {
    import longevity.subdomain.annotations.component

    @component case class Email(email: String)
    @component case class Markdown(markdown: String)
    @component case class Uri(uri: String)

    @component
    case class UserProfile(
      tagline: String,
      imageUri: Uri,
      description: Markdown)

    case class User(
      username: String,
      email: Email,
      profile: UserProfile)

    import longevity.subdomain.PType

    object User extends PType[User] {
      object props {
        val username = prop[String]("username")
        val email = prop[Email]("email")
        // ...
      }
      val keySet = emptyKeySet
    }
  }

  // used in http://longevityframework.github.io/longevity/manual/ptype/keys.html
  package keys1 {
    import longevity.subdomain.annotations.keyVal
    import longevity.subdomain.annotations.persistent

    @keyVal[User]
    case class Username(username: String)

    @persistent(keySet = Set(
      key(User.props.username)))
    case class User(
      username: Username,
      firstName: String,
      lastName: String)
  }

  // used in http://longevityframework.github.io/longevity/manual/ptype/keys.html
  package keys2 {
    import longevity.subdomain.KeyVal
    import longevity.subdomain.PType

    case class Username(username: String) extends KeyVal[User]

    case class User(
      username: Username,
      firstName: String,
      lastName: String)   

    object User extends PType[User] {
      object props {
        val username = prop[Username]("username")
        // ...
      }
      val keySet = Set(key(props.username))
    }
  }

  // used in http://longevityframework.github.io/longevity/manual/ptype/keys.html
  package keys3 {
    import longevity.subdomain.annotations.keyVal
    import longevity.subdomain.annotations.persistent

    @keyVal[User]
    case class Username(username: String)

    @keyVal[User]
    case class FullName(last: String, first: String)

    @persistent(keySet = Set(
      key(props.username),
      key(props.fullName)))
    case class User(
      username: Username,
      fullName: FullName)
  }

  // used in http://longevityframework.github.io/longevity/manual/ptype/partition-keys.html
  package partitionKeys1 {
    import longevity.subdomain.annotations.keyVal
    import longevity.subdomain.annotations.persistent

    @keyVal[User]
    case class Username(username: String)  

    @persistent(keySet = Set(
      partitionKey(props.username)))
    case class User(
      username: Username,
      firstName: String,
      lastName: String)
  }

  // used in http://longevityframework.github.io/longevity/manual/ptype/partition-keys.html
  package partitionKeys2 {
    import longevity.subdomain.annotations.keyVal
    import longevity.subdomain.annotations.persistent

    @keyVal[User]
    case class Username(username: String)

    @keyVal[User]
    case class FullName(last: String, first: String)

    @persistent(keySet = Set(
      key(props.username),
      partitionKey(props.fullName, partition(props.fullName.last))))
    case class User(
      username: Username,
      fullName: FullName)
  }

  // used in http://longevityframework.github.io/longevity/manual/ptype/indexes.html
  package indexes1 {
    import longevity.subdomain.annotations.keyVal
    import longevity.subdomain.annotations.persistent

    @keyVal[User]
    case class Username(username: String)

    @keyVal[User]
    case class FullName(last: String, first: String)

    @persistent(
      keySet = Set(key(props.username)),
      indexSet = Set(index(props.fullName.last, props.fullName.first)))
    case class User(
      username: Username,
      fullName: FullName)
  }

  // used in http://longevityframework.github.io/longevity/manual/ptype/indexes.html
  package indexes2 {
    import longevity.subdomain.KeyVal
    import longevity.subdomain.CType
    import longevity.subdomain.PType

    case class Username(username: String) extends KeyVal[User]

    case class FullName(last: String, first: String)

    object FullName extends CType[FullName]

    case class User(
      username: Username,
      fullName: FullName)

    object User extends PType[User] {
      object props {
        val username = prop[Username]("username")
        val lastName = prop[String]("fullName.last")
        val firstName = prop[String]("fullName.first")
      }
      val keySet = Set(key(props.username))
      override val indexSet = Set(index(props.lastName, props.firstName))
    }
  }

  // used in http://longevityframework.github.io/longevity/manual/ptype/sets.html
  object sets1 {
    case class User(
      username: String,
      firstName: String,
      lastName: String)

    import longevity.subdomain.PType

    object User extends PType[User] {
      object props {
      }
      val keySet = emptyKeySet
    }
  }

  // used in http://longevityframework.github.io/longevity/manual/ptype/sets.html
  object sets2 {
    case class User(
      username: String,
      firstName: String,
      lastName: String)

    import longevity.subdomain.PType
    import longevity.subdomain.ptype.Prop

    object User extends PType[User] {
      override lazy val propSet = Set.empty[Prop[User, _]]
      val keySet = emptyKeySet
    }
  }

  // used in http://longevityframework.github.io/longevity/manual/ptype/sets.html
  object sets3 {
    import longevity.subdomain.KeyVal
 
    case class Username(username: String) extends KeyVal[User]
    case class Email(email: String) extends KeyVal[User]

    case class User(
      username: Username,
      email: Email,
      firstName: String,
      lastName: String)

    import longevity.subdomain.PType
    import longevity.subdomain.ptype.Prop

    object User extends PType[User] {
      val usernameProp = prop[Username]("username")
      val emailProp = prop[Email]("email")
      val firstNameProp = prop[String]("firstName")
      val lastNameProp = prop[String]("lastName")

      override lazy val propSet = Set[Prop[User, _]](usernameProp, emailProp, firstNameProp, lastNameProp)
      val keySet = emptyKeySet
    }
  }

}
