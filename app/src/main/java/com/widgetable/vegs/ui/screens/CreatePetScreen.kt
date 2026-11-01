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
import com.widgetable.vegs.data.PetTypes
import com.widgetable.vegs.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePetScreen(
    viewModel: MainViewModel,
    onCreated: (String) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("Пушистик") }
    var selectedType by remember { mutableStateOf(PetTypes.list.first()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новый питомец") },
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
            Text("Выбери питомца", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(200.dp)
            ) {
                items(PetTypes.list) { (type, emoji) ->
                    Card(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable { selectedType = type to emoji },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedType.first == type)
                                MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(emoji, fontSize = 40.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Имя питомца") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(Modifier.height(32.dp))
            Button(
                onClick = {
                    viewModel.createPet(name, selectedType.first, selectedType.second) { id ->
                        onCreated(id)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Создать ${selectedType.second}")
            }
        }
    }
}
