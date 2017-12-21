# DelegationApp

Delegation App demonstrates a scenario where a home owner lists their property for rent, and delegates view access to another user. It is based on OACC http://oaccframework.org/ and it closely follows the ideas and the implementations behind secure-todo-example http://oaccframework.org/secure-todo-example.html.

There are a few specific differences from the secure-todo application
1. It uses Liquibase scripts to build the initial tables required for OACC and application domain
1. It then programatically initializes OACC database and builds the OACC domain specific resources and permissions
1. The context is a property management domain, rather than a to-do application
1. It has a postman suite to run integration tests
 
How to start the DelegationApp application
---

1. Run `mvn clean install` to build your application
1. Start application with `java -jar target/oacc-delegation-1.0-SNAPSHOT.jar server config.yml`
1. To check that your application is running enter url `http://localhost:8080`

Health Check
---

To see your applications health enter url `http://localhost:8081/healthcheck`
#oacc-delegation
