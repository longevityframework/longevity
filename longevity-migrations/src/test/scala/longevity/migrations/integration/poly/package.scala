package longevity.migrations.integration

import longevity.config.LongevityConfig
import longevity.migrations.Migration

package object poly {

  def userToUser(u1: m1.User): m2.User = u1 match {
    case m1.Member(m1.Username(u), l, f, t, m) => m2.Member(m2.Username(u), m2.Fullname(l, f, t), m)
    case m1.Commenter(m1.Username(u), l, f, t) => m2.Commenter(m2.Username(u), m2.Fullname(l, f, t))
  }

  def migrationForConfig(config: LongevityConfig) =
    Migration.builder[m1.M1, m2.M2](None, "v2", config, config)
      .drop[m1.Dropper]
      .create[m2.Creater]
      .update[m1.User, m2.User](userToUser)
      .build

}
