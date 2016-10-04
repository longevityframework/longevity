package longevity.test

import org.scalatest.Matchers
import org.scalatest.Suite
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.concurrent.ScaledTimeSpans
import org.scalatest.time.SpanSugar._
import scala.concurrent.ExecutionContext

/** common code for longevity specs that use futures */
trait LongevityFuturesSpec extends Matchers with ScalaFutures with ScaledTimeSpans {

  self: Suite =>

  protected implicit val executionContext: ExecutionContext

  override implicit def patienceConfig = PatienceConfig(
    timeout = scaled(10000.millis),
    interval = scaled(50.millis))

}
