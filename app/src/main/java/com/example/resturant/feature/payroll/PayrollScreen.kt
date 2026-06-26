package com.example.resturant.feature.payroll

import android.app.DatePickerDialog
import android.content.Context
import android.icu.util.Calendar as PersianCalendar
import android.icu.util.ULocale
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import kotlin.math.abs

private val Bg = Color(0xFF000000)
private val CardBg = Color(0xFF1C1C1E)
private val ChipBg = Color(0xFF2C2C2E)
private val Green = Color(0xFF2E9E4F)
private val Red = Color(0xFFE53935)
private val TextPrimary = Color.White
private val TextSecondary = Color(0xFF9A9A9E)

private val persianMonthNames = listOf(
    "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
    "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
)

data class PayrollSummary(
    val daysWorked: Int,
    val totalMinutes: Int,
    val overtimeMinutes: Int,
    val undertimeMinutes: Int,
    val baseSalary: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayrollScreen(
    onBack: () -> Unit = {},
    viewModel: PayrollViewModel = viewModel()
) {
    val employees by viewModel.employees.collectAsState(initial = emptyList())
    var selected by remember { mutableStateOf<Employee?>(null) }

    Scaffold(
        containerColor = Bg,
        topBar = {
            TopAppBar(
                title = { Text("Payroll") },
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
        if (employees.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("هنوز کارمندی ثبت نشده.", color = TextSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(employees, key = { it.id }) { employee ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(CardBg)
                            .clickable { selected = employee }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            employee.fullName,
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = TextSecondary)
                    }
                }
            }
        }
    }

    selected?.let { employee ->
        PayrollDialog(employee = employee, viewModel = viewModel, onDismiss = { selected = null })
    }
}

@Composable
private fun PayrollDialog(
    employee: Employee,
    viewModel: PayrollViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val attendance by remember(employee.id) { viewModel.attendanceForEmployee(employee.id) }
        .collectAsState(initial = emptyList())
    val adjustments by remember(employee.id) { viewModel.adjustmentsForEmployee(employee.id) }
        .collectAsState(initial = emptyList())

    var fromMillis by remember { mutableLongStateOf(firstOfCurrentPersianMonthMillis()) }
    var toMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    val fromKey = millisToDayKey(fromMillis)
    val toKey = millisToDayKey(toMillis)

    val summary = computeSummary(attendance, employee, fromKey, toKey)
    val netAdjust = adjustments.sumOf { it.amount }
    val finalTotal = summary.baseSalary + netAdjust

    var adjPlus by remember { mutableStateOf(true) }
    var adjAmount by remember { mutableStateOf("") }
    var adjNote by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Bg)
                .safeDrawingPadding()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Text(
                    employee.fullName,
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DateButton("From: ${gregorianMillisToPersian(fromMillis)}", Modifier.weight(1f)) {
                    pickDate(context, fromMillis) { fromMillis = it }
                }
                DateButton("To: ${gregorianMillisToPersian(toMillis)}", Modifier.weight(1f)) {
                    pickDate(context, toMillis) { toMillis = it }
                }
            }

            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(CardBg).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoRow("Days worked", summary.daysWorked.toString())
                InfoRow("Hours worked", formatMinutes(summary.totalMinutes))
                InfoRow("Overtime", formatMinutes(summary.overtimeMinutes), Green)
                InfoRow("Undertime", formatMinutes(summary.undertimeMinutes), Red)
                InfoRow("Salary for period", "${formatAmount(summary.baseSalary)} تومان")
            }

            Spacer(Modifier.height(20.dp))
            SectionLabel("DEBT / ADJUSTMENTS")

            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(CardBg).padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SignChip("+", selected = adjPlus, accent = Green) { adjPlus = true }
                    SignChip("−", selected = !adjPlus, accent = Red) { adjPlus = false }
                }
                DarkField(adjAmount, { adjAmount = it.filter { c -> c.isDigit() } }, "Amount", KeyboardType.Number)
                DarkField(adjNote, { adjNote = it }, "Note (reason)")
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(percent = 50)).background(Color.White)
                        .clickable {
                            val value = adjAmount.toDoubleOrNull() ?: 0.0
                            if (value > 0) {
                                viewModel.addAdjustment(employee.id, if (adjPlus) value else -value, adjNote.trim())
                                adjAmount = ""; adjNote = ""
                            }
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Add", color = Color(0xFF1B1B1B), fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(12.dp))

            adjustments.forEach { adj ->
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(CardBg).padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            (if (adj.amount >= 0) "+" else "−") + "${formatAmount(abs(adj.amount))} تومان",
                            color = if (adj.amount >= 0) Green else Red,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (adj.note.isNotBlank()) {
                            Spacer(Modifier.height(2.dp))
                            Text(adj.note, color = TextSecondary, fontSize = 13.sp)
                        }
                        Spacer(Modifier.height(2.dp))
                        Text(gregorianMillisToPersian(adj.date), color = TextSecondary, fontSize = 11.sp)
                    }
                    Icon(
                        Icons.Filled.Close, contentDescription = "delete", tint = TextSecondary,
                        modifier = Modifier.size(18.dp).clickable { viewModel.deleteAdjustment(adj) }
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(CardBg).padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Net payable", color = TextPrimary, fontWeight = FontWeight.Bold)
                Text("${formatAmount(finalTotal)} تومان", color = Green, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, valueColor: Color = TextPrimary) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = TextSecondary, fontSize = 13.sp)
        Text(value, color = valueColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SignChip(text: String, selected: Boolean, accent: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier.clip(RoundedCornerShape(percent = 50)).background(if (selected) accent else ChipBg)
            .clickable { onClick() }.padding(horizontal = 22.dp, vertical = 8.dp)
    ) {
        Text(text, color = if (selected) Color.White else TextSecondary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DateButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier.clip(RoundedCornerShape(12.dp)).background(CardBg).clickable { onClick() }.padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = TextPrimary, fontSize = 13.sp)
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(10.dp))
}

@Composable
private fun DarkField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = Color.White,
            focusedBorderColor = Color.White,
            unfocusedBorderColor = TextSecondary,
            focusedLabelColor = TextSecondary,
            unfocusedLabelColor = TextSecondary,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        ),
        modifier = Modifier.fillMaxWidth()
    )
}


