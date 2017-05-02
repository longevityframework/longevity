package longevity.integration

import longevity.FixedPoolExecutionContext

package object model {

  implicit lazy val modelTestsExecutionContext = new FixedPoolExecutionContext(10)

}
