package fr.o80.codingprogress.presentation.column

import com.intellij.util.ui.ColumnInfo
import fr.o80.codingprogress.presentation.ProgressItem

open class DefaultProgressItemColumnInfo(
    name: String,
    private val value: (ProgressItem) -> String
) : ColumnInfo<ProgressItem, String>(name) {
    override fun valueOf(item: ProgressItem?): String? = item?.let(value)
    override fun isCellEditable(item: ProgressItem?): Boolean = true
}
