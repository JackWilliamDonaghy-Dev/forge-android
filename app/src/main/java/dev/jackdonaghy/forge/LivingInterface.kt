package dev.jackdonaghy.forge

import android.app.Activity
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

/** A single evolving surface; the workout engine and voice cues stay unchanged. */
@Composable
fun LivingInterfaceRoot(vm: ForgeViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    val palette = state.theme.palette()
    val step = state.plan?.steps?.getOrNull(state.timer?.index ?: -1)
    val scene = LivingScenes.resolve(state.page, step?.phase, state.timer?.completed == true)
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val coach = remember(context) { VoiceCoach(context) }
    var confirmExit by remember { mutableStateOf(false) }

    DisposableEffect(coach) { onDispose { coach.close() } }
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) vm.pauseOnBackground()
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
    DisposableEffect(state.page) {
        val activity = context as? Activity
        if (state.page == ForgePage.WORKOUT) {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose { activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
    }
    LaunchedEffect(vm, coach) {
        vm.cues.collect { cue -> coach.play(cue, vm.state.value.speak, vm.state.value.beep) }
    }

    BackHandler(state.page != ForgePage.HOME) {
        when (state.page) {
            ForgePage.SETTINGS -> vm.closeSettings()
            ForgePage.PREPARATION -> vm.cancelPreparation()
            ForgePage.WORKOUT -> confirmExit = true
            else -> Unit
        }
    }

    val sceneTint = when (scene) {
        LivingScene.MOVE -> palette.accent
        LivingScene.RECOVER -> Color(0xFF65B5AD)
        LivingScene.WARM_UP -> Color(0xFFE5B86C)
        LivingScene.COOL_DOWN -> Color(0xFF89A8DC)
        LivingScene.COMPLETE -> Color(0xFF85D6A4)
        LivingScene.PREPARE -> palette.accent
        LivingScene.HOME, LivingScene.SETTINGS -> palette.accent
    }
    val accent by animateColorAsState(
        targetValue = sceneTint,
        animationSpec = tween(if (state.reduceMotion) 0 else 750),
        label = "living-accent"
    )
    val backgroundGlow by animateColorAsState(
        targetValue = if (palette.isLight) accent.copy(alpha = 0.13f)
            else accent.copy(alpha = 0.21f),
        animationSpec = tween(if (state.reduceMotion) 0 else 750),
        label = "living-glow"
    )
    val colors = if (palette.isLight) lightColorScheme(
        primary = accent, onPrimary = palette.onAccent,
        background = palette.background, onBackground = palette.text,
        surface = palette.surface, onSurface = palette.text,
        outline = palette.outline
    ) else darkColorScheme(
        primary = accent, onPrimary = palette.onAccent,
        background = palette.background, onBackground = palette.text,
        surface = palette.surface, onSurface = palette.text,
        outline = palette.outline
    )
    val feedback = LocalHapticFeedback.current

    MaterialTheme(colorScheme = colors) {
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(backgroundGlow, palette.background, palette.background)
                )
            )
        ) {
            when (state.page) {
                ForgePage.HOME -> LivingHome(
                    state = state,
                    palette = palette,
                    accent = accent,
                    onStart = vm::prepare,
                    onSettings = vm::openSettings
                )
                ForgePage.SETTINGS -> LivingSettings(
                    state = state,
                    palette = palette,
                    accent = accent,
                    onClose = vm::closeSettings,
                    onSpeak = vm::setSpeak,
                    onBeep = vm::setBeep,
                    onMotion = vm::setReduceMotion,
                    onTheme = vm::setTheme
                )
                ForgePage.PREPARATION -> LivingPreparation(
                    index = state.preparationIndex,
                    palette = palette,
                    accent = accent,
                    reduceMotion = state.reduceMotion,
                    onConfirm = {
                        feedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        vm.confirmPreparation()
                    }
                )
                ForgePage.WORKOUT -> {
                    val plan = state.plan
                    val timer = state.timer
                    if (plan != null && timer != null) {
                        if (timer.completed) {
                            LivingComplete(
                                palette, accent,
                                isDemo = plan.id == LivingDemo.plan.id,
                                onExit = vm::leave
                            )
                        } else {
                            val current = plan.steps[timer.index]
                            LivingWorkout(
                                plan = plan, timer = timer, step = current, scene = scene,
                                palette = palette, accent = accent,
                                reduceMotion = state.reduceMotion,
                                onPause = vm::playPause,
                                onPrevious = vm::previous,
                                onNext = vm::skip,
                                onExit = { confirmExit = true }
                            )
                        }
                    }
                }
            }

            if (confirmExit) {
                AlertDialog(
                    onDismissRequest = { confirmExit = false },
                    title = { Text("Finish this session?") },
                    text = { Text("Your timer will stop. This session won't count as completed.") },
                    confirmButton = {
                        TextButton(onClick = {
                            confirmExit = false
                            vm.leave()
                        }) { Text("End session") }
                    },
                    dismissButton = {
                        TextButton(onClick = { confirmExit = false }) { Text("Keep going") }
                    }
                )
            }
        }
    }
}

