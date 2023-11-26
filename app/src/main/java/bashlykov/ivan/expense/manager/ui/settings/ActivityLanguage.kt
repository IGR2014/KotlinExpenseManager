package bashlykov.ivan.expense.manager.ui.settings


import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bashlykov.ivan.expense.manager.ui.theme.ExpenseManagerTheme


class ActivityLanguage : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Задаём GUI
        setContent {
            ExpenseManagerSettingsLanguage()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ExpenseManagerSettingsLanguage() {
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
                            Text(
                                text = "Language"
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    // Закрытие активности
                                    finish()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    )
                    Text(
                        text = "Coming soon...",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentHeight(
                                align = Alignment.CenterVertically
                            )
                    )
                }
            }
        }
    }

    @Composable
    @Preview(
        name = "Language Activity (Dark)",
        showBackground = true,
        uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
    )
    @Preview(
        name = "Language Activity",
        showBackground = true,
        uiMode = Configuration.UI_MODE_NIGHT_NO
    )
    fun SettingsLanguageActivityPreview() {

        ExpenseManagerSettingsLanguage()

    }

}
