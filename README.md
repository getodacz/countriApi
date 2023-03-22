# Spring Boot Application with JWT Authentication and Authorization

This is a Spring Boot application that uses JSON Web Tokens (JWT) for authentication and authorization of API endpoints. It includes a SecurityConfiguration class that configures Spring Security to secure the application's endpoints based on the provided rules.

## How to run the application

Execute the following docker command to build and start the application:

```docker
 docker-compose up
```

The application will be available at https://localhost:8443

The "docker-compose up" command will start the following containers:
- An application container
- A Postgres database container

## Bonus Task
The application bonus task has been implemented with a rate limiter that limits the number of requests per second for unauthenticated users and authenticated users. 
The rate limit is configured in the application-prod.yml file.
```
For authenticated users rate limit = 20 requests per sec:
resilience4j.ratelimiter.instances.nonAuthCountryDataRateLimiter.limitForPeriod=5
For non-authenticated users rate limit = 5 requests per sec:
resilience4j.ratelimiter.instances.authCountryDataRateLimiter.limitForPeriod=20
```
## Assumptions

1. The application will accept a string of comma separated countries like below:
GET https://localhost:8443/api/v1/public/countries/IT,CA,US
2. The application accepts an input that is not "perfect" and will try to reformat it to clean it like explained below:
- It accepts white characters, and will remove them before processing the request
  Example of valid input: GET https://localhost:8443/api/v1/public/countries/CA , US, IT
- It accepts lower case characters, and will convert them to upper case before processing the request
  Example of valid input: GET https://localhost:8443/api/v1/public/countries/ca,US,it
- It accepts a string with a single country
  Example of valid input: GET https://localhost:8443/api/v1/public/countries/CA
- It accepts duplicate countries, duplicates will be ignored:
  Example of valid input: GET https://localhost:8443/api/v1/public/countries/CA,US,CA
3. The application will ignore countries that are not in the database, will process only the ones that are in the database
  Example: GET https://localhost:8443/api/v1/public/countries/CA,US,ZZ
  For this example the code will ignore inexistent country ZZ, will just process CA, US
4. The application will return an error if any of the "cleand-up" countries from the list is not exactly 2 characters long (after removing white spaces):
   Example: GET https://localhost:8443/api/v1/public/countries/CAD,US
   For this example the code will return an error due to the 3 letters country CAD

```
  {
    "statusCode": 400,
    "timestamp": "2023-03-22T14:16:02.241+00:00",
    "message": "Your request could not be processed. Country code must be 2 characters long",
    "description": "uri=/api/v1/public/countries/CAAAA"
  }
```
5. The application will return an error if all the countries from the input string are not found (not existing in the database):
   Example: GET https://localhost:8443/api/v1/public/countries/YY,ZZ
```
{
    "statusCode": 400,
    "timestamp": "2023-03-22T14:26:00.743+00:00",
    "message": "Your request could not be processed. The list of countries contains only invalid country codes.",
    "description": "uri=/api/v1/public/countries/YY,ZZ"
}
```
6. For input with countries on multiple continents, the expected output is as shown below:
   Example: GET https://localhost:8443/api/v1/public/countries/CAD,US,IT
```
[
    {
        "countries": ["IT"],
        "name": "Europe",
        "otherCountries": ["AD","AL","AT",... rest of the countries in "Europe" excluding "IT"]
        ]
    },
    {
        "countries": ["CA","US"],
        "name": "North America",
        "otherCountries": ["AG", "AI","AW","BB"... rest of the countries in "North America" excluding "CA","US"]
        ]
    }
]
```
## Endpoints
- Main public endpoint to get the list of continents and countries
  GET https://localhost:8443/api/v1/public/countries/CA,US,IT
```http request
### Countries from 2 continents (public endpoint)
GET https://localhost:8443/api/v1/public/countries/CA,US,RO,IT,MD,AD,AF,CL,CN,AR,BR,MX
Content-Type: application/json
```
- Main private endpoint to get the list of continents and countries
  GET https://localhost:8443/api/v1/private/countries/CA,US,IT
