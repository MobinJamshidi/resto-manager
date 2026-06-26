package com.example.resturant.feature.attendance

import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar as PersianCalendar
import android.icu.util.ULocale
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.resturant.feature.attendance.data.AttendanceRecord
import com.example.resturant.feature.employee.data.Employee
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val Bg = Color(0xFF000000)
private val CardBg = Color(0xFF1C1C1E)
private val ChipBg = Color(0xFF2C2C2E)
private val PresentColor = Color(0xFF2E9E4F)
private val AbsentColor = Color(0xFFE53935)
private val TextPrimary = Color.White
private val TextSecondary = Color(0xFF9A9A9E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    onBack: () -> Unit = {},
    viewModel: AttendanceViewModel = viewModel()
) {
    val employees by viewModel.employees.collectAsState(initial = emptyList())
    val todayRecords by viewModel.todayRecords.collectAsState(initial = emptyList())
    var monthlyEmployee by remember { mutableStateOf<Employee?>(null) }

    Scaffold(
        containerColor = Bg,
        topBar = {
            TopAppBar(
                title = { Text("Attendance") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Bg,
                    titleContentColor = TextPrimary,
                    navigationIconContentColor = TextPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            if (employees.isEmpty()) {
                Spacer(Modifier.height(40.dp))
                Text(
                    "هنوز کارمندی ثبت نشده. اول از بخش Employee Management کارمند اضافه کن.",
                    color = TextSecondary
                )
            } else {
                Spacer(Modifier.height(8.dp))
                SectionLabel("EMPLOYEES")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    employees.forEach { employee ->
                        EmployeeNameRow(employee.fullName) { monthlyEmployee = employee }
                    }
                }

                Spacer(Modifier.height(28.dp))
                SectionLabel("TODAY'S ATTENDANCE  •  ${gregorianKeyToPersian(viewModel.todayKey)}")
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    employees.forEach { employee ->
                        val record = todayRecords.find { it.employeeId == employee.id }
                        AttendanceRow(
                            label = employee.fullName,
                            record = record
                        ) { present, entry, exit ->
                            viewModel.setAttendance(employee.id, viewModel.todayKey, present, entry, exit)
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    monthlyEmployee?.let { employee ->
        MonthlyDialog(
            employee = employee,
            viewModel = viewModel,
            onDismiss = { monthlyEmployee = null }
        )
    }
}

@Composable
private fun MonthlyDialog(
    employee: Employee,
    viewModel: AttendanceViewModel,
    onDismiss: () -> Unit
) {
    val recordsFlow = remember(employee.id) { viewModel.recordsForEmployee(employee.id) }
    val records by recordsFlow.collectAsState(initial = emptyList())
    val recordsByDay = records.associateBy { it.dayKey }

    val start = remember { currentPersianYearMonth() }
    var displayYear by remember { mutableIntStateOf(start.first) }
    var displayMonth by remember { mutableIntStateOf(start.second) }
    val days = remember(displayYear, displayMonth) { persianMonthDays(displayYear, displayMonth) }

    var confirmClear by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Bg)
                .safeDrawingPadding()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Text(
                    employee.fullName,
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                )
                TextButton(onClick = { confirmClear = true }) {
                    Text("Clear", color = AbsentColor, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(CardBg)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (displayMonth == 0) {
                        displayMonth = 11
                        displayYear -= 1
                    } else {
                        displayMonth -= 1
                    }
                }) {
                    Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous month", tint = TextPrimary)
                }

                Text(
                    text = "${persianMonthNames[displayMonth]} $displayYear",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                IconButton(onClick = {
                    if (displayMonth == 11) {
                        displayMonth = 0
                        displayYear += 1
                    } else {
                        displayMonth += 1
                    }
                }) {
                    Icon(Icons.Filled.ChevronRight, contentDescription = "Next month", tint = TextPrimary)
                }
            }

            Spacer(Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                days.forEach { day ->
                    AttendanceRow(
                        label = day.persianLabel,
                        record = recordsByDay[day.gregorianKey]
                    ) { present, entry, exit ->
                        viewModel.setAttendance(employee.id, day.gregorianKey, present, entry, exit)
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            title = { Text("Clear all records?") },
            text = { Text("تمام رکوردهای حضور و غیابِ ${employee.fullName} پاک می‌شوند.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearForEmployee(employee.id)
                    confirmClear = false
                }) { Text("Clear", color = AbsentColor) }
            },
            dismissButton = {
                TextButton(onClick = { confirmClear = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun EmployeeNameRow(name: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardBg)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = TextSecondary)
    }
}

@Composable
private fun AttendanceRow(
    label: String,
    record: AttendanceRecord?,
    onSet: (isPresent: Boolean, entry: String, exit: String) -> Unit
) {
    val context = LocalContext.current
    val isSet = record != null
    val present = record?.isPresent == true
    val entry = record?.entryTime ?: ""
    val exit = record?.exitTime ?: ""

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardBg)
            .padding(14.dp)
    ) {
        Text(label, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusChip("Present", selected = isSet && present, accent = PresentColor) {
                onSet(true, entry, exit)
            }
            StatusChip("Absent", selected = isSet && !present, accent = AbsentColor) {
                onSet(false, "", "")
            }
        }

        if (isSet && present) {
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TimeChip("In: ${entry.ifEmpty { "--:--" }}") {
                    pickTime(context, entry) { onSet(true, it, exit) }
                }
                TimeChip("Out: ${exit.ifEmpty { "23:30" }}") {
                    pickTime(context, exit) { onSet(true, entry, it) }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 50))
                        .background(ChipBg)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text("Total: ${workHours(entry, exit)}", color = PresentColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun StatusChip(text: String, selected: Boolean, accent: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(if (selected) accent else ChipBg)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text,
            color = if (selected) Color.White else TextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun TimeChip(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(ChipBg)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(text, color = TextPrimary, fontSize = 13.sp)
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(10.dp))
}

private fun pickTime(context: Context, current: String, onPicked: (String) -> Unit) {
    val cal = Calendar.getInstance()
    if (current.contains(":")) {
        val parts = current.split(":")
        cal.set(Calendar.HOUR_OF_DAY, parts.getOrNull(0)?.toIntOrNull() ?: 10)
        cal.set(Calendar.MINUTE, parts.getOrNull(1)?.toIntOrNull() ?: 0)
    }
    TimePickerDialog(
        context,
        { _, hour, minute -> onPicked("%02d:%02d".format(hour, minute)) },
        cal.get(Calendar.HOUR_OF_DAY),
        cal.get(Calendar.MINUTE),
        true
    ).show()
}

private fun workHours(entry: String, exit: String): String {
    val start = parseMinutes(entry) ?: return "—"
    var end = parseMinutes(exit.ifBlank { "23:30" }) ?: return "—"
    if (end < start) end += 24 * 60
    val total = end - start
    return "${total / 60}h ${total % 60}m"
}

private fun parseMinutes(time: String): Int? {
    if (!time.contains(":")) return null
    val parts = time.split(":")
    val h = parts.getOrNull(0)?.toIntOrNull() ?: return null
    val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
    return h * 60 + m
}


private val persianMonthNames = listOf(
    "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
    "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
)

private data class PersianDay(val gregorianKey: String, val persianLabel: String)

private fun persianLocale() = ULocale("fa_IR@calendar=persian")

private fun currentPersianYearMonth(): Pair<Int, Int> {
    val c = PersianCalendar.getInstance(persianLocale())
    return c.get(PersianCalendar.YEAR) to c.get(PersianCalendar.MONTH)
}

private fun persianMonthDays(year: Int, month: Int): List<PersianDay> {
    val gregFmt = SimpleDateFormat("yyyy/MM/dd", Locale.US)
    val probe = PersianCalendar.getInstance(persianLocale())
    probe.clear()
    probe.set(year, month, 1)
    val maxDay = probe.getActualMaximum(PersianCalendar.DAY_OF_MONTH)

    val result = mutableListOf<PersianDay>()
    for (d in 1..maxDay) {
        val c = PersianCalendar.getInstance(persianLocale())
        c.clear()
        c.set(year, month, d)
        val key = gregFmt.format(Date(c.timeInMillis))
        val label = "$d ${persianMonthNames[month]} $year"
        result.add(PersianDay(key, label))
    }
    return result
}

private fun gregorianKeyToPersian(key: String): String {
    val parts = key.split("/")
    if (parts.size != 3) return key
    val gc = Calendar.getInstance()
    gc.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt(), 12, 0, 0)
    val pc = PersianCalendar.getInstance(persianLocale())
    pc.timeInMillis = gc.timeInMillis
    val d = pc.get(PersianCalendar.DAY_OF_MONTH)
    val m = pc.get(PersianCalendar.MONTH)
    val y = pc.get(PersianCalendar.YEAR)
    return "$d ${persianMonthNames[m]} $y"
}