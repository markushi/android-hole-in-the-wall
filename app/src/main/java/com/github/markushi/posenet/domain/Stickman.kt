package com.github.markushi.posenet.domain

import org.tensorflow.lite.examples.posenet.lib.BodyPart
import org.tensorflow.lite.examples.posenet.lib.Person
import org.tensorflow.lite.examples.posenet.lib.Position
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

class Stickman(
    val headX: Float,
    val headY: Float,
    val headRadius: Float,
    val shoulderX: Float,
    val shoulderY: Float,
    val hipX: Float,
    val hipY: Float,
    val leftElbowX: Float,
    val leftElbowY: Float,
    val leftWristX: Float,
    val leftWristY: Float,
    val leftKneeX: Float,
    val leftKeeY: Float,
    val leftAnkleX: Float,
    val leftAnkleY: Float,
    val rightElbowX: Float,
    val rightElbowY: Float,
    val rightWristX: Float,
    val rightWristY: Float,
    val rightKneeX: Float,
    val rightKeeY: Float,
    val rightAnkleX: Float,
    val rightAnkleY: Float
) {

    companion object {

        /**
         * Euclidean distance in 2d space
         */
        private fun distance2d(x0: Float, y0: Float, x1: Float, y1: Float) =
            sqrt((x1 - x0).pow(2) + (y1 - y0).pow(2))

        fun from(
            person: Person
        ): Stickman? {
            var nose: Position? = null
            var leftShoulder: Position? = null
            var rightShoulder: Position? = null
            var leftElbow: Position? = null
            var leftWrist: Position? = null
            var leftKnee: Position? = null
            var leftAnkle: Position? = null
            var leftHip: Position? = null
            var rightElbow: Position? = null
            var rightWrist: Position? = null
            var rightKnee: Position? = null
            var rightAnkle: Position? = null
            var rightHip: Position? = null

            val outputWidth = 1f
            val outputHeight = 1f

            for (keyPoint in person.keyPoints) {
                val position = keyPoint.position
                when (keyPoint.bodyPart) {
                    BodyPart.NOSE -> nose = position.copy()
                    BodyPart.LEFT_SHOULDER -> leftShoulder = position.copy()
                    BodyPart.RIGHT_SHOULDER -> rightShoulder = position.copy()
                    BodyPart.LEFT_ELBOW -> leftElbow = position.copy()
                    BodyPart.RIGHT_ELBOW -> rightElbow = position.copy()
                    BodyPart.LEFT_WRIST -> leftWrist = position.copy()
                    BodyPart.RIGHT_WRIST -> rightWrist = position.copy()
                    BodyPart.LEFT_HIP -> leftHip = position.copy()
                    BodyPart.RIGHT_HIP -> rightHip = position.copy()
                    BodyPart.LEFT_KNEE -> leftKnee = position.copy()
                    BodyPart.RIGHT_KNEE -> rightKnee = position.copy()
                    BodyPart.LEFT_ANKLE -> leftAnkle = position.copy()
                    BodyPart.RIGHT_ANKLE -> rightAnkle = position.copy()
                    else -> Unit
                }
            }

            val items = listOfNotNull(
                nose,
                Position(nose!!.x, nose.y - abs(nose.y - leftShoulder!!.y)),
                leftShoulder,
                rightShoulder,
                leftElbow,
                leftWrist,
                leftKnee,
                leftAnkle,
                leftHip,
                rightElbow,
                rightWrist,
                rightKnee,
                rightAnkle,
                rightHip
            )

            // mirror x coordinates
            for (item in items) {
                item.x = person.width - item.x
            }

            val centerHip = Position(
                (leftHip!!.x + rightHip!!.x) / 2,
                (leftHip.y + rightHip.y) / 2
            )

            val centerShoulder = Position(
                (leftShoulder.x + rightShoulder!!.x) / 2,
                (leftShoulder.y + rightShoulder.y) / 2
            )

            val xRange = MinMaxInt()
            val yRange = MinMaxInt()
            for (item in items) {
                xRange.update(item.x)
                yRange.update(item.y)
            }

            val xOffset = -xRange.min.toFloat()
            val yOffset = -yRange.min.toFloat()

            // we want the center hip to be exactly in the x center of the output
            val scaleX = (outputWidth / 2f) / max(
                abs(centerHip.x - xRange.min),
                abs(xRange.max - centerHip.x)
            )
            val scaleY = outputHeight / yRange.range()

            val postTranslateX = (outputWidth / 2f) - (scaleX * (centerHip.x - xRange.min))

            val transformer =
                Transformer(
                    xOffset,
                    yOffset,
                    scaleX,
                    scaleY,
                    postTranslateX
                )

            val headSize = distance2d(
                transformer.x(centerHip),
                transformer.y(centerHip),
                transformer.x(centerShoulder),
                transformer.y(centerShoulder)
            ) / 4f

            // mirror output
            return Stickman(
                transformer.x(nose),
                transformer.y(nose),
                headSize,
                transformer.x(centerShoulder),
                transformer.y(centerShoulder),
                transformer.x(centerHip),
                transformer.y(centerHip),
                transformer.x(leftElbow!!),
                transformer.y(leftElbow),
                transformer.x(leftWrist!!),
                transformer.y(leftWrist),
                transformer.x(leftKnee!!),
                transformer.y(leftKnee),
                transformer.x(leftAnkle!!),
                transformer.y(leftAnkle),
                transformer.x(rightElbow!!),
                transformer.y(rightElbow),
                transformer.x(rightWrist!!),
                transformer.y(rightWrist),
                transformer.x(rightKnee!!),
                transformer.y(rightKnee),
                transformer.x(rightAnkle!!),
                transformer.y(rightAnkle)
            )
        }
    }

    class Transformer(
        private val xOffset: Float,
        private val yOffset: Float,
        private val scaleX: Float,
        private val scaleY: Float,
        private val postTranslateX: Float
    ) {
        fun x(position: Position) = ((position.x + xOffset) * scaleX) + postTranslateX
        fun y(position: Position) = (position.y + yOffset) * scaleY
    }

    class MinMaxInt {
        var min: Int = Int.MAX_VALUE
        var max: Int = Int.MIN_VALUE

        fun update(value: Int) {
            if (value < min) {
                min = value
            }
            if (value > max) {
                max = value
            }
        }

        fun range() = max - min
    }
}