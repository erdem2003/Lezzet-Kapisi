package com.bitirmeprojesi.lezzetkapisi.Screens

import com.bitirmeprojesi.lezzetkapisi.Model.City
import com.bitirmeprojesi.lezzetkapisi.ViewModels.RegisterViewModel
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavHostController,
    registerViewModel: RegisterViewModel
) {
    // ─── STATE'LER ────────────────────────────────────────────────────────────

    // registerViewModel.errorMessage → hata mesajını UI'a yansıtır
    val errorMessage = registerViewModel.errorMessage.value

    // registerViewModel.isLoading → butonu disabled yapar, loading gösterir
    val isLoading = registerViewModel.isLoading.value

    // registerViewModel.cities → getCities() çağrısından gelen şehir listesi
    val cities = registerViewModel.cities

    // null = tip seçim ekranı, 0 = kullanıcı, 1 = işletme
    val selectedType = remember { mutableStateOf<Int?>(null) }
    val currentStep = remember { mutableStateOf(1) }

    // Ortak step-1 alanları
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val passwordConfirm = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }
    val passwordConfirmVisible = remember { mutableStateOf(false) }
    val passwordMatchError = remember { mutableStateOf(false) }

    // Kullanıcı step-2 alanları
    val username = remember { mutableStateOf("") }
    val selectedGender = remember { mutableStateOf("") }
    val userCity = remember { mutableStateOf("") }
    val userCityExpanded = remember { mutableStateOf(false) }
    val userPhotoUri = remember { mutableStateOf<Uri?>(null) }

    // İşletme step-2 alanları
    val businessName = remember { mutableStateOf("") }
    val businessDescription = remember { mutableStateOf("") }
    val businessCity = remember { mutableStateOf("") }
    val businessCityExpanded = remember { mutableStateOf(false) }
    val businessPhotoUri = remember { mutableStateOf<Uri?>(null) }

    val accentColor = Color(0xFF1877F2)
    val errorColor = Color(0xFFFF3B30)
    val backgroundColor = Color(0xFFFFFFFF)
    val surfaceColor = Color(0xFFF5F5F5)
    val borderColor = Color(0xFFE5E5EA)
    val textPrimary = Color(0xFF1C1C1E)
    val textSecondary = Color(0xFF8E8E93)

    val userImagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> userPhotoUri.value = uri }

    val businessImagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> businessPhotoUri.value = uri }

    // Ekran açılınca şehirleri yükle
    // registerViewModel.getCities() → Repository'den şehirleri çeker, cities state'ini doldurur
    LaunchedEffect(Unit) {
        registerViewModel.getCities()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(48.dp))

            // ── HEADER ───────────────────────────────────────────────────────
            Text(
                text = "Lezzet Kapısı",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = when {
                    selectedType.value == null -> "Hesap türünü seç"
                    currentStep.value == 1 -> "Adım 1 / 2  —  Giriş bilgileri"
                    selectedType.value == 0 -> "Adım 2 / 2  —  Profil bilgileri"
                    else -> "Adım 2 / 2  —  İşletme detayları"
                },
                fontSize = 14.sp,
                color = textSecondary
            )

            // Adım göstergesi — sadece tip seçildikten sonra görünür
            if (selectedType.value != null) {
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(accentColor)
                    )
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (currentStep.value == 2) accentColor else borderColor
                            )
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── TİP SEÇİMİ ───────────────────────────────────────────────────
            if (selectedType.value == null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TypeCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Person,
                        title = "Kullanıcı",
                        subtitle = "Bireysel hesap",
                        accentColor = accentColor,
                        borderColor = borderColor,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary
                    ) {
                        selectedType.value = 0
                        currentStep.value = 1
                    }
                    TypeCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Store,
                        title = "İşletme",
                        subtitle = "Kurumsal hesap",
                        accentColor = accentColor,
                        borderColor = borderColor,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary
                    ) {
                        selectedType.value = 1
                        currentStep.value = 1
                    }
                }
            }

            // ── KULLANICI STEP 1 ──────────────────────────────────────────────
            if (selectedType.value == 0 && currentStep.value == 1) {
                EmailPasswordSection(
                    email = email,
                    password = password,
                    passwordConfirm = passwordConfirm,
                    passwordVisible = passwordVisible,
                    passwordConfirmVisible = passwordConfirmVisible,
                    passwordMatchError = passwordMatchError,
                    errorMessage = errorMessage,
                    accentColor = accentColor,
                    surfaceColor = surfaceColor,
                    borderColor = borderColor,
                    textSecondary = textSecondary,
                    errorColor = errorColor,
                    isLoading = false,
                    buttonText = "Devam Et",
                    onContinue = {
                        when {
                            email.value.isEmpty() || password.value.isEmpty() ->
                                // registerViewModel.errorMessage → hata mesajı state'ini günceller
                                registerViewModel.errorMessage.value = "Lütfen tüm alanları doldurun"
                            password.value != passwordConfirm.value ->
                                passwordMatchError.value = true
                            else -> {
                                registerViewModel.errorMessage.value = ""
                                currentStep.value = 2
                            }
                        }
                    },
                    onBack = { selectedType.value = null }
                )
            }

            // ── KULLANICI STEP 2 ──────────────────────────────────────────────
            if (selectedType.value == 0 && currentStep.value == 2) {
                Column(modifier = Modifier.fillMaxWidth()) {

                    // Profil fotoğrafı — opsiyonel
                    // Seçilmezse registerViewModel.registerUser() cinsiyete göre default atar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                            .clickable { userImagePicker.launch("image/*") }
                            .padding(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    if (userPhotoUri.value != null) Color.Transparent
                                    else Color(0xFFE6F1FB)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (userPhotoUri.value != null) {
                                AsyncImage(
                                    model = userPhotoUri.value,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                                )
                            } else {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (userPhotoUri.value != null) "Fotoğraf seçildi" else "Profil fotoğrafı",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = textPrimary
                            )
                            Text(
                                text = "Opsiyonel · Galeriden seç",
                                fontSize = 12.sp,
                                color = textSecondary
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // Kullanıcı adı
                    RegisterTextField(
                        value = username.value,
                        onValueChange = { username.value = it },
                        placeholder = "Kullanıcı adı",
                        leadingIcon = Icons.Default.AlternateEmail,
                        accentColor = accentColor,
                        surfaceColor = surfaceColor,
                        borderColor = borderColor,
                        textSecondary = textSecondary
                    )

                    Spacer(Modifier.height(12.dp))

                    // Cinsiyet seçimi
                    Text("Cinsiyet", fontSize = 12.sp, color = textSecondary)
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        GenderOption(
                            modifier = Modifier.weight(1f),
                            label = "Erkek",
                            selected = selectedGender.value == "male",
                            accentColor = accentColor,
                            borderColor = borderColor,
                            textSecondary = textSecondary
                        ) { selectedGender.value = "male" }

                        GenderOption(
                            modifier = Modifier.weight(1f),
                            label = "Kadın",
                            selected = selectedGender.value == "female",
                            accentColor = accentColor,
                            borderColor = borderColor,
                            textSecondary = textSecondary
                        ) { selectedGender.value = "female" }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Şehir dropdown
                    // registerViewModel.cities → getCities() ile doldurulan liste
                    CityDropdown(
                        selectedCity = userCity,
                        expanded = userCityExpanded,
                        cities = cities,
                        accentColor = accentColor,
                        surfaceColor = surfaceColor,
                        borderColor = borderColor,
                        textSecondary = textSecondary,
                        textPrimary = textPrimary
                    )

                    Spacer(Modifier.height(20.dp))

                    // Hata mesajı
                    // registerViewModel.errorMessage → repository'den veya validasyondan gelen mesaj
                    if (errorMessage.isNotEmpty()) {
                        ErrorMessage(errorMessage, errorColor)
                        Spacer(Modifier.height(12.dp))
                    }

                    // Kayıt ol butonu
                    // registerViewModel.registerUser() → checkUsername → Auth → Storage → Firestore
                    RegisterButton(
                        text = "Kayıt Ol",
                        isLoading = isLoading,
                        accentColor = accentColor
                    ) {
                        when {
                            username.value.isEmpty() ->
                                registerViewModel.errorMessage.value = "Kullanıcı adı zorunludur"
                            selectedGender.value.isEmpty() ->
                                registerViewModel.errorMessage.value = "Lütfen cinsiyet seçiniz"
                            userCity.value.isEmpty() ->
                                registerViewModel.errorMessage.value = "Lütfen şehir seçiniz"
                            else -> {
                                registerViewModel.registerUser(
                                    email = email.value,
                                    password = password.value,
                                    username = username.value,
                                    city = userCity.value.split("-")[0].trim(),
                                    gender = selectedGender.value,
                                    photoUri = userPhotoUri.value, // null olabilir, ViewModel/Repo halleder
                                    onSuccess = {
                                        navController.navigate("user_feed") {
                                            popUpTo("register") { inclusive = true }
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    BackTextButton(textSecondary) {
                        currentStep.value = 1
                        registerViewModel.errorMessage.value = ""
                    }
                }
            }

            // ── İŞLETME STEP 1 ───────────────────────────────────────────────
            if (selectedType.value == 1 && currentStep.value == 1) {
                EmailPasswordSection(
                    email = email,
                    password = password,
                    passwordConfirm = passwordConfirm,
                    passwordVisible = passwordVisible,
                    passwordConfirmVisible = passwordConfirmVisible,
                    passwordMatchError = passwordMatchError,
                    errorMessage = errorMessage,
                    accentColor = accentColor,
                    surfaceColor = surfaceColor,
                    borderColor = borderColor,
                    textSecondary = textSecondary,
                    errorColor = errorColor,
                    isLoading = false,
                    buttonText = "Devam Et",
                    onContinue = {
                        when {
                            email.value.isEmpty() || password.value.isEmpty() ->
                                registerViewModel.errorMessage.value = "Lütfen tüm alanları doldurun"
                            password.value != passwordConfirm.value ->
                                passwordMatchError.value = true
                            else -> {
                                registerViewModel.errorMessage.value = ""
                                currentStep.value = 2
                            }
                        }
                    },
                    onBack = { selectedType.value = null }
                )
            }

            // ── İŞLETME STEP 2 ───────────────────────────────────────────────
            if (selectedType.value == 1 && currentStep.value == 2) {
                Column(modifier = Modifier.fillMaxWidth()) {

                    // İşletme fotoğrafı — zorunlu
                    // registerViewModel.registerBusiness() null gelirse hata verir
                    val photoError = businessPhotoUri.value == null &&
                            errorMessage == "İşletme fotoğrafı zorunludur"

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                1.dp,
                                if (photoError) errorColor else borderColor,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { businessImagePicker.launch("image/*") }
                            .padding(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (businessPhotoUri.value != null) Color.Transparent
                                    else if (photoError) errorColor.copy(alpha = 0.08f)
                                    else surfaceColor
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (businessPhotoUri.value != null) {
                                AsyncImage(
                                    model = businessPhotoUri.value,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(10.dp))
                                )
                            } else {
                                Icon(
                                    Icons.Default.Store,
                                    contentDescription = null,
                                    tint = if (photoError) errorColor else textSecondary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (businessPhotoUri.value != null) "Fotoğraf seçildi" else "İşletme fotoğrafı",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (photoError) errorColor else textPrimary
                            )
                            Text(
                                text = if (photoError) "Bu alan zorunludur" else "Zorunlu · Galeriden seç",
                                fontSize = 12.sp,
                                color = if (photoError) errorColor else textSecondary
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    RegisterTextField(
                        value = businessName.value,
                        onValueChange = { businessName.value = it },
                        placeholder = "İşletme adı",
                        leadingIcon = Icons.Default.Store,
                        accentColor = accentColor,
                        surfaceColor = surfaceColor,
                        borderColor = borderColor,
                        textSecondary = textSecondary
                    )

                    Spacer(Modifier.height(12.dp))

                    // Açıklama alanı — çok satırlı
                    OutlinedTextField(
                        value = businessDescription.value,
                        onValueChange = { businessDescription.value = it },
                        placeholder = { Text("İşletme açıklaması", color = textSecondary, fontSize = 14.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Description, null, tint = textSecondary, modifier = Modifier.size(20.dp))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = borderColor,
                            focusedContainerColor = surfaceColor,
                            unfocusedContainerColor = surfaceColor,
                            cursorColor = accentColor
                        )
                    )

                    Spacer(Modifier.height(12.dp))

                    CityDropdown(
                        selectedCity = businessCity,
                        expanded = businessCityExpanded,
                        cities = cities,
                        accentColor = accentColor,
                        surfaceColor = surfaceColor,
                        borderColor = borderColor,
                        textSecondary = textSecondary,
                        textPrimary = textPrimary
                    )

                    Spacer(Modifier.height(20.dp))

                    if (errorMessage.isNotEmpty()) {
                        ErrorMessage(errorMessage, errorColor)
                        Spacer(Modifier.height(12.dp))
                    }

                    // registerViewModel.registerBusiness() → Auth → Storage → Firestore
                    RegisterButton(
                        text = "Kayıt Ol",
                        isLoading = isLoading,
                        accentColor = accentColor
                    ) {
                        when {
                            businessPhotoUri.value == null ->
                                registerViewModel.errorMessage.value = "İşletme fotoğrafı zorunludur"
                            businessName.value.isEmpty() ->
                                registerViewModel.errorMessage.value = "İşletme adı zorunludur"
                            businessDescription.value.isEmpty() ->
                                registerViewModel.errorMessage.value = "Açıklama zorunludur"
                            businessCity.value.isEmpty() ->
                                registerViewModel.errorMessage.value = "Lütfen şehir seçiniz"
                            else -> {
                                registerViewModel.registerBusiness(
                                    email = email.value,
                                    password = password.value,
                                    businessName = businessName.value,
                                    description = businessDescription.value,
                                    city = businessCity.value.split("-")[0].trim(),
                                    photoUri = businessPhotoUri.value,
                                    onSuccess = {
                                        navController.navigate("business_feed") {
                                            popUpTo("register") { inclusive = true }
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    BackTextButton(textSecondary) {
                        currentStep.value = 1
                        registerViewModel.errorMessage.value = ""
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Alt link
            HorizontalDivider(color = Color(0xFFE5E5EA), thickness = 1.dp)
            Spacer(Modifier.height(16.dp))

            Row {
                Text("Zaten hesabın var mı?  ", fontSize = 14.sp, color = textSecondary)
                Text(
                    "Giriş Yap",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = accentColor,
                    modifier = Modifier.clickable {
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ─── REUSABLE COMPONENT'LER ───────────────────────────────────────────────────

// Tip seçim kartı (Kullanıcı / İşletme)
@Composable
private fun TypeCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color,
    borderColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE6F1FB)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = accentColor, modifier = Modifier.size(26.dp))
        }
        Spacer(Modifier.height(10.dp))
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
        Spacer(Modifier.height(2.dp))
        Text(subtitle, fontSize = 12.sp, color = textSecondary)
    }
}

// Email + şifre + şifre tekrar section (step 1'de ortak kullanılır)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmailPasswordSection(
    email: MutableState<String>,
    password: MutableState<String>,
    passwordConfirm: MutableState<String>,
    passwordVisible: MutableState<Boolean>,
    passwordConfirmVisible: MutableState<Boolean>,
    passwordMatchError: MutableState<Boolean>,
    errorMessage: String,
    accentColor: Color,
    surfaceColor: Color,
    borderColor: Color,
    textSecondary: Color,
    errorColor: Color,
    isLoading: Boolean,
    buttonText: String,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        RegisterTextField(
            value = email.value,
            onValueChange = { email.value = it },
            placeholder = "Email adresi",
            leadingIcon = Icons.Default.Email,
            accentColor = accentColor,
            surfaceColor = surfaceColor,
            borderColor = borderColor,
            textSecondary = textSecondary
        )
        Spacer(Modifier.height(12.dp))

        RegisterTextField(
            value = password.value,
            onValueChange = { password.value = it },
            placeholder = "Şifre",
            leadingIcon = Icons.Default.Lock,
            accentColor = accentColor,
            surfaceColor = surfaceColor,
            borderColor = borderColor,
            textSecondary = textSecondary,
            isPassword = true,
            passwordVisible = passwordVisible.value,
            onTogglePassword = { passwordVisible.value = !passwordVisible.value }
        )
        Spacer(Modifier.height(12.dp))

        RegisterTextField(
            value = passwordConfirm.value,
            onValueChange = {
                passwordConfirm.value = it
                passwordMatchError.value = it != password.value
            },
            placeholder = "Şifre tekrar",
            leadingIcon = Icons.Default.Lock,
            accentColor = accentColor,
            surfaceColor = surfaceColor,
            borderColor = if (passwordMatchError.value) Color(0xFFFF3B30) else borderColor,
            textSecondary = textSecondary,
            isPassword = true,
            passwordVisible = passwordConfirmVisible.value,
            onTogglePassword = { passwordConfirmVisible.value = !passwordConfirmVisible.value },
            isError = passwordMatchError.value
        )

        if (passwordMatchError.value) {
            Spacer(Modifier.height(4.dp))
            Text("Şifreler eşleşmiyor", fontSize = 12.sp, color = Color(0xFFFF3B30))
        }

        Spacer(Modifier.height(20.dp))

        if (errorMessage.isNotEmpty()) {
            ErrorMessage(errorMessage, Color(0xFFFF3B30))
            Spacer(Modifier.height(12.dp))
        }

        RegisterButton(buttonText, isLoading, accentColor, onContinue)
        Spacer(Modifier.height(12.dp))
        BackTextButton(textSecondary, onBack)
    }
}

// Genel text field
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    accentColor: Color,
    surfaceColor: Color,
    borderColor: Color,
    textSecondary: Color,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = textSecondary, fontSize = 14.sp) },
        leadingIcon = {
            Icon(leadingIcon, null, tint = textSecondary, modifier = Modifier.size(20.dp))
        },
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { onTogglePassword?.invoke() }) {
                    Icon(
                        if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        null,
                        tint = textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !passwordVisible)
            PasswordVisualTransformation() else VisualTransformation.None,
        isError = isError,
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = accentColor,
            unfocusedBorderColor = borderColor,
            focusedContainerColor = surfaceColor,
            unfocusedContainerColor = surfaceColor,
            cursorColor = accentColor
        )
    )
}

// Cinsiyet seçim chip'i
@Composable
private fun GenderOption(
    modifier: Modifier = Modifier,
    label: String,
    selected: Boolean,
    accentColor: Color,
    borderColor: Color,
    textSecondary: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) accentColor else borderColor,
                shape = RoundedCornerShape(10.dp)
            )
            .background(if (selected) Color(0xFFE6F1FB) else Color.White)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) accentColor else textSecondary
        )
    }
}

// Şehir dropdown
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CityDropdown(
    selectedCity: MutableState<String>,
    expanded: MutableState<Boolean>,
    cities: List<City>,
    accentColor: Color,
    surfaceColor: Color,
    borderColor: Color,
    textSecondary: Color,
    textPrimary: Color
) {
    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = { expanded.value = it }
    ) {
        OutlinedTextField(
            value = if (selectedCity.value.isEmpty()) "" else selectedCity.value,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text("Şehir seçiniz", color = textSecondary, fontSize = 14.sp) },
            leadingIcon = {
                Icon(Icons.Default.LocationOn, null, tint = textSecondary, modifier = Modifier.size(20.dp))
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
            },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = accentColor,
                unfocusedBorderColor = borderColor,
                focusedContainerColor = surfaceColor,
                unfocusedContainerColor = surfaceColor,
                cursorColor = accentColor
            )
        )
        ExposedDropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            cities.forEach { city ->
                DropdownMenuItem(
                    text = {
                        Text(
                            "${city.plate} - ${city.city_name}",
                            fontSize = 14.sp,
                            color = textPrimary
                        )
                    },
                    onClick = {
                        selectedCity.value = "${city.plate} - ${city.city_name}"
                        expanded.value = false
                    }
                )
            }
        }
    }
}

// Kayıt / Devam butonu
@Composable
private fun RegisterButton(
    text: String,
    isLoading: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = accentColor,
            contentColor = Color.White,
            disabledContainerColor = accentColor.copy(alpha = 0.6f)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            )
        } else {
            Text(text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

// Hata mesajı kutusu
@Composable
private fun ErrorMessage(message: String, errorColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(errorColor.copy(alpha = 0.08f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = errorColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(message, fontSize = 13.sp, color = errorColor)
    }
}

// Geri dön butonu
@Composable
private fun BackTextButton(textSecondary: Color, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("← Geri dön", fontSize = 14.sp, color = textSecondary)
    }
}