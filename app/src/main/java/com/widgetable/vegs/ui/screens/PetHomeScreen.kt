package com.widgetable.vegs.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.widgetable.vegs.R
import com.widgetable.vegs.data.RoomType
import com.widgetable.vegs.data.SharedPet
import com.widgetable.vegs.viewmodel.MainViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetHomeScreen(
    petId: String,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val pet = state.currentPet

    var currentRoom by remember { mutableStateOf(RoomType.LIVING) }
    var showActions by remember { mutableStateOf(false) }

    // Local drag position (for smooth dragging before sync)
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }

    LaunchedEffect(petId) {
        viewModel.loadPet(petId)
    }

    // Sync room from pet data when it changes
    LaunchedEffect(pet?.currentRoom) {
        pet?.currentRoom?.let {
            try {
                currentRoom = RoomType.valueOf(it)
            } catch (_: Exception) {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(pet?.name ?: "Дом питомцев", fontWeight = FontWeight.Bold)
                        Text(
                            "Уровень ${pet?.level ?: 1}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { showActions = !showActions }) {
                        Icon(Icons.Default.MoreVert, "Действия")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Room background + content
            RoomBackground(room = currentRoom)

            // Furniture layer (simple)
            RoomFurniture(room = currentRoom)

            // Draggable pet
            if (pet != null) {
                DraggablePet(
                    pet = pet,
                    room = currentRoom,
                    isDragging = isDragging,
                    dragOffset = dragOffset,
                    onDragStart = { isDragging = true },
                    onDrag = { offset -> dragOffset = offset },
                    onDragEnd = { finalX, finalY ->
                        isDragging = false
                        dragOffset = Offset.Zero
                        viewModel.updatePetPosition(pet.id, finalX, finalY, currentRoom.name)
                    }
                )
            }

            // Bottom room switcher
            RoomSwitcher(
                current = currentRoom,
                onSelect = { room ->
                    currentRoom = room
                    pet?.let {
                        viewModel.updatePetRoom(it.id, room.name)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )

            // Stats panel
            if (pet != null) {
                StatsPanel(
                    pet = pet,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                )
            }

            // Action sheet
            if (showActions && pet != null) {
                ActionSheet(
                    room = currentRoom,
                    pet = pet,
                    onFeed = { viewModel.feedPet(pet.id) },
                    onWash = { viewModel.washPet(pet.id) },
                    onPlay = { viewModel.playPet(pet.id) },
                    onSleep = { viewModel.toggleSleep(pet.id) },
                    onDismiss = { showActions = false },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Composable
private fun RoomBackground(room: RoomType) {
    val colors = when (room) {
        RoomType.LIVING -> listOf(Color(0xFFB3E5FC), Color(0xFFE1F5FE))
        RoomType.DINING -> listOf(Color(0xFFF8BBD9), Color(0xFFFCE4EC))
        RoomType.BATHROOM -> listOf(Color(0xFFB3E5FC), Color(0xFFE0F7FA))
        RoomType.BEDROOM -> listOf(Color(0xFFCE93D8), Color(0xFFE1BEE7))
        RoomType.OUTDOOR -> listOf(Color(0xFF81C784), Color(0xFFA5D6A7))
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors))
    )
}

@Composable
private fun RoomFurniture(room: RoomType) {
    when (room) {
        RoomType.DINING -> {
            // Simple table
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 120.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(220.dp)
                        .height(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF8BBD9))
                        .border(3.dp, Color(0xFFF48FB1), RoundedCornerShape(16.dp))
                )
            }
        }
        RoomType.BATHROOM -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 140.dp),
                contentAlignment = Alignment.Center
            ) {
                // Bathtub
                Box(
                    modifier = Modifier
                        .width(240.dp)
                        .height(120.dp)
                        .clip(RoundedCornerShape(60.dp))
                        .background(Color(0xFF81D4FA))
                        .border(4.dp, Color.White, RoundedCornerShape(60.dp))
                )
            }
        }
        RoomType.BEDROOM -> {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 80.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(2) {
                    Box(
                        modifier = Modifier
                            .width(130.dp)
                            .height(90.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE1BEE7))
                            .border(2.dp, Color(0xFFCE93D8), RoundedCornerShape(12.dp))
                    )
                }
            }
        }
        RoomType.LIVING -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 160.dp),
                contentAlignment = Alignment.Center
            ) {
                // Sofa
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .height(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF90CAF9))
                )
            }
        }
        RoomType.OUTDOOR -> {
            // Just green background for now
        }
    }
}

