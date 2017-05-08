package longevity.integration.noTranslation

// technically, there should be a test like this for mongoContext.inMemTestRepo as
// well. for now, we are going to let it slide when the InMem repo happily persists
// a type that is not in the domain model.

class MongoNoTranslationSpec extends NoTranslationSpec(mongoContext.testRepo)
