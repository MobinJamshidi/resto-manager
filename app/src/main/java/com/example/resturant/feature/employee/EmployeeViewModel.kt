package com.example.resturant.feature.employee

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.resturant.feature.employee.data.Employee
import com.example.resturant.feature.employee.data.EmployeeDatabase
import com.example.resturant.feature.employee.data.Position
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class EmployeeAnalytics(
    val total: Int = 0,
    val byPosition: Map<Position, Int> = emptyMap(),
    val totalSalary: Double = 0.0
)

class EmployeeViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = EmployeeDatabase.getInstance(application).employeeDao()

    val employees: Flow<List<Employee>> = dao.getAll()

    val analytics: Flow<EmployeeAnalytics> = dao.getAll().map { list ->
        EmployeeAnalytics(
            total = list.size,
            byPosition = list.groupingBy { it.position }.eachCount(),
            totalSalary = list.sumOf { it.salary }
        )
    }

    fun addEmployee(employee: Employee) = viewModelScope.launch {
        dao.insert(employee)
    }

    fun updateEmployee(employee: Employee) = viewModelScope.launch {
        dao.update(employee)
    }

    fun deleteEmployee(employee: Employee) = viewModelScope.launch {
        dao.delete(employee)
    }
}