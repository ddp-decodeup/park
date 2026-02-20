package com.parkloyalty.lpr.scan.di

import android.content.Context
import androidx.room.Room
import com.parkloyalty.lpr.scan.database.AppDatabase
import com.parkloyalty.lpr.scan.database.DBMigration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    private const val DB_NAME = "park_loyalty"
    //private const val DB_NAME = "park_loyalty_temp"

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DB_NAME
        )
            .allowMainThreadQueries() // Remove this in production for safety
            .fallbackToDestructiveMigration()
            .addMigrations(DBMigration.MIGRATION_5_6)
            .addMigrations(DBMigration.MIGRATION_6_7)
            .addMigrations(DBMigration.MIGRATION_7_8)
            .addMigrations(DBMigration.MIGRATION_8_9)
            .addMigrations(DBMigration.MIGRATION_9_10)
            .addMigrations(DBMigration.MIGRATION_10_11)
            .addMigrations(DBMigration.MIGRATION_11_12)
            .addMigrations(DBMigration.MIGRATION_12_13)
            .addMigrations(DBMigration.MIGRATION_13_14)
            .addMigrations(DBMigration.MIGRATION_14_15)
            .addMigrations(DBMigration.MIGRATION_15_16)
            .addMigrations(DBMigration.MIGRATION_16_17)
            .addMigrations(DBMigration.MIGRATION_17_18)
            .build()

    @Provides
    fun provideActivityDao(appDatabase: AppDatabase) = appDatabase.activityDao

    @Provides
    fun provideAuthDao(appDatabase: AppDatabase) = appDatabase.authDao

    @Provides
    fun provideCitationDao(appDatabase: AppDatabase) = appDatabase.citationDao

    @Provides
    fun provideDatasetDao(appDatabase: AppDatabase) = appDatabase.datasetDao

    @Provides
    fun provideInventoryDao(appDatabase: AppDatabase) = appDatabase.inventoryDao
}