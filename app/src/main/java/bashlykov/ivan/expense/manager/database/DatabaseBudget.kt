package bashlykov.ivan.expense.manager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

import bashlykov.ivan.expense.manager.database.converters.LocalDateTimeConverter
import bashlykov.ivan.expense.manager.database.dao.DaoBudget
import bashlykov.ivan.expense.manager.database.tables.Budget

// Главный класс БД
@TypeConverters(LocalDateTimeConverter::class)
@Database(entities = [Budget::class], version = 1, exportSchema = false)
abstract class DatabaseBudget : RoomDatabase() {

	abstract fun budgetDAO() : DaoBudget

	companion object {

		@Volatile
		private var INSTANCE: DatabaseBudget? = null

		@Synchronized
		fun getDatabase(context: Context): DatabaseBudget {
			return INSTANCE ?:
			synchronized(this) {
				Room.databaseBuilder(
					context,
					DatabaseBudget::class.java,
					"app_database"
				).build().also {
					INSTANCE = it
				}
			}
		}

	}

}
