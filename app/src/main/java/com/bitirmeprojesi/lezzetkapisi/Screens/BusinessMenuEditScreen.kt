package com.bitirmeprojesi.lezzetkapisi.Screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.bitirmeprojesi.lezzetkapisi.Components.BusinessBottomBar
import com.bitirmeprojesi.lezzetkapisi.ViewModels.MenuEditViewModel
import kotlinx.coroutines.delay

// ── Renk paleti (MenuAddScreen ile aynı) ─────────────────────────────────────
private val Blue700       = Color(0xFF0D3E7A)
private val Blue600       = Color(0xFF185FA5)
private val Blue100       = Color(0xFFB5D4F4)
private val Blue50        = Color(0xFFE6F1FB)
private val White         = Color(0xFFFFFFFF)
private val PageBg        = Color(0xFFF0F4FA)
private val TextDark      = Color(0xFF0C1A2E)
private val TextMuted     = Color(0xFF5A7399)
private val ErrorBg       = Color(0xFFFCEBEB)
private val ErrorText     = Color(0xFFA32D2D)
private val SuccessBg     = Color(0xFFE8F5E9)
private val SuccessText   = Color(0xFF2E7D32)
private val SuccessBorder = Color(0xFFA5D6A7)
private val CardShadow    = Color(0x14000000)

// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun BusinessMenuEditScreen(
    menu_id: String,
    viewModel: MenuEditViewModel,
    navController: NavController
) {
    val menu           by viewModel.menu
    val errorMessage   by viewModel.error_message
    val successMessage by viewModel.success_message
    val isLoading      by viewModel.isLoading

    // Mevcut menüden field'ları doldur (tek seferlik)
    var foodName  by remember { mutableStateOf("") }
    var foodDesc  by remember { mutableStateOf("") }
    var foodPrice by remember { mutableStateOf("") }

    // Menü yüklendikten sonra field'ları doldur
    LaunchedEffect(menu) {
        menu?.let {
            foodName  = it.food_name
            foodDesc  = it.food_description
            foodPrice = it.food_price.toString()
        }
    }

    // Ekran ilk açıldığında veriyi çek
    LaunchedEffect(menu_id) {
        viewModel.showMenu(menu_id)
    }

    // Başarı mesajını 3 sn sonra temizle
    LaunchedEffect(successMessage) {
        if (successMessage.isNotEmpty()) {
            delay(3000)
            viewModel.success_message.value = ""
        }
    }

    // Hata mesajını 3 sn sonra temizle
    LaunchedEffect(errorMessage) {
        if (errorMessage.isNotEmpty()) {
            delay(3000)
            viewModel.error_message.value = ""
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

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
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Sol: geri butonu + başlık
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(White.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication        = null
                                    ) { navController.popBackStack() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector        = Icons.Default.ArrowBack,
                                    contentDescription = "Geri",
                                    tint               = White,
                                    modifier           = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text       = "Menüyü Düzenle",
                                    color      = White,
                                    fontSize   = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text     = "Bilgileri güncelleyip kaydedin",
                                    color    = Blue100,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Sağ: ikon
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(White.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector        = Icons.Default.Edit,
                                contentDescription = "Düzenle",
                                tint               = White,
                                modifier           = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            },
            bottomBar = { BusinessBottomBar(navController) }
        ) { padding ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // ── Yükleniyor göstergesi ───────────────────────────────────
                AnimatedVisibility(
                    visible  = isLoading,
                    modifier = Modifier.align(Alignment.Center),
                    enter    = fadeIn(tween(200)),
                    exit     = fadeOut(tween(200))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = Blue600, strokeWidth = 3.dp)
                        Text(
                            text     = "Yükleniyor…",
                            color    = TextMuted,
                            fontSize = 14.sp
                        )
                    }
                }

                // ── İçerik (yükleme bitince göster) ────────────────────────
                AnimatedVisibility(
                    visible = !isLoading,
                    enter   = fadeIn(tween(300)),
                    exit    = fadeOut(tween(200))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {

                        // ── Fotoğraf Alanı (salt görüntü, tıklanamaz) ──────
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .background(
                                    Brush.verticalGradient(colors = listOf(Blue700, Blue600))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (menu?.food_photo_url?.isNotEmpty() == true) {
                                AsyncImage(
                                    model              = menu!!.food_photo_url,
                                    contentDescription = null,
                                    modifier           = Modifier.fillMaxSize(),
                                    contentScale       = ContentScale.Crop
                                )
                                // Gradient overlay
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(Color.Transparent, Blue700.copy(alpha = 0.7f))
                                            )
                                        )
                                )
                                // Sol alt: foto etiketi
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(16.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(White.copy(alpha = 0.18f))
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment     = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector        = Icons.Default.Image,
                                        contentDescription = null,
                                        tint               = White,
                                        modifier           = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text       = "Mevcut Fotoğraf",
                                        color      = White,
                                        fontSize   = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            } else {
                                // Fotoğraf yoksa placeholder
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .background(White.copy(alpha = 0.12f), CircleShape)
                                            .border(1.5.dp, White.copy(alpha = 0.25f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector        = Icons.Default.BrokenImage,
                                            contentDescription = null,
                                            tint               = White,
                                            modifier           = Modifier.size(30.dp)
                                        )
                                    }
                                    Text(
                                        text       = "Fotoğraf bulunamadı",
                                        color      = White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize   = 17.sp
                                    )
                                }
                            }
                        }

                        // ── Beyaz Form Kartı (fotoğraf üstüne taşıyor) ────
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-22).dp)
                                .shadow(
                                    elevation    = 12.dp,
                                    shape        = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                                    ambientColor = CardShadow,
                                    spotColor    = CardShadow
                                )
                                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                                .background(White)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(0.dp)
                            ) {

                                // ── Hata Mesajı ────────────────────────────
                                AnimatedVisibility(
                                    visible = errorMessage.isNotEmpty(),
                                    enter   = fadeIn(tween(200)) + expandVertically(),
                                    exit    = fadeOut(tween(200)) + shrinkVertically()
                                ) {
                                    if (errorMessage.isNotEmpty()) {
                                        Column {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(ErrorBg)
                                                    .border(1.dp, ErrorText.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                                    .padding(12.dp),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment     = Alignment.CenterVertically
                                            ) {
                                                Text("⚠️", fontSize = 16.sp)
                                                Text(
                                                    text     = errorMessage,
                                                    color    = ErrorText,
                                                    fontSize = 13.sp,
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                            Spacer(Modifier.height(16.dp))
                                        }
                                    }
                                }

                                // ── Bölüm Başlığı ──────────────────────────
                                Row(
                                    verticalAlignment     = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier              = Modifier.padding(bottom = 20.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(Blue50, RoundedCornerShape(10.dp))
                                            .border(1.dp, Blue100, RoundedCornerShape(10.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector        = Icons.Default.Restaurant,
                                            contentDescription = null,
                                            tint               = Blue600,
                                            modifier           = Modifier.size(18.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text       = "Yemek Bilgileri",
                                            fontSize   = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color      = TextDark
                                        )
                                        Text(
                                            text     = "Güncel bilgileri girin",
                                            fontSize = 11.sp,
                                            color    = TextMuted
                                        )
                                    }
                                }

                                // ── Yemek Adı ──────────────────────────────
                                ModernTextField(
                                    value         = foodName,
                                    onValueChange = { foodName = it },
                                    label         = "Yemek Adı",
                                    placeholder   = "örn. Adana Kebap",
                                    leadingIcon   = {
                                        Icon(
                                            Icons.Default.Restaurant,
                                            contentDescription = null,
                                            tint     = if (foodName.isNotEmpty()) Blue600 else TextMuted.copy(alpha = 0.5f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                )

                                Spacer(Modifier.height(14.dp))

                                // ── Açıklama ───────────────────────────────
                                ModernTextField(
                                    value         = foodDesc,
                                    onValueChange = { foodDesc = it },
                                    label         = "Açıklama",
                                    placeholder   = "Yemeği kısaca tanıtın…",
                                    minLines      = 3,
                                    maxLines      = 5,
                                    leadingIcon   = {
                                        Icon(
                                            Icons.Default.Description,
                                            contentDescription = null,
                                            tint     = if (foodDesc.isNotEmpty()) Blue600 else TextMuted.copy(alpha = 0.5f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                )

                                Spacer(Modifier.height(14.dp))

                                // ── Fiyat ──────────────────────────────────
                                ModernTextField(
                                    value           = foodPrice,
                                    onValueChange   = { foodPrice = it },
                                    label           = "Fiyat",
                                    placeholder     = "0.00",
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    leadingIcon     = {
                                        Icon(
                                            Icons.Default.AttachMoney,
                                            contentDescription = null,
                                            tint     = if (foodPrice.isNotEmpty()) Blue600 else TextMuted.copy(alpha = 0.5f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    trailingText = "₺"
                                )

                                Spacer(Modifier.height(24.dp))

                                // ── Kaydet Butonu ──────────────────────────
                                val isEnabled = !isLoading &&
                                        foodName.isNotBlank() &&
                                        foodDesc.isNotBlank() &&
                                        foodPrice.isNotBlank()

                                Button(
                                    onClick  = {
                                        viewModel.editMenu(menu_id, foodName, foodDesc, foodPrice)
                                    },
                                    enabled  = isEnabled,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .shadow(
                                            elevation    = if (isEnabled) 6.dp else 0.dp,
                                            shape        = RoundedCornerShape(16.dp),
                                            ambientColor = Blue600.copy(alpha = 0.3f),
                                            spotColor    = Blue600.copy(alpha = 0.3f)
                                        ),
                                    shape  = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor         = Blue600,
                                        disabledContainerColor = Blue100
                                    )
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(
                                            color       = White,
                                            strokeWidth = 2.dp,
                                            modifier    = Modifier.size(20.dp)
                                        )
                                    } else {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment     = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector        = Icons.Default.Save,
                                                contentDescription = null,
                                                tint               = White,
                                                modifier           = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text       = "Değişiklikleri Kaydet",
                                                fontSize   = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color      = White
                                            )
                                        }
                                    }
                                }

                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }

        // ── Başarı Toast (üstten kayarak giriyor) ────────────────────────────
        AnimatedVisibility(
            visible  = successMessage.isNotEmpty(),
            enter    = fadeIn(tween(250)) + slideInVertically(tween(300)) { -it },
            exit     = fadeOut(tween(200)) + slideOutVertically(tween(250)) { -it },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 12.dp, start = 20.dp, end = 20.dp)
                .zIndex(99f)
        ) {
            if (successMessage.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp))
                        .background(SuccessBg)
                        .border(1.dp, SuccessBorder, RoundedCornerShape(14.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector        = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint               = SuccessText,
                        modifier           = Modifier.size(20.dp)
                    )
                    Text(
                        text       = successMessage,
                        color      = SuccessText,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier   = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}