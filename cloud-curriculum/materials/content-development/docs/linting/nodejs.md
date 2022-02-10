# Linting in Node.js

_Disclaimer: We are [counting page hits](https://github.wdf.sap.corp/cloud-native-dev/usage-tracker) using a cookie to distinguish returning & new visitors._
<img src="https://cloud-native-dev-usage-tracker.cfapps.sap.hana.ondemand.com/pagehit/cc-materials/linting-nodejs/1x1.png" alt="" height="1" width="1">

## 👷‍♂️👷‍♀️ Audience

Developers with basic knowledge of JavaScript who want to learn more about [Linting](https://en.wikipedia.org/wiki/Lint_%28software%29){target=_blank} in Node.js.

## 🧐 Linting

*Linting* is the process of static code analysis, checking the source code for programming errors, bugs, stylistic errors and suspicious constructs.

A *Lint* or *Linter* is a program that supports linting, they are available for most programming languages.

The term *Lint* originally comes from a Unix utility, which was used to check the syntax of a program written in the **`C`** programming language.
There the term was derived from lint, the name for the tiny bits of fiber and fluff shed by clothing, as the command should act like the lint trap in a clothes dryer, detecting small errors to great effect.

## 🎯 Learning Objectives

In this exercise you will learn how to

- setup a linting environment using [ESLint](https://eslint.org){target=_blank}

- configure [ESLint](https://eslint.org){target=_blank}

- enforce a consistent code style

- automatically fix common coding errors

<!-- Prerequisites-->
{% include 'snippets/prerequisites/nodejs.md' %}

## 🛫 Getting Started

{% with branch_name="linting", folder_name="linting-nodejs" %}
{% include 'snippets/clone-import/nodejs.md' %}
{% endwith %}

--8<--- "snippets/npm-install-dependencies.md"

## 📗 Exercises

### 1 Setup ESLint 🧐

[ESLint](https://eslint.org){target=_blank} is a tool for identifying and reporting on patterns found in ECMAScript/JavaScript code, with the goal of making code more consistent and avoiding bugs.

1. If your are using [Visual Studio Code](https://code.visualstudio.com){target=_blank} please make sure to install the [ESLint Extension](https://marketplace.visualstudio.com/items?itemName=dbaeumer.vscode-eslint){target=_blank} or please check how to enable [ESLint](https://eslint.org){target=_blank} for your IDE, if not already built-in.

1. Install [ESLint](https://eslint.org){target=_blank} as a development dependency by running the following command in your project root:

    ```shell
    npm install --save-dev eslint
    ```

### 2 Configure ESLint ⚙️

Now that we have ESLint installed, we need to configure it.

1.  Please run the following command in your project root:

    ```shell
    npx eslint --init
    ```

    Please choose the following options:

    ```shell
    ? How would you like to use ESLint?
    ❯ To check syntax, find problems, and enforce code style
    ```

    ```shell
    What type of modules does your project use?
    ❯ JavaScript modules (import/export)
    ```

    ```shell
    Which framework does your project use?
    ❯ None of these
    ```

    ```shell
    Does your project use TypeScript?
    ❯ No
    ```

    ```shell
    Where does your code run?
    ✔ Node
    ```

    ```shell
    How would you like to define a style for your project?
    ❯ Use a popular style guide
    ```

    ```shell
    Which style guide do you want to follow?
    ❯ Standard: https://github.com/standard/standard
    ```

    ```shell
    What format do you want your config file to be in?
    ❯ JSON
    ```

    ```shell
    Would you like to install them now with npm?
    ❯ Yes
    ```

    If everything went well a new [ESLint configuration file](https://eslint.org/docs/user-guide/configuring/){target=_blank} `.eslintrc.json` should have been created.

    Also some new development dependencies should have been installed and added to your `package.json` file.

1. Open the newly created `.eslintrc.json` file.

    Change `"parserOptions.ecmaVersion"` to `"latest"` as we want to support the latest EcmaScript features and modern syntax for this exercise.

    !!! tip "EcmaScript Version"

        Even if we all use the term *JavaScript* the official name of the language is **ECMAScript**!

        Each year there is a new version of the ECMAScript specification with new features and improvements.

        ECMAScript versions have been abbreviated to `ES1`, `ES2`, `ES3`, `ES5`, and `ES6`.

        Since 2016 new versions are named by year (`ECMAScript 2016` / `2017` / `2018` / etc.).

        **By setting `"parserOptions.ecmaVersion"`, ESLint will automatically show an error if you use an ECMAScript feature that is unknown or not supported by the specified version.**

        For more details please have a look at [Specifying Parser Options](https://eslint.org/docs/user-guide/configuring/language-options#specifying-parser-options){target=_blank}.

1. Again in `.eslintrc.json` add a new entry under `"env"`:

    ```json
    "mocha": true
    ```

    This will add all of the [Mocha](https://mochajs.org){target=_blank} testing global variables, such as `describe` and `it`.

    Otherwise ESLint would complain about these global variables not being defined in our `test/*.test.js` files.

1. Open the `package.json` file and add another entry under `"scripts"`:

    ```json
    "lint": "eslint ."
    ```

    Now we can run ESLint by running `npm run lint` in our project root.

    If you do so now, you will get a list of all linting problems (`error` or `warning`) per file, line and column.

    If you open the files in your editor those errors will also be highlighted (*assuming you have installed the [ESLint Extension](https://marketplace.visualstudio.com/items?itemName=dbaeumer.vscode-eslint){target=_blank} or enabled it for your IDE*).

### 3 Fix ESLint Errors ✅

If we check the output of `npm run lint` you will see that there are a lot of errors, e.g. related to an `Extra semicolon` or `Missing space before function parentheses`.

This is because of the [JavaScript Standard](https://standardjs.com/rules.html){target=_blank} code style that we are enforcing, by our initial [ESLint](https://eslint.org){target=_blank} setup and configuration in `.eslintrc.json`.

!!! tip "Extra semicolon"

    JavaScript does not actually require semicolons at the end of a statement. Meaning they are optional!

    That's why the [JavaScript Standard](https://standardjs.com/rules.html){target=_blank} code style marks them as an error for cleaner code.

!!! tip "Other Style Guides"

    We could have used other style guides such as

    - [Airbnb JavaScript Style Guide](https://github.com/airbnb/javascript){target=_blank}

    - [Google JavaScript Style Guide](https://google.github.io/styleguide/jsguide.html){target=_blank}

    - [Semi-Standard JavaScript Style](https://github.com/standard/semistandard){target=_blank}

    - etc.

    **They all enforce different rules for brackets, statements, assignments, etc.**

    Just choose a style guide that suits you and your team.

    You can also define your own [rules](https://eslint.org/docs/rules/){target=_blank}.

1. As most of these errors are only related to code style. We can can automatically fix them by running:

      ```shell
      npx eslint . --fix
      ```

      All the code style issues should now be fixed, e.g.

      - `Extra semicolons` should have been removed

      - `Missing space before function parentheses` should have been added

      - `Too many blank lines at the end of file` should have been removed

  Only actual syntax errors should be left now.

1. Check the linting error in file `lib/server.js`:

    !!! error

        'hello' is not defined

    Looks like we are using a variable `hello` before we have declared it.

    Luckily ESLint detected this error.

    If you try to run `npm start` now you will get a runtime error

    ```shell
    ReferenceError: hello is not defined
    ```

    and the server does not start!

    Let's quickly fix this error by defining the `hello` variable before using it:

    ```js
    const hello = 'Hello there!'
    ```

1. Check for the linting error in file `test/application.test.js`

    !!! error

        'HELLO' is assigned a value but never used

    Looks like we have declared a variable `HELLO` but are not using it.

    Even though our tests would still run (`npm test`) this is dead and unused code.

    So let's also fix this by replacing all other occurrences of `'Hello there!'` with `HELLO`.

1. Run `npm run lint` again.

    Now we should have no more linting errors. 🎉

## 🏁 Summary

Nice job! 🥳 You have successfully

- setup a linting environment using [ESLint](https://eslint.org){target=_blank}

- configured [ESLint](https://eslint.org){target=_blank}

- enforced a consistent code style

- automatically fixed common coding errors

## 🏕 Survival Guide

It's always a good idea to

- setup a linting environment for your project, to detect static code errors and warnings as early as possible

- configure and enforce a consistent code style for all developers

- run a lint task before committing and pushing your code

- run a lint task, e.g. before running your tests, in your CI/CD pipeline

## 📚 Recommended Readings, Watchings and Courses

- [Getting Started with ESLint](https://eslint.org/docs/user-guide/getting-started){target=_blank}

- [Write Perfect Code With Standard And ESLint 📼](https://www.youtube.com/watch?v=kuHfMw8j4xk){target=_blank}

- [Better Code Quality with ESLint](https://www.pluralsight.com/courses/eslint-better-code-quality){target=_blank}
