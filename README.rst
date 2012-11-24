Reverse engineer a jdbc database into a scala Play 2 CRUD (Create Read Update Delete) Web App
=============================================================================================

The application written in Scala aims at reverse-engineering any relational database (jdbc-based) into a CRUD Play Application targetting the Play 2.1 release (with Scala 2.10) and the Slick Query and DB Access Library. Since this is a prototype, the scope will be limited to demonstrate the conversion of a very small mysql database into a Play App running an H2 in-memory db.
This functionality is often very useful to get started quickly to use a new stack into an existing industrial application (since most of the legacy lies in the DB).
The application has only external dependencies to the database jdbc connectors  (here mysql-connector library).
It is inspired and built upon 2 already existing samples, one being the sample Computer-Database part of the Play Framework distribution (which exhibits a CRUD app but with Anorm), the other being a sample of usage of Slick done by `Typesafe's Slick Team <http://slick.typesafe.com/>`_ (the Coffee database with its Suppliers showing 1-to-many relationships).
