# EclipseLink vs. Hibernate within Spring Boot
Hibernate, which is released under LGPL, is the default JPA implementation used by Spring Boot. Thus, to use Hibernate
you only have to add a maven dependency to spring-data-jpa in your ```pom.xml```. No additional settings need to be done,
spring boot simply launches an embedded HSQLDB instance as the underlying data source and Hibernate on top for JPA.

In order to use another Database, you have to take care of specifying a particular Bean for the dedicated Database, regardless of embedded, standalone or Cloud service via service binding.

# Maven Project Settings
## Additional Dependencies
In order to get rid of Hibernate JPA, you have to exclude the Hibernate in the dependency definition of spring data jpa.
The additional dependency specification with eclipselink makes sure that EclipseLink is used instead. 

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
        <groupId>org.eclipse.persistence</groupId>
        <artifactId>eclipselink</artifactId>
        <version>2.6.2</version>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
    </dependency>
    ...
 </dependencies>
```
## Plugins
Since EclipseLink uses so-called Load-time Weaving (LTW) for byte-code manipulation, you have to specify an apprepriate load-time byte-code weaver
for the spring-boot plugin and maven surefire plugin which is used for unit testing.

```
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <dependencies>
                <dependency>
                    <groupId>org.springframework</groupId>
                    <artifactId>spring-instrument</artifactId>
                    <version>${spring.version}</version>
                </dependency>
            </dependencies>
            <configuration>
                <agent>
                    ${settings.localRepository}/org/springframework/spring-instrument/${spring.version}/spring-instrument-${spring.version}.jar
                </agent>
            </configuration>
        </plugin>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <dependencies>
                <dependency>
                    <groupId>org.springframework</groupId>
                    <artifactId>spring-instrument</artifactId>
                    <version>${spring.version}</version>
                </dependency>
            </dependencies>
            <configuration>
                <argLine>
                    -javaagent:${settings.localRepository}/org/springframework/spring-instrument/${spring.version}/spring-instrument-${spring.version}.jar
                </argLine>
            </configuration>
        </plugin>
    </plugins>
</build>
```

# Configuration of Bean Injection
As mentioned above, the usage of H2 database and EclipseLink needs to be specified dedicatedly. Since there are only a few settings required, 
we put all of them into a single class ```com.sap.sptutorial.jpa.config.JpaConfig```.

## Specifying a dedicated Database
For using a different database than HSQLDB, a Bean of the type ```javax.sql.DataSource``` has to be exposed. Sometimes, 
it makes sense to have a standalone Database server instead of an embedded server, which an external Database client tool can connect to for inspections. 
For this purpose, two different profiles are applied, where the _default_ profile annotation is attached to the embedded server. 

## Specifying a dedicated JPA Implementation

### JPA Vendor
First of all, a Bean of the type ```JpaVendorAdapter``` must be exposed, which take on the particular JPA vendor in question. In our case, it's the 

```org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter``` .

### Entity Manager Factory
During the start-up, the spring-data-jpa seeks for an Bean with the name ```entityManagerFactory``` with which further settings such as Database and JPA implementation etc. can be done.
Since we require a container managed Entity Manager, a ```LocalContainerEntityManagerFactoryBean``` is instantiated. Normally, an Entity Manager corresponds to a _persistence unit_ 
specified in the ```persistence.xml``` file. A persistence unit comprises all entity classes (or tables in the corresponding schema) managed by this entity manager. Instead of specifying
the persistence unit name, one can also provide the package names which include the entity classes in question by invoking the factory method ```setPackagesToScan```.

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

# Transaction Management
committing changes

# Specifying Active Spring Profiles
As we use spring profiles ```@Profile``` to flexibly tell Spring framework what DataSource should be used for the each execution, we prefer to set the active
profiles along with the execution command, meaning on the command line, instead of Java property file or any other files compiled with the code (e.g. maven pom file).
The relevant spring property is ```spring.profiles.active```. On the command line, there are two ways to specify the active profiles:
1. Using the Java property definition syntax, i.e. __-D__*property.name=property.value*.
The command would look like: ```mvn spring-boot:run -Dspring.profiles.active=xyz```.
2. Thanks to spring's smart property resolution, it is also possible to use system environment variables to set a particular property. There, uppercase letters are used instead of
lowercase letters, and the dot **.** is replace by an underscore **_**, such as __PROPERTY_NAME__ for _property.name_. The command would look like
```SPRING_PROFILE_ACTIVE=xyz mvn spring-boot:run```.

Unfortunately, probably due to some bug of the maven spring-boot plugin, the first option is not possible, because we maintain a _<configuration>_ section for this plugin in
maven pom.xml file for load time weaver. The plugin seems to ignore any properties defined with _-D_ in the command line if such a _<configuration>_ section is present. Thus, only the second alternative
with system environment variable is the viable method.
