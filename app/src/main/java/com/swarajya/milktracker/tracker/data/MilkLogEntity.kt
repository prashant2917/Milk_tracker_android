package com.swarajya.milktracker.tracker.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// 1. The Entity (Table structure)
@Entity(tableName = "daily_milk_logs")
data class MilkLogEntity(
    @PrimaryKey 
    val date: String, // Store as "YYYY-MM-DD" for easy querying and sorting
    val morningQty: Float,
    val eveningQty: Float,
    val pricePerLiter: Float // Storing this per day protects historical billing
)

// 2. The DAO (Data Access Object)
@Dao
interface MilkLogDao {
    // Insert a new log, or replace it if the user is editing an existing day
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateLog(log: MilkLogEntity)

    // Get a specific day's log (useful for pre-filling the Bottom Sheet when editing)
    @Query("SELECT * FROM daily_milk_logs WHERE date = :date")
    fun getLogForDate(date: String): Flow<MilkLogEntity?>

    // Get all logs for a specific month to calculate the monthly bill
    // Using the "LIKE 'YYYY-MM-%'" SQL feature makes this very efficient
    @Query("SELECT * FROM daily_milk_logs WHERE date LIKE :yearMonth || '%'")
    fun getLogsForMonth(yearMonth: String): Flow<List<MilkLogEntity>>
}

// 3. The Database Setup
@Database(entities = [MilkLogEntity::class], version = 1, exportSchema = false)
abstract class MilkDatabase : RoomDatabase() {
    abstract fun milkLogDao(): MilkLogDao
}