# Datasource Configuration over JNDI
## Configuration on the Server
### Local Tomcat
Using Tomcat8.5, PostgreSQL

#### server.xml

```
<GlobalNamingResources>
  <Resource name="UserDatabase" auth="Container" ... />

  <Resource name="jdbc/DefaultDB" auth="Container" type="javax.sql.DataSource"
            username="D022051" password="test"
            url="jdbc:postgresql://localhost:5432/jnditest" driverClassName="org.postgresql.Driver"
            initialSize="20" maxWaitMillis="15000" maxTotal="75" maxIdle="20"
            maxAge="7200000" testOnBorrow="true" validationQuery="select 1" />
</GlobalNamingResources>
```

#### context.xml

```
<Context>
    <WatchedResource>WEB-INF/web.xml</WatchedResource>
    <WatchedResource>${catalina.base}/conf/web.xml</WatchedResource>

    <ResourceLink name="jdbc/DefaultDB"
                global="jdbc/DefaultDB"
                auth="Container"
                type="javax.sql.DataSource" />
</Context>
```

### HCP/Classic
Use [Hana Cockpit](https://account.hanatrial.ondemand.com/cockpit)

#### Database
Under `Databases & Schemas` create new Database (shared Hana)

#### Database Binding to Application
Under `Data Source Binding`create new Binding. Name of Datasource `<Default>``

## Application Enablement

### web.xml

```
<web-app id="WebApp_ID" version="3.1"
    xmlns="http://xmlns.jcp.org/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd">
    <welcome-file-list>
    ...
    </welcome-file-list>
    <resource-ref>
        <res-ref-name>jdbc/DefaultDB</res-ref-name>
        <res-type>javax.sql.DataSource</res-type>
    </resource-ref>
</web-app>
```

### application.yml

```
spring:
  datasource:
    jndi-name: java:comp/env/jdbc/DefaultDB
```

### Example Use
_JndiController.java_

```
@RestController
@RequestMapping("ds")
public class JndiController {
	private final DataSource dataSource;
	public JndiController(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
```
