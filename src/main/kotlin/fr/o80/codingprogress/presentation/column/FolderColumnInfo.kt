package fr.o80.codingprogress.presentation.column

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.ProjectManager
import fr.o80.codingprogress.presentation.ProgressItem
import java.io.File

class FolderColumnInfo(
    name: String,
    private val value: (ProgressItem) -> String,
) : FileColumnInfo(
    name = name,
    value = value
) {
    override fun preselectedFile(item: ProgressItem): File {
        val basePath = ProjectManager.getInstance().openProjects.first().basePath
        return File(value(item)).takeIf { it.isDirectory }
            ?: basePath?.let { File(it) }
            ?: File(".")
    }

    override fun fileDescription(): FileChooserDescriptor {
        return FileChooserDescriptor(false, true, false, false, false, false)
    }
}
