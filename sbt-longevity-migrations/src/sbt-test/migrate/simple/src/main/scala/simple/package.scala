import longevity.config.LongevityConfig
import longevity.migrations.Migration

package object simple {

  private val baseConfig = LongevityConfig.fromTypesafeConfig()
  val v1Config = baseConfig.copy(modelVersion = Some("v1"))
  val v2Config = baseConfig.copy(modelVersion = Some("v2"))

  val migrateUsername = { username1: v1.Username => v2.Username(username1.username) }
  val migrateEmail = { email1: v1.Email => v2.Email(email1.email) }
  val migrateUser = { u1: v1.User =>
    v2.User(
      username = migrateUsername(u1.username),
      email = migrateEmail(u1.email),
      v2.FullName(u1.last, u1.first, u1.title))
  }

  val v1_to_v2 = Migration.builder[v1.DomainModel, v2.DomainModel](
    Some("v1"),
    "v2",
    v1Config,
    v2Config
  ).update(migrateUser).build

  val testUser1 = v1.User(
    v1.Username("testUser"),
    v1.Email("testUser@example.com"),
    "last22", "first33", None)

  val testUser2 = v2.User(
    v2.Username("testUser"),
    v2.Email("testUser@example.com"),
    v2.FullName("last22", "first33", None))

}
