package fr.o80.codingprogress

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import fr.o80.codingprogress.component.CodingCafeProgressBarUIJava
import javax.swing.UIManager

class CodingStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        UIManager.put("ProgressBarUI", CodingCafeProgressBarUIJava::class.java.name)
        UIManager.getDefaults()[CodingCafeProgressBarUIJava::class.java.name] = CodingCafeProgressBarUIJava::class.java
    }
}
