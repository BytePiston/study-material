# Distributed Logging in Cloud Foundry

_Disclaimer: We are [counting page hits](https://github.wdf.sap.corp/cloud-native-dev/usage-tracker) using a cookie to distinguish returning & new visitors._
<img src="https://cloud-native-dev-usage-tracker.cfapps.sap.hana.ondemand.com/pagehit/cc-materials/distributed-logging-cf/1x1.png" alt="" height="1" width="1">

## 👷‍♂️👷‍♀️ Audience

{% if language == "Java" %}
Developers with basic knowledge of Java and Cloud Foundry.
{% elif language == "Node.js" %}
Developers with basic knowledge of Node.js and Cloud Foundry.
{% endif %}

## 🎯 Learning Objectives

In this exercise you will learn

- how to use a backing service to collect logs
- how to make APIs use a correlation id
- how to trace a request

<!-- Prerequisites-->
{% if language == "Java" %}
{% with
  required=[
    ('[Logging Basics](../../logging/java)'),
    ('[Cloud Foundry Basics](../../cloud-platforms/cloud-foundry-java)')
  ]
%}
{% include 'snippets/prerequisites/java.md' %}
{% endwith %}
{% elif language == "Node.js" %}
{% with
  required=[
    ('[Logging Basics](../../logging/nodesjs)'),
    ('[Cloud Foundry Basics](../../cloud-platforms/cloud-foundry-nodejs)')
  ]
%}
{% include 'snippets/prerequisites/nodejs.md' %}
{% endwith %}
{% endif %}

### Other 🚀

- A [Cloud Foundry trial account](https://cockpit.hanatrial.ondemand.com/cockpit/#/home/trial) - make sure to use the Region __US East(VA) cf-us10__ as not all backing services are available in every region/landscape.

- [CF client V8](https://github.com/cloudfoundry/cli/wiki/V8-CLI-Installation-Guide)

## 🛫 Getting Started

{% if language == "Java" %}

{% with branch_name="distributed-logging", folder_name="distributed-logging-cf-java" %}
{% include 'snippets/clone-import/java.md' %}
{% endwith %}

{% elif language == "Node.js" %}

{% with branch_name="distributed-logging", folder_name="distributed-logging-cf-nodejs" %}
{% include 'snippets/clone-import/nodejs.md' %}
{% endwith %}

Finally, install the required dependencies by running the following command within the `greetings` **and** `users` directories:

```sh
npm install
```

{% endif %}

## 🔍 Code Introduction

We provide two microservices: `greetings` and `users`.
Both expose a simple REST interface.
The `greetings` service calls the `users` service to retrieve the necessary information.

## 📗 Exercises

The microservices are almost ready to be deployed, so that you can focus on the distributed logging.

### 1 - Deploy the Services

{% if language == "Java" %}
1. In the root directory of the cloned project build the apps by running the following command:
    ```shell
    mvn package
    ```
{% endif %}
1. Make sure your Cloud Foundry CLI client is logged in and targeting the correct space.
1. In the root directory open the file `vars.yaml` and replace the `<YOUR C/D/I-NUMBER>` placeholder with your actual C/D/I-number in with the letter being lowercase.

    If you are not using a Cloud Foundry trial account you will also need to adapt the value for `domain` in the `vars.yaml`.

1. Run the following command:
    ```shell
    cf push --vars-file vars.yaml
    ```

    ??? info "Command Walkthrough"
        The values given in the file `vars.yaml` will be used to replace the placeholders (denoted by doubled parentheses) in the file `manifest.yaml`. For more details, see the [Cloud Foundry Documentation](https://docs.cloudfoundry.org/devguide/deploy-apps/manifest-attributes.html#variable-substitution).

### 2 - Use the Logging Service

The Cloud Foundry Marketplace provides the [application-logs service](https://help.sap.com/viewer/product/APPLICATION_LOGGING/Cloud/en-US) which lets us stream our logs to a central application logging stack.
The service uses the ELK-Stack to process, store and visualize the logs of the bound applications.

1. Create an instance of the `application-logs` service with the name `app-logs`.
    Use the `lite` or `free` plan if available. (You can use `cf marketplace` to see the available plans).

    ??? example "Need help?"
        Use the `create-service` command:
        ```shell
        cf create-service application-logs lite app-logs
        ```

1. Add a `services:` mapping to each service in `manifest.yaml` with `app-logs` being an entry.

    ??? example "Need help?"
        Your `manifest.yaml` should be similar to the following:
{% if language == "Java" %}
        ```yaml
        applications:
        - name: users
          memory: 800MB
          buildpacks:
          - https://github.com/cloudfoundry/java-buildpack.git
          services:
          - app-logs
          path: users/target/users.jar
          routes:
          - route: users-((identifier)).((domain))
        - name: greetings
          memory: 800MB
          buildpacks:
          - https://github.com/cloudfoundry/java-buildpack.git
          services:
          - app-logs
          path: greetings/target/greetings.jar
          routes:
          - route: greetings-((identifier)).((domain))
          env:
            USERS_URL: https://users-((identifier)).((domain))/api/v1/user/
        ```
{% elif language == "Node.js" %}
        ```yaml
        applications:
          - name: users
            memory: 80MB
            command: npm start
            buildpacks:
              - https://github.com/cloudfoundry/nodejs-buildpack
            path: users
            routes:
              - route: users-((identifier)).((domain))
            services:
              - app-logs
          - name: greetings
            memory: 80MB
            command: npm start
            buildpacks:
              - https://github.com/cloudfoundry/nodejs-buildpack
            path: greetings
            routes:
              - route: greetings-((identifier)).((domain))
            env:
              USERS_URL: https://users-((identifier)).((domain))/api/v1/users
            services:
              - app-logs
        ```
{% endif %}

1. Save the file and push the changes with the following command:

    ```shell
    cf push --vars-file vars.yaml
    ```

!!! warning "Dropped logs"
    The `application-logs` service has a quota limit on memory per hour.
    If the message volume exceeds the plan limit, all incoming messages are rejected until the next interval begins.
    Although it is unlikely that you will run into this limit during the exercise, it is something you should keep in mind if you are planning to take this into production.
    Read more about the quota limits and enforcement in the [service's documentation](https://help.sap.com/viewer/ee8e8a203e024bbb8c8c2d03fce527dc/Cloud/en-US/cd1fb12a31844fcaa5834dff798dba4c.html).

### 3 - Take a Look at the Logs

The service provides a nice web interface for us to query the logs coming from our applications.

#### 3.1 Access the Logs

1. Run `cf target` and copy the **API endpoint**. Replace `api` with `logs` and open the resulting address in a browser.
    For example, if `cf target` returns `https://api.cf.us10.hana.ondemand.com`, open `https://logs.cf.us10.hana.ondemand.com` in your browser.

You may need to sign in, but afterwards you should be seeing a Kibana dashboard.

#### 3.2 Discovery

1. Click on the menu icon in the top left menu and then on Discover to open the "Discover" page.
1. Expand the time range and click refresh until you see logs.

It seems the service already captured many logs, just from pushing the apps.
You may see logs from other applications, unrelated to the training, running on the landscape (even from other orgs and spaces).

??? tip "Fields of Interest"

    On the left side in Kibanas Discovery page you'll see the **Available Fields**.
    There you can find the `component_name` which contains the name of the microservice and `msg` which is the log message itself.
    You can hover over those fields and click **Add** to have them included in the table-view on the right.

    You can also filter the logs by a specific field value:
    Therefor click on the field and select the value with the magnifier symbol.
    E.g. you can filter the logs by org, so that only relevant logs are shown.

### 4 - Generate Some Traffic

1. Determine the route of the `greetings` app with `cf apps`.
1. Open it in a browser with the following path appended: `/api/v1/greetings/1`
    You should see a nice greeting for **Jane**.

1. Back in the Kibana tab, refresh to see the newly generated logs.
    It may take a few seconds for the logs to appear.
    Just keep refreshing until you see them and make sure that you have selected an appropriate timeframe.

1. In the greetings tab, navigate to the following path: `/api/v1/greetings/99`.
    You should see a greeting for **User**.

{% if language == "Java" %}
1. Find the error stacktrace emitted by the greetings service in Kibana.

The lines of the stacktrace are shown as separate log messages.
It clutters up the view and is bad for searchability.
Time to change the log format for more cohesive log messages.
{% elif language == "Node.js" %}
1. Find the warning log with the message:

    `Response from users service was not ok: https://users-((identifier)).((domain))/api/v1/users/99 - {\"status\":404,\"statusText\":\"Not Found\"}`

    (with `((identifier))` and `((domain))` matching your actual vars).

    If you look at the code that emits that log message you will see that it's supposed to contain more information, such as the response's status.
    Some configuration is needed, for it to be included.
{% endif %}

{% if language == "Java" %}
### 5 - Configure JSON Format

1. Create a file named `logback-spring.xml` in the `src/main/resources` directory for **both services** with the following content:
    ```xml
    <configuration debug="false" scan="false">
        <appender name="STDOUT-JSON" class="ch.qos.logback.core.ConsoleAppender">
            <encoder class="com.sap.hcp.cf.logback.encoder.JsonEncoder"/>
        </appender>
        <!-- for local development, you may want to switch to a more human-readable layout -->
        <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>%date %-5level [%thread] - [%logger] [%mdc] - %msg%n</pattern>
            </encoder>
        </appender>
        <root level="${LOG_ROOT_LEVEL:-INFO}">
            <!-- Use 'STDOUT' instead for human-readable output -->
            <appender-ref ref="STDOUT-JSON" />
        </root>
        <!-- request metrics are reported using INFO level, so make sure the instrumentation loggers are set to that level -->
        <logger name="com.sap.hcp.cf" level="INFO" />
    </configuration>
    ```
    With this configuration we are telling logback to write the logs in JSON format.

1. Rebuild the `.jar` files by running the following command:
    ```shell
    mvn package
    ```

1. Push the updated applications by running the following command:
    ```shell
    cf push --vars-file vars.yaml
    ```

1. Refresh the page with the path `/api/v1/greetings/99` on the greetings app to provoke the error again.

1. Search the logs in Kibana for the log message including the complete stacktrace.
{% endif %}

{% if language == "Java" %}
### 6 - Add Metadata
{% elif language == "Node.js" %}
### 5 - Add Metadata
{% endif %}
The log messages can be further enhanced with metadata which can be extracted from HTTP headers.
{% if language == "Java" %}
The used logging library provides a servlet filter which does exactly that (see the [documentation](https://github.com/SAP/cf-java-logging-support/wiki/Instrumenting-Servlets)).

1. Add the following dependency to the `pom.xml` files of both services:
    ```xml
    <!-- We're using the Servlet Filter instrumentation -->
    <dependency>
        <groupId>com.sap.hcp.cf.logging</groupId>
        <artifactId>cf-java-logging-support-servlet</artifactId>
        <version>${cf-logging-version}</version>
    </dependency>
    ```

1. Load/Sync the maven changes.

1. Add the following snippet to the files `SpringBootGreetingApplication.java` and `SpringBootUserApplication.java`:
    ```java
    @Bean
    public FilterRegistrationBean<RequestLoggingFilter> loggingFilter() {
        FilterRegistrationBean<RequestLoggingFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new RequestLoggingFilter());
        filterRegistrationBean.setName("request-logging");
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST);
        return filterRegistrationBean;
    }
    ```

    1. Add the missing imports with the help of your IDE.
        For the class `DispatcherType` use `javax.servlet.DispatcherType`.

1. Rebuild the `.jar` files by running the following command:
    ```shell
    mvn package
    ```

{% elif language == "Node.js" %}
The used logging library provides a middleware which does exactly that (see the [documentation](https://github.com/SAP/cf-nodejs-logging-support#request-logs)).

1. Add the following line as the very first middleware to the `application.js` files in **both** the `users` and `greetings` service:
    ```javascript
    app.use(log.logNetwork)
    ```
1. Make use of the request context in your HTTP endpoints in each service by substituting the `log` objects by `req.logger` (don't forget the ones passed on as parameters to called methods).
{% endif %}
1. Push the updated applications by running the following command:
    ```shell
    cf push --vars-file vars.yaml
    ```

1. Hit the `greetings` endpoint again to generate some new logs.

1. Search the logs for new fields and check the values of the field named `correlation_id`.

??? info "What is the Correlation-ID?"

    The `correlation_id` uniquely identifies each user-request.

    When a user-request is delegated from one microservice to another, the `correlation-id` is written as an HTTP-header.
    The next microservice then picks up the `correlation-id` from the HTTP-header and includes it in every log message that is written in the context of this user-request.
    When a microservice receives a request without `correlation-id` (e.g. initial user interaction) it creates a new one.

You probably noticed that the logs from the two services have different correlation-IDs even though one gets called from the other.
A little more work is needed to make that correlation visible in the logs...

{% if language == "Java" %}
### 7 - Transmit the Correlation-ID
{% elif language == "Node.js" %}
### 6 - Transmit the Correlation-ID
{% endif %}
{% if language == "Java" %}
1. Add the following code in the `restTemplate` method in the class `SpringBootGreetingApplication`:
    ```java
    restTemplate.getInterceptors().add((request, body, execution) -> {
        request.getHeaders().set(HttpHeaders.CORRELATION_ID.getName(), LogContext.getCorrelationId());
        return execution.execute(request, body);
    });
    ```

    It ensures that all outgoing requests using the `restTemplate` have the correlation-ID header set.

    1. Add the missing imports with the help of your IDE.
        For the `HttpHeaders` use `com.sap.hcp.cf.logging.common.request.HttpHeaders`.

1. Rebuild the `.jar` files by running the following command:
    ```shell
    mvn package
    ```
{% elif language == "Node.js" %}
1. Add the following object as the second argument to the `fetch` call in the file `greetings/service/users-service.js`:

    ```javascript
    const response = await fetch(url, {
      headers: {
        // forwards the correlation id from the logging context to the users service
        'X-CorrelationID': log.getCorrelationId()
      }
    })
    ```

    `fetch` has two [parameters](https://developer.mozilla.org/en-US/docs/Web/API/WindowOrWorkerGlobalScope/fetch#parameters), with the second being an object containing custom settings that you want to apply to the request.
    By setting the `headers` property with an object literal, you can add any headers to the request.
{% endif %}

1. Push the updated applications by running the following command:
    ```shell
    cf push --vars-file vars.yaml
    ```

1. Hit the greetings endpoint again to generate some new logs.
1. Check whether both services now have the same correlation-ID for the same "request" in their logs.

??? tip "View Correlation-IDs in The Table"

    Amongst the **Available Fields** you should be able to find the `correlation_id`.
    You can add it to your table-view to have it easier to correlate each log using the correlation-ID.

    You can also filter by a specific correlation-ID to trace down the logs of a specific request.

{% if language == "Java" %}
### 8 - Find the Bug
{% elif language == "Node.js" %}
### 7 - Find the Bug
{% endif %}

A user noticed a discrepancy between the greeting ids shown at `/api/v1/greetings` and the actual greetings returned for those ids.
The user wanted to generate a greeting for **Erika**, which supposedly had the id `2`, but `/api/v1/greetings/2` returned a greeting for **Juan**.
Can you use the logs to find out where this discrepancy may be coming from?

## 🏁 Summary
Good job!
In this exercise you

- [x] set up distributed logging infrastructure
{% if language == "Java" %}
- [x] configured a JSON log format
{% endif %}
- [x] added metadata to your logs
- [x] set up the handling of correlation IDs
- [x] traced a bug through the Kibana interface

## 🦄 Stretch Goals
You should already have a good idea of all common parts by now, you could stop here... oooor you can fix that pesky bug.

## 📚 Recommended Reading
- [Distributed Logging Architecture in the Container Era](https://blog.treasuredata.com/blog/2016/08/03/distributed-logging-architecture-in-the-container-era/)
- [Good Practices for Distributed Logging](https://www.javacodegeeks.com/2017/07/distributed-logging-architecture-microservices.html)

## 🔗 Related Topics
{% if language == "Java" %}
- [Java Logging Support for Cloud Foundry Library](https://github.com/SAP/cf-java-logging-support#java-logging-support-for-cloud-foundry)
{% elif language == "Node.js" %}
- [Node.js Logging Support for Cloud Foundry Library](https://github.com/SAP/cf-nodejs-logging-support#nodejs-logging-support-for-cloud-foundry)
{% endif %}
