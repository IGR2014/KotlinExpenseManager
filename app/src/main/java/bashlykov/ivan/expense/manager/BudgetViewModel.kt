package bashlykov.ivan.expense.manager

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import bashlykov.ivan.expense.manager.database.dao.DaoBudget
import bashlykov.ivan.expense.manager.database.tables.Budget
import kotlinx.coroutines.launch
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class BudgetViewModel(
	private val budgetDao: DaoBudget,
	private val savedStateHandle: SavedStateHandle
) : ViewModel() {

	companion object {

		fun provideFactory(
			budgetDao: DaoBudget,
			owner: SavedStateRegistryOwner,
			defaultArgs: Bundle? = null,
		): AbstractSavedStateViewModelFactory =
			object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
				override fun <T : ViewModel> create(
					key: String,
					modelClass: Class<T>,
					handle: SavedStateHandle
				): T {
					@Suppress("UNCHECKED_CAST")
					return BudgetViewModel(budgetDao, handle) as T
				}
			}
	}

	fun getAllBudget(): LiveData<List<Budget>> = runBlocking {
		return@runBlocking budgetDao.getBudget()
	}

	fun insertExpense(budget: Budget) = viewModelScope.launch(Dispatchers.IO) {
		budgetDao.insertBudget(budget)
	}

}
