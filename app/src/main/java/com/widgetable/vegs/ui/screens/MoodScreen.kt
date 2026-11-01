package com.widgetable.vegs.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.widgetable.vegs.data.Moods
import com.widgetable.vegs.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var selected by remember { mutableStateOf(state.profile?.mood ?: "😊") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Моё настроение") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                "Как ты себя чувствуешь?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Твои друзья увидят это на своих экранах",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(Moods.list) { (emoji, text) ->
                    Card(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable {
                                selected = emoji
                                viewModel.updateMood(emoji, text)
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selected == emoji)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = if (selected == emoji) 6.dp else 1.dp
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(emoji, fontSize = 32.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(
                "Текущее: ${state.profile?.mood ?: "😊"} ${state.profile?.moodText ?: ""}",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
