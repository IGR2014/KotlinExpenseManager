package bashlykov.ivan.expense.manager.database.tables

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import bashlykov.ivan.expense.manager.database.Category
import java.time.LocalDateTime
import java.time.Instant
import java.time.ZoneId
import java.util.TimeZone

@Entity(tableName = "Budget")
data class Budget(
	@PrimaryKey(autoGenerate = true)
	val id: Long = 0L,
	@ColumnInfo(defaultValue = "0")
	val amount: Long = 0L,
	@ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
	val dateTime: LocalDateTime = LocalDateTime.now(),
	@ColumnInfo(defaultValue = "0")
	val category: Category = Category.OTHER,
	@ColumnInfo(defaultValue = "")
	val comment: String = ""
) : Parcelable {

	constructor(parcel: Parcel) : this(
		id = parcel.readLong(),
		amount = parcel.readLong(),
		dateTime = LocalDateTime.ofInstant(
			Instant.ofEpochMilli(parcel.readLong()),
			TimeZone.getDefault().toZoneId()
		),
		category = parcel.readString()?.let {
			name -> Category.valueOf(name)
		}?: Category.OTHER,
		comment = parcel.readString()?: ""
	)

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeLong(id)
		parcel.writeLong(amount)
		parcel.writeLong(
			dateTime.atZone(
				ZoneId.systemDefault()
			).toInstant().toEpochMilli()
		)
		parcel.writeString(category.name)
		parcel.writeString(comment)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<Budget> {
		override fun createFromParcel(parcel: Parcel): Budget {
			return Budget(parcel)
		}

		override fun newArray(size: Int): Array<Budget?> {
			return arrayOfNulls(size)
		}
	}

}
