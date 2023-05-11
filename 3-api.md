**Mobile Development 2022/23 Portfolio**
# API description

Student ID: `c21086065`

_Complete the information above and then write your 300-word API description here.__


The first part of my application Is supposed to have checkboxes that when added will automatically add a delete and an edit button next to it. Instead of hardcoding the checkboxes in the layout I made a Linear layout container view that will store the checkboxes, the checkboxes are added via a method that designs them and sets the two buttons next to it. The reason why it’s a container Linear layout is because the application is set to be dynamic as the user could add as much checkboxes as they could. To save the checkboxes rather than use a database I used SharedPreferences. This is done so as the data that is being saved is simple such as the name and do represent a key-value pair. A design choice that made was that rather than have tab buttons I have added a floating button to navigate to the other page, this is simple because the tabs looked outdated while the circular button was simple and new.
__

The other part is the weather page. The layout consists of textviews, an imageview and a marquee text, each represent weather data that dynamically change with the data retrieved from the OpenWeather API. When the user grants the app to get the Location, the project will get the latitude and longitude of the device and will replace the data in the API’s URL to give the user the weather information of his location. With the location, a method using the Volley library makes an HTTP request to the API to retrieve the weather information. With the information retrieved from the JSON response, it would then replace the UI with the new data. The method also uses the Picasso library to dynamically change the weather icon with the weather icon from the API URL. I wanted the application to be dynamic and responsive which is why I implemented Picasso. Another choice made, was to keep the files as activities instead of fragments is because when the user navigates to the weather page, I want it to refresh each time.
