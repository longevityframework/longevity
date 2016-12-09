package longevity.model.query

/** either of the binary logical operators ''and'' and ''or'' */
sealed trait LogicalOp

/** the and operator */
case object AndOp extends LogicalOp

/** the or operator */
case object OrOp extends LogicalOp
