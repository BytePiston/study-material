# Asynchronous Programming in Node.js

_Disclaimer: We are [counting page hits](https://github.wdf.sap.corp/cloud-native-dev/usage-tracker) using a cookie to distinguish returning & new visitors._
<img src="https://cloud-native-dev-usage-tracker.cfapps.sap.hana.ondemand.com/pagehit/cc-materials/js-async-nodejs/1x1.png" alt="" height="1" width="1">

## 👷‍♂️👷‍♀️ Audience

Developers with basic knowledge of JavaScript who want to learn more about asynchronous programming in Node.js.

## 🎯 Learning Objectives

In this exercise you will learn

- the difference between asynchronous and synchronous programming

- the potential pitfalls of asynchronous programming

- how to

    - create and use Callback-based APIs

    - create and use Promise-based APIs

    - use the `async`/ `await` syntax

    - orchestrate async. tasks

<!-- Prerequisites-->
{% include 'snippets/prerequisites/nodejs.md' %}

## 🛫 Getting Started

{% with branch_name="async", folder_name="async-nodejs" %}
{% include 'snippets/clone-import/nodejs.md' %}
{% endwith %}

--8<--- "snippets/npm-install-dependencies.md"

## 📓 Code Introduction

In this *real world* example (😉) we will implement a *Pokedex* application.

As we have already caught **all** *Pokemon* in the world, we will use a *Pokedex* to keep track of them.

The *Pokedex* is implemented in file `lib/pokedex.js`. It provides methods to `read`, `write`, and `find` *Pokemon* by their `type`.

In file `index.js` we are using the `pokedex` module to find all our *Pokemon* for a given `type`, e.g. `fire`.

You can execute `npm start` to run the application.

All Pokemon found for the given type will be written to a file `data/<type>.json`, e.g. `data/fire.json`.

## 📗 Exercises

But as usual there is a catch!

The *Pokedex* is implemented in a synchronous way. This could [block us](https://nodejs.org/en/docs/guides/blocking-vs-non-blocking/){target=_blank} from catching even more *Pokemon*!

Let's see how we can make it asynchronous.

### 1 Callbacks 📞

#### 1.1 Import the Callback API

In file `lib/pokedex.js` we are currently using the [readFileSync](https://nodejs.org/docs/latest-v16.x/api/fs.html#fsreadfilesyncpath-options){target=_blank} and [writeFileSync](https://nodejs.org/docs/latest-v16.x/api/fs.html#fswritefilesyncfile-data-options){target=_blank} methods from the [File system](https://nodejs.org/docs/latest-v16.x/api/fs.html){target=_blank} module.

As the names suggest, these methods are **synchronous** and hence are **[blocking operations](https://nodejs.org/en/docs/guides/blocking-vs-non-blocking/){target=_blank}**.

!!! tip "Blocking"

    Blocking is when the execution of additional JavaScript in the Node.js process **must** wait until a non-JavaScript operation (e.g. reading and writing files) completes.

    This happens because the [event loop](https://nodejs.org/en/docs/guides/event-loop-timers-and-nexttick/){target=_blank} is unable to continue running JavaScript while a blocking operation is occurring.

Luckily, all of the I/O methods in the Node.js standard library provide **asynchronous** versions, which are **non-blocking**, and accept so-called **callback functions**.

1. Instead of using [readFileSync](https://nodejs.org/docs/latest-v16.x/api/fs.html#fsreadfilesyncpath-options){target=_blank}, [writeFileSync](https://nodejs.org/docs/latest-v16.x/api/fs.html#fswritefilesyncfile-data-options){target=_blank}, we can use [**readFile**](https://nodejs.org/docs/latest-v16.x/api/fs.html#fsreadfilepath-options-callback){target=_blank} and [**writeFile**](https://nodejs.org/docs/latest-v16.x/api/fs.html#fswritefilefile-data-options-callback){target=_blank}.

    ```javascript
    import { readFile, writeFile } from 'fs'
    ```

#### 1.2 Use a Callback API

Both methods accept a callback function as their last argument.

**Yes, we will be passing a function as an argument when calling another function!**

The callback function will be called when the operation has completed.

For [readFile](https://nodejs.org/docs/latest-v16.x/api/fs.html#fsreadfilepath-options-callback){target=_blank} the callback function will then be called with two arguments:

- **`error`**: An [Error](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Error){target=_blank}, if any (e.g. `File Not Found`), otherwise `null`
- **`data`**: The `data` read from the file, or `null` if an error occurred

For [writeFile](https://nodejs.org/docs/latest-v16.x/api/fs.html#fswritefilefile-data-options-callback){target=_blank} the callback function will be called with only one argument: `error`, as there is no `data` to return.

!!! tip "Error-first callbacks"

    This signature is also called [Error-first callback](https://nodejs.org/docs/latest-v16.x/api/errors.html#error-first-callbacks) and is the default for Callback-based APIs in Node.js.

    ```javascript
    const errorFirstCallback = (error, result) => {
      if (error) {
        console.error('There was an error', error)
      } else {
        console.log('Success!', result)
      }
    }
    doSomething(input, errorFirstCallback)
    ```

1. Adjust the `read` method in `lib/pokedex.js` to use the [readFile](https://nodejs.org/docs/latest-v16.x/api/fs.html#fsreadfilepath-options-callback){target=_blank} Callback API.

    Make sure to use `JSON.parse` in the success case of the callback to convert the `data` string into an `pokemon` array.

    ??? example "Need help?"

        ```javascript
        someMethod(input) {
          doSomething(input, (error, result) => {
            if (error) {
              // error case
            } else {
              // success case
              // ... do something with result
            }
          })
        }
        ```

1. Adjust the `write` method in `lib/pokedex.js` to use the [writeFile](https://nodejs.org/docs/latest-v16.x/api/fs.html#fswritefilefile-data-options-callback){target=_blank} Callback API.

    Make sure to use `JSON.stringify` to convert the `pokemon` array into a `data` string before writing it to the file.

    ??? example "Need help?"

        ```javascript
        someMethod(input) {
          // ... do something with input
          doSomething(input, (error) => {
            if (error) {
              // error case
            } else {
              // success case
            }
          })
        }
        ```

#### 1.2 Provide a Callback API

So far so good. The `pokedex` module does now use the `fs` Callback API internally.

But as Callback APIs do not return any value, `pokedex.read` and `pokedex.write` do also not return anything anymore.

Both methods need to be adjusted to accept a callback function as their last argument in order to forward any `error` or `pokemon`.

1. Adjust the `read` method in `lib/pokedex.js` to accept a callback function as its last argument.

1. Invoke the callback function with the `error` and `pokemon` arguments accordingly to the [**Error-first convention**](https://nodejs.org/docs/latest-v16.x/api/errors.html#error-first-callbacks){target=_blank} in the **error** or **success** case.

    ??? example "Need help?"

        ```javascript
        someMethod(input, callback) {
          doSomething(input, (error, result) => {
            if (error) {
               // error case
              callback(error)
            } else {
              // success case
              // ... do something with result
              callback(null, result)
            }
          })
        }
        ```

1. Also adjust the `write` method in `lib/pokedex.js` to accept a callback function as its last argument.

1. Again invoke the callback function accordingly to the [Error-first convention](https://nodejs.org/docs/latest-v16.x/api/errors.html#error-first-callbacks){target=_blank}.

    ??? example "Need help?"

        ```javascript
        someMethod(input, callback) {
          // ... do something with input
          doSomething(input, (error) => {
            if (error) {
               // error case
              callback(error)
            } else {
              // success case
              callback(null)
            }
          })
        }
        ```

#### 1.3 Consume a Callback API

We have now successfully implemented the `pokedex.read` and `pokedex.write` methods in a non-blocking way, providing a Callback API.

The next step is to consume the `pokedex` Callback API in `index.js`

1. Remove the `try-catch` block in `index.js`, as it will not work anymore.

    Errors will not be thrown, but instead be forwarded to the callback function.

1. Adjust the call to `pokedex.read()`: Remove the left side assigment to `pokemon` and instead provide a callback function.

    In case of an **error**, we should log that `readError` to the console and exit the program.

    Move all further code which should be executed after the file has been succesfully read and relies on `pokemon` (`pokedex.find()` and `pokedex.write()`) into the **success** case of the callback function.

    ```javascript
    pokedex.read('some/file/path', (readError, pokemon) => {
      if (readError) {
        // error case
      } else {
        // success case
        // ... do something with pokemon
      }
    })
    ```

1. Next adjust the call to `pokedex.write()` and also provide a callback function.

    Again move all further code into the callback function and handle the **error** and **success** case accordingly.

    ```javascript
    // ... get some pokemon
    pokedex.write('some/other/file/path', pokemon, (writeError) => {
      if (writeError) {
        // error case
      } else {
        // success case
      }
    })
    ```

    ??? example "Need help?"

        ```javascript
        pokedex.read('some/file/path', (readError, pokemon) => {
          if (readError) {
            // error case
          } else {
            // success case
            // ... do something with pokemon
            pokedex.write('some/other/file/path', pokemon, (writeError) => {
              if (writeError) {
                // error case
              } else {
                // success case
                // ... all done once we reach this line
              }
            })
          }
        })
        ```

    !!! tip "Nested callbacks"

        This is the biggest difference between the synchronous and the asynchronous Callback APIs.

        **Callback APIs do not return any value and do not throw errors which can be catched!**

        We will only receive any `error` or `data` as arguments being passed to our provided callback function.

        This *nesting* continues if we need to call another callback function.

1. Run `npm start` to ensure that the async program works as expected, e.g. change the `type` and check the existence of the `data/<type>.json` file and its content.

Congratulations! 🎉  You have now successfully implemented the `pokedex` module in a non-blocking way, using a Callback API!

!!! tip "Sync API vs. Callback API"

    **Sync API**

    ```javascript
    import { readFileSync } from 'fs'
    try {
      const data = readFileSync('./file.txt') // blocks here until file is read
      console.log(data)
    } catch (error) {
      console.error(error)
    }
    ```

    **Callback API**

    ```javascript
    import { readFile } from 'fs'
    readFile('./file.txt', (error, data) => {  // will be called when file has been read
      if (error) {
        console.error(error)
      } else {
        console.log(data)
      }
    }) // no value is being returned from readFile() !!!
    // execution immedialty continues here
    ```

But there are some important things to notice here:

 - the multiple calls to `console.error` as we need to handle the *error* case for each (nested) callback

 - the level of the final `console.log('... done!')` which is in the **success** case of the deepest callback function

Imagine you would have to call more subsequent Callback API methods in order to complete the task.

Your nesting would get deeper and deeper and each error would need to be handled in individually.

This would make it very hard to read and understand the code and to maintain all possible cases.

Sorry, but you are now trapped in the 🔥 [**Callback Hell**](http://callbackhell.com/){target=_blank} 🔥 !

!!! tip "Callback Hell"

    Some may also call it the **Callback Pyramid of Doom** because of its shape:

    ```javascript
    firstFunction(args, function() {
      secondFunction(args, function() {
        thirdFunction(args, function() {
          fourthFunction(args, function() {
            fifthFunction(args, function() {
              // And so on…
            })
          })
        })
      })
    })
    ```

    Definetly not the best way to write code!

But we *promise* you a way out in next exercise!

You will be able to solve this problem in a much more elegant way. 🧯

### 2 Promises 🤞

A [**Promise**](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise){target=_blank} object represents the eventual completion (or failure) of an asynchronous operation and its resulting value.

It is in one of these states:

- **`pending`**: initial state, neither `fulfilled` nor `rejected`.

- **`fulfilled`**: meaning that the operation was completed successfully.

- **`rejected`**: meaning that the operation failed.

A **`pending`** promise can either be **`fulfilled`** with a **`value`** or **`rejected`** with an **`error`**.

When either of these options happens, the associated handlers queued up by a promise's `then` and `catch` method are called:

- [Promise.then()](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/then){target=_blank}, if the promise is **`fulfilled`**, will be called with the **`value`** as its argument.

- [Promise.catch()](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/catch){target=_blank}, if the promise is **`rejected`**, will be called with the **`error`** as its argument.

![Promises](./images/promises.png)

Promises can also be **chained**, which allows orchestration of multiple asynchronous operations.

```javascript
doSomething()
  .then(result => doSomethingElse(result))
  .then(newResult => doThirdThing(newResult))
  .then(finalResult => console.log('Got the final result', finalResult))
  .catch(error => console.error('Something went wrong', error))
```

!!! tip "Callback API vs. Promise API"

    In the old days, doing several asynchronous operations in a row would lead to the classic *"nested"* callback hell / callback pyramid of doom:

    ```javascript
    doSomething(input, (doSomethingError, result) => {
      if (doSomethingError) {
        console.error('Something went wrong', doSomethingError)
      } else {
        doSomethingElse(result, (doSomethingElseError, newResult) => {
          if (doSomethingElseError) {
            console.error('Something went wrong', doSomethingElseError)
          } else {
            doThirdThing(newResult, (doThirdThingError, finalResult) => {
              if (doThirdThingError) {
                console.error('Something went wrong', doThirdThingError)
              } else {
                console.log('Got the final result', finalResult)
              }
            })
          }
        })
      }
    })
    ```

    With modern functions, we attach our callbacks to the returned promises instead, forming a *"flat"* promise chain:

    ```javascript
    doSomething()
      .then(result => doSomethingElse(result))
      .then(newResult => doThirdThing(newResult))
      .then(finalResult => console.log('Got the final result', finalResult))
      .catch(error => console.error('Something went wrong', error))
    ```

To create a new Promise we use the [new Promise() constructor](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/Promise){target=_blank}

The [constructor](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/Promise){target=_blank} accepts a single argument, the so-called **`executor` function**, which is called **immediately** when the Promise is instantiated.

The **`executor`** function is called with two arguments:

- **`resolve`**: a **`function`** that can be called to `resolve` the Promise with a `value`.

- **`reject`**: a **`function`** that can be called to `reject` the Promise with an `error`.

```javascript
const promise = new Promise((resolve, reject) => {
  doSomething(input, (error, result) => {
    if (error) {
      reject(error)
    } else {
      resolve(result)
    }
  })
})
```

#### 2.1 Use the new Promise() constructor

Let's start adjusting our `read` and `write` method so that they return a Promise, which will allow us better orchestration of our asynchronous operations.

1. Wrap the logic of the `read` method in a `new Promise()` *constructor*.

    Make sure to return the Promise instance.

    ??? example "Need help?"

        ```javascript
        someMethod(input, callback) {
          return new Promise((resolve, reject) => {
            doSomething(input, (error, result) => {
              if (error) {
                // error case
                callback(error)
              } else {
                 // success case
                // ... do something with result
                callback(null, result)
              }
            })
          })
        }
        ```

1. Wrap the logic of the `write` method in a `new Promise()` *constructor* .

    Again make sure to return the Promise instance.

    ??? example "Need help?"

        ```javascript
        someMethod(input, callback {
          return new Promise((resolve, reject) => {
            // ... do something with input
            doSomething(input, (error) => {
              if (error) {
                // error case
                callback(error)
              } else {
                // success case
                callback(null)
              }
            })
          })
        }
        ```

#### 2.2 Call resolve and reject

1. Remove the `callback` argument from the `read` method and replace the call of `callback(error)` with `reject(error)` and the call of `callback(null, pokemon)` with `resolve(pokemon)`.

    ??? example "Need help?"

        ```javascript
        someMethod(input, callback) {
          return new Promise((resolve, reject) => {
            doSomething(input, (error, result) => {
              if (error) {
                // error case
                reject(error)
              } else {
                // success case
                // ... do something with result
                resolve(result)
              }
            })
          })
        }
        ```

1. Remove the `callback` argument from the `write` method and replace the call of `callback(error)` with `reject(error)` and the call of `callback(null)` with `resolve()`.

    ??? example "Need help?"

        ```javascript
        someMethod(input, callback) {
          return new Promise((resolve, reject) => {
            // ... do something with input
            doSomething(input, (error) => {
              if (error) {
                // error case
                reject(error)
              } else {
                // success case
                resolve()
              }
            })
          })
        }
        ```

!!! tip "The Course of Events"

    Whenever we now call `pokedex.read()` or `pokedex.write()`:

    1. The `new Promise()` constructor will be called.

    1. It will create a new Promise instance which state is **`pending`**.

    1. The `executor` function will be called **immediately**!!!

    1. The `writeFile` and `readFile` operations will be triggered.

    1. Once the `writeFile` or `readFile` operations are done, their `callback` function will be called.

    1. Either **`resolve`** or **`reject`** will be called according to the success or failure of the operation.

    1. The Promise instance becomes **`settled`** with either state **`fulfilled`** or **`rejected`** according to the success or failure of the operation.

#### 2.3 Chain our Promises

Now that our `pokedex` modules provides a Promise-based API it's time to chain the methods together.

1. Moving from a Callback-based API to a Promise-based API is a breaking change.

    So there is no point in refactoring our code to use Promises.

    At this point we can simply start over. So let's only keep the first three statements in `index.js`:

    ```javascript
    import pokedex from './lib/pokedex.js'

    const type = 'fire' // fire, water, grass, electric, etc.

    console.log(`Looking for ${type} pokemon...`)
    ```

1. We start our chain of Promises by calling `pokedex.read()` which returns our initial Promise.

    We can chain the `read` method with the `then` and `catch` method of the returned Promise.

    In the **success** case the returned Promise will be resolved with the `pokemon` and `then` will be called with those resolved `pokemon`.

    In case of an **error** we can handle the `error` in the `catch` method of the returned Promise.

    ??? example "Need help?"

        ```javascript
        pokedex.read('some/file/path') {
          .then(pokemon => console.log('Got the pokemon', pokemon))
          .catch(error => console.error('Something went wrong', error))
        ```

    !!! tip "Promise.resolve()"

        Some developers also like to start a Promise chain with [Promise.resolve()](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/resolve){target=_blank} which returns a Promise that is always **`resolved`**.

        ```javascript
        Promise
          .resolve()
          .then(() => pokedex.read('some/file/path'))
          .then(pokemon => console.log('Got the pokemon', pokemon))
          .catch(error => console.error('Something went wrong', error))
        ```

        or

        ```javascript
        Promise
          .resolve('some/file/path')
          .then(path => pokedex.read(path))
          .then(pokemon => console.log('Got the pokemon', pokemon))
          .catch(error => console.error('Something went wrong', error))
        ```

        It makes the **Promise** more obvious and the code more consistent as each operation is done in a `then` block.

1. Next we want to filter the `pokemon` array by the `type` we are looking for and forward the `found` pokemon.

    For this we can simply replace the `console.log('Got the pokemon', pokemon)` with `pokedex.find(type, pokemon)`.

    The `found` pokemon will be passed to the next `then` block.

    ??? example "Need help?"

        ```javascript
        pokedex.read('some/file/path') {
          .then(pokemon => pokedex.find('some type', pokemon))
          .catch(error => console.error('Something went wrong', error))
        ```

    !!! tip "No return!?"

        If you are not familiar with the `.then(pokemon => pokedex.find(type, pokemon))` syntax.

        This is simply a **shorthand** for:

        ```javascript
        .then(pokemon => {
          return pokedex.find(type, pokemon)
        })
        ```

1. Next we want to write the `found` pokemon to a file.

    To do so we just add another `then` block to the chain.

    That's why we also say a Promise is `thenable`.

    We take the `found` pokemon and forward it to the `write` method.

    ??? example "Need help?"

        ```javascript
        pokedex
          .read('some/file/path')
          .then(pokemon => pokedex.find('some type', pokemon))
          .then(found => pokedex.write('some/other/file/path', found))
          .catch(error => console.error('Something went wrong', error))
        ```

    !!! tip "And then()?"

        In a `then` block we can do several things

        - **return a synchronous value or call synchronous methods**

            ```javascript
            .then(value => {
              const syncResult = syncMethod(value)
              return syncResult
            })
            ```

        - **throw a synchronous error**

            ```javascript
            .then(value => {
              const syncResult = syncMethod(value)
              if (!syncResult) {
                throw new Error('Sync method failed')
              }
              return syncResult
            })
            ```

        - **return a Promise, that will be resolved or rejected**

            ```javascript
            .then(value => {
              const promise = asyncMethod(value)
              return promise
            })
            ```

            which is the same as

            ```javascript
            .then(value => asyncMethod(value))
            ```

1. Run `npm start` again to ensure that the async program still works as expected, e.g. change the `type` and check the existence of the `data/<type>.json` file and its content.

That's it we have succesfully chained our Promises and our code is now much more readable. 👏

We have basically *flattened* our *nested* callbacks.

But wait **what in case of an error?**

That's maybe the best part: we can now handle any error that might occur in our chain at a single place: the `catch` block.

Which is much more maintainable and readable as handling each error individually as in a Callback scenario.

**Every synchronous error that gets thrown or any Promise that gets rejected will be directly forwared to the `catch` block.**

Any `then` block in between will be skipped. Just like as in a synchronous `try`/`catch` block.

E.g. if `pokedex.read('./data/pokemon.json')` would fail we could handle the error in the `catch` block and everything in between would be skipped.

!!! tip "Anitpatterns"

    **Avoid nested promises in a `then` block**

    ```javascript
    Promise
      .resolve()
      .then(() => {
        // DON'T DO THIS
        return pokedex.read('some/file/path')
          .then(pokemon => pokedex.find('some type', pokemon))
          .then(found => pokedex.write('some/other/file/path', found))
      })
      .catch(error => console.error('Something went wrong', error))
    ```

    This chain can simply be flattened:

    ```javascript
    Promise
      .resolve()
      .then(() => pokedex.read('some/file/path'))
      .then(pokemon => pokedex.find('some type', pokemon))
      .then(found => pokedex.write('some/other/file/path', found))
      .catch(error => console.error('Something went wrong', error))
    ```

    **Avoid any unnecessary `new Promise()` constructor**

    ```javascript
    getUser() {
      // DON'T DO THIS
      return new Promise((resolve, reject) => {
        this.#fetchUser()
          .then(user => resolve(user))
          .catch(error => reject(error))
      })
    }
    ```

    Instead return the Promise directly:

    ```javascript
    getUser() {
      return this.#fetchUser()
    }
    ```

    **Avoid multiple catch blocks**

    There should only be one `catch` block at the end chain. Just like as in a synchronous `try`/`catch` block.

    ```javascript
    Promise
      .resolve()
      .then(() => pokedex.read('some/file/path'))
      // DON'T DO THIS
      .catch(error => console.error('Something went wrong', error)) // will "swallow" any read error
      .then(pokemon => pokedex.find('some type', pokemon)) // pokemon will be undefined in case of a read error
      .then(found => pokedex.write('some/other/file/path', found))
      .catch(error => console.error('Something went wrong', error))
    ```

    A catch block will "swallow" any error thrown above.

    E.g. in above example the 1st `catch` would "swallow" any read error. The promise chain would then continue to the next `then` block which would receive `pokemon` with value `undefined`. The last `catch` will never be executed in case of an *read* error, but only in the case of a *find* or *write* error.

#### 2.4 Promisify

Let's got back to our `write` and `read` methods in our `pokedex` module.

There seems to be a pattern how we wrap the `read` and `write` methods in Promises.

Because of the *Error-first callbacks* convention we can convert any function that follows this convention using the `Promise` constructor with the same pattern.

This is called **Promisification** or **Promisifying**.

Because this is a common pattern we can use the [promisify](https://nodejs.org/docs/latest-v16.x/api/util.html#utilpromisifyoriginal){target=_blank} method of the built-in [Util](https://nodejs.org/docs/latest-v16.x/api/util.html){target=_blank} module.

1. Import the `promisify` method from the `util` module

    ??? example "Need help?"

        ```javascript
        import { promisify } from 'util'
        ```

2. Promisify the `readFile` and `writeFile` methods from the `fs` module

    ??? example "Need help?"

        ```javascript
        import { promisify } from 'util'
        import { readdir } from 'fs'

        const readdirPromise = util.promisify(readdir)

        // ...

        readDirectory(path) {
          return readdirPromise(path)
            .then(files => files.map(file => `${path}/${file}`))
        }
        ```

3. Use the promisified methods instead of using the `new Promise()` constructor in the `write` and `read` methods in our `pokedex` module.

    ??? example "Need help?"

        ```javascript
        someMethod(input)) {
          return someMethodPromise(input)
            .then(result => doSomething(result)))
        }
        ```

        ```javascript
        someOtherMethod(input, data) {
          const result = doSomethingElse(data)
          return someOtherMethodPromise(input, result)
        }
        ```

The `pokedex` code is much cleaner now.

#### 2.4 Use the Promise API

At this point you might think why is this so cumbersome? Why does the [File System](https://nodejs.org/api/fs.html){target=_blank} module not provide a Promise API in the first place?

Actually it does: [fs/promises](https://nodejs.org/api/fs.html#promises-api){target=_blank}.

1. Remove the import for the `util` module and the code for promisifying the `readFile` and `writeFile` methods.

1. Instead import the the [fs/promises](https://nodejs.org/api/fs.html#promises-api){target=_blank} module.

    ??? example "Need help?"

        ```javascript
        import { readFile, writeFile } from 'fs/promises'
        ```

1. Instead of using the promisified methods, e.g. `readFilePromise` and `writeFilePromise`, use the `readFile` and `writeFile` methods from [fs/promises](https://nodejs.org/api/fs.html#promises-api){target=_blank} directly.

    ??? example "Need help?"

        ```javascript
        read(path) {
          return readFile(path)
            .then(data => JSON.parse(data))
        }
        ```

        ```javascript
        write(path, pokemon) {
          const data = JSON.stringify(pokemon, null, 2)
          return writeFile(path, data)
        }
        ```

You have now come full circle and have earned some syntactic sugar 🍭 which we will provide in the next exercise.

### 3 async / await 🚍🚏

Let's get to the best part of this exercise: the [async / await](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/async_function){target=_blank} syntax.

The `async` and `await` keywords enable asynchronous, **Promise-based** behavior to be written in a cleaner style, avoiding the need to explicitly configure Promise chains.

First we have to *declare* a function with the `async` keyword.

```javascript
async function foo() {
   return 1
}
```

!!! tip "Async functions always return a promise"

    If the return value of an async function is not explicitly a promise, it will be implicitly wrapped in a promise.

    So above example is similar to:

    ```javascript
    function foo() {
      return Promise.resolve(1)
    }
    ```

Once we have declared the function `async` we can use the `await` keyword to wait for a Promise to resolve and to assign its value to a variable.

```javascript
async getUser() {
  const user = await this.#fetchUser()
  return user
}
```

Await expressions make Promise-returning functions behave as though they're synchronous by suspending execution until the returned Promise is fulfilled or rejected.

**The resolved value of the promise is treated as the return value of the await expression.**

Use of async and await enables the use of ordinary `try` / `catch` blocks around asynchronous code.

```javascript
async getUser() {
  let user = null
  try {
    user = await this.#fetchUser()
  } catch (error) {
    console.error('Something went wrong', error)
  }
  return user
}
```

Let's adjust our `pokedex` module to use the `async` / `await` syntax.

1. Declare the `read` and `write` methods as `async` functions.

    ??? example "Need help?"

        ```javascript
          async someMethod(input) {
            // ...
          }
        ```

1. Use the `await` keyword to wait for the `readFile` and `writeFile` methods to resolve.

    Make sure to assign any return value, e.g. `data` to a variable.

    Besides everything else can be implemented in a synchronous way.

    ??? example "Need help?"

        ```javascript
        async someMethod(input) {
          const data = await doSomething(input) // await the Promise to be resolved
          const result = doSomethingElse(data) // some synchronous operation
          return result // will return a Promise which resolves with result
        }
        ```

        ```javascript
        async someOtherMethod(input, data) {
          const result = doSomething(data) // some synchronous operation
          await doSomethingElse(input, result) // await the Promise to be resolved
        } // will return a Promise which resolves with undefined
        ```

1. Run `npm start`.

    Our application in `index.js` should still work as we did not break the `pokedex` API.

    We are still returning a `Promise` from the `read` and `write` methods!

1. Let's also clean up our `index.js` and get rid of the promise chain. Use the `await` keyword to wait for the `pokedex.read` and `pokedex.write` methods to resolve.

    Make sure to assign any return value, e.g. `pokemon` to a variable.

    Besides everything else can be implemented in a synchronous way.

    !!! tip "Wait a sec: `await` but no `async`?"

        This feature is called **[Top-level await](https://v8.dev/features/top-level-await)**.

        It enables developers to use the `await` keyword outside of `async` functions.

        It acts like a big async function causing other modules who import them to wait before they start evaluating their body.

        **The old behavior**

        ```javascript
        await Promise.resolve(console.log('🎉'))
        // → SyntaxError: await is only valid in async function

        (async () => {
          await Promise.resolve(console.log('🎉'))
          // → 🎉
        })()
        ```

        This pattern is called [Async IIFE (Immediately Invoked Function Expression)](https://anthonychu.ca/post/async-await-typescript-nodejs/){target=_blank}

        **The new behavior**

        ```javascript
        await Promise.resolve(console.log('🎉'))
         // → 🎉
        ```

1. Wrap the code in a `try` / `catch` block to handle any potential error.

    ??? example "Need help?"

        **Top-level await**

        ```javascript
        import pokedex from './lib/pokedex.js'

        const type = 'electric' // fire, water, grass, electric, etc.

        try {
          console.log(`Looking for ${type} pokemon...`)
          const pokemon = await pokedex.read('./data/pokemon.json')
          const found = pokedex.find(type, pokemon)
          await pokedex.write(`./data/${type}.json`, found)
        } catch (error) {
          console.error('Something went wrong', error)
        }
        ```

        **Async IIFE**

        ```javascript
        import pokedex from './lib/pokedex.js'

        (async () => {
          try {
            const type = 'electric' // fire, water, grass, electric, etc.
            console.log(`Looking for ${type} pokemon...`)
            const pokemon = await pokedex.read('./data/pokemon.json')
            const found = pokedex.find(type, pokemon)
            await pokedex.write(`./data/${type}.json`, found)
          } catch (error) {
            console.error('Something went wrong', error)
          }
        })()
        ```

1. Run `npm start` to ensure that the async program works as expected, e.g. change the `type` and check the existence of the `data/<type>.json` file and its content.

Well done! You have successfully implemented the `async` / `await` syntax. 👩‍🍳👨‍🍳

!!! tip "Sync. vs. Callbacks vs. Promises vs. async / await"

    **Sync.**

    *Blocking code*

    ```javascript
    import pokedex from './lib/pokedex.js'

    const type = 'fire' // fire, water, grass, electric, etc.

    try {
      console.log(`Looking for ${type} pokemon...`)
      const pokemon = pokedex.read('./data/pokemon.json')
      const fire = pokedex.find(type, pokemon)
      pokedex.write(`./data/${type}.json`, fire)
      console.log(`... done!`)
    } catch (error) {
      console.error(error)
    }
    ```

    **Callbacks**

    *No blocking code but a Callback Hell*

    ```javascript
    import pokedex from './lib/pokedex.js'

    const type = 'fire' // fire, water, grass, electric, etc.

    console.log(`Looking for ${type} pokemon...`)
    pokedex.read('./data/pokemon.json', (readError, pokemon) => {
      if (readError) {
        console.error('Something went wrong', readError)
      } else {
        const found = pokedex.find(type, pokemon)
        pokedex.write(`./data/${type}.json`, found, (writeError) => {
          if (writeError) {
            console.error('Something went wrong', writeError)
          } else {
            console.log('... done!')
          }
        })
      }
    })
    ```

    **Promises**

    *Better code orchestration and error handling, but with the Promise.then().catch() boilerplate*

    ```javascript
    import pokedex from './lib/pokedex.js'

    const type = 'fire' // fire, water, grass, electric, etc.

    pokedex
      .read('./data/pokemon.json')
      .then(pokemon => pokedex.find(type, pokemon))
      .then(found => pokedex.write(`./data/${type}.json`, found))
      .catch(error => console.error('Something went wrong', error)
    ```

    **async / await**

    *The code looks very similar to the sync. version but with the benefit that it does not block*

    ```javascript
    import pokedex from './lib/pokedex.js'

    const type = 'electric' // fire, water, grass, electric, etc.

    try {
      console.log(`Looking for ${type} pokemon...`)
      const pokemon = await pokedex.read('./data/pokemon.json')
      const found = pokedex.find(type, pokemon)
      await pokedex.write(`./data/${type}.json`, found)
    } catch (error) {
      console.error('Something went wrong', error)
    }
    ```

### 4 Orchestrating Tasks 🎼🎻

In a real world application you would have a lot of asynchronous tasks to perform.

So it is important to know how to orchestrate them correctly.

Before we get started let's do some refactoring in our `index.js` file.

1. Create a *async* function `lookForPokemon` that accepts a single argument `type`.

    The function should encapsulate the logic to our calls to the methods of the `pokedex` module.

    Make sure to use and *await* the function in our `try` / `catch` block passing the `type` argument.

    ??? example "Need help?"

        ```javascript
        import pokedex from './lib/pokedex.js'

        const lookForPokemon = async type => {
          console.log(`Looking for ${type} pokemon...`)
          const pokemon = await pokedex.read('./data/pokemon.json')
          const found = pokedex.find(type, pokemon)
          await pokedex.write(`./data/${type}.json`, found)
        }

        try {
          const type = 'electric' // fire, water, grass, electric, etc.
          await lookForPokemon(type)
        } catch (error) {
          console.error('Something went wrong', error)
        }
        ```

We are now ready to look for pokemon of multiple types.

#### 4.1 Sequentially

Let's look for some types of pokemon **one after another** (*`sequentially`*).

1. Instead of looking for just one `type` declare an array of multiple `types` to look for.

    ??? example "Need help?"

        ```javascript
        const types = ['fire', 'water', 'grass', 'electric']
        ```

1. Use a [for...of loop](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/for...of){target=_blank} to iterate over the `types` array.

    Make sure to *await* the `lookForPokemon` call for each `type`, so that we only start the next iteration of the loop after the previous one has finished.

    ??? example "Need help?"

        ```javascript
        try {
          const types = ['fire', 'water', 'grass', 'electric']
          for (const type of types) {
            await lookForPokemon(type)
          }
        } catch (error) {
          console.error('Something went wrong', error)
        }
        ```

    ??? tip "Promises Sequentially"

        Without the `async / await` keywords we could use [Array.reduce()](https://developer.mozilla.org/de/docs/Web/JavaScript/Reference/Global_Objects/Array/Reduce){target=_blank} to achieve the same result.

        ```javascript
          types.reduce((promise, type) => {
            return promise.then(() => lookForPokemon(type))
          }, Promise.resolve())
        ```

1. Run `npm start` to ensure that the async program works as expected, e.g. change the `types` and check the existence of the `data/<type>.json` **files**  and their content.

#### 4.2 Concurrently

You really only need to await one thing after the other if the second value is dependent on the first value, e.g. if you need to get a user id before you can get a user from the database.

As each type of pokemon is independent of the others, we can look for them **at the same time** (*`concurrently`*).

1. Make yourself familiar with [Promise.all()](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/all){target=_blank} which takes an iterable of promises as an input, and returns a single Promise that resolves to an array of the results of the input promises.

    ```javascript
    const [result1, result2, result3] = await Promise.all([
      task1(),
      task2(),
      task3()
    ])
    ```

1. Make yourself familiar with [Array.map()](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/map){target=_blank} which creates a new array populated with the results of calling a provided function on every element in the calling array.

    ```javascript
    const squareNumbers = [1, 2, 3, 4, 5].map(n => Math.pow(n, 2))
    ```

1. Use [Promise.all()](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/all){target=_blank} and [Array.map()](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/map){target=_blank} to look for the types of pokemon concurrently.

    ??? example "Need help?"

        **Short version**

        ```javascript
        const list = ['foo', 'bar']
        const results = await Promise.all(list.map(entry => doSomething(entry)))
        ```

        **Comprehensive version**

        ```javascript
        const list = ['foo', 'bar']
        const results = await Promise.all(list.map(async (entry) => {
          const result = await doSomething(entry)
          return result
        }))
        ```

1. Run `npm start` to ensure that the async program works as expected, e.g. change the `types` and check the existence of the `data/<type>.json` **files**  and their content.

Running tasks concurrently is a great way to improve the performance of your application, if tasks are **independent of each other**.

But you should also be careful to **not run too many tasks at the same time**, e.g. sending 100000 requests at the same time might not be a good idea. Instead you should limit the number of requests you send at the same time, e.g. using [bluebird](http://bluebirdjs.com/docs/api/promise.map.html#map-option-concurrency){target=_blank}.

## 🏁 Summary

Nice job! 🥳 You have successfully learned how to

- create and use Callback-based APIs

- create and use Promise-based APIs

- use the async/await syntax

- orchestrate async tasks

You have successfully become a Promise Ninja 🥷!

## 🏕 Survival Guide

1. [Check which ECMAScript features are supported by your Node.js version](https://node.green){target=_blank}

1. Use Promise-based APIs whenever possible.

1. If only Callback-based APIs are available use Promisification.

1. Use the `async` / `await` syntax whenever possible.

## 🦄 Stretch Goals

### Hybrid APIs 🚗

Sometimes you will encounter so-called **Hybrid APIs** which support both Callbacks and Promises.

Normally the Callbacks API is already deprecated but still keept for supporting legacy code and the Promises API is used for modern code.

1. Implement a Hybrid API for the read `method` in the `pokedex` module

    - If a (optional) `callback` function is passed to the method, the callback should be used and the method should return `undefined`.

    - Otherwise the method should return a `Promise`.

    - **Hint**: You cannot use `async` / `await` here, as it would always return a Promise!

    ??? example "Need help?"

        ```javascript
        read(path, callback) {
          if (typeof callback === 'function') {
            // use callback ...
            return
          } else {
            return new Promise((resolve, reject) => {
              // settle promise ...
            })
          }
        }
        ```

## 📚 Recommended Readings and Watchings

- [Using Promises](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Using_promises){target=_blank}

- [Jake Archibald on Promises](https://web.dev/promises/){target=_blank}

- [Fireship - The Async Await Episode](https://www.youtube.com/watch?v=vn3tm0quoqE){target=_blank}

- [Jake Archibald: In The Loop](https://www.youtube.com/watch?v=cCOL7MC4Pl0){target=_blank}

- [JavaScript Asynchronous Programming and Callbacks](https://nodejs.dev/learn/javascript-asynchronous-programming-and-callbacks){target=_blank}

- [Understanding JavaScript Promises](https://nodejs.dev/learn/understanding-javascript-promises){target=_blank}

- [You Don't Know JS: Async & Performance](https://github.com/getify/You-Dont-Know-JS/blob/1st-ed/async%20%26%20performance/ch3.md#chapter-3-promises){target=_blank}

## 🔗 Related Courses

- [JavaScript Promises and Async Programming](https://app.pluralsight.com/library/courses/javascript-promises-async-programming/table-of-contents){target=_blank}
