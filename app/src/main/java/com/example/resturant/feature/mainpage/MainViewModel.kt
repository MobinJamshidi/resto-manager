package com.example.resturant.feature.mainpage

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import com.example.resturant.feature.finance.data.FinanceDatabase
import com.example.resturant.feature.finance.data.FinanceRecord
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

private const val RESTAURANT_SECTION = 3

data class Task(
    val id: Long,
    val text: String,
    val isDone: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val financeDao = FinanceDatabase.getInstance(application).financeDao()

    val restaurantDebtTotal: Flow<Double> =
        financeDao.debtTotalForSection(RESTAURANT_SECTION)

    val upcomingDebts: Flow<List<FinanceRecord>>

    private val _tasks = mutableStateListOf<Task>()
    val tasks: List<Task> get() = _tasks
    private var nextId = 1L

    init {
        val (start, end) = tomorrowRange()
        upcomingDebts = financeDao.debtsBetween(RESTAURANT_SECTION, start, end)
    }

    fun addTask(text: String) {
        val trimmed = text.trim()
        if (trimmed.isNotEmpty()) {
            _tasks.add(0, Task(id = nextId++, text = trimmed))
        }
    }

    fun toggleTask(id: Long) {
        val index = _tasks.indexOfFirst { it.id == id }
        if (index != -1) {
            _tasks[index] = _tasks[index].copy(isDone = !_tasks[index].isDone)
        }
    }

    fun deleteTask(id: Long) {
        _tasks.removeAll { it.id == id }
    }

    private fun tomorrowRange(): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        cal.add(Calendar.DAY_OF_YEAR, 1)
        val end = cal.timeInMillis
        return start to end
    }
}