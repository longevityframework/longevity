package longevity.migrations.integration.basic.m1

@longevity.model.annotations.persistent[M1]
case class Dropper(
  id: String,
  descr: String)
