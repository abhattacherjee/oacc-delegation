# DelegationApp

Delegation App demonstrates the scenarios where a home owner lists their property for rent, and delegates view access to 
another user. It is based on OACC http://oaccframework.org/ and it closely follows the ideas 
and the implementations behind secure-todo-example http://oaccframework.org/secure-todo-example.html.

There are a few specific differences from the secure-todo application
1. It uses Liquibase scripts to build the initial tables required for OACC and application domain
1. It then programatically initializes OACC database and builds the OACC domain specific resources and permissions
1. The context is a property management domain, rather than a to-do application
1. It has a postman suite to run integration tests

The application starts by creating a hsql in-memory database instance for oacc and application domains. It then runs a 
series of SQL scripts using Liquibase to create the initial set of tables and users.

After the database setup, it runs OACC initialization code, and creates a series of domain specific
resource classes, permissions on those resource classes, and new resources owner-role and owner-role-helper.

Operations:

1. `POST /users/`: Create new user. Creates a new user and assigns the user to owner role, with access to create properties.
1. `POST /properties/`: Create a new property. Needs basic authentication in the header to create a property. The user
who creates the property has VIEW, EDIT and DELETE permissions. The user also has the permission 
to grant view access to another user for a specific property
1. `GET /properties/`: Gets all properties which the authenticated user has access to
1. `GET /properties/{id}`: Get the property details corresponding to `id`. Returns `UNAUTHORIZED`
if the authenticated user does not have permissions to view the property
1. `PUT /properties/{id}?share_with={email_address}`: Grant view access to the property with identifier `id`
to another user

The `postman` folder has postman tests to run through some scenarios.

How to start the DelegationApp application
---

1. Run `mvn clean install` to build your application
1. Start application with `java -jar target/oacc-delegation-1.0-SNAPSHOT.jar server oacc-delegation-config.yml`
1. To check that your application is running enter url `http://localhost:8080`

Health Check
---

To see your applications health enter url `http://localhost:8081/healthcheck`
