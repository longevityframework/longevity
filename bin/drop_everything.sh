#! /bin/sh

mongo < bin/drop_mongo.txt

cqlsh -e "drop keyspace longevity_migrations_test_basic"
cqlsh -e "drop keyspace longevity_migrations_test_failures"
cqlsh -e "drop keyspace longevity_migrations_test_poly"
cqlsh -e "drop keyspace longevity_test"

rm longevity_migrations_test_basic.db
rm longevity_migrations_test_failures.db
rm longevity_migrations_test_poly.db
rm longevity_test.db
