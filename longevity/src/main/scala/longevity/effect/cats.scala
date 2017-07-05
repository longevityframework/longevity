package longevity.effect

import _root_.cats.effect.IO
import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

/** contains [[longevity.effect.Effect]] implementations for `cats.effect.IO`.
 *
 * note that `cats-effect` is an optional library dependency in longevity, so you will have to
 * declare the dependency yourself to use these effects.
 *
 * @see [[https://github.com/typelevel/cats-effect]]
 */
object cats {

  /** an execution context that is used as a default for blocking operations. creates new threads as
   * needed, as per `java.util.concurrent.Executors.newCachedThreadPool`
   * 
   * @see [[https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/Executors.html#newCachedThreadPool()]]
   */
  val defaultBlockingContext = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())

  /** an implicit `longevity.effect.Effect` implementation for `cats.effect.IO`. uses the
   * [[defaultBlockingContext]] for blocking operations
   * 
   * @param nonBlockingContext the execution context used for non-blocking operations
   */
  implicit def ioEffect(implicit nonBlockingContext: ExecutionContext): Effect[IO] =
    ioEffect(nonBlockingContext, defaultBlockingContext)

  /** a `longevity.effect.Effect` implementation for `cats.effect.IO`
   * 
   * @param nonBlockingContext the execution context used for non-blocking operations
   * @param blockingContext the execution context used for non-blocking operations
   */
  def ioEffect(nonBlockingContext: ExecutionContext, blockingContext: ExecutionContext): Effect[IO] =
    new Effect[IO] {
      def pure[A](a: A): IO[A] = IO.pure(a)
      def map[A, B](fa: IO[A])(f: A => B): IO[B] = fa.map(f)
      def flatMap[A, B](fa: IO[A])(f: A => IO[B]): IO[B] = fa.flatMap(f)
      def mapBlocking[A, B](fa: IO[A])(f: A => B): IO[B] = for {
        _ <- IO.shift(blockingContext)
        b <- fa.map(f)
        _ <- IO.shift(nonBlockingContext)
      } yield b
      def run[A](fa: IO[A]) = fa.unsafeRunSync()
    }
  
}
