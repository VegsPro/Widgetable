package com.widgetable.vegs.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.widgetable.vegs.ui.theme.PinkPrimary
import com.widgetable.vegs.ui.theme.SoftBackground
import com.widgetable.vegs.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(onSuccess: () -> Unit) {
    var isLogin by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    // We need a temporary ViewModel or pass repo, but for simplicity use MainViewModel
    val viewModel = remember { MainViewModel() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(SoftBackground, PinkPrimary.copy(alpha = 0.15f))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))

            Text(
                text = "🌸",
                fontSize = 64.sp
            )
            Text(
                text = "Widgetable",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PinkPrimary
                )
            )
            Text(
                text = "Widget Together, Share Whatever!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(40.dp))

            // Guest button
            Button(
                onClick = {
                    isLoading = true
                    error = null
                    scope.launch {
                        val result = viewModel.signInAnonymously(name.ifBlank { "Друг" })
                        isLoading = false
                        result.fold(
                            onSuccess = { onSuccess() },
                            onFailure = { error = it.message }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Продолжить как гость", fontSize = 16.sp)
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("или", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))

            // Toggle
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp)
            ) {
                FilterChip(
                    selected = isLogin,
                    onClick = { isLogin = true },
                    label = { Text("Вход") }
                )
                Spacer(Modifier.width(4.dp))
                FilterChip(
                    selected = !isLogin,
                    onClick = { isLogin = false },
                    label = { Text("Регистрация") }
                )
            }

            Spacer(Modifier.height(24.dp))

            AnimatedVisibility(visible = !isLogin) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Имя") },
                    leadingIcon = { Icon(Icons.Default.Person, null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            if (!isLogin) Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            error?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    isLoading = true
                    error = null
                    scope.launch {
                        val result = if (isLogin) {
                            viewModel.login(email.trim(), password)
                        } else {
                            viewModel.register(email.trim(), password, name.ifBlank { "Друг" })
                        }
                        isLoading = false
                        result.fold(
                            onSuccess = { onSuccess() },
                            onFailure = { error = it.message ?: "Ошибка" }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = email.isNotBlank() && password.length >= 6
            ) {
                Text(if (isLogin) "Войти" else "Зарегистрироваться")
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}
