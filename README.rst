Reverse engineer a jdbc database into a scala Play 2 CRUD (Create Read Update Delete) Web App
=============================================================================================

The application written in Scala aims at reverse-engineering any relational database (jdbc-based) into a CRUD Play Application targetting the Play 2.1 release (with Scala 2.10) and the Slick Query and DB Access Library. Since this is a prototype, the scope will be limited to demonstrate the conversion of a very small mysql database into a Play App running an H2 in-memory db.
This functionality is often very useful to get started quickly to use a new stack into an existing industrial application (since most of the legacy lies in the DB).
The application has only external dependencies to the database jdbc connectors  (here mysql-connector library).
It is inspired and built upon 2 already existing samples, one being the sample Computer-Database part of the Play Framework distribution (which exhibits a CRUD app but with Anorm), the other being a sample of usage of Slick done by `Typesafe's Slick Team <http://slick.typesafe.com/>`_ (the Coffee database with its Suppliers showing 1-to-many relationships).

Requirements
------------

 - `sbt <https://github.com/harrah/xsbt>`_ 0.12.x
 - play 2.1 (based on scala 2.10)
 - A jdbc DB engine (we use MySql in this prototype)


Getting Started
---------------

In this tutorial 
- the Source will be a very small MySql Database ( A DB containing 2 tables,  Suppliers and Coffees with a 1-to-many relation between them).
- the Target will be a Play2 app running on localhost port 9000.

Step 1 - Install a sample Database (the source)
-----------------------------------------------

A sample database is available under ./sql/ , it is a mysql dump script called slick.sql
Either open your favorite Graphical DB Tool , create a schema called [database_name] (e.g. 'slick') and run the script in it,
or use mysqldump command utility::

> mysql -u root -p[root_password] [database_name] < ./sql/slick.sql


Step 2 - Create a Play empty app (the target)
---------------------------------------------

In a directory of your choice, <target_basedir>, for example /tmp::

> play new demo

.. image:: doc/images/new_play_app.png

:: > cd demo
:: > play run
Browse to http://localhost:9000 to make sure the play app is running


Step 3 - Configuring and Running the generator
----------------------------------------------
Open a new command terminal, and go to the root of the cruplay project you have been cloning::

> play
> run 9090  (or any other port different from 9000, since the target app is running there)

Browse to http://localhost:9090