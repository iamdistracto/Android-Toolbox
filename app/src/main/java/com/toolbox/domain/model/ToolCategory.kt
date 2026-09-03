package com.toolbox.domain.model

enum class ToolCategory(val title: String, val description: String, val iconRes: String) {
    VIDEO("Video", "Video tools for local processing", "Movie"),
    AUDIO("Audio", "Audio tools for local processing", "Audiotrack"),
    SUBTITLES_TEXT("Subtitles & Text", "Subtitles and text utilities", "Subtitles"),
    IMAGES("Images", "Image editing and metadata tools", "Image"),
    PRIVACY("Privacy", "Privacy and metadata cleaning", "Shield"),
    FILES("Files", "Archive and file utilities", "Folder"),
    PDF_DOCUMENTS("PDF / Documents", "PDF and document tools", "Description"),
    TINY_UTILITIES("Tiny Utilities", "Quick, simple utilities", "Build")
}
