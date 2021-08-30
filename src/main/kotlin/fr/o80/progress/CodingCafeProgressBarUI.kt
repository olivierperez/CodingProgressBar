package fr.o80.progress

import com.intellij.ui.scale.JBUIScale
import com.intellij.util.ui.GraphicsUtil
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import sun.swing.SwingUtilities2
import java.awt.*
import java.awt.event.ComponentAdapter
import java.awt.geom.AffineTransform
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage
import javax.swing.JComponent
import javax.swing.SwingConstants
import javax.swing.plaf.ComponentUI
import javax.swing.plaf.basic.BasicProgressBarUI

class CodingCafeProgressBarUI : BasicProgressBarUI() {

    @Volatile
    private var offset2 = 0

    @Volatile
    private var velocity = 1

    private lateinit var paintLoadedBackground: Paint

    override fun getPreferredSize(c: JComponent?): Dimension {
        paintLoadedBackground = LinearGradientPaint(
            0f,
            JBUIScale.scale(2f),
            0f,
            progressBar.height - JBUIScale.scale(6f),
            floatArrayOf(
                ONE_OVER_SEVEN * 1,
                ONE_OVER_SEVEN * 2,
                ONE_OVER_SEVEN * 3,
                ONE_OVER_SEVEN * 4,
                ONE_OVER_SEVEN * 5,
                ONE_OVER_SEVEN * 6,
                ONE_OVER_SEVEN * 7
            ),
            arrayOf(
                Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.cyan, Color.blue, Color(90, 0, 157)
            )
        )
        return Dimension(super.getPreferredSize(c).width, JBUIScale.scale(20))
    }

    override fun installListeners() {
        super.installListeners()
        progressBar.addComponentListener(object : ComponentAdapter() {
        })
    }

    override fun paintDeterminate(g: Graphics?, c: JComponent?) {
        val g2d = g as? Graphics2D ?: return
        c ?: return

        g2d.drawProgressBar(c) { insets, w, h, barRectWidth, barRectHeight ->
            val amountFull = getAmountFull(insets, barRectWidth, barRectHeight)


            g2d.drawBorder(component = c, width = w, height = h)
            g2d.drawProgression(width = amountFull, height = h)
            g2d.drawLoadingImage(loadingImage = CafeIcons.HUSKY_RIGHT, offset = amountFull)
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
        val g2d = g as? Graphics2D ?: return
        c ?: return

        g2d.drawProgressBar(c) { insets, w, h, barRectWidth, barRectHeight ->
            g2d.drawBorder(component = c, width = w, height = h)
            g2d.drawProgression(width = w, height = h)

            val loadingImage: BufferedImage =
                (if (velocity > 0) CafeIcons.HUSKY_RIGHT else CafeIcons.HUSKY_LEFT) as? BufferedImage
                    ?: return

            offset2 += velocity
            if (offset2 <= loadingImage.width / 2) {
                offset2 = loadingImage.width / 2
                velocity = 1
            } else if (offset2 >= w - loadingImage.width / 2) {
                offset2 = w - loadingImage.width / 2
                velocity = -1
            }

            g2d.drawLoadingImage(
                loadingImage = loadingImage,
                offset = offset2
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

    private fun isEven(value: Int): Boolean = value % 2 == 0

    private fun Graphics2D.drawBorder(component: JComponent, height: Int, width: Int) {
        val outsideRadius = JBUIScale.scale(9f)
        val insideRadius = JBUIScale.scale(8f)

        val parent = component.parent
        val background = if (parent != null) parent.background else UIUtil.getPanelBackground()

        translate(0, (component.height - height) / 2)
        color = progressBar.foreground
        fill(RoundRectangle2D.Float(0f, 0f, width - MARGIN, height - MARGIN, outsideRadius, outsideRadius))

        color = background
        fill(
            RoundRectangle2D.Float(
                MARGIN,
                MARGIN,
                width - 2f * MARGIN - MARGIN,
                height - 2f * MARGIN - MARGIN,
                insideRadius,
                insideRadius
            )
        )
    }

    private fun Graphics2D.drawLoadingImage(loadingImage: Image, offset: Int) {
        drawImage(
            loadingImage,
            affineTransform(
                offsetX = offset,
                tx = -10,
                ty = -2,
                sx = 1,
                sy = 1
            ),
            null
        )
    }

    private fun Graphics2D.drawProgression(width: Int, height: Int) {
        paint = paintLoadedBackground
        fill(
            RoundRectangle2D.Float(
                2f * MARGIN,
                2f * MARGIN,
                width - JBUIScale.scale(5f),
                height - JBUIScale.scale(5f),
                PROGRESSION_RADIUS,
                PROGRESSION_RADIUS
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
                SwingUtilities2.drawString(
                    progressBar, this, progressString,
                    renderLocation.x, renderLocation.y
                )
                color = selectionForeground
                clipRect(fillStart, y, amountFull, h)
                SwingUtilities2.drawString(
                    progressBar, this, progressString,
                    renderLocation.x, renderLocation.y
                )
            }
            SwingConstants.VERTICAL -> {
                color = selectionBackground
                val rotate = AffineTransform.getRotateInstance(Math.PI / 2)
                font = progressBar.font.deriveFont(rotate)
                renderLocation = getStringPlacement(
                    this, progressString,
                    x, y, w, h
                )
                SwingUtilities2.drawString(
                    progressBar, this, progressString,
                    renderLocation.x, renderLocation.y
                )
                color = selectionForeground
                clipRect(x, fillStart, w, amountFull)
                SwingUtilities2.drawString(
                    progressBar, this, progressString,
                    renderLocation.x, renderLocation.y
                )
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

    private fun affineTransform(offsetX: Int, tx: Int, ty: Int, sx: Int, sy: Int): AffineTransform =
        AffineTransform().apply {
            setToScale(
                JBUIScale.scale(sx).toDouble(),
                JBUIScale.scale(sy).toDouble()
            )
            setToTranslation(
                offsetX.toDouble() + JBUIScale.scale(tx).toDouble(),
                JBUIScale.scale(ty).toDouble()
            )
        }

    companion object {

        private const val ONE_OVER_SEVEN = 1f / 7
        private val MARGIN = JBUIScale.scale(1f)
        private val PROGRESSION_RADIUS = JBUIScale.scale(7f)

        @JvmStatic
        @Suppress("ACCIDENTAL_OVERRIDE", "UNUSED")
        fun createUI(c: JComponent): ComponentUI {
            c.border = JBUI.Borders.empty().asUIResource()
            return CodingCafeProgressBarUI()
        }
    }
}


