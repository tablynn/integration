# Integration
### Team: Emily Perelman and Tabitha Lynn

REPO LINK: https://github.com/cs0320-f2022/integration-eperelm2-tlynn1

Contributions: 
nharbiso and kkashyap
- helped with understanidng how to filter features and use of records to do so
ceng4 and ahudda1
- helped us understand general structure of redline handler class
agrondin-ctulpar-wpark14
- helped with overall structure and understanding of testing

Estimated time: 12 hours

## DESIGN CHOICES
We decided to create a RedlineHandler class that filters the features in the fulldownload.json based on the minimum and maximum latitude and minimum and maximum longitude from the query parameters. In this class, we take the query parameters and filter the json data based on the bounded box. We then return this data in response map, which has been adapted using Moshi. 

We also chose to import our entire sprint 2 into integration, creating a frontend and a backend folders for our code. Our backend is the copied sprint 2 code, plus the added RedlineHandler class and a RedlineHandlerTest class. Because of the additions, we are able to run the API Server with our "bounded box", returning the filtered features. The frontend handles creating the map. This means setting up all React components and the MapBox.tsx class, creating the map and allowing all the interactions including clicking and dragging.  

##ERRORS & BUGS
- When clicking on an area of the map, the state, city, and name is covered slightly by the other writing. It is still readable, but probably not ideal

##TESTING
GeoDataTest
- Successful testing of the filterFeatures without filtering
- No passing in min or maximum latitude and longitude with the filecall
- Successful filtering of valid min and max latitude and longitude 
- Testing missing parameters with the API call (specifically: not passing in minimum latitude 
and not having a maximum latitude
- Testing error when min lat/ long is greater than max lat/long in API call
- Testing when not numbers are passed in as coordinates for lat and long
- Testing when the coordinates are out of bounds

GeoDataFuzzTest
- Tests to see that random fuzz testing filters correctly with no errors


MockGeoDataTest
- created a mock to act as a dependency injection
- Did so in order to test without calling the backend API server

##HOW TO
####User Story 1:
To use our map, the user must run our local host. To do so, you can run npm start in the terminal. The map will then open in a browser. From there, you can drag and move the map however you like, zooming in to whichever city you prefer. 

###User Story 2:
After running npm start in the terminal, the map will open in the browser with the historical redlining data as an overlay over the map. Depending on which city you choose to zoom into, there may or may not be redlining data. 

For the S with distinction portion of user story 2, clicking on an area of the map will show the state, city, and name on the screen under the map. There is sometimes a null or empty value if the original data does not provide it for the specific city that is clicked on. 

###User Story 3:
To access the redlining data from the API server, first run the server in the backend from IntelliJ. Then, you can type in the link: "http://localhost:3231/redlineData?" + the minimum latitude, maximum latitude, minimum longitude, and maxiumum longitude into your browser. The resulting data will be the redlining GeoJSON dataset filtered geographically by the inputted min/max latitude/longitude created box. The data will appear in the format of a list of features.