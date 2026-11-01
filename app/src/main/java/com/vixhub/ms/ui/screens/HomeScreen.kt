package com.vixhub.ms.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vixhub.ms.data.SharedPet
import com.vixhub.ms.ui.theme.PetGreen
import com.vixhub.ms.ui.theme.PinkPrimary
import com.vixhub.ms.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToPet: (String) -> Unit,
    onNavigateToFriends: () -> Unit,
    onNavigateToMood: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val profile = state.profile

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Привет, ${profile?.displayName ?: "друг"}! 💕", fontWeight = FontWeight.Bold)
                        Text(
                            "Код: ${profile?.friendCode ?: "..."}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Text(profile?.avatarEmoji ?: "🌸", fontSize = 24.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Главная") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToFriends,
                    icon = { Icon(Icons.Default.People, null) },
                    label = { Text("Друзья") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToMood,
                    icon = { Icon(Icons.Default.Mood, null) },
                    label = { Text("Настроение") }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(Modifier.height(8.dp)) }

            // Quick actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        emoji = "😊",
                        title = "Настроение",
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToMood
                    )
                    QuickActionCard(
                        emoji = "💕",
                        title = "Скучаю",
                        modifier = Modifier.weight(1f),
                        onClick = { /* will send from friends */ }
                    )
                    QuickActionCard(
                        emoji = "👥",
                        title = "Друзья",
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToFriends
                    )
                }
            }

            // Pets section
            item {
                Text(
                    "Мои питомцы",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (state.petIds.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /* navigate create - for now from friends or profile */ },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = PetGreen.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🥚", fontSize = 48.sp)
                            Spacer(Modifier.height(8.dp))
                            Text("Заведи первого питомца!", fontWeight = FontWeight.SemiBold)
                            Text(
                                "Выращивайте вместе с друзьями",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = { /* create solo for now */ viewModel.createPet("Пушистик", "cat", "🐱") { onNavigateToPet(it) } },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Создать питомца")
                            }
                        }
                    }
                }
            } else {
                items(state.petIds) { petId ->
                    // We show currentPet if matches, else simple card
                    val pet = state.currentPet?.takeIf { it.id == petId }
                    PetCard(
                        pet = pet,
                        petId = petId,
                        onClick = {
                            viewModel.loadPet(petId)
                            onNavigateToPet(petId)
                        }
                    )
                }
            }

            // Friends moods
            if (state.friends.isNotEmpty()) {
                item {
                    Text(
                        "Настроение друзей",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(state.friends) { friend ->
                    FriendMoodCard(friend = friend, onMissYou = {
                        viewModel.sendMissYou(friend.uid)
                    })
                }
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun QuickActionCard(
    emoji: String,
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(90.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(emoji, fontSize = 28.sp)
            Text(title, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun PetCard(pet: SharedPet?, petId: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(PetGreen.copy(0.3f), PinkPrimary.copy(0.15f))
                    )
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(pet?.emoji ?: "🐱", fontSize = 48.sp)
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(pet?.name ?: "Питомец", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Уровень ${pet?.level ?: 1}", style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { (pet?.happiness ?: 50) / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = PinkPrimary,
                )
            }
            Icon(Icons.Default.ChevronRight, null)
        }
    }
}

@Composable
private fun FriendMoodCard(friend: com.vixhub.ms.data.Friend, onMissYou: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(friend.avatarEmoji, fontSize = 24.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(friend.displayName, fontWeight = FontWeight.SemiBold)
                Text("${friend.mood} ${friend.moodText}", style = MaterialTheme.typography.bodySmall)
            }
            TextButton(onClick = onMissYou) {
                Text("💕 Скучаю")
            }
        }
    }
}
