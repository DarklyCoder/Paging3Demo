package com.darklycoder.paging3demo.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn

class UserViewModel : ViewModel() {

    private val config = PagingConfig(
        pageSize = 10,
        prefetchDistance = 2,
        initialLoadSize = 10
    )

    val flow = Pager(config = config) { UserPagingSource() }.flow.cachedIn(viewModelScope)
}