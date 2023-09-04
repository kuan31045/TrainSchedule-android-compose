# TrainSchedule

**Train Schedule** is an Android application for Taiwan railway timetable inquiries. <br>
It is based on **Jetpack Compose** and **Material 3** for UI development, while adhering to Google's recommended architecture and the latest Android tech.
<br><br>
<p align="center">
  <a href="https://play.google.com/store/apps/details?id=com.kappstudio.trainschedule">
    <img src="art/googleplay.png" alt="GooglePlay" width='30%'
  </a>
</p>
<br>



<img src='art/s5.png' width='25%'/><img src = 'art/s2.png' width='25%'/><img src='art/s7.png' width='25%'/><img src ='art/s4.png' width='25%'/>



## Features


* 100% Jetpack Compose
* Material Design 3
* Domain layer with UseCase
* Flow & StateFlow
* Dagger-Hilt
* UDF pattern
* DTO pattern
* Jsoup
* Retrofit
* Room
* DataStore
* Dark theme & Dynamic color

<img src="art/demo.gif" align="right" width="32%"/>


## Tech Stacks
* [Jetpack Compose](https://developer.android.com/jetpack/compose) - Build declarative and reusable UI with Kotlin
* [Material 3](https://m3.material.io/) - Google’s design system for UI components, color, font style, and shapes
* [Dagger-Hilt](https://developer.android.com/training/dependency-injection/hilt-android) - Implement dependency injection
* [Jsoup](https://jsoup.org/) - Scrape documents from websites and parse HTML forms
* [Flow](https://developer.android.com/kotlin/flow) - Handle asynchronous data with observable streams
* [StateFlow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow#stateflow) - Enable flows to optimally emit state updates and values to multiple consumers
* [Compose Navigation](https://developer.android.com/jetpack/compose/navigation) - Navigate between composable screens
* [Retrofit](https://github.com/square/retrofit) - Fetche data from the server through RESTful API
* [Room](https://developer.android.com/training/data-storage/room) - Local persistence database
* [Datastore](https://developer.android.com/topic/libraries/architecture/datastore) - Aynchronous and consistent data storage for key-value pairs or typed objects
* [Dynamic color](https://developer.android.com/jetpack/compose/designsystems/material3#dynamic_color_schemes) - Derive custom colors from a user's wallpaper to be applied to the app UI

<br>
<img src="art/theme.gif" width="25%"/>



<br>








## Architectures
 <p align="center">
  <a href="https://developer.android.com/static/topic/libraries/architecture/images/mad-arch-overview.png">
    <img src="https://developer.android.com/static/topic/libraries/architecture/images/mad-arch-overview.png" alt="Architectures" width='70%'
  </a>
</p>

Follow Google recommended [Guide to app architecture](https://developer.android.com/jetpack/guide) to structure project architecture based on UI layer, Date layer and Domain layer.

### UI layer
* ViewModel: Fetch data from higher layers, update UI State(StateFlow) and handle UI event.
  
* Composable Function: Using collectAsState() function to collect UI state from ViewModel as an immutable state, then passed down to composable componets for displaying the UI. When a UI event occurs, it will call a function within the ViewModel.



 





   
### Date layer
  
### Domain layer
 

## Coustom UI

## 3rd Library

## Api Source

## Learning Resources
<a href="https://developer.android.com/courses/android-basics-compose/course?hl=en">Android Basics with Compose</a><br>
<a href="https://developer.android.com/topic/architecture">Guide to app architecture</a><br>
<a href="https://developer.android.com/jetpack/compose/architecture">Architecting your Compose UI</a><br>
<a href="https://youtu.be/1yiuxWK74vI?si=TwmxcFl0AQnSNJxJ">Introduction to drawing in Compose</a><br>

## LICENSE
