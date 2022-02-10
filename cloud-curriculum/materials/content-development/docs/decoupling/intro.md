# Decoupling & Test Isolation

_Disclaimer: We are [counting page hits](https://github.wdf.sap.corp/cloud-native-dev/usage-tracker) using a cookie to distinguish returning & new visitors._
<img src="https://cloud-native-dev-usage-tracker.cfapps.sap.hana.ondemand.com/pagehit/cc-materials/decoupling-intro/1x1.png" alt="" height="1" width="1">

## 🗺️ Introduction

### What is coupling?

Coupling is a term to describe the relationship between two modules (e.g. classes). If one module A uses another module B, then module A is dependent on module B. Another indicator can be: If changing one module requires changing another module, then these module are obviously coupled.

### What is decoupling?

When talking about decoupling, one does not mean to remove dependencies entirely. On the contrary, breaking down your software into modules and coupling them is desired. The idea is to keep the modules loosely coupled. So what is meant by decoupling is basically: **achieving loosely coupled design**

When two modules are loosely coupled, they are less dependent on each other and are less likely to break when the other module changes.

### Test Isolation

Test isolation refers to the ability of testing a module's core functionality without having to test the functionality of the dependent modules.

## 🚀 Get Started!

In this module, you will learn about decoupling of components and test isolation:

1. Make yourself familiar with the general concepts
    - [Slides](https://pages.github.tools.sap/EngineeringCulture/ase/AllLanguages/decouplingAndTestIsolation-slides/index.html) ([with speaker notes](https://pages.github.tools.sap/EngineeringCulture/ase/AllLanguages/decouplingAndTestIsolation-slides/index.html?showNotes=true))
1. How to isolate tests with Mockito in Java
    - [Slides](https://pages.github.tools.sap/EngineeringCulture/ase/Java/mockito-slides/index.html) ([with speaker notes](https://pages.github.tools.sap/EngineeringCulture/ase/Java/mockito-slides/index.html?showNotes=true))
1. Get your hands dirty: do the exercise in [Java](../java/) or in [Node.js](../nodejs/)

1. For Java developers:

    1. Learn how to leverage the Spring Framework to do Dependency Injection in Java
        - [Slides](https://pages.github.tools.sap/EngineeringCulture/ase/Java/spring-di-slides/index.html) ([with speaker notes](https://pages.github.tools.sap/EngineeringCulture/ase/Java/spring-di-slides/index.html?showNotes=true))
    
    1. Do the [Dependency Injection Framework exercise](../di-frameworks-java)

## 🎥 Recordings (For self learners) coming soon
