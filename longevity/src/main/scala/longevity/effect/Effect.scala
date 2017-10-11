package longevity.effect

import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.blocking
import scala.concurrent.duration.Duration
import scala.concurrent.duration.SECONDS

/** an effect type class. a monad describing how the persistence operations in longevity are
 * processed. effects are typically found implicitly when building your
 * [[longevity.context.LongevityContext LongevityContext]]. common effects are found within the
 * `Effect` companion object.
 *
 * the methods within this trait are used internally by longevity, and are only important to users
 * who wish to implement their own effect type classes.
 *
 * @tparam F the effectful type
 */
trait Effect[F[_]] {

  /** lift a raw value with type `A` into an `F[A]` */
  def pure[A](a: A): F[A]

  /** map an `F[A]` to an `F[B]` according to function `f` with type `A => B` */
  def map[A, B](fa: F[A])(f: A => B): F[B]

  /** flatMap an `F[A]` to an `F[B]` according to function `f` with type `A => F[B]` */
  def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]

  /** map an `F[A]` to an `F[B]`, using whatever means the effect has to perform potentially blocking
   * operations. for example, the effect may choose to perform the operation in a worker thread
   */
  def mapBlocking[A, B](fa: F[A])(f: A => B): F[B]

  /** execute the effects and produce a result. this is only used in the test frameworks, which need
   * to execute effects to check the result
   * 
   * @see longevity.test
   */
  def run[A](fa: F[A]): A

}

/** contains implicit `Effect` implementations for common effectful classes */
object Effect {

  /** provides object-oriented forwarders for `Effect` methods. if you have something like this:
   *
   * {{{
   * implicit val effect: Effect[F] = ...
   * val fa: F[A] = ...
   * val f: A => B = ...
   * }}}
   *
   * you can now say:
   *
   * {{{
   * import longevity.effect.Effect.Syntax
   * val fb: F[B] = fa.map(f)
   * }}}
   *
   * instead of:
   *
   * {{{
   * val fb: F[B] = effect.map(fa)(f)
   * }}}
   */
  implicit class Syntax[F[_] : Effect, A](fa: F[A]) {

    private val effect = implicitly[Effect[F]]

    /** map an `F[A]` to an `F[B]` according to function `f` with type `A => B` */
    def map[B](f: A => B): F[B] = effect.map(fa)(f)

    /** flatMap an `F[A]` to an `F[B]` according to function `f` with type `A => F[B]` */
    def flatMap[B](f: A => F[B]): F[B] = effect.flatMap(fa)(f)

    /** map an `F[A]` to an `F[B]`, using whatever means the effect has to perform potentially blocking
     * operations. for example, the effect may choose to perform the operation in a worker thread
     */
    def mapBlocking[B](f: A => B): F[B] = effect.mapBlocking(fa)(f)

    /** execute the effects and produce a result. this is only used in the test frameworks, which need
     * to execute effects to check the result
     * 
     * @see longevity.test
     */
    def run: A = effect.run(fa)

  }

  /** the default duration to await a future in [[Effect.run]] */
  val defaultDuration = Duration(10, SECONDS)

  /** an implicit `Effect` implementation for `scala.concurrent.Future` */
  implicit def futureEffect(
    implicit context: ExecutionContext,
    duration: Duration = defaultDuration) = new Effect[Future] {
    def pure[A](a: A): Future[A] = Future.successful(a)
    def map[A, B](fa: Future[A])(f: A => B): Future[B] = fa.map(f)
    def flatMap[A, B](fa: Future[A])(f: A => Future[B]): Future[B] = fa.flatMap(f)
    def mapBlocking[A, B](fa: Future[A])(f: A => B): Future[B] = blocking(fa.map(f))
    def run[A](fa: Future[A]) = Await.result(fa, duration)
  }

  /** an implicit `Effect` implementation for `longevity.effect.Blocking` */
  implicit val blockingEffect = new Effect[Blocking] {
    def pure[A](a: A): A = a
    def map[A, B](a: A)(f: A => B): B = f(a)
    def flatMap[A, B](a: A)(f: A => B): B = f(a)
    def mapBlocking[A, B](a: A)(f: A => B): B = f(a)
    def run[A](a: A) = a
  }

}
