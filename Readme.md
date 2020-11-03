# Spring Boot application for generating future transactions csv report

There are two ways to generate the report
1. Scheduled job - runs daily at 6A.M. Time can be configured in the application.properties file 
2. on demand - API(/futuretransactions/) is provided to generate the report and also same is returned as the response.
This on demand api is useful in case scheduled job has failed due to correct file not present at the path.
so correct file can be places and report can be generated.

## Requirements

1. Java - 1.8.x

2. Maven - 3.x.x

## Steps to Setup

**1. Clone the application**

```bash
git clone https://github.com/Dj45chd/futuretransactions.git
```
**2. Build and run the app using maven**
```
Input file to be placed at src/main/resources/input/Input.txt
Output file will be generated at src/main/resources/output/output.csv
Logs are generated at futuretransactions/logs/futuretransactions.log
```

**3. Build and run the app using maven**

```bash
mvn package
java -jar target/futuretransactions-1.0.0.jar
```

Alternatively, you can run the app without packaging it using -

```bash
mvn spring-boot:run
```

The app will start running at <http://localhost:8080>.

## Explore Rest APIs

The app defines following APIs.
    
    GET /futuretransactions/
        
