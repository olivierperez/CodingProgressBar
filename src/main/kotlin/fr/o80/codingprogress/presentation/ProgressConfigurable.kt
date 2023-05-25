package fr.o80.codingprogress.presentation

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.intellij.ui.table.TableView
import fr.o80.codingprogress.data.ProgressConfig
import fr.o80.codingprogress.data.ProgressConfigs
import fr.o80.codingprogress.domain.GetConfigUseCase
import fr.o80.codingprogress.domain.SaveConfigUseCase
import javax.swing.JComponent

class ProgressConfigurable(
    private val project: Project
) : Configurable {

    private val getConfig = service<GetConfigUseCase>()
    private val saveConfig = service<SaveConfigUseCase>()

    private var initial = getConfig().paths
        ?.map { ProgressItem(it.path ?: "", it.imagePath ?: "", it.colors ?: "") }
        ?.toMutableList()
        ?: mutableListOf()
    private val current = initial.cloned()

    private val tableModel = ProgressConfigTableModel(current)

    override fun createComponent(): JComponent = panel {
        row {
            cell(
                ToolbarDecorator.createDecorator(TableView(
                    tableModel
                ).apply {
                    isStriped = true
                })
                    .setAddAction {
                        tableModel.addRow(defaultProgressItem())
                        tableModel.fireTableDataChanged()
                    }
                    .createPanel()
            ).horizontalAlign(HorizontalAlign.FILL)
        }
    }

    override fun isModified(): Boolean {
        return initial != current
    }

    override fun apply() {
        saveConfig(ProgressConfigs(current.map { ProgressConfig(it.path, it.imagePath, it.colors) }.toMutableList()))
        initial = current.cloned()
    }

    override fun getDisplayName(): String {
        return "Coding Progress Bar"
    }

    private fun defaultProgressItem() = ProgressItem(project.basePath ?: "path", "", "")
}

private fun <E> MutableList<E>.cloned(): MutableList<E> {
    return this.map { it }.toMutableList()
}
