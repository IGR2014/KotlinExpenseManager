package bashlykov.ivan.expense.manager


import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import bashlykov.ivan.expense.manager.settings.SettingsApplication
import bashlykov.ivan.expense.manager.settings.SettingsLanguage
import bashlykov.ivan.expense.manager.settings.SettingsPalette
import bashlykov.ivan.expense.manager.settings.SettingsTheme
import bashlykov.ivan.expense.manager.ui.theme.ExpenseManagerTheme


class ActivitySettings : ComponentActivity() {

	// Чтение настроек
	private val prefs by lazy {
		getPreferences(Context.MODE_PRIVATE)
	}

	// Данные настроек
	private val settings by lazy {
		SettingsApplication(
			colorPalette = SettingsPalette.entries[prefs.getInt(SettingsPalette::class.simpleName, SettingsPalette.DEFAULT.ordinal)],
			colorTheme = SettingsTheme.entries[prefs.getInt(SettingsTheme::class.simpleName, SettingsTheme.DEVICE.ordinal)],
			language = SettingsLanguage.entries[prefs.getInt(SettingsLanguage::class.simpleName, SettingsLanguage.ENGLISH.ordinal)]
		)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		// Задаём GUI
		setContent {
			ExpenseManagerSettings()
		}
	}

	@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
	@Composable
	fun ExpenseManagerSettings() {
		ExpenseManagerTheme {
			Surface(
				modifier = Modifier.fillMaxSize(),
				color = MaterialTheme.colorScheme.background
			) {
				var editedSettings by remember {
					mutableStateOf(settings)
				}

				// Keyboard controller for handling the Done button
				val keyboardController = LocalSoftwareKeyboardController.current

				Column(
					modifier = Modifier
						.fillMaxSize()
						.padding(16.dp)
				) {
					TopAppBar(
						title = {
							Text("Settings")
						},
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
							// Save button
							IconButton(
								onClick = {
									// Сохранение настроек
									with (prefs.edit()) {
										putInt(SettingsLanguage::class.simpleName, editedSettings.language.ordinal)
										putInt(SettingsPalette::class.simpleName, editedSettings.colorPalette.ordinal)
										putInt(SettingsTheme::class.simpleName, editedSettings.colorTheme.ordinal)
										apply()
									}
									// Установка результата
									setResult(RESULT_OK)
									// Закрытие активности
									finish()
									// Скрыть контроллер клавиатуры
									keyboardController?.hide()
								}
							) {
								Icon(
									imageVector = Icons.Filled.Check,
									contentDescription = "Save"
								)
							}
						}
					)

					// Язык
					SettingItem(
						icon = Icons.Filled.Person,
						title = "Language",
						value = editedSettings.language.name,
						onClick = {
							// TODO
						}
					)

					// Тема
					SettingItem(
						icon = Icons.Filled.Edit,
						title = "Theme",
						value = editedSettings.colorTheme.name,
						onClick = {
							// TODO
						}
					)

					// Цветовая палитра
					SettingItem(
						icon = Icons.Filled.List,
						title = "Color Palette",
						value = editedSettings.colorPalette.name,
						onClick = {
							// TODO
						}
					)
				}
			}
		}
	}


	@Composable
	fun SettingItem(icon: ImageVector, title: String, value: String, onClick: () -> Unit) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.height(56.dp)
				.padding(16.dp)
				.clickable(onClick = onClick),
			verticalAlignment = Alignment.CenterVertically
		) {
			Icon(
				imageVector = icon,
				contentDescription = title,
				modifier = Modifier.size(24.dp)
			)
			Spacer(
				modifier = Modifier.width(16.dp)
			)
			Column {
				Text(
					text = title,
					style = MaterialTheme.typography.bodyLarge)
				Text(
					text = value,
					style = MaterialTheme.typography.titleLarge,
					color = Color.Gray
				)
			}
			Spacer(
				modifier = Modifier.weight(1f)
			)
			Icon(
				imageVector = Icons.Filled.ArrowForward,
				contentDescription = "Navigate"
			)
		}
	}

}
