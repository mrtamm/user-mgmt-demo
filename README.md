![Build](https://github.com/mrtamm/user-mgmt-demo/actions/workflows/build.yml/badge.svg)

User Management Demo
====================

Features
--------

* Simple user management as a Spring Boot app (Java 21) with REST API (`/api/v1/users`) and bundled
  Angular (v21) frontend
* REST API documentation exposed at http://localhost:8080/swagger.html
* Data persistence by in-memory H2 database, which is managed by Flyway
* Functionality tests for
  * [UserController](src/test/java/com/github/mrtamm/demo/UserControllerTests.java),
  * [UserService](src/test/java/com/github/mrtamm/demo/UserServiceTests.java),
  * [full integration tests](src/test/java/com/github/mrtamm/demo/DemoApplicationTests.java)
* Frontend tests for
  * form validation: [users-form.spec.ts](frontend/src/app/components/users-form/users-form.spec.ts)
  * store and reducer: [users-store.spec.ts](frontend/src/app/stores/users-store.spec.ts)


Developer Tools
---------------

The following tools are needed at minimum for working with the project:

* **Java SDK** (e.g. OpenJDK 21) - compiling source code
* **Gradle** – building and running the Java source code
* **node.js and npm** – building and running the frontend


Building And Running
--------------------

### Backend

* run locally: `gradle bootRun`
  * User interface at http://localhost:8080/
  * API description at http://localhost:8080/swagger-ui/index.html
* run tests and build the JAR artifact: `gradle build` (produces: `./build/libs/user-mgmt-demo.jar`)

### Frontend

* the source code is included at [./frontend/](./frontend)
* run locally `npm start` and open on browser at http://localhost:4200
* to update the bundled Angular app in the Spring Boot app: `npm run build`


Project Structure
-----------------

### Backend resources

* build script and dependencies: [build.gradle](build.gradle)
* source code:
  [src/main/java/com/github/mrtamm/demo](src/main/java/com/github/mrtamm/demo)
* database scripts (1 table):
  [src/main/resources/db/migration](src/main/resources/db/migration)
* application default config file:
  [src/main/resources/application.yaml](src/main/resources/application.yaml)

### Frontend resources

* build script and dependencies: [frontend/package.json](frontend/package.json)
* source code: [frontend/src](frontend/src)
* target directory for build: [src/main/resources/static](src/main/resources/static)
