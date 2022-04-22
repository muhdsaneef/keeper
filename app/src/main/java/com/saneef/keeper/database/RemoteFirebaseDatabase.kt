package com.saneef.keeper.database

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.installations.FirebaseInstallations
import com.saneef.keeper.TimestampHelper
import com.saneef.keeper.di.DefaultDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

class RemoteFirebaseDatabase @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val timestampHelper: TimestampHelper,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) {

    suspend fun uploadTimeStamp() {
        withContext(dispatcher) {
            FirebaseInstallations.getInstance().id.addOnSuccessListener {
                firebaseDatabase.getReference(it).child(timestampHelper.currentTimestamp.toString())
                    .setValue(Random.nextLong())
            }
        }
    }
}
