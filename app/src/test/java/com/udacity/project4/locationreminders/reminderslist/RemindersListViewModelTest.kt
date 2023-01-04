package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //provide testing to the RemindersListViewModel and its live data objects

    // Subject under test
    private lateinit var remindersListViewModel: RemindersListViewModel

    // Use a fake repository to be injected into the viewmodel
    private lateinit var dataSource: FakeDataSource
    private lateinit var appContext: Application

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel() = runBlocking {
        // We initialise the reminders to 3
        dataSource = FakeDataSource()
        appContext = getApplicationContext()
        val reminder1 = ReminderDTO("Title1", "Description1", "location1", 12.232, 15.548)
        val reminder2 = ReminderDTO("Title2", "Description2", "location2", 19.88, 14.235)
        val reminder3 = ReminderDTO("Title3", "Description3", "location3", 21.365, 32.45687)
        dataSource.saveReminder(reminder1)
        dataSource.saveReminder(reminder2)
        dataSource.saveReminder(reminder3)

        remindersListViewModel = RemindersListViewModel(appContext, dataSource)
    }

    @After
    fun stop_koin() {
        stopKoin()
    }

    @Test
    fun check_loading() {

        remindersListViewModel.loadReminders()
        val value = remindersListViewModel.showLoading.getOrAwaitValue()
        assertThat(value, `is` (false))

    }

    @Test
    fun shouldReturnError() = runBlocking{

        dataSource.deleteAllReminders()
        remindersListViewModel = RemindersListViewModel(appContext, dataSource)
        remindersListViewModel.loadReminders()
        val value = remindersListViewModel.showNoData.getOrAwaitValue()

        assertThat(value, `is`(true))

    }
}