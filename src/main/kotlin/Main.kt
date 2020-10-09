import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.pow
import kotlin.math.sqrt

var INPUT_IMAGE = ""
var OUTPUT_IMAGE = ""

fun main(args: Array<String>) {
    if (commandsOkay(args)) saveImage(energy())
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

fun energy(): BufferedImage {
    val bImage = ImageIO.read(File(INPUT_IMAGE))
    val energy = Array(bImage.width) { Array(bImage.height) { 0.0 } }
    var maxEnergy = 0.0

    for (i in 0 until bImage.width) {
        for (j in 0 until bImage.height) {
            val x = if (i == 0) 1 else if (i == bImage.width - 1) bImage.width - 2 else i
            val y = if (j == 0) 1 else if (j == bImage.height - 1) bImage.height - 2 else j
            val left = Color(bImage.getRGB(x - 1, j))
            val right = Color(bImage.getRGB(x + 1, j))
            val up = Color(bImage.getRGB(i, y - 1))
            val down = Color(bImage.getRGB(i, y + 1))
            val xDif = (left.red.toDouble() - right.red).pow(2) +
                    (left.green.toDouble() - right.green).pow(2) + (left.blue.toDouble() - right.blue).pow(2)
            val yDif = (up.red.toDouble() - down.red).pow(2) +
                    (up.green.toDouble() - down.green).pow(2) + (up.blue.toDouble() - down.blue).pow(2)
            val energyNum = sqrt(xDif + yDif)

            if (energyNum > maxEnergy) maxEnergy = energyNum
            energy[i][j] = energyNum
        }
    }
    for (i in 0 until bImage.width) {
        for (j in 0 until bImage.height) {
            val intensity = (255.0 * energy[i][j] / maxEnergy).toInt()
            val color = Color(intensity, intensity, intensity)
            bImage.setRGB(i, j, color.rgb)
        }
    }
    return bImage
}

fun saveImage(image: BufferedImage) = ImageIO.write(image, "png", File(OUTPUT_IMAGE))