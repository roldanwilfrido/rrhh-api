# RRHH-API
Application to admin Positions, Person and Employees of the company

### How to execute
1. Running unit tests and make jar file:
```
./gradlew clean test bootJar
```
2. Building image:
```
docker build --tag=rrhh-api:latest .
```
3. Run
```
docker run -p8089:8089 rrhh-api:latest
```

### RRHH-API details
Path base: `http://localhost:8089`

### /positions endpoint
Endpoint  | Info | Request Body
------------- | ------------- | ------------- 
[GET] /positions | Gets all positions | ---
[GET] /positions/{{id}} | Gets position by id | ---
[POST] /positions  | Adds new position  | *{"name": "dev"}*
[PUT] /positions/{{id}}  | Edits a position  | *{"name": "qa"}*
[DELETE] /positions/{{id}}  | Remove a position by Id | ---

### /persons endpoint
Endpoint  | Info | Request Body
------------- | ------------- | ------------- 
[GET] /persons | Gets all persons | ---
[GET] /persons/{{id}} | Gets person by id | ---
[POST] /persons  | Adds new person  | *{"name": "Camilo", "lastName": "ruiz", "address": "cra",  "cellphone": "124", "cityName": "Medellin"}*
[PUT] /persons/{{id}}  | Edits a person  | *{"name": "Camilo", "lastName": "ruiz", "address": "cra",  "cellphone": "124", "cityName": "Medellin"}*
[DELETE] /persons/{{id}}  | Remove a position by Id | ---

### /employees endpoint
Endpoint  | Info | Request Body
------------- | ------------- | ------------- 
[GET] /employees | Gets all employees | ---
[GET] /employees?person=<personId>&name=<personName> | Gets all employees | *parameters can be empty*
[GET] /employees/{{id}} | Gets an employee by id | ---
[POST] /employees  | Adds new employee  | *{"position": 1, "person": 1, "salary": 3000}*
[PUT] /employees/{{id}}  | Edits an employee  | *{"position": 1, "person": 1, "salary": 3000}*
[DELETE] /employees/{{id}}  | Remove a position by Id | ---


For more information please click [here](http://localhost:8089/docs)

### Additional information
Tool  | Version
------------- | -------------
Spring boot  | 2.5.2
Lombok  | 1.18.20
OpenApi   | 1.5.9
H2   | 1.4.200


Any further information please email me to [roldanhollow@gmail.com](mailto:roldanhollow@gmail.com)

Enjoy! 