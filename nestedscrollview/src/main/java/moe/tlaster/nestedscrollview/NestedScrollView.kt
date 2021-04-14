package moe.tlaster.nestedscrollview

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun VerticalNestedScrollView(
    modifier: Modifier = Modifier,
    state: NestedScrollViewState,
    header: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    NestedScrollView(
        modifier = modifier,
        state = state,
        orientation = Orientation.Vertical,
        header = header,
        content = content,
    )
}

// TODO
@Composable
private fun HorizontalNestedScrollView(
    modifier: Modifier = Modifier,
    state: NestedScrollViewState,
    header: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    NestedScrollView(
        modifier = modifier,
        state = state,
        orientation = Orientation.Horizontal,
        header = header,
        content = content,
    )
}

@Composable
private fun NestedScrollView(
    modifier: Modifier = Modifier,
    state: NestedScrollViewState,
    orientation: Orientation,
    header: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    Layout(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        val amount = when (orientation) {
                            Orientation.Vertical -> dragAmount.y
                            Orientation.Horizontal -> dragAmount.x
                        }
                        if (state.drag(amount) != 0f) {
                            change.consumePositionChange()
                            state.headerState.addPosition(
                                change.uptimeMillis,
                                change.position,
                            )
                        }
                    },
                    onDragEnd = {
                        scope.launch {
                            val velocity = state.headerState.dragEnd()
                            val flingVelocity = when (orientation) {
                                Orientation.Vertical -> velocity.y
                                Orientation.Horizontal -> velocity.x
                            }
                            state.fling(flingVelocity)
                        }
                    },
                    onDragCancel = {
                        state.headerState.resetTracking()
                    }
                )
            }
            .nestedScroll(state.nestedScrollConnectionHolder),
        content = {
            Box {
                header.invoke()
            }
            Box {
                content.invoke()
            }
        },
    ) { measurables, constraints ->
        layout(constraints.maxWidth, constraints.maxHeight) {
            when (orientation) {
                Orientation.Vertical -> {
                    val headerPlaceable =
                        measurables[0].measure(constraints.copy(maxHeight = Constraints.Infinity))
                    headerPlaceable.place(0, state.offset.roundToInt())
                    state.updateBounds(-(headerPlaceable.height.toFloat()))
                    val contentPlaceable =
                        measurables[1].measure(constraints.copy(maxHeight = constraints.maxHeight))
                    contentPlaceable.place(
                        0,
                        state.offset.roundToInt() + headerPlaceable.height
                    )
                }
                Orientation.Horizontal -> {
                    val headerPlaceable =
                        measurables[0].measure(constraints.copy(maxWidth = Constraints.Infinity))
                    headerPlaceable.place(state.offset.roundToInt(), 0)
                    state.updateBounds(-(headerPlaceable.width.toFloat()))
                    val contentPlaceable =
                        measurables[1].measure(constraints.copy(maxWidth = constraints.maxWidth))
                    contentPlaceable.place(
                        state.offset.roundToInt() + headerPlaceable.width,
                        0,
                    )
                }
            }
        }
    }
}
