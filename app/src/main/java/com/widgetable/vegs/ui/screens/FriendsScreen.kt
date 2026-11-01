package com.widgetable.vegs.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.widgetable.vegs.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var codeInput by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Друзья") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.PersonAdd, "Добавить")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.PersonAdd, null) },
                text = { Text("Добавить по коду") }
            )
        }
    ) { padding ->
        if (state.friends.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("👥", fontSize = 64.sp)
                    Spacer(Modifier.height(16.dp))
                    Text("Пока нет друзей", fontWeight = FontWeight.Bold)
                    Text(
                        "Добавь друга по его коду,\nчтобы вместе растить питомца",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.friends) { friend ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(friend.avatarEmoji, fontSize = 28.sp)
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(friend.displayName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Код: ${friend.friendCode}", style = MaterialTheme.typography.bodySmall)
                                Text("${friend.mood} ${friend.moodText}", style = MaterialTheme.typography.bodySmall)
                            }
                            TextButton(onClick = { viewModel.sendMissYou(friend.uid) }) {
                                Text("💕")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Добавить друга") },
            text = {
                Column {
                    Text("Введи 6-значный код друга")
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = codeInput,
                        onValueChange = { codeInput = it.uppercase().take(6) },
                        label = { Text("Код") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    message?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(it, color = MaterialTheme.colorScheme.primary)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.addFriend(codeInput) { result ->
                            result.fold(
                                onSuccess = {
                                    message = "Друг добавлен! 🎉"
                                    codeInput = ""
                                },
                                onFailure = { message = it.message }
                            )
                        }
                    },
                    enabled = codeInput.length == 6
                ) {
                    Text("Добавить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}
