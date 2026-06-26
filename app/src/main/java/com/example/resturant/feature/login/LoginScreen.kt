package com.example.resturant.feature.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.resturant.core.settings.SettingsViewModel
import kotlinx.coroutines.delay

private val Bg = Color(0xFF0E0E10)
private val BoxBg = Color(0xFF1C1C1E)
private val BoxBorder = Color(0xFF2C2C2E)
private val ErrorRed = Color(0xFFE5534B)
private val TextPrimary = Color.White
private val TextSecondary = Color(0xFF9A9A9E)

private const val PIN_LENGTH = 4

@Composable
fun LoginScreen(
    settingsViewModel: SettingsViewModel = viewModel(),
    onUnlock: () -> Unit = {}
) {
    val correctPin by settingsViewModel.pin.collectAsState()
    var pin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    LaunchedEffect(pin) {
        if (pin.length == PIN_LENGTH) {
            if (correctPin.isNotEmpty() && pin == correctPin) {
                onUnlock()
            } else {
                isError = true
                delay(600)
                pin = ""
                isError = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .safeDrawingPadding()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(80.dp))

        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.ExtraBold)) { append("Resto") }
                withStyle(SpanStyle(color = Color(0xFF8A8A8E))) { append("Manager") }
            },
            fontSize = 26.sp
        )

        Spacer(Modifier.height(60.dp))

        Text("Enter your passcode", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            if (isError) "Wrong passcode, try again" else "Type your 4-digit code to continue",
            color = if (isError) ErrorRed else TextSecondary,
            fontSize = 13.sp
        )

        Spacer(Modifier.height(36.dp))

        BasicTextField(
            value = pin,
            onValueChange = { v ->
                if (!isError && v.length <= PIN_LENGTH && v.all { it.isDigit() }) pin = v
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            textStyle = TextStyle(color = Color.Transparent),
            cursorBrush = SolidColor(Color.Transparent),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .focusable(),
            decorationBox = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    repeat(PIN_LENGTH) { index ->
                        val filled = index < pin.length
                        val active = index == pin.length
                        val border = when {
                            isError -> ErrorRed
                            active -> Color.White
                            else -> BoxBorder
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(18.dp))
                                .background(BoxBg)
                                .border(
                                    if (active || isError) 1.5.dp else 1.dp,
                                    border,
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
        )

        Spacer(Modifier.weight(1f))
    }
}