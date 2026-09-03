package com.toolbox

import android.app.Application
import com.toolbox.data.history.HistoryDatabase

class ToolboxApplication : Application() {
    val database by lazy { HistoryDatabase.getInstance(this) }
}
