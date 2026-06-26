package com.example.resturant.feature.payroll

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.resturant.feature.attendance.data.AttendanceDatabase
import com.example.resturant.feature.attendance.data.AttendanceRecord
import com.example.resturant.feature.employee.data.Employee
import com.example.resturant.feature.employee.data.EmployeeDatabase
import com.example.resturant.feature.payroll.data.PayrollAdjustment
import com.example.resturant.feature.payroll.data.PayrollDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PayrollViewModel(application: Application) : AndroidViewModel(application) {

    private val employeeDao = EmployeeDatabase.getInstance(application).employeeDao()
    private val attendanceDao = AttendanceDatabase.getInstance(application).attendanceDao()
    private val payrollDao = PayrollDatabase.getInstance(application).payrollDao()

    val employees: Flow<List<Employee>> = employeeDao.getAll()

    fun attendanceForEmployee(employeeId: Long): Flow<List<AttendanceRecord>> =
        attendanceDao.getForEmployee(employeeId)

    fun adjustmentsForEmployee(employeeId: Long): Flow<List<PayrollAdjustment>> =
        payrollDao.getForEmployee(employeeId)

    fun addAdjustment(employeeId: Long, amount: Double, note: String) = viewModelScope.launch {
        payrollDao.insert(
            PayrollAdjustment(
                employeeId = employeeId,
                amount = amount,
                note = note,
                date = System.currentTimeMillis()
            )
        )
    }

    fun deleteAdjustment(adjustment: PayrollAdjustment) = viewModelScope.launch {
        payrollDao.delete(adjustment)
    }
}