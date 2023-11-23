package bashlykov.ivan.expense.manager.database.tables

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

import bashlykov.ivan.expense.manager.database.Category

@Entity(tableName = "Budget")
data class Budget(
	@PrimaryKey(autoGenerate = true)
	val id: Long = 0,
	val amount: Long = 0,
	@ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
	val date: LocalDateTime = LocalDateTime.now(),
	val category: Category = Category.OTHER,
	@ColumnInfo(defaultValue = "")
	val comment: String = ""
)
