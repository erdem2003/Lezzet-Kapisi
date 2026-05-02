package com.bitirmeprojesi.lezzetkapisi.ViewModels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitirmeprojesi.lezzetkapisi.Repository.MenuAddRepository
import com.bitirmeprojesi.lezzetkapisi.Yolo_Category_Detect_Retrofit.YoloFCD_Instance
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class MenuAddViewModel : ViewModel() {

    val repo: MenuAddRepository = MenuAddRepository()

    val photoUri = mutableStateOf<Uri?>(null)
    val categoryMap = mutableStateMapOf<Int, String>() //Int veritabanına kaydetmek için , Stringi ekrana bastırmak için

    val errorMessage = mutableStateOf<String?>(null)

    val enabledMenuAddButton = mutableStateOf<Boolean?>(false)

    fun detectCategoryfromFood(context: Context) {

        if (photoUri.value == null) {
            errorMessage.value = "Resim seçmek zorunludur."
            clearState()
            return
        }
        val uri = photoUri.value
        Log.d("YOLO", "URI: $uri")
        viewModelScope.launch { //detectCategory fonksiyonu suspend oldugundan, ne zaman biticeğini bilmiyoruz.

            try {
                // 1. URI'yi InputStream'e çevir.Yani byte çevir diyoruz
                val inputStream = context.contentResolver.openInputStream(uri!!)

                // 2. InputStream'i geçici bir dosyaya kopyala
                val tempFile = File.createTempFile("food_image", ".jpg", context.cacheDir)
                inputStream?.use { input -> //.use otomatik kapatır bellek sızıntısı olmasın diye.
                    tempFile.outputStream().use { output ->
                        input.copyTo(output) // byte byte kopyala
                    }
                }

                // 3. Dosyayı sunucunun anlayacağı formata çevir
                val requestFile = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())

                // 4. Multipart olarak paketle ("image" → sunucudaki parametre adı)
                val imagePart = MultipartBody.Part.createFormData("file", tempFile.name, requestFile)

                // 5. API isteğini gönder
                val response = YoloFCD_Instance.yoloApi.detectCategory(imagePart)

                if (response.isSuccessful) {
                    val body = response.body() //Response olarak bize YoloResponse_FCD dataclassını döndürecek.

                    // Önce error var mı onu kontrol ettiriyorum (internette sıkıntı vs)
                    if (body != null && body.error == null) {
                        //doğru çalışacak yapı

                        if (body.status == true) {
                            errorMessage.value=null
                            enabledMenuAddButton.value=true
                            // her şey doğru, resimde dogru , cls ' da dönecek zaten
                            categoryMap.clear()
                            body.cls!!.forEach { it->
                                Log.d("YOLO","$it")
                            }

                            categoryMap.putAll(createMapfromFoodCollection(body.cls!!))

                            categoryMap.values.forEach {key->
                                Log.d("YOLO","$categoryMap.get(key)")
                            }

                        } else {
                            clearState()
                            errorMessage.value = "Girdiginiz resime göre yemek tespit edilemedi.Lütfen tekrar deneyiniz."
                        }
                    } else {
                        if (body == null) {
                            errorMessage.value = "Beklenmeyen hata"
                        } else if (body.error != null) {
                            errorMessage.value = body.error
                        } else {
                            errorMessage.value = "Beklenmeyen hata (2)"
                        }
                        clearState()
                    }
                } else {
                    //HTTP hatası (404,500 tarzı)
                    clearState()
                    errorMessage.value = "Sunucu hatası: ${response.code()}"
                }
            } catch (e: Exception) {
                clearState()
                errorMessage.value = e.message
            }
        }
    }

    suspend fun createMapfromFoodCollection(list: List<Int>) : HashMap<Int, String>{
        val food_map = repo.getFoodCollectionInfo()
        val return_map = hashMapOf<Int, String>()

        for (i in list){
            return_map.put(i , food_map.get(i).toString())
        }
        return return_map
    }

    fun clearState(){
        enabledMenuAddButton.value=false
        photoUri.value=null
        categoryMap.clear()
        errorMessage.value=null
    }

    fun categoryMapCutDelete(id : Int){
        categoryMap.remove(id)

        categoryMap.forEach { (key, value) ->
            Log.d("YOLO", "Kalan kategoriler → $key : $value")
        }


    }





}