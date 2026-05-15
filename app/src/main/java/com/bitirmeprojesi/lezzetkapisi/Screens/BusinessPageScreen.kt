package com.bitirmeprojesi.lezzetkapisi.Screens

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.bitirmeprojesi.lezzetkapisi.Components.BusinessBottomBar
import com.bitirmeprojesi.lezzetkapisi.Model.BusinessInfo
import com.bitirmeprojesi.lezzetkapisi.Model.Business_Comment
import com.bitirmeprojesi.lezzetkapisi.Model.Menu
import com.bitirmeprojesi.lezzetkapisi.Model.User
import com.bitirmeprojesi.lezzetkapisi.Model.User_Or_Business
import com.bitirmeprojesi.lezzetkapisi.ViewModels.BusinessPageViewModel
import com.bitirmeprojesi.lezzetkapisi.ViewModels.MenuSortType
import java.text.SimpleDateFormat
import java.util.Locale

// ─── Renkler ──────────────────────────────────────────────────────────────────
private object BPColors {
    val primary          = Color(0xFF1D6FD8)
    val primaryLight     = Color(0xFFDBEAFD)
    val accent           = Color(0xFF06B6D4)
    val background       = Color(0xFFFFFFFF)
    val surface          = Color(0xFFF8FAFF)
    val cardBg           = Color(0xFFFFFFFF)
    val textPrimary      = Color(0xFF1A1A2E)
    val textSecondary    = Color(0xFF6B7280)
    val divider          = Color(0xFFE5EAF5)
    val activeGreen      = Color(0xFF22C55E)
    val activeGreenBg    = Color(0xFFDCFCE7)
    val inactiveYellow   = Color(0xFFF59E0B)
    val inactiveYellowBg = Color(0xFFFEF3C7)
    val starYellow       = Color(0xFFFBBF24)
    val starYellowLight  = Color(0xFFFEF3C7)
    val starGray         = Color(0xFFE5E7EB)
    val errorRed         = Color(0xFFEF4444)
    val errorRedBg       = Color(0xFFFEE2E2)
    val successGreen     = Color(0xFF22C55E)
    val successGreenBg   = Color(0xFFDCFCE7)
}

private data class SortTab(
    val sort  : MenuSortType,
    val label : String,
    val icon  : ImageVector
)

private val sortTabs = listOf(
    SortTab(MenuSortType.NEWEST,          "En Yeni",          Icons.Filled.NewReleases),
    SortTab(MenuSortType.MOST_COMMENTED,  "En Çok Yorum",     Icons.Filled.ChatBubble),
    SortTab(MenuSortType.HIGHEST_STAR,    "En Yüksek Puan",   Icons.Filled.Star),
    SortTab(MenuSortType.BEST_PRICE_PERF, "Fiyat/Performans", Icons.Filled.TrendingUp),
)

// ─── Yardımcı fonksiyonlar ────────────────────────────────────────────────────
private fun displayNameFor(info: User_Or_Business?): String = when (info) {
    is User         -> info.username.ifBlank { info.email.take(12) + "..." }
    is BusinessInfo -> info.business_name
    else            -> "Kullanıcı"
}

private fun photoUrlFor(info: User_Or_Business?): String? = when (info) {
    is User         -> info.profile_photo.ifBlank { null }
    is BusinessInfo -> info.profile_photo.ifBlank { null }
    else            -> null
}

private fun initialsFor(info: User_Or_Business?, fallback: String): String = when (info) {
    is User         -> info.username.take(2).uppercase().ifBlank { fallback }
    is BusinessInfo -> info.business_name.take(2).uppercase().ifBlank { fallback }
    else            -> fallback
}

