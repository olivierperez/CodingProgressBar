package fr.o80.codingprogress.domain

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import fr.o80.codingprogress.data.ConfigService
import fr.o80.codingprogress.data.ProgressConfig

@Service
class GetConfigByProjectUseCase {

    private val configService = service<ConfigService>()

    operator fun invoke(project: Project?): ProgressConfig? {
        val projectPath = project?.basePath ?: ""
        val foundConfig = configService.read().paths
            ?.sortedByDescending { it.path?.length ?: -1 }
            ?.firstOrNull { config -> projectPath.startsWith(config.path ?: "") || config.path == "*" }
        return foundConfig?.let(::postFix)
    }

    private fun postFix(config: ProgressConfig): ProgressConfig {
        val colors = config.colors
        return when {
            colors.isNullOrBlank() -> config.copy(colors = "#A0A0A0,#202020")
            colors.contains(',') -> config
            else -> config.copy(colors = "$colors,$colors")
        }
    }

}
