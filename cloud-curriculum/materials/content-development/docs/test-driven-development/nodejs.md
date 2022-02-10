# 🧾 TDD in Node.js Basics

_Disclaimer: We are [counting page hits](https://github.wdf.sap.corp/cloud-native-dev/usage-tracker) using a cookie to distinguish returning & new visitors._
<img src="https://cloud-native-dev-usage-tracker.cfapps.sap.hana.ondemand.com/pagehit/cc-materials/tdd-nodejs/1x1.png" alt="" height="1" width="1">

## 👷‍♂️👷‍♀️ Audience

Developers with basic knowledge in JavaScript, Node.js and Mocha.

## 🎯 Learning Objectives

In this exercise you will learn

- how to develop software in a test-driven way

<!-- Prerequisites-->
{% with
  required=[
    ('[JavaScript Classes](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Classes)')
  ],
  beneficial=[
    ('[Mocha](https://mochajs.org)')
  ]
%}
{% include 'snippets/prerequisites/nodejs.md' %}
{% endwith %}

## 🛫 Getting Started

{% with branch_name="tdd", folder_name="tdd-nodejs" %}
{% include 'snippets/clone-import/nodejs.md' %}
{% endwith %}

--8<--- "snippets/npm-install-dependencies.md"

You can now run the tests using the following command:

```shell
npm test
```

## 🔍 Code Introduction

In `test/fizz-buzz.test.js` you will find a prepared test stub. There is also a corresponding productive class called `FizzBuzz` within `lib/fizz-buzz.js`, but no productive code has been implemented yet.

## 📗 Exercises

Your task is to develop the game "FizzBuzz" in a test-driven manner.

### 1 - The Game FizzBuzz

Usually, FizzBuzz is a game played by children to learn division. You may also have a look at the  [Wikipedia article on FizzBuzz](https://en.wikipedia.org/wiki/Fizz_buzz).

In our case, the method which needs to be developed takes an integer number as a parameter

- In case it is divisible by 3, the String "Fizz" should be returned.
- In case the number is divisible by 5, the String "Buzz" should be returned.
- For numbers that are divisible by 3 and 5, the String "FizzBuzz" should be returned.
- All other numbers should be printed as Strings ("1", "2", etc.).

### 2 - The Basic TDD Cycle

Follow these steps to develop the functionality in a test driven way:

1. Add a test that asserts that the method `print` in class `FizzBuzz` returns `"1"` if the number `1` is provided as a parameter.
1. Execute the test to see it is failing.
1. Write just enough productive code to make the test pass.
1. Add a second test that asserts that `print(2)` returns `"2"`. Run the tests to see the new test is failing.
1. Write just enough productive code to make both tests pass.
1. Think about possible refactorings. Can the code maybe be written in a simpler or more readable way?

    ??? example "Need help?"
        You may use the method `toString()` to make your code simpler:
        ```javascript
        print(sInput) {
            return sInput.toString()
        }
        ```

1. If you applied a refactoring, execute the tests again to see that they stay green.
1. Add a third test that asserts that `print(3)` returns `"Fizz"` and add the necessary productive code.

### 3 - Develop the Game

You now know how to apply the TDD cycle to given requirements.

Proceed with test driven development to fulfill the following requirements:

1. `print(5)` should return `"Buzz"`.
1. `print(6)` and all other numbers divisible by 3 should return `"Fizz"`.
    ??? tip "Use the Modulo Operator to Check Whether a Number is Divisible by another"
        The modulo operator (`%`) returns the remainder of a division.

        ```Javascript
        assert.equal(11 % 4, 3)
        ```

        If the remainder is 0 that means that the left-side argument is divisible by the right-side argument.

        ```Javascript
        assert.equal(4 % 2, 0)
        ```
1. `print(10)` and all other numbers divisible by 5 should return `"Buzz"`.
1. `print(15)` should return `"FizzBuzz"`.
1. All numbers divisible by 3 AND 5 should give `"FizzBuzz"`.
1. All numbers that are not divisible by 3 or 5 should be printed as Strings.

If some requirements are too large to be covered by one unit test you may break them down into smaller test cases.

Remember to apply refactorings from time to time, but make sure the tests are green before starting to refactor.

## 🏁 Summary

Good job!
In the prior exercises you learned how to apply the ASE practice of test driven development.
You saw how you produced a well tested codebase with some simple steps.
Lastly, you could apply the technique of refactoring because you had confidence in your test suite.

## 📚 Recommended Reading

- [Test-Driven Development](https://martinfowler.com/bliki/TestDrivenDevelopment.html)

## 🔗 Related Topics

- [What Is Refactoring](https://refactoring.guru/refactoring/what-is-refactoring)
