package bashlykov.ivan.expense.manager.ui.settings


import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bashlykov.ivan.expense.manager.settings.SettingsApplication
import bashlykov.ivan.expense.manager.settings.SettingsLanguage
import bashlykov.ivan.expense.manager.settings.SettingsPalette
import bashlykov.ivan.expense.manager.settings.SettingsTheme
import bashlykov.ivan.expense.manager.ui.theme.ExpenseManagerTheme


class ActivitySettings : ComponentActivity() {

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
				val editedSettings by if (LocalInspectionMode.current) {
					remember {
						mutableStateOf(
							SettingsApplication(
								colorPalette = SettingsPalette.DEFAULT,
								colorTheme = SettingsTheme.DEVICE,
								language = SettingsLanguage.ENGLISH
							)
						)
					}
				} else {
					remember {
						mutableStateOf(
							SettingsApplication(
								colorPalette = SettingsPalette.entries[
									getPreferences(Context.MODE_PRIVATE).getInt(
										SettingsPalette::class.simpleName,
										SettingsPalette.DEFAULT.ordinal
									)
								],
								colorTheme = SettingsTheme.entries[
									getPreferences(Context.MODE_PRIVATE).getInt(
										SettingsTheme::class.simpleName,
										SettingsTheme.DEVICE.ordinal
									)
								],
								language = SettingsLanguage.entries[
									getPreferences(Context.MODE_PRIVATE).getInt(
										SettingsLanguage::class.simpleName,
										SettingsLanguage.ENGLISH.ordinal
									)
								]
							)
						)
					}
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
							Text(
								text = "Settings"
							)
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
									with (getPreferences(Context.MODE_PRIVATE)?.edit()) {
										this?.putInt(
											SettingsLanguage::class.simpleName,
											editedSettings.language.ordinal
										)
										this?.putInt(
											SettingsPalette::class.simpleName,
											editedSettings.colorPalette.ordinal
										)
										this?.putInt(
											SettingsTheme::class.simpleName,
											editedSettings.colorTheme.ordinal
										)
										this?.apply()
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
							// Запуск активности настроек языка
							startActivity(
								Intent(
									applicationContext,
									ActivityLanguage::class.java
								)
							)
						}
					)

					// Тема
					SettingItem(
						icon = Icons.Filled.Edit,
						title = "Theme",
						value = editedSettings.colorTheme.name,
						onClick = {
							// Запуск активности настроек темы
							startActivity(
								Intent(
									applicationContext,
									ActivityTheme::class.java
								)
							)
						}
					)

					// Цветовая палитра
					SettingItem(
						icon = Icons.Filled.List,
						title = "Color palette",
						value = editedSettings.colorPalette.name,
						onClick = {
							// Запуск активности настроек палитры
							startActivity(
								Intent(
									applicationContext,
									ActivityPalette::class.java
								)
							)
						}
					)
				}
			}
		}
	}


	@OptIn(ExperimentalMaterial3Api::class)
	@Composable
	fun SettingItem(icon: ImageVector, title: String, value: String, onClick: () -> Unit) {
		Card(
			modifier = Modifier
				.fillMaxWidth()
				.padding(4.dp),
			onClick = onClick
		) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Icon(
					imageVector = icon,
					tint = MaterialTheme.colorScheme.primary,
					contentDescription = title,
					modifier = Modifier.size(24.dp)
				)
				Spacer(
					modifier = Modifier.width(16.dp)
				)
				Column {
					Text(
						text = title,
						color = MaterialTheme.colorScheme.primary,
						style = MaterialTheme.typography.titleLarge
					)
					Text(
						text = value,
						style = MaterialTheme.typography.bodyLarge,
						color = Color.Gray
					)
				}
				Spacer(
					modifier = Modifier.weight(1f)
				)
				Icon(
					imageVector = Icons.Filled.ArrowForward,
					tint = MaterialTheme.colorScheme.primary,
					contentDescription = "Navigate"
				)
			}
		}
	}

	@Composable
	@Preview(
		name = "Settings Activity (Dark)",
		showBackground = true,
		uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
	)
	@Preview(
		name = "Settings Activity",
		showBackground = true,
		uiMode = Configuration.UI_MODE_NIGHT_NO
	)
	fun SettingsActivityPreview() {

		ExpenseManagerSettings()

	}

}
