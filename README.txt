infoScoop OpenSource 3.0.0
========================

About infoScoop OpenSource
--------------------------
"infoScoop OpenSource" is information portal that evolute according with
personal work style.This portal provides important information for individuals
from business system and huge information in or out of the company. It implement
free arrangement of information and fits to individual information processing
skill and work style.

For more information, please see the infoScoop OpenSource web site at
http://www.infoscoop.org/.


How to Install
--------------
Refer to the URL below.
http://www.infoscoop.org/index.php/manual/quick-start.html


How to migrate from version 2.2.0 or version 2.2.1
--------------------------------------------------
To migrate from version 2.2.0 or version 2.2.1 to 2.2.3, follow the steps below.

1. if the static content URL is set, replacement of  the static content files is
  necessary. Replace the directory where static contents are stored currently to
  'infoscoop/staticContent' directory.

2. Update repository database.

  (1)Open import.csv file in tools/initdb/data/widgetconfig and delete 11 lines
     to edit as below.

       "Message",<LOB FILE='Message.xml' />

  (2). Open command prompt, change directory to tools/initdb
  (3). Copy the suitable JDBC Driver to lib dir.
  (4). Execute following command:
     >import.sh(bat) GADGET GADGETICON I18N WIDGETCONF

  [Warning]Executing steps above, following gadgets settings is initialized:
    * calc
    * todoList
    * alarm
    * blogparts
    * sticky
    * worldclock
    * message

  (5). If MySQL is being used, execute following SQL command.
     mysql>ALTER TABLE is_widgets MODIFY COLUMN `UID` VARCHAR(150) NOT NULL;

3. Edit tools/migration/migration.properties for database connection settings.

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

4. Copy the proper JDBC driver of the using DMBS to lib directory.
  (As for MySQL, the driver is included beforehand so skip this step.)

5. Execute migration tool
  Execute migration.bat(sh).

6. Delete backup table

  After executing the migration tool, the operation creates a backup table that has a suffix named "_bak223".
  Make sure that migration is properly done and delete the backup table by the following command.

  $ cleanup_temp_table.bat(sh)

7. Redeploy infoscoop.war to WebApplication Server.

How to migrate from version 2.2.2
--------------------------------------------------
To migrate from version 2.2.2 to 2.2.3, follow the steps below.

1. if the static content URL is set, replacement of  the static content files is
  necessary. Replace the directory where static contents are stored currently to
  'infoscoop/staticContent' directory.

2. Update repository database.

  (1). Open command prompt, change directory to tools/initdb
  (2). Copy the suitable JDBC Driver to lib dir.
  (3). Execute following command:
     >import.sh(bat) GADGET I18N

  [Warning]Executing steps above, following gadgets settings is initialized:
    * calc
    * todoList
    * alarm
    * blogparts
    * sticky
    * worldclock
    * message

  (4). If MySQL is being used, execute following SQL command.
     mysql>ALTER TABLE is_widgets MODIFY COLUMN `UID` VARCHAR(150) NOT NULL;

3. Edit tools/migration/migration.properties for database connection settings.

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

4. Copy the proper JDBC driver of the using DMBS to lib directory.
  (As for MySQL, the driver is included beforehand so skip this step.)

5. Execute migration tool
  Execute migration.bat(sh).

6. Delete backup table

  After executing the migration tool, the operation creates a backup table that has a suffix named "_bak223".
  Make sure that migration is properly done and delete the backup table by the following command.

  $ cleanup_temp_table.bat(sh)

7. Redeploy infoscoop.war to WebApplication Server.


License and Copyright
---------------------

This code is licensed under the **GNU Lesser General Public License (LGPL) v3**.
Please see LICENSE.txt for licensing and copyright information.


Changes from Version 2.1.1 to 2.2
---------------------------------
Refer to the URL below.
https://code.google.com/p/infoscoop/issues/list?can=1&q=milestone=2.2.0

Changes from Version 2.2.0 to 2.2.1
-----------------------------------
Refer to the URL below.
https://code.google.com/p/infoscoop/issues/list?can=1&q=milestone=2.2.1

Changes from Version 2.2.1 to 2.2.2
-----------------------------------
Refer to the URL below.
https://code.google.com/p/infoscoop/issues/list?can=1&q=milestone=2.2.2

Changes from Version 2.2.2 to 2.2.3
-----------------------------------
Refer to the URL below.
https://code.google.com/p/infoscoop/issues/list?can=1&q=milestone=2.2.3

Changes from Version 2.2.3 to 3.0.0
-----------------------------------
Refer to the URL below.
https://code.google.com/p/infoscoop/issues/list?can=1&q=milestone=3.0.0