# Service to Service Communication

_Disclaimer: We are [counting page hits](https://github.wdf.sap.corp/cloud-native-dev/usage-tracker) using a cookie to distinguish returning & new visitors._
<img src="https://cloud-native-dev-usage-tracker.cfapps.sap.hana.ondemand.com/pagehit/cc-materials/service2service-java/1x1.png" alt="" height="1" width="1">

## 👷‍♂️👷‍♀️ Audience

Developers with basic knowledge of Java and HTTP-Rest

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
      'Basic understanding of [HTTP-RESTful APIs](https://restfulapi.net/)'
  ],
  beneficial=[
      '[Spring](https://spring.io/)'
  ]
%}
{% include 'snippets/prerequisites/java.md' %}
{% endwith %}

## 🛫 Getting Started

{% with branch_name="service2service", folder_name="service2service-java" %}
{% include 'snippets/clone-import/java.md' %}
{% endwith %}

Run the application:

{% with main_class="SpringBootUsersApplication" %}
=== "Command Line"
{% filter indent(4) %}
{% include 'snippets/run-spring-boot/cli-maven-run.md' %}
{% endfilter %}
=== "Spring Tool Suite"
{% filter indent(4) %}
{% include 'snippets/run-spring-boot/sts-run.md' %}
{% endfilter %}
=== "IntelliJ"
{% filter indent(4) %}
{% include 'snippets/run-spring-boot/intellij-run.md' %}
{% endfilter %}
{% endwith %}

When the Application has started you should be able to see a line similar to the following in the console:
```
2021-06-02 11:51:04.830  INFO 25056 --- [  restartedMain] com.sap.cc.SpringBootUsersApplication    : Started SpringBootUsersApplication in 3.452 seconds (JVM running for 4.34)
```

## 🔍 Code Introduction

We have set up a simple application that displays users of the application.

In the `UserController` class there is a GET endpoint provided under `/api/v1/users/pretty/{userId}`.
It should display user information in an embellished way, but so far the endpoint only prints the user information as is which is not very pretty.

There is also a class called `PrettyUserPageCreator` that is responsible for creating the pretty user page. 

## 📗 Exercise
In this exercise, you will implement the communication to the AsciiArt service located at https://service2service-endpoint.cfapps.eu10.hana.ondemand.com/.

The AsciiArt service provides the functionality to convert strings into ascii art, which we are going to use for the `/api/v1/users/pretty/{userId}` endpoint.

### 1 - Explore the AsciiArt Service

The AsciiArt service team has provided the endpoint specification in their `README.md`.