// ─── Ana Ekran ────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessPageScreen(
    navController         : NavController,
    business_id           : String,
    businessPageViewModel : BusinessPageViewModel
) {
    if (business_id == "") {
        Box(
            modifier         = Modifier.fillMaxSize().background(BPColors.background),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.ErrorOutline, null,
                    tint = BPColors.primary, modifier = Modifier.size(64.dp))
                Spacer(Modifier.height(12.dp))
                Text("İşletme bilgileri yüklenemedi!",
                    fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = BPColors.textPrimary)
                Text("Bir hata oluştu, lütfen tekrar deneyin.",
                    fontSize = 13.sp, color = BPColors.textSecondary)
            }
        }
        return
    }

    LaunchedEffect(business_id) {
        businessPageViewModel.loadPage(business_id)
        businessPageViewModel.getCommentsForBusiness(business_id)
        businessPageViewModel.userBusinessStar(business_id)
    }

    val businessInfo       by businessPageViewModel.business_info_state
    val menuList           by businessPageViewModel.business_menu_list
    val errorMsg           by businessPageViewModel.error_message
    val isLoading          by businessPageViewModel.progress_bar
    val activeSort         by businessPageViewModel.activeSort
    val currentComment     by businessPageViewModel.current_comment
    val commentList        by businessPageViewModel.business_comment_list
    val commentListLoading by businessPageViewModel.comment_list_progress_bar
    val sendSuccessMsg     by businessPageViewModel.sendMessage_success_message
    val sendErrorMsg       by businessPageViewModel.sendMessage_error_message
    val sendButtonEnabled  by businessPageViewModel.enableSendMessageButton
    val userInfoMap        by businessPageViewModel.user_business_info_map

    // Yıldız state'leri
    val userStar           by businessPageViewModel.user_business_star
    val starErrorMsg       by businessPageViewModel.star_error_message
    val starLoading        by businessPageViewModel.star_progress_bar

    // Pull-to-refresh state
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(isLoading, commentListLoading) {
        if (!isLoading && !commentListLoading) {
            isRefreshing = false
        }
    }

    if (businessPageViewModel.refresh_progress_bar.value) {
        CircularProgressIndicator()
    } else {
        val snackbarHostState = remember { SnackbarHostState() }
        LaunchedEffect(errorMsg) {
            if (errorMsg.isNotBlank())
                snackbarHostState.showSnackbar(errorMsg, duration = SnackbarDuration.Long)
        }

        var showAllMenus        by remember { mutableStateOf(false) }
        var showAllComments     by remember { mutableStateOf(false) }
        val menuPreviewCount    = 2
        val commentPreviewCount = 3

        LaunchedEffect(activeSort) { showAllMenus = false }

        Scaffold(
            containerColor = BPColors.background,
            snackbarHost   = { SnackbarHost(snackbarHostState) },
            bottomBar      = { BusinessBottomBar(navController) }
        ) { innerPadding ->

            if (isLoading && !isRefreshing) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = BPColors.primary) }
                return@Scaffold
            }

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    businessPageViewModel.refreshPage(business_id)
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BPColors.background),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {

                    // ── 1. Header ──────────────────────────────────────────────
                    item { BusinessHeader(businessInfo, businessPageViewModel) }

                    // ── 2. Açıklama ────────────────────────────────────────────
                    businessInfo?.description?.takeIf { it.isNotBlank() }?.let { desc ->
                        item { DescriptionSection(desc) }
                    }

                    // ── 3. Menü Başlığı ────────────────────────────────────────
                    item {
                        SectionHeader(
                            icon = Icons.Filled.MenuBook,
                            title = "Menü",
                            badge = "${menuList.size} ürün"
                        )
                    }

                    // ── 4. Sıralama Sekmeleri ──────────────────────────────────
                    item {
                        MenuSortTabRow(
                            activeSort = activeSort,
                            onSortSelected = { businessPageViewModel.setSortType(it) }
                        )
                    }

                    // ── 5. Menü Kartları ───────────────────────────────────────
                    if (menuList.isEmpty()) {
                        item { EmptyState(Icons.Outlined.NoFood, "Henüz menü eklenmemiş") }
                    } else {
                        val displayed =
                            if (showAllMenus) menuList else menuList.take(menuPreviewCount)
                        items(displayed, key = { it.menu_id }) { menu ->
                            MenuCard(menu = menu, onClick = { Log.d("pageMenu", menu.menu_id) })
                            Spacer(Modifier.height(10.dp))
                        }
                        if (menuList.size > menuPreviewCount) {
                            item {
                                ShowMoreButton(
                                    expanded = showAllMenus,
                                    totalCount = menuList.size,
                                    visibleCount = menuPreviewCount,
                                    label = "ürün",
                                    onToggle = { showAllMenus = !showAllMenus }
                                )
                            }
                        }
                    }

                    // ── 6. Yorum & Yıldız Başlığı ──────────────────────────────
                    item {
                        Spacer(Modifier.height(8.dp))
                        SectionHeader(
                            icon = Icons.Filled.Star,
                            title = "Puanlar & Yorumlar",
                            badge = "${businessInfo?.count_comments ?: 0} yorum"
                        )
                    }

                    // ── 7. Yıldız Verme + Ortalama Bölümü ─────────────────────
                    item {
                        StarRatingSection(
                            businessInfo  = businessInfo,
                            userStarValue = userStar?.star_value,
                            isLoading     = starLoading,
                            errorMsg      = starErrorMsg,
                            onStarSend    = { starVal ->
                                businessPageViewModel.sendBusinessStar(business_id, starVal)
                            }
                        )
                    }

                    // ── 8. Yorum Yazma Alanı ───────────────────────────────────
                    item {
                        ReviewInputSection(
                            comment = currentComment,
                            sendSuccessMsg = sendSuccessMsg,
                            sendErrorMsg = sendErrorMsg,
                            buttonEnabled = sendButtonEnabled,
                            onComment = { businessPageViewModel.current_comment.value = it },
                            onSend = { businessPageViewModel.sendMessage(business_id) }
                        )
                    }

                    // ── 9. Yorum Listesi Başlığı ───────────────────────────────
                    item {
                        Spacer(Modifier.height(8.dp))
                        SectionHeader(
                            icon = Icons.Filled.ChatBubble,
                            title = "Kullanıcı Yorumları",
                            badge = "${commentList.size} yorum"
                        )
                    }

                    // ── 10. Yorum Kartları ─────────────────────────────────────
                    if (commentListLoading) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator(color = BPColors.primary) }
                        }
                    } else if (commentList.isEmpty()) {
                        item {
                            EmptyState(
                                Icons.Outlined.ChatBubbleOutline,
                                "Henüz yorum yapılmamış"
                            )
                        }
                    } else {
                        val displayed = if (showAllComments) commentList
                        else commentList.take(commentPreviewCount)
                        items(displayed, key = { it.comment_id }) { comment ->
                            CommentCard(
                                comment    = comment,
                                senderInfo = userInfoMap[comment.sender_id]
                            )
                            Spacer(Modifier.height(10.dp))
                        }
                        if (commentList.size > commentPreviewCount) {
                            item {
                                ShowMoreButton(
                                    expanded     = showAllComments,
                                    totalCount   = commentList.size,
                                    visibleCount = commentPreviewCount,
                                    label        = "yorum",
                                    onToggle     = { showAllComments = !showAllComments }
                                )
                            }
                        }
                    }

                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }
    }
}

