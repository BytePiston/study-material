# Java Persistence

_Disclaimer: We are [counting page hits](https://github.wdf.sap.corp/cloud-native-dev/usage-tracker) using a cookie to distinguish returning & new visitors._
<img src="https://cloud-native-dev-usage-tracker.cfapps.sap.hana.ondemand.com/pagehit/cc-materials/persistence-java/1x1.png" alt="" height="1" width="1">

## 👷‍♂️👷‍♀️ Audience

Developers with basic knowledge of Java and relational Databases

## 🎯 Learning Objectives

In this exercise you will learn

- how to connect your application to a database
- how to interact with a database using Spring Data JPA
- how to write test cases using an embedded database

<!-- Prerequisites-->
{% with
  tools=[
    '[**Docker**](https://docs.docker.com/engine/install/)'
  ],
  required=[
    'Basic understanding of [relational Databases](https://www.oracle.com/database/what-is-a-relational-database/)'
  ],
  beneficial=[
      '[Spring](https://spring.io/)'
  ]
%}
{% include 'snippets/prerequisites/java.md' %}
{% endwith %}

## 🛫 Getting Started

{% with branch_name="persistence", folder_name="persistence-java" %}
{% include 'snippets/clone-import/java.md' %}
{% endwith %}

Run the application:

{% with main_class="BooksApplication" %}
=== "Command Line"
{% filter indent(4) %}
{% include 'snippets/run-spring-boot/cli-maven-run.md' %}
{% endfilter %}
=== "Spring Tool Suite"
{% filter indent(4) %}
{% include 'snippets/run-spring-boot/sts-run.md' %}
{% endfilter %}
=== "IntelliJ"
{% filter indent(4) %}
{% include 'snippets/run-spring-boot/intellij-run.md' %}
{% endfilter %}
{% endwith %}

When the Application has started you should be able to see a line similar to the following in the console:
```
[2020-08-19 09:18:45.593] - 72849 INFO [restartedMain] --- com.sap.cc.library.BooksApplication: Started BooksApplication in 1.32 seconds (JVM running for 1.925)
```

## 🔍 Code Introduction

We have set up a simple application for managing books.
The focus of this exercise will be to implement an API to persist the domain objects into a database.

In `src/main/java` you will find the following classes in the `com.sap.cc.library.book` package:

- `Book` - which is the main entity and has two fields (`title` and `author`).
- `Author` with the field `name`.

In `src/test/java` the class `BookFixtures` is provided. 
It produces example book-objects you can use within your tests.

--8<-- "snippets/spring-dev-tools.md"

## 📗 Exercises

The exercises follow a "test first" approach, so before you write any production code, you should write a test case which tests the intended behavior.
Take small steps and implement only what is needed to get the test to pass.
Only then add another test case which defines additional behavior.

To run the tests:

=== "Command Line"
{% filter indent(4) %}
{% include 'snippets/run-tests/cli-run-tests.md' %}
{% endfilter %}
=== "Spring Tool Suite"
{% filter indent(4) %}
{% include 'snippets/run-tests/sts-run-tests.md' %}
{% endfilter %}
=== "IntelliJ"
{% filter indent(4) %}
{% include 'snippets/run-tests/intellij-run-tests.md' %}
{% endfilter %}

### 1 - Entities and Repositories

#### 1.1 Repositories

1. Go to the `src/test/java` directory and create a class `BookRepositoryTest` in the `com.sap.cc.library.book` package.
1. Add the following field to it:
```Java
private BookRepository repository;
```
Create the **interface** `BookRepository` inside the package `com.sap.cc.library.book` in `src/main/java`.
1. We want it to extend one of Spring Data JPAs Repository interfaces which are not available yet. Include the following dependency to the `pom.xml`:
```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```
1. Make the `BookRepository` extend `#!java JpaRepository<Book, Long>`.

#### 1.2 Test the Repository

1. In your `BookRepositoryTest` class, create a test method (annotated with `#!java @Test`) to test the `findAll`-method.
1. In the test, call the `findAll` method on the repository and assert that the returned list is empty (since we have not added any books yet).

    ??? example "Need help?"
        ```Java
        import org.junit.jupiter.api.Test;
        import static org.assertj.core.api.Assertions.assertThat;

        @Test
        void findAllThings_should_be_empty() {
            List<Thing> things = thingRepository.findAll();
            assertThat(things).isEmpty();
        }
        ```

1. Run the test. It should be failing due to a `NullPointerException`.

    We neither created a class that implements the `BookRepository` interface nor did we instantiate such a class in the test; Luckily Spring Data JPA can do this for us.
    Therefore lets load the Spring context to our tests.

1. Add the `#!java @SpringBootTest` annotation to the test class and the `#!java @Autowired` annotation to its `repository` field.
1. Rerun the test and you will find it fails with the following message:
    ```shell
    Failed to configure a DataSource: 'url' attribute is not specified and no embedded datasource could be configured.
    Reason: Failed to determine a suitable driver class
    ...
    If you want an embedded database (H2, HSQL or Derby), please put it on the classpath.
    ```
1. Let's follow Spring's suggestion by adding the dependency for H2 to the `pom.xml`:
    ```xml
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>
    ```

    The reasons for the test failing keep changing.
    Now the message is: `Not a managed type: class com.sap.cc.library.book.Book`.

#### 1.3 Your First Entity 📘

1. Add the `#!java @Entity`-annotation to the `Book`-class.

    !!! tip "Prefer JPA classes/annotations over provider specifics"
        Always import classes, interfaces or annotations from the JPA specification, not the JPA provider. 
        This way you keep your application and the JPA provider loosely coupled.
        For example choose `#!java @javax.persistence.Entity` over `#!java @org.hibernate.annotations.Entity`

    The test is still failing.
    This time for the following reason:
    `No identifier specified for entity: com.sap.cc.library.book.Book`.

1. Specify an identifier for the entity by giving it a `id` field of the type `Long`

    >Why `Long`?

    Remember that the `BookRepository` extends the interface `#!java JpaRepository<Book, Long>`.
    The first type parameter refers to the entity while the second specifies the type of its identifier.

1. Annotate the field with `#!java @Id` and create get and set methods for it (or let your IDE do it).

1. Run the test.

    It should now be failing with the error: `Could not determine type for: com.sap.cc.library.book.Author, at table: book, for columns: [org.hibernate.mapping.Column(author)]`

    Hibernate (the default JPA provider of spring boot) tries to find an appropriate database column type for the `author` attribute and apparently fails.

1. Add the `#!java @Transient` annotation to the `author` field, to tell JPA that this field should not be persisted.

1. Run the test.

    And finally...the test passes. 👍

### 2 - Generated Values

Our test proves we can retrieve books from the repository, but this isn't too exciting if we can't add any.

#### 2.1 Test the Save Method ❌

Add a test case where you:

1. `save` a book using the repository
1. retrieve all books
1. assert that only one book is retrieved 
1. assert this book has the title of the book added previously.

The class `BookFixtures` provides useful example books for tests via its static methods.

??? example "Need help?"
    
    - to get example books, use the `BookFixtures` class:
    ```Java
    Book cleanCode = BookFixtures.cleanCode();
    ```

    - to assert the size of a list use:
    ```Java
    assertThat(list).hasSize(1);
    ```

    - to assert that two fields are equal, use:
    ```Java
    assertThat(disc.getArtist()).isEqualTo(otherDisc.getArtist());
    ```

The test should be failing with the error: `ids for this class must be manually assigned before calling save(): com.sap.cc.library.book.Book`.

#### 2.2 Make the Test pass ✔️

The message suggests that we should set an id, but should we set it manually and how can we acquire an id that is guaranteed to be unique?

We should delegate the task of setting id fields to JPA. 
While JPA also supports the implementation of custom id generation "strategies" for special cases (outside of the scope of this exercise),
by default, it will assume we want it to acquire an id by working with the database by using sequences or sequence tables, whichever the database supports.

In most scenarios this is the way to go, since the database will be better at generating unique ids than our application.

1. Use the `#!java @GeneratedValue`-annotation to do exactly that.
    Put it on the `Book`'s `id` field and run the test.
    It should pass.


#### 2.3 Clean Up Before You Test 🧹 

In the test we rely on the database to be empty.
That's why we expect our newly saved book to be at the first position (`books.get(0)`).
However, we save a book inside the test, and thereby affect subsequent tests.

If another test, that also expects the database to be empty, runs after our test, it will fail.
It would be nice if the database had the same state whenever a test starts.

- Therefore, add a `clearDb`-method, annotate it with `#!java @BeforeEach` and call the repository's `deleteAll`-method inside.

### 3 - Repository Methods

The interface `JpaRepository` provides a wealth of generic methods to interact with the database, but none of them supports querying for entity-specific attributes.

#### 3.1 Test the Search 🔍

Write a new test for the `BookRepository` called "findBookByTitle" that:

1. saves two books
1. calls the (nonexistent) method `findByTitle`, with one of the books' title as parameter (e.g. `#!java findByTitle("Clean Code")`), on the repository 
1. asserts that the returned book's title is equal to that of the added book.

#### 3.2 Make the Test pass ✅

1. Add a method to the `BookRepository` interface called `findByTitle`.
    It should return a `Book` and accept a `String` parameter named `title`.

1. Run the test. It should pass.

>What is this magic?

Baeldung explains it well in their [Introduction to Spring Data JPA](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa#1-automatic-custom-queries):
>When Spring Data creates a new *Repository* implementation, it analyses all the methods defined by the interfaces and tries to **automatically generate queries from the method names**.
While this has some limitations, it's a very powerful and elegant way of defining new custom access methods with very little effort.

Take a look at the [Spring Data JPA reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods) to learn about the full capabilities of repository methods and find a table of the supported keywords inside method names.


### 4 - Data Durability

Let's ensure our persisted data survives the restart of the application.

#### 4.1 Set Up The Tests

1. Create a new test-class called `DataDurabilityTest` and annotate it with the `#!java @SpringBootTest` annotation.

1. Inject the `BookRepository` as a private attribute with the `#!java @Autowired` annotation.

1. Now go on and write the first test-method `populateDb()` where you implement the following:

    1. populate the repository with some Books
    1. assert that the `findAll`-method of the `BookRepository` returns a list with a size greater than or equal to the number of books added previously.

    The test should be passing.

    ??? example "Need help?"
        - To assert that a list "hasSizeGreaterThanOrEqualTo" a specific value you can use the following assertion:
        ```Java
        assertThat(list).hasSizeGreaterThanOrEqualTo(5);
        ```

1. Add another test-method `isDbPopulated()` where you implement the following:

    1. retrieve the list of all books from the `BookRepository`
    1. assert that the size of the returned list is greater than or equal to the number of books added in the `populateDb` test

    Both tests should be passing.

#### 4.2 Simulate Application Shutdown

Remember that we want to simulate an application shutdown to ensure that the data is durable independent of the application state.
For this test scenario we would like to shut down the Spring Boot application (killing the spring context is sufficient) in between the two tests.
Spring provides the `#!java @DirtiesContext`-annotation which can help us to build this scenario.

1. Add the following annotation above the class signature
    ```Java
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    ```

1. Run the tests.

    The `isDbPopulated`-test should fail, because the count of all books is expected `to be greater than or equal to <number of books added> but was 0`.

    ??? warning "@DirtiesContext and the Spring Context during tests"
        `#!java @DirtiesContext` can help us clean up any state we add/apply to the spring context through test cases. 
        It will throw away and re-create the whole Spring context of the application after every single test method (or class). 
        Use this feature sparingly, since it will significantly increase the runtime of your tests. 
        More often than not there are other, way more performant, ways of "cleaning up" the state your tests created.

#### 4.3 File Based H2

By default Spring Boot will configure the H2 database to store its data in-memory.
This kind of DB is designed to be volatile and data will be lost when the application gets restarted.

Let's reconfigure the H2 database to use file-based storage instead of in-memory storage in order to make our data persistent across application restarts / context reloads.

1. In `src/test/resources` there is an `application.properties` file (if not you can simply create it).

1. In it add these two configurations

    ```Properties
    spring.datasource.url=jdbc:h2:file:./data/book-db
    spring.jpa.hibernate.ddl-auto=update
    ```

    The line `spring.jpa.hibernate.ddl-auto=update` is required since the default value would be `drop-create`.
    With `drop-create` our DB-schema would be re-created every time the application starts, leading to data loss.
    Hence we use the value `update`. Please note the info box "Automatic schema generation" below for more information.

    The tests should be running now.

### 5 - Entity Relations

Next we want to be able to persist the `author` field. 
The `Author` class is referenced by the `Book` class via the `author` field, but since we added the `#!java @Transient` annotation to it, JPA will not consider it as data that needs to be persisted.

#### 5.1 Test that the Author Gets Saved

Adjust the `save` test in `BookRepositoryTest` to ensure that the author is also persisted.

1. Assert that the `name` of the returned book's `author` is equal to the `name` of the added book's `author`.

1. Run the test. 
    The test should be failing due to a `NullPointerException`.
    This is because the book entity, that got persisted to the database, did not include the author property because of the `#!java @Transient`.

#### 5.2 Declare the Relation Book -> Author

Most authors write more than one book.
Instead of duplicating the author information for every book, the books should reference an entry in the `Author` table.

1. Remove the `#!java @Transient` annotation and add a `#!java @ManyToOne` annotation to the `author` property.
    With that we indicate that *many* books can be associated *to one* author.

1. Run the test again. 

    It is still failing with the error message 
    `@OneToOne or @ManyToOne on com.sap.cc.library.book.Book.author references an unknown entity: com.sap.cc.library.book.Author`

    By adding the `#!java @ManyToOne` annotation, we defined the relation to another entity.
    But so far we haven't specified that `Author` is an entity.

#### 5.3  Make Author an Entity

1. Go to the `Author` class and make it an entity by adding the `#!java @Entity` annotation and an `id` field, just as you did in exercise 1 for the `Book` class.

1. Run the tests.
    They are still failing with the message `object references an unsaved transient instance - save the transient instance before flushing`.

    When the `save(book)` method of the `BookRepository` gets called, JPA sees that the book instance carries a reference to an `Author` entity that was not yet persisted. 
    Unfortunately it does not (yet) know how to handle this situation.

#### 5.4 Cascade

In order to achieve persistence of child entities along with the parent entity, a cascade type must be specified.

1. Go to the `Book` class and add the `cascade` parameter to the `#!java @ManyToOne` annotation.
    In this case we want to use the `#!java CascadeType.PERSIST` as the value.

    ??? example "Need help?"
        Annotations can have parameters, they are key/value pairs such as in the example below:
        ```Java
        @AwesomeAnnotation(parameterName = parameterValue)
        ```

    ??? question "Why CascadeType.PERSIST?"
        `#!java CascadeType.PERSIST` specifies, that the persist operation is propagated from a parent to a child entity.
        Whenever a book is saved with an `Author` entity that has not been persisted yet, the author should be saved too.
        However, if a book is removed, the associated author should not be removed, as there might be other books with the same author.

1. Run the tests.
    They should be passing now.

If they are failing, make sure that you have applied all the necessary steps from exercises 1 and 2 for the `Author` entity.
Perhaps you forgot the `#!java @GeneratedValue` annotation.

### 6 - Wire up a Productive DB

So far we have only worked with a H2 database for our tests (Note that we made sure that H2 is only used by the tests by doing the configuration in the `src/test/resources/application.properties` and by setting the scope of the H2 maven dependency to `test`).
We don't have a productive database that gets used when we have our application running.

If you try to run the application, it fails due to:

`Failed to configure a DataSource: 'url' attribute is not specified and no embedded datasource could be configured.`

Spring checks the `application.properties` to find a database configuration but finds none.
Let's create a database and wire it up with our application.

#### 6.1 Get a PostgreSQL running with the ease of containers 🐳

Use the following docker command to start a PostgreSQL database:
```shell
docker run --rm --name some-postgres -p 5432:5432 -e POSTGRES_PASSWORD=pw -d postgres
```

It is used for the following exercises, but once you are done you can stop it with the following command:
```shell
docker stop some-postgres
```

#### 6.2 Spring Datasource

Next, Spring wants to know the connection details of the database in order to create a datasource for it.

1. Provide it with the necessary configuration by setting the following properties in `src/main/resources/application.properties`:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
    spring.datasource.username=postgres
    spring.datasource.password=pw
    ```

    Additionally a driver is required to communicate with the database.

1. Add the following dependency to your `pom.xml`:
    ```xml
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    ```

The application should now start successfully.

#### 6.3 Database Schema

The PostgreSQL database does not have any tables yet (apart from the built-in ones).
When Spring detects an embedded database (such as H2) on the classpath it, by default, generates a database schema based on the persistence-related annotations (e.g. `#!java @Entity`, `#!java @Id`, `#!java @GeneratedValue`, ...).

- Tell Spring to do the same for the PostgreSQL database by adding the following line to `application.properties`:
    ```properties
    spring.jpa.hibernate.ddl-auto=update
    ```

You can find more detailed information [here](https://docs.spring.io/autorepo/docs/spring-boot/2.3.x/reference/html/howto.html#howto-initialize-a-database-using-hibernate).

!!! warning "Automatic schema generation"
    The setting makes Hibernate update any tables that have changed in structure (e.g. a column was added/removed).
    However, the way it applies these updates may not resemble your intention.
    While this is fine for tests and good for quick iterations early in development, you should never deploy an application with this setting.
    A solution which allows you to migrate your data through different versions of your database schema is more desirable, such as [Flyway](https://flywaydb.org/) and [Liquibase](https://www.liquibase.org/).
    These, however, are beyond the scope of this exercise.

## 🏁 Summary
Good job!
In this exercise you wrote test cases with an embedded database and declared a JPA repository to store and retrieve books. You even added a custom method. In addition you connected the application to a database running in a docker container.

## 🦄 Stretch Goals
You should already have a good idea of all common parts by now, you could stop here... oooor you can finish what you started:

1. Create a repository-method that returns all books whose title contains the searched string
1. Make Book-Author a `#!java @ManyToMany` relation
1. Add an `authoredBooks` attribute to the `Author` entity to make the relation bidirectional

## 📚 Recommended Reading
- [Efficient Java Persistence with JPA course](https://github.wdf.sap.corp/cloud-native-dev/java-persistence/wiki#efficient-java-persistence-with-jpa)
- [Spring Data JPA documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.core-concepts)
- [Spring Data JPA @Query for custom queries](https://www.baeldung.com/spring-data-jpa-query)
- [Bidirectional JPA Relations](https://dzone.com/articles/introduction-to-spring-data-jpa-part-4-bidirection)

## 🔗 Related Topics
- [Hibernate Second-Level Cache](https://www.baeldung.com/hibernate-second-level-cache)
- Use a real database for testing with [TestContainers](https://www.testcontainers.org/)
- [R2DBC Repositories](https://docs.spring.io/spring-data/r2dbc/docs/1.1.2.RELEASE/reference/html/#r2dbc.repositories) - Use Spring Data Repositories in the "reactive" programming model
- [CAP](https://github.wdf.sap.corp/pages/cap/java/getting-started)
- [DBeaver - Database Browser](https://dbeaver.io/)
- [SQL vs NoSql Overview](https://www.imaginarycloud.com/blog/sql-vs-nosql/)(Note that some data stores are not offered at SAP and never will be. Internal guidance on this topic is being worked on by CPA)
