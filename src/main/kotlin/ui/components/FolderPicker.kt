package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.io.File

class FolderPicker {

    @Composable
    fun CustomFolderPicker(
        onDirectorySelected: (String) -> Unit
    ) {
        var showDialog by remember { mutableStateOf(false) }
        var currentDirectory by remember { mutableStateOf(File(System.getProperty("user.home"))) }
        var selectedDirectory by remember { mutableStateOf<String?>(null) }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { showDialog = true }) {
                Text("Select Directory")
            }

            selectedDirectory?.let {
                Text("Selected Directory: $it")
            }

            if (showDialog) {
                FolderPickerDialog(currentDirectory = currentDirectory,
                    onDismissRequest = { showDialog = false },
                    onDirectorySelected = { directory ->
                        selectedDirectory = directory
                        onDirectorySelected(directory)
                        showDialog = false
                    },
                    onNavigate = { newDirectory ->
                        currentDirectory = newDirectory
                    })
            }
        }
    }

    @Composable
    fun FolderPickerDialog(
        currentDirectory: File,
        onDismissRequest: () -> Unit,
        onDirectorySelected: (String) -> Unit,
        onNavigate: (File) -> Unit
    ) {
        var searchText by remember { mutableStateOf("") }
        val filesInDirectory = remember(currentDirectory) {
            currentDirectory.listFiles()?.sortedWith(compareBy({ !it.isDirectory }, { it.name })) ?: emptyList()
        }
        val filteredFiles = filesInDirectory.filter {
            it.name.contains(searchText, ignoreCase = true)
        }

        Dialog(onDismissRequest = onDismissRequest) {
            Surface(
                modifier = Modifier.size(500.dp, 650.dp).background(Color.White).padding(16.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colors.surface
            ) {
                Column {
                    // Header with navigation, search, and cancel button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                currentDirectory.parentFile?.let { onNavigate(it) }
                            }, enabled = currentDirectory.parentFile != null
                        ) {
                            Text("Back")
                        }
                        Text(
                            text = "Current: ${currentDirectory.absolutePath}",
                            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.Black
                        )
                        Button(onClick = onDismissRequest) {
                            Text("Cancel")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Search Bar
                    OutlinedTextField(value = searchText,
                        onValueChange = { searchText = it },
                        label = { Text("Search") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Folder and file listing
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        if (filteredFiles.isNotEmpty()) {
                            items(filteredFiles) { file ->
                                Row(modifier = Modifier.fillMaxWidth().clickable {
                                        if (file.isDirectory) {
                                            onNavigate(file)
                                            searchText = "" // Clear search when navigating
                                        }
                                    }.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (file.isDirectory) "📁 ${file.name}" else "📄 ${file.name}",
                                        color = if (file.isDirectory) Color.Blue else Color.Gray,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (file.isDirectory) {
                                        Button(onClick = {
                                            onDirectorySelected(file.absolutePath)
                                            searchText = "" // Clear search on selection
                                        }) {
                                            Text("Select")
                                        }
                                    }
                                }
                            }
                        } else {
                            item {
                                Text(
                                    text = "No matching items found.",
                                    color = Color.Gray,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}