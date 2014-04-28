package org.hanuna.image

import org.opencv.photo.Photo

/**
 * @author erokhins
 */


fun Image.fastNlMeansDenoising(): MutableImage {
    val image = newEmptyImage()
    val myMat = image.toMat()
    Photo.fastNlMeansDenoising(toMat(), myMat)
    myMat.flush()
    return image
}