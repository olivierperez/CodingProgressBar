package fr.o80.codingprogress.data

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.RoamingType
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(name = "ProgressConfigs", storages = [Storage("ProgressConfigs.xml", roamingType = RoamingType.DISABLED)])
class ConfigServiceImpl : PersistentStateComponent<ProgressConfigs>, ConfigService {

    private var state: ProgressConfigs = defaultConfig()

    override fun getState(): ProgressConfigs {
        return state
    }

    override fun loadState(state: ProgressConfigs) {
        this.state = state
    }

    override fun save(configs: ProgressConfigs) {
        with(state) {
            paths = configs.paths
        }
    }

    override fun read(): ProgressConfigs {
        return ProgressConfigs(state.paths)
    }
}

interface ConfigService {
    fun save(configs: ProgressConfigs)
    fun read(): ProgressConfigs
}
