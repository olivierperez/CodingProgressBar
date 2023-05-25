package fr.o80.codingprogress.domain

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import fr.o80.codingprogress.data.ConfigService
import fr.o80.codingprogress.data.ProgressConfigs

@Service
class GetConfigUseCase {

    private val configService = service<ConfigService>()

    operator fun invoke(): ProgressConfigs {
        return configService.read()
    }
}
