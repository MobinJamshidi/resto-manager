package com.example.resturant.feature.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF0E0E10)
private val BoxBg = Color(0xFF1C1C1E)
private val BoxBorder = Color(0xFF2C2C2E)
private val LineColor = Color(0xFF55555A)
private val TextPrimary = Color.White
private val TextSecondary = Color(0xFF9A9A9E)
private val ErrorRed = Color(0xFFE5534B)

private const val PIN_LENGTH = 4

@Composable
fun SetupScreen(
    onDone: (restaurantName: String, pin: String) -> Unit = { _, _ -> }
) {
    var name by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp)
    ) {
        Spacer(Modifier.height(40.dp))

        Text("Let's set up", color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(8.dp))
        Text(
            "Name your café and choose a 4-digit passcode. You'll use this code to open the app.",
            color = TextSecondary, fontSize = 15.sp, lineHeight = 22.sp
        )

        Spacer(Modifier.height(36.dp))

        Text("Café name", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(10.dp))
        BasicTextField(
            value = name,
            onValueChange = { name = it; error = null },
            singleLine = true,
            textStyle = TextStyle(color = TextPrimary, fontSize = 16.sp),
            cursorBrush = SolidColor(Color.White),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(LineColor))

        Spacer(Modifier.height(32.dp))

        Text("Passcode", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(12.dp))
        PinRow(pin)
        BasicTextField(
            value = pin,
            onValueChange = { if (it.length <= PIN_LENGTH && it.all { c -> c.isDigit() }) { pin = it; error = null } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            textStyle = TextStyle(color = Color.Transparent),
            cursorBrush = SolidColor(Color.Transparent),
            modifier = Modifier.fillMaxWidth().height(1.dp)
        )

        Spacer(Modifier.height(28.dp))

        Text("Confirm passcode", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(12.dp))
        PinRow(confirm)
        BasicTextField(
            value = confirm,
            onValueChange = { if (it.length <= PIN_LENGTH && it.all { c -> c.isDigit() }) { confirm = it; error = null } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            textStyle = TextStyle(color = Color.Transparent),
            cursorBrush = SolidColor(Color.Transparent),
            modifier = Modifier.fillMaxWidth().height(1.dp)
        )

        if (error != null) {
            Spacer(Modifier.height(18.dp))
            Text(error!!, color = ErrorRed, fontSize = 13.sp)
        }

        Spacer(Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .clickable {
                    error = when {
                        name.isBlank() -> "Please enter your café name"
                        pin.length != PIN_LENGTH -> "Passcode must be 4 digits"
                        pin != confirm -> "Passcodes don't match"
                        else -> null
                    }
                    if (error == null) onDone(name.trim(), pin)
                }
                .padding(vertical = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Create & continue", color = Color(0xFF1B1B1B), fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun PinRow(value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        repeat(PIN_LENGTH) { index ->
            val filled = index < value.length
            val active = index == value.length
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(BoxBg)
                    .border(
                        if (active) 1.5.dp else 1.dp,
                        if (active) Color.White else BoxBorder,
                        RoundedCornerShape(18.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (filled) {
                    Box(
                        modifier = Modifier
                            .height(14.dp)
                            .fillMaxWidth(0.28f)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
            }
        }
    }
}