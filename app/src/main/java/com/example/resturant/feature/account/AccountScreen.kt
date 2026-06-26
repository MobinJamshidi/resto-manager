package com.example.resturant.feature.account

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.resturant.core.settings.SettingsViewModel

private val Bg = Color(0xFF000000)
private val CardBg = Color(0xFF1C1C1E)
private val TextPrimary = Color.White
private val TextSecondary = Color(0xFF9A9A9E)
private val Accent = Color(0xFF2E9E4F)
private val DividerColor = Color(0xFF2C2C2E)
private val LockRed = Color(0xFFE5534B)

@Composable
fun AccountScreen(
    settingsViewModel: SettingsViewModel = viewModel(),
    onBack: () -> Unit = {},
    onLock: () -> Unit = {}
) {
    val context = LocalContext.current
    val restaurant by settingsViewModel.restaurantName.collectAsState()

    var showNameDialog by remember { mutableStateOf(false) }
    var showPinDialog by remember { mutableStateOf(false) }
    var showSupportDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Text("Account", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Accent.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Storefront, contentDescription = null, tint = Accent, modifier = Modifier.size(46.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(
                restaurant.ifBlank { "My Restaurant" },
                color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(2.dp))
            Text("Local account", color = TextSecondary, fontSize = 14.sp)

            Spacer(Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(percent = 50))
                    .border(1.dp, Accent, RoundedCornerShape(percent = 50))
                    .clickable { showNameDialog = true }
                    .padding(horizontal = 22.dp, vertical = 9.dp)
            ) {
                Text("Edit name", color = Accent, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(24.dp))

        SectionLabel("ACCOUNT")
        SettingsGroup {
            RowItem(Icons.Filled.Storefront, "Restaurant name", restaurant.ifBlank { "—" }) { showNameDialog = true }
            DividerLine()
            RowItem(Icons.Filled.Lock, "Change passcode", "") { showPinDialog = true }
        }

        Spacer(Modifier.height(20.dp))

        SectionLabel("SUPPORT")
        SettingsGroup {
            RowItem(Icons.Filled.SupportAgent, "Contact support", "") { showSupportDialog = true }
        }

        Spacer(Modifier.height(28.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(CardBg)
                .clickable { onLock() }
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Lock, contentDescription = null, tint = LockRed, modifier = Modifier.size(20.dp))
                Spacer(Modifier.size(8.dp))
                Text("Lock app", color = LockRed, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
        }

        Spacer(Modifier.height(40.dp))
    }

    if (showNameDialog) {
        EditNameDialog(
            current = restaurant,
            onDismiss = { showNameDialog = false },
            onSave = { newName ->
                settingsViewModel.updateRestaurantName(newName)
                Toast.makeText(context, "ذخیره شد", Toast.LENGTH_SHORT).show()
                showNameDialog = false
            }
        )
    }

    if (showPinDialog) {
        ChangePinDialog(
            onDismiss = { showPinDialog = false },
            onSave = { newPin ->
                settingsViewModel.updatePin(newPin)
                Toast.makeText(context, "رمز تغییر کرد", Toast.LENGTH_SHORT).show()
                showPinDialog = false
            }
        )
    }

    if (showSupportDialog) {
        SupportDialog(onDismiss = { showSupportDialog = false })
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        color = TextSecondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 20.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg),
        content = content
    )
}

@Composable
private fun RowItem(icon: ImageVector, title: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(22.dp))
        Spacer(Modifier.size(14.dp))
        Text(title, color = TextPrimary, fontSize = 15.sp, modifier = Modifier.weight(1f))
        if (value.isNotBlank()) {
            Text(value, color = TextSecondary, fontSize = 14.sp)
            Spacer(Modifier.size(6.dp))
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun DividerLine() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 52.dp)
            .height(1.dp)
            .background(DividerColor)
    )
}

@Composable
private fun darkFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    cursorColor = Color.White,
    focusedBorderColor = Color.White,
    unfocusedBorderColor = TextSecondary,
    focusedLabelColor = TextSecondary,
    unfocusedLabelColor = TextSecondary,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent
)

@Composable
private fun EditNameDialog(current: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var name by remember { mutableStateOf(current) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBg,
        titleContentColor = TextPrimary,
        textContentColor = TextPrimary,
        title = { Text("Restaurant name") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                label = { Text("نام جدید") },
                colors = darkFieldColors()
            )
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onSave(name) }) {
                Text("ذخیره", color = Accent)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("انصراف", color = TextSecondary) }
        }
    )
}

@Composable
private fun ChangePinDialog(onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var newPin by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBg,
        titleContentColor = TextPrimary,
        textContentColor = TextPrimary,
        title = { Text("Change passcode") },
        text = {
            Column {
                OutlinedTextField(
                    value = newPin,
                    onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) { newPin = it; error = null } },
                    singleLine = true,
                    label = { Text("رمز جدید (۴ رقم)") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    colors = darkFieldColors()
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = confirm,
                    onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) { confirm = it; error = null } },
                    singleLine = true,
                    label = { Text("تکرار رمز جدید") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    colors = darkFieldColors()
                )
                if (error != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(error!!, color = LockRed, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                error = when {
                    newPin.length != 4 -> "رمز باید ۴ رقم باشد"
                    newPin != confirm -> "تکرار رمز مطابقت ندارد"
                    else -> null
                }
                if (error == null) onSave(newPin)
            }) { Text("ذخیره", color = Accent) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("انصراف", color = TextSecondary) }
        }
    )
}

@Composable
private fun SupportDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBg,
        titleContentColor = TextPrimary,
        textContentColor = TextPrimary,
        title = { Text("Contact support") },
        text = {
            Column {
                Text(
                    "RestoManager از طراحی رابط کاربری تا کدنویسی، به‌طور کامل توسط یک نفر طراحی و توسعه داده شده است.",
                    color = TextPrimary,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(12.dp))
                Text("برای ارتباط، پیشنهاد یا گزارش مشکل:", color = TextSecondary, fontSize = 13.sp)
                Spacer(Modifier.height(6.dp))
                Text(
                    "jamshid.mobin567@gmail.com",
                    color = Accent,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("باشه", color = Accent) }
        }
    )
}