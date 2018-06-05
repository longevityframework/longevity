package longevity.test

import longevity.model.query.Query
import streamadapter.fs2.fs2StreamToChunkerator

/** extends [[QuerySpec]] to add tests for 
 * [[longevity.persistence.streams.FS2Repo.queryToFS2 FS2Repo.queryToFS2]]
 * 
 * @tparam F the effect
 * @tparam M the model
 * @tparam P the persistent type
 */
trait ExerciseFS2[F[_], M, P] extends QuerySpec[F, M, P] {

  override protected def exerciseStreams(query: Query[P], expected: Set[P]): Unit = {
    super.exerciseStreams(query, expected)
    exerciseFS2(query, expected)
  }

  private def exerciseFS2(query: Query[P], expected: Set[P]): Unit = {
    val ec = scala.concurrent.ExecutionContext.Implicits.global
    val source = effect.run(repo.queryToFS2(query))
    val results = fs2StreamToChunkerator(ec).adapt(source).toVector.map(_.get).toSet
    val actual = pStates.map(_.get).toSet intersect results
    exerciseStream(query, actual, expected)
  }

}