- Go to the [AsciiArt service repo](https://github.tools.sap/cloud-curriculum/s2s-exercise-endpoint) and explore the endpoint specs in the readme-file.

### 2 - Create the Entity Representing the Request

Reading the endpoint specification, we saw that the POST endpoint accepts a body with the fields `toConvert` and `fontId`. We will specify a class that represents our request body.

1. Create a class `AsciiArtRequest` in the package `com.sap.cc.ascii`. 

1. Give it two `private` `String`-fields called `toConvert` and `fontId`.

1. Create a constructor to initialize both fields of the class.

1. Add getter methods for both fields, to enable object serialization when using the RestTemplate client.

### 3 - Create the AsciiArt Service Client

We will now create a `AsciiArtServiceClient` which implements the HTTP communication to use the AsciiArt service.

Let's write a test first.

1. Create a test class called `AsciiArtServiceClientTest` in the package `com.sap.cc.ascii` (which you may need to create first) of the `src/test/java` directory and insert the code from the following snippet:

    ```JAVA
    package com.sap.cc.ascii;

    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.mockito.InjectMocks;
    import org.mockito.Mock;
    import org.mockito.Mockito;
    import org.springframework.boot.test.context.SpringBootTest;
    import org.springframework.http.HttpStatus;
    import org.springframework.web.client.HttpClientErrorException;
    import org.springframework.web.client.RestTemplate;

    import static org.assertj.core.api.Assertions.assertThat;

    @SpringBootTest
    public class AsciiArtServiceClientTest {

        public static final String ASCII_SERVICE_URL = "http://example.com/api/v1/asciiArt/";

        public static final AsciiArtRequest WITH_VALID_ARGS = new AsciiArtRequest("HelloWorld", "3");
        public static final AsciiArtRequest WITH_UNKNOWN_FONT_ID = new AsciiArtRequest("handleThis", "9");

        public static final HttpClientErrorException BAD_REQUEST_EXCEPTION = HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "Font not found for fontId 9. Try a fontId within 0..8", null, null, null);

        @Mock
        private RestTemplate restTemplate;

        @Mock
        private BaseUrlProvider baseUrlProvider;

        @InjectMocks
        private AsciiArtServiceClient asciiArtServiceClient;

    }
    ```

    ??? info "Code walkthrough"
        - the `#!java @SpringBootTest` annotation registers this test class with Spring Boot which will, among other things, take care of dependency injection for this class
        - `WITH_VALID_ARGS` and `WITH_UNKNOWN_FONT_ID` are some static test fixtures to use in the tests
        - `#!java RestTemplate` enables us to execute synchronous http requests. [https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html](See here also)
            - we're going to `#!Java @Mock` the RestTemplate in order to simulate the AsciiService's responses
        - `#!java AsciiArtServiceClient` service client under test, mocks get injected into this with `#!java @InjectMocks`

1. Create the `AsciiArtServiceClient` class in the package `com.sap.cc.ascii` in the `src/main/java` directory using the following snippet:

    ```JAVA
    package com.sap.cc.ascii;

    import org.springframework.stereotype.Service;
    import org.springframework.web.client.RestTemplate;

    @Service
    public class AsciiArtServiceClient {

        private static final String ASCII_SERVICE_PATH = "/api/v1/asciiArt/";

        private final RestTemplate restTemplate;
        private final BaseUrlProvider baseUrlProvider;

        public AsciiArtServiceClient(RestTemplate restTemplate, BaseUrlProvider baseUrlProvider) {
            this.restTemplate = restTemplate;
            this.baseUrlProvider = baseUrlProvider;
        }

        public String getAsciiString(AsciiArtRequest asciiArtRequest) {
            //TODO: implement me 
            return null;
        }
    }
    ```

1. Run your tests. 
    They will probably fail due to:
    ```
    No qualifying bean of type 'org.springframework.web.client.RestTemplate' available
    ```

    We haven't defined the RestTemplate as a bean in order to make it injectable.

1. Go to your main class `SpringBootUsersApplication` and create a new method using the following snippet:

    ```Java
    @Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
    ```

    Now a Bean of the `RestTemplate` should be available in the application context and your tests should be passing again. 

### 4 - AsciiArtServiceClient "Happy Path"

1. Create a new test in `AsciiArtServiceClientTest` called `whenCallingGetAsciiString_thenClientMakesCorrectCallToService`.

1. In the test class we created a dummy RestTemplate using the `#!java @Mock` annotation.

    Let's define the behavior of the RestTemplate-mock with Mockito's when/then support.
    When a POST request is made (`#!java restTemplate.postForObject(...)`) with the parameters:

    - `url`: URL path to the service
    - `request object`: a request object that should be valid, e.g. static fixture `WITH_VALID_ARGS`
    - `response type`: the AsciiService returns text/plain, therefore `String.class`

    then the service should return `"'Hello World' as ascii art"`

    ??? example "Need help?"
        ```Java
        Mockito.when(awesomeCall(awesomeParam1, awesomeParam2)).thenReturn(awesomeResult);
        ```

1. Use Mockito's `when` and `then` methods to make the `BaseUrlProvider`-mock return `"http://example.com"` for the `getBaseUrl` method.

1. Now call the AsciiArtServiceClient's `getAsciiString()`-method (using the the same request object as above) and assertThat the retrieved result matches the string `"'Hello World' as ascii art"`

1. Run this test. 
    Obviously it is failing since we haven't implemented the AsciiArtServiceClient's behavior yet.

1. In the class `AsciiArtServiceClient` implement the method `getAsciiString()`.
    As required by the previously written test, the method should call the `postForObject` of `RestTemplate` with the respective parameters and return the result.
    - The URL is composed of the base URL (provided by the `BaseUrlProvider` 😲) and the `ASCII_SERVICE_PATH` constant.

1. Rerun your test. It should be passing now.

### 5 - Test and Implement Invalid Request Case

The AsciiArt service will return a `HTTP 400 BAD_REQUEST` for invalid request parameters such as unknown `fontId`s.
Our service should be able to handle this scenario since the `fontId` is chosen freely by the user during sign-up. 

1. Inside `AsciiArtServiceClientTest` create a test-method called `whenRequestingWithInvalidRequest_thenInvalidRequestExceptionIsThrown`.

1. Use `#!java Mockito.when().thenThrow()` to mock the Ascii-Art-Service's behavior.
    When getting called with an invalid request (use static fixture `WITH_UNKNOWN_FONT_ID`), then a `HTTP 400 Error Response` should be thrown (use static fixture `BAD_REQUEST_EXCEPTION`).

1. Make the `BaseUrlProvider`-mock return `"http://example.com"`, like in the previous test.

1. Finally assert that the `#!java asciiArtServiceClient.getAsciiString(WITH_UNKNOWN_FONT_ID)`-call throws an exception which is an instance of `#!java InvalidRequestException`.
    
    ??? example "Need help?"
        ```Java
        assertThatThrownBy(() -> awesomeCall(awesomeParam))
                .isInstanceOf(AwesomeException.class);
        ```
    Run the test. It's failing of course.

1. In the `AsciiArtServiceClient` class, move the post-call inside a try-block and catch the exception of type `HttpClientErrorException.BadRequest`.
    Inside the catch-block throw an `InvalidRequestException`.

1. Run the test again. Now it should be passing ✅

### 6 - Use the AsciiArt Service Client
The service client seems to be ready for use.
We can now convert any string value to ascii art by using the AsciiArt-service.

#### 6.1 Adjust a Test
Let's use this feature to convert the user's name before displaying it in the pretty page (`/api/v1/users/pretty/{id}`).

We want the `PrettyUserPageCreator` to use the `AsciiArtServiceClient` so let's adjust its test first.

1. Go to the `PrettyUserPageCreatorTest` class and declare a private field for `AsciiArtServiceClient` and annotate the field with `@Mock`.

1. Change the `prettyUserPageCreator` fields annotation to `@InjectMocks` to inject the mocked `AsciiArtServiceClient`.

1. Inside the `shouldCreateAPrettyUserPage` test, mock the `getAsciiString` method using `org.mockito.Mockito` and `org.mockito.ArgumentMatchers`

    ```java
    Mockito.when(asciiArtServiceClient.getAsciiString(ArgumentMatchers.any())).thenReturn("prettifiedName");
    ```


1. Adjust the assertion to expect `"prettifiedName" + "\r\n" + user.getPhoneNumber()` as result of the `getPrettyPage` method call.

1. Run the test to make sure it's failing.

#### 6.2 Make the Test Pass

First you will have to inject the `AsciiArtServiceClient` in the `PrettyUserPageCreator`.

1. Go to `PrettyUserPageCreator` and create a private field of type `AsciiArtServiceClient`.

1. Also add it as a constructor parameter and make sure to initialize the private field inside the constructor body.

1. Inside the `getPrettyPage`-method create a new `AsciiArtRequest` for the user's name, with the corresponding `fontPreference` of the user.

1. Use the `AsciiArtServiceClient`'s `getAsciiString(AsciiArtRequest)`-method to retrieve the ascii art and insert it into the string that is being returned (replacing the username).

1. Run the test to make sure it's passing. But another test might fail!

### 7 - Fix the Integration Test

The integration test `returnsPrettyPage` is failing.
The logs hint towards the cause:
```
java.net.MalformedURLException: unknown protocol: replaceme
```

If you peek into the file `src/test/resources/application.properties` you will see that the baseUrl is set to `replaceme://example.com`, which is not a valid URL.
This was done to keep the test from calling the live Ascii-Art-Service.
Without this precaution we might be writing tests that depend on the Ascii-Art-Service being reachable.

#### 7.1 What URL can be used for the tests?
We could mock the `RestTemplate` again, as we did in the `AsciiArtServiceClient`-test, but since this test covers multiple components, using a test double at such a low level might soon become a great maintenance burden and make the test very brittle.
Additionally, when using a "real" `RestTemplate` the test can uncover errors which would not occur when using a mock.
However, if we decide not to mock the REST-client, we require a mock endpoint that the REST-client can access.

1. Add the `WireMock` dependency to the `pom.xml`:
    ```xml
    <dependency>
        <groupId>com.github.tomakehurst</groupId>
        <artifactId>wiremock-jre8</artifactId>
        <version>2.29.1</version>
        <scope>test</scope>
    </dependency>
    ```

1. Load/Sync the maven changes.

#### 7.2 Start a Mock Server
Tools such as [WireMock](http://wiremock.org/) let us mock external services.

1. Add a `WireMockServer` to the class `IntegrationTest`
    ```Java
    import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
    //...
    private WireMockServer wireMockServer = new WireMockServer(options().port(8181));
    ```

1. Start the mock-server by calling `wireMockServer.start()` in a `@BeforeEach`-annotated method.

1. Stop the mock-server by calling `wireMockServer.stop()` in an `@AfterEach`-annotated method.

1. During the test, the mock-server will accept requests at `http://localhost:8181`, since that's the port we have configured. 
    Assign this URL to the property `service.ascii.url.base` via the `properties` argument of the `@SpringBootTest` annotation.

    ??? example "Need help?"
        ```Java
        @SpringBootTest(properties = { "some.property=someValue" })
        public class SomeClass {
            // ...
        }
        ```

#### 7.3 Declare the Stubs
When running the test now, it fails because the mock-server returned a 404 response.
To get it to return something else we need to tell it what to return before sending requests to it.

1. Specify the expected behavior at the start of the test:
    ```Java
    import static com.github.tomakehurst.wiremock.client.WireMock.*;
    //...
    wireMockServer.stubFor(post("/api/v1/asciiArt/")
        .willReturn(aResponse().withBody("PrettyName")));
    ```

    ??? info "Code Walkthrough"
        This snippet instructs the mock-server to return a response with the body "PrettyName" when a POST request is received for the path `/api/v1/asciiArt/`.

1. Adjust the assertion to ensure that the returned pretty page contains `"PrettyName"` before the line breaking character(s).

1. Run the test again. Now it should be passing ✅

### 8 - Manual Test

The tests increase our confidence that the components work together. But have you actually tried running the application?

- Run your application and manually test the endpoint `/api/v1/users/pretty/{id}`.
    You can test with the default users (ids 1 and 2).

### 9 - Update the API Version
You have been informed that the API of the AsciiArt service has recently been updated.
The new API is accessible at `/api/v2/asciiArt`.

1. Adjust the `ASCII_SERVICE_PATH` in the `AsciiArtServiceClient` to point to the new API and then fix the tests which are broken by this change.

    ??? info "Why are the tests failing?"
        We are doing a lot of mocking in our tests; Mocks expect a specific method or endpoint invocation, typically even with exact parameters. If we change the actual calls or their parameters the mock framework will return null.

        It's also pretty annoying we have to chase down all places where we have hard-coded the API path, isn't it? Maybe it would have been a good idea to have one single source of truth, be it a constant in our code used by all classes or, even better, a property that we could change at deploy-time without touching any code at all.

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

- Implement fallback behavior to deal with the functional outage of the AsciiArt Service. 
    - Catch the `Internal Server Error` of the AsciiArt service and introduce your error handling.
    - The pretty page should at least display the users information without layout.

## 📚 Recommended Reading
- [Baeldung guide to RestTemplate](https://www.baeldung.com/rest-template)
- [WebClient - modern alternative to RestTemplate](https://www.baeldung.com/spring-5-webclient)
- [Design Patterns for Microservice-To-Microservice Communication](https://dzone.com/articles/design-patterns-for-microservice-communication)

## 🔗 Related Topics
- [Data Transfer Object (DTO)](https://martinfowler.com/eaaCatalog/dataTransferObject.html)
