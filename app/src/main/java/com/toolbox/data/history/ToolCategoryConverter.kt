package com.toolbox.data.history

import androidx.room.TypeConverter

class ToolCategoryConverter {
    @TypeConverter
    fun fromToolCategory(category: com.toolbox.domain.model.ToolCategory): String {
        return category.name
    }

    @TypeConverter
    fun toToolCategory(name: String): com.toolbox.domain.model.ToolCategory {
        return com.toolbox.domain.model.ToolCategory.valueOf(name)
    }
}
