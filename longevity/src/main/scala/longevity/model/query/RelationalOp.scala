package longevity.model.query

/** a query relational operator. compares a persistent property to a raw value */
sealed trait RelationalOp

/** the equals operator */
case object EqOp extends RelationalOp

/** the not equals operator */
case object NeqOp extends RelationalOp

/** the less than operator */
case object LtOp extends RelationalOp

/** the less than equals operator */
case object LteOp extends RelationalOp

/** the greater than operator */
case object GtOp extends RelationalOp

/** the greater than equals operator */
case object GteOp extends RelationalOp
