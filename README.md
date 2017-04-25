# github-social #
Sample Android Application demonstrating various use cases of RxJava in a Social application. Integrated with Github rest api


## Features of the app ##

1. Home feed showcase **top 5 Android Repository**, **top 5 Android developers** on Github and one feature profile (currently **me**)
2. A user can view any **Github repository** details.
3. A user can view **Stargazers** of a repository. (User List)
4. A user can view **Followers** and **Following** of other users. (User List)
5. A user can view other user profile.
6. A user can view Repositories **created or forked** by other users from their profile.
7. A user can login and can
    1. View his/her Github profile
    2. View his/her starred repos.
    3. Following and Followers list
    4. Star any repository
    5. Follow any user.
8. A user can search a repository from Github. Search as you type.

### Home Feed ###

* Combine 3 parallel api call. 
    1. Fetch top 5 **Android repository**, sort by stars, from Github
    2. Fetch top **Android developer**, sort by followers, from Github
    3. Fetch my own profile to show in home screen

### RX used 
1. Rate limiting number of search request when searching Github. [Debounce](http://rxmarbles.com/#debounce)

### Github Authentication ###

* Login on Github using [Basic Authentication](https://developer.github.com/v3/auth/#basic-authentication) which respond back with OAuth token.
* Use the token to fetch the following when app starts.
    1. User starred repo
    2. User Following
    3. User Followers.
    
### Downloadable APK FILE 
[Download from here](https://drive.google.com/file/d/0B1jzf-vV0CXPWUV0OTRNQ2dLVkk/view)
    

### Resources ###
* [ReactiveX](http://reactivex.io)
* [RxMarbles](http://rxmarbles.com)
* [Github Api](https://developer.github.com/v3/)
* [RxJava](https://github.com/ReactiveX/RxJava)
* [RxAndroid](https://github.com/ReactiveX/RxAndroid)
* [RxRelay](https://github.com/JakeWharton/RxRelay)


### Developer ###

* Gaurav Tiwari

