# Spring-OData

## Current State of Implementation
The implementation more or less covers the content of the first two tutorial sessions of OLingo V4:
- https://olingo.apache.org/doc/odata4/tutorials/read/tutorial_read.html
- https://olingo.apache.org/doc/odata4/tutorials/readep/tutorial_readep.html

### Features
- metadata document (running example: http://localhost:8080/CustomerMaintenance/$metadata)
- service document (http://localhost:8080/CustomerMaintenance/)
- collection document (http://localhost:8080/CustomerMaintenance/customerSet)
- entity document (http://localhost:8080/CustomerMaintenance/customerSet(1))
- primitive property document (http://localhost:8080/CustomerMaintenance/customerSet(1)/dateOfBirth)

### Pointers to Implementation Details
#### Spring Configuration
The EdmProvider, the Processors, the ServiceMetadata and the global OData object are supposed to be thread-safe (still to be verified).
These objects are provisioned as beans with static scope.
The handler seems to be not thread-safe. Thus a static factory bean creates a handler for each request anew (might be a performance topic).

#### Central OData Controller
There is one Spring MVC controller which answers all requests under the service paths (can be configured as application property).
This controller gets the handler factory injected and dispatches the incomming requests to a new handler returned by the factory.

#### HandlerWrapper
This is a slightly changed copy of the ODataHttpHandlerImpl of OLingo. 
In ODataHttpHandlerImpl the odataServicePath is assumed to be equal to httpRequest.getServletPath(), 
which is not the case in Spring MVC. In our implementation the servicePath is handed over as constructor parameter.

#### EdmProvider
The generic SpringDataEdmProvider collects the relevant information for the Edm model from Spring-Data and Spring-Data-Rest metadata.
Only entities that have exposed repositories are taken into account, and only these are accesible over OData requests.

#### The Processors
Currently there is a collection- a entity- and a primitive-processor. Some common functionality is factored out into a 
common base class. Wherever possible the processing is delgated to an invoker of the Spring-Data repositories
(thanks to Oliver Gierke for pointing me to the relevant classes). In the case of the primitive processor there is no 
comparable repository invoker. Here the JPA criteria builder is used to assemble dynamically the query retrieving the requested field
from the DB.

#### ODataRepositoryExtension
The additional functionality to select (currently it's only select) data from the DB according to the OData queries
is kept in a repository extension, which must be the base for the application repositories (see the CustomerRepository example)

#### Example
There is a Customer entity and a CustomerRepository example under the example package.

#### Shortcommings
- currently only Integer, Long, String and Date are supported as field types
- often missing precondition checks
- only default exception handling
- only two of the nine tutorial chapters are covered - there is lots and lots of functionality missing
- are the processors realy thread-safe?
- how can the handlers be made thread-safe, so that one single instance is re-usable (performance)
- unit tests are planned and will come soon ;-)
- ...



