package com.toolbox.ui.tools

import androidx.compose.runtime.Composable
import com.toolbox.domain.model.Tool

object ToolScreenFactory {
    @Composable
    fun Render(tool: Tool) {
        when (tool.id) {
            "video_info" -> com.toolbox.ui.tools.video.VideoInfoTool()
            "audio_info" -> com.toolbox.ui.tools.audio.AudioInfoTool()
            "image_info" -> com.toolbox.ui.tools.images.ImageInfoTool()
            "image_resize" -> com.toolbox.ui.tools.images.ImageResizeTool()
            "image_exif" -> com.toolbox.ui.tools.images.ImageExifTool()
            "file_info" -> com.toolbox.ui.tools.files.FileInfoTool()
            "zip_folder" -> com.toolbox.ui.tools.files.ZipTool()
            "unzip_file" -> com.toolbox.ui.tools.files.UnzipTool()
            "text_counter" -> com.toolbox.ui.tools.tiny.TextCounterTool()
            "color_info" -> com.toolbox.ui.tools.tiny.ColorInfoTool()
            else -> {
                // Placeholder for unimplemented tools
                com.toolbox.ui.tools.placeholder.PlaceholderTool(tool.name)
            }
        }
    }
}
