package com.parkloyalty.lpr.scan.utils.permissions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PermissionsViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow<UiState>(UiState.Idle)
    val state = _state.asStateFlow()

    fun requestPermissionsUsingManager(manager: PermissionManager, perms: Array<String>) {
        viewModelScope.launch {
            _state.value = UiState.Requesting
            val result = manager.requestPermissions(perms)
            _state.value = UiState.Result(result)

            // If non-permanent denied -> show rationale and re-request
            if (result.denied.isNotEmpty()) {
                // Use manager.dialogPresenter? We kept DialogPresenter separate; call manager.dialogPresenter indirectly by manager methods or show UI in Activity/Fragment.
                // Here for pattern demonstration we instruct caller to show rationale and re-request:
            }

            // If permanently denied -> ask to open settings (UI should show dialog)
        }
    }

    sealed class UiState {
        object Idle : UiState()
        object Requesting : UiState()
        data class Result(val result: PermissionManager.PermissionResult) : UiState()
    }
}

//How to use quick one
//lifecycleScope.launchWhenStarted {
//    permissionManager.ensurePermissionsThen(
//        permissions = arrayOf(android.Manifest.permission.CAMERA),
//        rationaleMessage = "Camera permission is required to take a picture."
//    ) {
//        // onGranted -> open camera
//        openCamera()
//    }
//}
