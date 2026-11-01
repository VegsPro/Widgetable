package com.widgetable.vegs.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.widgetable.vegs.ui.theme.PetGreen
import com.widgetable.vegs.ui.theme.PinkPrimary
import com.widgetable.vegs.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetScreen(
    petId: String,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val pet = state.currentPet

    LaunchedEffect(petId) {
        viewModel.loadPet(petId)
    }

    // Bounce animation
    val infiniteTransition = rememberInfiniteTransition(label = "pet")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(pet?.name ?: "Питомец") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pet display
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(PetGreen.copy(0.4f), PinkPrimary.copy(0.2f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(pet?.emoji ?: "🐱", fontSize = 96.sp)
            }

            Spacer(Modifier.height(16.dp))
            Text(
                "Уровень ${pet?.level ?: 1}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Опыт: ${pet?.exp ?: 0} / ${(pet?.level ?: 1) * 50}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(32.dp))

            // Stats
            StatBar(label = "Сытость", value = pet?.hunger ?: 0, color = PetGreen)
            Spacer(Modifier.height(12.dp))
            StatBar(label = "Счастье", value = pet?.happiness ?: 0, color = PinkPrimary)
            Spacer(Modifier.height(12.dp))
            StatBar(label = "Энергия", value = pet?.energy ?: 0, color = MaterialTheme.colorScheme.tertiary)

            Spacer(Modifier.height(40.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { viewModel.feedPet(petId) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PetGreen)
                ) {
                    Icon(Icons.Default.Restaurant, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Покормить")
                }
                Button(
                    onClick = { viewModel.playPet(petId) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.SportsEsports, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Поиграть")
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(
                "Выращивайте вместе с друзьями!\nДобавьте друга, чтобы питомец был общим.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun StatBar(label: String, value: Int, color: androidx.compose.ui.graphics.Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text("$value%", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { value / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}
