infoScoop OpenSource 2.2.2
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


How to migrate from version 2.1.0
---------------------------------
To migrate from version 2.1.0 to 2.2, follow the steps below.

1. Update repository database.

  (1)Open import.csv file in tools/initdb/data/widgetconfig and delete 11 lines
     to edit as below.

       "Message",<LOB FILE='Message.xml' />

  (2). Open command pronpt, change directory to tools/initdb
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

2. If the staticContentUrl property is set, replacement of the static content
   files is necessary. Replace the directory where static contents are stored
   currently to 'infoscoop/staticContent' directory.

3. Redeploy infoscoop.war to WebApplication Server.

How to migrate from version 2.1.1
---------------------------------
To migrate from version 2.1.1 to 2.2, follow the steps below.

1. Update repository database.

  (1)Open import.csv file in tools/initdb/data/widgetconfig and delete 11 lines
     to edit as below.

       "Message",<LOB FILE='Message.xml' />

  (2). Open command prompt, change directory to tools/initdb
  (3). Copy the suitable JDBC Driver to lib dir.
  (4). Execute following command:
     >import.sh(bat) I18N WIDGETCONF

2. If the staticContentUrl property is set, replacement of the static content
   files is necessary. Replace the directory where static contents are stored
   currently to 'infoscoop/staticContent' directory.

3. Redeploy infoscoop.war to WebApplication Server.

How to migrate from version 2.2.0 or version 2.2.1
--------------------------------------------------
To migrate from version 2.2.0 or 2.2.1 to 2.2, follow the steps below.

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
     >import.sh(bat) I18N WIDGETCONF

3. Redeploy infoscoop.war to WebApplication Server.


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