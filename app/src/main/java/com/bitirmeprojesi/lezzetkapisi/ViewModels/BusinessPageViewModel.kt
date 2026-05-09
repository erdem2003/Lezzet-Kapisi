package com.bitirmeprojesi.lezzetkapisi.ViewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.bitirmeprojesi.lezzetkapisi.Model.BusinessInfo
import com.bitirmeprojesi.lezzetkapisi.Repository.BusinessPageRepository

class BusinessPageViewModel : ViewModel() {
    val businessPageRepository= BusinessPageRepository()

    val business_info_state= mutableStateOf<BusinessInfo?>(null)
    val error_message=mutableStateOf<String>("")

    fun business_info(business_id: String){

        businessPageRepository.getBusinessInfo(business_id = business_id,
            onError = {it->
                error_message.value=it
            },
            onSuccess = {business_info_it->
                business_info_state.value=business_info_it
            })

    }



}