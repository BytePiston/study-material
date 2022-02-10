# Node.js Persistence

---

## DB Client

- [node-postgres](https://www.npmjs.com/package/pg): non-blocking PostgreSQL client

- Allows us to connect to a postgres Database
- Supports Connection pooling, simple and parameterized queries

Notes:

- pg provides some more features (bulk-actions, named statements etc.), but considering our requirements we will stick with opening connections and executing queries

---

## Connecting to the DB

```javascript
import pg from 'pg'

const { Pool } = pg

// pools will use environment variables
// for connection information
const pool = new Pool()

const res = await pool.query('SELECT NOW()')

await pool.end()

```

Notes:

- A pool keeps several connections which are reused
- Connecting to a PostgreSQL server requires a handshake, which can take 20-30 milliseconds:
  - Not something we want to do every time the application executes a query
- Allows multiple queries to be performed simultaneously

---

## Queries

- `query` method
  - accepts a DB statement as first (and optionally a values array as second) argument
  - returns a `Promise` which resolves to a result object

```javascript
const result = await pool.query('SELECT * FROM books')
console.log(result.rows)
// [{ title: 'Clean Code', author: 'Robert C. Martin' },
// { title: 'Refactoring', author: 'Martin Fowler' }]
```

---

## Query Parameters

- Don't use string concatenation/templates
- RISK OF SQL-INJECTION VULNERABILITY

```javascript
const values = ['Clean Code', 'Robert C. Martin']

const res = await client.query('INSERT INTO books(title, author) VALUES($1, $2) RETURNING *', values)
console.log(res.rows[0])
// { title: 'Clean Code', author: 'Robert C. Martin' }
```

Notes:

- instead use $-sign placeholders and provide values in an array as the second parameter

---

## Persistence Layer Structure

- Create a file and make all interactions with the db go through this file
  - easily handle possible changes to the pg-API
  - single place to put logging and diagnostics regarding database access
  - single place to bootstrap and configure the database
  - no direct coupling to the pg driver

Notes:

- <https://node-postgres.com/guides/project-structure>

---

### Example

- create a file `db.js`

```javascript
import pg from 'pg'

const { Pool } = pg

export default (port) => {
  const pool = new Pool({
      database: 'postgres',
      user: 'postgres',
      password: 'pw',
      port: port || 5432
  })

  this.query = (...args) => pool.query(...args)
}
```

Notes:

- this file will be the only one requiring the `pg`-module
- everywhere else, where you want to query the DB you can require this `db`-module and call the exposed `query`-method

---

# Questions?
