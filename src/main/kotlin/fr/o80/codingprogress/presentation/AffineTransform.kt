package fr.o80.codingprogress.presentation

import java.awt.geom.AffineTransform

fun affineTransform(tx: Float, ty: Float, sx: Float, sy: Float): AffineTransform =
    AffineTransform().apply {
        scale(
            sx.toDouble(),
            sy.toDouble()
        )
        translate(
            tx.toDouble(),
            ty.toDouble()
        )
    }