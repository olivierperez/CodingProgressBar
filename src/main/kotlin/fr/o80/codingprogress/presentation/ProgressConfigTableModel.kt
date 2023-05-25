package fr.o80.codingprogress.presentation

import com.intellij.util.ui.ListTableModel
import fr.o80.codingprogress.presentation.column.DefaultProgressItemColumnInfo
import fr.o80.codingprogress.presentation.column.FolderColumnInfo
import fr.o80.codingprogress.presentation.column.ImageFileColumnInfo

class ProgressConfigTableModel(
    private val items: MutableList<ProgressItem>
) : ListTableModel<ProgressItem>(
    arrayOf(
        FolderColumnInfo("Path") { it.path },
        ImageFileColumnInfo("Image") { it.imagePath },
        DefaultProgressItemColumnInfo("Colors") { it.colors },
    ),
    items
) {
    override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {
        val oldItem = items[rowIndex]
        val newItem = when (columnIndex) {
            0 -> ProgressItem(aValue as String, oldItem.imagePath, oldItem.colors)
            1 -> ProgressItem(oldItem.path, aValue as String, oldItem.colors)
            2 -> ProgressItem(oldItem.path, oldItem.imagePath, aValue as String)
            else -> error("There are no more columns")
        }
        items[rowIndex] = newItem
        super.setValueAt(aValue, rowIndex, columnIndex)
    }
}

data class ProgressItem(
    val path: String,
    val imagePath: String,
    val colors: String,
)