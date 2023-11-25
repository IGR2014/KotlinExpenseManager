package bashlykov.ivan.expense.manager


import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import bashlykov.ivan.expense.manager.database.Category
import bashlykov.ivan.expense.manager.database.DatabaseBudget
import bashlykov.ivan.expense.manager.database.tables.Budget
import bashlykov.ivan.expense.manager.ui.theme.ExpenseManagerTheme
import java.time.format.DateTimeFormatter
import kotlin.math.abs

class ActivityMain : ComponentActivity() {

    // БД
    private val database: DatabaseBudget by lazy {
        DatabaseBudget.getDatabase(applicationContext)
    }

	// Модель получения данных из БД
	private val budgetViewModel: BudgetViewModel by viewModels {
		BudgetViewModel.provideFactory(
			database.budgetDAO(),
			owner = this
		)
	}

	// Контракт на запуск активности ввода нового значения дохода/расхода
	private val launcherNewBudget = registerForActivityResult(
		ActivityResultContracts.StartActivityForResult()
	) { result ->
		// Успешно ?
		if (result.resultCode == RESULT_OK) {
			// Android T+ ?
			val budget = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
				result.data?.getParcelableExtra(
					Budget::class.simpleName,
					Budget::class.java
				)
			} else {
				@Suppress("DEPRECATION")
				result.data?.getParcelableExtra(
					Budget::class.simpleName
				)
			}
			// Получили данные ?
			if (null != budget) {
				// Id == 0 ?
				if (0L == budget.id) {
					// Добавить новый
					budgetViewModel.insertBudget(budget)
				} else {
					// Обновить существующий
					budgetViewModel.updateBudget(budget)
				}
			}
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		// Создадим начальный экрна с иконкой приложения и анимацией
		installSplashScreen().setOnExitAnimationListener { viewProvider ->
			viewProvider.iconView
				.animate()
				.setDuration(
					// В случае первого запуска
					savedInstanceState?.let {
						2000
					} ?: 0
				)
				.withEndAction {
					// Закроем начальный экран
					viewProvider.remove()
					// Задаём GUI
					setContent {
						ExpenseManagerApp()
					}
				}
				.start()
		}
	}


	@Composable
	fun BudgetCard(budget: Budget) {
		Card(
			modifier = Modifier
				.fillMaxWidth()
				.padding(4.dp)
		) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.background(
						if (budget.amount > 0) {
							Color(.5f, 1f, .5f)
						} else if (budget.amount < 0) {
							Color(1f, .5f, .5f)
						} else {
							MaterialTheme.colorScheme.primary
						}
					)
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
							imageVector = if (budget.amount > 0) {
								Icons.Filled.KeyboardArrowUp
							} else if (budget.amount < 0) {
								Icons.Filled.KeyboardArrowDown
							} else {
								Icons.Filled.MoreVert
							},
							contentDescription = "Date",
							tint = if (budget.amount > 0) {
								Color(.5f, 1f, .5f)
							} else if (budget.amount < 0) {
								Color(1f, .5f, .5f)
							} else {
								MaterialTheme.colorScheme.primary
							},
							modifier = Modifier
								.size(30.dp)
								.padding(end = 4.dp)
						)

						Text(
							text = "${abs(budget.amount)}",
							style = MaterialTheme.typography.headlineSmall,
							fontWeight = FontWeight.Bold,
							color = if (budget.amount > 0) {
								Color(.5f, 1f, .5f)
							} else if (budget.amount < 0) {
								Color(1f, .5f, .5f)
							} else {
								MaterialTheme.colorScheme.primary
							}
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
								text = budget.dateTime.format(
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
						text = budget.category.name,
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
	fun BudgetListView(modifier: Modifier = Modifier) {
		// В режиме Preview?
		val budgetItems by if (LocalInspectionMode.current) {
			// Ленивое обращение к БД
			remember {
				mutableStateOf<List<Budget>>(
					mutableListOf(
						Budget(
							comment = "Income",
							amount = 5000,
							category = Category.SALARY
						),
						Budget(
							comment = "Outcome",
							amount = -1000,
							category = Category.RENT
						),
						Budget(
							comment = "Outcome",
							amount = -300,
							category = Category.PETS
						),
						Budget(
							comment = "Outcome",
							amount = -2200,
							category = Category.ENTERTAINMENT
						),
						Budget(
							comment = "Income",
							amount = 10000,
							category = Category.SALARY
						),
						Budget(
							comment = "Outcome",
							amount = -1000,
							category = Category.TRANSPORT
						)
					)
				)
			}
		} else {
			// Ленивое обращение к БД
			budgetViewModel.getAllBudget().observeAsState(mutableListOf())
		}
		// Вывод данных
		LazyColumn(
			modifier = modifier
				.padding(4.dp)
		) {
			items(budgetItems) { budgetItem ->
				BudgetCard(
					budget = budgetItem
				)
			}
		}
	}


	@Composable
	fun BudgetInfo(budget: Budget, icon: ImageVector, text: String, color: Color, modifier: Modifier = Modifier) {
		Card(
			modifier = modifier
				.fillMaxWidth()
				.padding(8.dp)
				.background(MaterialTheme.colorScheme.background)
		) {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(
						top = 24.dp,
						bottom = 24.dp,
						start = 16.dp,
						end = 16.dp
					)
			) {
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(),
					horizontalArrangement = Arrangement.spacedBy(10.dp)
				) {
					Icon(
						imageVector = icon,
						contentDescription = text,
						tint = color,
						modifier = Modifier
							.size(30.dp)
							.padding(0.dp)
					)

					Text(
						text = text,
						style = MaterialTheme.typography.titleMedium.copy(
							color = color
						)
					)
				}

				Text(
					text = "${abs(budget.amount)}",
					style = MaterialTheme.typography.headlineMedium.copy(
						color = color
					),
					textAlign = TextAlign.Center,
					modifier = Modifier.fillMaxWidth()
						.padding(
							top = 8.dp,
							bottom = 8.dp
						)
				)
			}
		}
	}

