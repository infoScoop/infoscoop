infoScoop OpenSource 4.0.0
==========================

About infoScoop OpenSource
--------------------------
"infoScoop OpenSource" is information portal that evolve according with
 personal work style. This portal provides important information for individuals
 from business system and huge information in or out of the company. It implements
 free arrangement of information and fits to individual information processing
 skill and work style.

For more information, please see the infoScoop OpenSource web site at
http://www.infoscoop.org/.


Installation Requirements
-------------------------
These are requirements that needs before the setup of infoScoop OpenSource.

・Java SE 7.0
・MySQL	5.1


How to Setup
------------
Unzip infoscoop-4.0.0-quickstart.zip(tar.gz) and follow the steps below.

1. Create Repository Database

Create a database for infoScoop OpenSource with mysql console.

$mysql -uroot
mysql>create database iscoop character set utf8;
mysql>exit

2. Changing database environmental settings
* When using MySQL for a repository database, it can certainly carry out. 

Add the following setup to the configuration file "my.cnf" of MySQL.
***************
[mysqld]
lower_case_table_names = 1
***************

3. Import Initial Data to Repository Database

Execute the following commands which inserts initial data to the created database.

$ mysql -uroot iscoop < infoscoop-4.0.0-quickstart/init_infoscoop.sql

* Choose 'init_info_infoscoop.ja.sql' file, if you want Japanese sample settings.

4. Settings of database

In default settings, MySQL uses the same server with default port 3306,
user ID "root" and no password. If other settings are need to change,
database settings should be changed.
Database settings can be changed in infoscoop.xml which is in the directory below.

apache-tomcat-7.0.34/conf/Catalina/localhost/infoscoop.xml

Change Resource element of GlobalNamingResources element.

・username: user ID to connect to the database
・password: password to connect to the database
・url: url to connect to the database.
  When connecting to a MySQL server of an another server,change "localhost" to
  a proper host name. Besides, If default port is not used, "3306" must be changed
  to proper value as well.

5. Start up infoScoop OpenSource Server

Execute the following command.

$ startup.bat(sh)

That is how to setup infoScoop OpenSource Quickstart.

To stop it, execute the following command.

$ shutdown.bat(sh)

6. Startup infoScoop OpenSource

Start a browser, and access to the following url.

http://<hostname>:8080/infoscoop/

The login page is displayed. The user is only admin/admin in the initial state.


How to Add Users
----------------
1. Edit import.csv

Edit the following file.

infoscoop-4.0.0-quickstart/initdb/data/accounts/import.csv

Add new users with the following format.

<USER_ID>,<USER_NAME>,<PASSWORD>

2. Execute addaccount.bat(sh)

Move to 'initdb' directory, and execute 'addaccount.bat(sh)' file.

$ cd infoscoop-4.0.0-quickstart/initdb
$ addaccount.bat(sh)


License and Copyright
---------------------

This code is licensed under the **GNU Lesser General Public License (LGPL) v3**.
Please see LICENSE.txt for licensing and copyright information.

Changes from Version 3.4.0 to 4.0.0
-----------------------------------
Refer to the URL below.
https://github.com/infoScoop/infoscoop/issues?q=milestone%3AMilestone-4.0.0+is%3Aclosed