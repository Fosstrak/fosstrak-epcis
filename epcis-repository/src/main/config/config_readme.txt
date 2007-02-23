TODO ... what needs to be configured, where are the config files, ...


- context.xml in webapp/META-INF/
- properties in captureclient, queryclient

- Set password for tomcat manager in
	 M2_HOME%/conf/settings.xml in server section
	 
	 eg. 	<server>
        	    <id>tomcat-manager</id>
        	    <username>admin</username>
        	    <password>admin</password>
    		</server>