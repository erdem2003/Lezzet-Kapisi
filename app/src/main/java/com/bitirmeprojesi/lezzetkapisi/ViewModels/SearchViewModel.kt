package com.bitirmeprojesi.lezzetkapisi.ViewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.bitirmeprojesi.lezzetkapisi.Model.BusinessInfo
import com.bitirmeprojesi.lezzetkapisi.Model.City
import com.bitirmeprojesi.lezzetkapisi.Repository.RegisterRepository
import com.bitirmeprojesi.lezzetkapisi.Repository.SearchRepository

class SearchViewModel : ViewModel() {

    val searchRepo         = SearchRepository()
    val registerRepository = RegisterRepository()

    val businesses   = mutableStateOf<List<BusinessInfo>>(emptyList())
    val error_message = mutableStateOf("")
    val query        = mutableStateOf("")
    val selectedCity = mutableStateOf("all")
    val cities       = mutableStateOf<List<City>>(emptyList())

    fun getCities() {
        registerRepository.getCities(
            onSuccess = { cities.value = it },
            onError   = { error_message.value = it }
        )
    }

    fun searchBusiness() {
        if (query.value.isBlank()) {
            businesses.value = emptyList()
            return
        }
        searchRepo.searchBusinessController(
            query       = query.value,
            selectedCity = selectedCity.value,
            onError     = { error_message.value = it },
            onSuccess   = { businesses.value = it }
        )
    }
}