infoScoop OpenSource 3.1.0
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


How to migrate from version 3.0.0 or version 3.1.0
--------------------------------------------------
To migrate from version 3.0.0 to 3.1.0, follow the steps below.

1. if the static content URL is set, replacement of  the static content files is
  necessary. Replace the directory where static contents are stored currently to
  'infoscoop/staticContent' directory.

2. Update repository database.

  (1). Open command prompt, change directory to tools/initdb
  (2). Copy the suitable JDBC Driver to lib dir.
  (3). Execute following command:
     >import.sh(bat) I18N

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

6. Redeploy infoscoop.war to WebApplication Server.

7. Search form and a userID(userName) display are moving to the command bar from this version. We recommend deleting a corresponding
   section from a custom header area. 
   Please delete the following HTML from "Administration page > Layout > Other Layout > header". 

   <td>
	<form name="searchForm" onsubmit="javascript:IS_Portal.SearchEngines.buildSearchTabs(document.getElementById('searchTextForm').value);return false;">
	<div style="float:right;margin-right:5px">
		<table>
			<tbody>
				<tr>
					<td colspan="2" align="right" style="font-size:80%;">
						<#if session.getAttribute("Uid")??>%{lb_welcome}${session.getAttribute("loginUserName")}%{lb_san}
						<#else><a href="login.jsp">%{lb_login}</a>
						</#if>
					</td>
				</tr>
				<tr>
					<td>
						<input id="searchTextForm" type="text" style="width:200px;height:23px;float:left;"/>
						<input type="submit" value="%{lb_search}" style="padding:0 0.4em;"/>
						<span id="editsearchoption">%{lb_searchOption}</span>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
	</form>
   </td>

   The above is a case of a default header. Please delete related HTML, if you are customizing. 


License and Copyright
---------------------

This code is licensed under the **GNU Lesser General Public License (LGPL) v3**.
Please see LICENSE.txt for licensing and copyright information.


Changes from Version 3.0.0 to 3.1.0
-----------------------------------
Refer to the URL below.
https://code.google.com/p/infoscoop/issues/list?can=1&q=milestone=3.1.0