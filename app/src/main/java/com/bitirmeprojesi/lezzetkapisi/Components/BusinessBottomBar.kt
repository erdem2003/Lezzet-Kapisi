package com.bitirmeprojesi.lezzetkapisi.Components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

// ─── Renk paleti (Mavi ton) ───────────────────────────────────────────────────
private object NavColors {
    val nav_active        = Color(0xFF1D6FD8)
    val nav_inactive      = Color(0xFF9CA3AF)
    val nav_surface       = Color(0xFFFFFFFF)
    val nav_divider       = Color(0xFFF0F0F0)
    val nav_indicator     = Color(0xFFDBEAFD)

    val nav_ai_start         = Color(0xFF1D6FD8)
    val nav_ai_end           = Color(0xFF06B6D4)
    val nav_ai_start_pressed = Color(0xFF1558B0)
    val nav_ai_end_pressed   = Color(0xFF0891B2)
    val nav_ai_shadow        = Color(0x591D6FD8)
    val nav_ai_label_active  = Color(0xFF1D6FD8)

    val popup_bg      = Color(0xFFFFFFFF)
    val popup_shadow  = Color(0x22000000)
    val popup_divider = Color(0xFFEEEEEE)
    val popup_hover   = Color(0xFFEFF6FF)
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val iconSelected: ImageVector = icon
)

// ─── Ana composable ───────────────────────────────────────────────────────────
@Composable
fun BusinessBottomBar(navController: NavController) {

    val items = listOf(
        BottomNavItem("business_feed", "Home",     Icons.Outlined.Home,     Icons.Filled.Home),
        BottomNavItem("search",        "Search",   Icons.Outlined.Search,   Icons.Filled.Search),
        BottomNavItem("settings",      "Settings", Icons.Outlined.Settings, Icons.Filled.Settings),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var menuPopupVisible by remember { mutableStateOf(false) }

    val isMenuSelected = currentRoute == "business_menu_add" || currentRoute == "business_menu_view"

    // ── Tüm wrapper: popup + navbar birlikte, ama navbar sabit yükseklikte ────
    Box(modifier = Modifier.fillMaxWidth()) {

        // ── Navbar — her zaman sabit, popup tarafından itilmez ────────────────
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            color = NavColors.nav_surface,
            shadowElevation = 0.dp
        ) {
            Column {
                HorizontalDivider(thickness = 1.dp, color = NavColors.nav_divider)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .height(66.dp)
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavBarItem(
                        item = items[0],
                        isSelected = currentRoute == items[0].route,
                        onClick = {
                            menuPopupVisible = false
                            navController.navigateSingleTop(items[0].route)
                        }
                    )

                    NavBarItem(
                        item = items[1],
                        isSelected = currentRoute == items[1].route,
                        onClick = {
                            menuPopupVisible = false
                            navController.navigateSingleTop(items[1].route)
                        }
                    )

                    AiChatButton(
                        isSelected = currentRoute == "chatbot",
                        onClick = {
                            menuPopupVisible = false
                            navController.navigateSingleTop("chatbot")
                        }
                    )

                    MenuNavItem(
                        isSelected = isMenuSelected,
                        isPopupOpen = menuPopupVisible,
                        onClick = { menuPopupVisible = !menuPopupVisible }
                    )

                    NavBarItem(
                        item = items[2],
                        isSelected = currentRoute == items[2].route,
                        onClick = {
                            menuPopupVisible = false
                            navController.navigateSingleTop(items[2].route)
                        }
                    )
                }
            }
        }

        // ── Popup — navbar'ın ÜSTÜNDE, onun yüksekliğini etkilemeden ──────────
        // wrapContentSize ile kendi boyutunu alıyor, offset ile navbar'ın üstüne konuyor
        AnimatedVisibility(
            visible = menuPopupVisible,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                // navigationBarsPadding + navbar yüksekliği (66dp) + divider (1dp) + gap (8dp)
                .navigationBarsPadding()
                .padding(bottom = (66 + 1 + 8).dp)
                .zIndex(10f),
            enter = fadeIn(tween(150)) + slideInVertically(tween(150)) { it / 2 },
            exit  = fadeOut(tween(100)) + slideOutVertically(tween(100)) { it / 2 }
        ) {
            MenuPopup(
                onAddClick = {
                    menuPopupVisible = false
                    navController.navigateSingleTop("business_menu_add")
                },
                onViewClick = {
                    menuPopupVisible = false
                    navController.navigateSingleTop("business_menu_view")
                },
                onDismiss = { menuPopupVisible = false }
            )
        }
    }
}

