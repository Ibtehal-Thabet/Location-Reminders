package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import java.util.LinkedHashMap


//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {

//   Create a fake data source to act as a double to the real data source

    var remindersDao: LinkedHashMap<String, ReminderDTO> = LinkedHashMap()

    private var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError)
            return Result.Error("Reminder exception")
        else
            return Result.Success(remindersDao.values.toList())

    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        remindersDao[reminder.id] = reminder
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (shouldReturnError) {
            return Result.Error("Reminder exception")
        }

        val reminder = remindersDao[id]

        if(reminder != null)
            return Result.Success(reminder)

        return Result.Error("Reminder not found")
    }

    override suspend fun deleteAllReminders() {
        remindersDao.clear()
    }
}