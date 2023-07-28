package moe.tlaster.nestedscrollview.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import moe.tlaster.nestedscrollview.VerticalNestedScrollView
import moe.tlaster.nestedscrollview.rememberNestedScrollViewState

class MainActivity : ComponentActivity() {
    @ExperimentalPagerApi
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun App() {
    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "VerticalNestedScrollView")
                    },
                    elevation = 0.dp
                )
            }
        ) {
            val scope = rememberCoroutineScope()
            val nestedScrollViewState = rememberNestedScrollViewState()
            VerticalNestedScrollView(
                modifier = Modifier.padding(it),
                state = nestedScrollViewState,
                header = {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = MaterialTheme.colors.primary,
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(text = "This is some awesome title")
                            Text(text = "This is some awesome title")
                            Text(text = "This is some awesome title")
                            Text(text = "This is some awesome title")
                            Text(text = "This is some awesome title")
                            Text(text = "This is some awesome title")
                            Text(text = "This is some awesome title")
                            Text(text = "This is some awesome title")
                            Text(text = "This is some awesome title")
                            Text(text = "This is some awesome title")
                            Text(text = "This is some awesome title")
                            Text(text = "This is some awesome title")
                            Text(text = "This is some awesome title")
                            Text(text = "This is some awesome title")
                        }
                    }
                },
                content = {
                    val pagerState = rememberPagerState(pageCount = 10)
                    val pages = (0..4).map { it }
                    Column {
                        TabRow(
                            selectedTabIndex = pagerState.currentPage,
                            indicator = { tabPositions ->
                                TabRowDefaults.Indicator(
                                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                                )
                            }
                        ) {
                            pages.forEachIndexed { index, title ->
                                Tab(
                                    text = { Text(text = "tab $title") },
                                    selected = pagerState.currentPage == index,
                                    onClick = {
                                        scope.launch {
                                            pagerState.animateScrollToPage(index)
                                        }
                                    },
                                )
                            }
                        }
                        HorizontalPager(
                            modifier = Modifier.weight(1f),
                            state = pagerState
                        ) {
                            LazyColumn {
                                items(100) {
                                    ListItem {
                                        Text(text = "item $it")
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}
