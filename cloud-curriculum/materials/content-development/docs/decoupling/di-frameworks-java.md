# Dependency Injection Frameworks

_Disclaimer: We are [counting page hits](https://github.wdf.sap.corp/cloud-native-dev/usage-tracker) using a cookie to distinguish returning & new visitors._
<img src="https://cloud-native-dev-usage-tracker.cfapps.sap.hana.ondemand.com/pagehit/cc-materials/di-framework-java/1x1.png" alt="" height="1" width="1">

## 🎯 Learning Objectives
In this exercise you will learn...

- how to use the Spring IoC container to set up integration tests
- how to declare beans with annotations
- how to replace beans with mocks in a test

<!-- Prerequisites-->
{% include 'snippets/prerequisites/java.md' %}

## 🛫 Getting Started

{% with branch_name="di-framework", folder_name="di-framework" %}
{% include 'snippets/clone-import/java.md' %}
{% endwith %}


## 🔍 Code Introduction

`GreetingApplication` initializes a Spring Boot application in its `main` method.
The heart of the application is the `GreetingService`, which generates a nice greeting message for a given name by using the `Greeting` entity supplied by the `RandomGreetingSupplier`.

## 📗 Exercises

In the following exercises you will use Spring annotations to specify a test setup in a declarative way.

### 1 - Application Context

The class `GreetingApplicationTests` is a default test class generated when a new Spring project is created with the [__Spring Initializr__](https://start.spring.io/).
The annotation `@SpringBootTest` declares that the application context should be started before the test is run.
And the test ensures that it does so without conflict, as the name suggests.

1. Run the test.
    It should pass.

1. Run all tests.
    You should see a test failure.

### 2 - Unsatisfied Dependency

1. Inspect the error:

    ```
    UnsatisfiedDependencyException: Error creating bean with name 'com.sap.cc.greetings.GreetingServiceTest':
        Unsatisfied dependency expressed through field 'greetingService';
        nested exception is [...].NoSuchBeanDefinitionException
    ```

1. Look at the `greetingService` field of the `GreetingServiceTest` class.

The `@Autowired` annotation asks Spring to inject a bean of type `GreetingService` into the field. But Spring is unable to find such a bean and therefore throws an exception.

### 3 - Bean Declaration

1. Add a `@Component`-annotation to the class `GreetingService`.

1. Run the tests again. They should pass now.

Notice how there is no `new` keyword used in the test class.
All the instantiation is defined through annotations.

??? info "What about the constructor parameter?"
    
    You may have noticed that `GreetingService`'s constructor expects an argument that implements the `Supplier` interface.
    How does Spring instantiate the `GreetingService`?
    It looks for a bean to satisfy the dependency expressed through constructor parameter.
    Apparently the class `RandomGreetingSupplier` implements the `Supplier` interface and is declared as a bean through the `@Component`-annotation, hence qualifying as an autowire candidate.

### 4 - Mocking Beans

Currently the assertion inside the `getGreetingForPerson` test is a little lax, because that test cannot know which greeting will be returned by the supplier.
Let's use a test double for the supplier so that we can control the returned greeting in the test and assert that the `GreetingService` does not modify the greeting message in any way.

1. Declare a new field of type `Supplier<Greeting>` in the class `GreetingServiceTest`.

1. Add the annotation `@MockBean` to the field.

1. Use Mockito's `when(...).thenReturn(...)` methods to return an instance of the `Greeting` class with the template `"Hello %s."`.

1. Assert that the returned greeting string is equal to `"Hello Angela."`.

1. Run the test. It should pass.

## 🦄 Stretch Goals

Sometimes you may want to use an alternative implementation of an interface in several test classes.
Write a new class which also implements `Supplier<Greeting>` and declare a bean for it.
Ensure that it is only used in tests through "Spring profiles".

## 🏁 Summary
Congratulations! 
You have successfully declared a bean and replaced a different bean with a mock inside a test.

## 📚 Recommended Reading
- [Spring Application Context](https://www.baeldung.com/spring-application-context)

## 🔗 Related Topic
- [Spring Profiles](https://www.baeldung.com/spring-profiles)
