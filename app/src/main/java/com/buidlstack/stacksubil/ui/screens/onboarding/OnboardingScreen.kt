package com.buidlstack.stacksubil.ui.screens.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buidlstack.stacksubil.ui.components.GradientButton
import com.buidlstack.stacksubil.ui.theme.*
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val description: String,
    val accentColor: Color
)

val onboardingPages = listOf(
    OnboardingPage(
        title = "Save measurements\ninstantly",
        subtitle = "01",
        description = "Store dimensions, distances and areas in seconds. Always at your fingertips.",
        accentColor = AccentCyan
    ),
    OnboardingPage(
        title = "Organize by\nprojects",
        subtitle = "02",
        description = "Group measurements for rooms, furniture or equipment. Keep everything structured.",
        accentColor = AccentMint
    ),
    OnboardingPage(
        title = "Use built-in\ncalculators",
        subtitle = "03",
        description = "Quickly calculate area, volume and material coverage. Built for professionals.",
        accentColor = AccentPurple
    )
)

@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == onboardingPages.size - 1

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            OnboardingPageContent(page = onboardingPages[page])
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                repeat(onboardingPages.size) { index ->
                    val selected = index == pagerState.currentPage
                    val color = onboardingPages[pagerState.currentPage].accentColor
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (selected) color else color.copy(alpha = 0.3f)
                            )
                            .size(
                                width = if (selected) 24.dp else 8.dp,
                                height = 8.dp
                            )
                    )
                }
            }

            if (isLastPage) {
                GradientButton(
                    text = "Get Started",
                    onClick = onFinished,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onFinished) {
                        Text(
                            text = "Skip",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = onboardingPages[pagerState.currentPage].accentColor,
                            contentColor = Color(0xFF0F172A)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("Next", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 80.dp, bottom = 160.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = page.subtitle,
            style = MaterialTheme.typography.labelLarge,
            color = page.accentColor,
            letterSpacing = 4.sp
        )

        Spacer(Modifier.height(16.dp))

        OnboardingIllustration(
            page = onboardingPages.indexOf(page),
            accentColor = page.accentColor,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        )

        Spacer(Modifier.height(40.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 36.sp
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

@Composable
fun OnboardingIllustration(
    page: Int,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "onboarding")
    val animVal by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "anim"
    )

    Canvas(modifier = modifier) {
        val cx = size.width / 2
        val cy = size.height / 2

        when (page) {
            0 -> drawRulerIllustration(cx, cy, accentColor, animVal)
            1 -> drawProjectsIllustration(cx, cy, accentColor, animVal)
            2 -> drawCalculatorsIllustration(cx, cy, accentColor, animVal)
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawRulerIllustration(
    cx: Float, cy: Float, accent: Color, anim: Float
) {
    val rulerW = size.width * 0.7f
    val rulerH = 44f
    val left = cx - rulerW / 2
    val top = cy - rulerH / 2
    drawRoundRect(
        color = accent.copy(alpha = 0.15f),
        topLeft = Offset(left, top),
        size = Size(rulerW, rulerH),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f)
    )
    drawRoundRect(
        color = accent.copy(alpha = 0.5f),
        topLeft = Offset(left, top),
        size = Size(rulerW, rulerH),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f),
        style = Stroke(width = 1.5f)
    )
    val tickCount = 12
    for (i in 0..tickCount) {
        val x = left + (i.toFloat() / tickCount) * rulerW
        val isMajor = i % 3 == 0
        val tickH = if (isMajor) rulerH * 0.5f else rulerH * 0.3f
        drawLine(
            color = accent.copy(alpha = if (isMajor) 0.8f else 0.4f),
            start = Offset(x, top),
            end = Offset(x, top + tickH),
            strokeWidth = if (isMajor) 2f else 1f
        )
    }
    val arrowY = cy + rulerH
    val arrowLeft = left + rulerW * 0.1f
    val arrowRight = left + rulerW * 0.9f
    drawLine(
        color = accent,
        start = Offset(arrowLeft, arrowY + 20f),
        end = Offset(arrowRight, arrowY + 20f),
        strokeWidth = 2f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = accent,
        start = Offset(arrowLeft, arrowY + 12f),
        end = Offset(arrowLeft, arrowY + 28f),
        strokeWidth = 2f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = accent,
        start = Offset(arrowRight, arrowY + 12f),
        end = Offset(arrowRight, arrowY + 28f),
        strokeWidth = 2f,
        cap = StrokeCap.Round
    )
    val markerX = arrowLeft + (arrowRight - arrowLeft) * (0.3f + anim * 0.4f)
    drawCircle(
        color = accent,
        radius = 6f,
        center = Offset(markerX, arrowY + 20f)
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.9f),
        radius = 3f,
        center = Offset(markerX, arrowY + 20f)
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawProjectsIllustration(
    cx: Float, cy: Float, accent: Color, anim: Float
) {
    val cardW = size.width * 0.55f
    val cardH = 64f
    val offsets = listOf(-70f, 0f, 70f)
    val alphas = listOf(0.4f, 0.7f, 0.5f)
    offsets.forEachIndexed { i, yOff ->
        val left = cx - cardW / 2 + (i - 1) * 12f
        val top = cy + yOff - cardH / 2
        drawRoundRect(
            color = accent.copy(alpha = alphas[i]),
            topLeft = Offset(left, top),
            size = Size(cardW, cardH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f),
            style = Stroke(width = 1.5f)
        )
        drawRoundRect(
            color = accent.copy(alpha = alphas[i] * 0.15f),
            topLeft = Offset(left, top),
            size = Size(cardW, cardH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f)
        )
        drawLine(
            color = accent.copy(alpha = alphas[i] * 0.6f),
            start = Offset(left + 12f, top + 20f),
            end = Offset(left + cardW * 0.7f, top + 20f),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = accent.copy(alpha = alphas[i] * 0.4f),
            start = Offset(left + 12f, top + 36f),
            end = Offset(left + cardW * 0.5f, top + 36f),
            strokeWidth = 1.5f,
            cap = StrokeCap.Round
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCalculatorsIllustration(
    cx: Float, cy: Float, accent: Color, anim: Float
) {
    val squareSize = 80f
    drawRect(
        color = accent.copy(alpha = 0.15f),
        topLeft = Offset(cx - squareSize - 10f, cy - squareSize / 2),
        size = Size(squareSize, squareSize)
    )
    drawRect(
        color = accent.copy(alpha = 0.6f),
        topLeft = Offset(cx - squareSize - 10f, cy - squareSize / 2),
        size = Size(squareSize, squareSize),
        style = Stroke(width = 1.5f)
    )
    val cubeX = cx + 20f
    val cubeY = cy - 35f
    val cs = 65f
    drawRect(
        color = AccentMint.copy(alpha = 0.15f),
        topLeft = Offset(cubeX, cubeY),
        size = Size(cs, cs)
    )
    drawRect(
        color = AccentMint.copy(alpha = 0.5f),
        topLeft = Offset(cubeX, cubeY),
        size = Size(cs, cs),
        style = Stroke(width = 1.5f)
    )
    val offset3D = 12f
    val top3DX = cubeX + offset3D
    val top3DY = cubeY - offset3D
    drawLine(AccentMint.copy(alpha = 0.4f), Offset(cubeX, cubeY), Offset(top3DX, top3DY), 1.5f)
    drawLine(AccentMint.copy(alpha = 0.4f), Offset(cubeX + cs, cubeY), Offset(top3DX + cs, top3DY), 1.5f)
    drawLine(AccentMint.copy(alpha = 0.4f), Offset(cubeX + cs, cubeY + cs), Offset(top3DX + cs, top3DY + cs), 1.5f)
    drawLine(AccentMint.copy(alpha = 0.4f), Offset(cubeX, cubeY + cs), Offset(top3DX, top3DY + cs), 1.5f)
    drawLine(AccentMint.copy(alpha = 0.4f), Offset(top3DX, top3DY), Offset(top3DX + cs, top3DY), 1.5f)
    drawLine(AccentMint.copy(alpha = 0.4f), Offset(top3DX, top3DY), Offset(top3DX, top3DY + cs), 1.5f)
    drawLine(AccentMint.copy(alpha = 0.4f), Offset(top3DX + cs, top3DY), Offset(top3DX + cs, top3DY + cs), 1.5f)
}