// ─── Standart nav item ────────────────────────────────────────────────────────
@Composable
private fun NavBarItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) NavColors.nav_active else NavColors.nav_inactive,
        animationSpec = tween(durationMillis = 250),
        label = "navItemColor"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) NavColors.nav_indicator else Color.Transparent,
        animationSpec = tween(durationMillis = 250),
        label = "navItemBg"
    )
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "navItemScale"
    )

    Column(
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(
            imageVector = if (isSelected) item.iconSelected else item.icon,
            contentDescription = item.label,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = item.label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            color = contentColor
        )
    }
}

// ─── Menu nav item ────────────────────────────────────────────────────────────
@Composable
private fun MenuNavItem(
    isSelected: Boolean,
    isPopupOpen: Boolean,
    onClick: () -> Unit
) {
    val isActive = isSelected || isPopupOpen

    val contentColor by animateColorAsState(
        targetValue = if (isActive) NavColors.nav_active else NavColors.nav_inactive,
        animationSpec = tween(250),
        label = "menuColor"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isActive) NavColors.nav_indicator else Color.Transparent,
        animationSpec = tween(250),
        label = "menuBg"
    )
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.08f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "menuScale"
    )

    Column(
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(
            imageVector = if (isActive) Icons.Filled.MenuBook else Icons.Outlined.MenuBook,
            contentDescription = "Menu",
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = "Menu",
            fontSize = 10.sp,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium,
            color = contentColor
        )
    }
}

// ─── AI Chat gradient butonu ──────────────────────────────────────────────────
@Composable
private fun AiChatButton(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val gradient = if (isSelected)
        Brush.linearGradient(listOf(NavColors.nav_ai_start_pressed, NavColors.nav_ai_end_pressed))
    else
        Brush.linearGradient(listOf(NavColors.nav_ai_start, NavColors.nav_ai_end))

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "aiScale"
    )

    val labelColor by animateColorAsState(
        targetValue = if (isSelected) NavColors.nav_ai_label_active else NavColors.nav_inactive,
        animationSpec = tween(250),
        label = "aiLabelColor"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier.graphicsLayer { scaleX = scale; scaleY = scale }
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .shadow(
                    elevation = if (isSelected) 6.dp else 10.dp,
                    shape = RoundedCornerShape(18.dp),
                    ambientColor = NavColors.nav_ai_shadow,
                    spotColor = NavColors.nav_ai_shadow
                )
                .clip(RoundedCornerShape(18.dp))
                .background(brush = gradient)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSelected) Icons.Filled.SmartToy else Icons.Outlined.SmartToy,
                contentDescription = "AI Chat",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = "AI",
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            color = labelColor
        )
    }
}

// ─── Menu popup ───────────────────────────────────────────────────────────────
@Composable
private fun MenuPopup(
    onAddClick: () -> Unit,
    onViewClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(160.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = NavColors.popup_shadow,
                spotColor = NavColors.popup_shadow
            ),
        shape = RoundedCornerShape(16.dp),
        color = NavColors.popup_bg,
        tonalElevation = 0.dp
    ) {
        Column {
            PopupMenuItem(
                icon = Icons.Outlined.AddBox,
                label = "Menu Add",
                onClick = onAddClick
            )
            HorizontalDivider(thickness = 0.5.dp, color = NavColors.popup_divider)
            PopupMenuItem(
                icon = Icons.Outlined.MenuBook,
                label = "Menu View",
                onClick = onViewClick
            )
        }
    }
}

@Composable
private fun PopupMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = NavColors.nav_active,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = NavColors.nav_active
        )
    }
}

// ─── NavController extension ──────────────────────────────────────────────────
fun NavController.navigateSingleTop(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}