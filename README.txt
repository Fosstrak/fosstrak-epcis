---------------------
Project Configuration
---------------------

the maven tomcat plugin uses the tomcat-manager application
the tomcat-manager requires authentication
edit %M2_HOME%/conf/settings.xml

and add a <server> entry at the corresponding location in the xml:
<server>
  <id>tomcat-manager</id>
  <username>your-tomcat-usr</username>
  <password>your-tomcat-pwd</password>
</server>


-----------
Maven Goals
-----------

mvn clean               - for cleaning the project
mvn compile             - for compiling

mvn tomcat:deploy       - deploying to tomcat
mvn tomcat:undeploy     - undeploying from tomcat


-----------------
Eclipse IDE Setup
-----------------

to add the classpath variable M2_HOME to eclipse, run

mvn -Declipse.workspace=<path-to-eclipse-workspace> eclipse:add-maven-repo

pointing it to the location of your eclipse workspace folder
in order to generate eclipse configuration files (.classpath, .settings) run

mvn eclipse:eclipse

then open eclipse and run
File -> Import ...
select "Existing Project into Workspace"
browse to the root directory of the maven project
in the lower window you should see all project modules listed
select all and click "Finish"