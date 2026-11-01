package com.widgetable.vegs.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.widgetable.vegs.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MainUiState(
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = true,
    val profile: UserProfile? = null,
    val friends: List<Friend> = emptyList(),
    val petIds: List<String> = emptyList(),
    val currentPet: SharedPet? = null,
    val error: String? = null
)

class MainViewModel : ViewModel() {
    private val repo = FirebaseRepo()

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        checkAuth()
    }

    private fun checkAuth() {
        val user = repo.currentUser
        if (user != null) {
            _uiState.update { it.copy(isLoggedIn = true, isLoading = false) }
            loadUserData(user.uid)
        } else {
            _uiState.update { it.copy(isLoggedIn = false, isLoading = false) }
        }
    }

    fun refreshUser() {
        val user = repo.currentUser ?: return
        _uiState.update { it.copy(isLoggedIn = true) }
        loadUserData(user.uid)
    }

    private fun loadUserData(uid: String) {
        viewModelScope.launch {
            // Profile
            repo.observeUserProfile(uid).collect { profile ->
                _uiState.update { it.copy(profile = profile) }
            }
        }
        viewModelScope.launch {
            repo.observeFriends(uid).collect { friends ->
                _uiState.update { it.copy(friends = friends) }
            }
        }
        viewModelScope.launch {
            repo.observeUserPets(uid).collect { ids ->
                _uiState.update { it.copy(petIds = ids) }
                if (ids.isNotEmpty() && _uiState.value.currentPet == null) {
                    loadPet(ids.first())
                }
            }
        }
    }

    fun loadPet(petId: String) {
        viewModelScope.launch {
            repo.decayPetStats(petId)
            repo.observePet(petId).collect { pet ->
                _uiState.update { it.copy(currentPet = pet) }
            }
        }
    }

    fun logout() {
        repo.signOut()
        _uiState.value = MainUiState(isLoggedIn = false, isLoading = false)
    }

    // Auth helpers exposed
    suspend fun signInAnonymously(displayName: String): Result<Unit> {
        val result = repo.signInAnonymously()
        return result.fold(
            onSuccess = { user ->
                // Check if profile exists
                val existing = repo.getUserProfile(user.uid)
                if (existing == null) {
                    repo.createUserProfile(user.uid, displayName)
                }
                refreshUser()
                Result.success(Unit)
            },
            onFailure = { Result.failure(it) }
        )
    }

    suspend fun register(email: String, password: String, name: String): Result<Unit> {
        return repo.registerWithEmail(email, password, name).fold(
            onSuccess = {
                refreshUser()
                Result.success(Unit)
            },
            onFailure = { Result.failure(it) }
        )
    }

    suspend fun login(email: String, password: String): Result<Unit> {
        return repo.signInWithEmail(email, password).fold(
            onSuccess = {
                refreshUser()
                Result.success(Unit)
            },
            onFailure = { Result.failure(it) }
        )
    }

    fun updateMood(emoji: String, text: String) {
        val uid = repo.currentUser?.uid ?: return
        viewModelScope.launch {
            repo.updateMood(uid, emoji, text)
        }
    }

    fun addFriend(code: String, onResult: (Result<Friend>) -> Unit) {
        val uid = repo.currentUser?.uid ?: return
        viewModelScope.launch {
            val result = repo.addFriendByCode(uid, code)
            onResult(result)
        }
    }

    fun createPet(name: String, type: String, emoji: String, withFriendUid: String? = null, onCreated: (String) -> Unit) {
        val uid = repo.currentUser?.uid ?: return
        viewModelScope.launch {
            val owners = if (withFriendUid != null) listOf(uid, withFriendUid) else listOf(uid)
            val pet = repo.createSharedPet(owners, name, type, emoji)
            onCreated(pet.id)
        }
    }

    fun feedPet(petId: String) {
        viewModelScope.launch { repo.feedPet(petId) }
    }

    fun playPet(petId: String) {
        viewModelScope.launch { repo.playWithPet(petId) }
    }

    fun sendMissYou(toUid: String) {
        val from = repo.currentUser?.uid ?: return
        viewModelScope.launch {
            repo.incrementMissYou(from, toUid)
        }
    }
}
