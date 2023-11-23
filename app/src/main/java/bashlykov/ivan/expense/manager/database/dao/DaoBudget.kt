package bashlykov.ivan.expense.manager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

import bashlykov.ivan.expense.manager.database.tables.Budget
import kotlinx.coroutines.flow.Flow

// Доступ к данным таблицы Budget
@Dao
interface DaoBudget {

	// Получение списка из БД
	@Query("SELECT * FROM Budget")
	fun getBudget() : LiveData<List<Budget>>

	// Добавление нового значения в БД
	@Insert
	suspend fun insertBudget(budget: Budget)

	// Изменение существующего значения в БД
	@Update
	suspend fun updateBudget(budget: Budget)

}
