package fr.o80.codingprogress.presentation.component

import com.intellij.openapi.components.service
import com.intellij.openapi.project.ProjectManager
import com.intellij.ui.JBColor
import com.intellij.ui.scale.JBUIScale
import com.intellij.util.ui.GraphicsUtil
import com.intellij.util.ui.UIUtil
import fr.o80.codingprogress.data.ProgressConfig
import fr.o80.codingprogress.domain.GetConfigByProjectUseCase
import fr.o80.codingprogress.domain.ImageService
import fr.o80.codingprogress.presentation.affineTransform
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Insets
import java.awt.LinearGradientPaint
import java.awt.Paint
import java.awt.event.ComponentAdapter
import java.awt.geom.AffineTransform
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage
import javax.swing.JComponent
import javax.swing.SwingConstants
import javax.swing.plaf.basic.BasicProgressBarUI
import kotlin.math.max
import kotlin.math.min

open class CodingCafeProgressBarUI : BasicProgressBarUI() {

    @Volatile
    private var indeterminateOffset = 0

    @Volatile
    private var velocity = 1

    private var config: ProgressConfig? = null
    private lateinit var paintLoadedBackground: Paint
    private var image: BufferedImage? = null
    private var reversedImage: BufferedImage? = null

    private val getConfigByProject = service<GetConfigByProjectUseCase>()
    private val imageService = service<ImageService>()

    override fun getPreferredSize(c: JComponent?): Dimension {
        config = getConfig()

        val config = config ?: return super.getPreferredSize(c)
        val colors = config.splitColors()
        val images = imageService.loadImages(config)
        image = images?.first
        reversedImage = images?.second

        paintLoadedBackground = LinearGradientPaint(
            0f,
            JBUIScale.scale(2f),
            0f,
            progressBar.height - JBUIScale.scale(6f),
            colors.indices.map { it.toFloat() / (colors.size - 1) }.toFloatArray(),
            colors.toTypedArray()
        )
        val imageHeight = image?.height ?: 20
        return Dimension(super.getPreferredSize(c).width, JBUIScale.scale((imageHeight + SCALED_MARGIN * 4).toInt()))
    }

    override fun installListeners() {
        super.installListeners()
        progressBar.addComponentListener(object : ComponentAdapter() {
        })
    }

    override fun paintDeterminate(g: Graphics?, c: JComponent?) {
        config ?: return super.paintDeterminate(g, c)
        val g2d = g as? Graphics2D ?: return
        c ?: return

        g2d.drawProgressBar(c) { insets, w, h, barRectWidth, barRectHeight ->
            val amountFull = getAmountFull(insets, barRectWidth, barRectHeight)

            g2d.drawBorder(component = c, width = w, height = h)
            g2d.drawProgression(width = amountFull, height = h)
            g2d.drawLoadingImage(
                component = c,
                image = image,
                offset = amountFull.toFloat()
            )
            g2d.drawDeterminedText(
                component = c,
                insets = insets,
                offsetX = amountFull,
                height = h,
                barRectWidth = barRectWidth,
                barRectHeight = barRectHeight
            )
        }
    }

    override fun paintIndeterminate(g: Graphics?, c: JComponent?) {
        config ?: return super.paintIndeterminate(g, c)
        val g2d = g as? Graphics2D ?: return
        c ?: return

        g2d.drawProgressBar(c) { insets, w, h, barRectWidth, barRectHeight ->
            g2d.drawBorder(component = c, width = w, height = h)
            g2d.drawProgression(width = w, height = h)

            val loadingImage: BufferedImage =
                (if (velocity > 0) image else reversedImage)
                    ?: return

            indeterminateOffset += velocity
            if (indeterminateOffset <= loadingImage.width / 2) {
                indeterminateOffset = loadingImage.width / 2
                velocity = 1
            } else if (indeterminateOffset >= w - loadingImage.width / 2) {
                indeterminateOffset = w - loadingImage.width / 2
                velocity = -1
            }

            g2d.drawLoadingImage(
                component = c,
                image = loadingImage,
                offset = indeterminateOffset.toFloat()
            )
            g2d.drawUndeterminedText(
                component = c,
                insets = insets,
                height = h,
                barRectWidth = barRectWidth,
                barRectHeight = barRectHeight
            )
        }
    }

    override fun getBoxLength(availableLength: Int, otherDimension: Int): Int = availableLength

    private fun getConfig(): ProgressConfig? {
        val project = ProjectManager.getInstance().openProjects.firstOrNull()
        return getConfigByProject(project)
    }

    private fun isEven(value: Int): Boolean = value % 2 == 0

    private fun Graphics2D.drawBorder(component: JComponent, height: Int, width: Int) {
        val outsideRadius = JBUIScale.scale(9f)
        val insideRadius = JBUIScale.scale(8f)

        val parent = component.parent
        val background = if (parent != null) parent.background else UIUtil.getPanelBackground()

        translate(0, (component.height - height) / 2)
        color = progressBar.foreground
        fill(
            RoundRectangle2D.Float(
                0f,
                0f,
                width - SCALED_MARGIN,
                height - SCALED_MARGIN,
                outsideRadius,
                outsideRadius
            )
        )

        color = background
        fill(
            RoundRectangle2D.Float(
                SCALED_MARGIN,
                SCALED_MARGIN,
                width - 2f * SCALED_MARGIN - SCALED_MARGIN,
                height - 2f * SCALED_MARGIN - SCALED_MARGIN,
                insideRadius,
                insideRadius
            )
        )
    }

