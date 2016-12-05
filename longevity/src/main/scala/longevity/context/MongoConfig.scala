package longevity.context

/** the mongo configuration
 *
 * @param uri the MongoDB URI
 * @param db the name of the MongoDB database to use
 *
 * @see LongevityConfig
 */
case class MongoConfig(uri: String, db: String)