@Composable
private fun LivingHeader(kicker: String, accent: Color, onBack: (() -> Unit)? = null) {
    Row(
        Modifier.fillMaxWidth().padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("FORGE / LIVING", color = accent, fontWeight = FontWeight.Black, fontSize = 17.sp,
            modifier = Modifier.weight(1f))
        if (onBack != null) TextButton(onClick = onBack) { Text("Back") }
    }
    Text(kicker, color = accent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun LivingHome(
    state: ForgeState,
    palette: ForgePalette,
    accent: Color,
    onStart: (Plan) -> Unit,
    onSettings: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LivingHeader("AN EXPERIMENT IN LESS INTERFACE", accent, onSettings)
        Spacer(Modifier.height(8.dp))
        Text("Move. Don't navigate.", fontSize = 38.sp, lineHeight = 42.sp,
            fontWeight = FontWeight.Black, color = palette.text)
        Text("One surface that changes with you.", color = palette.muted, fontSize = 16.sp)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.18f)),
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("EXPERIMENT 01", color = accent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("Experience the whole flow in 37 seconds.", color = palette.text,
                    fontSize = 23.sp, fontWeight = FontWeight.Bold, lineHeight = 28.sp)
                Button(onClick = { onStart(LivingDemo.plan) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Try 37-second demo")
                }
                Text("A preview, not a logged workout.", color = palette.muted, fontSize = 12.sp)
            }
        }
        Text("OR TRAIN FOR REAL", color = palette.muted, fontSize = 12.sp)
        Plans.all.forEach { plan ->
            Surface(
                modifier = Modifier.fillMaxWidth().clickable { onStart(plan) },
                color = palette.surface,
                shape = RoundedCornerShape(22.dp),
                tonalElevation = 0.dp
            ) {
                Row(
                    Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(plan.name, color = palette.text, fontWeight = FontWeight.Bold, fontSize = 21.sp)
                        Text("${plan.durationLabel}  •  ${plan.level}", color = palette.muted, fontSize = 13.sp)
                    }
                    Text("→", color = accent, fontSize = 25.sp)
                }
            }
        }
        Text("${state.sessions} sessions · ${state.minutes} minutes recorded in this lab",
            color = palette.muted, fontSize = 12.sp)
        TextButton(onClick = onSettings) { Text("Appearance & audio settings") }
    }
}

@Composable
private fun LivingSettings(
    state: ForgeState, palette: ForgePalette, accent: Color,
    onClose: () -> Unit, onSpeak: (Boolean) -> Unit, onBeep: (Boolean) -> Unit,
    onMotion: (Boolean) -> Unit, onTheme: (ForgeTheme) -> Unit
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        LivingHeader("TUNE YOUR EXPERIENCE", accent, onClose)
        Text("Settings", color = palette.text, fontSize = 34.sp, fontWeight = FontWeight.Black)
        PreferenceRow("Spoken coaching", "Exercise names, form tips and next-up cues",
            state.speak, onSpeak, palette)
        PreferenceRow("Countdown sounds", "Beep at the end of each interval",
            state.beep, onBeep, palette)
        PreferenceRow("Reduce motion", "Remove animated ambient breathing and transitions",
            state.reduceMotion, onMotion, palette)
        Text("COLOUR WORLDS", color = accent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        ForgeTheme.entries.forEach { theme ->
            val swatch = theme.palette()
            Surface(
                modifier = Modifier.fillMaxWidth().clickable { onTheme(theme) },
                color = swatch.surface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    if (theme == state.theme) 2.dp else 1.dp,
                    if (theme == state.theme) accent else palette.outline
                )
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = swatch.accent, shape = CircleShape,
                        modifier = Modifier.size(18.dp)
                    ) {}
                    Spacer(Modifier.width(13.dp))
                    Column(Modifier.weight(1f)) {
                        Text(theme.title, color = swatch.text, fontWeight = FontWeight.Bold)
                        Text(theme.description, color = swatch.text.copy(alpha = 0.78f), fontSize = 12.sp)
                    }
                    if (theme == state.theme) Text("✓", color = swatch.accent)
                }
            }
        }
        Text("All settings are local to Living Lab. Your regular Forge app is unchanged.",
            color = palette.muted, fontSize = 12.sp)
    }
}

