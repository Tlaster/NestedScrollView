package moe.tlaster.nestedscrollview

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.Velocity

internal class NestedScrollViewHeaderState {
    private val velocityTracker = VelocityTracker()

    fun dragEnd(): Velocity {
        return velocityTracker.calculateVelocity()
    }

    fun addPosition(uptimeMillis: Long, position: Offset) {
        velocityTracker.addPosition(uptimeMillis, position)
    }

    fun resetTracking() {
        velocityTracker.resetTracking()
    }
}