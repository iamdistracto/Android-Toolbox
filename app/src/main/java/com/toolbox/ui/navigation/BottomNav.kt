package com.toolbox.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.toolbox.domain.model.ToolCategory

@Composable
fun ToolboxBottomNav(
    onCategorySelected: (ToolCategory) -> Unit,
    onHistorySelected: () -> Unit
) {
    val categories = ToolCategory.entries.toTypedArray()
    var selectedIndex by remember { mutableStateOf(-1) }

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        categories.forEachIndexed { index, category ->
            val selected = selectedIndex == index
            NavigationBarItem(
                selected = selected,
                onClick = {
                    selectedIndex = index
                    onCategorySelected(category)
                },
                icon = {
                    Icon(
                        imageVector = when (category) {
                            ToolCategory.VIDEO -> Icons.Default.Movie
                            ToolCategory.AUDIO -> Icons.Default.Audiotrack
                            ToolCategory.SUBTITLES_TEXT -> Icons.Default.Subtitles
                            ToolCategory.IMAGES -> Icons.Default.Image
                            ToolCategory.PRIVACY -> Icons.Default.Shield
                            ToolCategory.FILES -> Icons.Default.Folder
                            ToolCategory.PDF_DOCUMENTS -> Icons.Default.Description
                            ToolCategory.TINY_UTILITIES -> Icons.Default.Build
                        },
                        contentDescription = null,
                        tint = if (selected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                label = {
                    Text(
                        text = when (category) {
                            ToolCategory.VIDEO -> "Video"
                            ToolCategory.AUDIO -> "Audio"
                            ToolCategory.SUBTITLES_TEXT -> "Text"
                            ToolCategory.IMAGES -> "Images"
                            ToolCategory.PRIVACY -> "Privacy"
                            ToolCategory.FILES -> "Files"
                            ToolCategory.PDF_DOCUMENTS -> "Docs"
                            ToolCategory.TINY_UTILITIES -> "Tiny"
                        },
                        color = if (selected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }
        NavigationBarItem(
            selected = selectedIndex == -1,
            onClick = {
                selectedIndex = -1
                onHistorySelected()
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = if (selectedIndex == -1)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            label = {
                Text(
                    text = "History",
                    color = if (selectedIndex == -1)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )
    }
}
