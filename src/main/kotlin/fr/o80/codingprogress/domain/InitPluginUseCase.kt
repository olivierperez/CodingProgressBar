package fr.o80.codingprogress.domain

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import fr.o80.codingprogress.data.ConfigService
import fr.o80.codingprogress.data.ProgressConfig
import fr.o80.codingprogress.data.ProgressConfigs

@Service
class InitPluginUseCase {

    private val configService = service<ConfigService>()

    operator fun invoke() {
        val currentConfigs = configService.read()
        if (currentConfigs.paths.isNullOrEmpty()) {
            configService.save(defaultConfig())
        }
    }

    private fun defaultConfig() = ProgressConfigs(
        mutableListOf(
            ProgressConfig(
                path = "*",
                imagePath = "husky",
                colors = "#eb4023,#f8ca51,#fffd5d,#61fa4c,#90f6f5,#2610f0,#551d84"
            )
        )
    )
}