// ─── Yıldız Verme + Ortalama Bölümü ──────────────────────────────────────────
@Composable
private fun StarRatingSection(
    businessInfo  : BusinessInfo?,
    userStarValue : Double?,          // null = daha önce vermemiş
    isLoading     : Boolean,
    errorMsg      : String,
    onStarSend    : (Double) -> Unit
) {
    // Kullanıcının seçtiği geçici yıldız (henüz gönderilmedi)
    var hoveredStar by remember { mutableStateOf(0) }

    // userStarValue değişince hoveredStar'ı sıfırla
    LaunchedEffect(userStarValue) { hoveredStar = 0 }

    // Görüntülenecek yıldız: önce verilen, sonra hover, sonra 0
    val displayedStar = when {
        userStarValue != null -> userStarValue.toInt()   // kalıcı seçim
        hoveredStar > 0       -> hoveredStar             // geçici seçim
        else                  -> 0
    }

    // Kullanıcı zaten oy verdiyse tekrar gönderemez
    //val canRate = userStarValue == null && !isLoading
    val canRate = !isLoading

    Surface(
        modifier        = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape           = RoundedCornerShape(20.dp),
        color           = BPColors.surface,
        shadowElevation = 3.dp
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {

            // ── Ortalama Puanı ─────────────────────────────────────────────
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier              = Modifier.fillMaxWidth()
            ) {
                // Büyük puan sayısı
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text       = String.format("%.1f", businessInfo?.average_star ?: 0.0),
                        fontSize   = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = BPColors.primary,
                        lineHeight = 52.sp
                    )
                    Text(
                        text     = "/ 5",
                        fontSize = 13.sp,
                        color    = BPColors.textSecondary
                    )
                }

                // Sağ taraf: yıldızlar + yorum sayısı
                Column(
                    modifier              = Modifier.weight(1f),
                    verticalArrangement   = Arrangement.spacedBy(6.dp)
                ) {
                    // Dolu yıldızlar (ortalamaya göre kısmi değil tam/yarım)
                    val avg = businessInfo?.average_star ?: 0.0
                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        repeat(5) { i ->
                            val filled = i < avg
                            val halfFilled = !filled && (i < avg + 0.5)
                            Icon(
                                imageVector = when {
                                    filled     -> Icons.Filled.Star
                                    halfFilled -> Icons.Filled.StarHalf
                                    else       -> Icons.Outlined.StarBorder
                                },
                                contentDescription = null,
                                tint     = if (filled || halfFilled) BPColors.starYellow
                                else BPColors.starGray,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Text(
                        text     = "${businessInfo?.count_comments ?: 0} değerlendirme",
                        fontSize = 12.sp,
                        color    = BPColors.textSecondary
                    )

                    // Kaç kişi oy verdi chip
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = BPColors.primaryLight
                    ) {
                        Text(
                            text     = "${businessInfo?.count_star ?: 0} puan verildi",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color    = BPColors.primary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            HorizontalDivider(
                color    = BPColors.divider,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 14.dp)
            )

            // ── Puan Verme Alanı ───────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Filled.Grade, null,
                    tint     = BPColors.starYellow,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text       = if (userStarValue != null) "Puanınız" else "Puan Ver",
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = BPColors.textPrimary
                )
            }

            Spacer(Modifier.height(12.dp))

            if (isLoading) {
                // Sadece yıldız alanında spinner
                Box(
                    modifier         = Modifier.fillMaxWidth().height(52.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color       = BPColors.primary,
                        modifier    = Modifier.size(28.dp),
                        strokeWidth = 3.dp
                    )
                }
            } else {
                // 5 yıldız
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    repeat(5) { i ->
                        val starIndex = i + 1
                        val isSelected = starIndex <= displayedStar

                        val scale by animateFloatAsState(
                            targetValue = if (isSelected) 1.15f else 1f,
                            animationSpec = spring(dampingRatio = 0.5f),
                            label = "starScale"
                        )
                        val tintColor by animateColorAsState(
                            targetValue = if (isSelected) BPColors.starYellow else BPColors.starGray,
                            animationSpec = tween(150),
                            label = "starColor"
                        )

                        Icon(
                            imageVector        = if (isSelected) Icons.Filled.Star
                            else Icons.Outlined.StarBorder,
                            contentDescription = "$starIndex yıldız",
                            tint               = tintColor,
                            modifier           = Modifier
                                .size(44.dp)
                                .scale(scale)
                                .padding(2.dp)
                                .clickable(
                                    enabled           = canRate,
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication        = null
                                ) {
                                    if (canRate) {
                                        hoveredStar = starIndex
                                        onStarSend(starIndex.toDouble())
                                    }
                                }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Durum mesajı
                val statusText = when {
                    userStarValue != null ->
                        "✓  ${userStarValue.toInt()} yıldız verdiniz — teşekkürler!"
                    hoveredStar > 0 -> "Gönderiliyor..."
                    else -> "Yıldıza dokunarak puan verin"
                }
                val statusColor = when {
                    userStarValue != null -> BPColors.successGreen
                    hoveredStar > 0       -> BPColors.primary
                    else                  -> BPColors.textSecondary
                }

                Text(
                    text      = statusText,
                    fontSize  = 12.sp,
                    color     = statusColor,
                    fontWeight = if (userStarValue != null) FontWeight.SemiBold else FontWeight.Normal,
                    modifier  = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                // Hata mesajı
                if (errorMsg.isNotBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Surface(
                        shape    = RoundedCornerShape(8.dp),
                        color    = BPColors.errorRedBg,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text     = errorMsg,
                            fontSize = 12.sp,
                            color    = BPColors.errorRed,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
    Spacer(Modifier.height(4.dp))
}

// ─── Yorum Kartı ──────────────────────────────────────────────────────────────
@Composable
private fun CommentCard(
    comment    : Business_Comment,
    senderInfo : User_Or_Business?
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy  HH:mm", Locale("tr"))
    val dateStr    = dateFormat.format(comment.created_date.toDate())
    val fallback   = comment.sender_id.take(2).uppercase()

    val displayName = displayNameFor(senderInfo)
    val photoUrl    = photoUrlFor(senderInfo)
    val initials    = initialsFor(senderInfo, fallback)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(3.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = BPColors.cardBg
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier         = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(BPColors.primaryLight),
                    contentAlignment = Alignment.Center
                ) {
                    if (photoUrl != null) {
                        AsyncImage(
                            model              = photoUrl,
                            contentDescription = displayName,
                            contentScale       = ContentScale.Crop,
                            modifier           = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    } else {
                        Text(
                            text       = initials,
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color      = BPColors.primary
                        )
                    }
                }

                Spacer(Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    if (senderInfo == null) {
                        Text("Yükleniyor...", fontSize = 13.sp, color = BPColors.textSecondary)
                    } else {
                        Text(
                            text       = displayName,
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = BPColors.textPrimary,
                            maxLines   = 1,
                            overflow   = TextOverflow.Ellipsis
                        )
                        if (senderInfo is BusinessInfo) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Store, null,
                                    tint     = BPColors.accent,
                                    modifier = Modifier.size(11.dp))
                                Spacer(Modifier.width(3.dp))
                                Text("İşletme", fontSize = 10.sp,
                                    color = BPColors.accent, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Schedule, null,
                            tint     = BPColors.textSecondary,
                            modifier = Modifier.size(11.dp))
                        Spacer(Modifier.width(3.dp))
                        Text(dateStr, fontSize = 11.sp, color = BPColors.textSecondary)
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = BPColors.divider, thickness = 0.8.dp)
            Spacer(Modifier.height(10.dp))

            Text(
                text       = comment.comment,
                fontSize   = 13.sp,
                color      = BPColors.textSecondary,
                lineHeight = 20.sp
            )
        }
    }
}

// ─── Yorum Yazma Alanı ────────────────────────────────────────────────────────
@Composable
private fun ReviewInputSection(
    comment        : String,
    sendSuccessMsg : String,
    sendErrorMsg   : String,
    buttonEnabled  : Boolean,
    onComment      : (String) -> Unit,
    onSend         : () -> Unit
) {
    Surface(
        modifier        = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape           = RoundedCornerShape(16.dp),
        color           = BPColors.surface,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.RateReview, null,
                    tint = BPColors.primary, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Yorum Yaz", fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold, color = BPColors.primary)
            }

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value         = comment,
                onValueChange = onComment,
                placeholder   = {
                    Text("Deneyiminizi paylaşın...",
                        fontSize = 13.sp, color = BPColors.textSecondary)
                },
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(12.dp),
                minLines  = 3,
                maxLines  = 6,
                colors    = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = BPColors.primary,
                    unfocusedBorderColor = BPColors.divider,
                    focusedTextColor     = BPColors.textPrimary,
                    unfocusedTextColor   = BPColors.textPrimary,
                    cursorColor          = BPColors.primary
                )
            )

            Spacer(Modifier.height(10.dp))

            if (sendSuccessMsg.isNotBlank()) {
                Surface(
                    shape    = RoundedCornerShape(10.dp),
                    color    = BPColors.successGreenBg,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier          = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.CheckCircle, null,
                            tint = BPColors.successGreen, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(sendSuccessMsg, fontSize = 12.sp,
                            color = BPColors.successGreen, fontWeight = FontWeight.Medium)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            if (sendErrorMsg.isNotBlank()) {
                Surface(
                    shape    = RoundedCornerShape(10.dp),
                    color    = BPColors.errorRedBg,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier          = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Error, null,
                            tint = BPColors.errorRed, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(sendErrorMsg, fontSize = 12.sp,
                            color = BPColors.errorRed, fontWeight = FontWeight.Medium)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            Button(
                onClick  = onSend,
                enabled  = buttonEnabled,
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor         = BPColors.primary,
                    disabledContainerColor = BPColors.primaryLight
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!buttonEnabled) {
                    CircularProgressIndicator(
                        color       = BPColors.primary,
                        modifier    = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Gönderiliyor...",
                        fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                        color      = BPColors.primary)
                } else {
                    Icon(Icons.Filled.Send, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Gönder", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }
        }
    }
}

// ─── Ortak Composable'lar ──────────────────────────────────────────────────────

@Composable
private fun MenuSortTabRow(activeSort: MenuSortType, onSortSelected: (MenuSortType) -> Unit) {
    LazyRow(
        modifier              = Modifier.fillMaxWidth().padding(bottom = 10.dp),
        contentPadding        = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sortTabs) { tab ->
            val selected = tab.sort == activeSort
            Surface(
                shape           = RoundedCornerShape(20.dp),
                color           = if (selected) BPColors.primary else BPColors.surface,
                shadowElevation = if (selected) 4.dp else 1.dp,
                modifier        = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null
                ) { onSortSelected(tab.sort) }
            ) {
                Row(
                    modifier              = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(tab.icon, null,
                        tint     = if (selected) Color.White else BPColors.primary,
                        modifier = Modifier.size(15.dp))
                    Text(tab.label, fontSize = 12.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color      = if (selected) Color.White else BPColors.textPrimary)
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(icon: ImageVector, title: String, badge: String) {
    Column {
        Row(
            modifier          = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = BPColors.primary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BPColors.textPrimary)
            Spacer(Modifier.weight(1f))
            Surface(shape = RoundedCornerShape(20.dp), color = BPColors.primaryLight) {
                Text(badge, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                    color    = BPColors.primary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
            }
        }
        HorizontalDivider(color = BPColors.divider, thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun ShowMoreButton(
    expanded     : Boolean,
    totalCount   : Int,
    visibleCount : Int,
    label        : String,
    onToggle     : () -> Unit
) {
    OutlinedButton(
        onClick  = onToggle,
        shape    = RoundedCornerShape(12.dp),
        colors   = ButtonDefaults.outlinedButtonColors(contentColor = BPColors.primary),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Icon(
            if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
            null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(
            if (expanded) "Daha az göster" else "+${totalCount - visibleCount} $label daha göster",
            fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}

@Composable
private fun EmptyState(icon: ImageVector, message: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = BPColors.textSecondary, modifier = Modifier.size(48.dp))
            Spacer(Modifier.height(8.dp))
            Text(message, color = BPColors.textSecondary, fontSize = 14.sp)
        }
    }
}

@Composable
private fun BusinessHeader(
    businessInfo          : BusinessInfo?,
    businessPageViewModel : BusinessPageViewModel
) {
    Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
        AsyncImage(
            model              = businessInfo?.profile_photo,
            contentDescription = "Kapak",
            contentScale       = ContentScale.Crop,
            modifier           = Modifier.fillMaxSize()
        )
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0x33000000), Color(0xCC000000)))
        ))
        Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape    = RoundedCornerShape(16.dp),
                    color    = Color.White,
                    modifier = Modifier.size(72.dp).shadow(8.dp, RoundedCornerShape(16.dp))
                ) {
                    AsyncImage(
                        model              = businessInfo?.profile_photo,
                        contentDescription = "Profil",
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp))
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(businessInfo?.business_name ?: "",
                        fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White,
                        maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.LocationOn, null,
                            tint = BPColors.accent, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(3.dp))
                        Text(businessPageViewModel.business_city,
                            fontSize = 13.sp, color = Color(0xFFCFD8E3))
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatChip(Icons.Filled.Star, BPColors.starYellow,
                    String.format("%.1f", businessInfo?.average_star ?: 0.0))
                StatChip(Icons.Filled.ChatBubble, BPColors.accent,
                    "${businessInfo?.count_comments ?: 0} yorum")
            }
        }
    }
}

@Composable
private fun StatChip(icon: ImageVector, tint: Color, label: String) {
    Surface(shape = RoundedCornerShape(20.dp), color = Color(0xBB000000)) {
        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text(label, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun DescriptionSection(description: String) {
    var expanded by remember { mutableStateOf(false) }
    Surface(
        modifier        = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        shape           = RoundedCornerShape(16.dp),
        color           = BPColors.surface,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Info, null,
                    tint = BPColors.primary, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Hakkında", fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold, color = BPColors.primary)
            }
            Spacer(Modifier.height(6.dp))
            Text(description, fontSize = 13.sp, color = BPColors.textSecondary,
                lineHeight = 20.sp,
                maxLines   = if (expanded) Int.MAX_VALUE else 3,
                overflow   = TextOverflow.Ellipsis)
            if (description.length > 120) {
                TextButton(onClick = { expanded = !expanded }, contentPadding = PaddingValues(0.dp)) {
                    Text(if (expanded) "Daha az göster" else "Devamını oku",
                        fontSize = 12.sp, color = BPColors.primary)
                }
            }
        }
    }
}

@Composable
private fun MenuCard(menu: Menu, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clickable(remember { MutableInteractionSource() }, null, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = BPColors.cardBg
    ) {
        Box {
            Row(
                modifier          = Modifier.fillMaxWidth().padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape    = RoundedCornerShape(12.dp),
                    color    = BPColors.surface,
                    modifier = Modifier.size(90.dp)
                ) {
                    AsyncImage(
                        model              = menu.food_photo_url,
                        contentDescription = menu.food_name,
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(menu.food_name,
                        fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BPColors.textPrimary,
                        maxLines = 1, overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(end = 8.dp))
                    Text(menu.food_description,
                        fontSize = 12.sp, color = BPColors.textSecondary,
                        maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 17.sp)
                    Spacer(Modifier.height(2.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MiniStat(Icons.Filled.Star,       BPColors.starYellow,
                            String.format("%.1f", menu.averageLike))
                        MiniStat(Icons.Filled.ChatBubble, BPColors.accent,
                            "${menu.count_command}")
                        MiniStat(Icons.Filled.TrendingUp, BPColors.primary,
                            String.format("%.1f", menu.price_performance))
                    }
                    Spacer(Modifier.height(2.dp))
                    Surface(shape = RoundedCornerShape(8.dp), color = BPColors.primaryLight) {
                        Text("₺${String.format("%.2f", menu.food_price)}",
                            fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BPColors.primary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp))
                    }
                }
            }
            Surface(
                shape    = RoundedCornerShape(topStart = 16.dp, bottomEnd = 12.dp),
                color    = if (menu.active) BPColors.activeGreenBg else BPColors.inactiveYellowBg,
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Row(
                    modifier              = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Box(modifier = Modifier
                        .size(7.dp).clip(CircleShape)
                        .background(if (menu.active) BPColors.activeGreen else BPColors.inactiveYellow))
                    Text(if (menu.active) "Aktif" else "Pasif",
                        fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
                        color    = if (menu.active) BPColors.activeGreen else BPColors.inactiveYellow)
                }
            }
        }
    }
}

@Composable
private fun MiniStat(icon: ImageVector, tint: Color, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(13.dp))
        Spacer(Modifier.width(3.dp))
        Text(value, fontSize = 11.sp, color = BPColors.textSecondary, fontWeight = FontWeight.Medium)
    }
}