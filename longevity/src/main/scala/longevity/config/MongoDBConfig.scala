package longevity.config

/** the MongoDB configuration
 *
 * @param uri the MongoDB URI
 * @param db the name of the MongoDB database to use
 *
 * @see LongevityConfig
 */
case class MongoDBConfig(uri: String, db: String)
