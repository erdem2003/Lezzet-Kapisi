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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SoupKitchen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
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
private val Blue700      = Color(0xFF0D3E7A)
private val Blue600      = Color(0xFF185FA5)
private val Blue100      = Color(0xFFB5D4F4)
private val Blue50       = Color(0xFFE6F1FB)
private val PageBg       = Color(0xFFF0F4FA)
private val White        = Color(0xFFFFFFFF)
private val TextDark     = Color(0xFF0C1A2E)
private val TextMuted    = Color(0xFF5A7399)
private val ErrorText    = Color(0xFFA32D2D)
private val DangerRed    = Color(0xFFD32F2F)
private val GoldStar     = Color(0xFFF9A825)
private val CardShadow   = Color(0x14000000)
private val ActiveGreen  = Color(0xFF2E7D32)
private val ActiveBg     = Color(0xCC2E7D32)
private val PassiveAmber = Color(0xFF8D6200)
private val PassiveBg    = Color(0xCCF9A825)

// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuViewScreen(
    navController: NavController,
    viewModel: MenuViewViewModel
) {
    val menuList     by viewModel.menuList
    val errorMessage by viewModel.errorMessage

    var searchQuery by remember { mutableStateOf("") }

    val filteredMenuList = remember(menuList, searchQuery) {
        if (searchQuery.isBlank()) menuList
        else menuList.filter {
            it.food_name.contains(searchQuery.trim(), ignoreCase = true)
        }
    }

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
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Column {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text       = "Menülerim",
                                color      = White,
                                fontSize   = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text     = "${filteredMenuList.size} ürün listeleniyor",
                                color    = Blue100,
                                fontSize = 11.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication        = null
                                ) {
                                    Log.d("Deneme", "Logo basıldı")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector        = Icons.Default.SoupKitchen,
                                contentDescription = "Şef",
                                tint               = White,
                                modifier           = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .background(White.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector        = Icons.Default.Search,
                            contentDescription = "Ara",
                            tint               = White.copy(alpha = 0.7f),
                            modifier           = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        BasicTextField(
                            value         = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine    = true,
                            textStyle     = TextStyle(color = White, fontSize = 13.sp),
                            cursorBrush   = SolidColor(White),
                            decorationBox = { innerTextField ->
                                Box(contentAlignment = Alignment.CenterStart) {
                                    if (searchQuery.isEmpty()) {
                                        Text(
                                            text     = "Yemek adına göre ara…",
                                            color    = White.copy(alpha = 0.5f),
                                            fontSize = 13.sp
                                        )
                                    }
                                    innerTextField()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        bottomBar = { BusinessBottomBar(navController = navController) }
    ) { paddingValues ->

        if (viewModel.isLoading.value == true) {
            Box(
                modifier         = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color       = Blue600,
                    strokeWidth = 3.dp,
                    modifier    = Modifier.size(48.dp)
                )
            }
            return@Scaffold
        }

        if (errorMessage.isNotEmpty()) {
            Box(
                modifier         = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier  = Modifier.fillMaxWidth().padding(24.dp),
                    colors    = CardDefaults.cardColors(containerColor = White),
                    shape     = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier            = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("⚠️", fontSize = 40.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(text = errorMessage, color = ErrorText, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick  = { viewModel.clearState(); viewModel.menuListController() },
                            colors   = ButtonDefaults.buttonColors(containerColor = Blue600),
                            shape    = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Tekrar Dene", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
            return@Scaffold
        }

        if (filteredMenuList.isEmpty()) {
            Box(
                modifier         = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier         = Modifier.size(96.dp).background(Blue50, RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = Icons.Default.Restaurant,
                            contentDescription = null,
                            tint               = Blue600,
                            modifier           = Modifier.size(48.dp)
                        )
                    }
                    Spacer(Modifier.height(20.dp))
                    Text(
                        text       = if (searchQuery.isBlank()) "Henüz menü eklenmedi"
                        else "\"$searchQuery\" için sonuç bulunamadı",
                        color      = TextDark,
                        fontSize   = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text     = if (searchQuery.isBlank())
                            "Menü eklemek için alt bardaki\nMenu butonuna basabilirsin"
                        else "Farklı bir isim deneyin",
                        color    = TextMuted,
                        fontSize = 13.sp
                    )
                }
            }
            return@Scaffold
        }

        LazyColumn(
            modifier            = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding      = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(
                items = filteredMenuList,
                key   = { _, menu -> menu.food_name + menu.createdDate.toString() }
            ) { index, menu ->
                AnimatedVisibility(
                    visible = true,
                    enter   = fadeIn(tween(300, delayMillis = index * 60)) +
                            slideInVertically(tween(300, delayMillis = index * 60)) { it / 3 }
                ) {
                    MenuCard(
                        menu          = menu,
                        categoryNames = viewModel.menuid_foodCategory[menu.menu_id] ?: emptyList(),
                        onCardClick   = { Log.d("Deneme", "Card Click") },
                        onEditClick   = {
                            navController.navigate("business_menu_edit/${menu.menu_id}")
                            Log.d("Deneme", "Edit Click")
                        },
                        onDeleteClick = {
                            viewModel.menuDeleteController(menu.menu_id)
                            Log.d("Deneme", "Delete Click")
                        },
                        onInfoClick   = { Log.d("Deneme", "Info Click - menu_id: ${menu.menu_id}") }
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
    onDeleteClick: () -> Unit,
    onInfoClick: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    val dateFormatted = remember(menu.createdDate) {
        try {
            SimpleDateFormat("dd MMM yyyy", Locale("tr")).format(menu.createdDate.toDate())
        } catch (e: Exception) { "" }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor   = White,
            shape            = RoundedCornerShape(20.dp),
            icon = {
                Box(
                    modifier         = Modifier
                        .size(52.dp)
                        .background(DangerRed.copy(alpha = 0.10f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = Icons.Default.Delete,
                        contentDescription = null,
                        tint               = DangerRed,
                        modifier           = Modifier.size(26.dp)
                    )
                }
            },
            title = {
                Text(
                    text       = "Menüyü Kaldır",
                    color      = TextDark,
                    fontSize   = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text       = "\"${menu.food_name}\" menüden kalıcı olarak kaldırılacak. Emin misiniz?",
                    color      = TextMuted,
                    fontSize   = 13.sp,
                    lineHeight = 20.sp
                )
            },
            dismissButton = {
                OutlinedButton(
                    onClick  = { showDeleteDialog = false },
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted),
                    border   = androidx.compose.foundation.BorderStroke(1.dp, Blue100),
                    shape    = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(44.dp)
                ) {
                    Text("Vazgeç", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            },
            confirmButton = {
                Button(
                    onClick  = { showDeleteDialog = false; onDeleteClick() },
                    colors   = ButtonDefaults.buttonColors(containerColor = DangerRed),
                    shape    = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(44.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Evet, Kaldır", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp), ambientColor = CardShadow, spotColor = CardShadow)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onCardClick
            ),
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // ── Üst alan: fotoğraf + içerik ───────────────────────────────────
            Row(
                modifier          = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                // ── Fotoğraf + sol üst köşe aktif/pasif badge'i ───────────────
                Box {
                    AsyncImage(
                        model              = menu.food_photo_url,
                        contentDescription = menu.food_name,
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier
                            .size(88.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Blue50)
                            .border(1.dp, Blue100, RoundedCornerShape(12.dp))
                    )

                    // ── Aktif / Pasif badge – sol üst köşe ────────────────────
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(5.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (menu.active) ActiveBg else PassiveBg)
                            .padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .background(White, CircleShape)
                        )
                        Text(
                            text       = if (menu.active) "Aktif" else "Pasif",
                            color      = White,
                            fontSize   = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text       = menu.food_name,
                            color      = TextDark,
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines   = 1,
                            overflow   = TextOverflow.Ellipsis,
                            modifier   = Modifier.weight(1f)
                        )

                        Spacer(Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .background(Blue50, CircleShape)
                                .border(1.dp, Blue100, CircleShape)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication        = null,
                                    onClick           = onInfoClick
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector        = Icons.Default.Info,
                                contentDescription = "Bilgi",
                                tint               = Blue600,
                                modifier           = Modifier.size(15.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(3.dp))

                    Text(
                        text       = menu.food_description,
                        color      = TextMuted,
                        fontSize   = 12.sp,
                        maxLines   = 2,
                        overflow   = TextOverflow.Ellipsis,
                        lineHeight = 16.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("★", color = GoldStar, fontSize = 13.sp)
                        Spacer(Modifier.width(3.dp))
                        Text(
                            text       = String.format("%.1f", menu.averageLike),
                            color      = GoldStar,
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text     = "💬 ${menu.count_command} yorum",
                            color    = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    if (dateFormatted.isNotEmpty()) {
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector        = Icons.Default.CalendarToday,
                                contentDescription = "Tarih",
                                tint               = TextMuted.copy(alpha = 0.7f),
                                modifier           = Modifier.size(11.dp)
                            )
                            Spacer(Modifier.width(3.dp))
                            Text(
                                text     = dateFormatted,
                                color    = TextMuted.copy(alpha = 0.7f),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // ── Kategori chip'leri ─────────────────────────────────────────────
            if (categoryNames.isNotEmpty()) {
                Row(
                    modifier              = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    categoryNames.forEach { CategoryBadge(name = it) }
                }
            }

            HorizontalDivider(
                color     = Blue50,
                thickness = 1.dp,
                modifier  = Modifier.padding(horizontal = 12.dp)
            )

            // ── Alt alan: fiyat + butonlar ─────────────────────────────────────
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text       = "₺${String.format("%.2f", menu.food_price)}",
                    color      = Blue700,
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(Modifier.weight(1f))

                OutlinedButton(
                    onClick        = onEditClick,
                    colors         = ButtonDefaults.outlinedButtonColors(contentColor = Blue600),
                    border         = androidx.compose.foundation.BorderStroke(1.dp, Blue100),
                    shape          = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier       = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Düzenle", modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Düzenle", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }

                Spacer(Modifier.width(8.dp))

                OutlinedButton(
                    onClick        = { showDeleteDialog = true },
                    colors         = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                    border         = androidx.compose.foundation.BorderStroke(1.dp, DangerRed.copy(alpha = 0.4f)),
                    shape          = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier       = Modifier.height(36.dp)
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
            text       = name,
            color      = Blue600,
            fontSize   = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines   = 1,
            overflow   = TextOverflow.Ellipsis
        )
    }
}