	@Composable
	fun BudgetInfoView() {
		// Ленивое обращение к БД
		val totalBudget = budgetViewModel.getAllBudget().observeAsState(mutableListOf())
		// Общий доход
		val totalIncome = totalBudget.value
			.filter { it.category == Category.INCOME || it.category == Category.SALARY }
			.sumOf { it.amount }
		// Общий расход
		val totalOutcome = totalBudget.value
			.filter { it.category != Category.INCOME && it.category != Category.SALARY }
			.sumOf { it.amount }
		// Общий бюджет
		val totalAmount = totalIncome - totalOutcome
		// Карточка с общим бюджетом
		val totalAmountCard = Budget(
			comment = "Total",
			amount = totalAmount
		)
		// Карточка с общим доходом
		val totalIncomeCard = Budget(
			comment = "Income",
			amount = totalIncome
		)
		// Карточка с общим расходом
		val totalOutcomeCard = Budget(
			comment = "Outcome",
			amount = totalOutcome
		)

		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(bottom = 16.dp)
		) {
			// Карточка с общей суммой
			BudgetInfo(
				budget = totalAmountCard,
				icon = Icons.Filled.Info,
				text = "Total",
				color = MaterialTheme.colorScheme.primary
			)

			// Карточки с общим значением доходов и расходов
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(top = 4.dp)
			) {
				// Карточка с общими доходами
				BudgetInfo(
					budget = totalIncomeCard,
					icon = Icons.Filled.KeyboardArrowUp,
					text = "Income",
					color = Color(.5f, 1f, .5f),
					modifier = Modifier.weight(1f)
				)
				// Карточка с общими раходами
				BudgetInfo(
					budget = totalOutcomeCard,
					icon = Icons.Filled.KeyboardArrowDown,
					text = "Outcome",
					color = Color(1f, .5f, .5f),
					modifier = Modifier.weight(1f)
				)
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
				Column(
					modifier = Modifier
						.fillMaxSize()
						.padding(16.dp)
				) {
					// Верхняя панель главного экрана
					TopAppBar(
						title = { Text(text = "Expense Manager") },
						navigationIcon = {
							IconButton(
								onClick = {}
							) {
								Icon(Icons.Filled.Menu, contentDescription = "Application Menu")
							}
						},
						actions = {
							IconButton(
								onClick = {
									// Запуск активности для редактирования/изменения
									launcherNewBudget.launch(
										Intent(
											applicationContext,
											ActivityBudgetChange::class.java
										)
									)
								}
							) {
								Icon(
									imageVector = Icons.Filled.Add,
									contentDescription = "Add Budget"
								)
							}
							IconButton(
								onClick = {
									// Запуск активности статистики
									startActivity(
										Intent(
											applicationContext,
											ActivityStatistics::class.java
										)
									)
								}
							) {
								Icon(
									imageVector = Icons.Filled.List,
									contentDescription = "Open Statistics")
							}
							IconButton(
								onClick = {
									// Запуск активности настроек
									startActivity(
										Intent(
											applicationContext,
											ActivitySettings::class.java
										)
									)
								}
							) {
								Icon(
									imageVector = Icons.Filled.Settings,
									contentDescription = "Open Settings")
							}
						}
					)

					// Информация о бюджете
					BudgetInfoView()

					// Список доходов/расходов
					BudgetListView()
				}
			}
		}
	}


	@Composable
	@Preview(
		name = "Budget Card (Dark)",
		showBackground = true,
		uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
	)
	@Preview(
		name = "Budget Card",
		showBackground = true,
		uiMode = Configuration.UI_MODE_NIGHT_NO
	)
	fun BudgetCardPreview() {
		// Тестовые данные
		val sampleBudget = Budget(
			amount = 500,
			category = Category.ENTERTAINMENT,
			comment = "Sample Comment"
		)

		// Превью тестовых данных
		BudgetCard(
			budget = sampleBudget
		)
	}

	@Composable
	@Preview(
		name = "Expense Manager Activity (Dark)",
		showBackground = true,
		uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
	)
	@Preview(
		name = "Expense Manager Activity",
		showBackground = true,
		uiMode = Configuration.UI_MODE_NIGHT_NO
	)
	fun ExpenseManagerAppPreview() {

		// Превью главного окна
		ExpenseManagerApp()

	}


}
