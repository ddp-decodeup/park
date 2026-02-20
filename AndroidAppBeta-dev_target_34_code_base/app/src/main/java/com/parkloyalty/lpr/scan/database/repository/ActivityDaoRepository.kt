package com.parkloyalty.lpr.scan.database.repository

import com.parkloyalty.lpr.scan.database.services.ActivityDao
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityDaoRepository @Inject constructor(
    private val activityDao: ActivityDao
) {
    suspend fun insertWelcomeForm(welcomeForm: WelcomeForm) =
        activityDao.insertWelcomeForm(welcomeForm = welcomeForm)

    fun getWelcomeForm(): WelcomeForm? = activityDao.getWelcomeForm()

    fun getWelcomeFormList(): List<WelcomeForm?>? = activityDao.getWelcomeFormList()
}