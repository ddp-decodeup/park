package com.parkloyalty.lpr.scan.database.services

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.parkloyalty.lpr.scan.ui.login.model.WelcomeForm

@Dao
interface ActivityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWelcomeForm(welcomeForm: WelcomeForm)

    @Query("SELECT * FROM welcome_form ORDER BY table_id DESC LIMIT 1")
    fun getWelcomeForm() : WelcomeForm?

    @Query("SELECT * FROM welcome_form")
    fun getWelcomeFormList() : List<WelcomeForm?>?
}