package com.parkloyalty.lpr.scan.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DBMigration {

    val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE welcome_form ADD COLUMN city_zone_name_code TEXT")
        }
    }
    val MIGRATION_6_7 = object : Migration(6, 7) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "CREATE TABLE `UnUploadFacsimileImage` (`lprnumber` TEXT NOT NULL,`uploadedCitationId` TEXT NOT NULL," +
                        " `ticketNumber` INTEGER NOT NULL,`status` INTEGER NOT NULL,`imageLink` TEXT NOT NULL,`imagePath` TEXT NOT NULL,`imageCount` INTEGER NOT NULL, PRIMARY KEY(`ticketNumber`))"
            )
        }
    }

    val MIGRATION_7_8 = object : Migration(7, 8) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE welcome_form ADD COLUMN lot TEXT")
        }
    }
    val MIGRATION_8_9 = object : Migration(8, 9) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE citation_images ADD COLUMN timeImagePath TEXT")
        }
    }
    val MIGRATION_9_10 = object : Migration(9, 10) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE welcome_form ADD COLUMN officer_lookup_code TEXT")
            database.execSQL("ALTER TABLE offline_cancel_citation ADD COLUMN void_reason_lookup_code TEXT")
        }
    }
    val MIGRATION_10_11 = object : Migration(10, 11) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "CREATE TABLE `qr_code_inventory_table` (`scan_data` TEXT NOT NULL,`from` TEXT NOT NULL," +
                        " `upload_status` TEXT NOT NULL,`id` INTEGER NOT NULL, PRIMARY KEY(`id`))"
            )
        }
    }

    val MIGRATION_11_12 = object : Migration(11, 12) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE welcome_form DROP PRIMARY KEY")
            database.execSQL("ALTER TABLE welcome_form ADD COLUMN table_id INTEGER NOT NULL, PRIMARY KEY(`table_id`)")
        }
    }

    val MIGRATION_12_13 = object : Migration(12, 13) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "CREATE TABLE `inventory_to_show_table` (`equipment_id` TEXT NOT NULL,`equipment_name` TEXT NOT NULL,`is_required` INTEGER NOT NULL," +
                        " `equipment_value` TEXT NOT NULL,`is_checked_out` INTEGER NOT NULL,`last_checked_out` TEXT NOT NULL,`id` INTEGER NOT NULL, PRIMARY KEY(`id`))"
            )
        }
    }

    val MIGRATION_13_14 = object : Migration(13, 14) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE `municipal_citation_layout` (`data` TEXT NOT NULL ,`success` INTEGER,`response` TEXT ,`id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        }
    }

    val MIGRATION_14_15 = object : Migration(13, 14) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE `dataset_decal_year_list` (`decal_year_list` TEXT ,`id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE `dataset_car_make_list` (`car_make_list` TEXT ,`id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE `dataset_car_color_list` (`car_color_list` TEXT ,`id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE `dataset_state_list` (`state_list` TEXT ,`id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE `dataset_block_list` (`block_list` TEXT ,`id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE `dataset_street_list` (`street_list` TEXT ,`id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE `dataset_meter_list` (`meter_list` TEXT ,`id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE `dataset_space_list` (`space_list` TEXT ,`id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE `dataset_car_body_style_list` (`car_body_style_list` TEXT ,`id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE `dataset_violation_list` (`violation_list` TEXT ,`id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE `dataset_side_list` (`side_list` TEXT ,`id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE `dataset_tier_stem_list` (`tier_stem_list` TEXT ,`id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE `dataset_notes_list` (`notes_list` TEXT ,`id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE `dataset_remarks_list` (`remarks_list` TEXT ,`id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE `dataset_regulation_time_list` (`regulation_time_list` TEXT ,`id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE `dataset_lot_list` (`lot_list` TEXT ,`id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE `dataset_settings_list` (`settings_list` TEXT ,`id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE `dataset_cancel_reason_list` (`cancel_reason_list` TEXT ,`id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE `dataset_pbc_zone_list` (`pbc_zone_list` TEXT ,`id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE `dataset_void_and_reissue_reason_list` (`void_and_reissue_reason_list` TEXT ,`id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE `dataset_municipal_violation_list` (`municipal_violation_list` TEXT ,`id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE `dataset_municipal_block_list` (`municipal_block_list` TEXT ,`id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE `dataset_municipal_street_list` (`municipal_street_list` TEXT ,`id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE `dataset_municipal_city_list` (`municipal_city_list` TEXT ,`id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE `dataset_municipal_state_list` (`municipal_state_list` TEXT ,`id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("DROP TABLE IF EXISTS `dataset_type`")
        }
    }
    val MIGRATION_15_16 = object : Migration(14, 15) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add new column with default empty string
            database.execSQL("ALTER TABLE unUploadFacsimileImage ADD COLUMN imageType TEXT NOT NULL DEFAULT ''")
        }
    }

//    val MIGRATION_16_17 = object : Migration(15, 16) {
//        override fun migrate(database: SupportSQLiteDatabase) {
//            database.execSQL("CREATE TABLE `owner_bill_layout` (`data` TEXT NOT NULL ,`success` INTEGER,`response` TEXT ,`id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
//        }
//    }

    val MIGRATION_16_17 = object : Migration(15, 16) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE `dataset_vio_list` (`vio_list` TEXT ,`id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        }
    }
    val MIGRATION_17_18 = object : Migration(16, 17) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE `dataset_holiday_calendar_list` (`holiday_calendar_list` TEXT ,`id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        }
    }
}