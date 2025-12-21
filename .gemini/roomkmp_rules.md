# Room KMP Implementation Guide

Use Room for all persistence. Follow these patterns:

## 1. Database Definition (`commonMain`)

```kotlin
@Database(entities = [WaterIntakeEntity::class], version = 1)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun waterIntakeDao(): WaterIntakeDao
}

@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
```

## 2. DAO Pattern

- Always return `Flow<T>` for observable queries.
- Use `suspend` for all write operations (Insert/Delete/Update).

```kotlin
@Dao
interface WaterIntakeDao {
    @Query("SELECT * FROM water_intake ORDER BY timestamp DESC")
    fun getAllIntakes(): Flow<List<WaterIntakeEntity>>

    @Insert
    suspend fun insert(intake: WaterIntakeEntity)
}
```

## 3. Entity Pattern

- Use `timestamp` (Long) for dates (Instant/LocalDateTime mapping).
- Table names should be `snake_case`.

```kotlin
@Entity(tableName = "water_intake")
data class WaterIntakeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amountMl: Int,
    val timestamp: Long
)
```

## 4. Instantiation

- Builder provided in platform source sets (`androidMain`, `iosMain`).
- Driver: Always use `BundledSQLiteDriver()` for consistency.
- Queries should use `Dispatchers.IO`.

## 5. Repository Integration

- Repositories in `data/` layer must wrap DAOs.
- Map `Entity` to `Domain Model` in the Repository.

```kotlin
class WaterRepositoryImpl(private val dao: WaterIntakeDao) : WaterRepository {
    override fun getTodayIntake(): Flow<List<WaterIntake>> =
        dao.getAllIntakes().map { entities -> entities.map { it.toDomain() } }
}
```
