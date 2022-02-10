# Distributed Logging with Node.js

---

![cf-nodejs-logging-support](https://nodei.co/npm/cf-nodejs-logging-support.png)

- collection of support libraries for node.js applications
- provides means to
  - emit structured application log messages
  - collect request metrics
- Maintained by SAP

Notes:

- tuned for the logging service available on Cloud Foundry, but also useful for other environments

---

## Minimal Example

```javascript
import log from 'cf-nodejs-logging-support'

// Formatted log message
log.info("The answer is %d", 42)
```

Notes:

- By default the logs are emitted in a JSON format

---

## Middleware

- logs all incoming requests

```javascript
// Bind to express app
app.use(log.logNetwork)
```

---

## Logging Contexts

```javascript
app.get('/', function (req, res) {
  // Context bound custom message
  req.logger.info("Hello World will be sent")

  res.send('Hello World')
})
```

- Two types: **global** and **request** contexts
- middleware adds context bound loggers to request objects
- their logs have some additional fields, such as:
  - `request_id`
  - `correlation_id`

---

## Correlation ID

- read from header "`X-CorrelationID`" if set
- generated otherwise (UUIDv4)

```javascript
// Get correlation_id from logger bound to request
var id = req.logger.getCorrelationId()
```

Notes:

- Use the shown method to acquire the correlation ID in order to set in on outgoing requests.

---

# Questions?
