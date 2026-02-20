package com.parkloyalty.lpr.scan.views.fragments.search

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    @ApplicationContext val context: Context
) : ViewModel() {
}