package com.example.resturant.feature.employee

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
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
import com.example.resturant.feature.employee.data.Employee
import com.example.resturant.feature.employee.data.GuaranteeType
import com.example.resturant.feature.employee.data.MaritalStatus
import com.example.resturant.feature.employee.data.Position
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val DarkBg = Color(0xFF000000)
private val CardBg = Color(0xFF1C1C1E)
private val ChipBg = Color(0xFF2C2C2E)
private val ChipSelected = Color(0xFF2E9E4F)
private val TextPrimary = Color.White
private val TextSecondary = Color(0xFF9A9A9E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeScreen(
    onBack: () -> Unit = {},
    viewModel: EmployeeViewModel = viewModel()
) {
    val employees by viewModel.employees.collectAsState(initial = emptyList())
    var dialogOpen by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Employee?>(null) }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Employee Management") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBg,
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
        ) {
            if (employees.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("هنوز کارمندی ثبت نشده", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(employees, key = { it.id }) { employee ->
                        EmployeeCard(employee = employee) {
                            editing = employee
                            dialogOpen = true
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(Color.White)
                    .clickable {
                        editing = null
                        dialogOpen = true
                    }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Add Employee", color = Color(0xFF1B1B1B), fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
        }
    }

    if (dialogOpen) {
        EmployeeDialog(
            existing = editing,
            onDismiss = { dialogOpen = false },
            onSave = { employee ->
                if (employee.id == 0L) viewModel.addEmployee(employee)
                else viewModel.updateEmployee(employee)
                dialogOpen = false
            },
            onDelete = { employee ->
                viewModel.deleteEmployee(employee)
                dialogOpen = false
            }
        )
    }
}

@Composable
private fun EmployeeCard(employee: Employee, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(employee.fullName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(4.dp))
            Text(employee.position.label, color = TextSecondary, fontSize = 13.sp)
        }
        Text(employee.phoneNumber, color = TextSecondary, fontSize = 13.sp)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EmployeeDialog(
    existing: Employee?,
    onDismiss: () -> Unit,
    onSave: (Employee) -> Unit,
    onDelete: (Employee) -> Unit
) {
    val context = LocalContext.current

    var fullName by remember { mutableStateOf(existing?.fullName ?: "") }
    var age by remember { mutableStateOf(existing?.age?.toString() ?: "") }
    var phone by remember { mutableStateOf(existing?.phoneNumber ?: "") }
    var emergencyPhone by remember { mutableStateOf(existing?.emergencyPhone ?: "") }
    var position by remember { mutableStateOf(existing?.position ?: Position.WAITER) }
    var nationalId by remember { mutableStateOf(existing?.nationalId ?: "") }
    var marital by remember { mutableStateOf(existing?.maritalStatus ?: MaritalStatus.SINGLE) }
    var address by remember { mutableStateOf(existing?.address ?: "") }
    var salary by remember { mutableStateOf(existing?.salary?.toLong()?.toString() ?: "") }
    var startDate by remember { mutableLongStateOf(existing?.startDate ?: System.currentTimeMillis()) }
    var experience by remember { mutableStateOf(existing?.workExperience ?: "") }
    var hasHealthCard by remember { mutableStateOf(existing?.hasHealthCard ?: false) }
    var healthExpiration by remember { mutableLongStateOf(existing?.healthCardExpiration ?: System.currentTimeMillis()) }
    var guarantee by remember { mutableStateOf(existing?.guaranteeType ?: GuaranteeType.PROMISSORY_NOTE) }
    var dailyHours by remember { mutableStateOf(existing?.dailyHours?.toString() ?: "") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(percent = 50))
                            .background(Color.White)
                            .clickable {
                                val employee = (existing ?: Employee(
                                    fullName = "", age = 0, phoneNumber = "", emergencyPhone = "",
                                    position = position, nationalId = "", maritalStatus = marital,
                                    address = "", salary = 0.0, startDate = startDate,
                                    workExperience = "", hasHealthCard = false, guaranteeType = guarantee
                                )).copy(
                                    fullName = fullName.trim(),
                                    age = age.toIntOrNull() ?: 0,
                                    dailyHours = dailyHours.toIntOrNull() ?: 0,
                                    phoneNumber = phone.trim(),
                                    emergencyPhone = emergencyPhone.trim(),
                                    position = position,
                                    nationalId = nationalId.trim(),
                                    maritalStatus = marital,
                                    address = address.trim(),
                                    salary = salary.toDoubleOrNull() ?: 0.0,
                                    startDate = startDate,
                                    workExperience = experience.trim(),
                                    hasHealthCard = hasHealthCard,
                                    healthCardExpiration = if (hasHealthCard) healthExpiration else null,
                                    guaranteeType = guarantee
                                )
                                onSave(employee)
                            }
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text("Save", color = Color(0xFF1B1B1B), fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = if (existing == null) "Add Employee" else "Edit Employee",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(20.dp))

                DarkField(fullName, { fullName = it }, "Full Name")
                Spacer(Modifier.height(12.dp))
                DarkField(age, { age = it.filter { c -> c.isDigit() } }, "Age", KeyboardType.Number)
                Spacer(Modifier.height(12.dp))
                DarkField(phone, { phone = it.filter { c -> c.isDigit() } }, "Phone Number", KeyboardType.Phone)
                Spacer(Modifier.height(12.dp))
                DarkField(emergencyPhone, { emergencyPhone = it.filter { c -> c.isDigit() } }, "Emergency Phone Number", KeyboardType.Phone)
                Spacer(Modifier.height(12.dp))
                DarkField(nationalId, { nationalId = it.filter { c -> c.isDigit() } }, "National ID", KeyboardType.Number)
                Spacer(Modifier.height(12.dp))
                DarkField(address, { address = it }, "Residential Address")
                Spacer(Modifier.height(12.dp))
                DarkField(salary, { salary = it.filter { c -> c.isDigit() } }, "Salary", KeyboardType.Number)
                Spacer(Modifier.height(12.dp))
                DarkField(dailyHours, { dailyHours = it.filter { c -> c.isDigit() } }, "Daily Work Hours (e.g. 10)", KeyboardType.Number)
                Spacer(Modifier.height(12.dp))
                DarkField(experience, { experience = it }, "Work Experience")

                Spacer(Modifier.height(20.dp))

                SectionLabel("POSITION")
                ChipGroup(
                    options = Position.entries,
                    selected = position,
                    onSelect = { position = it },
                    label = { it.label }
                )

                Spacer(Modifier.height(20.dp))

                SectionLabel("MARITAL STATUS")
                ChipGroup(
                    options = MaritalStatus.entries,
                    selected = marital,
                    onSelect = { marital = it },
                    label = { it.label }
                )

                Spacer(Modifier.height(20.dp))

                SectionLabel("GUARANTEE TYPE")
                ChipGroup(
                    options = GuaranteeType.entries,
                    selected = guarantee,
                    onSelect = { guarantee = it },
                    label = { it.label }
                )

                Spacer(Modifier.height(20.dp))

                SectionLabel("HEALTH CARD")
                ChipGroup(
                    options = listOf(true, false),
                    selected = hasHealthCard,
                    onSelect = { hasHealthCard = it },
                    label = { if (it) "Has card" else "No card" }
                )

                if (hasHealthCard) {
                    Spacer(Modifier.height(12.dp))
                    DateCard("Health Card Expiration", formatDate(healthExpiration)) {
                        showDatePicker(context, healthExpiration) { healthExpiration = it }
                    }
                }

                Spacer(Modifier.height(20.dp))

                SectionLabel("START DATE")
                DateCard("Start Date Availability", formatDate(startDate)) {
                    showDatePicker(context, startDate) { startDate = it }
                }

                if (existing != null) {
                    Spacer(Modifier.height(28.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(percent = 50))
                            .background(ChipBg)
                            .clickable { onDelete(existing) }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Delete Employee", color = Color(0xFFE53935), fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = TextSecondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(Modifier.height(10.dp))
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun <T> ChipGroup(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    label: (T) -> String
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(percent = 50))
                    .background(if (isSelected) ChipSelected else ChipBg)
                    .clickable { onSelect(option) }
                    .padding(horizontal = 18.dp, vertical = 12.dp)
            ) {
                Text(
                    text = label(option),
                    color = if (isSelected) Color.White else TextSecondary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun DateCard(label: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = TextSecondary)
        Spacer(Modifier.height(0.dp))
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(label, color = TextSecondary, fontSize = 12.sp)
            Text(value, color = TextPrimary, fontSize = 16.sp)
        }
    }
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

private fun showDatePicker(context: Context, current: Long, onPicked: (Long) -> Unit) {
    val cal = Calendar.getInstance().apply { timeInMillis = current }
    DatePickerDialog(
        context,
        { _, year, month, day ->
            val picked = Calendar.getInstance().apply {
                set(year, month, day, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            onPicked(picked.timeInMillis)
        },
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH)
    ).show()
}

private fun formatDate(millis: Long): String =
    SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date(millis))