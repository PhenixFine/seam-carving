import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

var INPUT_IMAGE = ""
var OUTPUT_IMAGE = ""

fun main(args: Array<String>) {
    if (commandsOkay(args)) saveImage(negativeImage())
}

fun commandsOkay(args: Array<String>): Boolean {
    for (i in args.indices) {
        val last = i == args.lastIndex

        when (args[i]) {
            "-in" -> if (last || !File(args[i + 1]).isFile) {
                println("${args[i + 1]} is not a file.")
                return false
            } else INPUT_IMAGE = args[i + 1]
            "-out" -> if (last) {
                println("A filename was not given.")
                return false
            } else try {
                File(args[i + 1]).writeText("")
                OUTPUT_IMAGE = args[i + 1]
            } catch (e: Exception) {
                println(
                    "There was an error in writing to your file. Please ensure it is not set to read only or open" +
                            "in another program."
                )
            }
        }
    }
    if (INPUT_IMAGE == "") println("Missing -in image filename to invert color of.")
    if (OUTPUT_IMAGE == "") println("Missing -out filename to save the negative image.")
    return !(INPUT_IMAGE == "" || OUTPUT_IMAGE == "")
}

fun negativeImage(): BufferedImage {
    val bImage = ImageIO.read(File(INPUT_IMAGE))

    for (i in 0 until bImage.width) {
        for (j in 0 until bImage.height) {
            var color = Color(bImage.getRGB(i, j))

            color = Color(255 - color.red, 255 - color.green, 255 - color.blue)
            bImage.setRGB(i, j, color.rgb)
        }
    }
    return bImage
}

fun saveImage(image: BufferedImage) = ImageIO.write(image, "png", File(OUTPUT_IMAGE))