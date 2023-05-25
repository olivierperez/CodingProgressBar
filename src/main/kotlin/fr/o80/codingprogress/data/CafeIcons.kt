package fr.o80.codingprogress.data

import com.intellij.openapi.util.IconLoader
import java.awt.image.BufferedImage

object CafeIcons {

    var HUSKY_LEFT = loadImage("/husky-left.png")

    var HUSKY_RIGHT = loadImage("/husky-right.png")

    private fun loadImage(filename: String) =
        IconLoader.toImage(IconLoader.getIcon(filename, CafeIcons::class.java)) as? BufferedImage
            ?: error("Cannot load progress bar image from file: $filename")
}
