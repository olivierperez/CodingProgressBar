package fr.o80.codingprogress.presentation.column

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.AbstractTableCellEditor
import fr.o80.codingprogress.presentation.ProgressItem
import java.awt.Component
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.JTable
import javax.swing.table.TableCellEditor

abstract class FileColumnInfo(
    name: String,
    private val value: (ProgressItem) -> String
) : DefaultProgressItemColumnInfo(name, value) {
    override fun getEditor(item: ProgressItem): TableCellEditor {
        return object : AbstractTableCellEditor() {

            private var valueSelected: String = value(item)

            override fun getCellEditorValue(): String {
                return valueSelected
            }

            override fun getTableCellEditorComponent(
                table: JTable?,
                value: Any?,
                isSelected: Boolean,
                row: Int,
                column: Int
            ): Component {
                openFileChooser()
                return JBTextField(valueSelected).apply {
                    addMouseListener(object : MouseAdapter() {
                        override fun mouseClicked(e: MouseEvent?) {
                            openFileChooser()
                        }
                    })
                }
            }

            fun openFileChooser() {
                val preselectedVirtualFile = LocalFileSystem.getInstance().findFileByIoFile(preselectedFile(item))

                FileChooser.chooseFile(
                    fileDescription(),
                    null,
                    null,
                    preselectedVirtualFile
                ) { virtualFile ->
                    valueSelected = virtualFile.canonicalPath ?: ""
                }
            }
        }
    }

    abstract fun preselectedFile(item: ProgressItem): File
    abstract fun fileDescription(): FileChooserDescriptor
}

