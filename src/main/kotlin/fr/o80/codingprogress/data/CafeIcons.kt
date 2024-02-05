package fr.o80.codingprogress.data

import com.intellij.openapi.util.IconLoader
import java.awt.Image
import java.awt.image.BufferedImage
import javax.swing.ImageIcon


object CafeIcons {
    @JvmField
    var HUSKY_LEFT = loadImage("/husky-left.png")

    @JvmField
    var HUSKY_RIGHT = loadImage("/husky-right.png")

    private fun loadImage(filename: String): BufferedImage {
        return IconLoader.toImage(ImageIcon(CafeIcons::class.java.getResource(filename)))
            ?.toBufferedImage()
            ?: error("Cannot load progress bar image from file: $filename")
    }

    private fun Image.toBufferedImage(): BufferedImage {
        if (this is BufferedImage) {
            return this
        }

        return BufferedImage(
            this.getWidth(null),
            this.getHeight(null),
            BufferedImage.TYPE_INT_ARGB
        ).apply {
            createGraphics().apply {
                drawImage(this@toBufferedImage, 0, 0, null)
                dispose()
            }
        }
    }
}
