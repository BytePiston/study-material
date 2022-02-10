# Node.js Persistence Basics

_Disclaimer: We are [counting page hits](https://github.wdf.sap.corp/cloud-native-dev/usage-tracker) using a cookie to distinguish returning & new visitors._
<img src="https://cloud-native-dev-usage-tracker.cfapps.sap.hana.ondemand.com/pagehit/cc-materials/persistence-nodejs/1x1.png" alt="" height="1" width="1">

## 👷‍♂️👷‍♀️ Audience

Developers with basic knowledge of javascript, node and relational databases, who want to know how to connect an application to a database.

## 🎯 Learning Objectives

In this exercise you will learn

- how to connect your application with a database.
- how to interact with a database using [node-postgres](https://node-postgres.com/).
- how to write test cases using a database container.

<!-- Prerequisites-->
{% with
  required=[
    ('[JavaScript Classes](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Classes)'),
    ('[Private class features](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Classes/Private_class_fields)')
  ],
  beneficial=[
      ('[Mocha](https://mochajs.org)')
  ]
%}
{% include 'snippets/prerequisites/nodejs.md' %}
{% endwith %}

## 🛫 Getting Started

{% with branch_name="persistence", folder_name="persistence-nodejs" %}
{% include 'snippets/clone-import/nodejs.md' %}
{% endwith %}

--8<--- "snippets/npm-install-dependencies.md"

## 🔍 Code Introduction

Let's have a look at the provided source code:

- `lib/books/book-service.js` exports a class that has some methods which throw a `NotImplementedError`.
  Over the course of the exercises, you will implement these methods to communicate with the database.
- `test/books/book-service-test.js` is a test file for the `book-service` which represents the database interface. Currently, there is one failing test. We will change that!

Any files not listed are not important for now and will be explained later in the exercise.

## 📗 Exercises

The exercises follow a "test first" approach, so before you write any production code, you will write a test case which tests the intended behavior.
Take small steps and implement only what is needed to make the test pass.
Only then add another test case which defines additional behavior.

To run the tests:

{% include 'snippets/run-npm-tests/cli.md' %}

### 1 - Acquiring the Database Connection

Before we start retrieving and persisting data into the db, let's ensure that we have connected to the database successfully.
Therefore we will be testing if we can perform a basic query.

#### 1.1 - Install the DB-Client
To be able to connect and send queries to a postgres database we will be using the `node-postgres` package.
This postgres client will let us set up a connection to our database.

<a href="https://www.npmjs.com/package/pg" rel="nofollow"><img src="https://nodei.co/npm/pg.png"></a>

1. Execute the following command to install it:
    ```shell
    npm install pg
    ```

1. Inside the file `book-service.test.js`, import the `pg`-module.
    We are going to need the `Pool` property of the `pg`-module.

1. Create a new instance of `pg.Pool` and assign it to a local variable `pool` inside the `describe` scope.

    The constructor of `Pool` admits an options object that contains the connection properties.
    Use one of the following connection properties:

    ```javascript
    {
        connectionString: 'postgres://postgres:pw@localhost:5432/postgres'
    }
    ```
    ```javascript
    {
        database: 'postgres',
        user: 'postgres',
        password: 'pw',
        port: 5432
    }
    ```

#### 1.2 - Write the First Test

Complete the first test in the file `book-service.test.js`.

1. Use the `query` method of the `pool` object with the following SQL string as argument:

    ```SQL
    SELECT 1 as one
    ```

1. `await` the result and assert that its `rows` property is equal to an array containing the following object:
    ```javascript
    { one: 1 }
    ```

    !!! info "Use `assert.deepEqual` when comparing arrays and objects"

The test verifies that a database connection can be used to execute SQL queries.
It should fail due to the following error:

- `Error: connect ECONNREFUSED 127.0.0.1:5432`

Well, we haven't set up any database yet, thus the driver is not able to connect.

#### 1.3 - Start a Test Database in Docker
We need a database which we will be using in our test runtime.

Let's start a PostgreSQL database in a container with the following command:
```shell
docker run --rm --name test-postgres -p 5432:5432 -e POSTGRES_PASSWORD=pw -d postgres
```

or via running the npm command
```shell
npm run db:start
```

??? info "Brief explanation of the docker command"
    1. `docker run` pulls and starts a docker container (in that case a `postgres`container - see last argument - which will be pulled from the public docker repository)
    1. `--rm` will remove the container completely once it is stopped.
    1. `--name test-postgres` names the postgres container (use e.g. `docker ps`)
    1. `-p 5432:5432` maps the port 5432 on localhost (your machine) to the exposed port 5432 in the docker container. That way you can connect to your database via `localhost:5432`
    1. `-e POSTGRES_PASSWORD=pw` injects the environment variable `POSTGRES_PASSWORD` into the container on startup
    1. `-d` runs the container in detached mode: it does not block the terminal thread.

Now rerun your test. It should be passing ✔.

#### 1.4 - Close the Database Connection 🚪

You may have noticed that even though the test has already passed the node process is still running.

This is because the `pool` is still connected to the database. It will only close the connection after a certain time of inactivity. (*The [default](https://node-postgres.com/api/pool) `idleTimeoutMillis` is 10000 millisecons (10 seconds)*)

You should close the database connection once all tests have finished.

1. Create a `before` hook where you move the instantiation of `pg.Pool` to. Make sure the `pool` variable is available in the whole suite (describe block).
1. Create a `after` hook wherein you close the connection by calling the async `pool.end()` method.

    ??? example "Need help?"
        ```javascript
        describe('BookService', () => {
          let pool

          before(() => {
              pool = new pg.Pool({/* ... */})
          })

          after(async () => {
              await pool.end()
          })

          /* ...*/
        )}
        ```

The the database connection and the node process should now also be closed once the tests have finished.

### 2 - Simple Queries

The `BookService` class specifies the database interface for adding and retrieving books.
However, in its current state it only throws errors.
Time to change that!

#### 2.1 - Test the BookService ❌
Open the file `book-service.test.js` and add a test called "should retrieve all books" that:

1. instantiates the `BookService` and passes the `pool` object to its constructor.
1. retrieves all books from it
1. asserts that the returned array is empty (since no books have been added).
    Keep in mind that the `getAllBooks`-method is an `async` function and therefore needs to be `await`ed.

When you are done you should have a test failing with the message `Not implemented yet`.

#### 2.2 - Implement the BookService

The `BookService` shall send queries to the database through the `pool` object which provides the `query`-method.
The query method returns a `Promise` which resolves with a result object.
Its `rows` property is an array which contains all the entries found.

1. `import` the BookService from `'../../lib/books/book-service.js'`
1. Add the `pool` as dependency to `BookService`'s constructor function, assign it to a private class variable `#pool`, and use it to execute the following query in its `getAllBooks` method:
    ```SQL
    SELECT title, author FROM books
    ```

1. Make the `getAllBooks` method return the rows from the query.

1. Make sure to pass the `pool` object to the `BookService` constructor in the test.

1. Run the test. It should fail with the following error:
`relation "book" does not exist`

#### 2.3 - Create the Schema

The table "book" was never created, so the query fails naturally.
The file `schema.js` provides SQL-queries for creating and dropping the required table.

1. `import` the query `CREATE_TABLE_SQL` via object destructuring from `'../../lib/schema.js'`
1. Like with the `before` hook, create an async `beforeEach` hook where you use the pool to execute the `CREATE_TABLE_SQL` query.
    Keep in mind, that the `query` method returns a `Promise` which needs to be `await`ed as the tests should only run, after the schema has been created.

The test should now be passing.

!!! warn "Database Migrations"
    Executing a query on every app start is not the best way to go about.
    The query will have to be carefully designed to not change anything when run repeatedly.
    Migration tools like [db-migrate](https://db-migrate.readthedocs.io/en/latest/) are usually the better alternative.
    They keep track of schema versions and run pending migrations.
    Many query builders or ORMs designed for Node.js also provide migration functionality like [Sequelize](https://sequelize.org/master/index.html) and [Knex.js](http://knexjs.org/).


### 3 - Query Parameters

The previous test ensures that the book retrieval is working, but that is not very exciting, when there is no way to add any books.

#### 3.1 - A new Test ❌

1. Go to `book-service.test.js` and create a new test case "should add book".
1. Extract the creation of the `BookService` instance into the `before`-hook to avoid duplication.

    ??? example "Need help?"
        Make sure that the `BookService` instance is accessible in the test by placing the variable declaration as in the following example:
        ```javascript
        let service

        before(() => {
            //...
            service = new ThingService(pool)
        })
        ```

1. To test the functionality of adding a book, call the `addBook`-method of the `BookService` and give it an object like:
    ```javascript
    { title: 'Refactoring', author: 'Martin Fowler' }
    ```

    !!! info "Remember to `await` the `#!javascript async` functions"

1. Use the previously implemented `getAllBooks`-method to retrieve all books.
1. Assert that the retrieved array has exactly one entry with the expected title and author.

The test should be failing with the message `Not implemented yet`.

#### 3.2 - Make the Test Pass ✔️

Go to `book-service.js` and implement the `addBook`-method.

1. Query the database by using the `query`-method on the `pool` object with the following SQL:
    ```SQL
    INSERT INTO BOOKS (title, author) VALUES ($1, $2)
    ```

1. Supply an array containing the values to replace the place-holders `$1` and `$2` as a second parameter to the `query` function.
    Use the properties of the `books` object appropriately.

    ??? example "Need help?"
        ```javascript
        await this.#pool.query('INSERT INTO ARTIST (name, surname) VALUES ($1, $2)', ['John', 'Lennon'])
        ```

Your test should be passing now.
If not, you possibly ran the test multiple times and end up with the test failing due to:
```
error: duplicate key value violates unique constraint "title_author_uniqueness"
```

In this case the next exercise step should help you fixing this problem.

#### 3.3 - Clean up the Schema 🧹

One of the tests changes the state of the database while both tests assume a certain state at the beginning.
The `Mocha` test runner executes the tests in the same order consistently, but other test runners might not.
If a test breaks, simply due to having been moved it is very fragile and quite possibly not isolated enough.

In order to make the tests more stable, ensure that the database is cleaned up after each test.

1. `import` the query `DROP_TABLE_SQL` alongside the already imported `CRAETE_TABLE_SQL`
1. Create an `afterEach` hook wherein you execute the async `DROP_TABLE_SQL` query provided by `schema.js`.

The tests should then be passing regardless of their order.

### 4 - Exception Handling

Let's add some exception handling to our communication with the database.
For this matter we will be using the unique constraint on the `books` table.

#### 4.1 - Add Test to Ensure Uniqueness

The `books` table has a unique constraint on the columns `title` and `author`.
Let's ensure that a proper error is thrown when the constraint is violated.

1. Add a new test to `book-service-test.js` with the name "should throw an error when unique constraint is violated".

1. `import` the UniqueConstraintViolationError from `'../../lib/unique-constraint-violation-error.js'`

1. Inside the test call the `addBook`-method of the bookService twice with the same book parameter in order to provoke the constraint violation.

1. Use the async `assert.rejects()` method to make sure the second call to the database throws an async error. This methods accepts an async (to be failing) function and the error class as arguments. Here, this should be the second call to `bookService.addBook()` and the `new UniqueConstraintViolationError()` class.

    ??? example "Need help?"
        ```javascript
        await assert.rejects(anAsyncFunctionThatShouldRejectWithAnError(), new SomeErrorClass())
        ```

The test should be failing.

#### 4.2 - Make the Test Pass ✔️

To make the test pass, first we need to check if the error thrown by `pg` is because of the unique constraint violation.
And then throw a custom error `UniqueConstraintViolationError` in order to make the error case more distinguishable.

1. Go to the `book-service.js` and wrap the call of `pool.query` into a try-catch.

1. `import` the UniqueConstraintViolationError from `'../unique-constraint-violation-error.js'` like in the test class

1. Inside the catch block, check if the `error`'s `code` property is equal to the static class constant `BookService.UNIQUE_VIOLATION_ERROR_CODE` which has the `pg`-provided value `23505`.
    If so, throw a new `UniqueConstraintViolationError`.

    ??? info "PostgreSQL Error Codes"
        PostgreSQL maps different types of errors to error codes.

        You can check out the reference of [PostgreSQL Error Codes](https://www.postgresql.org/docs/10/errcodes-appendix.html) to see the list of error codes that can occur.

    In the else case, you can just throw the `error` that was caught before.

Your tests should be all passing now.

## 🏁 Summary
Good job!
In this exercise you wrote test cases with a containerized database, installed and used the `pg` module to connect to it and fired off your own SQL queries.

## 🦄 Stretch Goals
You should already have a good idea of all common parts by now, you could stop here... oooor you can finish what you started:

1. Implement the `getBookByTitle` method, so that it returns all books whose title matches exactly with the argument string.
1. Make the `getBookByTitle` return all books whose title _includes_ the argument string.

## 📚 Recommended Reading
- [Transactions](https://node-postgres.com/features/transactions)
- [Suggested Project Structure Guide](https://node-postgres.com/guides/project-structure)

## 🔗 Related Topics
- [Raw SQL vs Query Builder vs ORM](https://levelup.gitconnected.com/raw-sql-vs-query-builder-vs-orm-eee72dbdd275)
- [Sequelize ORM](https://sequelize.org/)
- [knexjs query-builder](http://knexjs.org/)
- [SQL vs NoSql Overview](https://www.imaginarycloud.com/blog/sql-vs-nosql/)(Note that some data stores are not offered at SAP and never will be. Internal guidance on this topic is being worked on by CPA)
