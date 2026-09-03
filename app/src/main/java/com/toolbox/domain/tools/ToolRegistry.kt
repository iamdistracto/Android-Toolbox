package com.toolbox.domain.tools

import com.toolbox.core.Constants
import com.toolbox.domain.model.Tool
import com.toolbox.domain.model.ToolCategory

object ToolRegistry {
    private val tools = mutableListOf<Tool>()

    init {
        registerBuiltInTools()
    }

    fun register(tool: Tool) {
        tools.add(tool)
    }

    fun getTool(id: String): Tool? = tools.find { it.id == id }

    fun getToolsForCategory(category: ToolCategory): List<Tool> {
        return tools.filter { it.category == category }
    }

    fun getAllTools(): List<Tool> = tools.toList()

    private fun registerBuiltInTools() {
        tools.add(
            Tool(
                id = "video_info",
                name = "Video Info",
                description = "Get detailed video metadata",
                category = ToolCategory.VIDEO,
                supportedExtensions = Constants.SUPPORTED_VIDEO_EXTENSIONS.toList()
            )
        )
        tools.add(
            Tool(
                id = "audio_info",
                name = "Audio Info",
                description = "Get detailed audio metadata",
                category = ToolCategory.AUDIO,
                supportedExtensions = Constants.SUPPORTED_AUDIO_EXTENSIONS.toList()
            )
        )
        tools.add(
            Tool(
                id = "image_info",
                name = "Image Info",
                description = "View EXIF data and image properties",
                category = ToolCategory.IMAGES,
                supportedExtensions = Constants.SUPPORTED_IMAGE_EXTENSIONS.toList()
            )
        )
        tools.add(
            Tool(
                id = "image_resize",
                name = "Resize Image",
                description = "Resize images to custom dimensions",
                category = ToolCategory.IMAGES,
                supportedExtensions = Constants.SUPPORTED_IMAGE_EXTENSIONS.toList()
            )
        )
        tools.add(
            Tool(
                id = "image_exif",
                name = "Remove EXIF",
                description = "Strip metadata from images",
                category = ToolCategory.IMAGES,
                supportedExtensions = Constants.SUPPORTED_IMAGE_EXTENSIONS.toList()
            )
        )
        tools.add(
            Tool(
                id = "file_info",
                name = "File Info",
                description = "View detailed file information",
                category = ToolCategory.FILES,
                supportedExtensions = emptyList()
            )
        )
        tools.add(
            Tool(
                id = "zip_folder",
                name = "Zip Folder",
                description = "Compress files into a ZIP archive",
                category = ToolCategory.FILES,
                supportedExtensions = Constants.SUPPORTED_ARCHIVE_EXTENSIONS.toList()
            )
        )
        tools.add(
            Tool(
                id = "unzip_file",
                name = "Unzip File",
                description = "Extract files from a ZIP archive",
                category = ToolCategory.FILES,
                supportedExtensions = Constants.SUPPORTED_ARCHIVE_EXTENSIONS.toList()
            )
        )
        tools.add(
            Tool(
                id = "text_counter",
                name = "Text Counter",
                description = "Count words, characters, and lines",
                category = ToolCategory.TINY_UTILITIES,
                supportedExtensions = Constants.SUPPORTED_TEXT_EXTENSIONS.toList()
            )
        )
        tools.add(
            Tool(
                id = "color_info",
                name = "Color Info",
                description = "Inspect and convert colors",
                category = ToolCategory.TINY_UTILITIES
            )
        )
    }
}
