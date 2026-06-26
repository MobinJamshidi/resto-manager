package com.example.resturant.feature.attendance

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.resturant.feature.attendance.data.AttendanceDatabase
import com.example.resturant.feature.attendance.data.AttendanceRecord
import com.example.resturant.feature.employee.data.Employee
import com.example.resturant.feature.employee.data.EmployeeDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AttendanceViewModel(application: Application) : AndroidViewModel(application) {

    private val attendanceDao = AttendanceDatabase.getInstance(application).attendanceDao()
    private val employeeDao = EmployeeDatabase.getInstance(application).employeeDao()

    val employees: Flow<List<Employee>> = employeeDao.getAll()

    val todayKey: String = currentDayKey()

    val todayRecords: Flow<List<AttendanceRecord>> = attendanceDao.getForDay(todayKey)

    fun recordsForEmployee(employeeId: Long): Flow<List<AttendanceRecord>> =
        attendanceDao.getForEmployee(employeeId)

    fun setAttendance(
        employeeId: Long,
        dayKey: String,
        isPresent: Boolean,
        entryTime: String,
        exitTime: String
    ) = viewModelScope.launch {
        attendanceDao.upsert(
            AttendanceRecord(
                employeeId = employeeId,
                dayKey = dayKey,
                isPresent = isPresent,
                entryTime = entryTime,
                exitTime = exitTime
            )
        )
    }

    fun clearForEmployee(employeeId: Long) = viewModelScope.launch {
        attendanceDao.clearForEmployee(employeeId)
    }
}

fun currentDayKey(): String {
    val cal = Calendar.getInstance()
    if (cal.get(Calendar.HOUR_OF_DAY) < 2) {
        cal.add(Calendar.DAY_OF_YEAR, -1)
    }
    return SimpleDateFormat("yyyy/MM/dd", Locale.US).format(cal.time)
}