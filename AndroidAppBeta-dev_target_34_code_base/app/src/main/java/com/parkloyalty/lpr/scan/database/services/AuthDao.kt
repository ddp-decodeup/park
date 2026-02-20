package com.parkloyalty.lpr.scan.database.services

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.parkloyalty.lpr.scan.ui.login.model.CommonLoginResponse

@Dao
interface AuthDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogin(commonLoginResponse: CommonLoginResponse)

    @Query("SELECT * FROM login")
    fun getLogin(): CommonLoginResponse?
}