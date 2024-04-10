package fr.o80.codingprogress.domain

import com.intellij.openapi.components.Service
import com.intellij.util.ui.ImageUtil
import fr.o80.codingprogress.data.CafeIcons
import fr.o80.codingprogress.data.ProgressConfig
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

@Service
class ImageService {
    fun loadImages(
        config: ProgressConfig,
        maxHeight: Int
    ): Pair<BufferedImage, BufferedImage>? {
        if (config.imagePath == "husky") {
            return Pair(CafeIcons.HUSKY_RIGHT, CafeIcons.HUSKY_LEFT)
        }

        val normalImage = loadImage(config.imagePath)?.resized(maxHeight)
        return if (normalImage != null) {
            val flippedImage = normalImage.horizontallyFlipped()
            Pair(normalImage, flippedImage)
        } else {
            null
        }
    }
}

private fun BufferedImage.resized(maxHeight: Int): BufferedImage {
    if (this.height < maxHeight) {
        return this
    }
    val maxWidth = this.width * maxHeight / height

    return BufferedImage(maxWidth, maxHeight, type).apply {
        createGraphics().let { g2d ->
            g2d.drawImage(this, 0, 0, maxWidth, maxHeight, null)
            g2d.dispose()
        }
    }
}

private fun loadImage(imagePath: String?): BufferedImage? {
    imagePath?.takeUnless { it.isBlank() } ?: return null
    return ImageIO.read(File(imagePath))
}

private fun BufferedImage.horizontallyFlipped(): BufferedImage {
    val affineTransform = AffineTransform.getScaleInstance(-1.0, 1.0)
    affineTransform.translate(-width.toDouble(), 0.0)

    val newImage = ImageUtil.createImage(width, height, BufferedImage.TYPE_INT_ARGB)
    val gg = newImage.createGraphics()
    gg.transform(affineTransform)
    gg.drawImage(this, 0, 0, null)
    gg.dispose()
    return newImage
}
