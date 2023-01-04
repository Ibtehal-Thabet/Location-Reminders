package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.udacity.project4.locationreminders.MainCoroutineRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    // provide testing to the SaveReminderView and its live data objects

    // Subject under test
    private lateinit var saveReminderViewModel: SaveReminderViewModel

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
    fun setupViewModel() {
        // We initialise the repository with no tasks
        dataSource = FakeDataSource()
        appContext = ApplicationProvider.getApplicationContext()

        saveReminderViewModel = SaveReminderViewModel(appContext, dataSource)

    }

    @After
    fun stop_koin() {
        stopKoin()
    }


    @Test
    fun check_loading() {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        val reminderDataItem = ReminderDataItem("title", "description", "location",
            29.969, 31.248)

        // Load the task in the viewmodel
        saveReminderViewModel.saveReminder(reminderDataItem)

        // Then progress indicator is shown
        assertThat(
            saveReminderViewModel.showLoading.getOrAwaitValue(),
            `is`(true))

        // Execute pending coroutines actions
        mainCoroutineRule.resumeDispatcher()

        // Then progress indicator is hidden
        assertThat(
            saveReminderViewModel.showLoading.getOrAwaitValue(),
            `is`(false)
        )
    }

    @Test
    fun shouldReturnError_emptyTitle() {

        val reminderDataItem = ReminderDataItem(null, "description", "location",
            29.969, 31.248)

        assertThat(
            saveReminderViewModel.validateEnteredData(reminderDataItem),
            `is`(false)
        )
        assertThat(
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
            `is`(R.string.err_enter_title)
        )

    }

    @Test
    fun shouldReturnError_emptyLocation() {

        val reminderDataItem = ReminderDataItem("title", "description", null,
            29.969, 31.248)

        assertThat(
            saveReminderViewModel.validateEnteredData(reminderDataItem),
            `is`(false)
        )
        assertThat(
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
            `is`(R.string.err_select_location)
        )

    }

}