@Composable
private fun PreferenceRow(
    name: String, detail: String, value: Boolean,
    onChange: (Boolean) -> Unit, palette: ForgePalette
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(name, color = palette.text, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(detail, color = palette.muted, fontSize = 12.sp)
        }
        Switch(checked = value, onCheckedChange = onChange)
    }
}

@Composable
private fun LivingPreparation(
    index: Int, palette: ForgePalette, accent: Color,
    reduceMotion: Boolean, onConfirm: () -> Unit
) {
    val step = WorkoutPreparation.steps[index]
    var accepted by remember(index) { mutableStateOf(false) }
    LaunchedEffect(accepted, index) {
        if (accepted) {
            delay(if (reduceMotion) 80 else 260)
            onConfirm()
        }
    }
    Box(
        Modifier.fillMaxSize().clickable(enabled = !accepted) { accepted = true }
            .padding(26.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Text("GET READY · ${index + 1} / 3", color = accent, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                repeat(3) { i ->
                    Box(
                        Modifier.width(54.dp).height(5.dp)
                            .background(if (i <= index) accent else palette.surface,
                                RoundedCornerShape(99.dp))
                    )
                }
            }
            Spacer(Modifier.height(30.dp))
            Text(step.symbol, fontSize = 74.sp)
            Text(step.title, color = palette.text, fontSize = 32.sp,
                fontWeight = FontWeight.Black, lineHeight = 38.sp, textAlign = TextAlign.Center)
            Text(step.description, color = palette.muted, fontSize = 17.sp,
                textAlign = TextAlign.Center, lineHeight = 25.sp)
            Spacer(Modifier.height(30.dp))
            Text(if (accepted) "✓  READY" else "TAP ANYWHERE WHEN READY",
                color = accent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
private fun LivingWorkout(
    plan: Plan, timer: TimerSnapshot, step: Step, scene: LivingScene,
    palette: ForgePalette, accent: Color, reduceMotion: Boolean,
    onPause: () -> Unit, onPrevious: () -> Unit, onNext: () -> Unit,
    onExit: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val progress = (1f - timer.remainingMs.toFloat() / (step.seconds * 1000f)).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress, animationSpec = tween(if (reduceMotion) 0 else 180),
        label = "dial-progress"
    )
    val ambient = rememberInfiniteTransition(label = "breathing")
    val breathe by ambient.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing-radius"
    )
    val dialScale by animateFloatAsState(
        targetValue = when (scene) {
            LivingScene.RECOVER -> if (reduceMotion) 1f else breathe
            LivingScene.MOVE -> 1.01f
            else -> 0.95f
        },
        animationSpec = tween(if (reduceMotion) 0 else 400),
        label = "dial-scale"
    )
    var dragX by remember { mutableFloatStateOf(0f) }
    val next = plan.steps.drop(timer.index + 1)
        .firstOrNull { it.phase == Phase.EXERCISE || it.phase == Phase.COOLDOWN }
    val seconds = ((timer.remainingMs + 999) / 1000).toInt()
    Column(
        Modifier.fillMaxSize()
            .pointerInput(timer.index) {
                detectHorizontalDragGestures(
                    onDragStart = { dragX = 0f },
                    onHorizontalDrag = { change, amount ->
                        change.consume()
                        dragX += amount
                    },
                    onDragEnd = {
                        if (dragX < -110f) onNext()
                        if (dragX > 110f) onPrevious()
                        dragX = 0f
                    },
                    onDragCancel = { dragX = 0f }
                )
            }
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(13.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("FORGE / LIVING", color = accent, fontSize = 16.sp, fontWeight = FontWeight.Black,
                modifier = Modifier.weight(1f))
            TextButton(onClick = onExit) { Text("End") }
        }
        Spacer(Modifier.height(12.dp))
        Text(LivingScenes.heading(scene), color = accent, fontSize = 13.sp,
            fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
        Text(step.name, color = palette.text, fontSize = 31.sp,
            fontWeight = FontWeight.Black, textAlign = TextAlign.Center, lineHeight = 37.sp)
        Text(step.guidance, color = palette.muted, fontSize = 15.sp,
            textAlign = TextAlign.Center, lineHeight = 21.sp)
        Spacer(Modifier.height(20.dp))
        Box(Modifier.size(250.dp).scale(dialScale).clickable {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onPause()
        }.semantics {
            contentDescription = if (timer.running) "Pause workout timer" else "Resume workout timer"
        }, contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize()) {
                val width = 13.dp.toPx()
                val inset = width / 2 + 9.dp.toPx()
                drawCircle(color = accent.copy(alpha = 0.11f))
                drawArc(
                    color = accent.copy(alpha = 0.22f), startAngle = -90f, sweepAngle = 360f,
                    useCenter = false, topLeft = Offset(inset, inset),
                    size = Size(size.width - inset * 2, size.height - inset * 2),
                    style = Stroke(width = width, cap = StrokeCap.Round)
                )
                drawArc(
                    color = accent, startAngle = -90f, sweepAngle = 360f * animatedProgress,
                    useCenter = false, topLeft = Offset(inset, inset),
                    size = Size(size.width - inset * 2, size.height - inset * 2),
                    style = Stroke(width = width, cap = StrokeCap.Round)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("%02d:%02d".format(seconds / 60, seconds % 60),
                    color = palette.text, fontSize = 45.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(6.dp))
                Text(if (timer.running) "TAP TO PAUSE" else "TAP TO RESUME",
                    color = accent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(10.dp))
        Text("STEP ${timer.index + 1} / ${plan.steps.size}  ·  ${plan.name.uppercase()}",
            color = palette.muted, fontSize = 11.sp)
        LinearProgressIndicator(
            progress = { (timer.index + progress) / plan.steps.size },
            modifier = Modifier.fillMaxWidth().height(5.dp),
            color = accent,
            trackColor = palette.surface
        )
        if (scene == LivingScene.RECOVER) {
            Text("Let your breath settle.", color = palette.text, fontSize = 18.sp,
                fontWeight = FontWeight.Medium)
        }
        if (next != null) {
            Text("COMING UP", color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(next.name, color = palette.text, fontSize = 17.sp)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onPrevious, modifier = Modifier.weight(1f)) { Text("← Previous") }
            OutlinedButton(onClick = onNext, modifier = Modifier.weight(1f)) { Text("Next →") }
        }
        Text("Swipe left or right to change steps. Tap the circle to pause.",
            color = palette.muted, fontSize = 12.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun LivingComplete(
    palette: ForgePalette, accent: Color, isDemo: Boolean, onExit: () -> Unit
) {
    Box(Modifier.fillMaxSize().padding(28.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(22.dp)) {
            Text("✦", color = accent, fontSize = 80.sp)
            Text("DONE", color = accent, fontSize = 13.sp, letterSpacing = 4.sp,
                fontWeight = FontWeight.Bold)
            Text(if (isDemo) "You felt the flow." else "You finished strong.",
                color = palette.text, fontSize = 32.sp, fontWeight = FontWeight.Black,
                lineHeight = 37.sp, textAlign = TextAlign.Center)
            Text(
                if (isDemo) "Preview complete. Your workout totals were not changed."
                else "Recover, breathe, and drink some water.",
                color = palette.muted, fontSize = 16.sp, textAlign = TextAlign.Center
            )
            Button(onClick = onExit, modifier = Modifier.fillMaxWidth()) { Text("Back to Forge") }
        }
    }
}
