package com.example.sidebarnav.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sidebarnav.data.Session
import kotlinx.coroutines.delay

data class MenuItem(
    val label: String,
    val icon: ImageVector,
    val action: () -> Unit
)

@Composable
fun HomeScreen(
    loggedIn: Boolean,
    onShowLogin: () -> Unit,
    onShowMemo: () -> Unit
) {
    var drawerOpen by remember { mutableStateOf(false) }
    var content by remember { mutableStateOf("🏠 主页\n\n欢迎使用侧边栏应用\nMaterial You 动态取色") }
    var currentItem by remember { mutableStateOf("主页") }

    val menuItems = listOf(
        MenuItem("主页", Icons.Default.Home) { content = "🏠 主页\n\n欢迎使用侧边栏应用\nMaterial You 动态取色" },
        MenuItem("备忘录", Icons.Default.Book) { onShowMemo() },
        MenuItem("关于", Icons.Default.Info) { content = "ℹ️ 关于\n\n侧边栏示例\nMaterial You · API 31\n服务端: Python Flask" },
        MenuItem(if (loggedIn) "退出登录" else "登录/注册", Icons.Default.AccountCircle) {
            if (loggedIn) Session.get().logout()
            else onShowLogin()
        }
    )

    Box(Modifier.fillMaxSize()) {
        // 主内容
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("侧边栏应用") },
                    navigationIcon = {
                        IconButton(onClick = { drawerOpen = true }) {
                            Icon(Icons.Default.Menu, contentDescription = "菜单")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { padding ->
            Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = content,
                    transitionSpec = {
                        (fadeIn(tween(200)) + slideInHorizontally(initialOffsetX = { it / 4 })) togetherWith
                                (fadeOut(tween(200)) + slideOutHorizontally(targetOffsetX = { -it / 4 }))
                    },
                    label = "content"
                ) { text ->
                    Text(
                        text,
                        fontSize = 18.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }
        }

        // 自制抽屉：半透明遮罩 + 侧边面板
        if (drawerOpen) {
            // 遮罩
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f))
                    .clickable { drawerOpen = false }
            )

            // 侧边栏从左侧滑入
            AnimatedVisibility(
                visible = true,
                enter = slideInHorizontally(
                    animationSpec = tween(300),
                    initialOffsetX = { -it }
                ),
                exit = slideOutHorizontally(
                    animationSpec = tween(300),
                    targetOffsetX = { -it }
                ),
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(280.dp),
                    tonalElevation = 16.dp,
                    shape = RoundedCornerShape(topEnd = 0.dp, bottomEnd = 24.dp)
                ) {
                    Column(Modifier.fillMaxSize()) {
                        Spacer(Modifier.height(24.dp))
                        Text(
                            "☰ Sidebar App",
                            Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        HorizontalDivider()
                        Spacer(Modifier.height(8.dp))
                        menuItems.forEach { item ->
                            val selected = currentItem == item.label
                            NavigationDrawerItem(
                                label = { Text(item.label) },
                                selected = selected,
                                onClick = {
                                    currentItem = item.label
                                    drawerOpen = false
                                    // 延迟执行内容切换，让抽屉先关
                                    androidx.compose.runtime.LaunchedEffect(currentItem) {
                                        delay(250)
                                        item.action()
                                    }
                                },
                                icon = { Icon(item.icon, contentDescription = null) },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}