```http request
### Should allow access to private endpoint for authenticated users (get a refreshed token once this one expires)
GET https://localhost:8443/api/v1/private/countries/CA,US,IT
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlbWlseS5qb2huc29uQGV4YW1wbGUuY29tIiwiaWF0IjoxNjc5NDY1NTQ2LCJleHAiOjE2Nzk0NjczNDZ9.7_9M2YPTnbFG9DIUiVlBEYcjofQD7w4PYqiKKLJVti0

{
  "email": "emily.johnson@example.com",
  "password": "Zd3k9XsT"
}
```
- User Authentication - public endpoint
  POST https://localhost:8443/api/v1/auth/authenticate
```http request
### Should authenticate 2nd user: Carlos Rodriguez and return a JWT token
POST https://localhost:8443/api/v1/auth/authenticate
Content-Type: application/json

{
"email": "carlos.rodriguez@example.com",
"password": "Hg7fDp2R"
}
```

Notes: 
- The following test users are preloaded in the database:
```
FirstName: Emily, LastName: Johnson, Email: emily.johnson@example.com, Password: Zd3k9XsT
FirstName: Carlos, LastName: Rodriguez, Email: carlos.rodriguez@example.com, Password: Hg7fDp2R
FirstName: Samantha, LastName: Lee, Email: samantha.lee@example.com, Password: Lk6tGh9Q
FirstName: Daniel, LastName: Kim, Email: daniel.kim@example.com, Password: Vb4rJf8W
FirstName: Julia, LastName: Martinez, Email: julia.martinez@example.com, Password: Nm5dRs7Z
```
Note: Once authenticated with email/password, the authentication endpoint will return a JWT token that can be used to access any private endpoint.

- Actuator endpoints (allowed for authenticated users only)
  https://localhost:8443/actuator/health
  https://localhost:8443/actuator/info
  https://localhost:8443/actuator/metrics
```http request
### Get countries from 2 continents (public endpoint)
GET https://localhost:8443/api/v1/public/countries/CA,US,RO,IT,MD,AD,AF,CL,CN,AR,BR,MX
Content-Type: application/json
```
- There are preconfigured test endpoints defined in the apiRequests.http file that can be used to test all the endpoints IntelliJ directly
They can also be copied and executed in postman.
Sample:
```http request
### More complex test case with countries from 4 different continents (public endpoint)
GET https://localhost:8443/api/v1/public/countries/CA,US,RO,IT,MD,AD,AF,CL,CN,AR,BR,MX
Content-Type: application/json
```
The apiRequests-prod.https is in the src/main/resources/apiRequests location.
There is also a dev version which uses the http protocol on port 8080 instead of https on port 8443
which can be used for local tests with an IDE like Intellij

## SSL
The PROD application started by "docker-compose up" is configured to use SSL for maximum security.
API calls to the PROD use https instead of http and 8443 port instead of 8080
Example:
https://localhost:8443/api/v1/public/countries/CA,US,IT

## Logs
The logs are configured as per below requirements: 
"please update the app so that only logs with a warning level or above are logged to the console."

The log configuration is done in application-prod.yaml and customized using profiles.


## Postgress Database
When the application is started in docker.
It will create a docker container for a Postgres database.
The database contains the data for all the countries and continents obtained 
from the following URL: https://countries.trevorblades.com/graphql

The application creates the data in a new schema called "country_api" and the tables are named 
- continent
- country
- api-user

## JWT Authentication
The application uses JWT authentication to secure the private endpoints.

## Caching requests
The application uses Ehcache to cache the requests.
The cache is configured in the `src/main/resources/ehcache.xml` file.
The cache expires every 10 minutes

## Flyway Migrations
The application uses Flyway to manage database migrations. 
The Flyway migrations are located in the `src/main/resources/db/migration` directory.

