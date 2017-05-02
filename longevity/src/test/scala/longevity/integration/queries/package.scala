package longevity.integration

import longevity.FixedPoolExecutionContext

package object queries {

  implicit lazy val queryTestsExecutionContext = new FixedPoolExecutionContext(10)

}
