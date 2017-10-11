package longevity.migrations

case class ValidationException private[migrations] (val errors: Seq[ValidationError])
extends Exception(
  s"""|validation failed with the following errors:
      |
      | - ${errors.map(_.message).mkString("\n - ")}
      |""".stripMargin)
