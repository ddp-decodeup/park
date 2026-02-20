package com.parkloyalty.lpr.scan.database.repository

import com.parkloyalty.lpr.scan.database.services.AuthDao
import com.parkloyalty.lpr.scan.ui.login.model.CommonLoginResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthDaoRepository @Inject constructor(
    private val authDao: AuthDao
) {
    suspend fun insertLogin(commonLoginResponse: CommonLoginResponse) =
        authDao.insertLogin(commonLoginResponse)

    fun getLogin(): CommonLoginResponse? = authDao.getLogin()
}