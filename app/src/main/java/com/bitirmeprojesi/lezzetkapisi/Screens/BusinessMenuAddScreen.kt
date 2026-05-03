package com.bitirmeprojesi.lezzetkapisi.Screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
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

    // ── Field'ları hemen temizle, 3 saniye sonra toast'ı kaldır ──
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
            bottomBar      = { BusinessBottomBar(navController) },
            containerColor = PageBg
        ) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {

                // ── Fotoğraf Alanı ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .background(Blue700)
                        .clickable { galleryLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (photoUri != null) {
                        AsyncImage(
                            model              = photoUri,
                            contentDescription = null,
                            modifier           = Modifier.fillMaxSize(),
                            contentScale       = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(0.55f))
                                    )
                                ),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Text(
                                text     = "Değiştirmek için tıkla",
                                color    = White.copy(0.85f),
                                fontSize = 13.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .background(White.copy(0.12f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector        = Icons.Default.AddAPhoto,
                                    contentDescription = null,
                                    tint               = White,
                                    modifier           = Modifier.size(32.dp)
                                )
                            }
                            Text(
                                text       = "Fotoğraf Yükle",
                                color      = White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize   = 16.sp
                            )
                            Text(
                                text     = "Kategori otomatik tespit edilecek",
                                color    = Blue100,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // ── Kart Formu ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-20).dp)
                        .shadow(8.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(White)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        // ── Hata Mesajı ──
                        AnimatedVisibility(
                            visible = errorMessage != null,
                            enter   = fadeIn() + expandVertically(),
                            exit    = fadeOut() + shrinkVertically()
                        ) {
                            errorMessage?.let {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(ErrorBg)
                                        .border(1.dp, ErrorText.copy(0.2f), RoundedCornerShape(12.dp))
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment     = Alignment.CenterVertically
                                ) {
                                    Text(it, color = ErrorText, fontSize = 13.sp)
                                }
                            }
                        }

                        // ── Kategoriler ──
                        AnimatedVisibility(visible = categoryMap.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                                Row(
                                    modifier              = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment     = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text       = "Tespit Edilen Kategoriler",
                                        fontSize   = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color      = TextMuted
                                    )
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

                                HorizontalDivider(color = Blue50, thickness = 1.dp)
                            }
                        }

                        Text(
                            text       = "Yemek Bilgileri",
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color      = TextDark
                        )

                        MenuTextField(
                            value         = foodName,
                            onValueChange = { foodName = it },
                            label         = "Yemek Adı"
                        )

                        MenuTextField(
                            value         = foodDesc,
                            onValueChange = { foodDesc = it },
                            label         = "Açıklama",
                            minLines      = 3,
                            maxLines      = 5
                        )

                        MenuTextField(
                            value           = foodPrice,
                            onValueChange   = { foodPrice = it },
                            label           = "Fiyat (₺)",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick  = {
                                viewModel.menuAddController(foodName, foodDesc, foodPrice)
                            },
                            enabled  = viewModel.enabledMenuAddButton.value == true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape  = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor         = Blue600,
                                disabledContainerColor = Blue100
                            )
                        ) {
                            Text(
                                text       = "Menüye Ekle",
                                fontSize   = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = White
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // ── Success Toast ──
        AnimatedVisibility(
            visible  = succesMessage != null,
            enter    = fadeIn() + slideInVertically { -it },
            exit     = fadeOut() + slideOutVertically { -it },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, start = 20.dp, end = 20.dp)
                .zIndex(99f)
        ) {
            succesMessage?.let {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SuccessBg)
                        .border(1.dp, SuccessBorder, RoundedCornerShape(14.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
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
                        fontWeight = FontWeight.Medium,
                        modifier   = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun MenuTextField(
    value           : String,
    onValueChange   : (String) -> Unit,
    label           : String,
    minLines        : Int = 1,
    maxLines        : Int = 1,
    keyboardOptions : KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value           = value,
        onValueChange   = onValueChange,
        label           = { Text(label) },
        modifier        = Modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(14.dp),
        minLines        = minLines,
        maxLines        = maxLines,
        keyboardOptions = keyboardOptions,
        colors          = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = Blue600,
            unfocusedBorderColor = Blue100,
            focusedLabelColor    = Blue600,
            cursorColor          = Blue600
        )
    )
}

@Composable
fun CategoryChip(
    name      : String,
    canRemove : Boolean,
    onRemove  : () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(Blue50)
            .border(1.dp, Blue100, RoundedCornerShape(50.dp))
            .padding(start = 14.dp, end = 8.dp, top = 7.dp, bottom = 7.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text       = name,
            color      = Blue600,
            fontSize   = 13.sp,
            fontWeight = FontWeight.Medium
        )
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(if (canRemove) Blue100 else Blue50)
                .clickable(enabled = canRemove) { onRemove() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Default.Close,
                contentDescription = "Sil",
                tint               = if (canRemove) Blue600 else Blue100,
                modifier           = Modifier.size(12.dp)
            )
        }
    }
}