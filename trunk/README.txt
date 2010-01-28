infoScoop OpenSource 2.0.0 RC2
============================================

What is it?
-----------------------------------------
infoScoop OpenSource is information portal that evolute according with personal work style.

For more information, please see the infoScoop OpenSource website at http://www.infoscoop.org/.


Installation
-----------------------------------------
Please refer to the following URL.
http://www.infoscoop.org/index.php/manual/quick-start.html


How to update to RC2 from an old version.
-----------------------------------------
1. Redeploy infoscoop.war to WebApplication Server according to Quick Start.

2. Replace gadget files in the repository database.

  (1). Open SQL executable tool.
  (2). Execute following SQL command.
     > delete from IS_GADGETS where type in ('calc','blogparts','todoList','alarm','sticky','worldclock')
     > delete from IS_GADGET_ICONS where type in ('calc','blogparts','todoList','alarm','sticky','worldclock')
  (3). Open command pronpt, change directory to tools
  (4). Copy the suitable JDBC Driver to lib dir.
  (5). Modify bin/datasource.xml to set database appropriately.
  (6). Execute following ccd ommand:
     >import.sh(bat) GADGET,GADGETICON

[Warning]Executing steps above, following gadgets settings is initialized:

* calc
* todoList
* alarm
* blogparts
* sticky
* worldclock


Licensing and Copyright
---------------------------------------------

This code is licensed under the **GNU Lesser General Public License (LGPL) v3**. Please see
LICENSE.txt for licensing and copyright information.


Changes from 2.0.0-RC1 to current 2.0.0-RC2 version.
---------------------------------------------
Please refer to the following URL.
http://code.google.com/p/infoscoop/issues/list?can=1&q=label%3AMilestone-2.0-RC2