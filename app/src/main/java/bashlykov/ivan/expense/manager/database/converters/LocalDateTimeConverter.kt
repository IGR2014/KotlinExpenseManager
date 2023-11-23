package bashlykov.ivan.expense.manager.database.converters

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.TimeZone

// Преобразователь локальной даты/времени в таймстамп для БД
class LocalDateTimeConverter {

	@TypeConverter
	fun toDate(timestamp: Long?): LocalDateTime? {
		return if (timestamp == null) null else LocalDateTime.ofInstant(
			Instant.ofEpochMilli(timestamp),
			TimeZone.getDefault().toZoneId()
		);
	}

	@TypeConverter
	fun toTimestamp(date: LocalDateTime?): Long? {
		return date?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli();
	}
}