@Composable
private fun DraggablePet(
    pet: SharedPet,
    room: RoomType,
    isDragging: Boolean,
    dragOffset: Offset,
    onDragStart: () -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: (Float, Float) -> Unit
) {
    val density = LocalDensity.current

    // Base position from pet data (normalized)
    val baseX = pet.posX
    val baseY = pet.posY

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val maxW = constraints.maxWidth.toFloat()
        val maxH = constraints.maxHeight.toFloat()

        val petSize = 110.dp
        val petSizePx = with(density) { petSize.toPx() }

        val currentX = (baseX * maxW + dragOffset.x).coerceIn(0f, maxW - petSizePx)
        val currentY = (baseY * maxH + dragOffset.y).coerceIn(0f, maxH - petSizePx - 180f) // leave space for bottom bar

        Box(
            modifier = Modifier
                .offset { IntOffset(currentX.roundToInt(), currentY.roundToInt()) }
                .size(petSize)
                .pointerInput(pet.id) {
                    detectDragGestures(
                        onDragStart = { onDragStart() },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            onDrag(dragOffset + dragAmount)
                        },
                        onDragEnd = {
                            val newNormX = (currentX / maxW).coerceIn(0.05f, 0.9f)
                            val newNormY = (currentY / maxH).coerceIn(0.1f, 0.75f)
                            onDragEnd(newNormX, newNormY)
                        }
                    )
                }
        ) {
            PetSprite(pet = pet, isSleeping = pet.isSleeping)
            // Name tag
            Text(
                text = pet.name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 4.dp)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
private fun PetSprite(pet: SharedPet, isSleeping: Boolean) {
    val painter = when (pet.type) {
        "penguin" -> {
            if (isSleeping) painterResource(R.drawable.pet_penguin_sit)
            else painterResource(R.drawable.pet_penguin_stand)
        }
        else -> null
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (painter != null) {
            Image(
                painter = painter,
                contentDescription = pet.name,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Fit
            )
        } else {
            // Fallback emoji for other pets
            Text(
                text = pet.emoji,
                fontSize = 64.sp
            )
        }
    }
}

@Composable
private fun RoomSwitcher(
    current: RoomType,
    onSelect: (RoomType) -> Unit,
    modifier: Modifier = Modifier
) {
    val rooms = listOf(
        RoomType.LIVING to "🛋️",
        RoomType.DINING to "🍽️",
        RoomType.BATHROOM to "🛁",
        RoomType.BEDROOM to "🛏️",
        RoomType.OUTDOOR to "🌳"
    )

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            rooms.forEach { (room, emoji) ->
                val selected = room == current
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (selected) Color(0xFFFFB6C1) else Color.Transparent)
                        .clickable { onSelect(room) }
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(emoji, fontSize = 22.sp)
                }
            }
        }
    }
}

@Composable
private fun StatsPanel(pet: SharedPet, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.width(110.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.9f))
    ) {
        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            MiniStat("🍎", pet.hunger)
            MiniStat("💖", pet.happiness)
            MiniStat("⚡", pet.energy)
            MiniStat("✨", pet.cleanliness)
        }
    }
}

@Composable
private fun MiniStat(icon: String, value: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(icon, fontSize = 12.sp)
        Spacer(Modifier.width(4.dp))
        LinearProgressIndicator(
            progress = { value / 100f },
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = when {
                value > 60 -> Color(0xFF81C784)
                value > 30 -> Color(0xFFFFB74D)
                else -> Color(0xFFE57373)
            }
        )
    }
}

@Composable
private fun ActionSheet(
    room: RoomType,
    pet: SharedPet,
    onFeed: () -> Unit,
    onWash: () -> Unit,
    onPlay: () -> Unit,
    onSleep: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Действия", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton("🍎", "Кормить", onFeed)
                if (room == RoomType.BATHROOM) {
                    ActionButton("🛁", "Купать", onWash)
                }
                ActionButton("🎾", "Играть", onPlay)
                if (room == RoomType.BEDROOM) {
                    ActionButton("😴", if (pet.isSleeping) "Разбудить" else "Спать", onSleep)
                }
            }
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                Text("Закрыть")
            }
        }
    }
}

@Composable
private fun ActionButton(emoji: String, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FilledIconButton(onClick = onClick) {
            Text(emoji, fontSize = 22.sp)
        }
        Text(label, fontSize = 11.sp)
    }
}
