# TestAssignment
Fuel Consumption Management specified by "Fuel Consumption Management.docx"

## Tech Stack
* Spring boot
* H2 in memory database
* Maven as dependency manager

### How to run
java -jar TestAssignment-0.0.1-SNAPSHOT.jar  
After this the application will listen http://localhost:8080/

### How to build

You can import it your IDE as a Maven project.  
In Eclipse: Import -> Maven -> Existing Maven projects  
You can find more in: https://spring.io/guides/gs/serving-web-content/

#### REST
1. Register single fuel consumption: POST /api/consumptions request body contains fuel consumption json object  
Example with curl:  
```curl
curl -X POST \
  http://localhost:8080/api/consumptions \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{
	"fuelType": "Diesel",
	"pricePerLitter": 1.5,
	"volume": 100,
	"date": "2019-04-01T12:00:00",
	"driverID": "driver001"
}'
```
2. Register fuel consumptions from csv file  
POST /api/consumptions/file multipart request with file=absoule_path_csvFile  
Example with curl:  
```curl
curl -F file=@/Users/denizalp/Desktop/TestAssignment/example.csv \
   http://localhost:8080/api/consumptions/file
```  
3. Get all fuel consumptions by month(optional) and driver(optional)  
GET /api/consumptions?month=month&driver=driverID  
Example with curl:  
```curl
curl -X GET \
  http://localhost:8080/api/consumptions
```  
4. Get total spent money grouped by month for driver(optional)  
GET /api/consumptions/totalSpentMoneyByMonth?driver=driverID  
Example with curl:  
```curl
curl -X GET \
  http://localhost:8080/api/consumptions/totalSpentMoneyByMonth
```  
5. Get statistics for each month, list fuel consumption records grouped by fuel type for driver(optional)  
GET /api/consumptions/statsByFuelType?driver=driverID  
Example with curl:  
```curl
curl -X GET \
  http://localhost:8080/api/consumptions/statsByFuelType
```  
##### Comments
In project directory, there are "example.csv" and "bad1.csv".  
* First is a good example to register consumptions from file. Please pay attention to the first line. The column names must be same ignoring
case and order.
* Second is not a valid example for bulk register.
* In REST part, optional means that request param is not necessary for the call. If there is, then the results will be narrowed by the parameters
otherwise the results will be obtained by default.
