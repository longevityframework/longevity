package longevity.test

import longevity.model.query.Query
import streamadapter.fs2.fs2StreamToChunkerator

/** extends [[QuerySpec]] to add tests for 
 * [[longevity.persistence.streams.FS2Repo.queryToFS2 FS2Repo.queryToFS2]]
 * 
 * @tparam M the model
 *
 * @tparam P the persistent type
 */
trait ExerciseFS2[M, P] extends QuerySpec[M, P] {

  override protected def exerciseStreams(query: Query[P], expected: Set[P]): Unit = {
    super.exerciseStreams(query, expected)
    exerciseFS2(query, expected)
  }

  private def exerciseFS2(query: Query[P], expected: Set[P]): Unit = {
    val S = fs2.Strategy.fromFixedDaemonPool(8, threadName = "worker")
    val source = repo.queryToFS2(query)
    val results = fs2StreamToChunkerator(S).adapt(source).toVector.map(_.get).toSet
    val actual = pStates.map(_.get).toSet intersect results
    exerciseStream(query, actual, expected)
  }

}
