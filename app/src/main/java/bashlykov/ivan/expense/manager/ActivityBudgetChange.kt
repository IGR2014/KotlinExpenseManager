package bashlykov.ivan.expense.manager


import android.app.TimePickerDialog
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bashlykov.ivan.expense.manager.database.Category
import bashlykov.ivan.expense.manager.database.tables.Budget
import bashlykov.ivan.expense.manager.ui.theme.ExpenseManagerTheme
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.TimeZone
import kotlin.math.abs


class ActivityBudgetChange : ComponentActivity() {

	private lateinit var currentBudget: Budget

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		// Задаём GUI
		setContent {
			ExpenseManagerNewBudget()
		}
	}

	@OptIn(ExperimentalMaterial3Api::class)
	@Composable
	fun ExpenseManagerNewBudget() {
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
					// В режиме Preview?
					currentBudget = if (LocalInspectionMode.current) {
						// Данные для примера
						Budget(
							amount = 500,
							category = Category.OTHER,
							comment = "Sample Comment"
						)
					} else {
						// Android T+ ?
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
							intent?.getParcelableExtra(
								Budget::class.simpleName,
								Budget::class.java
							)
						} else {
							@Suppress("DEPRECATION")
							intent?.getParcelableExtra(
								Budget::class.simpleName
							)
						} ?: Budget()
					}

					// Верхняя панель главного экрана
					TopAppBar(
						title = { Text(text = "Expense Manager") },
						navigationIcon = {
							IconButton(
								onClick = {
									// Установка результата
									setResult(RESULT_CANCELED)
									// Закрытие активности
									finish()
								}
							) {
								Icon(
									imageVector = Icons.Filled.ArrowBack,
									contentDescription = "Back"
								)
							}
						},
						actions = {
							IconButton(
								onClick = {
									// Установка результата
									setResult(
										RESULT_OK,
										Intent().putExtra(
											Budget::class.simpleName,
											currentBudget.copy(
												amount = when (currentBudget.category) {
													Category.INCOME,
													Category.SALARY -> {
														abs(currentBudget.amount)
													}
													Category.FOOD,
													Category.ENTERTAINMENT,
													Category.TRANSPORT,
													Category.RENT,
													Category.PETS,
													Category.HEALTH,
													Category.BEAUTY,
													Category.TRANSFER,
													Category.OTHER -> {
														-abs(currentBudget.amount)
													}
												}
											)
										)
									)
									// Закрытие активности
									finish()
								}
							) {
								Icon(
									imageVector = Icons.Filled.Check,
									contentDescription = "Add/Edit Success"
								)
							}
						}
					)

					// Редактирование или создание новой статьи доходов/расходов
					BudgetEditView(
						budget = currentBudget
					)
				}
			}
		}
	}


	@Composable
	fun BudgetEditView(budget: Budget) {
		// Сохранение состояния нужных переменных
		var editedBudget by remember {
			mutableStateOf(budget)
		}
		val isEditMode by remember {
			mutableStateOf(budget.id != 0L)
		}

		// Budget details
		BudgetDetailsInput(
			budget = editedBudget,
			isEditMode = isEditMode
		) { updatedBudget ->
			editedBudget = updatedBudget
			currentBudget = updatedBudget
		}
	}

	@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
	@Composable
	fun BudgetDetailsInput(budget: Budget, isEditMode: Boolean, onBudgetChange: (Budget) -> Unit) {
		// Состояние выпадающего списка категории
		var expandedCategory by remember {
			mutableStateOf(false)
		}
		var expandedDate by remember {
			mutableStateOf(false)
		}
		var expandedTime by remember {
			mutableStateOf(false)
		}
		// Параметры даты/времени
		val datePickerState = rememberDatePickerState(
			initialSelectedDateMillis = budget.dateTime.atZone(
				ZoneId.systemDefault()
			).toInstant().toEpochMilli()
		)

		// Управление клавиатурой
		val keyboardController = LocalSoftwareKeyboardController.current

		// Отображение данных
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp)
		) {
			// Сумма
			OutlinedTextField(
				value = budget.amount.toString(),
				onValueChange = { text ->
					val newBudget = budget.copy(
						amount = text.toLong()
					)
					onBudgetChange(newBudget)
				},
				label = {
					Text(
						text = "Amount"
					)
				},
				keyboardOptions = KeyboardOptions.Default.copy(
					keyboardType = KeyboardType.Number,
					imeAction = ImeAction.Done
				),
				keyboardActions = KeyboardActions(
					onDone = {
						if (!isEditMode) {
							onBudgetChange(budget)
						}
						keyboardController?.hide()
					}
				),
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 16.dp)
			)

			// Дата
			OutlinedTextField(
				value = budget.dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
				onValueChange = {},
				label = { Text("Date") },
				enabled = false,
				modifier = Modifier
					.fillMaxWidth()
					.clickable {
						expandedDate = true
					}
			)
			if (expandedDate) {
				// Диалог ввода даты
				DatePickerDialog(
					onDismissRequest = {
						expandedDate = false
					},
					confirmButton = {
						TextButton(onClick = {
							val newBudget = budget.copy(
								dateTime = LocalDateTime.ofInstant(
									datePickerState.selectedDateMillis?.let { millis ->
										Instant.ofEpochMilli(
											millis
										)
									},
									TimeZone.getDefault().toZoneId()
								)
							)
							onBudgetChange(newBudget)
							expandedDate = false
						}) {
							Text(text = "Confirm")
						}
					},
					dismissButton = {
						TextButton(onClick = {
							expandedDate = false
						}) {
							Text(text = "Cancel")
						}
					}
				) {
					DatePicker(
						state = datePickerState,
						modifier = Modifier.fillMaxWidth()
					)
				}
			}

			// Время
			OutlinedTextField(
				value = budget.dateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
				onValueChange = {},
				label = { Text("Time") },
				enabled = false,
				modifier = Modifier
					.fillMaxWidth()
					.clickable {
						expandedTime = true
					}
			)
			if (expandedTime) {
				// Диалог ввода времени
				TimePickerDialog(
					LocalContext.current,
					{ _: Any, hour: Int, minute: Int ->
						val newBudget = budget.copy(
							dateTime = LocalDateTime.ofInstant(
								datePickerState.selectedDateMillis?.let { millis ->
									Instant.ofEpochMilli(
										millis
									)
								},
								TimeZone.getDefault().toZoneId()
							).with(LocalTime.of(hour, minute))
						)
						onBudgetChange(newBudget)
						expandedTime = false
					},
					budget.dateTime.atZone(
						ZoneId.systemDefault()
					).hour,
					budget.dateTime.atZone(
						ZoneId.systemDefault()
					).minute,
					true
				).show()
			}

			// Категория в виде выпадающего списка
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 16.dp)
			) {
				OutlinedTextField(
					value = budget.category.name,
					onValueChange = {},
					label = { Text("Category") },
					enabled = false,
					modifier = Modifier
						.fillMaxWidth()
						.clickable {
							expandedCategory = true
						}
				)

				DropdownMenu(
					expanded = expandedCategory,
					onDismissRequest = {
						expandedCategory = false
					},
					modifier = Modifier.background(MaterialTheme.colorScheme.background)
				) {
					for (category in Category.entries) {
						DropdownMenuItem(
							text = {
								Text(
									text = category.name
								)
							},
							onClick = {
								val newBudget = budget.copy(
									category = category
								)
								onBudgetChange(newBudget)
								expandedCategory = false
							}
						)
					}
				}
			}

			// Комментарий
			OutlinedTextField(
				value = budget.comment,
				onValueChange = { text ->
					val newBudget = budget.copy(
						comment = text
					)
					onBudgetChange(newBudget)
				},
				label = { Text("Comment") },
				keyboardOptions = KeyboardOptions.Default.copy(
					imeAction = ImeAction.Done
				),
				keyboardActions = KeyboardActions(
					onDone = {
						if (!isEditMode) {
							onBudgetChange(budget)
						}
						keyboardController?.hide()
					}
				),
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 16.dp)
			)
		}
	}


	@Composable
	@Preview(
		name = "BudgetCardPreview (Dark)",
		showBackground = true,
		uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
	)
	@Preview(
		name = "BudgetCardPreview",
		showBackground = true,
		uiMode = Configuration.UI_MODE_NIGHT_NO
	)
	fun BudgetActivityPreview() {
		// Данные для примера
		val sampleBudget = Budget(
			amount = 500,
			category = Category.OTHER,
			comment = "Sample Comment"
		)

		// Превью тестового редактора данных
		BudgetEditView(
			budget = sampleBudget,
		)
	}


	@Composable
	@Preview(
		name = "Expense Manager Edit Activity (Dark)",
		showBackground = true,
		uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
	)
	@Preview(
		name = "Expense Manager Edit Activity",
		showBackground = true,
		uiMode = Configuration.UI_MODE_NIGHT_NO
	)
	fun ExpenseManagerAppPreview() {

		// Превью окна
		ExpenseManagerNewBudget()

	}


}
