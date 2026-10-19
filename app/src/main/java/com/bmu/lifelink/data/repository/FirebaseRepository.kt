package com.bmu.lifelink.data.repository

import com.bmu.lifelink.data.models.BlogPost
import com.bmu.lifelink.data.models.BloodRequest
import com.bmu.lifelink.data.models.Donor
import com.bmu.lifelink.data.models.DonorApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://bmu-hall-default-rtdb.asia-southeast1.firebasedatabase.app")
) {

    // Current authenticated user
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    val authStateFlow: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun signInAdmin(email: String, password: String):Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
            val user = result.user
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Authentication succeeded but user was null."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOutAdmin() {
        auth.signOut()
    }

    // Public write: Request blood
    suspend fun submitBloodRequest(request: BloodRequest): Result<String> {
        return try {
            val ref = database.getReference("requests").push()
            val id = ref.key ?: throw Exception("Failed to generate database reference key.")
            ref.setValue(request.toMap(useServerTimestamp = true)).await()
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Public write: Be a donor
    suspend fun submitDonorApplication(application: DonorApplication): Result<String> {
        return try {
            val ref = database.getReference("donorApplications").push()
            val id = ref.key ?: throw Exception("Failed to generate database reference key.")
            ref.setValue(application.toMap(useServerTimestamp = true)).await()
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Public read: Blog posts
    fun getBlogPostsFlow(): Flow<List<BlogPost>> = callbackFlow {
        val ref = database.getReference("blogPosts")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val publishedPosts = mutableListOf<BlogPost>()
                for (child in snapshot.children) {
                    val id = child.key ?: continue
                    @Suppress("UNCHECKED_CAST")
                    val value = child.value as? Map<String, Any?> ?: continue
                    publishedPosts.add(BlogPost.fromFirebase(id, value))
                }
                val allPosts = BlogPost.mergeAndSort(publishedPosts)
                trySend(allPosts)
            }

            override fun onCancelled(error: DatabaseError) {
                // If there's an error reading database, fall back to starter posts
                trySend(BlogPost.starterPosts)
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    // Admin read: Donors
    fun getDonorsFlow(): Flow<Result<Map<String, Donor>>> = callbackFlow {
        val ref = database.getReference("donors")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val donors = mutableMapOf<String, Donor>()
                for (child in snapshot.children) {
                    val id = child.key ?: continue
                    val name = child.child("name").getValue(String::class.java) ?: ""
                    val bloodType = child.child("bloodType").getValue(String::class.java) ?: ""
                    val phone = child.child("phone").getValue(String::class.java) ?: ""
                    val location = child.child("location").getValue(String::class.java) ?: ""
                    val availability = child.child("availability").getValue(String::class.java) ?: "Available"
                    val createdAt = child.child("createdAt").getValue(Long::class.java) ?: 0L
                    val updatedAt = child.child("updatedAt").getValue(Long::class.java) ?: 0L
                    val applicationId = child.child("applicationId").getValue(String::class.java)

                    donors[id] = Donor(
                        id = id,
                        name = name,
                        bloodType = bloodType,
                        phone = phone,
                        location = location,
                        availability = availability,
                        createdAt = createdAt,
                        updatedAt = updatedAt,
                        applicationId = applicationId
                    )
                }
                trySend(Result.success(donors))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Result.failure(error.toException()))
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    // Admin read: Requests
    fun getBloodRequestsFlow(): Flow<Result<Map<String, BloodRequest>>> = callbackFlow {
        val ref = database.getReference("requests")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val requests = mutableMapOf<String, BloodRequest>()
                for (child in snapshot.children) {
                    val id = child.key ?: continue
                    val studentId = child.child("studentId").getValue(String::class.java) ?: ""
                    val name = child.child("name").getValue(String::class.java) ?: ""
                    val phone = child.child("phone").getValue(String::class.java) ?: ""
                    val bloodType = child.child("bloodType").getValue(String::class.java) ?: ""
                    val units = child.child("units").getValue(String::class.java) ?: "1"
                    val hospital = child.child("hospital").getValue(String::class.java) ?: ""
                    val urgency = child.child("urgency").getValue(String::class.java) ?: ""
                    val details = child.child("details").getValue(String::class.java) ?: ""
                    val status = child.child("status").getValue(String::class.java) ?: "New"
                    val createdAt = child.child("createdAt").getValue(Long::class.java) ?: 0L

                    requests[id] = BloodRequest(
                        id = id,
                        studentId = studentId,
                        name = name,
                        phone = phone,
                        bloodType = bloodType,
                        units = units,
                        hospital = hospital,
                        urgency = urgency,
                        details = details,
                        status = status,
                        createdAt = createdAt
                    )
                }
                trySend(Result.success(requests))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Result.failure(error.toException()))
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    // Admin read: Donor Applications
    fun getDonorApplicationsFlow(): Flow<Result<Map<String, DonorApplication>>> = callbackFlow {
        val ref = database.getReference("donorApplications")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val applications = mutableMapOf<String, DonorApplication>()
                for (child in snapshot.children) {
                    val id = child.key ?: continue
                    val studentId = child.child("studentId").getValue(String::class.java) ?: ""
                    val name = child.child("name").getValue(String::class.java) ?: ""
                    val phone = child.child("phone").getValue(String::class.java) ?: ""
                    val bloodType = child.child("bloodType").getValue(String::class.java) ?: ""
                    val location = child.child("location").getValue(String::class.java) ?: ""
                    val status = child.child("status").getValue(String::class.java) ?: "Pending"
                    val createdAt = child.child("createdAt").getValue(Long::class.java) ?: 0L
                    val reviewedAt = child.child("reviewedAt").getValue(Long::class.java)
                    val donorId = child.child("donorId").getValue(String::class.java)

                    applications[id] = DonorApplication(
                        id = id,
                        studentId = studentId,
                        name = name,
                        phone = phone,
                        bloodType = bloodType,
                        location = location,
                        status = status,
                        createdAt = createdAt,
                        reviewedAt = reviewedAt,
                        donorId = donorId
                    )
                }
                trySend(Result.success(applications))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Result.failure(error.toException()))
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    // Admin write: Save Donor (Add or Update)
    suspend fun saveDonor(donor: Donor): Result<Unit> {
        return try {
            val now = System.currentTimeMillis()
            if (donor.id.isNotBlank()) {
                val donorRef = database.getReference("donors").child(donor.id)
                val updates = donor.copy(updatedAt = now).toMap()
                donorRef.updateChildren(updates).await()
            } else {
                val donorRef = database.getReference("donors").push()
                val newDonor = donor.copy(
                    id = donorRef.key ?: "",
                    createdAt = now,
                    updatedAt = now
                )
                donorRef.setValue(newDonor.toMap()).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Admin write: Delete Donor
    suspend fun deleteDonor(id: String): Result<Unit> {
        return try {
            database.getReference("donors").child(id).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Admin write: Review application (Approve or Decline)
    suspend fun reviewDonorApplication(application: DonorApplication, approve: Boolean): Result<Unit> {
        return try {
            val reviewedAt = System.currentTimeMillis()
            if (approve) {
                val donorRef = database.getReference("donors").push()
                val donorKey = donorRef.key ?: throw Exception("Could not generate donor key.")
                val newDonorMap = mapOf(
                    "name" to application.name,
                    "bloodType" to application.bloodType,
                    "phone" to application.phone,
                    "location" to application.location,
                    "availability" to "Available",
                    "createdAt" to reviewedAt,
                    "updatedAt" to reviewedAt,
                    "applicationId" to application.id
                )
                val updates = mapOf<String, Any?>(
                    "donors/$donorKey" to newDonorMap,
                    "donorApplications/${application.id}/status" to "Approved",
                    "donorApplications/${application.id}/reviewedAt" to reviewedAt,
                    "donorApplications/${application.id}/donorId" to donorKey
                )
                database.reference.updateChildren(updates).await()
            } else {
                val updates = mapOf<String, Any?>(
                    "status" to "Declined",
                    "reviewedAt" to reviewedAt
                )
                database.getReference("donorApplications").child(application.id).updateChildren(updates).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Admin write: Update Request status
    suspend fun updateRequestStatus(id: String, newStatus: String): Result<Unit> {
        return try {
            database.getReference("requests").child(id).child("status").setValue(newStatus).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Admin write: Delete Request
    suspend fun deleteRequest(id: String): Result<Unit> {
        return try {
            database.getReference("requests").child(id).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Admin write: Publish Blog Post
    suspend fun publishBlogPost(title: String, body: String, coverUrl: String): Result<String> {
        return try {
            val postRef = database.getReference("blogPosts").push()
            val id = postRef.key ?: throw Exception("Could not generate post key.")
            val now = System.currentTimeMillis()
            val postMap = mapOf(
                "title" to title.trim(),
                "body" to body.trim(),
                "coverUrl" to coverUrl.trim(),
                "createdAt" to now,
                "updatedAt" to now
            )
            postRef.setValue(postMap).await()
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Admin write: Delete Blog Post
    suspend fun deleteBlogPost(id: String): Result<Unit> {
        return try {
            database.getReference("blogPosts").child(id).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
