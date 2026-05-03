package com.bitirmeprojesi.lezzetkapisi.Screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SoupKitchen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.bitirmeprojesi.lezzetkapisi.Components.BusinessBottomBar
import com.bitirmeprojesi.lezzetkapisi.ViewModels.MenuAddViewModel
import kotlinx.coroutines.delay

// ── Renkler (MenuViewScreen ile aynı palet) ───────────────────────────────────
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
fun BusinessMenuAddScreen(
    navController: NavHostController,
    viewModel: MenuAddViewModel
) {
    val context       = LocalContext.current
    val photoUri      by viewModel.photoUri
    val categoryMap   = viewModel.categoryMap
    val errorMessage  by viewModel.errorMessage
    val succesMessage by viewModel.succesMessage

    var foodName  by remember { mutableStateOf("") }
    var foodDesc  by remember { mutableStateOf("") }
    var foodPrice by remember { mutableStateOf("") }

    // Field'ları hemen temizle, 3 sn sonra toast'ı kaldır
    LaunchedEffect(succesMessage) {
        if (succesMessage != null) {
            foodName  = ""
            foodDesc  = ""
            foodPrice = ""
            delay(3000)
            viewModel.succesMessage.value = null
        }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.clearState() }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.photoUri.value = it
            viewModel.detectCategoryfromFood(context)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Scaffold(
            containerColor = PageBg,
            // ── Top Bar – MenuViewScreen ile birebir aynı yapı ─────────────────
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
                        // Sol: başlık + alt açıklama
                        Column {
                            Text(
                                text       = "Yeni Menü Ekle",
                                color      = White,
                                fontSize   = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text     = "Fotoğraf yükle & yayınla",
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
                                    indication = null
                                ) {
                                    // TODO: şef profiline veya ilgili sayfaya yönlendir
                                    Log.d("Deneme","Logo basıldı")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SoupKitchen,
                                contentDescription = "Şef",
                                tint = White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            },
            bottomBar = { BusinessBottomBar(navController) }
        ) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {

                // ── Fotoğraf Alanı ─────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(
                            Brush.verticalGradient(colors = listOf(Blue700, Blue600))
                        )
                        .clickable { galleryLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (photoUri != null) {
                        // Fotoğraf seçilmişse göster
                        AsyncImage(
                            model              = photoUri,
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
                        // Sol alt: değiştir etiketi
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
                                imageVector        = Icons.Default.Edit,
                                contentDescription = null,
                                tint               = White,
                                modifier           = Modifier.size(13.dp)
                            )
                            Text(
                                text       = "Fotoğrafı değiştir",
                                color      = White,
                                fontSize   = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        // Fotoğraf henüz seçilmedi – upload alanı
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
                                    imageVector        = Icons.Default.AddAPhoto,
                                    contentDescription = null,
                                    tint               = White,
                                    modifier           = Modifier.size(30.dp)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text       = "Fotoğraf Ekle",
                                    color      = White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize   = 17.sp
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text     = "Kategori otomatik tespit edilecek",
                                    color    = Blue100,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                // ── Beyaz Form Kartı (fotoğraf üstüne taşıyor) ────────────────
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

                        // ── Hata Mesajı ────────────────────────────────────────
                        AnimatedVisibility(
                            visible = errorMessage != null,
                            enter   = fadeIn(tween(200)) + expandVertically(),
                            exit    = fadeOut(tween(200)) + shrinkVertically()
                        ) {
                            errorMessage?.let {
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
                                    Text(it, color = ErrorText, fontSize = 13.sp, modifier = Modifier.weight(1f))
                                }
                                Spacer(Modifier.height(16.dp))
                            }
                        }

                        // ── Kategori Chip'leri ─────────────────────────────────
                        AnimatedVisibility(
                            visible = categoryMap.isNotEmpty(),
                            enter   = fadeIn(tween(300)) + expandVertically(),
                            exit    = fadeOut(tween(200)) + shrinkVertically()
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                                Row(
                                    modifier              = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment     = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment     = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(Blue600, CircleShape)
                                        )
                                        Text(
                                            text       = "Tespit Edilen Kategoriler",
                                            fontSize   = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color      = TextMuted
                                        )
                                    }
                                    if (categoryMap.size == 1) {
                                        Text(
                                            text     = "En az 1 kategori zorunlu",
                                            fontSize = 11.sp,
                                            color    = ErrorText
                                        )
                                    }
                                }

                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement   = Arrangement.spacedBy(8.dp)
                                ) {
                                    categoryMap.forEach { (id, name) ->
                                        CategoryChip(
                                            name      = name,
                                            canRemove = categoryMap.size > 1,
                                            onRemove  = {
                                                if (categoryMap.size > 1) {
                                                    viewModel.categoryMapCutDelete(id)
                                                }
                                            }
                                        )
                                    }
                                }

                                HorizontalDivider(
                                    color     = Blue50,
                                    thickness = 1.dp,
                                    modifier  = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }

                        // ── Bölüm Başlığı ──────────────────────────────────────
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
                                    text     = "Tüm alanları doldurun",
                                    fontSize = 11.sp,
                                    color    = TextMuted
                                )
                            }
                        }

                        // ── Yemek Adı ──────────────────────────────────────────
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

                        // ── Açıklama ───────────────────────────────────────────
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

                        // ── Fiyat ──────────────────────────────────────────────
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

                        // ── Ekle Butonu ────────────────────────────────────────
                        val isEnabled = viewModel.enabledMenuAddButton.value == true

                        Button(
                            onClick  = {
                                viewModel.menuAddController(foodName, foodDesc, foodPrice)
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
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment     = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector        = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint               = White,
                                    modifier           = Modifier.size(18.dp)
                                )
                                Text(
                                    text       = "Menüye Ekle",
                                    fontSize   = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color      = White
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }

        // ── Başarı Toast (üstten kayarak giriyor) ────────────────────────────
        AnimatedVisibility(
            visible  = succesMessage != null,
            enter    = fadeIn(tween(250)) + slideInVertically(tween(300)) { -it },
            exit     = fadeOut(tween(200)) + slideOutVertically(tween(250)) { -it },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 12.dp, start = 20.dp, end = 20.dp)
                .zIndex(99f)
        ) {
            succesMessage?.let {
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
                        text       = it,
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

// ── Modern TextField ──────────────────────────────────────────────────────────
@Composable
fun ModernTextField(
    value           : String,
    onValueChange   : (String) -> Unit,
    label           : String,
    placeholder     : String          = "",
    minLines        : Int             = 1,
    maxLines        : Int             = 1,
    keyboardOptions : KeyboardOptions = KeyboardOptions.Default,
    leadingIcon     : (@Composable () -> Unit)? = null,
    trailingText    : String?         = null
) {
    val isFilled = value.isNotEmpty()

    OutlinedTextField(
        value           = value,
        onValueChange   = onValueChange,
        label           = {
            Text(
                text       = label,
                fontSize   = 13.sp,
                fontWeight = if (isFilled) FontWeight.SemiBold else FontWeight.Normal
            )
        },
        placeholder     = {
            Text(
                text     = placeholder,
                fontSize = 13.sp,
                color    = TextMuted.copy(alpha = 0.5f)
            )
        },
        leadingIcon     = leadingIcon,
        trailingIcon    = if (trailingText != null) ({
            Text(
                text       = trailingText,
                color      = Blue600,
                fontSize   = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier.padding(end = 4.dp)
            )
        }) else null,
        modifier        = Modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(14.dp),
        minLines        = minLines,
        maxLines        = maxLines,
        keyboardOptions = keyboardOptions,
        colors          = OutlinedTextFieldDefaults.colors(
            focusedBorderColor      = Blue600,
            unfocusedBorderColor    = if (isFilled) Blue100 else Color(0xFFDDE6F0),
            focusedLabelColor       = Blue600,
            unfocusedLabelColor     = TextMuted,
            focusedContainerColor   = White,
            unfocusedContainerColor = if (isFilled) Blue50.copy(alpha = 0.5f) else White,
            cursorColor             = Blue600
        )
    )
}

// ── Kategori Chip ─────────────────────────────────────────────────────────────
@Composable
fun CategoryChip(
    name      : String,
    canRemove : Boolean,
    onRemove  : () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(if (canRemove) Blue50 else Color(0xFFF0F4FA))
            .border(1.dp, Blue100, RoundedCornerShape(50.dp))
            .padding(start = 12.dp, end = if (canRemove) 6.dp else 12.dp, top = 7.dp, bottom = 7.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text       = name,
            color      = Blue600,
            fontSize   = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
        if (canRemove) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Blue100)
                    .clickable { onRemove() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Default.Close,
                    contentDescription = "Sil",
                    tint               = Blue700,
                    modifier           = Modifier.size(10.dp)
                )
            }
        }
    }
}