import org.jetbrains.kotlin.config.KotlinCompilerVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	id("com.android.application")
	id("org.jetbrains.kotlin.android")
	id("kotlin-kapt")
	id("com.google.devtools.ksp")
}

kotlin {

	jvmToolchain {
		languageVersion = JavaLanguageVersion.of(JavaVersion.VERSION_19.toString())
	}

	compilerOptions {
		apiVersion      = KotlinVersion.fromVersion(
			KotlinCompilerVersion.VERSION
				.split('.')
				.joinToString(
					separator       = ".",
					limit           = 2,
					truncated       = ""
				)
				.dropLast(1)
		)
		languageVersion = apiVersion
		jvmTarget       = JvmTarget.fromTarget(JavaVersion.VERSION_19.toString())
	}

}

android {
	namespace = "bashlykov.ivan.expense.manager"
	compileSdk = 34

	defaultConfig {
		applicationId = "bashlykov.ivan.expense.manager"
		minSdk = 24
		targetSdk = 34
		versionCode = 1
		versionName = "1.0"
		multiDexEnabled = true
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables {
			useSupportLibrary = true
		}
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
	}
	compileOptions {
		isCoreLibraryDesugaringEnabled = true
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}
	kotlinOptions {
		jvmTarget = "1.8"
	}
	buildFeatures {
		compose = true
	}
	composeOptions {
		kotlinCompilerExtensionVersion = "1.5.4"
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
}

dependencies {
	implementation("androidx.core:core-ktx:1.12.0")
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
	implementation("androidx.activity:activity-compose:1.8.1")
	implementation(platform("androidx.compose:compose-bom:2023.10.01"))
	implementation("androidx.compose.ui:ui")
	implementation("androidx.compose.ui:ui-graphics")
	implementation("androidx.compose.ui:ui-tooling-preview")
	implementation("androidx.compose.material3:material3")
	implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
	testImplementation("junit:junit:4.13.2")
	androidTestImplementation("androidx.test.ext:junit:1.1.5")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
	androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
	androidTestImplementation("androidx.compose.ui:ui-test-junit4")
	debugImplementation("androidx.compose.ui:ui-tooling")
	debugImplementation("androidx.compose.ui:ui-test-manifest")
	// LiveData для MVC
	implementation("androidx.compose.runtime:runtime-livedata:1.5.4")
	// Room для работы с SQLite (https://developer.android.com/jetpack/androidx/releases/room)
	implementation("androidx.room:room-runtime:2.6.0")
	annotationProcessor("androidx.room:room-compiler:2.6.0")
	implementation("androidx.room:room-ktx:2.6.0")
	implementation("androidx.paging:paging-runtime-ktx:3.2.1")
	ksp("androidx.room:room-compiler:2.6.0")
	// Возможность использовать новые классы со старой Java
	coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
}