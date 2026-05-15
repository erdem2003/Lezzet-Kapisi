package com.bitirmeprojesi.lezzetkapisi.ViewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitirmeprojesi.lezzetkapisi.Model.BusinessInfo
import com.bitirmeprojesi.lezzetkapisi.Model.Business_Comment
import com.bitirmeprojesi.lezzetkapisi.Model.Business_Stars
import com.bitirmeprojesi.lezzetkapisi.Model.Menu
import com.bitirmeprojesi.lezzetkapisi.Model.User_Or_Business
import com.bitirmeprojesi.lezzetkapisi.Repository.BusinessPageRepository
import com.bitirmeprojesi.lezzetkapisi.Repository.RegisterRepository
import kotlinx.coroutines.launch



// Sekme enum'u
enum class MenuSortType {
    NEWEST,          // createdDate desc
    MOST_COMMENTED,  // count_command desc
    HIGHEST_STAR,    // averageLike desc
    BEST_PRICE_PERF  // price_performance desc
}

class BusinessPageViewModel : ViewModel() {

    val businessPageRepository = BusinessPageRepository()
    val registerRepository= RegisterRepository()

    val business_info_state  = mutableStateOf<BusinessInfo?>(null)
    val error_message        = mutableStateOf<String>("")
    val sendMessage_success_message = mutableStateOf<String>("")
    val sendMessage_error_message=mutableStateOf<String>("")
    val enableSendMessageButton=mutableStateOf<Boolean>(true)

    val progress_bar         = mutableStateOf(true)
    val comment_list_progress_bar=mutableStateOf(true)

    // Ham liste hiç değişmez, sadece bir kere dolar
    private val _rawMenuList = mutableStateOf<List<Menu>>(emptyList())

    // Aktif sekme
    val activeSort = mutableStateOf(MenuSortType.NEWEST)

    // UI'ın okuduğu liste — activeSort değişince otomatik güncellenir
    val business_menu_list = mutableStateOf<List<Menu>>(emptyList())
    val business_comment_list=mutableStateOf<List<Business_Comment>>(emptyList())


    var business_city: String=""

    val current_comment=mutableStateOf<String>("")

    val user_business_info_map=mutableStateOf< MutableMap<String, User_Or_Business>>(mutableMapOf())

    val refresh_progress_bar=mutableStateOf<Boolean>(false)

    //Yıldız işlemleri
    val user_business_star=mutableStateOf<Business_Stars?>(null)
    val star_error_message=mutableStateOf<String>("")
    val star_progress_bar=mutableStateOf<Boolean>(false)




    // Sıralama değiştir
    fun setSortType(sort: MenuSortType) {
        activeSort.value = sort
        business_menu_list.value = sortedList(sort)
    }

    private fun sortedList(sort: MenuSortType): List<Menu> {

        if (sort == MenuSortType.NEWEST) {
            return _rawMenuList.value
                .sortedByDescending { it.createdDate.seconds }
        }

        if (sort == MenuSortType.MOST_COMMENTED) {
            return _rawMenuList.value
                .sortedByDescending { it.count_command }
        }

        if (sort == MenuSortType.HIGHEST_STAR) {
            return _rawMenuList.value
                .sortedByDescending { it.averageLike }
        }

        if (sort == MenuSortType.BEST_PRICE_PERF) {
            return _rawMenuList.value
                .sortedByDescending { it.price_performance }
        }

        return _rawMenuList.value
    }

    suspend fun business_info(business_id: String) {
        businessPageRepository.getBusinessInfoController(
            business_id = business_id,
            onError     = { error_message.value = it },
            onSuccess   = { business_info_state.value = it }
        )
    }

    suspend fun getMenuForBusiness(business_id: String) {
        businessPageRepository.getMenuListController(
            business_id,
            onError   = { error_message.value = it },
            onSuccess = { menuList ->
                _rawMenuList.value       = menuList
                business_menu_list.value = sortedList(activeSort.value)
                progress_bar.value=false
            }
        )
    }

