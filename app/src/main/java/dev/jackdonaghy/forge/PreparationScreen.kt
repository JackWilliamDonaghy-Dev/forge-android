package dev.jackdonaghy.forge

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * A single full-screen tap target: tap anywhere in the app content to
 * confirm readiness and automatically move to the next step.
 */
@Composable
fun PreparationScreen(
    modifier: Modifier = Modifier,
    stepIndex: Int,
    palette: ForgePalette,
    onConfirm: () -> Unit,
    onBack: () -> Unit
) {
    val step = WorkoutPreparation.steps[stepIndex]
    var confirmed by remember(stepIndex) { mutableStateOf(false) }
    val feedback = LocalHapticFeedback.current

    BackHandler { onBack() }
    LaunchedEffect(confirmed, stepIndex) {
        if (confirmed) {
            delay(240)
            onConfirm()
        }
    }

    Box(
        modifier = modifier
            .clickable(enabled = !confirmed) {
                confirmed = true
                feedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
            .padding(horizontal = 28.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                "GET READY · ${stepIndex + 1} OF ${WorkoutPreparation.steps.size}",
                color = palette.accent, fontSize = 13.sp, fontWeight = FontWeight.Bold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(WorkoutPreparation.steps.size) { i ->
                    Box(
                        Modifier.width(46.dp).height(5.dp).background(
                            if (i <= stepIndex) palette.accent else palette.surface,
                            RoundedCornerShape(50)
                        )
                    )
                }
            }
            Spacer(Modifier.height(26.dp))
            Text(step.symbol, fontSize = 72.sp)
            Text(
                step.title,
                fontSize = 31.sp,
                fontWeight = FontWeight.Bold,
                color = palette.text,
                textAlign = TextAlign.Center,
                lineHeight = 38.sp
            )
            Text(
                step.description,
                fontSize = 16.sp,
                color = palette.muted,
                textAlign = TextAlign.Center,
                lineHeight = 23.sp
            )
            Spacer(Modifier.height(30.dp))
            Text(
                if (confirmed) "✓ Confirmed" else "TAP ANYWHERE TO CONFIRM",
                color = palette.accent,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}
