#!/bin/sh

echo "ok"

curl -X PUT http://couchdb:5984/_users
curl -X PUT http://couchdb:5984/_replicator
curl -X PUT http://couchdb:5984/_global_changes

curl -X PUT http://couchdb:5984/accounts
curl -X PUT http://couchdb:5984/transactions

curl --upload-file summary.json http://couchdb:5984/transactions/_design/summary
