# NestedScrollView  
[![](https://jitpack.io/v/Tlaster/NestedScrollView.svg)](https://jitpack.io/#Tlaster/NestedScrollView)  

Let you have `NestedScrollView` in Jetpack Compose 

<img src="image/image.webp" height=400>

# Usage
Add Jitpack
```
maven { url 'https://jitpack.io' }
```
Add the dependency
```
implementation "com.github.Tlaster:NestedScrollView:$version_nestedscrollview"
```
Example
```kotlin
val nestedScrollViewState = rememberNestedScrollViewState()
VerticalNestedScrollView(
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
                        onClick = {}
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
```

# License
```
MIT License

Copyright (c) 2021 Tlaster

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
