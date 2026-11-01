package com.widgetable.vegs.data

data class UserProfile(
    val uid: String = "",
    val displayName: String = "",
    val friendCode: String = "",
    val avatarEmoji: String = "🌸",
    val mood: String = "😊",
    val moodText: String = "Хорошо",
    val missYouCount: Int = 0,
    val lastSeen: Long = System.currentTimeMillis()
)

data class Friend(
    val uid: String = "",
    val displayName: String = "",
    val friendCode: String = "",
    val avatarEmoji: String = "🌸",
    val mood: String = "😊",
    val moodText: String = "",
    val lastSeen: Long = 0L
)

data class SharedPet(
    val id: String = "",
    val name: String = "Пушистик",
    val type: String = "cat", // cat, dog, bunny, dinosaur
    val emoji: String = "🐱",
    val hunger: Int = 80,      // 0-100
    val happiness: Int = 80,   // 0-100
    val energy: Int = 80,      // 0-100
    val level: Int = 1,
    val exp: Int = 0,
    val lastFed: Long = 0L,
    val lastPlayed: Long = 0L,
    val owners: List<String> = emptyList(), // uids
    val createdAt: Long = System.currentTimeMillis()
)

data class MoodEntry(
    val emoji: String = "😊",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val uid: String = ""
)

object PetTypes {
    val list = listOf(
        "cat" to "🐱",
        "dog" to "🐶",
        "bunny" to "🐰",
        "dino" to "🦕",
        "fox" to "🦊",
        "panda" to "🐼"
    )
}

object Moods {
    val list = listOf(
        "😊" to "Хорошо",
        "🥰" to "Влюблён(а)",
        "😴" to "Сонный(ая)",
        "😢" to "Грустно",
        "😤" to "Злюсь",
        "🤩" to "В восторге",
        "🤒" to "Болею",
        "😎" to "Круто",
        "🥺" to "Скучаю",
        "😋" to "Вкусно"
    )
}
