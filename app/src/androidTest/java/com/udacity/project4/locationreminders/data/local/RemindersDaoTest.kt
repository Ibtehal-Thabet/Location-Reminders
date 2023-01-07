package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

//    Add testing implementation to the RemindersDao.kt
    private lateinit var database: RemindersDatabase
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertReminderAndGetById() = runBlockingTest {
        val reminderDTO = ReminderDTO("title", "description", "location",
            29.969, 31.248)
        database.reminderDao().saveReminder(reminderDTO)

        val reminder = database.reminderDao().getReminderById(reminderDTO.id)

        assertThat<ReminderDTO>(reminder as ReminderDTO, notNullValue())
        assertThat(reminder.id, `is`(reminderDTO.id))
        assertThat(reminder.title, `is`(reminderDTO.title))
        assertThat(reminder.description, `is`(reminderDTO.description))
        assertThat(reminder.location, `is`(reminderDTO.location))
        assertThat(reminder.latitude, `is`(reminderDTO.latitude))
        assertThat(reminder.longitude, `is`(reminderDTO.longitude))
    }

    @Test
    fun deleteAllRemindesr() = runBlockingTest {
        val originalReminderDTO = ReminderDTO("title", "description","location" , 29.969, 31.248)
        database.reminderDao().saveReminder(originalReminderDTO)

        database.reminderDao().deleteAllReminders()
    }

}