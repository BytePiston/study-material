# Service to Service Communication

_Disclaimer: We are [counting page hits](https://github.wdf.sap.corp/cloud-native-dev/usage-tracker) using a cookie to distinguish returning & new visitors._
<img src="https://cloud-native-dev-usage-tracker.cfapps.sap.hana.ondemand.com/pagehit/cc-materials/service2service-nodejs/1x1.png" alt="" height="1" width="1">

## 👷‍♂️👷‍♀️ Audience

Developers with basic knowledge of Node.js and HTTP-Rest

## 🎯 Learning Objectives
In this exercise you will learn:

- how to make a synchronous call to an existing service
- how to test the service client

<!-- Prerequisites-->
{% with
  tools=[
    ('**HTTP client** like [Postman](https://www.postman.com/downloads/) or [cURL](https://linuxize.com/post/curl-post-request/) (cURL commands are provided)')
  ],
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

{% with branch_name="service2service", folder_name="service2service-nodejs" %}
{% include 'snippets/clone-import/nodejs.md' %}
{% endwith %}

--8<--- "snippets/npm-install-dependencies.md"

Now you can run the application using the following command:
```shell
npm start
```

When the Application has started you should be able to see a line similar to the following in the console:
```
Users app listening at http://localhost:3000
```

## 🔍 Code Introduction

We have set up a simple application that displays users of the application.

There is a GET endpoint provided under `/api/v1/users/{userId}/page` that should display user information in an embellished way, but so far the endpoint only prints the user information as is which is not very pretty.

Additionally there is a GET endpoint provided under `/api/v1/users/{userId}` to get user objects.
Initially there are two users with IDs `0` and `1`.

## 📗 Exercise
In this exercise, you will implement the communication to the AsciiArt service located at https://service2service-endpoint.cfapps.eu10.hana.ondemand.com/.

The AsciiArt service provides the functionality to convert strings into ascii art, which we are going to use in the user's "pretty" page (`/api/v1/users/{userId}/page`).

### 1 - Explore the AsciiArt Service

The AsciiArt service team has provided the endpoint specification in their `README.md`.

- Go to the [AsciiArt service repo](https://github.tools.sap/cloud-curriculum/s2s-exercise-endpoint) and explore the endpoint specs in the README file.

### 2 - Install HTTP-Client
Node.js' `HTTP` module allows you to make HTTP requests but it is rather low-level and requires a fair amount of boilerplate code.
For this exercise we prefer to use the `node-fetch` package, which aims to bring the browser's [Fetch API](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API) to Node.js.

- Run the following command to install `node-fetch`:
```
npm install node-fetch
```

### 3 - Implement the Ascii-Art Service Client

We want the `AsciiArtServiceClient` class to implement the HTTP communication that enables us to use the AsciiArt service.

Let's modify the existing `should call service` test.

1. In the test, call `getAsciiString` on the `client` object with `await` and the following object as argument:
    ```javascript
    {
        toConvert: 'Hello World',
        fontId: '1'
    }
    ```

1. Before calling `getAsciiString`, assign a function to the `fetchMock` variable with two arguments `url` and `options` in which you assert that:
    - the url is equal to `client.servicePath`
    - the `method` property of `options` is `'POST'`
    - the `headers` property of `options` is equal to: `{ 'Content-Type': 'application/json' }` (use the `deepEqual`-function to compare objects)
    - the `body` property of `options` is equal to `'{"toConvert":"Hello World","fontId":"1"}'`

    ??? example "Need help?"
        ```javascript
        fetchMock = (url, options) => {
            assert.equal(url, client.servicePath)
            assert.equal(options.method, 'POST')
            assert.deepEqual(options.headers, { 'Content-Type': 'application/json' })
            assert.equal(options.body, '{"toConvert":"Hello World","fontId":"1"}')
        }
        ```

1. Import the `Response constructor` from `node-fetch` and create a `Response` object inside `fetchMock`, with another arbitrary string as body.

    ??? example "Need help?"
        See the [documentation](https://developer.mozilla.org/en-US/docs/Web/API/Response/Response) for details on the parameters.

        ```javascript
        import { Response } from 'node-fetch'

        const response = new Response('Pretty Hello World')
        ```

1. Make `fetchMock` return a resolved `Promise` of the response.

1. Assert that the (`await`ed) result of the call to `getAsciiString` is equal to the string passed to the `Response` constructor.

1. Run the test to make sure it's failing.

1. Implement the `getAsciiString` method to make the test pass.
    You will need to call `#!javascript this.#fetch` with the appropriate arguments.
    The test expects a string for the body, so make sure to use `JSON.stringify`.
    Then, `await` the response and return `response.text()`.

1. Run the test to make sure it's passing.

### 4 - Test and Implement Invalid Request Case

The AsciiArt service will return a `HTTP 400 BAD_REQUEST` with invalid request parameters, i.e. unknown `fontId`.
Our service should be able to handle this scenario since the `fontId` is chosen freely by the user during sign-up.

1. Create a new test in `ascii-art-service-client.test.js` called `should throw invalid request exception`.

1. Inside the test, assign a function to the `fetchMock` variable, which returns a `Promise` resolving to a `Response` object with the status `400`.

    ??? example "Need help?"
        ```javascript
        fetchMock = () => {
            return Promise.resolve(new Response(null, { status: 200 }))
        }
        ```

1. Call `getAsciiString` on the `client` object. You can reuse the argument from the other test.

1. Assert that the returned `Promise` is rejected with an error that has the message "Invalid request". Note that the `rejects` function from the `assert` module needs to be `await`ed.

    ??? example "Need help?"
        ```
        await assert.rejects(promise, {
            message: 'Expected error message'
        })
        ```

1. Run the test to make sure it's failing.

1. In the `AsciiArtServiceClient`, throw an `Error` with the message "Invalid request" if the response's status is `400`.

1. Run the test to make sure it's passing.

### 5 - Use the AsciiArt Service Client
The service client seems to be ready for use.
We can now obtain the converted ascii art for any string from the AsciiArt-service.

#### 5.1 Adjust a Test
Let's use this feature to convert the name before serving it in the pretty page (`/api/v1/users/{id}/page`) of a user, along with their (plain) phone number.
We want the `PrettyUserPageCreator` to use the `AsciiArtServiceClient` so let's adjust its test first.

1. Open `pretty-user-page-creator.test.js` and declare a variable `asciiArtServiceClientMock` in the same scope as `userPageCreator`.

1. Inside the `beforeEach` block, assign an empty object to `asciiArtServiceClientMock` and pass it to `PrettyUserPageCreator`'s constructor as parameter.

1. Inside the `should create a pretty user page` test, mock the `getAsciiString` method with the following snippet:
    ```javascript
    asciiArtServiceClientMock.getAsciiString = (request) => {
        assert.equal(request.toConvert, 'John')
        assert.equal(request.fontId, '2')
        return Promise.resolve('"John" written in a pretty way')
    }
    ```

1. Replace `John` with `"John" written in a pretty way` in the assertion at the bottom.

1. Run the test to make sure it's failing.

#### 5.2 Make the Test Pass

First you will have to inject the `AsciiArtServiceClient` into the `PrettyUserPageCreator`.

1. Add a constructor function to the `PrettyUserPageCreator` class in `pretty-user-page-creator.js` with one argument named `asciiArtServiceClient`.

1. Inside the `PrettyUserPageCreator` constructor: Assign the variable `asciiArtServiceClient` to the private class field `#asciiArtServiceClient`.

    ??? example "Need help?"
        ```javascript
        class SomeClass {
            #injectedObject = null

            constructor(injectedObject) {
                this.#injectedObject = injectedObject
            }
        }
        ```

1. Inside the `createUserPage` function: create an `asciiArtRequest` object like in the following snippet:
    ```javascript
    const asciiArtRequest = {
        toConvert: user.name,
        fontId: user.fontPreference
    }
    ```

1. Acquire the prettified username by calling `getAsciiString` on the `#asciiArtServiceClient` with the `asciiArtRequest`.

1. Replace `user.name` with the pretty username in the returned string.

1. Run the tests to make sure it's passing. But another test might fail!

### 6 - Fix the Integration Test
The test `should create, return user and print a pretty page` from the `App-Integration` block is failing.
If you scroll up to see the logged error you will find the cause:
```
TypeError: Cannot read property 'getAsciiString' of undefined
```

#### 6.1 Import the AsciiArtServiceClient
Seems like we forgot to inject an instance of `AsciiArtServiceClient` into the `PrettyUserPageCreator` in this test.

1. Open the file `app.integration.test.js` and import the class `AsciiArtServiceClient`:
    ```javascript
    import AsciiArtServiceClient from '../lib/ascii-art-service-client.js'
    ```

#### 6.2 What to Mock?
We could mock the `fetch` function again, as we did in the `AsciiArtServiceClient`-test, but since this test covers multiple components, using a test double at such a low level might soon become a great maintenance burden and make the test very brittle.
It would be nice if we could somehow mock the external service without needing to inject test doubles into our classes.
Luckily there are packages which let us do just that!

1. Run the following command to install the package `mockttp` which will allow us to mock the Ascii-art-service:
    ```shell
    npm install --save-dev mockttp
    ```

1. Import the dependency into the integration test, using the following code snippet:
    ```javascript
    import mockttp from 'mockttp'
    ```

1. Get an instance of `mockServer` and start and stop it using the `beforeEach` and `afterEach` hooks within the `App-integration` block:
    ```javascript
    const mockServer = mockttp.getLocal()

    beforeEach(() => mockServer.start())

    afterEach((() => mockServer.stop()))
    ```

#### 6.3 Create an AsciiArtServiceClient Instance

1. Inside the test: Create an instance of `AsciiArtServiceClient`.
   You will need to provide two arguments to its constructor:

    1. The `fetch` function, which you will need to import:
        ```javascript
        import fetch from 'node-fetch'
        ```

    1. An "options" object containing a property called `servicePath`.
        Use the string returned by the following function call:
        ```javascript
        mockServer.urlFor('/asciiArt')
        ```

    ??? example "Need help?"
        ```javascript
        const asciiArtServiceClient = new AsciiArtServiceClient(fetch, {
            servicePath: mockServer.urlFor('/mocked-path')
        })
        ```

1. Use the created instance as an argument in the `PrettyUserPageCreator`'s constructor call.

1. Before the call to the page endpoint happens, you need to tell the mock-server what to do when it receives a request:
    ```javascript
    await mockServer.post('/asciiArt').thenReply(200, 'A pretty name')
    ```

1. Replace the first `.*` in the final `match`-assertion with `A pretty name`.

1. Run the tests. All should be passing.

### 7 - Manual Test
The tests increase our confidence that the components work together. But have you actually tried running the application?

- Run your application and manually test the endpoint `/api/v1/users/{id}/page`.
    You can test with the default users with ids 0 and 1.

You will find that the app is not yet wired up correctly!

#### 7.1 Fix the Integration
We fixed the setup in the integration test, but not in the app's actual entry point!

1. Open `server.js` and import `AsciiArtServiceClient` and `node-fetch`:
    ```javascript
    import AsciiArtServiceClient from './ascii-art-service-client.js'
    import fetch from 'node-fetch'
    ```

1. Create an instance of `AsciiArtServiceClient`, this time using the "real" `fetch` (you can omit the 2nd parameter this time).

1. Pass the instance to the `PrettyUserPageCreator` constructor call.

1. Restart your application and hit the user page endpoint again.

You should see the user name displayed in some fancy ascii art.

### 8 - Update the API Version
You have been informed that the API of the AsciiArt service has recently been updated.
The new API is accessible at `/api/v2/asciiArt`.

1. Adjust the `servicePath` in the `AsciiArtServiceClient` to target the new API.

1. Restart your application and invoke the endpoint `/api/v1/users/pretty/{id}`.

    Are you getting an error? Hm...perhaps the updated endpoint is not working properly.

1. What happens if you invoke the POST-endpoint of the AsciiArt service?

    Invoke the POST-endpoint on `https://service2service-endpoint.cfapps.eu10.hana.ondemand.com/api/v2/asciiArt/` with the JSON-body:
    ```JSON
    {
        "toConvert": "G. Harrison",
        "fontId": "7"
    }
    ```

    ??? Example "Invoke with cURL"
        ```bash
        curl --header "Content-Type: application/json" --data '{"toConvert": "G. Harrison", "fontId": "7"}' https://service2service-endpoint.cfapps.eu10.hana.ondemand.com/api/v2/asciiArt/
        ```
        Add `-v` or `--verbose` to print additional info like http status code and headers.

1. Seems like the other service's team has broken something.
    Of course the service being broken is something designed in this exercise for illustrative purposes.
    It should induce you to think about following questions:

    - The other team broke the service. Is it their fault that your whole application is not working anymore?
    - How does my service handle the broken communication to other services? Does my service crash?

## 🏁 Summary
Good job!

You have learned:

* [x] how to call an existing service synchronously from your microservice.
* [x] about the risks of synchronous calls to other services.

## 🦄 Stretch Goals
Not enough? Go ahead and achieve the Stretch Goals:

- Instead of defining your own mocks in the tests, use [sinon](https://sinonjs.org/) instead.

- Implement fallback behavior to deal with the functional outage of the AsciiArt Service.
    - Catch the `Internal Server Error` of the AsciiArt service and introduce your error handling.
    - The pretty page should at least display the users information without layout.

## 📚 Recommended Reading
- [Design Patterns for Microservice-To-Microservice Communication](https://dzone.com/articles/design-patterns-for-microservice-communication)

## 🔗 Related Topics
- [Data Transfer Object (DTO)](https://martinfowler.com/eaaCatalog/dataTransferObject.html)
