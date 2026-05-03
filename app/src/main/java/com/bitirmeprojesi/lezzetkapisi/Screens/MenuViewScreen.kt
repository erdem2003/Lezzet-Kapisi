package com.bitirmeprojesi.lezzetkapisi.Screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.bitirmeprojesi.lezzetkapisi.Components.BusinessBottomBar
import com.bitirmeprojesi.lezzetkapisi.Model.MenuItem
import com.bitirmeprojesi.lezzetkapisi.ViewModels.MenuViewViewModel
import java.text.SimpleDateFormat
import java.util.Locale

// ── Renkler ──────────────────────────────────────────────────────────────────
private val Blue700    = Color(0xFF0D3E7A)
private val Blue600    = Color(0xFF185FA5)
private val Blue100    = Color(0xFFB5D4F4)
private val Blue50     = Color(0xFFE6F1FB)
private val PageBg     = Color(0xFFF0F4FA)
private val White      = Color(0xFFFFFFFF)
private val TextDark   = Color(0xFF0C1A2E)
private val TextMuted  = Color(0xFF5A7399)
private val ErrorText  = Color(0xFFA32D2D)
private val DangerRed  = Color(0xFFD32F2F)
private val GoldStar   = Color(0xFFF9A825)
private val CardShadow = Color(0x14000000)

// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuViewScreen(
    navController: NavController,
    viewModel: MenuViewViewModel
) {
    val menuList     by viewModel.menuList
    val errorMessage by viewModel.errorMessage

    LaunchedEffect(Unit) {
        viewModel.menuListController()
    }

    Scaffold(
        containerColor = PageBg,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(colors = listOf(Blue700, Blue600)))
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column {
                    Text(
                        text = "Menülerim",
                        color = White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "${menuList.size} ürün listeleniyor",
                        color = Blue100,
                        fontSize = 13.sp
                    )
                }
            }
        },
        bottomBar = { BusinessBottomBar(navController = navController) }
    ) { paddingValues ->

        // ── Loading ───────────────────────────────────────────────────────────
        if (viewModel.isLoading.value == true) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Blue600,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(48.dp)
                )
            }
            return@Scaffold
        }

        // ── Hata mesajı ───────────────────────────────────────────────────────
        if (errorMessage.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("⚠️", fontSize = 40.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = errorMessage,
                            color = ErrorText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = {
                                viewModel.clearState()
                                viewModel.menuListController()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Blue600),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Tekrar Dene", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
            return@Scaffold
        }

        // ── Boş liste ─────────────────────────────────────────────────────────
        if (menuList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .background(Blue50, RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = null,
                            tint = Blue600,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Spacer(Modifier.height(20.dp))
                    Text(
                        text = "Henüz menü eklenmedi",
                        color = TextDark,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Menü eklemek için alt bardaki\nMenu butonuna basabilirsin",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            }
            return@Scaffold
        }

        // ── Liste ─────────────────────────────────────────────────────────────
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(
                items = menuList,
                key = { _, menu -> menu.food_name + menu.createdDate.toString() }
            ) { index, menu ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(300, delayMillis = index * 60)) +
                            slideInVertically(
                                tween(300, delayMillis = index * 60),
                                initialOffsetY = { it / 3 }
                            )
                ) {
                    MenuCard(
                        menu = menu,
                        categoryNames = viewModel.menuid_foodCategory[menu.menu_id] ?: emptyList(),
                        onCardClick = {
                            // TODO: yemek detay sayfasına yönlendir
                            // navController.navigate("menu_detail/${menu.menu_id}")
                            Log.d("Deneme","Card Click")
                        },
                        onEditClick = {
                            // TODO: düzenleme sayfasına yönlendir
                            // navController.navigate("business_menu_edit/${menu.menu_id}")
                            Log.d("Deneme","Edit Click")
                        },
                        onDeleteClick = {
                            // TODO: silme dialogu göster veya direkt sil
                            // viewModel.deleteMenu(menu)
                            Log.d("Deneme","Delete Click")
                        }
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun MenuCard(
    menu: MenuItem,
    categoryNames: List<String>,
    onCardClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateFormatted = remember(menu.createdDate) {
        try {
            SimpleDateFormat("dd MMM yyyy", Locale("tr")).format(menu.createdDate.toDate())
        } catch (e: Exception) { "" }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp), ambientColor = CardShadow, spotColor = CardShadow)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onCardClick
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // ── Üst alan: fotoğraf + içerik ───────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Fotoğraf
                AsyncImage(
                    model = menu.food_photo_url,
                    contentDescription = menu.food_name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Blue50)
                        .border(1.dp, Blue100, RoundedCornerShape(12.dp))
                )

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = menu.food_name,
                        color = TextDark,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = menu.food_description,
                        color = TextMuted,
                        fontSize = 12.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp
                    )
                    Spacer(Modifier.height(8.dp))

                    // Yıldız + yorum sayısı
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("★", color = GoldStar, fontSize = 13.sp)
                        Spacer(Modifier.width(3.dp))
                        Text(
                            text = String.format("%.1f", menu.averageLike),
                            color = GoldStar,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "💬 ${menu.count_command} yorum",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    // Oluşturulma tarihi
                    if (dateFormatted.isNotEmpty()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "📅 $dateFormatted",
                            color = TextMuted.copy(alpha = 0.7f),
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // ── Kategori chip'leri (ayırıcının hemen üstü) ────────────────────
            if (categoryNames.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    categoryNames.forEach { categoryName ->
                        CategoryBadge(name = categoryName)
                    }
                }
            }

            // ── Ayırıcı ───────────────────────────────────────────────────────
            HorizontalDivider(
                color = Blue50,
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            // ── Alt alan: fiyat + butonlar ─────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₺${String.format("%.2f", menu.food_price)}",
                    color = Blue700,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(Modifier.weight(1f))

                // Düzenle butonu
                OutlinedButton(
                    onClick = onEditClick,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Blue600),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Blue100),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Düzenle", modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Düzenle", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }

                Spacer(Modifier.width(8.dp))

                // Kaldır butonu
                OutlinedButton(
                    onClick = onDeleteClick,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DangerRed.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Kaldır", modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Kaldır", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

// ── Kategori badge ────────────────────────────────────────────────────────────
@Composable
private fun CategoryBadge(name: String) {
    Box(
        modifier = Modifier
            .background(Blue50, RoundedCornerShape(50.dp))
            .border(1.dp, Blue100, RoundedCornerShape(50.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            color = Blue600,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}