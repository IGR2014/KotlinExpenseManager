package bashlykov.ivan.expense.manager

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import bashlykov.ivan.expense.manager.database.Category

import bashlykov.ivan.expense.manager.database.DatabaseBudget
import bashlykov.ivan.expense.manager.database.tables.Budget
import bashlykov.ivan.expense.manager.ui.theme.ExpenseManagerTheme
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {

	// БД
	private val database: DatabaseBudget by lazy {
		DatabaseBudget.getDatabase(applicationContext)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			ExpenseManagerApp()
		}
	}


	@Composable
	fun BudgetCard(budget: Budget) {
		Card(
			modifier = Modifier
				.fillMaxWidth()
		) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.background(if (budget.amount > 0) Color.Green else if (budget.amount < 0) Color.Red else Color.Transparent)
			) {

				Box(
					modifier = Modifier
						.width(16.dp)
				)

				Column(
					modifier = Modifier
						.fillMaxWidth()
						.background(MaterialTheme.colorScheme.primaryContainer)
						.padding(16.dp)
				) {
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.padding(bottom = 8.dp),
						horizontalArrangement = Arrangement.SpaceBetween
					) {

						Icon(
							if (budget.amount > 0) Icons.Filled.KeyboardArrowUp else if (budget.amount < 0) Icons.Filled.KeyboardArrowDown else Icons.Filled.MoreVert,
							contentDescription = "Date",
							tint = if (budget.amount > 0) Color.Green else if (budget.amount < 0) Color.Red else Color.Transparent,
							modifier = Modifier
								.size(30.dp)
								.padding(end = 4.dp)
						)

						Text(
							text = "${budget.amount}",
							style = MaterialTheme.typography.headlineSmall,
							fontWeight = FontWeight.Bold,
							color = if (budget.amount > 0) Color.Green else if (budget.amount < 0) Color.Red else MaterialTheme.colorScheme.primary
						)

						Row(
							modifier = Modifier
								.fillMaxWidth()
								.padding(top = 8.dp),
							horizontalArrangement = Arrangement.Absolute.Right
						) {

							Icon(
								Icons.Filled.DateRange,
								contentDescription = "Date",
								tint = MaterialTheme.colorScheme.primary,
								modifier = Modifier
									.size(20.dp)
									.padding(end = 4.dp)
							)

							Text(
								text = budget.date.format(
									DateTimeFormatter.ofPattern(
										"dd.MM.yyyy HH:mm:ss"
									)
								),
								style = MaterialTheme.typography.bodyMedium,
								color = MaterialTheme.colorScheme.primary
							)

						}

					}

					Text(
						text = budget.comment,
						style = MaterialTheme.typography.bodyLarge,
						color = MaterialTheme.colorScheme.primary,
						modifier = Modifier
							.padding(bottom = 8.dp)
					)

				}
			}
		}
	}

	@Composable
	fun BudgetList() {
		val budgetViewModel: BudgetViewModel = viewModel(
			factory = BudgetViewModel.provideFactory(
				database.budgetDAO(),
				owner = LocalSavedStateRegistryOwner.current
			)
		)
		val budgetItems by budgetViewModel.getAllBudget().observeAsState(mutableListOf())
		LazyColumn {
			items(budgetItems) { budgetItem ->
				BudgetCard(budgetItem)
			}
		}
	}

	@OptIn(ExperimentalMaterial3Api::class)
	@Composable
	fun ExpenseManagerApp() {

		ExpenseManagerTheme {
			Surface(
				modifier = Modifier.fillMaxSize(),
				color = MaterialTheme.colorScheme.background
			) {
				Column {

					TopAppBar(
						title = { Text(text = "Expense Manager") },
						navigationIcon = {
							IconButton(onClick = { /* Handle navigation icon click */ }) {
								Icon(Icons.Filled.Menu, contentDescription = "App Menu")
							}
						},
						actions = {
							IconButton(onClick = { /* Handle add expense action */ }) {
								Icon(Icons.Filled.Add, contentDescription = "Add Expense")
							}
						}
					)

					BudgetList()

				}
			}
		}

	}


	@Composable
	@Preview(
		showBackground = true,
		uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
	)
	fun BudgetCardPreview() {
		BudgetCard(budget = Budget(amount = 500, category = Category.ENTERTAINMENT, comment = "Sample Comment"))
	}

	@Composable
	@Preview(
		showBackground = true,
		uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
	)
	fun ExpenseManagerAppPreview() {
		ExpenseManagerApp()
	}


}
