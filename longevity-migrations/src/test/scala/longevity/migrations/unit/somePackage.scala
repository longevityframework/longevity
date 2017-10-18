package longevity.migrations

// i want to say package object unit here, but Scala reflection gets thrown for an infinite loop when i do
package object somePackage {

  import unit._

  val basicMigration = Migration.builder[basic.DomainModel, basic.DomainModel](None, "v1")
    .update[basic.User, basic.User](identity)
    .build

}
