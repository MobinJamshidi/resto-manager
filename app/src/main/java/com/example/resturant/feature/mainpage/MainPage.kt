package com.example.resturant.feature.mainpage

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Bg = Color(0xFF000000)
private val Surface = Color(0xFF1C1C1E)
private val PillBg = Color(0xFF2C2C2E)
private val Glass = Color(0x14FFFFFF)
private val GlassBorder = Color(0x26FFFFFF)
private val TextPrimary = Color.White
private val TextSecondary = Color(0xFF9A9A9E)
private val DonePill = Color.White
private val DonePillText = Color(0xFF1B1B1B)
private val TrackColor = Color(0xFF3A3A3C)

@Composable
fun MainPage(
    restaurantName: String = "",
    onFinanceClick: () -> Unit = {},
    onEmployeeClick: () -> Unit = {},
    onNoteClick: () -> Unit = {},
    onAttendanceClick: () -> Unit = {},
    onProductsClick: () -> Unit = {},
    onPayrollClick: () -> Unit = {},
    onAccountClick: () -> Unit = {},
    viewModel: MainViewModel = viewModel()
) {
    val debtTotal by viewModel.restaurantDebtTotal.collectAsState(initial = 0.0)
    val upcomingDebts by viewModel.upcomingDebts.collectAsState(initial = emptyList())
    val tasks = viewModel.tasks
    var input by remember { mutableStateOf("") }

    val cards = listOf(
        GridItem("Restaurant Financial", Icons.Filled.Payments, onFinanceClick),
        GridItem("Employee Management", Icons.Filled.Groups, onEmployeeClick),
        GridItem("Note", Icons.Filled.EditNote, onNoteClick),
        GridItem("Products", Icons.Filled.Inventory2, onProductsClick),
        GridItem("Attendance", Icons.Filled.FactCheck, onAttendanceClick),
        GridItem("Accounts", Icons.Filled.ManageAccounts, onAccountClick),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .safeDrawingPadding()
            .padding(horizontal = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(8.dp))

            Text(
                if (restaurantName.isBlank()) "Good work" else "Good work, $restaurantName",
                fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary
            )

            Spacer(Modifier.height(12.dp))

            SummaryRow(
                title = "Restaurant total debt",
                value = "${formatAmount(debtTotal)} تومان"
            )

            if (upcomingDebts.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Text("Debts due tomorrow", fontSize = 13.sp, color = TextSecondary)
                Spacer(Modifier.height(6.dp))
                upcomingDebts.forEach { debt ->
                    SummaryRow(
                        title = formatDate(debt.date),
                        value = "${formatAmount(debt.amount)} تومان"
                    )
                    Spacer(Modifier.height(6.dp))
                }
            }

            Spacer(Modifier.height(16.dp))

            TasksCard(
                tasks = tasks,
                onToggle = { viewModel.toggleTask(it) },
                onDelete = { viewModel.deleteTask(it) }
            )

            Spacer(Modifier.height(16.dp))

            cards.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { item ->
                        GridCard(item = item, modifier = Modifier.weight(1f))
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }

        InputBar(
            value = input,
            onValueChange = { input = it },
            onSend = {
                viewModel.addTask(input)
                input = ""
            }
        )

        Spacer(Modifier.height(12.dp))
    }
}

private data class GridItem(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
private fun TasksCard(
    tasks: List<Task>,
    onToggle: (Long) -> Unit,
    onDelete: (Long) -> Unit
) {
    val total = tasks.size
    val done = tasks.count { it.isDone }
    val remaining = total - done

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Glass)
            .border(1.dp, GlassBorder, RoundedCornerShape(28.dp))
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Tasks", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(Modifier.height(2.dp))
                Text(
                    text = if (total == 0) "هنوز تسکی نیست" else "$remaining تسک باقی مانده",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
            ProgressRing(done = done, total = total)
        }

        if (tasks.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                tasks.chunked(2).forEach { rowTasks ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        rowTasks.forEach { task ->
                            TaskPill(
                                task = task,
                                onToggle = onToggle,
                                onDelete = onDelete,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowTasks.size == 1) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskPill(
    task: Task,
    onToggle: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDone = task.isDone
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(if (isDone) DonePill else PillBg)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clickable { onToggle(task.id) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = task.text,
                color = if (isDone) DonePillText else TextPrimary,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(6.dp))
            if (isDone) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    tint = DonePillText,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, TextSecondary, CircleShape)
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        Icon(
            Icons.Filled.Close,
            contentDescription = "delete",
            tint = if (isDone) DonePillText else TextSecondary,
            modifier = Modifier
                .size(18.dp)
                .clickable { onDelete(task.id) }
        )
    }
}

@Composable
private fun ProgressRing(done: Int, total: Int) {
    val fraction = if (total == 0) 0f else done.toFloat() / total.toFloat()
    Box(
        modifier = Modifier.size(46.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { fraction },
            modifier = Modifier.size(46.dp),
            color = Color.White,
            trackColor = TrackColor,
            strokeWidth = 3.dp
        )
        Text("$done/$total", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun SummaryRow(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Surface)
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = TextSecondary, fontSize = 13.sp)
        Text(value, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun GridCard(item: GridItem, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .height(96.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .clickable { item.onClick() }
            .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(item.icon, contentDescription = null, tint = TextPrimary)
        Text(item.label, fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun InputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Surface)
            .padding(start = 18.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 16.dp)
        ) {
            if (value.isEmpty()) {
                Text("input task", color = TextSecondary, fontSize = 15.sp)
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(color = TextPrimary, fontSize = 15.sp),
                cursorBrush = SolidColor(Color.White),
                modifier = Modifier.fillMaxWidth()
            )
        }
        IconButton(onClick = onSend) {
            Icon(
                Icons.AutoMirrored.Filled.Send,
                contentDescription = "send",
                tint = Color.White
            )
        }
    }
}

private fun formatAmount(value: Double): String = "%,.0f".format(value)

private fun formatDate(millis: Long): String =
    SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date(millis))