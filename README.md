# github-social #
Sample Android Application demonstrating various use cases of RxJava in a Social application. Integrated with [Github Api](https://developer.github.com/v3/).


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
9. Overcome Github Api limitation :
    * Github api don't mark which repository is starred by current logged in user in **Listing** as well as **details**
    * Github api don't mark which other users is followed by currently logged in user in **Listing** as well as **details**
    * A mechanism to parallel fetch **logged in user's** star repos and following list and store it locally and query it for showing **follow button** or **star button**.
10. All list (User List, Repo list) have infinite scroll pagination.

### Home Feed ###

* Combine 3 parallel api call. 
    1. Fetch top 5 **Android repository**, sort by stars, from Github
    2. Fetch top **Android developer**, sort by followers, from Github
    3. Fetch my own profile to show in home screen

### RX used 
1. Rate limiting number of search request when searching Github. [Debounce](http://rxmarbles.com/#debounce)
2. RxBus using [RxRelay](https://github.com/JakeWharton/RxRelay)
3. Serial api calls.
4. Parallel api calls.
5. Dependent serial parallel Async operations.

### Github Authentication ###

* Login on Github using [Basic Authentication](https://developer.github.com/v3/auth/#basic-authentication) which responds back with OAuth token.
* Use the token to fetch the following when app starts.
    1. User starred repo
    2. User Following
    3. User Followers.
* Use OAuth token to follow other users or to star Github repositories.


### Downloadable APK FILE 
[Download from here](https://drive.google.com/file/d/0B1jzf-vV0CXPWUV0OTRNQ2dLVkk/view)
    

### Library used ###
* [RxJava](https://github.com/ReactiveX/RxJava) as RxAndroid depends on this
* [RxAndroid](https://github.com/ReactiveX/RxAndroid)
* [RxRelay](https://github.com/JakeWharton/RxRelay) for making RxBus
* [RxBinding](https://github.com/JakeWharton/RxBinding) 
* [Retrofit](https://github.com/square/retrofit) HTTP Client 
* [Fresco](http://frescolib.org) as image library
* [MarkdownView](https://github.com/tiagohm/MarkdownView) for showing Readme files
* Google Support libraries, RecyclerView, Card View etc.


### Resources ###
* [ReactiveX](http://reactivex.io)
* [RxMarbles](http://rxmarbles.com)
* [Github Api](https://developer.github.com/v3/)
* [RxJava](https://github.com/ReactiveX/RxJava)
* [RxAndroid](https://github.com/ReactiveX/RxAndroid)
* [RxRelay](https://github.com/JakeWharton/RxRelay)


### Developer ###

* Gaurav Tiwari

