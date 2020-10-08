package experiment

import java.awt.Color
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun merge(backImage: String, frontImage: String, frontVisible: Int): BufferedImage {
    val backGround = ImageIO.read(File(backImage))
    val front = imageTransparency(frontImage, frontVisible)
    val g = backGround.createGraphics()

    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.drawImage(front, 0, 0, null)
    g.dispose()
    return backGround
}

fun alterImageColor(inputImage: String, subtractColor: Int): BufferedImage {
    val bImage = ImageIO.read(File(inputImage))
    if (subtractColor <= 0) return bImage

    for (i in 0 until bImage.width) {
        for (j in 0 until bImage.height) {
            var color = Color(bImage.getRGB(i, j))
            val red = if (color.red > subtractColor) color.red - subtractColor else subtractColor - color.red
            val green = if (color.green > subtractColor) color.green - subtractColor else subtractColor - color.green
            val blue = if (color.blue > subtractColor) color.blue - subtractColor else subtractColor - color.blue

            color = Color(red, green, blue)
            bImage.setRGB(i, j, color.rgb)
        }
    }
    return bImage
}

fun imageTransparency(imageFile: String, transparency: Int): BufferedImage {
    val original = ImageIO.read(File(imageFile))
    val copy = BufferedImage(original.width, original.height, BufferedImage.TYPE_INT_ARGB)

    for (i in 0 until copy.width) {
        for (j in 0 until copy.height) {
            var color = Color(original.getRGB(i, j), true)

            color = Color(color.red, color.green, color.blue, transparency)
            copy.setRGB(i, j, color.rgb)
        }
    }
    return copy
}

fun checkTransparency(imageFile: String, x: Int, y: Int) {
    val checkImage = ImageIO.read(File(imageFile))
    val color = Color(checkImage.getRGB(x, y), true)
    println(color.alpha)
}