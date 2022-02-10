# Unit Testing with JUnit

_Disclaimer: We are [counting page hits](https://github.wdf.sap.corp/cloud-native-dev/usage-tracker) using a cookie to distinguish returning & new visitors._
<img src="https://cloud-native-dev-usage-tracker.cfapps.sap.hana.ondemand.com/pagehit/cc-materials/unit-testing-java/1x1.png" alt="" height="1" width="1">

## 👷‍♂️👷‍♀️ Audience

Developers with basic knowledge of Java, but no experience in unit testing with JUnit

## 🎯 Learning Objectives

In this exercise you will learn

- how to use basic JUnit annotations
- how to write JUnit tests for an existing codebase
- how to run JUnit tests in your IDE and via maven and evaluate the test coverage

<!-- Prerequisites-->
{% with
  tools=[
      '[EclEmma Java Code Coverage Plugin](https://www.eclemma.org/installation.html#marketplace) (when using Spring Tool Suite)'
  ],
  required=[
      'Basic understanding of the [ancient roman numerals system](https://en.wikipedia.org/wiki/Roman_numerals)'
  ]
%}
{% include 'snippets/prerequisites/java.md' %}
{% endwith %}

## 🛫 Getting Started

{% with branch_name="unit-testing", folder_name="unit-testing-java" %}
{% include 'snippets/clone-import/java.md' %}
{% endwith %}

Run the tests:

{% with main_class="RomanNumeralsTests" %}
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
{% endwith %}

If the tests were successful you should see the following logs in the console:

```logtalk
[INFO] Running com.sap.cc.unittesting.RomanNumeralsTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.228 s - in com.sap.cc.unittesting.RomanNumeralsTest
```

## 🔍 Code Introduction


- In `src/main/java/com/sap/cc/unittesting/RomanNumerals.java` you will find the class `RomanNumerals`, containing the method `public int toArabic(String romanNumeral)`, which takes a roman numeral as a parameter and, after conversion, returns an arabic integer.

- There is also the stub of a test class in the corresponding test path `src/test/java/com/sap/cc/unittesting/RomanNumeralsTest.java` that:
    - has a field of type `RomanNumerals` that is instantiated in a method annotated with `@BeforeEach` - this means JUnit will automatically instantiate our class under test (`RomanNumerals`), before the execution of each test method.
    - contains a first simple JUnit test method called `exampleTest` (annotated with `@Test`).
    - has `assertJ`'s `assertThat()` imported and ready to use.

## 📗 Exercises

It is your task to add unit tests for the class `RomanNumerals`.

If you like, you can check the initial test coverage before we begin:

=== "Spring Tool Suite"
{% filter indent(4) %}
{% include 'snippets/run-test-coverage/sts-run-test-coverage.md' %}
{% endfilter %}
=== "IntelliJ"
{% filter indent(4) %}
{% include 'snippets/run-test-coverage/intellij-run-test-coverage.md' %}
{% endfilter %}


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
        ```java
        assertThat(romanNumerals.toArabic("X")).isEqualTo(10);
        ```


    !!! info "Naming conventions for unit test methods"
        - There are some popular [naming conventions for test methods](https://dzone.com/articles/7-popular-unit-test-naming), like "Given-When-Then" or "Should_ExpectedBehavior_When_StateUnderTest".
        - You may pick any convention you feel comfortable with, but you should use it consistently in your code.


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
1. Have a look at the coverage report for `RomanNumerals` and check if there are any chunks of code you have missed so far.
1. Think about relevant test cases to cover the missing code and add them. You may also add some more complex equivalent test cases to the previous steps.

!!! info "The Value of Test Coverage..."
    - A high test coverage doesn't automatically mean your test suite is sufficient, it is a great tool to help you to find the spots you've missed though.
    - Remember, high test coverage is a **necessary**, but **not a sufficient** identifier of a healthy codebase.
    - You could easily write tests to achieve high coverage without actually making any assertions about the outcome, thus testing nothing except that no exceptions are thrown. This is a behavior which can often be seen if Test Coverage is used as a KPI by management without granting the time/resources to actually do things right.
    - Use test coverage as what it is: a high level indicator, not a KPI.


## 🏁 Summary

Good job! In this unit you have:

* [x] learned how write tests in JUnit.
* [x] leveraged your IDE and maven to execute the tests.
* [x] improved the codebase by adding a test suite with sufficient coverage.

## 📚 Recommended Reading

- [Article on unit testing in the ASE Hub](https://pages.github.tools.sap/EngineeringCulture/ase/AllLanguages/unitTesting)
- [JUnit5 Documentation](https://junit.org/junit5/docs/current/user-guide/)

## 🤔 Solution
If you get stuck completely you can take a peek at the [solution branch](https://github.tools.sap/cloud-curriculum/exercise-code-java/tree/unit-testing-solution)
