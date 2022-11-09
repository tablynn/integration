# Sprint 2: API Proxy Assignment
### Team: Aanya Hudda (ahudda1), Tabitha Lynn (tlynn1)

REPO LINK: https://github.com/cs0320-f2022/sprint-2-ahudda1-tlynn1

Contributions:
ceng4 and ssompal1 (helped with creating two types of https requests (weather and forecast)) &
whavens and jwilner (idea of using records within WeatherHandler instead of having separate classes)

Estimated time: 14 hours


## DESIGN CHOICES
For our WeatherHandler class, we decided to create records instead of classes. When talking to other people, we discussed creating different classes like Forecast, ForecastProperties, and TimePeriod to mimic the classes needed for the moshi adapter to serialize the map of strings and objects into JSONs. We opted to use records; this is an example of defensive programming. Records are immutable data classes—they can’t be changed by users or other backend developers.

Another design choice we made was creating a shared state of List of List of Strings. Meaning, in our Server class, we created an instance of  List<List<String>> called csvData. csvData was passed into both our LoadHandler class and GetHandler class. Using this shared state, the LoadHandler class can fill csvData with the file contents (if executed successfully) and then the GetHandler class can then use csvData (and that file’s contents). The shared state of List<List<String>> is a dependency injection because the two endpoints, loadCSV and getCSV need to share state (the List<List<String>> ).


##ERRORS & BUGS
Checkstyle: the errors we encountered have to do with capitalization in our package names and variables. However, we are keeping the names as they are because they contain abbreviations (URL, or CSV) that should not be lower case.

Note: we chose not to accommodate for invalid latitude and longitude values (aka lat/lon outside of the US) because a TA told us we didn't have to accommodate for that condition. However, we know that this is an important case to be checking to improve the user's experience.


##TESTING
####CSVHandler tests
    LoadCSV testing 
    -Invalid API call gives an API error
    -Invalid API call gives an API error
    -LoadCSV called without a filePath provided prints error_bad_request
    -LoadCSV called with an invalid filePath provided prints error_datasource
    -LoadCSV called with a valid filePath provided prints success
    GetCSV testing and load/get interaction
    -If getCSV is called without loadCSV being called, error_bad_json is printed.
    -If loadCSV is called with an invalid filePath before getCSV is called, error_bad_json is printed.
    -If loadCSV is called with a valid filePath before getCSV is called, success and the contents of the csv file are printed.


####WeatherHandler tests
    -Weather called without both query parameters (latitude and longitude) provided prints error_bad_request.
    -Weather called without one of two query parameters (longitude) provided prints error_bad_request.
    -Weather called with both query parameters provided except both query parameters are not given values prints error_datasource.
    -Weather called with both query parameters, where both query parameters are valid numbers prints success.


##HOW TO
####User Story 1:
The loadCSV class can be run by a developer in a web browser. The link that should be run is: localhost:3231/loadCSV?filePath=X, where X is replaced by the filepath to the CSV file that you want to parse and load. The site will print {"result":"success"} if the CSV file has been successfully loaded, and  {"result":"error_datasource"}/{"result":"error_bad_json"}if it the file did not work.

####User Story 2:
Similar to User Story 1, getCSV is called in a web browser after loadCSV is called successfully. The link that should be run is: localhost:3231/getCSV. No query parameters are necessary. Because loadCSV has just been called successfully, getCSV will have the filled CSV data contents from the originally inputted filePath. The site will then print "result":"success" and then “content”: and the contents of the file loaded

####User Story 3:
We structured our code to make it possible to add a new datasource without unnecessary effort or refactoring. A backend developer has easy access to change the query parameters (latitude and longitude) as well as the API website link to be inputted into the HttpRequest (webLink). To make our query parameters easily changeable (if our developer wanted to change them), we stored our query parameters in string variables (ex. filePath, latitude, longitude). Similarly, the web link that the HttpRequest takes in is created as a separate String variable, which can be easily changed to be a different API website link (a new data source).


