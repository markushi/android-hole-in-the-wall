package com.github.markushi.posenet.domain

import android.util.Log
import org.tensorflow.lite.examples.posenet.lib.BodyPart
import org.tensorflow.lite.examples.posenet.lib.Person
import org.tensorflow.lite.examples.posenet.lib.Position
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

class Pose(
    val head: PositionFloat,
    val headRadius: Float,
    val shoulder: PositionFloat,
    val hip: PositionFloat,
    val leftElbow: PositionFloat,
    val leftWrist: PositionFloat,
    val leftKnee: PositionFloat,
    val leftAnkle: PositionFloat,
    val rightElbow: PositionFloat,
    val rightWrist: PositionFloat,
    val rightKnee: PositionFloat,
    val rightAnkle: PositionFloat
) {

    private fun positions() = listOf(
        head,
        shoulder,
        hip,
        leftElbow,
        leftWrist,
        leftKnee,
        rightElbow,
        rightWrist,
        rightKnee
    )

    fun diffTo(other: Pose): Float {
        val positions = positions()
        val otherPositions = other.positions()

        var totalDistance = 0.0f
        for (i in 0 until positions.size) {
            totalDistance += distance2d(positions[i], otherPositions[i])
        }
        return totalDistance / positions.size
    }

    companion object {

        /**
         * Euclidean distance in 2d space
         */
        private fun distance2d(p0: PositionFloat, p1: PositionFloat) =
            sqrt((p1.x - p0.x).pow(2) + (p1.y - p0.y).pow(2))

        fun from(vararg floats: Float): Pose {
            return Pose(
                PositionFloat(floats[0], floats[1]),
                floats[2],
                PositionFloat(floats[3], floats[4]),
                PositionFloat(floats[5], floats[6]),
                PositionFloat(floats[7], floats[8]),
                PositionFloat(floats[9], floats[10]),
                PositionFloat(floats[11], floats[12]),
                PositionFloat(floats[13], floats[14]),
                PositionFloat(floats[15], floats[16]),
                PositionFloat(floats[17], floats[18]),
                PositionFloat(floats[19], floats[20]),
                PositionFloat(floats[21], floats[22])
            )
        }

        fun from(
            person: Person
        ): Pose? {
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
                    postTranslateX,
                    0f
                )

            val headSize = distance2d(
                transformer.position(centerHip),
                transformer.position(centerShoulder)
            ) / 4f

            return Pose(
                transformer.position(nose),
                headSize,
                transformer.position(centerShoulder),
                transformer.position(centerHip),
                transformer.position(leftElbow!!),
                transformer.position(leftWrist!!),
                transformer.position(leftKnee!!),
                transformer.position(leftAnkle!!),
                transformer.position(rightElbow!!),
                transformer.position(rightWrist!!),
                transformer.position(rightKnee!!),
                transformer.position(rightAnkle!!)
            )
        }
    }

    class Transformer(
        private val preTranslateX: Float,
        private val preTranslateY: Float,
        private val scaleX: Float,
        private val scaleY: Float,
        private val postTranslateX: Float,
        private val postTranslateY: Float
    ) {
        private fun x(position: Position) = ((position.x + preTranslateX) * scaleX) + postTranslateX
        private fun y(position: Position) = ((position.y + preTranslateY) * scaleY) + postTranslateY
        fun position(position: Position) = PositionFloat(x(position), y(position))
    }

    class PositionFloat(val x: Float, val y: Float)

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