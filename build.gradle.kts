// Плагины сборки
plugins {
	id("com.android.application") version "8.2.0-rc03" apply false
	id("com.google.devtools.ksp") version "1.9.20-1.0.14" apply false
	kotlin("android") version "1.9.20" apply false
	kotlin("jvm") version "1.9.20" apply false
}

// Дополнения к скрипту сборки
buildscript {
	dependencies {
		classpath(kotlin("gradle-plugin", version = "1.9.20"))
	}
}
