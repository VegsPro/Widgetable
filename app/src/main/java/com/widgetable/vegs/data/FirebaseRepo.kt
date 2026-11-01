package com.widgetable.vegs.data

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
import java.util.UUID
import kotlin.random.Random

class FirebaseRepo {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    val currentUser: FirebaseUser? get() = auth.currentUser

    // ---------- Auth ----------
    suspend fun signInAnonymously(): Result<FirebaseUser> = try {
        val result = auth.signInAnonymously().await()
        result.user?.let { Result.success(it) } ?: Result.failure(Exception("No user"))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> = try {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        result.user?.let { Result.success(it) } ?: Result.failure(Exception("No user"))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun registerWithEmail(email: String, password: String, displayName: String): Result<FirebaseUser> = try {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user ?: return Result.failure(Exception("No user"))
        createUserProfile(user.uid, displayName)
        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun signOut() = auth.signOut()

    // ---------- Profile ----------
    private fun generateFriendCode(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return (1..6).map { chars[Random.nextInt(chars.length)] }.joinToString("")
    }

    suspend fun createUserProfile(uid: String, displayName: String): UserProfile {
        val code = generateFriendCode()
        val profile = UserProfile(
            uid = uid,
            displayName = displayName.ifBlank { "Друг" },
            friendCode = code,
            avatarEmoji = listOf("🌸", "🦄", "🐱", "🌈", "🍓", "✨").random()
        )
        db.child("users").child(uid).setValue(profile).await()
        // Also store code -> uid mapping for lookup
        db.child("friendCodes").child(code).setValue(uid).await()
        return profile
    }

    suspend fun getUserProfile(uid: String): UserProfile? {
        val snap = db.child("users").child(uid).get().await()
        return snap.getValue(UserProfile::class.java)
    }

    fun observeUserProfile(uid: String): Flow<UserProfile?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(UserProfile::class.java))
            }
            override fun onCancelled(error: DatabaseError) {
                trySend(null)
            }
        }
        db.child("users").child(uid).addValueEventListener(listener)
        awaitClose { db.child("users").child(uid).removeEventListener(listener) }
    }

    suspend fun updateMood(uid: String, emoji: String, text: String) {
        db.child("users").child(uid).child("mood").setValue(emoji).await()
        db.child("users").child(uid).child("moodText").setValue(text).await()
        db.child("users").child(uid).child("lastSeen").setValue(System.currentTimeMillis()).await()
    }

    suspend fun incrementMissYou(fromUid: String, toUid: String) {
        // Simple counter on target user
        val ref = db.child("users").child(toUid).child("missYouCount")
        val current = ref.get().await().getValue(Int::class.java) ?: 0
        ref.setValue(current + 1).await()
    }

    // ---------- Friends ----------
    suspend fun addFriendByCode(myUid: String, code: String): Result<Friend> {
        return try {
            val targetUidSnap = db.child("friendCodes").child(code.uppercase()).get().await()
            val targetUid = targetUidSnap.getValue(String::class.java)
                ?: return Result.failure(Exception("Код не найден"))

            if (targetUid == myUid) return Result.failure(Exception("Это твой код"))

            val targetProfile = getUserProfile(targetUid)
                ?: return Result.failure(Exception("Пользователь не найден"))

            // Add to both friends lists
            val friendObj = mapOf(
                "uid" to targetUid,
                "displayName" to targetProfile.displayName,
                "friendCode" to targetProfile.friendCode,
                "avatarEmoji" to targetProfile.avatarEmoji,
                "addedAt" to System.currentTimeMillis()
            )
            db.child("friends").child(myUid).child(targetUid).setValue(friendObj).await()

            val myProfile = getUserProfile(myUid)
            val meObj = mapOf(
                "uid" to myUid,
                "displayName" to (myProfile?.displayName ?: "Друг"),
                "friendCode" to (myProfile?.friendCode ?: ""),
                "avatarEmoji" to (myProfile?.avatarEmoji ?: "🌸"),
                "addedAt" to System.currentTimeMillis()
            )
            db.child("friends").child(targetUid).child(myUid).setValue(meObj).await()

            Result.success(
                Friend(
                    uid = targetUid,
                    displayName = targetProfile.displayName,
                    friendCode = targetProfile.friendCode,
                    avatarEmoji = targetProfile.avatarEmoji,
                    mood = targetProfile.mood,
                    moodText = targetProfile.moodText
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeFriends(uid: String): Flow<List<Friend>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Friend>()
                for (child in snapshot.children) {
                    val f = child.getValue(Friend::class.java)
                    if (f != null) list.add(f)
                }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) {
                trySend(emptyList())
            }
        }
        db.child("friends").child(uid).addValueEventListener(listener)
        awaitClose { db.child("friends").child(uid).removeEventListener(listener) }
    }

    // ---------- Shared Pet ----------
    suspend fun createSharedPet(ownerUids: List<String>, name: String, type: String, emoji: String): SharedPet {
        val id = UUID.randomUUID().toString().take(8)
        val pet = SharedPet(
            id = id,
            name = name,
            type = type,
            emoji = emoji,
            owners = ownerUids,
            lastFed = System.currentTimeMillis(),
            lastPlayed = System.currentTimeMillis()
        )
        db.child("pets").child(id).setValue(pet).await()
        // Link pet to each owner
        ownerUids.forEach { uid ->
            db.child("userPets").child(uid).child(id).setValue(true).await()
        }
        return pet
    }

    fun observePet(petId: String): Flow<SharedPet?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(SharedPet::class.java))
            }
            override fun onCancelled(error: DatabaseError) {
                trySend(null)
            }
        }
        db.child("pets").child(petId).addValueEventListener(listener)
        awaitClose { db.child("pets").child(petId).removeEventListener(listener) }
    }

    fun observeUserPets(uid: String): Flow<List<String>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ids = snapshot.children.mapNotNull { it.key }
                trySend(ids)
            }
            override fun onCancelled(error: DatabaseError) {
                trySend(emptyList())
            }
        }
        db.child("userPets").child(uid).addValueEventListener(listener)
        awaitClose { db.child("userPets").child(uid).removeEventListener(listener) }
    }

    suspend fun feedPet(petId: String) {
        val ref = db.child("pets").child(petId)
        val snap = ref.get().await()
        val pet = snap.getValue(SharedPet::class.java) ?: return
        val newHunger = (pet.hunger + 25).coerceAtMost(100)
        val newHappiness = (pet.happiness + 5).coerceAtMost(100)
        val newExp = pet.exp + 10
        val newLevel = if (newExp >= pet.level * 50) pet.level + 1 else pet.level
        ref.child("hunger").setValue(newHunger).await()
        ref.child("happiness").setValue(newHappiness).await()
        ref.child("exp").setValue(if (newLevel > pet.level) 0 else newExp).await()
        ref.child("level").setValue(newLevel).await()
        ref.child("lastFed").setValue(System.currentTimeMillis()).await()
    }

    suspend fun playWithPet(petId: String) {
        val ref = db.child("pets").child(petId)
        val snap = ref.get().await()
        val pet = snap.getValue(SharedPet::class.java) ?: return
        val newHappiness = (pet.happiness + 20).coerceAtMost(100)
        val newEnergy = (pet.energy - 15).coerceAtLeast(0)
        val newExp = pet.exp + 15
        val newLevel = if (newExp >= pet.level * 50) pet.level + 1 else pet.level
        ref.child("happiness").setValue(newHappiness).await()
        ref.child("energy").setValue(newEnergy).await()
        ref.child("exp").setValue(if (newLevel > pet.level) 0 else newExp).await()
        ref.child("level").setValue(newLevel).await()
        ref.child("lastPlayed").setValue(System.currentTimeMillis()).await()
    }

    suspend fun decayPetStats(petId: String) {
        // Called periodically or on open
        val ref = db.child("pets").child(petId)
        val snap = ref.get().await()
        val pet = snap.getValue(SharedPet::class.java) ?: return
        val now = System.currentTimeMillis()
        val hoursSinceFed = (now - pet.lastFed) / (1000 * 60 * 60.0)
        val hoursSincePlay = (now - pet.lastPlayed) / (1000 * 60 * 60.0)

        val newHunger = (pet.hunger - (hoursSinceFed * 4).toInt()).coerceIn(0, 100)
        val newHappiness = (pet.happiness - (hoursSincePlay * 3).toInt()).coerceIn(0, 100)
        val newEnergy = (pet.energy + (hoursSincePlay * 2).toInt()).coerceIn(0, 100) // rest recovers energy

        ref.child("hunger").setValue(newHunger).await()
        ref.child("happiness").setValue(newHappiness).await()
        ref.child("energy").setValue(newEnergy).await()
    }
}
