package longevity.migrations.integration

import longevity.config.LongevityConfig
import longevity.migrations.Migration
import longevity.migrations.integration.basic.m1
import longevity.migrations.integration.basic.m2

package object failures {

  object IntermittentFailure extends RuntimeException

  private val failureRate = 40

  def userToUser: m1.User => m2.User = {
    var i = 0

    { u1: m1.User =>
      i += 1
      if (i % failureRate == 0) throw IntermittentFailure
      else
        m2.User(
          m2.Username(u1.username.value),
          m2.Fullname(u1.last, u1.first, u1.title))
    }
  }

  def migrationForConfig(config: LongevityConfig) =
    Migration.builder[m1.M1, m2.M2](None, "v2", config, config)
      .drop[m1.Dropper]
      .create[m2.Creater]
      .update[m1.User, m2.User](userToUser)
      .build

}
