# Ping Test
```
http(s)://<host>:<port>/hello/?name=XYZ
http(s)://<host>:<port>/ping/?name=XYZ
http(s)://<host>:<port>/hello/?name=
http(s)://<host>:<port>/ping/?name=
```

# Deployment onto CF@HCP with PostgreSQL

So far this application was implemented with an embedded H2 database for local test purposes. If you want to deploy
it to a Cloud environment such as _Cloud Foundry_ for productive usage, the spring-cloud provides a great deal of support
to relieve developers from configuration adjustments by applying default configurations of a particular Cloud environment
in question. Of course, it is always possible to alter these default configurations step by step later on.

__Theoretically__, the application can be deployed onto a Cloud Foundry runtime environment with another well-known database,
e.g. _PostgreSQL_ or _HANA_ without any changes, if the database is supported.

CF@HCP provides a database service for _PostgreSQL_. To use PostgreSQL instead of H2Database on CF@HCP, you have to subscribe
 the service first:
 ```
 cf create-service postgresql-[9.4-lite*] free myPostgres
 ```

 The preceding command signs up for the _free_ plan of the PostgreSQL database provided on CF@HCP. The service instance is bound
 to the logical name _myPostgres_. Once you deploy the app, you can either use the bind-service command or the deployment
 manifest file to specify the service binding.


```

---
applications:
- name: myapp
  path: ...
  buildpack: java_buildpack
  memory: 512M
  instances: 1
  host: myapp
  domain: cfapps.sap.hana.ondemand.com
  services:
    - myPostgres

```

You can browse the service offers using: ```cf marketplace```. You can browser your own service subscriptions using: ```cf services```.

Unfortunately, as mentioned before, it is only a theoretical statement, that no adjustments are required in case of exchanging the underlying
database for the Cloud deployment. First of all, you have to make sure, that you stick to the relevant standards, e.g. SQL, JDBC, in your codes.
It is obvious, that the database exchange will fail, if incompatible syntax or data types or similar things are involved. For instance,
the JDBC driver of H2 copes with String-typed parameters for non-String columns in prepared statements, such as ```SELECT COUNT(*) FROM PLAYERS WHERE ID = ?```,
whereas the ID column is of integer type. In contrast, the same prepared statement will not work in case of Postgres' JDBC driver
which strictly requires integer-typed parameters.

Beside the requirement of database compatibilities, there are also other inconveniences sometimes caused by different bugs in different versions.
The spring-cloud module is loaded by the Java build pack of Cloud Foundry, as the build pack detects that the app being deployed is a Spring application.
This is basically the spring-cloud auto configuration module, which in turn figures out the relevant JDBC driver and load the driver accordingly. All these
are supposed to work without any additional settings.

In our case, we deploy the application (which is written for H2 database) as it is onto Cloud Foundry by merely tell CF to bind the app
to the Postgres service instance. By this, spring-cloud auto configuration should load the Postgres JDBC driver and ignore the uploaded H2 JDBC driver.
As said, __this is only how it is theoretically supposed to work__. (It had indeed worked that way a while ago.) Due to some bugs in the recent versions
of either CF or Spring-cloud, it fails in the current HCP settings. As a workaround, one has to add the following dependencies to the project and upload them
along with the application bundle being deployed.

```
 <dependencies>
    ...
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-cloudfoundry-connector</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-spring-service-connector</artifactId>
        <scope>runtime</scope>
    </dependency>
    ...
 </dependencies>
```
