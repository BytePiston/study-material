# SAP HCP Logging with Spring Boot
This example shows how the logging facility of the SAP HCP may be used in a Spring Boot project.
The code of the SAP logging libs are in Github: <https://github.com/SAP/cf-java-logging-support>

## Run the example
From the root folder of the project run: mvn clean package spring-boot-run
Or just push the resulting jar to CF
Test the app by opening a browser window for the URL: <http://localhost:8080/hello?name=someone> 
or the corresponding cloud URL from CF. 

## Monitor your app on kibana
Just open <https://logs.cf.sap.hana.ondemand.com>

## Log Configuration
The example uses Logback. The configuration is kept in the logback-spring.xml file under src/main/resources.
Because the json format is quite verbose, I use a less explicit pattern for the local test. The json format is
used in the cloud case only and thus makes the logging output available to the hcp tools.
By the way: this is the advantages of using logback with Spring Boot, you can use Spring profiles to separate 
configuration alternatives.

## Coding Remarks
- All code is dumped into one single [class](https://github.wdf.sap.corp/d022051/SpringTutorial/blob/master/62_hcp-logging/src/test/java/com/example/HcpLoggingApplicationTests.java)
This is of course not the recommended way ;-)
- See how simple it is to configure a servlet-filter with Spring Boot: Just add it as a bean. 
- I like to use Project Lombok a lot. The @Sl4j annotation is easily added to a class. 
And a configured logger is always ready to use. 