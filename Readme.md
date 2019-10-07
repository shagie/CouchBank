# CouchBank

A simple banking example in couchdb

`docker-compose up`

Build the Data module with maven and run it.

View url:`
http://localhost:5984/transactions/_design/summary/_view/balance?startkey=%22000001%22&endkey=%22000001%22`

The key is in quotes.  The above example is for account id `000001`.