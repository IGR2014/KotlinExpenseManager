package bashlykov.ivan.expense.manager


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bashlykov.ivan.expense.manager.database.Category
import bashlykov.ivan.expense.manager.database.DatabaseBudget
import bashlykov.ivan.expense.manager.database.tables.Budget
import bashlykov.ivan.expense.manager.ui.theme.ExpenseManagerTheme


class ActivityStatistics : ComponentActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Задаём GUI
        setContent {
            ExpenseManagerStatistics()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ExpenseManagerStatistics() {
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
                    TopAppBar(
                        title = {
                            Text("Statistics")
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {}
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    )

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
                    // Для каждой категории
                    Category.entries.forEach { category ->
                        // Общий доход/расход
                        val totalBudget = budgetItems
                            .filter { it.category == category }
                            .sumOf { it.amount }
                        StatisticsCard(
                            title = category.name,
                            value = totalBudget.toString(),
                            icon = Icons.Filled.Warning,
                            iconColor = if (totalBudget >= 0) {
                                Color.Green
                            } else {
                                Color.Red
                            }
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun StatisticsCard(title: String, value: String, icon: ImageVector, iconColor: Color) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onClick = {}
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconColor
                    )
                }
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }

    @Composable
    @Preview(showBackground = true)
    fun StatisticsActivityPreview() {

        ExpenseManagerStatistics()

    }

}
