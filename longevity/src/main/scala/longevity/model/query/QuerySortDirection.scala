package longevity.model.query

/** the direction for a [[QuerySortExpr query sort expression]] */
sealed trait QuerySortDirection

/** an ascending sort for a [[QuerySortExpr query sort expression]] */
case object Ascending extends QuerySortDirection

/** a descending sort for a [[QuerySortExpr query sort expression]] */
case object Descending extends QuerySortDirection
