package fr.o80.progress

import com.intellij.ui.Gray
import com.intellij.ui.JBColor
import com.intellij.ui.scale.JBUIScale
import com.intellij.util.ui.GraphicsUtil
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import sun.swing.SwingUtilities2
import java.awt.*
import java.awt.event.ComponentAdapter
import java.awt.geom.AffineTransform
import java.awt.geom.Area
import java.awt.geom.Rectangle2D
import java.awt.geom.RoundRectangle2D
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.SwingConstants
import javax.swing.plaf.ComponentUI
import javax.swing.plaf.basic.BasicProgressBarUI

class CodingCafeProgressBarUI : BasicProgressBarUI() {

    @Volatile
    private var offset = 0

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
                Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.cyan, Color.blue, VIOLET
            )
        )
        return Dimension(super.getPreferredSize(c).width, JBUI.scale(20))
    }

    override fun installListeners() {
        super.installListeners()
        progressBar.addComponentListener(object : ComponentAdapter() {
        })
    }

    override fun paintDeterminate(g2d: Graphics?, c: JComponent?) {
        val g = g2d as? Graphics2D ?: return
        c ?: return

        if (progressBar.orientation != SwingConstants.HORIZONTAL || !c.componentOrientation.isLeftToRight) {
            super.paintDeterminate(g, c)
            return
        }
        val config = GraphicsUtil.setupAAPainting(g)
        val b = progressBar.insets // area for border

        val w = progressBar.width
        var h = progressBar.preferredSize.height
        if (!isEven(c.height - h)) h++

        val barRectWidth = w - (b.right + b.left)
        val barRectHeight = h - (b.top + b.bottom)

        if (barRectWidth <= 0 || barRectHeight <= 0) {
            return
        }

        val amountFull = getAmountFull(b, barRectWidth, barRectHeight)

        val parent = c.parent
        val background = if (parent != null) parent.background else UIUtil.getPanelBackground()

        g.color = background
        if (c.isOpaque) {
            g.fillRect(0, 0, w, h)
        }

        val r = JBUIScale.scale(8f)
        val r2 = JBUIScale.scale(9f)
        val off = JBUIScale.scale(1f)

        g.translate(0, (c.height - h) / 2)
        g.color = progressBar.foreground
        g.fill(RoundRectangle2D.Float(0f, 0f, w - off, h - off, r2, r2))
        g.color = background
        g.fill(RoundRectangle2D.Float(off, off, w - 2f * off - off, h - 2f * off - off, r, r))
        g.paint = paintLoadedBackground
        CafeIcons.HUSKY_LEFT.paintIcon(progressBar, g, amountFull - JBUI.scale(10), -JBUI.scale(6))
        g.fill(
            RoundRectangle2D.Float(
                2f * off,
                2f * off,
                amountFull - JBUIScale.scale(5f),
                h - JBUIScale.scale(5f),
                JBUIScale.scale(7f),
                JBUIScale.scale(7f)
            )
        )
        g.translate(0, -(c.height - h) / 2)

        // Deal with possible text painting
        if (progressBar.isStringPainted) {
            paintString(
                g, b.left, b.top,
                barRectWidth, barRectHeight,
                amountFull, b
            )
        }
        config.restore()
    }

    override fun paintIndeterminate(g2d: Graphics?, c: JComponent?) {
        val g = g2d as? Graphics2D ?: return
        c ?: return

        val b = progressBar.insets // area for border

        val barRectWidth = progressBar.width - (b.right + b.left)
        val barRectHeight = progressBar.height - (b.top + b.bottom)

        if (barRectWidth <= 0 || barRectHeight <= 0) {
            return
        }

        g.color = JBColor(Gray._240.withAlpha(50), Gray._128.withAlpha(50))
        val w = c.width
        var h = c.preferredSize.height
        if (!isEven(c.height - h)) h++

        g.paint = paintLoadedBackground

        if (c.isOpaque) {
            g.fillRect(0, (c.height - h) / 2, w, h)
        }
        g.color = JBColor(Gray._165.withAlpha(50), Gray._88.withAlpha(50))
        val config = GraphicsUtil.setupAAPainting(g)
        g.translate(0, (c.height - h) / 2)

        val old = g.paint
        g.paint = paintLoadedBackground

        val r = JBUIScale.scale(8f)
        val r2 = JBUIScale.scale(9f)
        val containingRoundRect = Area(RoundRectangle2D.Float(1f, 1f, w - 2f, h - 2f, r, r))
        g.fill(containingRoundRect)
        g.paint = old
        offset = (offset + 1) % getPeriodLength()
        offset2 += velocity
        if (offset2 <= 2) {
            offset2 = 2
            velocity = 1
        } else if (offset2 >= w - JBUI.scale(15)) {
            offset2 = w - JBUI.scale(15)
            velocity = -1
        }
        val area = Area(
            Rectangle2D.Float(
                0f, 0f, w.toFloat(),
                h.toFloat()
            )
        )
        area.subtract(Area(RoundRectangle2D.Float(1f, 1f, w - 2f, h - 2f, r, r)))
        g.paint = Gray._128
        if (c.isOpaque) {
            g.fill(area)
        }

        area.subtract(Area(RoundRectangle2D.Float(0f, 0f, w.toFloat(), h.toFloat(), r2, r2)))

        val parent = c.parent
        val background = if (parent != null) parent.background else UIUtil.getPanelBackground()
        g.paint = background
        if (c.isOpaque) {
            g.fill(area)
        }

        val scaledIcon: Icon =
            if (velocity > 0) CafeIcons.HUSKY_LEFT
            else CafeIcons.HUSKY_RIGHt
        scaledIcon.paintIcon(progressBar, g, offset2 - JBUI.scale(10), -JBUI.scale(6))

        g.draw(RoundRectangle2D.Float(1f, 1f, w - 2f - 1f, h - 2f - 1f, r, r))
        g.translate(0, -(c.height - h) / 2)

        // Deal with possible text painting
        if (progressBar.isStringPainted) {
            if (progressBar.orientation == SwingConstants.HORIZONTAL) {
                paintString(g, b.left, b.top, barRectWidth, barRectHeight, boxRect.x, boxRect.width)
            } else {
                paintString(g, b.left, b.top, barRectWidth, barRectHeight, boxRect.y, boxRect.height)
            }
        }
        config.restore()
    }

    override fun getBoxLength(availableLength: Int, otherDimension: Int): Int = availableLength

    private fun paintString(g2d: Graphics, x: Int, y: Int, w: Int, h: Int, fillStart: Int, amountFull: Int) {
        val g = g2d as? Graphics2D ?: return

        val progressString = progressBar.string
        g.font = progressBar.font
        var renderLocation = getStringPlacement(
            g, progressString,
            x, y, w, h
        )
        val oldClip = g.clipBounds

        if (progressBar.orientation == SwingConstants.HORIZONTAL) {
            g.color = selectionBackground
            SwingUtilities2.drawString(
                progressBar, g, progressString,
                renderLocation.x, renderLocation.y
            )
            g.color = selectionForeground
            g.clipRect(fillStart, y, amountFull, h)
            SwingUtilities2.drawString(
                progressBar, g, progressString,
                renderLocation.x, renderLocation.y
            )
        } else { // VERTICAL
            g.color = selectionBackground
            val rotate = AffineTransform.getRotateInstance(Math.PI / 2)
            g.font = progressBar.font.deriveFont(rotate)
            renderLocation = getStringPlacement(
                g, progressString,
                x, y, w, h
            )
            SwingUtilities2.drawString(
                progressBar, g, progressString,
                renderLocation.x, renderLocation.y
            )
            g.color = selectionForeground
            g.clipRect(x, fillStart, w, amountFull)
            SwingUtilities2.drawString(
                progressBar, g, progressString,
                renderLocation.x, renderLocation.y
            )
        }
        g.clip = oldClip
    }

    private fun getPeriodLength(): Int = JBUI.scale(16)

    private fun isEven(value: Int): Boolean = value % 2 == 0

    companion object {

        private const val ONE_OVER_SEVEN = 1f / 7
        private val VIOLET = Color(90, 0, 157)

        @JvmStatic
        @Suppress("ACCIDENTAL_OVERRIDE", "UNUSED")
        fun createUI(c: JComponent): ComponentUI {
            c.border = JBUI.Borders.empty().asUIResource()
            return CodingCafeProgressBarUI()
        }
    }
}