    private fun Graphics2D.drawLoadingImage(component: JComponent, image: BufferedImage?, offset: Float) {
        image ?: return

        val verticalMargin = (component.height - image.height) / 2f
        val horizontalMargin = SCALED_MARGIN * 2 + SCALED_PROGRESSION_RADIUS / 2
        val maxedOffset = min(
            max(horizontalMargin, offset - image.width / 2f),
            component.width - image.width - horizontalMargin
        )

        drawImage(
            image,
            affineTransform(
                tx = maxedOffset,
                ty = verticalMargin,
                sx = 1f,
                sy = 1f
            ),
            null
        )
    }

    private fun Graphics2D.drawProgression(width: Int, height: Int) {
        paint = paintLoadedBackground
        fill(
            RoundRectangle2D.Float(
                2f * SCALED_MARGIN,
                2f * SCALED_MARGIN,
                width - JBUIScale.scale(5f),
                height - JBUIScale.scale(5f),
                SCALED_PROGRESSION_RADIUS,
                SCALED_PROGRESSION_RADIUS
            )
        )
    }

    private fun Graphics2D.drawDeterminedText(
        component: JComponent,
        insets: Insets,
        offsetX: Int,
        height: Int,
        barRectWidth: Int,
        barRectHeight: Int
    ) {
        if (progressBar.isStringPainted) {
            translate(0, -(component.height - height) / 2)

            paintString(
                this, insets.left, insets.top,
                barRectWidth, barRectHeight,
                offsetX, insets
            )
        }
    }

    private fun Graphics2D.drawUndeterminedText(
        component: JComponent,
        insets: Insets,
        height: Int,
        barRectWidth: Int,
        barRectHeight: Int
    ) {
        if (progressBar.isStringPainted) {
            translate(0, -(component.height - height) / 2)
            if (progressBar.orientation == SwingConstants.HORIZONTAL) {
                paintString(insets.left, insets.top, barRectWidth, barRectHeight, boxRect.x, boxRect.width)
            } else {
                paintString(insets.left, insets.top, barRectWidth, barRectHeight, boxRect.y, boxRect.height)
            }
        }
    }

    private fun Graphics2D.paintString(x: Int, y: Int, w: Int, h: Int, fillStart: Int, amountFull: Int) {
        val progressString = progressBar.string
        font = progressBar.font
        var renderLocation = getStringPlacement(
            this, progressString,
            x, y, w, h
        )
        val oldClip = clipBounds

        when (progressBar.orientation) {
            SwingConstants.HORIZONTAL -> {
                color = selectionBackground
                drawString(progressString, renderLocation.x, renderLocation.y)
                color = selectionForeground
                clipRect(fillStart, y, amountFull, h)
                drawString(progressString, renderLocation.x, renderLocation.y)
            }

            SwingConstants.VERTICAL -> {
                color = selectionBackground
                val rotate = AffineTransform.getRotateInstance(Math.PI / 2)
                font = progressBar.font.deriveFont(rotate)
                renderLocation = getStringPlacement(
                    this, progressString,
                    x, y, w, h
                )
                drawString(progressString, renderLocation.x, renderLocation.y)
                color = selectionForeground
                clipRect(x, fillStart, w, amountFull)
                drawString(progressString, renderLocation.x, renderLocation.y)
            }
        }
        clip = oldClip
    }

    private inline fun Graphics2D.drawProgressBar(
        component: JComponent,
        block: (Insets, w: Int, wh: Int, barRectWidth: Int, barRectHeight: Int) -> Unit
    ) {
        if (progressBar.orientation != SwingConstants.HORIZONTAL || !component.componentOrientation.isLeftToRight) {
            super.paintDeterminate(this, component)
            return
        }

        val config = GraphicsUtil.setupAAPainting(this)

        val b = progressBar.insets
        val w = progressBar.width
        var h = progressBar.preferredSize.height
        if (!isEven(component.height - h)) h++

        val barRectWidth = w - (b.right + b.left)
        val barRectHeight = h - (b.top + b.bottom)

        if (barRectWidth <= 0 || barRectHeight <= 0) {
            return
        }

        block(b, w, h, barRectWidth, barRectHeight)
        config.restore()
    }

    companion object {
        private const val MARGIN = 1f
        private val SCALED_MARGIN = JBUIScale.scale(MARGIN)
        private val SCALED_PROGRESSION_RADIUS = JBUIScale.scale(7f)
    }
}

private fun ProgressConfig.splitColors(): List<JBColor> {
    return colors?.split(',')?.map { it.toJBColor() } ?: listOf(JBColor.GRAY)
}

private fun String.toJBColor(): JBColor {
    val colorInt = this.substring(1).toInt(16)
    return JBColor(colorInt, colorInt)
}
