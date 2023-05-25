package fr.o80.codingprogress.presentation.column

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.ProjectManager
import fr.o80.codingprogress.presentation.ProgressItem
import java.io.File

class ImageFileColumnInfo(
    name: String,
    private val value: (ProgressItem) -> String,
) : FileColumnInfo(
    name = name,
    value = value
) {
    override fun preselectedFile(item: ProgressItem): File {
        val basePath = ProjectManager.getInstance().openProjects.first().basePath
        return File(value(item)).takeIf { it.exists() }
            ?: basePath?.let { File(it) }
            ?: File(".")
    }

    override fun fileDescription(): FileChooserDescriptor {
        return FileChooserDescriptor(true, false, false, false, false, false)
            .withFileFilter { virtualFile ->
                virtualFile.extension in arrayOf("png", "jpg", "jpeg")
            }
    }
}
