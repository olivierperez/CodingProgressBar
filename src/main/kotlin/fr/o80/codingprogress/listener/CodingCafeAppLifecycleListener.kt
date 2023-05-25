package fr.o80.codingprogress.listener

import com.intellij.ide.AppLifecycleListener
import com.intellij.openapi.components.service
import fr.o80.codingprogress.domain.InitPluginUseCase

class CodingCafeAppLifecycleListener : AppLifecycleListener {

    private val initPlugin = service<InitPluginUseCase>()

    override fun appFrameCreated(commandLineArgs: MutableList<String>) {
        initPlugin()
    }
}
