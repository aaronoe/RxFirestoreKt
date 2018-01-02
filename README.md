# RxFirestoreKt
A set of extension functions to wrap Firebase's Firestore Android SDK with RxJava sugar.

Usage
-----
Extension functions are available for the following types from Firestore:
- `DocumentReference`
- `CollectionReference`
- `Query`
- `FirebaseFirestore`

Call the methods `getObservable()`, `getFlowable()` and `getSingle` on `CollectionReference`, `Query` or `DocumentReference` 
to get one-time values or subscribtions to those rerferences/queries.
The firestore-listener is cancelled when the disposable is disposed.

```kotlin
val collectionReference = FirebaseFirestore.getInstance().collection("USERS")

val disposable = collectionReference.getObservable<User>()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ userList ->
            // do stuff here
        }, { exception ->
            // exception handling
        })
```

Download
--------
Maven:
```xml
<dependency>
  <groupId>de.aaronoe</groupId>
  <artifactId>rxfirestorekt</artifactId>
  <version>0.1.0</version>
  <type>pom</type>
</dependency>
```
or Gradle:
```groovy
compile 'de.aaronoe:rxfirestorekt:0.1.0'
```


All Available Extensions
-----
```kotlin
val collectionReference = FirebaseFirestore.getInstance().collection("USERS")
val userDocumentReference = collectionReference.document("unique-user-id")
val usersQuery = collectionReference.whereEqualTo("userName", "test")

val disposable = collectionReference.getObservable<User>()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ userList ->
            // do stuff here
        }, { exception ->
            // exception handling
        })

val usersObservable: Observable<List<User>> = collectionReference.getObservable<User>()
val usersFlowable: Flowable<List<User>> = collectionReference.getFlowable<User>(BackpressureStrategy.LATEST)
val usersSingle: Single<List<User>> = collectionReference.getSingle<User>()

val queryObservable = usersQuery.getObservable<User>()
val queryFlowable = usersQuery.getFlowable<User>(BackpressureStrategy.LATEST)
val querySingle = usersQuery.getSingle<User>()

val userObservable: Observable<User> = userDocumentReference.getObservable<User>()
val userSingle: Single<User> = userDocumentReference.getSingle<User>()
val userFlowable: Flowable<User> = userDocumentReference.getFlowable<User>(BackpressureStrategy.LATEST)

val setUserCompletable: Completable = userDocumentReference.setDocument(User("username", "userid", 0))
val addUserSingle: Single<DocumentReference> = collectionReference.addDocumentSingle(User("username", "userid", 0))
val deletedDocument: Completable = userDocumentReference.deleteDocument()

val updateCompletable: Completable = userDocumentReference.updateDocumentCompletable("userName", "newvalue")

val updateMap = HashMap<String, String>().apply {
    put("userName", "newvalue")
    put("userId", "newvalue")
}
val updateCompletableMap: Completable = userDocumentReference.updateDocumentCompletable(updateMap)

val incrementValue: Single<Long> = userDocumentReference.incrementField("followerCount", 1)

val transaction: Single<String> = FirebaseFirestore.getInstance().runTransactionSingle(Transaction.Function<String> {
    it.set(userDocumentReference, User("username", "userid", 0))
    it.update(userDocumentReference, "userName", "test123")
    it.get(userDocumentReference).get("userName") as String
})
```