private fun computeSummary(
    records: List<AttendanceRecord>,
    employee: Employee,
    fromKey: String,
    toKey: String
): PayrollSummary {
    var days = 0
    var totalMin = 0
    var overtime = 0
    var undertime = 0
    val requiredMin = employee.dailyHours * 60

    records.filter { it.isPresent && it.dayKey >= fromKey && it.dayKey <= toKey }.forEach { r ->
        val worked = minutesWorked(r.entryTime, r.exitTime)
        totalMin += worked
        days++
        val diff = worked - requiredMin
        if (diff > 0) overtime += diff else undertime += -diff
    }

    val hourlyRate = if (employee.dailyHours > 0) employee.salary / (employee.dailyHours * 30.0) else 0.0
    val baseSalary = hourlyRate * (totalMin / 60.0)

    return PayrollSummary(days, totalMin, overtime, undertime, baseSalary)
}

private fun minutesWorked(entry: String, exit: String): Int {
    val start = parseMinutes(entry) ?: return 0
    var end = parseMinutes(exit.ifBlank { "23:30" }) ?: return 0
    if (end < start) end += 24 * 60
    return end - start
}

private fun parseMinutes(time: String): Int? {
    val parts = time.split(":")
    if (parts.size != 2) return null
    val h = parts[0].toIntOrNull() ?: return null
    val m = parts[1].toIntOrNull() ?: return null
    return h * 60 + m
}

private fun formatMinutes(min: Int): String {
    val h = min / 60
    val m = min % 60
    return "${h}h ${m}m"
}

private fun formatAmount(value: Double): String = "%,.0f".format(value)

private fun millisToDayKey(millis: Long): String =
    SimpleDateFormat("yyyy/MM/dd", Locale.US).format(Date(millis))

private fun firstOfCurrentPersianMonthMillis(): Long {
    val c = PersianCalendar.getInstance(ULocale("fa_IR@calendar=persian"))
    val y = c.get(PersianCalendar.YEAR)
    val m = c.get(PersianCalendar.MONTH)
    c.clear()
    c.set(y, m, 1)
    return c.timeInMillis
}

private fun gregorianMillisToPersian(millis: Long): String {
    val c = PersianCalendar.getInstance(ULocale("fa_IR@calendar=persian"))
    c.timeInMillis = millis
    val d = c.get(PersianCalendar.DAY_OF_MONTH)
    val m = c.get(PersianCalendar.MONTH)
    val y = c.get(PersianCalendar.YEAR)
    val name = persianMonthNames.getOrElse(m) { "" }
    return "$d $name $y"
}

private fun pickDate(context: Context, initialMillis: Long, onPicked: (Long) -> Unit) {
    val cal = Calendar.getInstance()
    cal.timeInMillis = initialMillis
    DatePickerDialog(
        context,
        { _, year, month, day ->
            val c = Calendar.getInstance()
            c.set(year, month, day, 12, 0, 0)
            c.set(Calendar.MILLISECOND, 0)
            onPicked(c.timeInMillis)
        },
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH)
    ).show()
}