    suspend fun getCityForBusiness(){

            registerRepository.getCitiesSuspend(onSuccess = { business_cityList->
                for(city in business_cityList){
                    if (business_info_state.value!!.city==city.plate){
                        business_city=city.city_name
                    }
                }
            }, onError = {it->
                error_message.value=it

            })


    }

    fun sendMessage(business_id: String) {
        enableSendMessageButton.value = false
        if (current_comment.value == "") {
            sendMessage_success_message.value = "Yorumunuz boş olamaz !"
            enableSendMessageButton.value = true  // bunu da açmayı unutma
            return
        }
        viewModelScope.launch {
            businessPageRepository.sendMessage(
                business_id,
                current_comment.value,
                onError = {
                    sendMessage_success_message.value = it
                },
                onSuccess = { message, newComment ->
                    sendMessage_success_message.value = message
                    current_comment.value = ""

                    //  Yeni yorumu listeye ekle
                    business_comment_list.value = business_comment_list.value + newComment

                    //  Yeni yorumun sahibinin bilgisini de map'e ekle
                    viewModelScope.launch {
                        businessPageRepository.getUserInfo(
                            newComment.sender_id,
                            onError = { },
                            onSuccess = { result ->
                                user_business_info_map.value = user_business_info_map.value
                                    .toMutableMap()
                                    .apply { put(newComment.sender_id, result) }
                            }
                        )
                    }
                }
            )
            enableSendMessageButton.value = true
        }
    }

    fun getCommentsForBusiness(business_id: String) {
        comment_list_progress_bar.value = true
        viewModelScope.launch {
            businessPageRepository.getCommentList(
                business_id,
                onError = {
                    error_message.value = "Yorumlar çekilirken bir hata oluştu"
                    comment_list_progress_bar.value = false
                },
                onSuccess = { commentList ->
                    business_comment_list.value = commentList
                    viewModelScope.launch {
                        commentList
                            .filter { !user_business_info_map.value.containsKey(it.sender_id) }
                            .map { userOrBusiness ->
                                launch {
                                    businessPageRepository.getUserInfo(
                                        userOrBusiness.sender_id,
                                        onError = { error_message.value = it },
                                        onSuccess = { result ->
                                            //  Yeni map ata, Compose takip eder
                                            user_business_info_map.value = user_business_info_map.value
                                                .toMutableMap()
                                                .apply { put(userOrBusiness.sender_id, result) }
                                        }
                                    )
                                }
                            }.forEach { it.join() }

                        comment_list_progress_bar.value = false //  hepsi bitince kapat
                    }
                }
            )
        }
    }

    fun loadPage(business_id: String) {
        viewModelScope.launch {
            progress_bar.value = true
            business_info(business_id) //Business'ın bilgilerini aldık.
            if (error_message.value==""){ //Sorun yok pipe devam edicek
                getCityForBusiness()  //Business'ın şehrini aldık
                if (error_message.value==""){
                    getMenuForBusiness(business_id)
                }
            }
        }
    }

    fun refreshPage(business_id: String) {
        refresh_progress_bar.value=true
        // state'leri sıfırla
        business_info_state.value = null
        business_comment_list.value = emptyList()
        business_menu_list.value = emptyList()
        user_business_info_map.value = mutableMapOf()
        error_message.value = ""
        sendMessage_success_message.value = ""
        // yeniden yükle
        loadPage(business_id)
        getCommentsForBusiness(business_id)
        refresh_progress_bar.value=false
    }




    //Yıldız için
    fun userBusinessStar(business_id:String){
        star_progress_bar.value=true
        viewModelScope.launch {
            businessPageRepository.getBusinessStarForUser(business_id, onError = { errormsg->
                star_error_message.value=errormsg
            }, onSuccess = { business_star->
                user_business_star.value=business_star

            })
            star_progress_bar.value=false
        }
    }

    fun sendBusinessStar(business_id: String,starValue: Double){
        star_progress_bar.value=true
        viewModelScope.launch {
            businessPageRepository.sendBusinessStar(business_id,starValue, onError = {
                star_error_message.value=it
            }, onSuccess = {business_stars->
                user_business_star.value=business_stars
            })
            star_progress_bar.value=false
        }
    }






}

