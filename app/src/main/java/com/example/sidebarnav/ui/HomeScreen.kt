package com.example.sidebarnav.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
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
import kotlinx.coroutines.launch

data class MenuItem(
    val label: String,
    val icon: ImageVector,
    val action: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    loggedIn: Boolean,
    onShowLogin: () -> Unit,
    onShowMemo: () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var content by remember { mutableStateOf("🏠 主页\n\n欢迎使用侧边栏应用\nMaterial You 动态取色") }

    val menuItems = listOf(
        MenuItem("主页", Icons.Default.Home) { content = "🏠 主页\n\n欢迎使用侧边栏应用\nMaterial You 动态取色" },
        MenuItem("备忘录", Icons.Default.Book) { onShowMemo() },
        MenuItem("关于", Icons.Default.Info) { content = "ℹ️ 关于\n\n侧边栏示例\nMaterial You · API 31\n服务端: Python Flask" },
        MenuItem(if (loggedIn) "退出登录" else "登录/注册", Icons.Default.AccountCircle) {
            if (loggedIn) {
                com.example.sidebarnav.data.Session.get().logout()
            } else {
                onShowLogin()
            }
        }
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(16.dp))
                Text(
                    "☰ Sidebar App",
                    Modifier.padding(16.dp),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
                menuItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.label) },
                        selected = false,
                        onClick = {
                            // 先关抽屉(带动画)，抽屉关完后再执行动作
                            scope.launch {
                                drawerState.close()
                                // 等抽屉完全关闭后执行内容切换，避免被吞动画
                                item.action()
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = null) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("侧边栏应用") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
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
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                // 内容切换加淡入淡出动画，避免生硬跳变
                AnimatedContent(
                    targetState = content,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
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
    }
}
