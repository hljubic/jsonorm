# JsonORM [![](https://jitpack.io/v/hljubic/jsonorm.svg)](https://jitpack.io/#hljubic/jsonorm)

Easily distribute data over your Android application. Alternative for SQLite database.

This library is inspired with [SugarORM](https://github.com/chennaione/sugar) library.

## How it works
Library serialize your data and store it in JSON format. You can store your data in
* Text file
* Shared Preferences
* Assets (only for read)

## Installation

### Gradle
You can get library from jitpack.io repository. Add this lines to your project's build.gradle file:

```
repositories {
    maven {
        url "https://jitpack.io"
    }
}
```
and this lines to your app's build.gradle
```
dependencies {
    compile 'com.github.hljubic:jsonorm:1.0.2'
}
```

## Preparation
First step is to initialize JsonOrm class (it's required only once)

```java
JsonOrm.with(context);
```
In second step, we will create a class that will represent our objects that we will save in the file ***users.json***

```java
@InFile("users.json")
public class User extends JsonTable<User> {
    private String username;
    private String password;

    public User() {
      // Empty constructor is required
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
```
**NOTE:** **@InFile** annotation is not required, if you don't annotate class your data will be stored in Shared Preferences by default. 
If you want to read some predefined data from assets folder you should replace it with **InAssets** annotation.

```java
@InAssets("filename.json")
public class User extends JsonTable<User> {

  /*
    Attributes must be as same as they are represented in file
    You can use @Serialize annotation which is used in Gson library
  */
  
}
```

## Usage
You are ready to perform CRUD operations on your model.
### Save data

```java
User user = new User("john", "john123");
user.save();
```
Every CRUD operation has it's own async version. 
So, if you don't want to block UI thread, you can do something like this:

```java
user.saveAsync();
```
If you need callback, you can pass OnResponseListener object to **saveAsync** method
```java
user.saveAsync(new JsonTable.OnResponseListener<User>() {
    @Override
    public void onComplete() {
      // Your user object is stored
    }
});
```
**NOTE:** If your file is stored in assets, you can't save data, only reading is allowed.

### Update
```java
User user = User.findById(User.class, "userId");
user.setPasword("john124");
user.updateAsync();
```
If you don't manually define it, ID will be generated randomly.

### Read
To get all data from storage, you can call **listAll** method.
```java
List<User> users = User.listAll(User.class);

// Or async version
User.listAllAsync(User.class, new JsonTable.OnListResponseListener<User>() {
    @Override
    public void onComplete(List<User> users) {
    
        for (User user : users) {
            Log.i("TAG", user.getUsername());
        }
        
    }
});
```

### Delete
Delete is also common operation, but it can't be used with @InAssets annotation.
```java
User.findById(User.class, "userId").delete();
```
## Done
Excellent! You have made your first app that uses JsonORM library. 
