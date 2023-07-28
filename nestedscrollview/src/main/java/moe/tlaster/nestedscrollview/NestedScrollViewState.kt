package moe.tlaster.nestedscrollview

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import kotlin.math.abs

/**
 * Create a [NestedScrollViewState] that is remembered across compositions.
 */
@Composable
fun rememberNestedScrollViewState(
    flingAnimationSpec: DecayAnimationSpec<Float>? = rememberSplineBasedDecay(),
): NestedScrollViewState {
    val saver = remember {
        NestedScrollViewState.Saver(flingAnimationSpec = flingAnimationSpec)
    }
    return rememberSaveable(
        saver = saver,
    ) {
        NestedScrollViewState(flingAnimationSpec = flingAnimationSpec)
    }
}

/**
 * A state object that can be hoisted to observe scale and translate for [NestedScrollView].
 *
 * In most cases, this will be created via [rememberNestedScrollViewState].
 */
@Stable
class NestedScrollViewState(
    initialOffset: Float = 0f,
    initialMaxOffset: Float = 0f,
    val flingAnimationSpec: DecayAnimationSpec<Float>?,
) {
    companion object {
        fun Saver(
            flingAnimationSpec: DecayAnimationSpec<Float>?,
        ): Saver<NestedScrollViewState, *> = listSaver(
            save = {
                listOf(it.offset, it._maxOffset.value)
            },
            restore = {
                NestedScrollViewState(
                    initialOffset = it[0],
                    initialMaxOffset = it[1],
                    flingAnimationSpec = flingAnimationSpec,
                )
            },
        )
    }

    /**
     * The maximum value for [NestedScrollView] Content to translate
     */
    val maxOffset: Float
        get() = _maxOffset.value

    /**
     * The current value for [NestedScrollView] Content translate
     */
    var offset: Float
        get() = _offset.value
        internal set(newOffset) {
            _offset.value = newOffset.coerceIn(
                minimumValue = maxOffset,
                maximumValue = 0f,
            )
        }

    internal val nestedScrollConnectionHolder = object : NestedScrollConnection {

        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            // Don't intercept if scrolling down.
            if (available.y > 0f) return Offset.Zero

            val prevHeightOffset = offset
            offset += available.y
            return if (prevHeightOffset != offset) {
                // We're in the middle of top app bar collapse or expand.
                // Consume only the scroll on the Y axis.
                available.copy(x = 0f)
            } else {
                Offset.Zero
            }
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource,
        ): Offset {
            if (available.y < 0f || consumed.y < 0f) {
                // When scrolling up, just update the state's height offset.
                val oldHeightOffset = offset
                offset += consumed.y
                return Offset(0f, offset - oldHeightOffset)
            }

            if (available.y > 0f) {
                // Adjust the height offset in case the consumed delta Y is less than what was
                // recorded as available delta Y in the pre-scroll.
                val oldHeightOffset = offset
                offset += available.y
                return Offset(0f, offset - oldHeightOffset)
            }
            return Offset.Zero
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            val superConsumed = super.onPostFling(consumed, available)
            return superConsumed + settleNestedScrollView(this@NestedScrollViewState, available.y, flingAnimationSpec)
        }
    }

    // private var changes = 0f
    private var _offset = mutableStateOf(initialOffset)
    private val _maxOffset = mutableStateOf(initialMaxOffset)

    internal fun updateBounds(maxOffset: Float) {
        _maxOffset.value = maxOffset
    }
}

internal suspend fun settleNestedScrollView(state: NestedScrollViewState, velocity: Float, flingAnimationSpec: DecayAnimationSpec<Float>?): Velocity {
    var remainingVelocity = velocity
    // In case there is an initial velocity that was left after a previous user fling, animate to
    // continue the motion to expand or collapse the app bar.
    if (flingAnimationSpec != null && abs(velocity) > 1f) {
        var lastValue = 0f
        AnimationState(
            initialValue = 0f,
            initialVelocity = velocity,
        )
            .animateDecay(flingAnimationSpec) {
                val delta = value - lastValue
                val initialHeightOffset = state.offset
                state.offset = initialHeightOffset + delta
                val consumed = abs(initialHeightOffset - state.offset)
                lastValue = value
                remainingVelocity = this.velocity
                // avoid rounding errors and stop if anything is unconsumed
                if (abs(delta - consumed) > 0.5f) {
                    this.cancelAnimation()
                }
            }
    }
    return Velocity(0f, remainingVelocity)
}
