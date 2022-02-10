# From Hibernate to OpenJPA
OpenJPA is another JPA implementation supported by Spring Data JPA. OpenJPA facilitates Entity enhancement at different stages:
- Build time
- Deploy time
- Run-time
- Dynamically at run-time

# Maven Project Settings
## Additional Dependencies
In order to get rid of Hibernate JPA, you have to exclude the Hibernate in the dependency definition of spring data jpa.
Besides, a dependency to OpenJPA is naturally required.

```
 <dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
        <exclusions>
            <exclusion>
                <artifactId>hibernate-entitymanager</artifactId>
                <groupId>org.hibernate</groupId>
            </exclusion>
        </exclusions>
    </dependency>
    <dependency>
        <groupId>org.apache.openjpa</groupId>
        <artifactId>openjpa</artifactId>
        <version>2.4.1</version>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
    </dependency>
    ...
 </dependencies>
```
## Plugins
To enable build time enhancement, an extra OpenJPA maven plugin is required:

```
<build>
    <plugins>
      <plugin>
        <groupId>org.apache.openjpa</groupId>
        <artifactId>openjpa-maven-plugin</artifactId>
        <version>2.4.1</version>
        <configuration>
            <includes>**/domain/*.class</includes>
            <!--excludes>**/entities/XML*.class</excludes-->
            <addDefaultConstructor>true</addDefaultConstructor>
            <enforcePropertyRestrictions>true</enforcePropertyRestrictions>
        </configuration>
        <executions>
            <execution>
                <id>enhancer</id>
                <phase>process-classes</phase>
                <goals>
                    <goal>enhance</goal>
                </goals>
            </execution>
        </executions>
      </plugin>
      ...
    </plugins>
</build>
```

As the `pom.xml` snippet shows, the build time enhancement takes place in the `process-classes` lifecycle phase, right after the `compile` phase.
Unfortunately, this build plugin assumes the existence of a `META-INF/persistence.xml` file or some other files located under different paths and with different file names,
 which have to adhere to the same format as defined in the JPA spec for persistence unit definition. If different file names and paths are used, you can specify them along with the
 plugin configurations above in the `pom.xml` file.

Actually, the plugin assumes the mere existence of the `persistence.xml` file, which contains an empty `persistence-unit` section. Entity classes to be enhanced can be specified
 along with the maven plugin as shown above. For the OpenJPA run time within Spring Data JPA, Java configuration as described below can be used to specify the entities the persistence unit consists of.

```
<?xml version="1.0"?>
<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd"
             version="2.1">
    <persistence-unit name="XYZ">
    </persistence-unit>
</persistence>
```

Another inconvenience is that this plugin insists of validating the XML file by downloading schema XSD from the URL. There is no way to opt out the validation.
And this is quite annoying, if you are behind a proxy server or even be cut off from the network. In case of being behind a proxy, you can help yourself by adding the Java properties to the
 maven command as follows:
```
mvn process-classes -Dhttp.proxyHost=XYZ, -Dhttp.proxyPort=8080
```
or
```
mvn openjpa:enhance -Dhttp.proxyHost=XYZ, -Dhttp.proxyPort=8080
```

# Configuration of Bean Injection
As mentioned above, the usage of H2 database and OpenJPA needs to be specified dedicatedly. Since there are only a few settings required,
we put all of them into a single class `com.sap.sptutorial.jpa.config.JpaConfig`.

## Specifying a dedicated Database
For using a different database than HSQLDB, a Bean of the type `javax.sql.DataSource` has to be exposed. Sometimes,
it makes sense to have a standalone Database server instead of an embedded server, which an external Database client tool can connect to for inspections. 
For this purpose, two different profiles are applied, where the _default_ profile annotation is attached to the embedded server. 

## Specifying a dedicated JPA Implementation

### JPA Vendor
First of all, a Bean of the type `JpaVendorAdapter` must be exposed, which take on the particular JPA vendor in question. In our case, it's the
`org.springframework.orm.jpa.vendor.OpenJpaVendorAdapter`.

### Entity Manager Factory
During the start-up, the spring-data-jpa seeks for an Bean with the name `entityManagerFactory` with which further settings such as Database and JPA implementation etc. can be done.
Since we require a container managed Entity Manager, a `LocalContainerEntityManagerFactoryBean` is instantiated. Normally, an Entity Manager corresponds to a _persistence unit_
specified in the `persistence.xml` file. A persistence unit comprises all entity classes (or tables in the corresponding schema) managed by this entity manager. Instead of specifying
the persistence unit name, one can also provide the package names which include the entity classes in question by invoking the factory method `setPackagesToScan`.

# Entity Names
Different JPA implementations map the Entity and attribute names differently to the database table and column names. This makes the work with import scripts inconvenient. It makes
 sense to use JPA annotations ```@Table``` and ```@Colunm``` to fix the mapping manually.
 
# Schema Initialization
Hibernate generates DDLs according to Entity definitions and apply them by default. Then, if a ```import.sql``` script file is available, the script will be executed as well to import
 the initial data set.

EclipseLink goes differently. If there is a ```schema.sql``` script file available, which includes the DDL and insert statements, this script will be executed on start-up.
Or you have to specify DDL generations with the ```EclipseLinkJpaVendorAdapter``` instance and a SQL script file with a name different than ```schema.sql``` which includes
 insert statements of the initial data set. To make sure, that this script gets executed after the DDL statements have been executed, you can add a start-up hook to the SpringApplication:
```
@SpringBootApplication
public class JpaApplication {

  @Value("classpath:import.sql")
  private Resource importSqlScript;

  public static void main(String[] args) throws Exception {
    SpringApplication.run(JpaApplication.class, args);
  }

  @Bean
  CommandLineRunner init(DataSource ds) {
    return args -> {
      org.springframework.jdbc.datasource.init.ScriptUtils.executeSqlScript(ds.getConnection(), importSqlScript);
    };
  }

}
```
