# Authentication & Authorization

_Disclaimer: We are [counting page hits](https://github.wdf.sap.corp/cloud-native-dev/usage-tracker) using a cookie to distinguish returning & new visitors._
<img src="https://cloud-native-dev-usage-tracker.cfapps.sap.hana.ondemand.com/pagehit/cc-materials/auth-java/1x1.png" alt="" height="1" width="1">

## 👷‍♂️👷‍♀️ Audience

Developers with basic knowledge of Java and Spring Web, but little to no experience in how to implement authentication via OpenID Connect and Spring Security.

## 🎯 Learning Objectives

In this exercise you will learn

- how to log in through OpenID Connect
- how to protect a private API

<!-- Prerequisites-->
{% with
  required=[
    ('[Spring](https://spring.io/)')
  ],
  beneficial=[]
%}
{% include 'snippets/prerequisites/java.md' %}
{% endwith %}

## 🛫 Getting Started

{% with branch_name="security", folder_name="security-java" %}
{% include 'snippets/clone-import/java.md' %}
{% endwith %}

## 🔍 Code Introduction

Since we want to focus on learning how to authenticate and authorize users in this exercise, we will provide some code in the starting point:

- the `com.sap.cc.hello.auth` package contains the class `HelloAuthController` which is a simple REST Controller with two endpoints
    - `/welcome` shows a welcome message and the users authorities (if authenticated)
    - `/restricted` represents a restricted resource which we will add authorization checks to in this exercise. It returns content similar to the welcome page.
- the `com.sap.cc.hello.auth.sapias` package contains the class `IasOAuth2UserService` which helps Spring Security understand the responses coming from the SAP IAS Identity Provider. This class may be hard to understand if you are not familiar with how Spring Security works - but this is fine, you won't need to understand this class to do the exercise.
- the `pom.xml` contains the `spring-boot-starter-oauth2-client` dependency which itself depends on Spring Security. This will automatically secure the application. By default it will require all requests to be authenticated. It will output a random password to the console during application startup (the username is "user"). You could start up the application now to see this behavior if you like (optional).

## 📗 Exercises

### 1 - Configure Spring Security
As mentioned in the code introduction, the application is currently secured by default due to the fact that Spring Security is present as a dependency.
Since we want to Authenticate and Authorize our Users using an IDP (Identity Provider) we need to do some configuration.

#### 1.1 Add Configuration for OAuth/OpenIdConnect Provider
We need to provide spring security with some configuration data so it knows how to talk to the OIDC/OAuth2 server

1. Create a file named `OAuthClientConfiguration.java` with the following content in your project, Spring Security will detect and use it:
    
    ```java
    package com.sap.cc.hello.auth;

    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.oauth2.client.registration.ClientRegistration;
    import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
    import org.springframework.security.oauth2.client.registration.ClientRegistrations;
    import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
    import org.springframework.security.oauth2.core.AuthorizationGrantType;

    @Configuration
    public class OAuthClientConfiguration {

        @Bean
        public ClientRegistrationRepository getClientRegistrationRepository() {
            ClientRegistration oidcClientRegistration = ClientRegistrations
                    .fromIssuerLocation("https://aghtrtxzy.accounts400.ondemand.com")
                    .registrationId("sapias")
                    .clientName("SAPIAS")
                    .clientId("2a30e426-b564-4313-a4a3-e3e4d6c88050")
                    .clientSecret("g/CmJZ?8gTD-7PSuG/lYs_8Uq:jT=Jy")
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .scope("openid")
                    .build();

            return new InMemoryClientRegistrationRepository(oidcClientRegistration);
        }

    }
    ```

??? info "Code Walkthrough"
    - `ClientRegistrationRepository`: Contains the configuration for OAuth Clients, in our case we are creating a `InMemoryClientRegistrationRepository`
    - `fromIssuerLocation`: Creates a ClientRegistrationBuilder which configures itself based on the meta-information-endpoint of the given URL
        The required URIs can be found on a `.well-known/openid-configuration` path.
            E.g. for our SAP IAS: [https://aghtrtxzy.accounts400.ondemand.com/.well-known/openid-configuration](https://aghtrtxzy.accounts400.ondemand.com/.well-known/openid-configuration).
    - `registrationId`: The id used by Spring internally to refer to this client registration
    - `clientName`: The internal name of the OAuth client
    - `clientId` and `clientSecret`: The OAuth clients credentials
    - `authorizationGrantType`: There are different types of OAuth Authorization grants, we are telling spring security to use the type "authorization_code"
    - `scope`: The OAuth scopes we are requesting, note that these are not scopes in the sense of roles, but [scopes as defined by OAuth](https://oauth.net/2/scope/). In our case requesting the "openid" scope is sufficient.


#### 1.2 Start up the application and log in

1. Start the application

1. Navigate to [localhost:8080/welcome](http://localhost:8080/welcome). You will automatically be redirected to the login server (SAP IAS)

1. Use the following credentials to log in:
    - username: `regular`
    - password: `Pa$$word`

1. After the login you should be redirected to localhost:8080/welcome again and see the "Welcome Page" showing "Hello Robin Regular!"

1. Log out. After the logout you should be redirected to "localhost:8080/login?logout" which is the default logout page

??? info "We barely did any configuration, how does Spring Security know what to do?"
    As with everything in the Spring ecosystem the rule is "Convention over configuration": Spring Security has a set of defaults it will use unless you tell it to do otherwise by providing your customization. By default spring security will require authentication (but not authorization) for all incoming requests no matter to what endpoint.


### 2 - Customize the Spring Security configuration
The logout flow currently redirects to "localhost:8080/login?logout" and keeps the session between our server and the SAP IAS open even after logout. Due to this the users will not be asked for their credentials, but automatically be logged in by SAP IAS with the credentials they entered previously. We want to change this behavior so the users can log in with a different user. To change behavior we need to provide configuration/customization to spring security.



1. Paste the following code into a new class in the package `com.sap.cc.hello.auth` called `WebSecurityConfig.java`:

    ```java
    package com.sap.cc.hello.auth;

    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
    import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
    import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

    @EnableWebSecurity
    public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

        private ClientRegistrationRepository clientRegistrationRepository;

        public WebSecurityConfig(ClientRegistrationRepository clientRegistrationRepository) {
            this.clientRegistrationRepository = clientRegistrationRepository;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.oauth2Login().and().oauth2Client();
            configureLogoutBehavior(http);
            configureAuth(http);

        }

        private void configureAuth(HttpSecurity http) throws Exception {
            http.authorizeRequests()
            .anyRequest().authenticated();
        }

        private void configureLogoutBehavior(HttpSecurity http) throws Exception {
            OidcClientInitiatedLogoutSuccessHandler logoutSuccessHandler = new OidcClientInitiatedLogoutSuccessHandler(
                    clientRegistrationRepository);
            logoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/welcome");
            http.logout().logoutSuccessHandler(logoutSuccessHandler);
        }

    }
    ```


    ??? info "Code Walkthrough"
        - `WebSecurityConfigurerAdapter`: Spring will look for classes extending `WebSecurityConfigurerAdapter` that you provide and use them instead of its defaults
        - `ClientRegistrationRepository`: This will find the ClientRegistrationRepository that we provided in step 1
        - `#!java configure(HttpSecurity http)`: The configure method gives us the opportunity to provide custom configuration
            - `http.oauth2Login()` and `http.oauth2Client()`: Tell spring security that we want to use OAuth2 to log in our customers and that we want to use a configured OAuthClient to do so. We could provide further customization to those methods if we wanted, but the defaults are fine for now.
            - `configureLogoutBehavior(http)`: delegates to the method below to customize the logout behavior
            - `configureAuth(http)`: delegates to the method below to define authentication and authorization
        - `configureAuth(HttpSecurity http)`: We are telling spring security that any incoming request needs to be authenticated (we want the user to prove his identity). At this point we are not yet specifying any restrictions w.r.t authorization of specific endpoints
        - `configureLogoutBehavior(HttpSecurity http)`: We are customizing the logout flow so that the user is redirected to "localhost:8080/welcome" after logout. In addition we are changing the logout behavior by telling Spring Security to use the `OidcClientInitiatedLogoutSuccessHandler` instead of its default one. Due to this, the session between our server and the OAuth Server will be closed upon logout: This has the effect that the user will need to re-enter his credentials again during login, which is what we want to achieve.


1. (Re-)start the application

1. Navigate to [localhost:8080/welcome](http://localhost:8080/welcome). You will automatically be redirected to the login server (SAP IAS)

1. Use the following credentials to log in:
    - username: `regular`
    - password: `Pa$$word`

1. After the login you should be redirected to localhost:8080/welcome again and see the "Welcome Page" showing "Hello Robin Regular!"

1. Log out. After the logout you should be redirected to "localhost:8080/welcome"; Due to the fact that every request must be authenticated according to the config above, you will immediately be redirected to the SAP IAS login page again.

### 3 - Make Welcome Page (/welcome) accessible without authentication
Currently any incoming request will require authentication, but we want the welcome page to be accessible to unauthenticated users too

1. Add additional configuration to the chain in the `configureAuth` method to make "/welcome" accessible without authentication

    ??? example "Need help?"
        ```java
        private void configureAuth(HttpSecurity http) throws Exception {
            http.authorizeRequests()
            .antMatchers("/myPath").permitAll()
            .anyRequest().authenticated();
        }
        ```

    !!! info "Order matters!"
        The ordering of the matchers matters, since they will be executed in the order they are added and only the first match counts. This means that more specific matchers (e.g. "/api/protected/resource") must be defined before matchers with a broader scope (e.g. "/api/*") otherwise the more specific matcher will never match since the broader matcher matches before it. `#!java anyRequest()` must always be the last matcher in the chain and does not allow any matchers to be defined after it.
        
1. Restart the application

1. Navigate to [localhost:8080/welcome](http://localhost:8080/welcome), you should not have to log in.

1. You should see the "Welcome Page" showing "Hello unauthenticated user!"

1. Navigate to [localhost:8080/restricted](http://localhost:8080/restricted) (you can use the link on the welcome page). Since all requests, except to /welcome, must be authenticated you should be prompted to log in, just as before.

1. Log out (if you logged in)

### 4 - Secure the "/restricted" page
Currently the `restricted` page is not restricted by any authorization checks, let us change that.

1. Add additional configuration to the chain in the `configureAuth` method to protect the "/restricted" endpoint. Only users which have the role `ADMIN` should be allowed to access the "/restricted" page.

    ??? example "Need help?"
        ```java
        private void configureAuth(HttpSecurity http) throws Exception {
            http.authorizeRequests()
            .antMatchers("/myPath").permitAll()
            .antMatchers("/myRestrictedPath").hasRole("theRole")
            .anyRequest().authenticated();
	    }
        ```

1. Restart the application

1. Navigate to [localhost:8080/restricted](http://localhost:8080/restricted), you should be prompted to log in, just as before.

1. Use the following credentials to log in:
    - username: `regular`
    - password: `Pa$$word`

1. Since the `regular` user does not have the role `ADMIN` you should get a 403 (Forbidden) response.

1. Navigate to [localhost:8080/welcome](http://localhost:8080/welcome), there you can see that the user only has the authority `ROLE_USER` 

1. Log out

1. Navigate to [localhost:8080/restricted](http://localhost:8080/restricted), you should be prompted to log in again

1. Use the following credentials to log in with the `privileged` user which has the role `ADMIN`:
    - username: `privileged`
    - password: `Pa$$word`

1. You should see the restricted page with the text "Hello Paula Privileged!
Your authorizations: [ROLE_USER, ROLE_ADMIN]"




## 🏁 Summary
Good job!
In this exercise you used OpenID Connect to delegate authentication and read the access token to authorize an endpoint.

<!--
## 🦄 Stretch Goals
You should already have a good idea of all common parts by now, you could stop here... oooor you can finish what you started:
-->

## 📚 Recommended Reading
- [Spring Security Docs](https://docs.spring.io/spring-security/site/docs/current/reference/html5/)
- [Spring Security Architecture](https://spring.io/guides/topicals/spring-security-architecture)
- [OAuth and OpenID Connect in Plain English (VIDEO)](https://www.youtube.com/watch?v=sSy5-3IkXHE)
- [Effective Oauth2 with Spring Security and Spring Boot - Pluralsight](https://app.pluralsight.com/library/courses/oauth2-spring-security-spring-boot/table-of-contents)

## 🔗 Related Topics

- [SAP Technology Guidelines: Identity Authentication and Single Sign-On (SSO)](https://github.tools.sap/CentralEngineering/TechnologyGuidelines/tree/latest/tg02)
- [SAP IAS Docs](https://help.sap.com/viewer/6d6d63354d1242d185ab4830fc04feb1/LATEST/en-US/d17a116432d24470930ebea41977a888.html)
- [CP Security Knowledge-Base](https://github.wdf.sap.corp/pages/CPSecurity/Knowledge-Base/)
- The [Application Security Engagement and Enablement Team](https://sap.sharepoint.com/sites/124611) offers learning resources and trainings for secure programming and hacking 
