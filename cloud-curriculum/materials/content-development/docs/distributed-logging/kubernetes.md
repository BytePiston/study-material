# Distributed Logging in Kubernetes

_Disclaimer: We are [counting page hits](https://github.wdf.sap.corp/cloud-native-dev/usage-tracker) using a cookie to distinguish returning & new visitors._
<img src="https://cloud-native-dev-usage-tracker.cfapps.sap.hana.ondemand.com/pagehit/cc-materials/distributed-logging-k8s/1x1.png" alt="" height="1" width="1">

## 👷‍♂️👷‍♀️ Audience

{% if language == "Java" %}
Developers with basic knowledge of Java and Kubernetes.
{% elif language == "Node.js" %}
Developers with basic knowledge of Node.js and Kubernetes.
{% endif %}

## 🎯 Learning Objectives

In this exercise you will learn

- how to set up distributed logging infrastructure
- how to make APIs use a correlation id
- how to trace a request through the logs

<!-- Prerequisites-->
{% if language == "Java" %}
{% with
  required=[
    ('[Logging Basics](../../logging/java)'),
    ('[Kubernetes Basics](../../cloud-platforms/kubernetes-java)')
  ]
%}
{% include 'snippets/prerequisites/java.md' %}
{% endwith %}

{% elif language == "Node.js" %}
{% with
  required=[
    ('[Logging Basics](../../logging/nodejs)'),
    ('[Kubernetes Basics](../../cloud-platforms/kubernetes-nodejs)')
  ]
%}
{% include 'snippets/prerequisites/nodejs.md' %}
{% endwith %}
{% endif %}

### Other 🚀

- Access to a Kubernetes Cluster.
  You can create a trial cluster with [Gardener](https://dashboard.garden.canary.k8s.ondemand.com/).
  **Make sure to include the `Nginx Ingress` Add-on.** (It can also be added later.)

!!! danger "Combined cluster name and project name length"

    The combination of your `project name` and `cluster name` must not exceed 16 characters!
    This is required since the domain names (e.g. `app.ingress.<CLUSTER>.<PROJECT>.shoot.canary.k8s-hana.ondemand.com`) you will generate an SSL certificate for must not exceed the character limit of 64 characters. Since the "base" domain given to us is already quite long, this only leaves us with 16 characters that we are allowed to use.
    [See also](https://gardener.cloud/documentation/guides/administer_shoots/x509_certificates/)

## 🛫 Getting Started

{% if language == "Java" %}
{% with branch_name="distributed-logging", folder_name="distributed-logging-k8s-java" %}
{% include 'snippets/clone-import/java.md' %}
{% endwith %}

You should have three maven projects in the IDE:

- parent-distributed-logging
- greetings
- users
{% elif language == "Node.js" %}
{% with branch_name="distributed-logging", folder_name="distributed-logging-k8s-nodejs" %}
{% include 'snippets/clone-import/nodejs.md' %}
{% endwith %}

Finally, install the required dependencies by running the following command in each of the directories `greetings` and `users`:

```sh
npm install
```
{% endif %}

Verify that your Kubernetes client is connected to your cluster by running the following command:
```shell
kubectl cluster-info
```
It should print a message similar to the following, with `<CLUSTER>` and `<PROJECT>` being replaced:
```
Kubernetes control plane is running at https://api.<CLUSTER>.<PROJECT>.shoot.canary.k8s-hana.ondemand.com
CoreDNS is running at https://api.<CLUSTER>.<PROJECT>.shoot.canary.k8s-hana.ondemand.com/api/v1/namespaces/kube-system/services/kube-dns:dns/proxy
```
Remember the `<CLUSTER>` and `<PROJECT>` names (or the fact that you can get them using the above command), you will need them later.

If it is not connected you can see the first exercise of the [Kubernetes topic](../../cloud-platforms/kubernetes-java/#1-accessing-the-cluster) to find out how to configure `kubectl`.

## 🔍 Code Introduction

We provide two microservices: `greetings` and `users`.
Both expose a simple REST interface.
The `greetings` service calls the `users` service to retrieve the necessary information and then creates a pretty greeting text.

## 📗 Exercises

The microservices are almost ready to be deployed, so that you can focus on the distributed logging.

### 1 - Deploy the Services
To get you started quickly we provide the necessary `.yaml`-files in the directory `deployment/apps`.
{% if language == "Java" %}
It may be shown as part of the `parent-distributed-logging` project, depending on your IDE.
{% endif %}

1. Run the shell script `fill_placeholders.sh` with the arguments:
    1. Your c/d/i-number (with the letter being lowercase)
    1. The name of your Kubernetes cluster
    1. The name of the gardener project, which your cluster belongs to.

    Your terminal command should look similar to the following:
    ```shell
    ./fill_placeholders.sh i012345 my-cluster my-project
    ```
    ??? tip "Don't know what your cluster and project names are?"
        Re-read the [end of the the getting-started section above](./#getting-started)

1. Use the provided shell script with your (lowercase) c/d/i-number to create and push docker images for the applications:
    ```shell
    ./push_images.sh <your c/d/i-number>
    ```

1. Run the following command to deploy the apps to your cluster:
    ```shell
    kubectl apply -f deployment/apps
    ```

### 2 - Deploy the Infrastructure for Distributed Logging

We provide a set of yaml-files in the `deployment/efk-stack` folder containing the Kubernetes resources needed for the logging infrastructure.
Deploying those resources will set up EFK (Elasticsearch + Fluentd + Kibana, EFK-Stack) to implement Distributed Logging.
<!-- TODO: insert link for further reading on efk or the distributed logging architecture? -->

1. Deploy the EFK-Stack by running:

    ```shell
    kubectl apply -f deployment/efk-stack
    ```

    This will deploy the infrastructure as needed for this exercise.
    All resources will be bound to the namespace `kube-logging`.
    You can view and monitor the deployed resources with:

    ```shell
    kubectl get all --namespace kube-logging
    ```

The pods of the EFK components should be all up and running (it might take a minute or two for them to init and start up).

### 3 - Take a Look at the Logs

Once the pods are ready you can view the Kibana user-interface with your web-browser.
Kibana needs a little configuration before you can explore the logs.
You'll have to create an index pattern to differentiate sources of log events.
This can be done through the graphical interface as explained in [the guide](https://www.elastic.co/guide/en/kibana/current/index-patterns.html) but we also provide a shell script, which uses Kibana's REST API.

1. Run the provided shell script to create an index pattern:
    ```shell
    ./create_index_pattern.sh
    ```

1. Run
    ```shell
    kubectl get ingress --namespace kube-logging
    ```

1. Copy the link (`HOSTS` column) and enter it in your web-browser.

1. Click on the compass icon in the left menu bar (it is the topmost entry) to open the "Discover" page.

1. Expand the time range and click refresh until you see logs.

Looks like Fluentd has collected quite a few logs already.

??? tip "Add Fields of Interest"

    On the left side in Kibanas Discovery page you'll see the **Available Fields**.
    There you can find the `kubernetes.labels.app` which contains the name of the microservice and `msg` which is the log message itself.
    Hover over those fields and click **Add** to have them included in the table-view on the right.

    You can also filter the logs by a specific field value.
    Click on the field and select the value with the magnifier symbol.

### 4 - Generate Some Traffic

1. Run
    ```shell
    kubectl get ingress
    ```

1. Copy the link (`HOSTS` column) and enter it in your web-browser with the following path appended: `/api/v1/greetings/1`
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

1. Build and push the docker images by re-running the provided `push_images.sh` script.
    The script also updates the deployed instances by deleting the pods (if they exist).
1. Refresh the page with the path `/api/v1/greetings/99` on the greetings app to provoke the error again.
1. Search the logs in Kibana for the log message including the complete stacktrace.

### 6 - Add Metadata
The log messages can be further enhanced with metadata which can be extracted from HTTP headers.
The used logging library provides a servlet filter which does exactly that (see the [documentation](https://github.com/SAP/cf-java-logging-support/wiki/Instrumenting-Servlets)).

1. Add the following dependency to the `pom.xml` files of **both services**:
    ```xml
    <!-- We're using the Servlet Filter instrumentation -->
    <dependency>
        <groupId>com.sap.hcp.cf.logging</groupId>
        <artifactId>cf-java-logging-support-servlet</artifactId>
        <version>${cf-logging-version}</version>
    </dependency>
    ```

1. Load/Sync the maven changes.

1. Add the following snippet to the files `SpringBootGreetingApplication.java` and `SpringBootUserApplication.java` to register the ServletFilter (RequestLoggingFilter):
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

1. Build and push the docker images by re-running the provided `push_images.sh` script.
{% elif language == "Node.js" %}
### 5 - Add Metadata
The log messages can be further enhanced with metadata (e.g. Correlation-ID) which can be extracted from HTTP headers.
The used logging library provides a middleware which does exactly that (see the [documentation](https://github.com/SAP/cf-nodejs-logging-support#request-logs)).

1. Add the following line to the `application.js` files in **both services**:
    ```javascript
    app.use(log.logNetwork)
    ```

    The library let's you choose between the global and request logging context.
    In the request logging context there will be additional fields (e.g. correlation_id, request_id, etc.) attached to the log message.
    See the [documentation](https://github.com/SAP/cf-nodejs-logging-support#logging-contexts).

1. Make use of the request context in your HTTP endpoints in each service by substituting the `log` objects by `req.logger` (don't forget the ones passed on as parameters to called methods).
1. Build and push the docker images by re-running the provided `push_images.sh` script.
1. Also make sure to delete the affected pods (running the old docker images) using `kubectl delete pod`
{% endif %}

1. Hit the greetings endpoint again to generate some new logs.
1. Search the logs for new fields and check the values of the field named `correlation-id`.

??? info "What is the Correlation-ID?"

    The `correlation-id` uniquely identifies each user-request.
    When a user-request is delegated from one microservice to another, the `correlation-id` is written as an HTTP-header.
    The next microservice then picks up the `correlation-id` from the HTTP-header and includes it in every log message that is written in the context of this user-request.
    When a microservice receives a request without `correlation-id` (e.g. initial user interaction) it creates a new one.

??? tip "Add Correlation-ID to The Table"

    Amongst the **Available Fields** you should be able to find the `correlation-id`.
    Add it to your table-view to make it easier to correlate each log using the correlation-ID.

    You can also filter by a specific correlation-ID to trace down the logs of a specific request.

You probably noticed that the logs from the two services have different correlation-IDs even though one gets called from the other.
A little more work is needed to make that correlation visible in the logs...

{% if language == "Java" %}
### 7 - Transmit the Correlation-ID
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

1. Build and push the docker images by re-running the provided `push_images.sh` script.
{% elif language == "Node.js" %}
### 6 - Transmit the Correlation-ID
1. Add the following object as the second argument to the `fetch` call in the file `user-service.js`:
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

1. Build and push the docker images by re-running the provided `push_images.sh` script.

1. Also make sure to delete the affected pod (running the old docker image) using `kubectl delete pod`
{% endif %}

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

{% if language == "Java" %}
- [x] set up distributed logging infrastructure
- [x] configured a JSON log format
- [x] set up a servlet filter to handle or generate correlation IDs
- [x] traced a bug through the Kibana interface
{% elif language == "Node.js" %}
- [x] set up distributed logging infrastructure
- [x] set up the handling of correlation IDs
- [x] used an express middleware to handle or generate correlation IDs
- [x] traced a bug through the Kibana interface
{% endif %}

## 🦄 Stretch Goal
You should already have a good idea of all common parts by now, you could stop here... oooor you can fix that pesky bug.

## 📚 Recommended Reading
- [Distributed Logging Architecture in the Container Era](https://blog.treasuredata.com/blog/2016/08/03/distributed-logging-architecture-in-the-container-era/)
- [Good Practices for Distributed Logging](https://www.javacodegeeks.com/2017/07/distributed-logging-architecture-microservices.html)

## 🔗 Related Topics
{% if language == "Java" %}
- [Cluster-level Logging in Kubernetes with Fluentd](https://medium.com/kubernetes-tutorials/cluster-level-logging-in-kubernetes-with-fluentd-e59aa2b6093a)
- [Documentation of the used logging library](https://github.com/SAP/cf-java-logging-support/wiki)
{% elif language == "Node.js" %}
- [Node.js Logging Support for Cloud Foundry Library](https://github.com/SAP/cf-nodejs-logging-support#nodejs-logging-support-for-cloud-foundry)
{% endif %}
