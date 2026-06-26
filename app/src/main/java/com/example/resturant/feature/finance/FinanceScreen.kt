package com.example.resturant.feature.finance

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.resturant.feature.finance.data.FinanceRecord
import com.example.resturant.feature.finance.data.Partner
import com.example.resturant.feature.finance.data.RecordType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val Bg = Color(0xFF000000)
private val CardBg = Color(0xFF1C1C1E)
private val ChipBg = Color(0xFF2C2C2E)
private val TextPrimary = Color.White
private val TextSecondary = Color(0xFF9A9A9E)

private const val RESTAURANT_SECTION = 3
private const val PARTNER_SECTION_OFFSET = 100000
private fun partnerSection(id: Long): Int = (PARTNER_SECTION_OFFSET + id).toInt()

private fun typeColor(type: RecordType): Color = when (type) {
    RecordType.EXPENSE -> Color(0xFFE08B3D)
    RecordType.DEBT_GIVEN -> Color(0xFF2E9E4F)
    RecordType.WITHDRAWAL -> Color(0xFF4C8DFF)
    RecordType.INSTALLMENT -> Color(0xFF9B6DFF)
}

private fun typeIcon(type: RecordType): ImageVector = when (type) {
    RecordType.EXPENSE -> Icons.Filled.ShoppingCart
    RecordType.DEBT_GIVEN -> Icons.Filled.AccountBalanceWallet
    RecordType.WITHDRAWAL -> Icons.Filled.Savings
    RecordType.INSTALLMENT -> Icons.Filled.Receipt
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FinanceScreen(
    onBack: () -> Unit = {},
    viewModel: FinanceViewModel = viewModel()
) {
    val partners by viewModel.partners.collectAsState(initial = emptyList())
    var selectedSection by remember { mutableIntStateOf(RESTAURANT_SECTION) }
    var addOpen by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<Partner?>(null) }

    LaunchedEffect(partners) {
        if (selectedSection != RESTAURANT_SECTION &&
            partners.none { partnerSection(it.id) == selectedSection }
        ) {
            selectedSection = RESTAURANT_SECTION
        }
    }

    Scaffold(
        containerColor = Bg,
        topBar = {
            TopAppBar(
                title = { Text("Restaurant Financial") },
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
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TabChip(
                    text = "رستوران",
                    selected = selectedSection == RESTAURANT_SECTION,
                    onClick = { selectedSection = RESTAURANT_SECTION }
                )
                partners.forEach { partner ->
                    TabChip(
                        text = partner.name,
                        selected = selectedSection == partnerSection(partner.id),
                        onClick = { selectedSection = partnerSection(partner.id) },
                        onLongClick = { deleteTarget = partner }
                    )
                }
                AddChip { addOpen = true }
            }

            SectionPage(section = selectedSection, viewModel = viewModel)
        }
    }

    if (addOpen) {
        AddPartnerDialog(
            onDismiss = { addOpen = false },
            onAdd = { name ->
                viewModel.addPartner(name)
                addOpen = false
            }
        )
    }

    deleteTarget?.let { partner ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("حذف شریک؟") },
            text = { Text("شریک «${partner.name}» حذف می‌شود.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deletePartner(partner)
                    deleteTarget = null
                }) { Text("حذف", color = Color(0xFFE53935)) }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text("انصراف") }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TabChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(if (selected) Color.White else CardBg)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        Text(
            text,
            color = if (selected) Color(0xFF1B1B1B) else TextSecondary,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

@Composable
private fun AddChip(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(ChipBg)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Filled.Add, contentDescription = "Add partner", tint = TextPrimary, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun AddPartnerDialog(onDismiss: () -> Unit, onAdd: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(CardBg)
                .padding(20.dp)
        ) {
            Text("شریک جدید", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            DarkField(name, { name = it }, "نام شریک")
            Spacer(Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 50))
                        .clickable { onDismiss() }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text("انصراف", color = TextSecondary, fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.size(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 50))
                        .background(Color.White)
                        .clickable { if (name.isNotBlank()) onAdd(name.trim()) }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text("افزودن", color = Color(0xFF1B1B1B), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun SectionPage(section: Int, viewModel: FinanceViewModel) {
    val recordsFlow = remember(section) { viewModel.recordsForSection(section) }
    val records by recordsFlow.collectAsState(initial = emptyList())

    var dialogOpen by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<FinanceRecord?>(null) }

    val totals = remember(records) {
        RecordType.entries.associateWith { t -> records.filter { it.type == t }.sumOf { it.amount } }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RecordType.entries.chunked(2).forEach { rowTypes ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        rowTypes.forEach { t ->
                            StatTile(type = t, total = totals[t] ?: 0.0, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            SectionLabelPadded("RECORDS")

            if (records.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("هنوز رکوردی ثبت نشده", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(records, key = { it.id }) { record ->
                        RecordCard(
                            record = record,
                            onEdit = { editing = record; dialogOpen = true },
                            onDelete = { viewModel.delete(record) }
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .size(58.dp)
                .clip(RoundedCornerShape(percent = 50))
                .background(Color.White)
                .clickable { editing = null; dialogOpen = true },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add", tint = Color(0xFF1B1B1B), modifier = Modifier.size(26.dp))
        }
    }

    if (dialogOpen) {
        RecordDialog(
            existing = editing,
            onDismiss = { dialogOpen = false },
            onSave = { record ->
                viewModel.save(record.copy(section = section))
                dialogOpen = false
            },
            onDelete = { record ->
                viewModel.delete(record)
                dialogOpen = false
            }
        )
    }
}

@Composable
private fun StatTile(type: RecordType, total: Double, modifier: Modifier = Modifier) {
    val color = typeColor(type)
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(color.copy(alpha = 0.13f))
            .padding(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(typeIcon(type), contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.height(10.dp))
        Text(type.label, color = TextSecondary, fontSize = 12.sp)
        Spacer(Modifier.height(2.dp))
        Text("${formatAmount(total)}", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun RecordCard(record: FinanceRecord, onEdit: () -> Unit, onDelete: () -> Unit) {
    val color = typeColor(record.type)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CardBg)
            .clickable { onEdit() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(typeIcon(record.type), contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
        }

        Spacer(Modifier.size(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(record.type.label, color = color, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text(formatDate(record.date), color = TextSecondary, fontSize = 11.sp)
            }
            Spacer(Modifier.height(4.dp))
            Text("${formatAmount(record.amount)} تومان", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            if (record.description.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(record.description, color = TextSecondary, fontSize = 13.sp)
            }

            if (record.type == RecordType.INSTALLMENT) {
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(ChipBg)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        "کل ${formatAmount(record.totalInstallment ?: 0.0)} • ${record.months ?: 0} ماه • مانده ${formatAmount(record.remaining ?: 0.0)}",
                        color = TextSecondary, fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(Modifier.size(8.dp))
        Icon(
            Icons.Filled.Close,
            contentDescription = "delete",
            tint = TextSecondary,
            modifier = Modifier
                .size(20.dp)
                .clickable { onDelete() }
        )
    }
}

@Composable
private fun RecordDialog(
    existing: FinanceRecord?,
    onDismiss: () -> Unit,
    onSave: (FinanceRecord) -> Unit,
    onDelete: (FinanceRecord) -> Unit
) {
    val context = LocalContext.current

    var type by remember { mutableStateOf(existing?.type ?: RecordType.EXPENSE) }
    var amount by remember { mutableStateOf(existing?.amount?.toLong()?.toString() ?: "") }
    var description by remember { mutableStateOf(existing?.description ?: "") }
    var date by remember { mutableLongStateOf(existing?.date ?: System.currentTimeMillis()) }
    var total by remember { mutableStateOf(existing?.totalInstallment?.toLong()?.toString() ?: "") }
    var months by remember { mutableStateOf(existing?.months?.toString() ?: "") }
    var remaining by remember { mutableStateOf(existing?.remaining?.toLong()?.toString() ?: "") }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Bg)
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
                            val isInstallment = type == RecordType.INSTALLMENT
                            val record = (existing ?: FinanceRecord(
                                section = 0, type = type, amount = 0.0, date = date
                            )).copy(
                                type = type,
                                amount = amount.toDoubleOrNull() ?: 0.0,
                                date = date,
                                description = description.trim(),
                                totalInstallment = if (isInstallment) total.toDoubleOrNull() else null,
                                months = if (isInstallment) months.toIntOrNull() else null,
                                remaining = if (isInstallment) remaining.toDoubleOrNull() else null
                            )
                            onSave(record)
                        }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text("Save", color = Color(0xFF1B1B1B), fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                if (existing == null) "افزودن رکورد" else "ویرایش رکورد",
                color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(20.dp))
            SectionLabel("TYPE")
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                RecordType.entries.chunked(2).forEach { rowTypes ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        rowTypes.forEach { t ->
                            val selected = type == t
                            val color = typeColor(t)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (selected) color else ChipBg)
                                    .clickable { type = t }
                                    .padding(vertical = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    t.label,
                                    color = if (selected) Color.White else TextSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            DarkField(amount, { amount = it.filter { c -> c.isDigit() } }, "مبلغ", KeyboardType.Number)
            Spacer(Modifier.height(12.dp))
            DarkField(description, { description = it }, "توضیحات (برای چه کسی / بابت چه چیزی)")

            if (type == RecordType.INSTALLMENT) {
                Spacer(Modifier.height(12.dp))
                DarkField(total, { total = it.filter { c -> c.isDigit() } }, "مبلغ کل قسط", KeyboardType.Number)
                Spacer(Modifier.height(12.dp))
                DarkField(months, { months = it.filter { c -> c.isDigit() } }, "تعداد ماه‌ها", KeyboardType.Number)
                Spacer(Modifier.height(12.dp))
                DarkField(remaining, { remaining = it.filter { c -> c.isDigit() } }, "مبلغ باقیمانده", KeyboardType.Number)
            }

            Spacer(Modifier.height(16.dp))
            SectionLabel("DATE")
            DateCard("تاریخ", formatDate(date)) {
                showDatePicker(context, date) { date = it }
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
                    Text("Delete Record", color = Color(0xFFE53935), fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(10.dp))
}

@Composable
private fun SectionLabelPadded(text: String) {
    Text(
        text, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
    )
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
    val calendar = Calendar.getInstance().apply { timeInMillis = current }
    DatePickerDialog(
        context,
        { _, year, month, day ->
            val picked = Calendar.getInstance().apply {
                set(year, month, day, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            onPicked(picked.timeInMillis)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

private fun formatDate(millis: Long): String =
    SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date(millis))

private fun formatAmount(value: Double): String = "%,.0f".format(value)