The following Flyway migrations are implemented in the application:
- V2__Create_Database.sql (creates the database tables
- V3__Populate_Data.sql (populates the database tables with data obtained from the https://countries.trevorblades.com/graphql
    
## Generators
The following generators are implemented - as tests in the "generators" package
1. Flyway migration generator
   class: FlywayMigrationScriptGeneratorTest
   generator method:   generateFlywayMigrationData
When executed, this generator test will print to the console the SQL statements for creating Flyway migration script with the latest data from  https://countries.trevorblades.com/graphql
Sample output:
```sql
delete from country;
delete from continent;
insert into continent(code, name) values ('AF','Africa');
insert into continent(code, name) values ('AN','Antarctica');
insert into continent(code, name) values ('AS','Asia');
insert into continent(code, name) values ('EU','Europe');
insert into continent(code, name) values ('NA','North America');
insert into continent(code, name) values ('OC','Oceania');
insert into continent(code, name) values ('SA','South America');
insert into country(code, name, continent_code) values ('AO','Angola','AF');
insert into country(code, name, continent_code) values ('BF','Burkina Faso','AF');
insert into country(code, name, continent_code) values ('BI','Burundi','AF');
insert into country(code, name, continent_code) values ('BJ','Benin','AF');
... (same for all countries)
```
This tool was created for being able to refresh the API data periodically with ease.

To refresh, just capture the output of this generator and paste it in a new V4__Populate_Data.sql file.
2. JWT key generator
   class: JwtSecretKeyGeneratorTest
   generator method:   generateSecretKey

This tool was designed to be able to periodically refresh the JWT secret key.
The current JWT secret key is stored in the application-prod.yaml file:
```yaml
jwtToken:
  secret-key
```

## Populating initial source data using the GraphQL query and the https://countries.trevorblades.com/graphql API
The query used to obtain data from the GraphQL endpoint can be found here:
`src/main/resources/graphql/getContinentDetails.graphql`

To populate the data,  I used the generateFlywayMigrationData generator method explained above in the "Generators" section
This generator calls the GraphQL endpoint and generates the SQL statements for populating the database.

These statements were added manually in the V3__Populate_Data.sql file.
When the application is starts, the Flyway migrations will be executed by the spring boot application and the database will be created and populated using the V3__Populate_Data.sql file

## Updating with the latest source data using the GraphQL query and the https://countries.trevorblades.com/graphql API
To refresh the data, run the generateFlywayMigrationData generator method explained above in the "Generators" section
This generator calls the GraphQL endpoint and generates the SQL statements for populating the latest data to the database.

Add the generated statements manually in a new V3__Populate_Data.sql file in the Flyway `src/main/resources/db/migration` directory.
When the application is restarted, the Flyway migrations will be executed by the spring boot application and the database will be updated using the latest data from the V3__Populate_Data.sql file

## Security Configuration

The application's security is defined in the `SecurityConfiguration` class. This class is annotated with `@Configuration` and `@EnableWebSecurity` to enable Spring Security.

The `SecurityConfiguration` class contains a method named `filterChain()` that returns a `SecurityFilterChain` object. This method is responsible for configuring the application's security filters.

The `HttpSecurity` object is passed as an argument to the `filterChain()` method. This object is used to configure the application's security policies. The following security policies are implemented in the application:

- CSRF protection is enabled, but requests to the `/api/v1/auth/**` endpoint are exempted from CSRF protection.
- Requests to the `/actuator/**` endpoint require authentication.
- Requests to the `/api/v1/private/**` endpoint require authentication.
- Requests to the `/api/v1/public/**` endpoint are allowed without authentication.
- Requests to the `/api/v1/auth/**` endpoint are allowed without authentication, as these endpoints require user/password authentication.
- All other requests require authentication.

The `sessionCreationPolicy()` method is used to configure the session management policy for the application. In this case, `SessionCreationPolicy.STATELESS` is used to specify that the application will not create an HTTP session.
The `authenticationProvider()` method is used to configure the authentication provider for the application. This method takes an `AuthenticationProvider` object as an argument.
The `addFilterBefore()` method is used to add the `JwtAuthenticationFilter` to the application's filter chain.
The `headers()` method is used to configure the HTTP headers for the application. In this case, `xssProtection()` is used to add protection against XSS attacks.