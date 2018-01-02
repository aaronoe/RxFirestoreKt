package de.aaronoe.rxfirestorekt

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import de.aaronoe.rxfirestore.*
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
    }


    data class User(val userName: String, val userId: String, val followerCount: Long) {
        constructor() : this(
                userName = "",
                userId = "",
                followerCount = 0
        )
    }
}
