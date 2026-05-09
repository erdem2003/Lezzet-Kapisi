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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
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
import com.bitirmeprojesi.lezzetkapisi.Model.BusinessInfo

import com.bitirmeprojesi.lezzetkapisi.ViewModels.SearchViewModel

private val Blue700    = Color(0xFF0D3E7A)
private val Blue600    = Color(0xFF185FA5)
private val Blue100    = Color(0xFFB5D4F4)
private val Blue50     = Color(0xFFE6F1FB)
private val PageBg     = Color(0xFFF0F4FA)
private val White      = Color(0xFFFFFFFF)
private val TextDark   = Color(0xFF0C1A2E)
private val TextMuted  = Color(0xFF5A7399)
private val GoldStar   = Color(0xFFF9A825)
private val CardShadow = Color(0x14000000)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    searchViewModel: SearchViewModel
) {
    val businesses   by searchViewModel.businesses
    val errorMessage by searchViewModel.error_message
    val query        by searchViewModel.query
    val cities       by searchViewModel.cities
    val selectedCity by searchViewModel.selectedCity

    var filterExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        searchViewModel.getCities()
    }

    Scaffold(
        containerColor = PageBg,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Blue700, Blue600)))
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
                                text       = "Restoranlar",
                                color      = White,
                                fontSize   = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text     = "${businesses.size} restoran listeleniyor",
                                color    = Blue100,
                                fontSize = 11.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(White.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector        = Icons.Default.Restaurant,
                                contentDescription = null,
                                tint               = White,
                                modifier           = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // ── Arama + Filtre Satırı ─────────────────────────────────
                    Row(
                        modifier          = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Arama Çubuğu
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .background(White.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector        = Icons.Default.Search,
                                contentDescription = null,
                                tint               = White.copy(alpha = 0.7f),
                                modifier           = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            BasicTextField(
                                value         = query,
                                onValueChange = {
                                    searchViewModel.query.value = it
                                    if (it.isNotBlank()) searchViewModel.searchBusiness()
                                    else searchViewModel.businesses.value = emptyList()
                                },
                                singleLine    = true,
                                textStyle     = TextStyle(color = White, fontSize = 13.sp),
                                cursorBrush   = SolidColor(White),
                                decorationBox = { innerTextField ->
                                    Box(contentAlignment = Alignment.CenterStart) {
                                        if (query.isEmpty()) {
                                            Text(
                                                text     = "Restoran adına göre ara…",
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

                        // Filtre Butonu
                        Box {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        if (selectedCity != "all") White.copy(alpha = 0.35f)
                                        else White.copy(alpha = 0.15f),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication        = null
                                    ) { filterExpanded = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector        = Icons.Default.FilterList,
                                    contentDescription = "Filtrele",
                                    tint               = White,
                                    modifier           = Modifier.size(20.dp)
                                )
                            }

                            // Aktif filtre göstergesi (nokta)
                            if (selectedCity != "all") {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(GoldStar, RoundedCornerShape(50))
                                        .align(Alignment.TopEnd)
                                )
                            }

                            DropdownMenu(
                                expanded         = filterExpanded,
                                onDismissRequest = { filterExpanded = false },
                                modifier         = Modifier.background(White)
                            ) {
                                // "Hepsi" seçeneği
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text       = "Hepsi",
                                            color      = if (selectedCity == "all") Blue600 else TextDark,
                                            fontSize   = 13.sp,
                                            fontWeight = if (selectedCity == "all") FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        searchViewModel.selectedCity.value = "all"
                                        if (query.isNotBlank()) searchViewModel.searchBusiness()
                                        filterExpanded = false
                                    }
                                )

                                HorizontalDivider(color = Blue50, thickness = 1.dp)

                                cities.forEach { city ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text       = "${city.plate} - ${city.city_name}",
                                                color      = if (selectedCity == city.plate) Blue600 else TextDark,
                                                fontSize   = 13.sp,
                                                fontWeight = if (selectedCity == city.plate) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        onClick = {
                                            searchViewModel.selectedCity.value = city.plate
                                            if (query.isNotBlank()) searchViewModel.searchBusiness()
                                            filterExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Aktif şehir filtresi etiketi
                    if (selectedCity != "all") {
                        val cityLabel = cities.find { it.plate == selectedCity }
                            ?.let { "${it.plate} - ${it.city_name}" } ?: selectedCity
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text     = "Filtre: $cityLabel",
                            color    = GoldStar,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        },
        bottomBar = { BusinessBottomBar(navController = navController) }
    ) { paddingValues ->

        when {
            errorMessage.isNotEmpty() -> {
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
                            Text(
                                text       = errorMessage,
                                color      = Color(0xFFA32D2D),
                                fontSize   = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick  = { searchViewModel.searchBusiness() },
                                colors   = ButtonDefaults.buttonColors(containerColor = Blue600),
                                shape    = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            ) {
                                Text("Tekrar Dene", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            query.isBlank() -> {
                Box(
                    modifier         = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier         = Modifier
                                .size(96.dp)
                                .background(Blue50, RoundedCornerShape(24.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector        = Icons.Default.Search,
                                contentDescription = null,
                                tint               = Blue600,
                                modifier           = Modifier.size(48.dp)
                            )
                        }
                        Spacer(Modifier.height(20.dp))
                        Text(
                            text       = "Restoran aramak için yazmaya başlayın",
                            color      = TextDark,
                            fontSize   = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text     = "Restoran adını yukarıdaki alana girin",
                            color    = TextMuted,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            businesses.isEmpty() -> {
                Box(
                    modifier         = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier         = Modifier
                                .size(96.dp)
                                .background(Blue50, RoundedCornerShape(24.dp)),
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
                            text       = "\"$query\" için sonuç bulunamadı",
                            color      = TextDark,
                            fontSize   = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text     = "Farklı bir isim deneyin",
                            color    = TextMuted,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier            = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding      = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(
                        items = businesses,
                        key   = { _, b -> b.business_id }
                    ) { index, business ->
                        AnimatedVisibility(
                            visible = true,
                            enter   = fadeIn(tween(300, delayMillis = index * 60)) +
                                    slideInVertically(tween(300, delayMillis = index * 60)) { it / 3 }
                        ) {
                            BusinessCard(
                                business    = business,
                                onCardClick = {
                                    Log.d("search_", "info sayfası açılıyor  ${business.business_id}")
                                    navController.navigate("business_page/${business.business_id}")
                                    // İlgili business'ın detay/info sayfasına navigate edilecek
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BusinessCard(
    business: BusinessInfo,
    onCardClick: () -> Unit
) {
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
        Row(
            modifier          = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model              = business.profile_photo,
                contentDescription = business.business_name,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Blue50)
                    .border(1.dp, Blue100, RoundedCornerShape(12.dp))
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = business.business_name,
                    color      = TextDark,
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text       = business.description,
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
                        text       = String.format("%.1f", business.average_star),
                        color      = GoldStar,
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text     = "📍 ${business.city}",
                        color    = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}