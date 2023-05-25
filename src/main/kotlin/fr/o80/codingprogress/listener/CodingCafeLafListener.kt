package fr.o80.codingprogress.listener

import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.LafManagerListener
import fr.o80.codingprogress.component.CodingCafeProgressBarUIJava
import javax.swing.UIManager

class CodingCafeLafListener : LafManagerListener {

    override fun lookAndFeelChanged(source: LafManager) {
        updateUi()
    }

    private fun updateUi() {
        UIManager.put("ProgressBarUI", CodingCafeProgressBarUIJava::class.java.name)
        UIManager.getDefaults()[CodingCafeProgressBarUIJava::class.java.name] = CodingCafeProgressBarUIJava::class.java
    }

}
