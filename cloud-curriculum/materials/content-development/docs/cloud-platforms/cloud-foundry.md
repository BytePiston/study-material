# Cloud Foundry Basics ({{ language }})

_Disclaimer: We are [counting page hits](https://github.wdf.sap.corp/cloud-native-dev/usage-tracker) using a cookie to distinguish returning & new visitors._
<img src="https://cloud-native-dev-usage-tracker.cfapps.sap.hana.ondemand.com/pagehit/cc-materials/cloud-foundry/1x1.png" alt="" height="1" width="1">

## 👷‍♂️👷‍♀️ Audience

Developers who want to learn how applications can be deployed to Cloud Foundry.

## 🎯 Learning Objectives

In this exercise you will learn...

- how to push an application using the Cloud Foundry CLI.
- how to configure routes through the `manifest.yml` file.
- how to manipulate deployed applications.

<!-- Prerequisites-->
{% if language == "Java" %}
{% with
  tools=[
    ('[**CF client V8**](https://github.com/cloudfoundry/cli/wiki/V8-CLI-Installation-Guide)')
  ],
  required=[
    ('(Very) basic Java development skills')
  ]
%}
{% include 'snippets/prerequisites/java.md' %}
{% endwith %}
{% elif language == "Node.js" %}
{% with
  tools=[
      ('[**CF client V8**](https://github.com/cloudfoundry/cli/wiki/V8-CLI-Installation-Guide)')
  ],
  beneficial=[
      ('[Mocha](https://mochajs.org)')
  ]
%}
{% include 'snippets/prerequisites/nodejs.md' %}
{% endwith %}

{% endif %}

**Other**

- A [Cloud Foundry trial account](https://cockpit.hanatrial.ondemand.com/cockpit/#/home/trial) - make sure to use the Region **US East(VA) cf-us10** as not all backing services are available in every region/landscape.

## 🛫 Getting Started

{% if language == "Java" %}

{% with branch_name="cloud-platforms", folder_name="cloud-platforms-java-cf" %}
{% include 'snippets/clone-import/java.md' %}
{% endwith %}

{% elif language == "Node.js" %}

{% with branch_name="cloud-platforms", folder_name="cloud-platforms-nodejs-cf" %}
{% include 'snippets/clone-import/nodejs.md' %}
{% endwith %}

--8<--- "snippets/npm-install-dependencies.md"

{% endif %}

## 🔍 Code Introduction

We have a service that delivers a fortune cookie from a collection of quotes stored in a database. The goal of this exercise is to get it running on Cloud Foundry.

The important files are:

{% if language == "Java" %}

- `com.sap.cc.fortunecookies.FortuneCookieController`: the REST layer - responds with a fortune cookie at the root url
- `com.sap.cc.fortunecookies.FortuneCookieRepository`: class responsible for retrieving the quotes from the db
{% elif language == "Node.js" %}
- `lib/config.js`: the configuration for the application
- `lib/connection-pool.js`: the connection pool for accessing the database
- `lib/migrate.js`: the [db-migrate](https://db-migrate.readthedocs.io/en/latest/) script that runs before the application starts
- `lib/application.js`: the [express](https://expressjs.com) app that responds with a fortune cookie at the root url
- `lib/fortune-cookie-service.js`: the service that retrieves a random fortune cookie quote from the db
- `lib/server.js`: the main entry point that wires up everything and starts the server
{% endif %}

The other folder and files are not important for now.

## 📗 Exercises

Type `cf` or `cf help` in a terminal to see all `cf` commands, and `cf help <command>` to see the description and usage details for the particular command.

### 1 - Configuration and Login

Cloud Foundry instances are accessed through their api-url.

#### 1.1 Configure the API Endpoint

Use `#!shell cf api` to make sure that the API endpoint is set to `https://api.cf.us10.hana.ondemand.com`.

#### 1.2 Login

Find a command to login and use it. If your user is assigned to multiple orgs or spaces, you might be prompted to select a particular org or space.

### 2 - Look Around

Applications in Cloud Foundry are grouped into spaces and spaces are grouped into organizations.

#### 2.1 Organizations

Find out more about organizations using:

1. `#!shell cf orgs` to view all organizations you are a member of
1. `#!shell cf target` with the `-o` option to select an organization (well, sorry if you only have access to one org)
1. `#!shell cf org-users ...` to see all users of an org, grouped by org role

#### 2.2 Spaces

Find out more about spaces using:

1. `#!shell cf spaces` to view all spaces inside of your selected org
1. `#!shell cf target` with the `-s` option to select a space (well, sorry if you only have access to one space)
1. `#!shell cf space-users ...` to see all users of a space, grouped by space role

??? info "Creating a new space"
    Depending on your org role you can try to create another space using `#!shell cf create-space ...`.

!!! warning "Switch back before you continue"
    If you switched to (targeted) a different org and/or space, make sure you switch back before you continue. Use `#!shell cf target` to display your current target.

### 3 - Deployment

Deploy the app in your space. In Cloud Foundry terminology, the deployment is called _push_. To deploy your app,

{% if language == "Java" %}

1. build the jar file first:

    ```shell
    mvn package
    ```

1. push the app to your currently targeted Cloud Foundry space:

    ```shell
    cf push fortune-cookies -p target/fortune-cookies.jar --random-route
    ```

{% elif language == "Node.js" %}

1. push the app to your currently targeted Cloud Foundry space:

    ```shell
    cf push fortune-cookies --random-route
    ```

{% endif %}

1. scratch your head why it failed - and then check the logs:

   ```shell
   cf logs fortune-cookies --recent
   ```

!!! warning "Expected failure"

    The app failed to start. This is expected!

    **The app failed to start because the database service is not yet available.**

    We will fix this in the [next exercise](#4-services).

{% if language == "Node.js" %}
??? info "My app does not work in CF"
    If your code works locally but not within your CF deployment, your local node.js version and the one that CF uses might differ.
    First of all check what node versions are supported by checking the latest release of the [node.js buildpack](https://github.com/cloudfoundry/nodejs-buildpack/releases).

    Ideally your code should get compiled by a node.js version that is newer than your local node.js version. Choose the respective version and specify it in your `package.json` like the following:

    ```json
    "engines": {
        "node": ">=16"
    }
    ```
{% endif %}

??? info "Routes"
    You used the `--random-route` flag to make sure you get a URL that is not already reserved by another app in a different space. By default/if not specified explicitly, Cloud Foundry uses the app name as hostname/prefix, suffixed with the default domain.

    Especially for generic app names like `hello-world`, the chance is high that the URL is already taken and cannot be used by your app.

    There are more options on `#!shell cf push` that give you fine-grained control on the route, as well as `#!shell cf map-route` and `#!shell cf unmap-route` to (un-)map routes independent from the push operation.

### 4 - Services

As you could see in the logs, the app cannot connect to the database. The database is something that the application needs, but it's not part of the application itself. In Cloud Foundry terminology, such things are called "services". Another typical example could be a messaging service.

#### 4.1 Create DB Service

Your application uses a PostgreSQL database.
Create a PostgreSQL database service instance that we can use later on in our application by:

1. checking the marketplace for available services: `cf marketplace` - find the right entry
1. creating a new Postgres SQL service: `cf create-service ...` - if there are multiple plans, use the `trial` or `development` plan

??? example "Need help?"
    If you just type `#!shell cf create-service`, the help will tell you that the first argument is the service name (in below example: `postgresql-db`), second is the plan (in below example: `development`), and third is your instance name - you can freely choose, as long as it is unique inside your space.

    ```shell
    cf create-service postgresql-db trial fortune-cookies-db
    ```

??? info "It will take a while...take a break"
    The service creation might take a few minutes - don't you need a coffee?
    Check the status from time to time:
    ```shell
    cf services

```

#### 4.2 Bind Service

After you have successfully created the database service instance, provide the connection details to your application. In Cloud Foundry, this concept is called "binding". To bind your app to the service,

1. run command `#!shell cf bind-service ...`
    - you should see a warning to restage the app, this is because binding the app to the service changes the environment, and most application read the environment only at startup time
1. restage the app: `#!shell cf restage ...`
    - you will still see the error
1. inspect the environment of your app: `#!shell cf env ...`
    - you should see an environment variable `VCAP_SERVICES` which is now visible to your app, a JSON string containing db connection details

??? info "Binding is not doing much"
    As you can see, binding just provides service details to the app. Your app still needs to be "told" to use this information to connect to the db. You'll do this in the next step.

??? info "Restage vs. restart"
    There's also a `#!shell cf restart ...` command. You may wonder what's the difference between `restart` and `restage`. It's explained nicely in [this Stack Overflow post](https://stackoverflow.com/questions/50475750/what-is-difference-between-restage-and-restart-in-pcf#:~:text=Restart%20your%20app%20to%20refresh,When%20to%20Restage%3A&text=The%20staging%20process%20has%20access,the%20contents%20of%20the%20droplet).

#### 4.3 Connect to DB

The app has now access to the database connection information. To establish the connection we need to read the environment variable `VCAP_SERVICES` and extract the connection details.

1. run `#!shell cf env` and check the output, you should find a `VCAP_SERVICES` env variable that contains the connection information
{% if language == "Java" %}
1. add the [cfenv library](https://github.com/pivotal-cf/java-cfenv), that automatically supplies your app with the connection info from `VCAP_SERVICES` env variable, to your `pom.xml`:

    ```xml
    <dependency>
        <groupId>io.pivotal.cfenv</groupId>
        <artifactId>java-cfenv-boot</artifactId>
        <version>2.2.2.RELEASE</version>
    </dependency>
    ```

{% elif language == "Node.js" %}

1. install the [cfenv](https://www.npmjs.com/package/cfenv) module that helps you parsing and accessing the `VCAP_SERVICES` env variable:

    ```shell
    npm install cfenv
    ```

1. create a file `vcap.json` at the root of project

    ```json
    {
        "application": {
            "port": 3000
        },
        "services": {
            "fortune-cookies-db": [{
                "name": "fortune-cookies-db",
                "credentials": {
                    "uri": "postgres://postgres:postgres@localhost/postgres",
                    "sslcert": null,
                    "sslrootcert": null
                }
            }]
        }
    }
    ```

    This file holds the _default_ config for the [cfenv](https://www.npmjs.com/package/cfenv) module when running the app **locally** and the `VCAP_SERVICES` environment variable has not been set.

1. edit `lib/config.js` and use the [cfenv](https://www.npmjs.com/package/cfenv) module to parse the `VCAP_SERVICES` environment variable and export the `app` and `postgres` config:

    ```javascript
    import cfenv from 'cfenv'

    const appEnv = cfenv.getAppEnv({
        vcapFile: 'vcap.json'
    })

    const { app: { port } } = appEnv

    const { uri: connectionString, sslcert: cert, sslrootcert: ca } = appEnv.getServiceCreds('fortune-cookies-db')

    export default {
        app: {
            port
        },
        postgres: {
            connectionString,
            ssl: (cert && ca) ? { cert, ca } : false
        }
    }
    ```

Now that you have retrieved the connection information for the database in the `config` object, it will be used to initialize the `connection-pool` and the underlying [pg](https://node-postgres.com) driver.

{% endif %}

#### 4.4 Push Updated App

Push your changes to Cloud Foundry and verify things are working by:

{% if language == "Java" %}

1. building the jar file:

    ```shell
    mvn package
    ```

1. pushing the changed app and checking it's successful (use `#!shell cf logs ... --recent` if not):

    ```shell
    cf push fortune-cookies -p target/fortune-cookies.jar --random-route
    ```

    - If the app fails to start, execute the following command (if on Windows use git bash):

        ```shell
        cf set-env fortune-cookies JBP_CONFIG_SPRING_AUTO_RECONFIGURATION '{enabled: false}'
        ```

    - Afterwards, `restage` the app, so that the environment variable becomes effective.
{% elif language == "Node.js" %}
1. pushing the app and checking it's successful (use `#!shell cf logs ... --recent` if not)

    ```shell
    cf push fortune-cookies --random-route
    ```

{% endif %}

1. opening the app URL in the browser - it should display a fortune now

    !!! info "Find URL in console output"
        If pushing is successful, the URL should be printed in the console output.

        If you face trouble opening the URL in the browser, you probably need to explicitly prefix the URL with `https://`

??? info "See more app details"
    To show more information on the deployed app like memory consumption and CPU utilization:

    ```shell
    cf app fortune-cookies
    ```

### 5 - Manifest

You can configure your application by only using the Cloud Foundry CLI and perhaps you prefer working with shell scripts. An alternative Cloud Foundry provides are manifest files.

When the command `#!shell cf push` is executed, it first looks for a file named `manifest.yml` in the current directory (some options prevent this). In it you can specify the configuration for your application.

Create a file named `manifest.yml` in the project root directory with the following content:

{% if language == "Java" %}

```YAML
applications:
- name: fortune-cookies
  memory: 800MB
  path: target/fortune-cookies.jar
  buildpacks:
  - https://github.com/cloudfoundry/java-buildpack.git
  env:
    # see https://github.tools.sap/cloud-curriculum/materials/blob/main/cloud-platforms/cloud-foundry/java-memory-allocation.md
    JBP_CONFIG_OPEN_JDK_JRE: '{ memory_calculator: { stack_threads: 200 }}'
    MALLOC_ARENA_MAX: 4
  services:
  - fortune-cookies-db
```

{% elif language == "Node.js" %}

```YAML
applications:
- name: fortune-cookies
  memory: 80MB
  command: npm start
  buildpacks:
  - https://github.com/cloudfoundry/nodejs-buildpack
  services:
  - fortune-cookies-db
```

{% endif %}

If you now want to deploy your app again, you do not have to specify an application name in the command, as it can be read from the manifest. `#!shell cf push` suffices.

!!! info "Specifying the Buildpack"
    Before, you were able to push an application without specifying a buildpack.
    That works because Cloud Foundry can (or tries to) detect which buildpack should be used. However it is a good practice to specify it. To do so, you simply supply a URL to the buildpack's Git repository.

### 6 - Cleanup Operations

Now you've learned how to create a bunch of things: an app instance, a service instance and URLs/routes. How to cleanup the mess if you don't need all of these any longer? Here's how.

#### 6.1 Stop App

1. Stop your app using `#!shell cf stop ...`.
2. Run `#!shell cf apps` thereafter and convince yourself the app is stopped.
3. Try to access the URL in the browser, you shouldn't get any response. If you still do, did you probably map the route to another app in the same space?

#### 6.2 Delete App

Delete the app now using `#!shell cf delete ...`. This will free up the resources and help to stay within your quota limits.

#### 6.3 Delete Service

You deleted the app, now it's time for the service to go. Run `#!shell cf delete-service ...` to delete it. RIP in peace.

#### 6.4 Delete Route

1. Run `#!shell cf routes` - you will see that the route is still available, although not used by any app.
2. Run `#!shell cf delete-route ...` or simply `#!shell cf delete-orphaned-routes` to make the unused routes available again.

!!! info "Orphaned routes"
    Cloud Foundry keeps routes, even if they are not bound to any app, for a reason: consider you deleted or unmapped your app only temporarily from a route, and later on want to use it again. If the route automatically got deleted, then someone else could use the route for an app in a different org/space. When you then later on want to use the route again, you can't, since it is already taken.

## 🏁 Summary

Good job!
In the prior exercises you pushed an application using the Cloud Foundry CLI, bound it to a backing service and connected to the database, and then you stored the deployment information in a `manifest.yml` to simplify the deployment.

## 🦄 Stretch Goals

You should already have a good idea of all common parts by now, you could stop here... oooor you can explore a little further:

- Deploy the other type of app (Java/Node.js) to find out how they differ. What happens when you have both running and map them to the same route?
- Map another route to your application using `#!shell cf map-route`. In what situations can this be useful?

## 📚 Recommended Reading

- [cf push](https://docs.cloudfoundry.org/devguide/push.html)
- [Configuring Routes and Domains](https://docs.cloudfoundry.org/devguide/deploy-apps/routes-domains.html)
- [Managing Service Instances](https://docs.cloudfoundry.org/devguide/services/managing-services.html)
{% if language == "Java" %}
- [Tips for Java developers](https://docs.cloudfoundry.org/buildpacks/java/java-tips.html)
- [Java memory allocation in CloudFoundry](https://github.tools.sap/cloud-curriculum/materials/blob/master/cloud-platforms/cloud-foundry/JavaMemoryAllocation.md)
{% elif language == "Node.js" %}
- [Tips for Node.js developers](https://docs.cloudfoundry.org/buildpacks/node/node-tips.html)
- [Node-Postgres SSL](https://node-postgres.com/features/ssl)
{% endif %}

## 🔗 Related Topics

- [SAP Kernel Services SampleApp & Tutorial](https://pages.github.tools.sap/KernelServices/adoption-guide/cf-sample-app): has information and tutorials on the SAP Kernel Services for CF
- [User-provided services](https://docs.cloudfoundry.org/devguide/services/user-provided.html): useful when you need a service that is not available in the Cloud Foundry marketplace.
{% if language == "Java" %}
- [Buildpacks](https://docs.cloudfoundry.org/buildpacks/): more information on buildpack and buildpack-specific configuration options, e.g. to optimize the memory config for your Java app.
{% elif language == "Node.js" %}
- [Buildpacks](https://docs.cloudfoundry.org/buildpacks/): more information on buildpack and buildpack-specific configuration options, e.g. to optimize the memory config for your Node.js app.
{% endif %}
- [Managing Apps](https://docs.cloudfoundry.org/devguide/managing-apps-index.html): advanced things like running one-off tasks, scaling apps and health checks.
- [Orgs, Spaces, Roles and Permissions](https://docs.cloudfoundry.org/concepts/roles.html): Few more infos.
- You might want to have a look if [Kyma](https://jam4.sapjam.com/groups/QrsMLb8Me6YjlNkgcm5Ku4/overview_page/sRvzqbb8tAQUqdgBxkRrte) is a better fit for your specific use case
