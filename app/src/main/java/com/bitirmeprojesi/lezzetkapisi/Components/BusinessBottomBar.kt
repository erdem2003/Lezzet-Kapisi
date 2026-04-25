package com.bitirmeprojesi.lezzetkapisi.Components

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

// ─── Renk paleti ──────────────────────────────────────────────────────────────
private object NavColors {
    val nav_active        = Color(0xFF7C3AED)   // Vibrant purple
    val nav_inactive      = Color(0xFF9CA3AF)   // Cool gray
    val nav_surface       = Color(0xFFFFFFFF)   // Navbar background
    val nav_divider       = Color(0xFFF0F0F0)   // Top border
    val nav_item_hover    = Color(0xFFF5F3FF)   // Hover/pressed fill
    val nav_indicator     = Color(0xFFEDE9FE)   // Seçili item bg

    val nav_ai_start      = Color(0xFF7C3AED)   // AI buton gradient başlangıç
    val nav_ai_end        = Color(0xFFEC4899)   // AI buton gradient bitiş
    val nav_ai_start_pressed = Color(0xFF6D28D9)
    val nav_ai_end_pressed   = Color(0xFFDB2777)
    val nav_ai_shadow     = Color(0x59_7C3AED)  // AI buton gölgesi (%35 alpha)
    val nav_ai_label_active  = Color(0xFF7C3AED)
}

// ─── Nav item data class ──────────────────────────────────────────────────────
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
        BottomNavItem("business_feed",      "Home",     Icons.Outlined.Home,       Icons.Filled.Home),
        BottomNavItem("search",             "Search",   Icons.Outlined.Search,     Icons.Filled.Search),
        BottomNavItem("business_menu_add",  "Add",      Icons.Outlined.AddBox,     Icons.Filled.AddBox),
        BottomNavItem("business_menu_view", "Menu",     Icons.Outlined.MenuBook,   Icons.Filled.MenuBook),
        BottomNavItem("settings",           "Settings", Icons.Outlined.Settings,   Icons.Filled.Settings),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = NavColors.nav_surface,
        shadowElevation = 0.dp
    ) {
        Column {
            HorizontalDivider(
                thickness = 1.dp,
                color = NavColors.nav_divider
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .height(66.dp)
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home
                NavBarItem(
                    item = items[0],
                    isSelected = currentRoute == items[0].route,
                    onClick = { navController.navigateSingleTop(items[0].route) }
                )

                // Search
                NavBarItem(
                    item = items[1],
                    isSelected = currentRoute == items[1].route,
                    onClick = { navController.navigateSingleTop(items[1].route) }
                )

                // AI Chat — merkezi gradient buton
                AiChatButton(
                    isSelected = currentRoute == "chatbot",
                    onClick = { navController.navigateSingleTop("chatbot") }
                )

                // Add
                NavBarItem(
                    item = items[2],
                    isSelected = currentRoute == items[2].route,
                    onClick = { navController.navigateSingleTop(items[2].route) }
                )

                // Menu
                NavBarItem(
                    item = items[3],
                    isSelected = currentRoute == items[3].route,
                    onClick = { navController.navigateSingleTop(items[3].route) }
                )

                // Settings
                NavBarItem(
                    item = items[4],
                    isSelected = currentRoute == items[4].route,
                    onClick = { navController.navigateSingleTop(items[4].route) }
                )
            }
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
    val contentColor = if (isSelected) NavColors.nav_active else NavColors.nav_inactive

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) NavColors.nav_indicator else Color.Transparent)
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

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .offset(y = (-10).dp)
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
            color = if (isSelected) NavColors.nav_ai_label_active else NavColors.nav_inactive,
            modifier = Modifier.offset(y = (-10).dp)
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
