infoScoop OpenSource 2.0.1
==========================

What is it?
-----------
infoScoop OpenSource is information portal that evolute according with personal work style.

For more information, please see the infoScoop OpenSource website at http://www.infoscoop.org/.


Installation
------------
Please refer to the following URL.
http://www.infoscoop.org/index.php/manual/quick-start.html


How to update to 2.0.1 from an old version.
-----------------------------------------
1. Redeploy infoscoop.war to WebApplication Server according to Quick Start.

2. Replace gadget files in the repository database and update i18n resources.

  (1). Open SQL executable tool.
  (2). Execute following SQL command.
     > delete from IS_GADGETS where type in ('calc','blogparts','todoList','alarm','sticky','worldclock')
     > delete from IS_GADGET_ICONS where type in ('calc','blogparts','todoList','alarm','sticky','worldclock')
  (3). Open command pronpt, change directory to tools/initdb
  (4). Copy the suitable JDBC Driver to lib dir.
  (5). Execute following command:
     >import.sh(bat) GADGET,GADGETICON,I18N

  [Warning]Executing steps above, following gadgets settings is initialized:
    * calc
    * todoList
    * alarm
    * blogparts
    * sticky
    * worldclock

3. if the static content URL is set, replacement of  the static content files is necessary.
  Replace the directory where static contents are stored currently to 'infoscoop/staticContent' directory.


Licensing and Copyright
-----------------------

This code is licensed under the **GNU Lesser General Public License (LGPL) v3**. Please see
LICENSE.txt for licensing and copyright information.


Changes from 2.0.0 to current 2.0.1 version.
--------------------------------------------
Please refer to the following URL.
http://code.google.com/p/infoscoop/issues/list?can=1&q=label%3DMilestone-2.0.1