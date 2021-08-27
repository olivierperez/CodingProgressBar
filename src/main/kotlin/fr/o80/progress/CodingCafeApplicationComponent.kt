package fr.o80.progress

import com.intellij.ide.ui.LafManager
import javax.swing.UIManager

class CodingCafeApplicationComponent {

    init {
        LafManager.getInstance().addLafManagerListener { updateUi() }
        updateUi()
    }

    private fun updateUi() {
        UIManager.put("ProgressBarUI", CodingCafeProgressBarUI::class.java.name)
        UIManager.getDefaults()[CodingCafeProgressBarUI::class.java.name] = CodingCafeProgressBarUI::class.java
    }
}
