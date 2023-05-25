package fr.o80.codingprogress.presentation

import com.intellij.ui.scale.JBUIScale
import java.awt.geom.AffineTransform

fun affineTransform(tx: Float, ty: Float, sx: Float, sy: Float): AffineTransform =
    AffineTransform().apply {
        scale(
            JBUIScale.scale(sx).toDouble(),
            JBUIScale.scale(sy).toDouble()
        )
        translate(
            JBUIScale.scale(tx).toDouble(),
            JBUIScale.scale(ty).toDouble()
        )
    }