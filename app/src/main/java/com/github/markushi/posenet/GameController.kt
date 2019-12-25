package com.github.markushi.posenet

import androidx.lifecycle.MutableLiveData
import com.github.markushi.posenet.domain.Pose
import java.util.*

class GameController {

    companion object {
        const val MAX_WALL_DIFF = 0.06f
    }

    sealed class State {
        object Ready : State()
        data class Running(
            val pointsText: String,
            val remainingTimeText: String,
            val wallPose: Pose,
            val userPose: Pose?,
            val wallDiff: Float
        ) : State()

        class Over(val pointsText: String) : State()
    }

    val state = MutableLiveData<State>(State.Ready)

    /**
     * remaining time in seconds
     */
    private var remainingTime = 0
    private var points: Int = 0
    private var userPose: Pose? = null
    private var wallPose: Pose? = null
    private var wallIdx = 0
    private var wallDiff: Float = 1f
    private var timer: Timer? = null

    private var walls = listOf(
        Pose.from(
            0.521f,
            0.134f,
            0.079f,
            0.514f,
            0.268f,
            0.500f,
            0.582f,
            0.215f,
            0.299f,
            0.000f,
            0.299f,
            0.368f,
            0.820f,
            0.333f,
            1.000f,
            0.799f,
            0.304f,
            0.979f,
            0.284f,
            0.611f,
            0.835f,
            0.632f,
            0.995f
        ),
        Pose.from(
            0.420f,
            0.150f,
            0.084f,
            0.470f,
            0.257f,
            0.500f,
            0.593f,
            0.300f,
            0.407f,
            0.200f,
            0.514f,
            0.320f,
            0.807f,
            0.320f,
            0.979f,
            0.820f,
            0.157f,
            1.000f,
            0.121f,
            0.620f,
            0.800f,
            0.700f,
            1.000f
        ),
        Pose.from(
            0.533f,
            0.114f,
            0.079f,
            0.533f,
            0.228f,
            0.500f,
            0.544f,
            0.256f,
            0.329f,
            0.233f,
            0.409f,
            0.322f,
            0.772f,
            0.267f,
            0.966f,
            0.944f,
            0.295f,
            1.000f,
            0.128f,
            0.678f,
            0.772f,
            0.733f,
            1.000f
        ),
        Pose.from(
            0.500f,
            0.103f,
            0.082f,
            0.488f,
            0.212f,
            0.500f,
            0.541f,
            0.000f,
            0.212f,
            0.200f,
            0.110f,
            0.363f,
            0.795f,
            0.300f,
            0.993f,
            1.000f,
            0.219f,
            0.663f,
            0.123f,
            0.613f,
            0.788f,
            0.600f,
            1.000f
        )
    )

    private fun updateRunningState() {
        val minutes = remainingTime / 60
        val seconds = remainingTime % 60
        val remainingTimeText = String.format(Locale.US, "%02d:%02d", minutes, seconds)
        state.postValue(
            State.Running(
                "$points Points",
                remainingTimeText,
                wallPose!!,
                userPose,
                wallDiff
            )
        )
    }

    private fun moveToStopState() {
        state.postValue(State.Over("$points"))
    }

    private fun startGame() {
        timer?.cancel()

        points = 0
        walls = walls.shuffled()
        wallIdx = 0
        wallPose = walls[wallIdx]
        remainingTime = 30
        timer = Timer("game-countdown").apply {
            scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    if (remainingTime <= 0) {
                        this@apply.cancel()
                        moveToStopState()
                    } else {
                        remainingTime--
                        updateRunningState()
                    }
                }
            }, 1000, 1000)
        }
        updateRunningState()
    }

    fun stopGame() {
        timer?.cancel()
        moveToStopState()
    }

    fun onUserPose(pose: Pose) {
        userPose = pose

        // Raising hands to launch the game
        if (state.value == State.Ready || state.value is State.Over) {
            if (pose.leftWrist.y < 0.1f && pose.rightWrist.y < 0.1f) {
                startGame()
            }
            return
        }

        // No more time
        if (remainingTime <= 0) {
            return
        }


        // Compare wall to user pose
        wallDiff = wallPose?.diffTo(pose) ?: 1f
        if (wallDiff <= MAX_WALL_DIFF) {
            points++
            wallIdx = (wallIdx + 1) % walls.size
            wallPose = walls[wallIdx]
        }
        updateRunningState()
    }
}