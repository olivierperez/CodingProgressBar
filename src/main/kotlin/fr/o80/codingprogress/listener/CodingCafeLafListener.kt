package fr.o80.codingprogress.listener

import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.LafManagerListener
import com.intellij.openapi.application.ApplicationActivationListener
import com.intellij.openapi.wm.IdeFrame
import fr.o80.codingprogress.component.CodingCafeProgressBarUIJava
import javax.swing.UIManager

class CodingCafeLafListener : LafManagerListener, ApplicationActivationListener {

    init {
        updateUi()
    }

    override fun lookAndFeelChanged(source: LafManager) {
        updateUi()
    }

    override fun applicationActivated(ideFrame: IdeFrame) {
        updateUi()
    }

    private fun updateUi() {
        UIManager.put("ProgressBarUI", CodingCafeProgressBarUIJava::class.java.name)
        UIManager.getDefaults()[CodingCafeProgressBarUIJava::class.java.name] = CodingCafeProgressBarUIJava::class.java
    }
}
