infoScoop OpenSource 2.1.0
==========================

About infoScoop OpenSource
--------------------------
"infoScoop OpenSource" is information portal that evolute according with personal work style.This portal provides important information for individuals from business system and huge infromation in or out of the company. It implement  free arrangement of information and fits to individual information processing skill and work style.

For more information, please see the infoScoop OpenSource website at http://www.infoscoop.org/.


How to Install
---------------
Refer to the URL below.
http://www.infoscoop.org/index.php/manual/quick-start.html

How to Setup
----------------
Unzip infoscoop-2.1.0-quickstart.zip and follow the steps below.

1. Create Repository Database

Create a database for infoScoop OpenSource with mysql console.

$mysql -uroot
mysql>create database iscoop character set utf8;
mysql>exit

2. Import Initial Data to Repository Database

execute the following commands which inserts initial data to the created database.

$ mysql -uroot iscoop < infoscoop-2.1.0-quickstart/init_infoscoop.sql

3. Settings of database

In default settings, MySQL uses the same server with default port 3306, user ID "root" and no password.
If other settings are need to change, database settings should be changed.
Database settings can be changed in infoscoop.xml which is in the directory below.

apache-tomcat-6.0.28/conf/Catalina/localhost/infoscoop.xml

Change Resource element of GlobalNamingResources element.

・username: user ID to connect to the database
・password: password to connect to the database
・url: url to connect to the database. 
  When connecting to a MySQL server of an another server,change "localhost" to a proper host name.
  Besides, If default port is not used, "3306" must be changed to proper value as well.

4. Start up infoScoop OpenScorce Server

Execute the following command.

$ startup.bat(sh)

That is how to setup infoScoop OpenScorce Quickstart.

To stop it, execute the following command.

$ shutdown.bat(sh)

5. Startup infoScoop OpenScorce

Start a browser, and access to the following url.

http://<hostname>:8080/infoscoop/

The login page is displayed. The user is only admin/admin in the initial state.

New users can be added at the administration page.
To display the administration screen, access to the following url.

http://<hostname>:8080/infoscoop/admin


License and Copyright
---------------------

This code is licensed under the **GNU Lesser General Public License (LGPL) v3**. Please see
LICENSE.txt for licensing and copyright information.


Changes from Version 2.0.1 to 2.1.0
-----------------------------------
Refer to the URL below.
http://code.google.com/p/infoscoop/issues/list?can=1&q=label%3AMilestone-2.1.0+label%3ADefect+OR+label%3AMilestone-2.1.0++label%3AEnhancement