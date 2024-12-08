package ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import service.Gemini
import service.generateReadme
import ui.components.FolderPicker
import java.awt.Desktop
import java.net.URI

@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    var selectedDirectory by remember { mutableStateOf<String?>(null) }
    var generationState by remember { mutableStateOf("IDLE") }
    val folderPicker = FolderPicker()
    val coroutineScope = rememberCoroutineScope()
    var displayText by remember { mutableStateOf<String?>(null) }
    var apiKey by remember { mutableStateOf("") }
    var selectedModel by remember { mutableStateOf(Gemini.GEMINI_PRO) }

    // List of models
    val models = listOf(
        Gemini.GEMINI_PRO,
        Gemini.GEMINI_FLASH,
        Gemini.GEMINI_FLASH_8b,
        Gemini.GEMNI_EXP,
        Gemini.GEMNI_EXP_2
    )

    // Dropdown expanded state
    var dropdownExpanded by remember { mutableStateOf(false) }

    // Gradient animation colors
    val gradientColors = listOf(Color(0xFF6A11CB), Color(0xFF2575FC))
    val infiniteTransition = rememberInfiniteTransition()
    val animatedGradient = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to README Generator",
                style = MaterialTheme.typography.h5.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // API Key Input and Model Selection
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                backgroundColor = Color(0xFFF9F9F9)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Model Selector
                    Text("Select Model", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { dropdownExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = selectedModel, fontWeight = FontWeight.Bold)
                    }

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        models.forEach { model ->
                            DropdownMenuItem(onClick = {
                                selectedModel = model
                                dropdownExpanded = false
                            }) {
                                Text(text = model)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // API Key Input
                    Text("API Key", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = apiKey,
                            onValueChange = { apiKey = it },
                            placeholder = { Text("Enter API Key") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val getApiKeyUrl = "https://aistudio.google.com/app/apikey"
                                if (Desktop.isDesktopSupported()) {
                                    Desktop.getDesktop().browse(URI(getApiKeyUrl))
                                }
                            }
                        ) {
                            Text("Get API Key")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Folder Picker and README Generation
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                backgroundColor = Color(0xFFF9F9F9)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    folderPicker.CustomFolderPicker { directory ->
                        selectedDirectory = directory
                        generationState = "IDLE"
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (apiKey.isEmpty()) {
                                displayText = "Please enter an API Key!"
                                return@Button
                            }
                            coroutineScope.launch {
                                generationState = "GENERATING"
                                generateReadme(apiKey= apiKey,basePath = selectedDirectory!!, model = selectedModel) {
                                    displayText = it
                                    if (it == "README file generated successfully!") {
                                        generationState = "GENERATED"
                                    }
                                }
                            }
                        },
                        enabled = generationState == "IDLE" && selectedDirectory != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (generationState == "GENERATING") Color.Transparent else MaterialTheme.colors.primary
                        )
                    ) {
                        if (generationState == "GENERATING") {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text("Generate README")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            displayText?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.body1.copy(
                        fontSize = 16.sp,
                        color = MaterialTheme.colors.onSurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEFF3F4), RoundedCornerShape(8.dp))
                        .padding(16.dp)
                )
            }

            if (generationState == "GENERATED") {
                Text(
                    text = "README generation complete!",
                    style = MaterialTheme.typography.subtitle1.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colors.secondary
                    ),
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF34D399), Color(0xFF10B981))
                            ),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp)
                )
            }
        }
    }
}
