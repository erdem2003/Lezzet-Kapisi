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
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

private val Blue600  = Color(0xFF185FA5)
private val Blue100  = Color(0xFFB5D4F4)
private val Blue50   = Color(0xFFE6F1FB)
private val White    = Color(0xFFFFFFFF)
private val PageBg   = Color(0xFFF4F8FE)
private val TextMuted  = Color(0xFF5A7399)
private val ErrorBg    = Color(0xFFFCEBEB)
private val ErrorText  = Color(0xFFA32D2D)

@Composable
fun BusinessMenuAddScreen(
    navController: NavHostController,
    viewModel: MenuAddViewModel
) {
    val context      = LocalContext.current
    val photoUri     by viewModel.photoUri
    val categoryMap  = viewModel.categoryMap
    val errorMessage by viewModel.errorMessage

    var foodName  by remember { mutableStateOf("") }
    var foodDesc  by remember { mutableStateOf("") }
    var foodPrice by remember { mutableStateOf("") }

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

    Scaffold(
        bottomBar      = { BusinessBottomBar(navController) },
        containerColor = PageBg
    ) { padding ->
        Text(Firebase.auth.currentUser!!.uid) //Sil.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {

            // ── Header ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(listOf(Color(0xFF0D3E7A), Blue600))
                    )
                    .padding(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 28.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text       = "Menü Ekle",
                        fontSize   = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color      = White
                    )
                    Text(
                        text       = "Fotoğraf yükle, kategoriler otomatik tespit edilsin",
                        fontSize   = 13.sp,
                        color      = Blue100,
                        lineHeight = 18.sp
                    )
                }
            }

            // ── İçerik ──
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                // ── Fotoğraf Alanı ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .shadow(4.dp, RoundedCornerShape(20.dp), clip = false)
                        .clip(RoundedCornerShape(20.dp))
                        .background(White)
                        .clickable { galleryLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (photoUri != null) {
                        AsyncImage(
                            model            = photoUri,
                            contentDescription = null,
                            modifier         = Modifier.fillMaxSize(),
                            contentScale     = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.28f)),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Text(
                                text     = "Değiştirmek için tıkla",
                                color    = White,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(Blue50, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector        = Icons.Default.AddAPhoto,
                                    contentDescription = null,
                                    tint               = Blue600,
                                    modifier           = Modifier.size(26.dp)
                                )
                            }
                            Text(
                                text       = "Fotoğraf Yükle",
                                color      = Blue600,
                                fontWeight = FontWeight.SemiBold,
                                fontSize   = 15.sp
                            )
                            Text(
                                text     = "Kategori otomatik tespit edilecek",
                                color    = TextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

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
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text       = "Tespit Edilen Kategoriler",
                                fontSize   = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = TextMuted
                            )
                            // En az 1 kalması gerektiğini kullanıcıya bildir
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
                                    name     = name,
                                    // Sadece 1'den fazla varsa sil butonu aktif
                                    canRemove = categoryMap.size > 1,
                                    onRemove = {
                                        if (categoryMap.size > 1) {
                                            viewModel.categoryMapCutDelete(id)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = Blue100, thickness = 1.dp)

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

                Button(
                    onClick  = { /* kayıt işlemi */ },
                    enabled =viewModel.enabledMenuAddButton.value!! , //enabled
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape  = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue600)
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
    canRemove : Boolean,  // ← 1 kaldığında false gelir
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
                // 1 kaldığında soluk göster
                .background(if (canRemove) Blue100 else Blue50)
                .clickable(enabled = canRemove) { onRemove() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Default.Close,
                contentDescription = "Sil",
                // 1 kaldığında soluk göster
                tint               = if (canRemove) Blue600 else Blue100,
                modifier           = Modifier.size(12.dp)
            )
        }
    }
}