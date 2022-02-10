# 🧾 Node.js Logging Basics

_Disclaimer: We are [counting page hits](https://github.wdf.sap.corp/cloud-native-dev/usage-tracker) using a cookie to distinguish returning & new visitors._
<img src="https://cloud-native-dev-usage-tracker.cfapps.sap.hana.ondemand.com/pagehit/cc-materials/logging-nodejs/1x1.png" alt="" height="1" width="1">

## 👷‍♂️👷‍♀️ Audience

Developers with basic to intermediate knowledge in JavaScript and Node,js, who want to take advantage of an adequate logging setup.

## 🎯 Learning Objectives

In this exercise you will learn

- how to integrate a logger framework (demonstrated with [Winston](https://github.com/winstonjs/winston)) into a Node.js project
- how to use different logging levels and formats
- how to enrich the logs with meta information

<!-- Prerequisites-->
{% with
  required=[
    ('[Express](https://expressjs.com/)'),
    ('[JavaScript Classes](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Classes)'),
    ('[Private class features](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Classes/Private_class_fields)')
  ],
  beneficial=[
    ('[Winston](https://github.com/winstonjs/winston)')
  ]
%}
{% include 'snippets/prerequisites/nodejs.md' %}
{% endwith %}

## 🛫 Getting Started

{% with branch_name="logging", folder_name="logging-nodejs" %}
{% include 'snippets/clone-import/nodejs.md' %}
{% endwith %}

--8<--- "snippets/npm-install-dependencies.md"

You can now run the application using the following command:

```shell
npm start
```

When the application has started you should be able to see the following log message in the console

```shell
Server started on port 3000
```

You can now use following URLs to consume the endpoints.

[http://localhost:3000/hello](http://localhost:3000/hello)

You can also try to hand a parameter to the call as follows:

[http://localhost:3000/hello?name=Mars](http://localhost:3000/hello?name=Mars)

**File changes only become effective, once the application is restarted!**

To save you from having to restart the application yourself, you can start the application with:

```shell
npm run watch
```

The watch script uses [nodemon](https://www.npmjs.com/package/nodemon) to watch the source files and restart the server whenever a change is detected.

## 🔍 Code Introduction

We have set up a simple express application which provides two endpoints that produce a greeting with the passed `name` parameter.

You can see the implementation of the endpoints in file `application.js`.

You will see that each of the endpoints (`/hello` and `/howdy`) call the `createGreeting` method of the `GreetingService` class (file `greeting-service.js`) with the arguments `"Hello"` and `"Howdy"` respectively.

The `createGreeting` method validates the provided name by checking whether it contains a number.

If the name is valid a greeting is returned, otherwise an error is thrown.

The `/howdy` endpoint is *deprecated* as pointed out by the `log.error('Deprecated endpoint used!')` call in file `application.js`.

In file `logger.js` we will encapsulate the logger functionality.

The main entry point is the file `server.js` which brings everything together, injects all dependencies, and starts the application.

The following exercises let you engage in the task of setting up useful logs and the respective configuration for it.

!!! tip "Encapuslate the logger functionality"

    It is usually a good idea to encapsulate / wrap any third-party modules you are using, e.g. for logging or persistence.

    This ensures that if the third-party module changes it's API or even if you exchange the module with another one, you don't have to change your code besides the encapsulation.

    Other consumers within (or outside) your application can keep using your wrapped / encapsualted API, which will always be stable.

## 📗 Exercises

### 1 Logging Basics 📝

There are several different logging frameworks out there.
We have chosen [Winston](https://github.com/winstonjs/winston) because it provides all our desired features, is widely used and is even used internally in SAPs logging library.
However, you should be able to easily transfer the skills you gained from these exercises to other logging frameworks, as most of them work in a similar manner.

!!! warning "Choosing a logging framework"
    Other logging frameworks might better suit your needs.
    For any project you should base your framework choice on your specific requirements.
    Some other possible frameworks are [Pino](https://www.npmjs.com/package/pino) and [Roarr](https://www.npmjs.com/package/roarr).

#### 1.1 Install Winston 💾

You can check out the winston package here:

<a href="https://www.npmjs.com/package/winston" rel="nofollow"><img src="https://nodei.co/npm/winston.png?downloads=true&ampdownloadRank=true" alt="NPM"></a>

1. Install it with the following command:

    ```shell
    npm install winston
    ```

1. In file `logger.js` add the following at the very start of the file to import the winston module.

    ```javascript
    import winston from 'winston'
    ```

#### 1.2 Create a Logger Instance 📠

Now that we have imported winston we can use the required functions to create a logger instance.

In file `logger.js`:

Create and export a `logger` instance which logs to the `Console` *transport* with the `createLogger()` function.

```javascript
const { createLogger, transports } = winston
const { Console } = transports

const logger = createLogger({
  transports: [
    new Console()
  ]
})

export default logger
```

??? info "Code Walkthrough"
    The `createLogger()` function takes a configuration object as parameter and returns a logger instance.
    In the above snippet we are configuring our logger with a console transport.
    Transports specify where our logs are written to.
    By default winston does not configure any transport, so the logs do not get written anywhere.
    To make them visible we specify a `Console` transport in the configuration object.
    Notice that the `transports` property takes an array, implying that you can specify multiple transports.
    The following exercises will not further mention transports, but if you would like to, you can read more about them [here](https://github.com/winstonjs/winston#transports).

??? tip "Use Javascripts destructuring assignment syntax"
    Use Javascripts [destructuring assignment syntax](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/Destructuring_assignment) to specify what you need:

    ```javascript
    const { createLogger, transports: { Console } } = winston
    ```

    That way you don't have to type `winston.*` in front of every winston functionality you are using, resulting in shorter code like this:

    ```javascript
    const logger = createLogger({
      transports: [
        new Console()
      ]
    })
    ```

#### 1.3 Using the Logger 📝

Let's put our newly created logger instance to use.

In file `server.js`:

1. Import the `logger`.

    ```javascript
    import logger from './util/logger.js'
    ```

1. Replace the call to `console.error` with `logger.error`.

1. Replace the call to `console.log` with `logger.info`.

    !!! info "`console`"
        The built-in `console` is fine for debugging purposes, but it should not be used for production logging, since it lacks the important features a logging framework provides, such as:
        - logging levels
        - defining a root logging level for filtering purposes
        - customizable logging formats & patterns
        - defining one or multiple destinations for the logs

#### 1.4 Pass it Around 🏈

In file `server.js`:

1. Pass the `logger` as an argument to `GreetingService`'s `constructor` so that it uses our `logger` instance instead of `console` which is the default value, if no `logger` is passed.

    ```javascript
    const greetingService = new GreetingService(logger)
    ```

1. Also pass the `logger` as an argument to the `application` *factory function*.

    ```javascript
    const app = application(greetingService, logger)
    ```

1. Hit an endpoint to invoke the logging, e.g. `http://localhost:3000/howdy?name=Partner`

    Do you notice any difference in the appearance of the logs?

The default format for the logs in winston is `JSON`.
Log formats will be covered in [exercise 3](#3-log-formats).

### 2 Logging Levels 🗃

Every [logging level](https://github.com/winstonjs/winston#logging-levels) in winston is given a specific integer priority.

   The levels are numerically ascending from most important to least important.

   The following log levels are available:

  ```javascript
  {
      // most important
      error: 0,
      warn: 1,
      info: 2,
      http: 3,
      verbose: 4,
      debug: 5,
      silly: 6
      // least important
  }
  ```

??? info "Logging levels in winston"

    The logging levels are numerically **ascending** from most important to least important, meaning that 0 represents the highest priority and 6 the lowest.
    You can read more about logging levels in [the documentation](https://github.com/winstonjs/winston#logging-levels).
    When the winston documentation mentions something along the lines of "logging level *info* or lower" it would translate to *info*, *warn* or *error*.
    Be aware that this ordering is not consistent across logging frameworks!

#### 2.1 Logger Methods 🖍

Each log level has a corresponding method with the same name, e.g. the `log.info()` *method* for the `info` *level*.

Let's change the logging level of the `greeting created for ${name}` message in order to not clutter up the console too much.

1. Replace the call to `this.#log.info` with `this.#log.debug` in file `greeting-service.js`.

    ```javascript
    this.#log.debug(`greeting created for ${name}`)
    ```

1. Change the log message level for incoming requests in file `application.js` from `info` to `http`.

    ```javascript
    app.use((req, res, next) => {
      const { method, url } = req
      log.http(`${method} ${url}`)
      next()
    })
    ```

1. Change the log message level for the *deprecated* endpoint `/howdy` in file `application.js` from `error` to `warn`.

    ```javascript
    log.warn('Deprecated endpoint used!')
    ```

Can you still see these messages being logged afterwards?

#### 2.2 Root Logging Level 🌱

Winston configures the root logging level to `info` by default.
It can be specified during instantiation.
In the following example it is set to `warn`:

```javascript
const logger = createLogger({
  level: 'warn', // default: info
  transports: [
    new Console()
  ]
})
```

1. Increase and lower the logging level of your logger.

    Try e.g. `error`, `warn`, `info`, `http`, `debug`, etc. and hit the endpoint again.

    Does the overall log output change?

### 3 Log Formats ✍️

Next to logging level and transports, winston also allows us to configure the format of the log messages.
The default is `json`, but winston provides many more.

#### 3.1 Format the Logging Output 💬

1. Replace the default formatter with `simple()`, to make the output more human-readable.
    How does the log output change?

??? example "Need help?"

    ```javascript
    const { createLogger, format, transports } = winston
    const { simple } = format
    const { Console } = transports

    const logger = createLogger({
      level: 'debug',
      format: simple(),
      transports: [
        new Console()
      ]
    })
    ```

??? info "Dynamic configuration of the logger"
    In a real project it is unlikely that you will hard code your logger configuration like this.
    Consider using different configurations for your logger depending on the environment your application is running on.

    E.g. debug-logs can be helpful during development but should not clutter up the production logs.
    [Environment variables](https://medium.com/the-node-js-collection/making-your-node-js-work-everywhere-with-environment-variables-2da8cdf6e786) can be used to specify the environment.

#### 3.2 Enhance your Logs 📑

The logs can be enhanced with additional information, such as timestamps.
Winston provides a `timestamp` format which can be used to achieve this.
However, simply replacing `simple()` with `timestamp()` would break the logging, and only output `undefined`.

??? question "Why?"
    Winston has a set of ***finalizing formats*** such as:

    - `json`
    - `logstash`
    - `printf`
    - `prettyPrint`
    - `simple`

    These populate a special property in the log object, that goes through all the formats.
    The value of that property is what later appears in the console.
    If no "finalizing format" is configured, that property remains `undefined`.

To combine different formats the `combine()` method can be used.

1. Adjust the format for the logger to the following:

    ```javascript
    const { format } = winston
    const { combine, timestamp, simple } = format

    format: combine(
      timestamp(),
      simple()
    )
    ```

    This will make your log outputs look like this:

    ```shell
    info: Server started on port 3000 {"timestamp":"2020-07-10T13:13:39.890Z"}
    ```

    The `simple` format appends all additional information to the message as a JSON-object.

1. Replace the `simple` format with a `printf` format to define a custom template:

```javascript
const { format } = winston
const { combine, timestamp, printf } = format

format: combine(
  timestamp(),
  printf(({ level, message, timestamp, ...additionalInfo }) => {
    return `[${(new Date(timestamp)).toLocaleTimeString()}] [${level}] ${message} ${Object.keys(additionalInfo).length ? JSON.stringify(additionalInfo) : ''}`
  })
)
```

#### 3.3 Parameterized Log Messages 🗞

`format.splat()` allows us to use printf-like format messages.
These are preferable over string concatenation and template strings, due to performance reasons.

1. Use `format.splat()` as the first format in the `combine`.

1. Replace the template strings in the existing logs with printf-like format messages, e.g. using

    - `%s`: String

    - `%d`: Number

    - `%j`: JSON

    - etc. (for a full list of available placeholders please have a look at [util.format](https://nodejs.org/dist/latest/docs/api/util.html#util_util_format_format_args) which is internally used by `format.splat()`)

??? example "Need help?"

    ```javascript
    const { createLogger, format, transports } = winston
    const { combine, splat, timestamp, json } = format
    const { Console } = transports

    const logger = createLogger({
      level: 'debug',
      format: combine(
        splat(),
        timestamp(),
        json()
      ),
      transports: [
        new Console()
      ]
    })

    logger.info('The Ultimate Answer to %s is... %d!', 'Life, The Universe and Everything', 42)
    logger.info('Server started on http://%s:%d', 'localhost', 3000)
    ```

### 4 Logging Meta Information 🔖

Node.js only runs within a single thread.
While logging frameworks in other languages e.g. Java can use thread objects to store context information, Node.js developers have to rely on other methods.

#### 4.1 Child Loggers 👶

The `child()` method of a logger returns a new child `logger` instance, which inherits all configuration from its parent but can also be configured independently.

In file `server.js`:

1. Create a child instance of the `logger` and assign it to new variable `log`.

    ```javascrtipt
    const log = logger.child()
    ```

1. Replace the calls to `logger.error` with `log.error` and `logger.info` with `log.info` to use the child logger (instead of the parent logger).

#### 4.2 Add the Module Name 👩‍🏫

The `child()` method also allows us to pass an object containing arbitrary metadata, e.g.

```javascript
logger.child({ some: 'metadata' })
```

1. Pass an object with a single property `module` with value `server` as metadata to identify the `server` module in the logs.

    ```javascript
    const log = logger.child({ module: 'server' })
    ```

1. Start the server again and see in which logs which `module` property is shown.

#### 4.3 Indefinite Logs 🤷🏼‍♂️

As of now we can only clearly identify the origin of the log messages for the `server` module.

But it would be nice to be able to identify the origin of the log messages for all modules, right?

In file `greeting-service.js`

1. Create a child instance of the `logger` in the `GreetingService` class' constructor and assign it to the private `#log` instance field. Pass the `module` property as metadata to identify the `greeting-service` module in the logs.

??? example "Need help?"

    ```javascript
    class GreetingService {
      #log = null

      constructor(logger = console) {
        this.#log = logger.child({ module: 'greeting-service' })
      }

      // ...
    }
    ```

1. Also create a child instance of the `logger` in file `application.js` and assign it to the `log` variable.

??? example "Need help?"

    ```javascript
    export default (greetingService, logger = console) => {
      const log = logger.child({ module: 'application' })

      // ...
    }
    ```

1. Call the endpoints of the application to see in which logs which `module` property is shown.

1. Verify that every log is showing the `module` property of the respective module.

## 🏁 Summary

Good job!

In the prior exercises you introduced a logging library into an application and enhanced the existing logs with log levels.

Also, you applied different formats and added meta-information.

## 🦄 Stretch Goals

As of now we have only logged to the terminal using the `Console` transport.

But winston supports logging to other transports, e.g. to a file or to a database.

1. Add an additional [File transport](https://github.com/winstonjs/winston/blob/master/docs/transports.md#file-transport) to your logger instance.

??? example "Need help?"

    ```javascript
    import winston from 'winston'

    const { createLogger, format: { combine, splat, timestamp, json }, transports: { Console, File } } = winston

    const logger = createLogger({
      level: 'debug',
      format: combine(
        splat(),
        timestamp(),
        json()
      ),
      transports: [
        new Console(),
        new File({
          filename: 'logs/exercise-code-nodejs.log'
        })
      ]
    })

    export default logger
    ```

1. Have a look at the `File` transport [options](https://github.com/winstonjs/winston/blob/master/docs/transports.md#file-transport), especially the `tailable` option.

1. How could this feature become useful when running in production?

1. How could you only enable the `File` transport in production?

??? example "Need help?"

    [Node.js, the difference between development and production](https://nodejs.dev/learn/nodejs-the-difference-between-development-and-production)

    ```javascript
    transports: process.env.NODE_ENV === 'production' ? [ /* transports for production*/] : [/* transports for development*/]
    ```
1. Try logging to a database, have a look at the available [transports](https://github.com/winstonjs/winston/blob/master/docs/transports.md).

## 📚 Recommended Reading

- [A Guide to Node.js Logging](https://www.twilio.com/blog/guide-node-js-logging)
- [Environment variables in Node.js](https://medium.com/the-node-js-collection/making-your-node-js-work-everywhere-with-environment-variables-2da8cdf6e786)
- [Logging: Best Practices for Node.JS Applications](https://blog.bitsrc.io/logging-best-practices-for-node-js-applications-8a0a5969b94c)

## 🔗 Related Topics

- [pino](https://github.com/pinojs/pino#readme)
- [roarr](https://github.com/gajus/roarr#readme)
- [winston transports](https://github.com/winstonjs/winston#transports)
