package longevity.migrations

import org.scalatest.Matchers
import org.scalatest.FlatSpec

class LineTaggerSpec extends FlatSpec with Matchers {

  private val t = LineTagger("example.model", "example.migrations.v0")

  behavior of "LineTagger.tagLine"

  it should "substitute in full package declarations" in {
    t.tagLine("package example.model")     should equal ("package example.migrations.v0")
    t.tagLine("package example.model;")    should equal ("package example.migrations.v0;")
    t.tagLine("  package example.model")   should equal ("  package example.migrations.v0")
    t.tagLine(";;  package example.model") should equal (";;  package example.migrations.v0")
  }

  it should "substitute in prefix package declarations" in {
    t.tagLine("package example")     should equal ("package example.migrations")
    t.tagLine("package example;")    should equal ("package example.migrations;")
    t.tagLine("  package example")   should equal ("  package example.migrations")
    t.tagLine(";;  package example") should equal (";;  package example.migrations")
  }

  it should "substitute in package object declarations" in {
    t.tagLine("package object model")     should equal ("package object v0")
    t.tagLine("package object model {")   should equal ("package object v0 {")
    t.tagLine("  package object model")   should equal ("  package object v0")
    t.tagLine(";;  package object model") should equal (";;  package object v0")
  }

  it should "substitute in import declarations" in {
    t.tagLine("import example.model")     should equal ("import example.migrations.v0")
    t.tagLine("import example.model;")    should equal ("import example.migrations.v0;")
    t.tagLine("  import example.model")   should equal ("  import example.migrations.v0")
    t.tagLine(";;  import example.model") should equal (";;  import example.migrations.v0")

    t.tagLine("import example.model.foo")     should equal ("import example.migrations.v0.foo")
    t.tagLine("import example.model.foo;")    should equal ("import example.migrations.v0.foo;")
    t.tagLine("  import example.model.foo")   should equal ("  import example.migrations.v0.foo")
    t.tagLine(";;  import example.model.foo") should equal (";;  import example.migrations.v0.foo")
  }
  
}
