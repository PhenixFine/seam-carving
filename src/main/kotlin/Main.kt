import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun main() {
    val width = getNum("Enter rectangle width:")
    val height = getNum("Enter rectangle height:")
    val fileName = getString("Enter output image name:")

    saveImage(createImage(width, height), fileName)
}

fun createImage(width: Int, height: Int): BufferedImage {
    val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val g = bufferedImage.graphics

    g.color = Color.red
    g.drawLine(0, 0, width - 1, height - 1)
    g.drawLine(width - 1, 0, 0, height - 1)
    return bufferedImage
}

fun saveImage(image: BufferedImage, file: String) = ImageIO.write(image, "png", File(file))

fun getNum(text: String, defaultMessage: Boolean = false): Int {
    val strErrorNum = " was not a number, please try again: "
    var num = text
    var default = defaultMessage

    do {
        num = getString(if (default) num + strErrorNum else num)
        if (!default) default = true
    } while (!isNumber(num))

    return num.toInt()
}

fun getString(text: String): String {
    println(text)
    return readLine()!!
}

fun isNumber(number: String) = number.toIntOrNull() != null