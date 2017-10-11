package longevity.persistence.mongo

import com.mongodb.client.model.Filters
import longevity.model.ptype.Prop
import longevity.model.query.EqOp
import longevity.model.query.GtOp
import longevity.model.query.GteOp
import longevity.model.query.LtOp
import longevity.model.query.LteOp
import longevity.model.query.NeqOp
import longevity.model.query.RelationalOp
import longevity.model.realized.RealizedPrimaryKey
import longevity.persistence.PState
import org.bson.BsonDocument
import org.bson.BsonValue
import org.bson.conversions.Bson
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

/** utilities for reading from a mongo collection. used by [[MongoRetrieve]] and
 * [[MongoQuery]]
 */
private[mongo] trait MongoRead[F[_], M, P] {
  repo: MongoPRepo[F, M, P] =>

  private lazy val bsonToModelTypeTranslator =
    new BsonToDomainModelTranslator(modelType.emblematic)

  protected def bsonToState(document: BsonDocument): PState[P] = {
    val id = if (hasPrimaryKey) None else {
      val objectId = document.getObjectId("_id").getValue
      Some(MongoId(objectId))
    }
    val rv = rowVersionFromDocument(document)
    val cdt = dateTimeFromDocument("_createdTimestamp", document)
    val udt = dateTimeFromDocument("_updatedTimestamp", document)
    val ms = booleanFromDocument("_migrationStarted", document)
    val mc = booleanFromDocument("_migrationComplete", document)
    val p  = bsonToModelTypeTranslator.translate(document)(pTypeKey)
    PState(id, rv, cdt, udt, ms, mc, p, p)
  }

  private def rowVersionFromDocument(document: BsonDocument) = if (document.isInt64("_rowVersion")) {
    Some(document.getInt64("_rowVersion").longValue)
  } else {
    None
  }

  private def dateTimeFromDocument(name: String, document: BsonDocument) = if (document.isDateTime(name)) {
    Some(new DateTime(document.getDateTime(name).getValue, DateTimeZone.UTC))
  } else {
    None
  }

  private def booleanFromDocument(name: String, document: BsonDocument) = if (document.isBoolean(name)) {
    document.getBoolean(name).getValue
  } else {
    false
  }

  protected def propValToMongo[A](value: A, prop: Prop[_ >: P, A]): BsonValue = {
    domainModelToBsonTranslator.translate(value, false)(prop.propTypeKey)
  }

  protected def mongoRelationalFilter[A](prop: Prop[_ >: P, A], op: RelationalOp, value: A) = {
    realizedPType.primaryKey match {
      case Some(k) if !k.fullyPartitioned && k.key.keyValProp == prop =>
        def f[V](k: RealizedPrimaryKey[M, P, V]) =
          mongoRelationalFilterForPrimaryKey[V](k, op, value.asInstanceOf[V])
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

  private def mongoRelationalFilterForPrimaryKey[V](
    key: RealizedPrimaryKey[M, P, V],
    op: RelationalOp,
    value: V) = {
    val propPaths = key.queryInfos

    def translate[B](pp: key.QueryInfo[B], value: V) =
      domainModelToBsonTranslator.translate(pp.get(value), false)(pp.typeKey)

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
