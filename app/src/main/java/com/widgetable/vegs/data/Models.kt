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

enum class RoomType {
    LIVING,    // Гостиная
    DINING,    // Столовая
    BATHROOM,  // Ванная
    BEDROOM,   // Спальня
    OUTDOOR    // Улица / Город питомцев
}

data class PetOutfit(
    val hat: String = "",       // e.g. "blue_hat"
    val glasses: Boolean = false,
    val clothes: String = ""    // e.g. "purple_jacket"
)

data class SharedPet(
    val id: String = "",
    val name: String = "Питомец",
    val type: String = "penguin", // penguin, bear, cat, fox...
    val emoji: String = "🐧",
    // Stats
    val hunger: Int = 80,
    val happiness: Int = 80,
    val energy: Int = 80,
    val cleanliness: Int = 80,
    val level: Int = 1,
    val exp: Int = 0,
    // Position in current room (normalized 0..1)
    val posX: Float = 0.5f,
    val posY: Float = 0.6f,
    val currentRoom: String = RoomType.LIVING.name,
    val outfit: PetOutfit = PetOutfit(),
    val isSleeping: Boolean = false,
    val lastFed: Long = 0L,
    val lastPlayed: Long = 0L,
    val lastWashed: Long = 0L,
    val owners: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class House(
    val id: String = "",
    val name: String = "Дом",
    val ownerUids: List<String> = emptyList(),
    val petIds: List<String> = emptyList(),
    val currentRoom: String = RoomType.LIVING.name,
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
        "penguin" to "🐧",
        "bear" to "🐻",
        "cat" to "🐱",
        "fox" to "🦊",
        "bunny" to "🐰",
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

object RoomInfo {
    val rooms = listOf(
        RoomType.LIVING to "Гостиная",
        RoomType.DINING to "Столовая",
        RoomType.BATHROOM to "Ванная",
        RoomType.BEDROOM to "Спальня",
        RoomType.OUTDOOR to "Улица"
    )
}
