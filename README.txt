infoScoop OpenSource 2.1.0 RC1
===============================

About infoScoop OpenSource
--------------------------
"infoScoop OpenSource" is information portal that evolute according with personal work style.This portal provides important information for individuals from business system and huge infromation in or out of the company. It implement  free arrangement of information and fits to individual information processing skill and work style.

For more information, please see the infoScoop OpenSource website at http://www.infoscoop.org/.


How to Install
---------------
Refer to the URL below.
http://www.infoscoop.org/index.php/manual/quick-start.html


How to migrate from version 2.0
-----------------------------
To migrate from version 2.0 to 2.1 RC1, follow the steps below.

[Transfer Data]
It is strongly recommended that the following steps should be done after backing up the database data.

Note that Executing migration tool overwrite the following data with initial data.
・Header in Other Layout
・CSS in Other Layout
・Gadget settings for alarm, blogparts, calculator, sticker and TODOList
These data must be manually transfered. 
Copy and save them referring to those in administration page before migration operation.
After the migration, edit those properly again in administration page

1. Edit migration.properties for database connection settings.

DBMS=mysql
DATABASE_URL=jdbc:mysql://localhost:3306/iscoop
#SCHEMA=iscoop
USER=root
PASSWORD=
#TABLESPACE=

1)DBMS: Specify one of these; mysql, oracle, db2.
2)DATABASE_URL: Specify a URL to connect database with JDBC.
3)SCHEMA: If the value is empty, the schema is to be the same as the user name. Note that this should not be set in case of MySQL.
4)USER: Specify a database connection user.
5)PASSWORD: Specify a database connection password.
6)TABLESPACE: Option that is only for DB2, which specifies tablespace.

2. Copy the proper JDBC driver of the using DMBS to lib directory.
(As for MySQL, the driver is included beforehand so skip this step.)

3. Execute migration tool
Execute migration.bat(sh).

4. Delete backup table

After executing the migration tool, the operation creates a backup table that has a suffix named "_bak20".
Make sure that migration is properly done and delete the backup table by the following command.

$ cleanup_temp_table.bat(sh)


[Update Applications]
1. Replace WAR file. As for the way to update a web application, refer to each manual of web application server.
2. Change static contents if it is set. Replace the directory in which static contents are placed with infoscoop/staticContent.


License and Copyright
---------------------

This code is licensed under the **GNU Lesser General Public License (LGPL) v3**. Please see
LICENSE.txt for licensing and copyright information.


Changes from Version 2.0.1 to 2.1.0
-----------------------------------
Refer to the URL below.
http://code.google.com/p/infoscoop/issues/list?can=1&q=label%3DMilestone-2.1.0