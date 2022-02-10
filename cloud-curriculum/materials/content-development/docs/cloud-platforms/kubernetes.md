# Kubernetes Basics ({{ language }})

_Disclaimer: We are [counting page hits](https://github.wdf.sap.corp/cloud-native-dev/usage-tracker) using a cookie to distinguish returning & new visitors._
<img src="https://cloud-native-dev-usage-tracker.cfapps.sap.hana.ondemand.com/pagehit/cc-materials/kubernetes/1x1.png" alt="" height="1" width="1">

## 👷‍♂️👷‍♀️ Audience

Developers who want to learn how to deploy an application using Kubernetes.

## 🎯 Learning Objectives

In this exercise you will learn

- how to create a Docker image for your application
- how to deploy an application on a Kubernetes cluster

{% if language == "Java" %}
<!-- Prerequisites-->
{% with
  tools=[
    ('**A Terminal**'),
    ('**Docker** (install on your platform following [these instructions](https://docs.docker.com/engine/install/))'),
    ('**Kubectl** (comes bundled with Docker for Desktop, or install following [these instructions](https://kubernetes.io/docs/tasks/tools/install-kubectl/))')
  ],
  required=[
    ('[Basic Docker knowledge](https://docs.docker.com/get-started/overview/)')
  ]
%}
{% include 'snippets/prerequisites/java.md' %}
{% endwith %}
{% elif language == "Node.js" %}
{% with
  tools=[
    ('[**Docker**](https://docs.docker.com/engine/install/)'),
    ('[**Kubectl**](https://kubernetes.io/docs/tasks/tools/install-kubectl/) (comes bundled with Docker for Desktop, or install following these instructions)')
  ],
  beneficial=[
    '[Mocha](https://mochajs.org)'
  ]
%}
{% include 'snippets/prerequisites/nodejs.md' %}
{% endwith %}

{% endif %}

**Other**

- Access to a Kubernetes Cluster.
  You can create a trial cluster with [Gardener](https://dashboard.garden.canary.k8s.ondemand.com/).
  Make sure to include the `Nginx Ingress` Add-on. (It can also be added later.)

!!! danger "Combined cluster name and project name length"

    The combination of your `project name` and `cluster name` must not exceed 16 characters!
    This is required since the domain names (e.g. `app.ingress.<CLUSTER>.<PROJECT>.shoot.canary.k8s-hana.ondemand.com`) you will generate an SSL certificate for must not exceed the character limit of 64 characters. Since the "base" domain given to us is already quite long, this only leaves us with 16 characters that we are allowed to use.
    [See also](https://gardener.cloud/documentation/guides/administer_shoots/x509_certificates/)

## 📗 Exercises

### 1 - Accessing the Cluster

First you need to configure `kubectl` to communicate with your cluster.

{% include 'snippets/k8s-access-instructions.md' %}

### 2 - The Application

The application that is going to be deployed, is a simple web app that is provided in a git repository.
It retrieves a random quote from a database and displays it on the browser.

#### 2.1 Clone the Repository

{% if language == "Java" %}

{% with branch_name="cloud-platforms", folder_name="cloud-platforms-java-k8s" %}
{% include 'snippets/clone-import/java.md' %}
{% endwith %}

{% elif language == "Node.js" %}

{% with branch_name="cloud-platforms", folder_name="cloud-platforms-nodejs-k8s" %}
{% include 'snippets/clone-import/nodejs.md' %}
{% endwith %}

--8<--- "snippets/npm-install-dependencies.md"

{% endif %}

### 3 - Dockerize Your Application

Docker images are a common way to package applications for deployment in the cloud.
Let's learn how to turn the provided application into a docker image and push it to the registry.

#### 3.1 Create a Dockerfile

Create a file named `Dockerfile`, in the root directory of the project, with the following content:
{% if language == "Java" %}

```Dockerfile
FROM openjdk:8-jdk-alpine
COPY target/fortune-cookies.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

??? info "Dockerfile walkthrough"
    - `FROM openjdk:8-jdk-alpine`: Base image to use for subsequent instructions
    - `COPY target/fortune-cookies.jar app.jar`: copy the `jar` file into the container
    - `ENTRYPOINT ["java","-jar","/app.jar"]`: start the application

{% elif language == "Node.js" %}

```Dockerfile
FROM node:16-alpine

ENV NODE_ENV=production

WORKDIR /app

COPY package*.json ./

RUN npm ci --only=production

COPY lib ./lib/
COPY migrations ./migrations/

EXPOSE 3000

CMD [ "npm", "start" ]
```

??? info "Dockerfile walkthrough"

    - `FROM  node:16-alpine`: Base image to use for subsequent instructions
    - `ENV NODE_ENV=production`: Set the environment variable `NODE_ENV` to `production` (many modules such as [express](http://expressjs.com/en/advanced/best-practice-performance.html#set-node_env-to-production) handle this variable automatically, e.g. by reducing debug output)
    - `WORKDIR /app`: Set the working directory to `/app` for all subsequent instructions
    - `COPY package*.json ./`: copy both `package.json` and `package-lock.json` into the container at the path `./`
    - `RUN npm ci --only=production`: install dependencies using [npm-ci](https://docs.npmjs.com/cli/v7/commands/npm-ci)(`--only=production` is a flag that tells npm to only install production dependencies)
    - `COPY lib ./lib/`: copy the `lib` directory into the container at path `./lib/`
    - `COPY migrations ./migrations/`: copy the `migrations` directory into the container at path `./migrations/`
    - `EXPOSE 3000`: expose the application on port 3000
    - `CMD [ "npm", "start" ]`: start the application

{% endif %}

#### 3.2 Build and Push to the Registry

We provide a docker image registry at `cc-ms-k8s-training.common.repositories.cloud.sap`, which you can use to store your docker images for this exercise.
A registry is needed, because the Kubernetes cluster cannot (and should not) pull images from your machine.
To prevent overriding images pushed by other participants, we ask you to put your D/C/I number into the image name in the following instructions.

{% if language == "Java" %}

1. Build a `jar` file with the following command:

    ```shell
    mvn package
    ```

{% endif %}

1. Build the docker image with the following command:

    ```shell
    docker build -t cc-ms-k8s-training.common.repositories.cloud.sap/fortune-cookies-<your D/C/I number> .
    ```

    !!! warning "Insert your D/C/I number"
        Replace `<your D/C/I number>` with your real D/C/I number in the above command.
        All letters in a tag must be lowercase!

    ??? info "Command explanation"
        The dot at the end signifies that the current directory should be sent to the docker daemon as build context.
        The `-t` option specifies that the image should be tagged.
        A tag consists of an optional registry hostname (the default is docker's public registry), a name and an optional tag (the default is `latest`).
        Images can be referenced by tag, so in order to push you only have to supply an image tag.

1. Log in to the Docker repository with following command:

    ```shell
    docker login -u "claude" -p "9qR5hbhm7Dzw6BNZcRFv" cc-ms-k8s-training.common.repositories.cloud.sap
    ```

1. Push the built image to the registry with the following command (with your D/C/I number inserted):

    ```shell
    docker push cc-ms-k8s-training.common.repositories.cloud.sap/fortune-cookies-<your D/C/I number>
    ```

### 4 - Create a Deployment for Application

Let's start with the deployment of our application.

#### 4.1 Create a YAML File

1. Create a file named `app.yaml` at the root of the project with the following content:

    ```YAML
    apiVersion: apps/v1
    kind: Deployment
    metadata:
      name: fortune-cookies
    spec:
      selector:
        matchLabels:
          app: fortune-cookies
      replicas: 1
      template:
        metadata:
          labels:
            app: fortune-cookies
        spec:
          containers:
            - name: app
              image: cc-ms-k8s-training.common.repositories.cloud.sap/fortune-cookies-<your D/C/I number>
              imagePullPolicy: Always
              ports:
                - containerPort: {{ application_port }}
    ```

    ??? info "Fields walkthrough"
        - `apiVersion`: Which version of the Kubernetes API we are using to create this object
        - `kind`: What kind of object we are creating
        - `metadata.name`: Name that uniquely identifies the object
        - `spec`: The desired state of the object
            - `selector`: A label selector is a query over a set of resources. It must match the pod template's label.
            - `replicas`: Number of desired pods
            - `template`: Describes the pods that will be created
                - `metadata.labels`: Map of string keys and values that can be used to organize and categorize objects.
                - `spec`: Specification of the desired behavior of the pod
                    - `containers`: List of containers belonging to the pod
                        - `name`: Name of the container specified as a DNS_LABEL
                        - `image`: Docker image name
                        - `ports.containerPort`:  Number of port to expose on the pod's IP address

1. Replace the placeholder: `<your D/C/I number>`

#### 4.2 Apply the File

1. Apply the file to the cluster with the following command:

  ```shell
  kubectl apply -f app.yaml
  ```

1. Run `kubectl get deployments` to see the newly created deployment.
1. Run `kubectl get pods` to see the pod, created by the deployment.

#### 4.3 Configure Registry Access

No pods were created, because Kubernetes is unable to pull the images from the registry.
Can you figure out why?

1. Use `kubectl describe pod <pod-name>` to see what causes the failure of the pull.

1. Looks like Kubernetes does not have the credentials to perform the pull from the registry.
  Run the following command to let the Kubernetes cluster authenticate to the registry:

    ```
    kubectl create secret docker-registry regcred --docker-server=cc-ms-k8s-training.common.repositories.cloud.sap --docker-username=claude --docker-password=9qR5hbhm7Dzw6BNZcRFv
    ```

1. Add the following to the Deployment in the `app.yaml` on the same indentation level as `containers`:

   ```YAML
      imagePullSecrets:
            - name: regcred
   ```

1. Apply your changes with `kubectl apply -f app.yaml`.

1. Check the status of the pod that got created by your Deployment object with `kubectl get pods`. It should be failing to start.

1. Check the logs of the failing pod with `kubectl logs`.

!!! warning "Expected failure"

    The app failed to start. This is expected!

    **The app failed to start because the database service is not yet available.**

    We will fix this in the [next exercise](#5-deploy-a-database).

### 5 - Deploy a Database

If you look into the source code of the application, you will see that it needs to have a database connected where it stores the quotes.
In this exercise you are going to deploy a database on your Kubernetes cluster.

#### 5.1 Create a Deployment

1. Create a file named `db.yaml`.
1. In it specify a `Deployment` to fulfill following requirements:
    - It should bear the name `fortune-cookies-db`.
    - It should have one replica.
    - It should select pods, which match the label `app: fortune-cookies-db`
    - It's pods should...
        - have the label `app: fortune-cookies-db`.
        - run a container with...
            - the name `app`.
            - the image `postgres:11-alpine`.
            - the port `5432` exposed.
    - For the sake of simplicity, let's allow connections without a password.
      Therefore add the following environment variable to your database at `deployment.spec.template.spec.containers.env`:

      ```YAML
      - name: POSTGRES_HOST_AUTH_METHOD
        value: trust
      ```

1. Create the deployment object with `kubectl apply -f db.yaml`.
1. Check if your database has started by using `kubectl get pods`.

#### 5.2 Make the Database Accessible

In the previous exercise you saw that there is a pod running the database image, but by default there is no way to access a pod.

You could create a new file for the service you are about to declare, but Kubernetes also supports chaining YAML-files, by using `---` as a separator.
Thereby you can keep all related objects in the same file.

1. Append a line consisting only of `---` to the file `db.yaml` and insert the following content after it:

    ```YAML
    apiVersion: v1
    kind: Service
    metadata:
      name: fortune-cookies-db
    spec:
      selector:
        app: fortune-cookies-db
      ports:
        - name: db
          port: 5432
          targetPort: 5432
      type: ClusterIP
    ```

    ??? info "Fields walkthrough"
        - `spec.selector`: Route service traffic to pods with label keys and values matching this selector.
        - `spec.ports`: Name of the port. Must be unique within the service
            - `name`: Name of the port. Must be unique within the service
            - `port`: Port to be exposed by this service
            - `targetPort`: Port to access the pods targeted by this service

1. Create the service object with `kubectl apply -f db.yaml`

??? info "Why is a service needed?"

    A service is required to make the database accessible to the application:
    <object data="https://pages.github.tools.sap/cloud-curriculum/materials/cloud-platforms/slides/kubernetes/images/service.svg" type="image/svg+xml" width="80%" style="padding: 1rem">
    </object>

#### 5.3 Configure the Database Connection

You will notice that the application still does not start, despite having a database running and exposed via a service.
{% if language == "Java" %}
If you look into the application code you will find out why: no database connection is specified.

1. Go to your `app.yaml` and specify two environment variables for the app container using `Deployment.spec.template.spec.containers[0].env`:
    - `SPRING_DATASOURCE_URL` should have the value `jdbc:postgresql://fortune-cookies-db/postgres`.
    - `SPRING_DATASOURCE_USERNAME` should have the value `postgres`.

{% elif language == "Node.js" %}

1. In file `lib/config.js` we need to read a environment variable e.g. `PG_CONNECTION_STRING` to get the `connectionString` for the database when running in a container. (You can choose any other name, but please make sure to be consistent.)

    !!! tip "Access environment variables through `process.env`"

        ```javascript
          const { PG_CONNECTION_STRING } = process.env;
        ```

    Make sure to also use a default value for the `connectionString` in case `PG_CONNECTION_STRING` is not set, e.g. when running **locally**.

    ```javascript
      const {
        PORT: port = 3000,
        PG_CONNECTION_STRING: connectionString = 'postgresql://postgres:postgres@localhost:5432/postgres'
      } = process.env

      export default {
        app: {
          port
        },
        postgres: {
          connectionString
        }
      }
    ```

1. Go to your `app.yaml` and specify that the container should have an environment variable using `Deployment.spec.template.spec.containers[0].env`.
    - It should have the same name, e.g. `PG_CONNECTION_STRING`
    - It should have the cluster-specific value `postgres://postgres@fortune-cookies-db/postgres` (~ as defined in [Create a Deployment](#51-create-a-deployment))

{% endif %}

    !!! tip "Use `kubectl explain` for information on specific fields"

1. Build and push the new, changed image to the registry.
1. Apply the `app.yaml` with `kubectl apply -f app.yaml` to update your Deployment with the recent changes.

### 6 - Make the Controller Do Some Work

In the YAML file you described your _desired state_ in the form of a _Deployment_.
The Deployment Controller changes the actual state towards the desired state.
For example it creates a pod running the specified image, if there is none.

1. Delete a pod using the `kubectl delete pod` command.
    Use the `--help` option for usage information.
2. Run `kubectl get pods` to see how the deployment controller created a new pod in order to adhere to the desired state.

### 7 - Make the Application Accessible

In the previous exercise you saw that there is a pod running the application image, but by default there is no way to access a pod.

#### 7.1 Declare a Service

1. Append `---` to `app.yaml` and specify a `Service` like you did for the database already in exercise 5.2 but with the following properties:
    - It should be of the type `ClusterIP`.
    - It should bear the name `fortune-cookies`.
    - It should expose port `{{ application_port }}` and target port `{{ application_port }}` of pods which match to the selector: `app: fortune-cookies`.

#### 7.2 Apply the File

Run the following command:

```shell
kubectl apply -f app.yaml
```

It should print, that a service has been created.
Run `kubectl get services` to see the newly created service.

### 8 - Make it Accessible From the Web

Even though we created a service for the deployment, there is still no external IP to address the pod.
The service was needed to make the application accessible from within the cluster.
By declaring an _Ingress_ you can expose applications to the web.

<!-- What is Ingress: https://kubernetes.io/docs/concepts/services-networking/ingress/#what-is-ingress -->

#### 8.1 Create an Ingress

1. Add another `---` separator to the `app.yaml` file and insert the following content after it:

    ```YAML
    apiVersion: networking.k8s.io/v1
    kind: Ingress
    metadata:
      annotations:
        cert.gardener.cloud/purpose: managed
        nginx.ingress.kubernetes.io/proxy-body-size: 150m
      name: fortune-cookies
    spec:
      rules:
      - host: app.ingress.<CLUSTER>.<PROJECT>.shoot.canary.k8s-hana.ondemand.com
        http:
          paths:
          - backend:
              service:
                name: fortune-cookies
                port:
                  number: {{ application_port }}
            path: /
            pathType: Exact
      tls:
      - hosts:
        - app.ingress.<CLUSTER>.<PROJECT>.shoot.canary.k8s-hana.ondemand.com
        secretName: app-certs
    ```

    ??? info "Fields walkthrough"
        - `metadata.annotations`: An unstructured key-value map that may be set by external tools to store and retrieve arbitrary metadata
            - `cert.gardener.cloud/purpose`: By setting this value to `managed`, the cert-controller-manager will automatically request a certificate for all domains given by the hosts in the tls section of the Ingress spec
            - `nginx.ingress.kubernetes.io/proxy-body-size`: Maximum allowed size of the client request body
        - `spec.rules`: List of rules used to configure the Ingress
            - `host`: Fully qualified domain name of a network host
            - `http.paths`: A collection of paths that map requests to backends
                - `backend`: Definition of the referenced service endpoint to which the traffic will be forwarded to
                    - `service`:  References a Service as a backend
                      - `name`:  Name of the referenced service
                      - `port`:  Port of the referenced service
                        - `number`: The numerical port number on the service
                - `path`: Path to match against the path of an incoming request
                - `pathType`: Determines how the path should be matched.
                    One of: `Exact`, `Prefix`, `ImplementationSpecific`.
        - `spec.tls`: TLS configuration
            - `hosts`: List of hosts included in the TLS certificate. Values must match the names used in the tlsSecret
            - `secretName`: Name of the secret used to terminate SSL traffic on 443

1. If the hostname exceeds the [64 character limit](https://gardener.cloud/documentation/guides/administer_shoots/x509_certificates/#Character-Restrictions), add another TLS host with a shorter name before the other one, e.g. `a.ingress.<CLUSTER>.<PROJECT>.shoot.canary.k8s-hana.ondemand.com`.

1. Replace all occurrences of `<CLUSTER>` with the name of your cluster and all occurrences of `<PROJECT>` with the name of your project.

??? info "Why is an ingress needed?"

    An ingress lets us manage external access to the services in a cluster:
    <object data="https://pages.github.tools.sap/cloud-curriculum/materials/cloud-platforms/slides/kubernetes/images/ingress.svg" type="image/svg+xml" width="80%" style="padding: 1rem">
    </object>

#### 8.2 Apply the File

1. Run the following command:

    ```shell
    kubectl apply -f app.yaml
    ```

    It should print, that an ingress has been created.

1. Run `kubectl get ingresses` to see the newly created ingress.

The application is now available at the URLs shown inside the `HOSTS` column.

#### 8.3 Access the Application

1. Access the deployed application on your web browser and find your fortune. 🥠

!!! danger "Certificate Warning: ERR_CERT_AUTHORITY_INVALID"

    If you encounter a warning regarding the certificate while accessing the application via browser, you might have exceeded the character limit on the hostname.
    In this case, follow the instructions on exercise 8.1.2 to shorten the hostname. After that you should be able to access the application without warnings.

### 9 - Persistent Volumes

Kubernetes usually assumes applications to be stateless.
If a pod needs to be recreated, no effort is made to conserve any of its internal state.

#### 9.1 Why care?

1. Run `kubectl get pods` and find the name of the pod currently running the database.
1. To simulate a pod crashing, delete the pod using `kubectl delete pod`.
1. Navigate to `app.ingress.<CLUSTER>.<PROJECT>.shoot.canary.k8s-hana.ondemand.com` (replace `<CLUSTER>` and `<PROJECT>`) or refresh the page to see that the application is unable to serve quotes.

<!-- TODO: instruct to just refresh the page, as the participants probably won't close the page immediately -->

The container's filesystem was deleted along with the container itself.
If you want your data to survive, you need to configure accordingly.

#### 9.2 Specify a Volume

To have your data outlive any pod you can use persistent volumes.

1. Claim a persistent volume by appending the following to the `db.yaml`.
    Make sure that is separated by `---` from any preceding or following content:

    ```YAML
    apiVersion: v1
    kind: PersistentVolumeClaim
    metadata:
      name: database
    spec:
      accessModes:
        - ReadWriteOnce
      resources:
        requests:
          storage: 2Gi
    ```

    ??? info "Field walkthrough"
        - `spec`: Spec defines the desired characteristics of a volume requested by a pod author.
            - `accessModes`: AccessModes contains the desired access modes the volume should have.
              See [here](https://kubernetes.io/docs/concepts/storage/persistent-volumes/#access-modes) for possible values.
            - `resources.requests.storage`: Request a certain amount of storage for the volume.

2. Use the claimed volume on the database container.
    Add the following to the deployment at the same indentation level as `containers`:

    ```YAML
    volumes:
      - name: storage
        persistentVolumeClaim:
          claimName: database
    ```

    ??? info "Field walkthrough"
        - `volumes`: List of volumes that can be mounted by containers belonging to the pod.
            - `name`: Volume's name. Must be unique within the pod.
            - `persistenceVolumeClaim.claimName`: A reference to a PersistentVolumeClaim in the same namespace.

1. Add the following to the `containers` block at the same indentation level as `name`, `image` and `ports`:

    ```YAML
    volumeMounts:
    - name: storage
      mountPath: /var/lib/postgresql/data
    ```

    ??? info "Field walkthrough"
        - `volumeMounts`: Pod volumes to mount into the container's filesystem.
            - `mountPath`: Path within the container at which the volume should be mounted.
            - `name`: Must match the name of a volume

1. We need to tell our PostgreSQL-DB where we want our data to be stored.
In PostgreSQL, this place/directory is referred as `PGDATA`.
Add the following environment variable:

```YAML
- name: PGDATA
  value: /var/lib/postgresql/data/pgdata
```

1. Run `kubectl apply -f db.yaml` to apply the changes.

#### 9.4 Observe the Persistence

The database migrations are only executed at application start.

<!-- TODO: instruct to just refresh the page, as the participants probably won't close the page immediately -->
1. Delete the pod running the application to trigger the creation of a new pod.
1. Navigate to `app.ingress.<CLUSTER>.<PROJECT>.shoot.canary.k8s-hana.ondemand.com` (replace `<CLUSTER>` and `<PROJECT>`) or refresh the page to see that the quotes are back in the database.
1. Delete the pod currently running the database using `kubectl delete pod`.
1. Refresh the page to see that the application can still serve quotes from a database, despite the deletion.

## 🏁 Summary

Good job!
In the prior exercises you pushed your application as a docker container to a registry, deployed it on a Kubernetes cluster along with a database, which you made resilient by using a persistent volume.

## 🦄 Stretch Goals

You should already have a good idea of all common parts by now, you could stop here... oooor you can finish what you started:

- Commands like `kubectl get pods` give us information about all the pods in the cluster, even when we likely only care about a specific subset.
  Use a [namespace](https://kubernetes.io/docs/concepts/overview/working-with-objects/namespaces/) to separate all registry-related objects from the default namespace.
- Use a secret to protect the database with a password.
  Use the environment variable `POSTGRES_PASSWORD` on the postgres container and `SPRING_DATASOURCE_PASSWORD` on the application container.
  Remove the `POSTGRES_HOST_AUTH_METHOD` variable on the database container.

## 📚 Recommended Reading

- [Kubernetes Basics](https://kubernetes.io/docs/tutorials/kubernetes-basics/)
- [Kubernetes Secrets](https://kubernetes.io/docs/concepts/configuration/secret/)
- Kubernetes Volumes & Volume Types:
    - [Official Guide](https://kubernetes.io/docs/concepts/storage/volumes/)
    - [K8s Volumes explained (YT)](https://www.youtube.com/watch?v=0swOh5C3OVM)
- [kubectl CLI Usage examples](https://github.tools.sap/cloud-curriculum/materials/blob/main/cloud-platforms/kubernetes/kubectl-cli.md)

## 🔗 Related Topics

- [SAP Kernel Services SampleApp & Tutorial](https://pages.github.tools.sap/KernelServices/adoption-guide/k8s-sample-app): has information and tutorials on the SAP Kernel Services for K8s
- [Docker & K8s Training](https://github.wdf.sap.corp/slvi/docker-k8s-training)
- [Docker - Getting Started Walk-through for Developers](https://training.play-with-docker.com/#dev)
- [Dockerfile reference](https://docs.docker.com/engine/reference/builder/)
{% if language == "Java" %}
- [Spring Boot with Docker](https://spring.io/guides/gs/spring-boot-docker/)
{% elif language == "Node.js" %}
- [Dockerizing a Node.js web app](https://nodejs.org/en/docs/guides/nodejs-docker-webapp/)
{% endif %}
