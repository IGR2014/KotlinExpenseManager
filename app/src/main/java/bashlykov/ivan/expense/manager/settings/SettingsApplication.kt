package bashlykov.ivan.expense.manager.settings

data class SettingsApplication(
	val language: SettingsLanguage = SettingsLanguage.ENGLISH,
	val colorTheme: SettingsTheme = SettingsTheme.DEVICE,
	val colorPalette: SettingsPalette = SettingsPalette.DEFAULT
)
