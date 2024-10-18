package moe.tlaster.nestedscrollview

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Define a [VerticalNestedScrollView].
 *
 * @param state the state object to be used to observe the [VerticalNestedScrollView] state.
 * @param modifier the modifier to apply to this layout.
 * @param content a block which describes the header.
 * @param content a block which describes the content.
 */
@Composable
fun VerticalNestedScrollView(
    state: NestedScrollViewState,
    modifier: Modifier = Modifier,
    contentTopPadding: Dp = 0.dp,
    header: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    NestedScrollView(
        modifier = modifier,
        state = state,
        orientation = Orientation.Vertical,
        header = header,
        content = content,
        contentTopPadding = contentTopPadding,
    )
}

@Composable
private fun NestedScrollView(
    state: NestedScrollViewState,
    orientation: Orientation,
    header: @Composable () -> Unit,
    content: @Composable () -> Unit,
    contentTopPadding: Dp,
    modifier: Modifier = Modifier,
) {
    Layout(
        modifier = modifier
            .scrollable(
                orientation = orientation,
                state = rememberScrollableState { delta ->
                    val initialOffset = state.offset
                    state.offset += delta
                    state.offset - initialOffset
                },
            )
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
                    val bounds = -(headerPlaceable.height.toFloat()) + contentTopPadding.toPx()
                    state.updateBounds(bounds.coerceAtMost(0f))
                    val contentPlaceable =
                        measurables[1].measure(constraints.copy(maxHeight = constraints.maxHeight - contentTopPadding.roundToPx()))
                    contentPlaceable.place(
                        0,
                        state.offset.roundToInt() + headerPlaceable.height,
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
