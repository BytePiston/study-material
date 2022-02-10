# JavaScript Async

_Disclaimer: We are [counting page hits](https://github.wdf.sap.corp/cloud-native-dev/usage-tracker) using a cookie to distinguish returning & new visitors._
<img src="https://cloud-native-dev-usage-tracker.cfapps.sap.hana.ondemand.com/pagehit/cc-materials/js-async-intro/1x1.png" alt="" height="1" width="1">

## 🗺️ Introduction

>Javascript is a single threaded language. This means it has one call stack and one memory heap. As expected, it executes code in order and must finish executing a piece code before moving onto the next. It's synchronous, but at times that can be harmful. For example, if a function takes awhile to execute or has to wait on something, it freezes everything up in the meanwhile. (source: [dev.to](https://dev.to/steelvoltage/if-javascript-is-single-threaded-how-is-it-asynchronous-56gd))

What is the use of asynchrony in a single threaded runtime?

>I/O operations like file I/O, network I/O, database I/O, (which is generally just network I/O), and web service calls benefit from asynchrony. Anytime that your code is making a database call, or a web service call or a network call or talking to the file system, that can be done asynchronously while that operation is in progress. (source: [Wikipedia](https://en.wikipedia.org/wiki/Asynchrony_(computer_programming)))

## 🚀 Get Started!

In this module, you will learn about the basics of asynchronous programming in JavaScript:

1. Make yourself familiar with the concepts:
    - [Slides](../slides) ([with speaker notes](../slides/?showNotes=true))

1. Get your hands dirty: do the exercise in [Node.js](../nodejs)

## 🎥 Recordings (For self learners) coming soon
