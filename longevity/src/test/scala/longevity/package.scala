import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

package object longevity {

  implicit lazy val fixedPoolExecutionContext_10 = new FixedPoolExecutionContext(30)
  //implicit lazy val fixedPoolExecutionContext_10 = scala.concurrent.ExecutionContext.Implicits.global

  class FixedPoolExecutionContext(numThreads: Int) extends ExecutionContext {
    val threadPool = Executors.newFixedThreadPool(numThreads)
    def execute(runnable: Runnable) = threadPool.submit(runnable)
    def reportFailure(t: Throwable) = {}
    def shutdown = threadPool.shutdown
  }

}
