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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

// ─── Renk paleti ──────────────────────────────────────────────────────────────
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
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val iconSelected: ImageVector = icon
)

// ─── Adaptif boyut yardımcıları ───────────────────────────────────────────────

private fun adaptDp(ref: Float, actual: Float, min: Float, max: Float, refScreen: Float = 800f): Dp =
    ((ref / refScreen) * actual).coerceIn(min, max).dp

private fun adaptSp(ref: Float, actual: Float, min: Float, max: Float, refScreen: Float = 800f): TextUnit =
    ((ref / refScreen) * actual).coerceIn(min, max).sp

// ─── Ana composable ───────────────────────────────────────────────────────────
@Composable
fun BusinessBottomBar(navController: NavController) {

    val config  = LocalConfiguration.current
    val screenH = config.screenHeightDp.toFloat()
    val screenW = config.screenWidthDp.toFloat()

    val contentH    = adaptDp(64f,  screenH, 52f, 72f)
    val iconSize    = adaptDp(22f,  screenH, 18f, 26f)
    val aiBtnSize   = adaptDp(44f,  screenH, 36f, 52f)
    val aiRadius    = adaptDp(16f,  screenH, 12f, 20f)
    val itemRadius  = adaptDp(12f,  screenH,  9f, 16f)
    val itemHPad    = adaptDp(12f,  screenH,  8f, 16f)
    val itemVPad    = adaptDp( 6f,  screenH,  4f,  9f)
    val gap         = adaptDp( 3f,  screenH,  2f,  5f)
    val barHPad     = adaptDp( 4f,  screenH,  2f,  8f)
    val labelSp     = adaptSp(10f,  screenH,  8f, 12f)
    val popupOffset = contentH + 12.dp

    val popupW    = adaptDp(170f, screenW, 140f, 210f)
    val popupHPad = adaptDp( 16f, screenH,  12f,  20f)
    val popupVPad = adaptDp( 14f, screenH,  10f,  18f)
    val popupIcon = adaptDp( 20f, screenH,  15f,  24f)
    val popupGap  = adaptDp( 12f, screenH,   8f,  16f)
    val popupLbl  = adaptSp( 14f, screenH,  11f,  16f)

    val items = listOf(
        BottomNavItem("business_feed", "Home",     Icons.Outlined.Home,     Icons.Filled.Home),
        BottomNavItem("search",        "Search",   Icons.Outlined.Search,   Icons.Filled.Search),
        BottomNavItem("settings",      "Settings", Icons.Outlined.Settings, Icons.Filled.Settings),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var menuPopupVisible by remember { mutableStateOf(false) }
    val isMenuSelected = currentRoute == "business_menu_add" || currentRoute == "business_menu_view"

    Box(modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),  // ← BURAYA TAŞINDI (eskiden Row'daydı)
            color = NavColors.nav_surface,
            shadowElevation = 0.dp
        ) {
            Column {
                HorizontalDivider(thickness = 1.dp, color = NavColors.nav_divider)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        // navigationBarsPadding() KALDIRILDI → Row artık asla şişmez
                        .height(contentH)               // heightIn yerine sabit height yeterli
                        .padding(horizontal = barHPad),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavBarItem(items[0], currentRoute == items[0].route, iconSize, itemRadius, itemHPad, itemVPad, gap, labelSp) {
                        menuPopupVisible = false; navController.navigateSingleTop(items[0].route)
                    }
                    NavBarItem(items[1], currentRoute == items[1].route, iconSize, itemRadius, itemHPad, itemVPad, gap, labelSp) {
                        menuPopupVisible = false; navController.navigateSingleTop(items[1].route)
                    }
                    AiChatButton(currentRoute == "chatbot", aiBtnSize, aiRadius, iconSize, gap, labelSp) {
                        menuPopupVisible = false; navController.navigateSingleTop("chatbot")
                    }
                    MenuNavItem(isMenuSelected, menuPopupVisible, iconSize, itemRadius, itemHPad, itemVPad, gap, labelSp) {
                        menuPopupVisible = !menuPopupVisible
                    }
                    NavBarItem(items[2], currentRoute == items[2].route, iconSize, itemRadius, itemHPad, itemVPad, gap, labelSp) {
                        menuPopupVisible = false; navController.navigateSingleTop(items[2].route)
                    }
                }
            }
        }

        // ── Popup ─────────────────────────────────────────────────────────────
        AnimatedVisibility(
            visible = menuPopupVisible,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = popupOffset)
                .zIndex(10f),
            enter = fadeIn(tween(150)) + slideInVertically(tween(150)) { it / 2 },
            exit  = fadeOut(tween(100)) + slideOutVertically(tween(100)) { it / 2 }
        ) {
            MenuPopup(popupW, popupHPad, popupVPad, popupIcon, popupLbl, popupGap,
                onAddClick  = { menuPopupVisible = false; navController.navigateSingleTop("business_menu_add") },
                onViewClick = { menuPopupVisible = false; navController.navigateSingleTop("business_menu_view") },
                onDismiss   = { menuPopupVisible = false }
            )
        }
    }
}

