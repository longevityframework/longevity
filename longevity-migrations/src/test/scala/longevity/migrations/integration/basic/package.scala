package longevity.migrations.integration

import longevity.config.LongevityConfig
import longevity.migrations.Migration

package object basic {

  def userToUser(u1: m1.User): m2.User = 
    m2.User(
      m2.Username(u1.username.value),
      m2.Fullname(u1.last, u1.first, u1.title))

  def migrationForConfig(config: LongevityConfig) =
    Migration.builder[m1.M1, m2.M2](None, "v2", config, config)
      .drop[m1.Dropper]
      .create[m2.Creater]
      .update(userToUser)
      .build

}
