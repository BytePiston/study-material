# Unit Testing with Mocha

_Disclaimer: We are [counting page hits](https://github.wdf.sap.corp/cloud-native-dev/usage-tracker) using a cookie to distinguish returning & new visitors._
<img src="https://cloud-native-dev-usage-tracker.cfapps.sap.hana.ondemand.com/pagehit/cc-materials/unit-testing-nodejs/1x1.png" alt="" height="1" width="1">

## 👷‍♂️👷‍♀️ Audience

Developers with basic knowledge of Node.js, but no experience in unit testing with Mocha

## 🎯 Learning Objectives

In this exercise you will learn

- how to use basic Mocha annotations
- how to write Mocha tests for an existing codebase
- how to run Mocha tests in your IDE and evaluate the test coverage

<!-- Prerequisites-->
{% with
  tools=[
    ('Optionally: [*coverage-gutters*](https://marketplace.visualstudio.com/items?itemName=ryanluker.vscode-coverage-gutters)')
  ],
  required=[
    ('Basic understanding of the [**ancient roman numerals system**](https://en.wikipedia.org/wiki/Roman_numerals)')
  ],
  beneficial=[
    ('[JavaScript Classes](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Classes)'),
    ('[Private class features](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Classes/Private_class_fields)')
  ]
%}
{% include 'snippets/prerequisites/nodejs.md' %}
{% endwith %}

## 🛫 Getting Started

{% with branch_name="unit-testing", folder_name="unit-testing-nodejs" %}
{% include 'snippets/clone-import/nodejs.md' %}
{% endwith %}

--8<--- "snippets/npm-install-dependencies.md"

Run the tests:

```sh
npm test
```

If the tests were successful you should see the following log in the console:

```logtalk
0 passing (1ms)
```

## 🔍 Code Introduction

- In `lib/number-converter.js` you will find the class `NumberConverter` with the (only public) method `romanToArabic`, which takes a roman numeral as a parameter and, after conversion, returns an arabic integer.

- There is also an empty test stub in the corresponding test path `test/number-converter.test.js` that:
    - groups tests that belong together in a `describe` block.
    - has a variable `converter` that is instantiated in the function `beforeEach` - this means Mocha will automatically instantiate our class under test (`NumberConverter`), before the execution of each test.
    - contains a first simple example test, starting with `it(...)` which verifies that the `converter` variable is defined.
    - has `assert` imported from the built-in `Node.js` library `assert/strict` and ready to use to make verifications.

    !!! info "Write tests like specifications"
        Mocha uses "Behavior-Driven Development" (BDD) style in their API. This means tests read like specification - the "describe" and "it" blocks should form a sentence, in our case "Number converter passes an example test".

## 📗 Exercises

It is your task to add unit tests for the class `NumberConverter`.

If you like, you can check the initial test coverage before we begin:

```sh
npm run test:coverage
```

You have several options to inspect the coverage:
- check the console output of your test run, it prints a coverage summary, mentioning lines which were not covered
- inspect the coverage html report which was generated in `/coverage/lcov-report/index.html' (open the index.html in browser)
- if you have a coverage plugin installed in your IDE, the coverage can also be displayed directly in the editor window

Now let's write some tests!
**Execute the tests after each change you make**

### 1 - Single Roman Numerals

First, let's write some simple test cases for the "Happy Path".

1. Start with single digit roman numerals, here are some examples:
    - `I` should return `1`
    - `V` should return `5`
    - `M` should return `1000`

    ??? example "Need help?"
        A typical assertion within a test method might look like this:
        ```javascript
        const value = converter.romanToArabic("X")
        assert.equal(value, 10)
        ```

    !!! info "Naming conventions for unit test methods"
        A famous styles for naming tests that is often recommended is called `Given-When-Then`. However it doesn't quite fit to a spec-like style that Mocha uses. We can tweak it into a `Describe-Should-When` pattern:
        - `Describe` refers to the overall thing we want to test or the general context, put this in the `describe` block, such as `Number converter`
        - `Should` describes the expected behavior, put this at the beginning of the text in the `it('...')` that describes your test, such as `returns 42`
        - `When` describes the specific situation of the test, it complets the `it('...')` part, such as `...when passing XLII`
        - Check the [Mocha docs](https://mochajs.org/#hooks) for more info

    !!! info "Granularity of test methods"
        - You can either put multiple related assertions into one test method, or add several tests methods with one assertion each.
        - Both approaches are fine, as long as you don't bundle unrelated requirements (e.g. single digit roman numerals and subtractive numerals) into the same test method.
        - Keeping unrelated requirements separated keeps the tests small, and focused on one aspect, thus making them easier to read, maintain and understand.

1. Add more test cases until you feel comfortable!

### 2 - Invalid Numerals

As a next step, we want to test the most important "Error Paths".

- Add test cases that assert that `-1` is returned in case invalid parameters such as:
    - numerals that are not existing
    - empty strings
    - null values
    - combinations of numerals that are not allowed

    are provided

### 3 - Additive and Subtractive Numerals

Now let's make it more complex: In the roman numeration, literals can be additive and subtractive.

1. Add some test cases for valid additive roman numerals, e.g.:
    - `II`
    - `VI`
    - `CXI`

1. Also add some test cases for valid subtractive roman numerals, like
    - `IV`
    - `XL`
    - `IC`

1. Add more test cases until you feel comfortable with your test suite for additive and subtractive numerals.

### 4 - Complex Numerals

1. If not done already, extend your test suite with test cases for more complex roman numerals, such as:
    - `XIV`
    - `CMXL`
    - `MMXXI`
1. Once you've added a few cases, execute the tests with test coverage tracking again.
1. Have a look at the coverage report for `NumberConverter` and check if there are any chunks of code you have missed so far.
1. Think about relevant test cases to cover the missing code and add them. You may also add some more complex equivalent test cases to the previous steps.

!!! info "The Value of Test Coverage..."
    - A high test coverage doesn't automatically mean your test suite is sufficient, it is a great tool to help you to find the spots you've missed though.
    - Remember, high test coverage is a **necessary**, but **not a sufficient** identifier of a healthy codebase.
    - You could easily write tests to achieve high coverage without actually making any assertions about the outcome, thus testing nothing except that no exceptions are thrown. This is a behavior which can often be seen if Test Coverage is used as a KPI by management without granting the time/resources to actually do things right.
    - Use test coverage as what it is: a high level indicator, not a KPI.


## 🏁 Summary

Good job! In this unit you have:

* [x] learned how to write tests in Mocha.
* [x] used your terminal to execute the tests.
* [x] improved the codebase by adding a test suite with sufficient coverage.

## 📚 Recommended Reading

- [Article on unit testing in the ASE Hub](https://pages.github.tools.sap/EngineeringCulture/ase/AllLanguages/unitTesting)
- [Mocha Documentation](https://mochajs.org/#hooks)

## 🤔 Solution

If you get stuck completely you can take a peek at the [solution branch](https://github.tools.sap/cloud-curriculum/exercise-code-nodejs/tree/unit-testing-solution)
