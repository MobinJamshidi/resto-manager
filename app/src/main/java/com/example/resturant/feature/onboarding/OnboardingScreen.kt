package com.example.resturant.feature.onboarding

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.width
import kotlinx.coroutines.launch

private val Bg = Color(0xFF0E0E10)
private val CardDark = Color(0xFF1C1C1E)
private val TextPrimary = Color.White
private val TextSecondary = Color(0xFF9A9A9E)
private val Yellow = Color(0xFFF5D90A)
private val Blue = Color(0xFF35A7FF)
private val BearBody = Color(0xFF1B1B1D)
private val BearBelly = Color(0xFFF5F5F5)

private data class Slide(
    val title: String,
    val highlight: String,
    val subtitle: String,
    val accent: Color
)

private val slides = listOf(
    Slide(
        title = "Run your restaurant",
        highlight = "in one place",
        subtitle = "Track finances, staff, attendance and menu — all offline, all yours.",
        accent = Yellow
    ),
    Slide(
        title = "Know your money",
        highlight = "every day",
        subtitle = "See debts, expenses and upcoming payments at a glance.",
        accent = Blue
    ),
    Slide(
        title = "Stay organized",
        highlight = "stay calm",
        subtitle = "Notes, tasks and payroll, ready whenever you need them.",
        accent = Yellow
    )
)

@Composable
fun OnboardingScreen(onFinish: () -> Unit = {}) {
    val pagerState = rememberPagerState(pageCount = { slides.size })
    val scope = rememberCoroutineScope()
    val isLast = pagerState.currentPage == slides.lastIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .safeDrawingPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                "Skip",
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onFinish() }
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            val slide = slides[page]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(Modifier.height(20.dp))

                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(color = TextPrimary, fontWeight = FontWeight.ExtraBold)) {
                            append(slide.title + "\n")
                        }
                        withStyle(SpanStyle(color = slide.accent, fontWeight = FontWeight.ExtraBold)) {
                            append(slide.highlight)
                        }
                    },
                    fontSize = 30.sp,
                    lineHeight = 38.sp
                )

                Spacer(Modifier.height(16.dp))
                Text(slide.subtitle, color = TextSecondary, fontSize = 15.sp, lineHeight = 22.sp)

                Spacer(Modifier.height(40.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(260.dp)
                            .clip(RoundedCornerShape(40.dp))
                            .background(CardDark),
                        contentAlignment = Alignment.Center
                    ) {
                        MeditatingBear(accent = slide.accent)
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 28.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(slides.size) { index ->
                    val selected = index == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(if (selected) 24.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (selected) Color.White else Color(0xFF3A3A3C))
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable {
                        if (isLast) onFinish()
                        else scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next",
                    tint = Color(0xFF1B1B1B),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun MeditatingBear(accent: Color) {
    Canvas(modifier = Modifier.size(150.dp)) {
        drawMeditatingBear(accent)
    }
}

private fun DrawScope.drawMeditatingBear(accent: Color) {
    val w = size.width
    val h = size.height
    val cx = w / 2f

    drawCircle(
        color = accent.copy(alpha = 0.18f),
        radius = w * 0.46f,
        center = Offset(cx, h * 0.5f)
    )

    drawRoundRectCompat(
        color = BearBody,
        left = cx - w * 0.30f,
        top = h * 0.42f,
        right = cx + w * 0.30f,
        bottom = h * 0.86f,
        corner = w * 0.28f
    )

    drawRoundRectCompat(
        color = BearBelly,
        left = cx - w * 0.17f,
        top = h * 0.52f,
        right = cx + w * 0.17f,
        bottom = h * 0.82f,
        corner = w * 0.16f
    )

    drawCircle(color = BearBody, radius = w * 0.20f, center = Offset(cx, h * 0.34f))

    drawCircle(color = BearBody, radius = w * 0.075f, center = Offset(cx - w * 0.17f, h * 0.20f))
    drawCircle(color = BearBody, radius = w * 0.075f, center = Offset(cx + w * 0.17f, h * 0.20f))

    drawLine(
        color = BearBelly,
        start = Offset(cx - w * 0.10f, h * 0.34f),
        end = Offset(cx - w * 0.04f, h * 0.34f),
        strokeWidth = w * 0.012f
    )
    drawLine(
        color = BearBelly,
        start = Offset(cx + w * 0.04f, h * 0.34f),
        end = Offset(cx + w * 0.10f, h * 0.34f),
        strokeWidth = w * 0.012f
    )

    drawCircle(color = BearBody, radius = w * 0.06f, center = Offset(cx - w * 0.28f, h * 0.70f))
    drawCircle(color = BearBody, radius = w * 0.06f, center = Offset(cx + w * 0.28f, h * 0.70f))

    drawCircle(color = BearBelly, radius = w * 0.07f, center = Offset(cx - w * 0.16f, h * 0.84f))
    drawCircle(color = BearBelly, radius = w * 0.07f, center = Offset(cx + w * 0.16f, h * 0.84f))

    val steamTop = h * 0.12f
    drawArc(
        color = accent,
        startAngle = 200f,
        sweepAngle = 140f,
        useCenter = false,
        topLeft = Offset(cx - w * 0.05f, steamTop),
        size = Size(w * 0.10f, h * 0.10f),
        style = Stroke(width = w * 0.018f)
    )
}

private fun DrawScope.drawRoundRectCompat(
    color: Color,
    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
    corner: Float
) {
    drawRoundRect(
        color = color,
        topLeft = Offset(left, top),
        size = Size(right - left, bottom - top),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(corner, corner)
    )
}