// ─── Standart nav item ────────────────────────────────────────────────────────
@Composable
private fun NavBarItem(
    item: BottomNavItem, isSelected: Boolean,
    iconSize: Dp, itemRadius: Dp, itemHPad: Dp, itemVPad: Dp,
    gap: Dp, labelSp: TextUnit, onClick: () -> Unit
) {
    val contentColor by animateColorAsState(
        if (isSelected) NavColors.nav_active else NavColors.nav_inactive, tween(250), label = "c")
    val bgColor by animateColorAsState(
        if (isSelected) NavColors.nav_indicator else Color.Transparent, tween(250), label = "bg")
    val scale by animateFloatAsState(
        if (isSelected) 1.07f else 1f, spring(Spring.DampingRatioMediumBouncy), label = "s")

    Column(
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(RoundedCornerShape(itemRadius))
            .background(bgColor)
            .clickable(remember { MutableInteractionSource() }, null, onClick = onClick)
            .padding(horizontal = itemHPad, vertical = itemVPad),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(gap)
    ) {
        Icon(if (isSelected) item.iconSelected else item.icon, item.label,
            tint = contentColor, modifier = Modifier.size(iconSize))
        Text(item.label, fontSize = labelSp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            color = contentColor)
    }
}

// ─── Menu nav item ────────────────────────────────────────────────────────────
@Composable
private fun MenuNavItem(
    isSelected: Boolean, isPopupOpen: Boolean,
    iconSize: Dp, itemRadius: Dp, itemHPad: Dp, itemVPad: Dp,
    gap: Dp, labelSp: TextUnit, onClick: () -> Unit
) {
    val isActive = isSelected || isPopupOpen
    val contentColor by animateColorAsState(
        if (isActive) NavColors.nav_active else NavColors.nav_inactive, tween(250), label = "c")
    val bgColor by animateColorAsState(
        if (isActive) NavColors.nav_indicator else Color.Transparent, tween(250), label = "bg")
    val scale by animateFloatAsState(
        if (isActive) 1.07f else 1f, spring(Spring.DampingRatioMediumBouncy), label = "s")

    Column(
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(RoundedCornerShape(itemRadius))
            .background(bgColor)
            .clickable(remember { MutableInteractionSource() }, null, onClick = onClick)
            .padding(horizontal = itemHPad, vertical = itemVPad),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(gap)
    ) {
        Icon(if (isActive) Icons.Filled.MenuBook else Icons.Outlined.MenuBook, "Menu",
            tint = contentColor, modifier = Modifier.size(iconSize))
        Text("Menu", fontSize = labelSp,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium,
            color = contentColor)
    }
}

// ─── AI Chat gradient butonu ──────────────────────────────────────────────────
@Composable
private fun AiChatButton(
    isSelected: Boolean,
    aiBtnSize: Dp, aiRadius: Dp, iconSize: Dp, gap: Dp, labelSp: TextUnit,
    onClick: () -> Unit
) {
    val gradient = Brush.linearGradient(
        if (isSelected) listOf(NavColors.nav_ai_start_pressed, NavColors.nav_ai_end_pressed)
        else            listOf(NavColors.nav_ai_start,         NavColors.nav_ai_end)
    )
    val scale by animateFloatAsState(
        if (isSelected) 1.07f else 1f, spring(Spring.DampingRatioMediumBouncy), label = "ai")
    val labelColor by animateColorAsState(
        if (isSelected) NavColors.nav_ai_label_active else NavColors.nav_inactive, tween(250), label = "lc")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(gap),
        modifier = Modifier.graphicsLayer { scaleX = scale; scaleY = scale }
    ) {
        Box(
            modifier = Modifier
                .size(aiBtnSize)
                .shadow(if (isSelected) 6.dp else 10.dp, RoundedCornerShape(aiRadius),
                    ambientColor = NavColors.nav_ai_shadow, spotColor = NavColors.nav_ai_shadow)
                .clip(RoundedCornerShape(aiRadius))
                .background(gradient)
                .clickable(remember { MutableInteractionSource() }, null, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(if (isSelected) Icons.Filled.SmartToy else Icons.Outlined.SmartToy,
                "AI Chat", tint = Color.White, modifier = Modifier.size(iconSize))
        }
        Text("AI", fontSize = labelSp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            color = labelColor)
    }
}

// ─── Menu popup ───────────────────────────────────────────────────────────────
@Composable
private fun MenuPopup(
    popupW: Dp, popupHPad: Dp, popupVPad: Dp,
    popupIcon: Dp, popupLbl: TextUnit, popupGap: Dp,
    onAddClick: () -> Unit, onViewClick: () -> Unit, onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(popupW)
            .shadow(12.dp, RoundedCornerShape(16.dp),
                ambientColor = NavColors.popup_shadow, spotColor = NavColors.popup_shadow),
        shape = RoundedCornerShape(16.dp),
        color = NavColors.popup_bg,
        tonalElevation = 0.dp
    ) {
        Column {
            PopupMenuItem(Icons.Outlined.AddBox,   "Menu Add",  popupIcon, popupHPad, popupVPad, popupLbl, popupGap, onAddClick)
            HorizontalDivider(thickness = 0.5.dp, color = NavColors.popup_divider)
            PopupMenuItem(Icons.Outlined.MenuBook, "Menu View", popupIcon, popupHPad, popupVPad, popupLbl, popupGap, onViewClick)
        }
    }
}

@Composable
private fun PopupMenuItem(
    icon: ImageVector, label: String,
    iconSize: Dp, hPad: Dp, vPad: Dp, labelSp: TextUnit, gap: Dp,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(remember { MutableInteractionSource() }, null, onClick = onClick)
            .padding(horizontal = hPad, vertical = vPad),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(gap)
    ) {
        Icon(icon, label, tint = NavColors.nav_active, modifier = Modifier.size(iconSize))
        Text(label, fontSize = labelSp, fontWeight = FontWeight.Medium, color = NavColors.nav_active)
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