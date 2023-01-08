package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

//    Add testing implementation to the RemindersLocalRepository.kt

    private lateinit var localReminder: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        // using an in-memory database for testing, since it doesn't survive killing the process
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        localReminder =
            RemindersLocalRepository(
                database.reminderDao(),
                Dispatchers.Main
            )
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun saveReminder_retrievesReminder() = runBlocking {
        // GIVEN - a new reminder saved in the database
        val newReminderDTO = ReminderDTO("title", "description", "location", 11.2, 12.65)
        localReminder.saveReminder(newReminderDTO)

        // WHEN  - Reminder retrieved by ID
        val result = localReminder.getReminder(newReminderDTO.id)

        // THEN - Same reminder is returned
        Assert.assertThat(result is Result.Success, `is`(true))
        result as Result.Success
        Assert.assertThat(result.data.title, `is`("title"))
        Assert.assertThat(result.data.description, `is`("description"))
        Assert.assertThat(result.data.location, `is`("location"))
        Assert.assertThat(result.data.latitude, `is`(11.2))
        Assert.assertThat(result.data.longitude, `is`(12.65))
    }

    @Test
    fun deleteAllReminders() = runBlocking {
        // Given a new reminder in the persistent repository
        val newReminderDTO = ReminderDTO("title", "description", "location", 11.2, 12.65)
        localReminder.saveReminder(newReminderDTO)

        // When completed in the persistent repository
        localReminder.deleteAllReminders()
        val result = localReminder.getReminders()

        // Then the task can be retrieved from the persistent repository and is complete
        Assert.assertThat(result is Result.Success, `is`(true))
        result as Result.Success
        Assert.assertThat(result.data.isEmpty(), `is`(true))
    }

    @Test
    fun returnError() = runBlocking {
        val newReminderDTO = ReminderDTO("title", "description", "location", 11.2, 12.65)
        localReminder.deleteAllReminders()

        val result = localReminder.getReminder(newReminderDTO.id)

        assertThat(result is Result.Error, `is` (true))
        result as Result.Error
        assertThat(result.message, `is` ("Reminder not found!"))
    }

}