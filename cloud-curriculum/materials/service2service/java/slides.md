# Service 2 Service Communication
**Java**

<!-- 
Agenda:
- Spring REST Client
- RestTemplate
    - GET with RestTemplate I
    - GET with RestTemplate II
    - POST with RestTemplate I
    - POST with RestTemplate II
- RestTemplate Bean
- RestTemplateBuilder
- Catch the HttpStatusCodeException
- Client-Side Testing
-->

---

### Spring REST Client

`org.springframework.web.client.RestTemplate`

- synchronous HTTP client

Notes:
- RestTemplate is a synchronous client to perform HTTP requests
    - exposes a simple, template method API
    - thread will be blocked until client receives a response

- reactive alternative [WebClient](https://www.baeldung.com/spring-5-webclient)

- WebClient is a (modern) alternative that supports a non-blocking and asynchronous approach
    - For the upcoming exercise we'll stick to the RestTemplate for now

---

### RestTemplate

|   HTTP    | RestTemplate methods |
| --------- | -------------------- |
| DELETE    | delete(String, String...) |
| GET       | getForObject(String, Class, String...) |
| HEAD      | headForHeaders(String, String...) |
| OPTIONS   | optionsForAllow(String, String...) |
| POST      | postForLocation(String, Object, String...) |
| PUT       | put(String, Object, String...) |


- method names: *method***For***WhatIsReturned*

Notes:
- names of the methods correspond to the HTTP method they invoke
- second part of the name indicates what is returned
    - getForObject: perform get, convert the response into object and return it
    - postForLocation: perform post, convert given object to HTTP request and return HTTP Location header
    - postForObject: preform post, convert the response into object and return it
- The methods try to enforce REST best practices 👍

- https://spring.io/blog/2009/03/27/rest-in-spring-3-resttemplate

---

### GET with RestTemplate I

```java
String result = restTemplate.getForObject("http://example.com/hotels/{hotel}/bookings/{booking}", String.class, "42", "21");
```
- URI variables as varargs
- will perform GET on http://example.com/hotels/42/bookings/21

Notes:
- each of the methods take a URI as first argument
- the URI can be a template so that variables can be injected
    - as varargs...
    - as `Map<String, String>`

------

### GET with RestTemplate II

```java
Map<String, String> vars = new HashMap<String, String>();
vars.put("hotel", "42");
vars.put("booking", "21");
String result = restTemplate.getForObject("http://example.com/hotels/{hotel}/bookings/{booking}", String.class, vars);
```

---

### POST with RestTemplate I

```java
HotelDTO hotelDto = new HotelDTO();
hotelDto.setName("Awesome Resort");
Hotel result = restTemplate.postForObject("http://example.com/hotels", hotelDto, Hotel.class)
```

- will create a new Hotel resource and return the representation object

------

### POST with RestTemplate II

```java
 ResponseEntity<String> entity = template.getForEntity("https://example.com", String.class);
 String body = entity.getBody();
 MediaType contentType = entity.getHeaders().getContentType();
 HttpStatus statusCode = entity.getStatusCode();
```

- retrieve the HTTP ResponseEntity containing **headers** and **body**

---

### RestTemplate Bean

Spring does not provide a RestTemplate bean

```java
@Bean
public RestTemplate restTemplate() {
    return new RestTemplate();
}
```

- register a RestTemplate bean in @Configuration class
- inject it in your client class

Notes:
- alternatively use RestTemplateBuilder

------

### RestTemplateBuilder

```java
private RestTemplate restTemplate;

@Autowired
public HelloController(RestTemplateBuilder builder) {
    this.restTemplate = builder.build();
}
```
- scope limited to this class

Notes:
- this RestTemplate bean has its scope limited to the class in which it is built.
- For customizing the RestTemplate in application wide scope RestTemplateCustomizer can be used
    - functional interface: to implement request interceptors (logging), use proxy routes, add authentication etc.

---

### Catch the HttpStatusCodeException

```java
try {
    result = restTemplate.postForObject(AWESOME_SERVICE_PATH, awesomeRequest, String.class);
} catch (HttpClientErrorException.BadRequest ex) {
    // handle Bad Request
} catch (HttpServerErrorException.InternalServerError ex) {
    // handle Internal Server Error
}
```

Notes:
- static access to HTTP Status Codes
    - HttpClientErrorException for client side errors
    - HttpServerErrorException for server side errors

---

### Client-Side Testing
- mock targeted services behavior

```java
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private HotelServiceClient hotelServiceClient;

    @Test
    void whenServiceThrowsXXXError_thenXYZ() {
        Hotel expectedObject = new Hotel();

        Mockito.when(restTemplate.getForObject()).thenReturn(expectedObject);
        Mockito.when(restTemplate.getForObject()).thenThrow(HTTP_ERROR_EXCEPTION);

        //assert hotelsServiceClient behaves as expected
    }
```

Notes:
- simulate the HotelService responses by mocking the RestTemplate
    - successful request, HTTP 2xx, "happy path"
    - throw HttpClientErrorException or HttpServerErrorException
- class under test HotelServiceClient
    - the client class is a communication gateway, hides HTTP functionality
    - it also catches and handles potential HTTP-errors
- Which test approach is used here?

---

# Questions?