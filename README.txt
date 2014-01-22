infoScoop OpenSource 3.3.0-beta
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


How to migrate from version 3.1.1
--------------------------------------------------
To migrate from version 3.1.1 to 3.3.0-beta, follow the steps below.

1. Redeploy infoscoop.war to WebApplication Server.

* When employment server is Linux and a repository database is MySQL,
  a shift procedure is changed as follows. 

1. Change into a small letter all the names of the table stored in the repository database.

2. Add the following setup to the configuration file "my.cnf" of MySQL.
***************
[mysqld]
lower_case_table_names = 1
***************

3. Redeploy infoscoop.war to WebApplication Server.


License and Copyright
---------------------

This code is licensed under the **GNU Lesser General Public License (LGPL) v3**.
Please see LICENSE.txt for licensing and copyright information.


Changes from Version 3.1.1 to 3.3.0-beta
-----------------------------------
Refer to the URL below.
https://github.com/infoScoop/infoscoop/issues?milestone=24&state=closed