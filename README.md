# Github Profile Search
=========================

This app provides screen to search for github user name and display searched user details.

Introduction
-------------
The app consists of single fragment (SearchFragment) that provides search box to enter github username and display the search results.

Search result displays user details, user followers and the ones user is following.

### Functionality
##### Fetch github user details from github api using retrofit
##### Fetch github user followers list from github api using retrofit
##### Fetch github user following list from github api using retrofit
##### Store fetched user details, followers, following in Room Db with refresh timestamp
##### Provides Offline support using Room DB once data is fetched from github api, with expiry every 2 hours
##### Displays error ui in case of any error

### Architecture Used
Clean and MVVM with LiveData

### UI Tests
The projects uses Espresso for UI testing.
SearchFragmentTest mocks SearchViewModel to run the tests.

### Database Tests
The project creates an in memory database for each database test
and uses instrument test to run them on the device.

### Local Unit Tests
#### ViewModel Tests
ViewModel is tested using local unit tests with mock Repository
implementations.

#### Repository Tests
Repository is tested using local unit tests with mock web service and
mock database.

#### Webservice Tests
The project uses MockWebServer project to test REST api interactions.

#### DB Tests
SearchDaoImpl is tested using local unit tests with mock dao implementations.

### Libraries
* Android Support Library
* Android Architecture Components
* Facebook Shimmer Library for shimmer loading effect
* Koin for dependency injection
* Retrofit and OkHttp for REST api communication
* Kotlin coroutines
* Glide for image loading
* Espresso for UI tests
* Mockwebserver for api test
* Mockk for mocking in tests
* Room for offline storage

###### Note :

This app uses no authentication github api, which has a rate limit of 60 requests per hour for each ip.



