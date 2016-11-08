package longevity.persistence.mongo

import com.mongodb.client.model.Filters
import longevity.persistence.PState
import longevity.subdomain.KeyVal
import longevity.subdomain.Persistent
import longevity.subdomain.ptype.Prop
import longevity.subdomain.query.EqOp
import longevity.subdomain.query.GtOp
import longevity.subdomain.query.GteOp
import longevity.subdomain.query.LtOp
import longevity.subdomain.query.LteOp
import longevity.subdomain.query.NeqOp
import longevity.subdomain.query.RelationalOp
import longevity.subdomain.realized.RealizedPartitionKey
import org.bson.BsonDocument
import org.bson.BsonValue
import org.bson.conversions.Bson

/** utilities for reading from a mongo collection. used by [[MongoRetrieve]] and
 * [[MongoQuery]]
 */
private[mongo] trait MongoRead[P <: Persistent] {
  repo: MongoRepo[P] =>

  private lazy val bsonToSubdomainTranslator =
    new BsonToSubdomainTranslator(subdomain.emblematic)

  protected def bsonToState(document: BsonDocument): PState[P] = {
    val id = if (hasPartitionKey) None else {
      val objectId = document.getObjectId("_id").getValue
      Some(MongoId[P](objectId))
    }
    val rv = if (document.isInt64("_rowVersion")) {
      Some(document.getInt64("_rowVersion").longValue)
    } else {
      None
    }
    val p  = bsonToSubdomainTranslator.translate(document)(pTypeKey)
    PState(id, rv, p)
  }

  protected def propValToMongo[A](value: A, prop: Prop[_ >: P <: Persistent, A]): BsonValue = {
    subdomainToBsonTranslator.translate(value, false)(prop.propTypeKey)
  }

  protected def mongoRelationalFilter[A](prop: Prop[_ >: P <: Persistent, A], op: RelationalOp, value: A) = {
    realizedPType.partitionKey match {
      case Some(k) if !k.fullyPartitioned && k.key.keyValProp == prop =>
        def f[V <: KeyVal[P, V]](k: RealizedPartitionKey[P, V]) =
          mongoRelationalFilterForPartitionKey[V](k, op, value.asInstanceOf[V])
        f(k)
      case _ =>
        op match {
          case EqOp  => Filters.eq (prop.path, propValToMongo(value, prop))
          case NeqOp => Filters.ne (prop.path, propValToMongo(value, prop))
          case LtOp  => Filters.lt (prop.path, propValToMongo(value, prop))
          case LteOp => Filters.lte(prop.path, propValToMongo(value, prop))
          case GtOp  => Filters.gt (prop.path, propValToMongo(value, prop))
          case GteOp => Filters.gte(prop.path, propValToMongo(value, prop))
        }
    }
  }

  private def mongoRelationalFilterForPartitionKey[V <: KeyVal[P, V]](
    key: RealizedPartitionKey[P, V],
    op: RelationalOp,
    value: V) = {
    val propPaths = key.queryInfos

    def translate[B](pp: key.QueryInfo[B], value: V) =
      subdomainToBsonTranslator.translate(pp.get(value), false)(pp.typeKey)

    def eqPP[B](pp: key.QueryInfo[B]) = Filters.eq(pp.inlinedPath, translate(pp, value))
    def nePP[B](pp: key.QueryInfo[B]) = Filters.ne(pp.inlinedPath, translate(pp, value))
    def ltPP[B](pp: key.QueryInfo[B]) = Filters.lt(pp.inlinedPath, translate(pp, value))
    def gtPP[B](pp: key.QueryInfo[B]) = Filters.gt(pp.inlinedPath, translate(pp, value))

    def and(fs: Seq[Bson]) = Filters.and(fs: _*)
    def or (fs: Seq[Bson]) = Filters.or (fs: _*)

    def eq = and { propPaths.map { pp => eqPP(pp) } }
    def ne = or  { propPaths.map { pp => nePP(pp) } }

    def diff(f: (key.QueryInfo[_]) => Bson) = or {      
      for { i <- 0 until propPaths.length } yield {
        and {
          propPaths.take(i).map(pp => eqPP(pp)) :+ f(propPaths(i))
        }
      }
    }
 
    def lt = diff { pp => ltPP(pp) }
    def gt = diff { pp => gtPP(pp) }

    op match {
      case EqOp  => eq
      case NeqOp => ne
      case LtOp  => lt
      case LteOp => Filters.or(lt, eq)
      case GtOp  => gt
      case GteOp => Filters.or(gt, eq)
    }

  }

}
