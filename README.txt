infoScoop OpenSource 2.2
========================

About infoScoop OpenSource
--------------------------
"infoScoop OpenSource" is information portal that evolute according with personal work style.This portal provides important information for individuals from business system and huge infromation in or out of the company. It implement  free arrangement of information and fits to individual information processing skill and work style.

For more information, please see the infoScoop OpenSource website at http://www.infoscoop.org/.


How to Install
---------------
Refer to the URL below.
http://www.infoscoop.org/index.php/manual/quick-start.html


How to migrate from version 2.1.0
---------------------------------
To migrate from version 2.1.0 to 2.2, follow the steps below.

1. Replace gadget files in the repository database and update i18n resources.

  (1). Open SQL executable tool.
  (2). Execute following SQL command.
     > delete from IS_GADGETS where type in ('calc','blogparts','todoList','alarm','sticky','worldclock')
     > delete from IS_GADGET_ICONS where type in ('calc','blogparts','todoList','alarm','sticky','worldclock')
  (3). Open command pronpt, change directory to tools/initdb
  (4). Copy the suitable JDBC Driver to lib dir.
  (5). Execute following command:
     >import.sh(bat) GADGET GADGETICON I18N

  [Warning]Executing steps above, following gadgets settings is initialized:
    * calc
    * todoList
    * alarm
    * blogparts
    * sticky
    * worldclock

2. if the static content URL is set, replacement of  the static content files is necessary.
  Replace the directory where static contents are stored currently to 'infoscoop/staticContent' directory.

3. Redeploy infoscoop.war to WebApplication Server.

How to migrate from version 2.1.1
---------------------------------
To migrate from version 2.1.1 to 2.2, follow the steps below.

1. Replace gadget files in the repository database and update i18n resources.

  (1). Open command pronpt, change directory to tools/initdb
  (2). Copy the suitable JDBC Driver to lib dir.
  (3). Execute following command:
     >import.sh(bat) I18N

2. if the static content URL is set, replacement of  the static content files is necessary.
  Replace the directory where static contents are stored currently to 'infoscoop/staticContent' directory.

3. Redeploy infoscoop.war to WebApplication Server.


License and Copyright
---------------------

This code is licensed under the **GNU Lesser General Public License (LGPL) v3**. Please see
LICENSE.txt for licensing and copyright information.


Changes from Version 2.1.1 to 2.2
---------------------------------
Refer to the URL below.
https://code.google.com/p/infoscoop/issues/list?can=1&q=milestone=2.2.0