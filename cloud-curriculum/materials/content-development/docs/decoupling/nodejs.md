# Decoupling & Test Isolation

_Disclaimer: We are [counting page hits](https://github.wdf.sap.corp/cloud-native-dev/usage-tracker) using a cookie to distinguish returning & new visitors._
<img src="https://cloud-native-dev-usage-tracker.cfapps.sap.hana.ondemand.com/pagehit/cc-materials/decoupling-nodejs/1x1.png" alt="" height="1" width="1">

## 👷‍♂️👷‍♀️ Audience

Node.js Developers that want to learn how tightly coupled code can be decoupled.

## 🎯 Learning Objectives

In this exercise you will learn...

- how to decouple your code by using the Dependency Inversion Principle (DIP) and Dependency Injection (DI)
- how to isolate your tests
- how to use [Sinon](https://sinonjs.org/) for basic stubbing

<!-- Prerequisites-->
{% with
  required=[
    ('[JavaScript Classes](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Classes)'),
    ('[Private class features](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Classes/Private_class_fields)'),
    ('[Inheritance with class syntax](https://developer.mozilla.org/en-US/docs/Learn/JavaScript/Objects/Inheritance#inheritance_with_class_syntax)')
  ],
  beneficial=[
    ('[Mocha](https://mochajs.org)'),
    ('[Sinon](https://sinonjs.org)')
  ]
%}
{% include 'snippets/prerequisites/nodejs.md' %}
{% endwith %}

## 🛫 Getting Started

{% with branch_name="decoupling", folder_name="decoupling" %}
{% include 'snippets/clone-import/nodejs.md' %}
{% endwith %}

--8<--- "snippets/npm-install-dependencies.md"

Run the tests with

{% include 'snippets/run-npm-tests/cli.md' %}

The tests should be passing ✅

Make sure that the tests are passing after each exercise step.

## 🔍 Code Introduction

The main entity of the code is a `movie` object.
It consists of the fields `title`, `director` and `rating`:

```javascript
{
  title: 'The Godfather',
  director: 'Francis Ford Coppola',
  rating: 9.1
}
```

The movie entities can be persisted to an in-memory storage using the class `InMemoryMovieStorage`.
It provides basic CRUD operations for the movie entities.

Finally we have the `MovieFinder` class, which is responsible for finding and retrieving movies from the storage, e.g. filtered by `title` or `director`.

The exercise will focus on the `MovieFinder` class and the corresponding tests.

## 📗 Exercises

In the following exercises we will tackle the dependencies of the class `MovieFinder`.
Step by step we will adjust the dependency of this class to make it more loosely coupled.
By isolating the unit tests from external dependencies, we will be able to test the core functionality of `MovieFinder` in isolation.

### 1. Implement Dependency Inversion Principle (DIP)

The **Dependency Inversion Principle** states that:

>1. High-level modules should not depend on low-level modules.
    Both should depend on the abstraction.
>1. Abstractions should not depend on details.
    Details should depend on the abstraction. (source: [Wikipedia](https://en.wikipedia.org/wiki/Dependency_inversion_principle))

Let's understand this principle in the context of our code.

The `MovieFinder` class directly uses the concrete `InMemoryMovieStorage` class, therefore it is tightly coupled.

In our scenario, the `MovieFinder` class is the high-level module, since it's depending on another module (`InMemoryMovieStorage`).

According to DIP we should make it depend on an abstraction.

#### 1.1 Create an Abstraction of `InMemoryMovieStorage`

The `InMemoryMovieStorage` class provides basic CRUD operations for the movie entities.

To implement this functionality it uses a `Map`, which is just an implementation detail.

Hence we could let other storage classes implement these operations to feature a new storage type, e.g. persisting data to an actual database.

1. Let's create a parent class `MovieStorage` in file `lib/movie-storage.js`:

    ```javascript
    export default class MovieStorage {
      constructor () {
        if (this.constructor === MovieStorage) {
          throw new Error('Abstract class')
        }
      }

      async create (movie) { // eslint-disable-line no-unused-vars
        throw new Error('Not implemented')
      }

      async read (id) { // eslint-disable-line no-unused-vars
        throw new Error('Not implemented')
      }

      async readAll () {
        throw new Error('Not implemented')
      }

      async update (id, movie) { // eslint-disable-line no-unused-vars
        throw new Error('Not implemented')
      }

      async delete (id) { // eslint-disable-line no-unused-vars
        throw new Error('Not implemented')
      }

      async deleteAll () {
        throw new Error('Not implemented')
      }
    }
    ```

    !!! hint "Abstract class"
        In JavaScript there are no actual abstract classes (yet). Instead, we can check the `constructor` to prevent the instantiation of the class and throw an error, if a method is not implemented.
        In other languages, e.g. Java or TypeScript we would rather use an interface in this case.

    Also make sure to `import` and `export` the `MovieStorage`class in `index.js`.

    ```javascript
    import MovieStorage from './lib/movie-storage.js'
    import InMemoryMovieStorage from './lib/in-memory-movie-storage.js'
    import MovieFinder from './lib/movie-finder.js'

    export { InMemoryMovieStorage, MovieFinder, MovieStorage }
    ```

1. Go to the class `InMemoryMovieStorage` in `lib/in-memory-movie-storage.js` and make it **extend** the `MovieStorage` class:

    Also make sure to add the `super()` call as the very first statement to the constructor.

    ??? example "Need help?"

        ```javascript
        import Animal from './animal.js'

        export default class Dog extends Animal {
          constructor () {
            super()
          }
        }
        ```

    !!! hint "Override"
        Methods defined in `InMemoryMovieStorage` will override the methods of `MovieStorage`.

### 2. Isolate the `MovieFinder` test

The tests in file `test/movie-finder.test.js` rely upon the proper functioning of the `MovieStorage` implementation, and consequently of the `InMemoryMovieStorage`.

Due to the fact that we use the `InMemoryMovieStorage` directly in the `MovieFinder` test:

- If there are some regressions in `InMemoryMovieStorage`, the tests for `MovieFinder` will probably fail as well.

How can we isolate our test class from the functionality of the dependencies of the productive class?

#### 2.1 Write a Stub for `MovieStorage`

It would be nice if we had an implementation of `MovieStorage` that could just provide us with the values that we need in order to test the core functionality of `MovieFinder` independently.

Since we've created an abstraction (`MovieStorage`) in the previous exercise, we can now create our own implementation of it, just for testing.

1. Create a file `movie-storage-stub.js` in the `test` folder.

1. Implement the `readAll` method to make it return the exported `MOVIES` from `test/movies.js`:

    ```javascript
    import { MovieStorage } from '../index.js'
    import { MOVIES } from './movies.js'

    export default class MovieStorageStub extends MovieStorage {
      async readAll () {
        return MOVIES
      }
    }
    ```

    The `MovieFinder` class only uses the `readAll` method, hence only this method is implemented in the stub.

#### 2.2 Inject the MovieStorageStub

1. In the `MovieFinder` class create a setter method for the private `#movieStorage` instance field:

    ```javascript
    setMovieStorage (movieStorage) {
      this.#movieStorage = movieStorage
    }
    ```

1. In the `MovieFinder` test `beforeEach` hook remove the existing code for creating movies and use the setter method to pass a new instance of `MovieStorageStub` to the `movieFinder`.

    ```javascript
    import MovieStorageStub from './movie-storage-stub.js'

    // ...

      beforeEach(() => {
        movieFinder = new MovieFinder()
        movieFinder.setMovieStorage(new MovieStorageStub())
      })
    ```

### 3. Use Dependency Injection (DI)

In the previous exercise we already injected the dependency (`MovieStorageStub`) using ***Setter Injection***.

In this particular exercise we want to use ***Constructor Injection***.

By using Constructor Injection for `MovieFinder`, we explicitly specify that for an instantiation of that class the dependency is a required property.

1. Update the constructor of the `MovieFinder` class to take an instance of `MovieStorage` as a constructor parameter and assign it's value to the class' private instance field `this.#movieStorage = movieStorage` (instead of creating a new instance as it is currently implemented). Also make sure to remove the unused `InMemoryMovieStorage` import:

    ??? example "Need help?"

        ```javascript
        export default class MovieFinder {
          #movieStorage = null

          constructor (movieStorage) {
            this.#movieStorage = movieStorage
          }
        ```

1. In the `MovieFinder` test use Constructor Injection to instantiate `MovieFinder` and pass in the `MovieStorageStub` as the dependency.

    ```javascript
    beforeEach(() => {
      movieFinder = new MovieFinder(new MovieStorageStub())
    })
    ```

### 4. Use Sinon

We used the `MovieStorageStub` to provide canned answers to the calls towards the `MovieStorage`.

Using [Sinon](https://sinonjs.org/) we won't have to write [test doubles](https://martinfowler.com/bliki/TestDouble.html) manually.

Within our test class we can create test doubles with Sinon and instruct them to behave as we wish.

#### 4.1 Add Sinon to the Project

Let's install Sinon as a dev dependency:

  ```shell
  npm install -D sinon
  ```

#### 4.2 Create and Inject the Stub

1. In the `MovieFinder` test import sinon and create a new stub instance:

    ```javascript
    // ...

    import sinon from 'sinon'
    import { MovieFinder, MovieStorage } from '../index.js'

    // ...

    const movieStorageStub = sinon.createStubInstance(MovieStorage)
    ```

1. Now inject the `movieStorageStub` instance to the `MovieFinder` object.

    ```javascript
    // ...

    describe('MovieFinder', () => {
      let movieFinder = null

      const movieStorageStub = sinon.createStubInstance(MovieStorage)

      beforeEach(() => {
        movieFinder = new MovieFinder(movieStorageStub)
      })

      // ...
    })
    ```

We can now delete the `MovieStorageStub` (file `test/movie-storage-stub.js`) as it should not be used anymore.

#### 4.3 Tell the Mock How to Behave

1. We can use `stub.method.returns(value)` to mock the `movieStorageStub`'s behavior. Similar to the `MovieStorageStub` we only need to define the behavior for `readAll`.

    ```javascript
    movieStorageStub.readAll.returns(Promise.resolve(MOVIES))
    ```

    We could also use a shorthand when creating the stub instance:

    ```javascript
    const movieStorageStub = sinon.createStubInstance(MovieStorage, {
      readAll: Promise.resolve(MOVIES)
    })
    ```

    !!! hint "Stub API"
        Have a look at the [Sinon documentation](https://sinonjs.org/releases/v12.0.1/stubs/) for more details on the stub API.

1. Run your tests.
    They should be passing.

## 🏁 Summary

Congratulations!
You have successfully decoupled a class and its dependency.
Now you're able to use different implementations more flexibly.
Also it is easier to test the functionality of `MovieFinder` since we can isolate it easily from its dependencies.

## 📚 Recommended Reading

- [Martin Fowler - Reducing Coupling](https://martinfowler.com/ieeeSoftware/coupling.pdf)
- [Inversion of Control (IoC) example](https://www.tutorialsteacher.com/ioc)

## 🔗 Related Topics

- [Martin Fowler - Test Doubles](https://martinfowler.com/bliki/TestDouble.html)
