package fr.o80.codingprogress.component;

import com.intellij.ide.PowerSaveMode;
import fr.o80.codingprogress.presentation.component.CodingCafeProgressBarUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;

/**
 * This Java class is used to override the static function [javax.swing.plaf.ComponentUI#createUI].
 */
public class CodingCafeProgressBarUIJava extends CodingCafeProgressBarUI {

    @SuppressWarnings({"MethodOverridesStaticMethodOfSuperclass", "UnusedDeclaration"})
    public static @NotNull ComponentUI createUI(JComponent c) {
        boolean powerMode = PowerSaveMode.isEnabled();
        if (powerMode) {
            return new BasicProgressBarUI();
        } else {
            return new CodingCafeProgressBarUI();
        }
    }
}
