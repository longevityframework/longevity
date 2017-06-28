package longevity

import longevity.persistence.streams.AkkaStreamsRepo
import longevity.persistence.streams.FS2Repo
import longevity.persistence.streams.IterateeIoRepo
import longevity.persistence.streams.PlayRepo

/** manages entity persistence operations */
package object persistence {

  /** implicit conversion from [[Repo]] to [[longevity.persistence.streams.AkkaStreamsRepo
   * AkkaStreamsRepo]]
   * 
   * @tparam F the effect
   * @tparam M the model
   */
  implicit def repoToAkkaStreamsRepo[F[_], M](repo: Repo[F, M]) = new AkkaStreamsRepo(repo)

  /** implicit conversion from [[Repo]] to [[longevity.persistence.streams.FS2Repo FS2Repo]]
   * 
   * @tparam F the effect
   * @tparam M the model
   */
  implicit def repoToFS2Repo[F[_], M](repo: Repo[F, M]) = new FS2Repo(repo)

  /** implicit conversion from [[Repo]] to [[longevity.persistence.streams.IterateeIoRepo IterateeIoRepo]]
   * 
   * @tparam F the effect
   * @tparam M the model
   */
  implicit def repoToIterateeIoRepo[F[_], M](repo: Repo[F, M]) = new IterateeIoRepo(repo)

  /** implicit conversion from [[Repo]] to [[longevity.persistence.streams.PlayRepo PlayRepo]]
   * 
   * @tparam F the effect
   * @tparam M the model
   */
  implicit def repoToPlayRepo[F[_], M](repo: Repo[F, M]) = new PlayRepo(repo)

}
