package com.example.dewy.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dewy.data.models.Tip
import com.example.dewy.data.repositories.StreakRepository
import com.example.dewy.data.repositories.TipRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TipViewModel(
    private val repository: TipRepository = TipRepository()
) : ViewModel(){

    private val _tip = MutableStateFlow<Tip?>(null)
    val tip: StateFlow<Tip?> = _tip

    init {
        fetchRandomTip()
    }

    fun fetchRandomTip() {
        viewModelScope.launch(Dispatchers.Main) {
            val fetchedTip = repository.fetchRandomTip()
            if (fetchedTip != null) {
                _tip.value = Tip(
                    content = fetchedTip.content,
                    category = fetchedTip.category.replaceFirstChar { it.uppercase() }
                )
            } else {
                _tip.value = Tip("No tips available.", "General")
            }
        }
    }

}