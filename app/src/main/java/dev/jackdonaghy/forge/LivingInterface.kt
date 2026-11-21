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
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.math.pow

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

    // The visual score responds to the planned exercise and elapsed time,
    // never to unmeasured physical exertion. No sensors are involved.
    val targetIntensity = if (state.page == ForgePage.WORKOUT && state.plan != null && state.timer != null) {
        VisualIntensity.score(state.plan!!, state.timer!!).toFloat()
    } else 12f
    val visualIntensity by animateFloatAsState(
        targetValue = targetIntensity,
        animationSpec = tween(if (state.reduceMotion) 0 else 700),
        label = "whole-screen-intensity"
    )
    val power = (visualIntensity / 100f).coerceIn(0f, 1f)
    val sceneHue = when (scene) {
        LivingScene.MOVE -> palette.accent
        LivingScene.RECOVER -> Color(0xFF74B5AC)
        LivingScene.WARM_UP -> Color(0xFF97A9B2)
        LivingScene.COOL_DOWN -> Color(0xFF89A6C1)
        LivingScene.COMPLETE -> Color(0xFF85D6A4)
        LivingScene.PREPARE, LivingScene.HOME, LivingScene.SETTINGS -> palette.accent
    }
    val contrast = power.pow(1.35f)
    val targetAccent = if (scene == LivingScene.HOME || scene == LivingScene.SETTINGS) {
        palette.accent
    } else {
        lerp(
            lerp(palette.background, sceneHue, if (palette.isLight) 0.45f else 0.28f),
            sceneHue,
            contrast
        )
    }
    val accent by animateColorAsState(
        targetValue = targetAccent,
        animationSpec = tween(if (state.reduceMotion) 0 else 650),
        label = "living-accent"
    )
    val backgroundGlow by animateColorAsState(
        targetValue = lerp(
            palette.background,
            sceneHue,
            if (palette.isLight) (0.025f + power * 0.15f) else (0.02f + power * 0.34f)
        ),
        animationSpec = tween(if (state.reduceMotion) 0 else 650),
        label = "living-glow"
    )
    val colors = if (palette.isLight) lightColorScheme(
        primary = palette.accent, onPrimary = palette.onAccent,
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
                                intensity = visualIntensity,
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
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("FORGE / LIVING", color = accent, fontSize = 16.sp,
                fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
            TextButton(onClick = onSettings) { Text("Settings") }
        }
        Spacer(Modifier.height(40.dp))
        Text(
            "Choose your movement.",
            color = palette.text, fontSize = 34.sp, lineHeight = 39.sp,
            fontWeight = FontWeight.Black
        )
        Spacer(Modifier.height(15.dp))
        Plans.all.forEach { plan ->
            Surface(
                modifier = Modifier.fillMaxWidth().clickable { onStart(plan) },
                color = palette.surface.copy(alpha = 0.75f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(plan.name, color = palette.text,
                            fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                        Text(plan.durationLabel, color = palette.muted, fontSize = 12.sp)
                    }
                    Text("›", color = accent, fontSize = 27.sp)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        TextButton(
            onClick = { onStart(LivingDemo.plan) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Explore the 37-second demo", color = palette.accent)
        }
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
    palette: ForgePalette, accent: Color, intensity: Float, reduceMotion: Boolean,
    onPause: () -> Unit, onPrevious: () -> Unit, onNext: () -> Unit,
    onExit: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val progress = (1f - timer.remainingMs.toFloat() / (step.seconds * 1000f)).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(if (reduceMotion) 0 else 170),
        label = "dial-progress"
    )
    val ambient = rememberInfiniteTransition(label = "recovery-breathing")
    val breath by ambient.animateFloat(
        initialValue = 0.975f,
        targetValue = 1.035f,
        animationSpec = infiniteRepeatable(
            animation = tween(2300),
            repeatMode = RepeatMode.Reverse
        ),
        label = "recovery-breathing-scale"
    )
    val dialScale = if (scene == LivingScene.RECOVER && !reduceMotion) breath else 1f
    val seconds = ((timer.remainingMs + 999) / 1000).toInt()
    var dragX by remember { mutableFloatStateOf(0f) }

    // Fixed zones prevent a long or short form tip from shifting the clock.
    // A narrow/short screen uses the same geometry, only with a smaller dial.
    BoxWithConstraints(
        Modifier.fillMaxSize()
            .pointerInput(timer.index) {
                detectHorizontalDragGestures(
                    onDragStart = { dragX = 0f },
                    onHorizontalDrag = { change, delta ->
                        change.consume()
                        dragX += delta
                    },
                    onDragEnd = {
                        if (dragX < -110f) onNext()
                        if (dragX > 110f) onPrevious()
                        dragX = 0f
                    },
                    onDragCancel = { dragX = 0f }
                )
            }
            .semantics {
                customActions = listOf(
                    CustomAccessibilityAction("Next exercise") { onNext(); true },
                    CustomAccessibilityAction("Previous exercise") { onPrevious(); true }
                )
            }
    ) {
        val compact = maxHeight < 760.dp
        val tiny = maxHeight < 550.dp
        val dialSize = when {
            tiny -> 150.dp
            maxHeight < 660.dp -> 184.dp
            compact -> 212.dp
            else -> 248.dp
        }
        // The top region is always the same height, even if its text wraps.
        Row(
            Modifier.align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(start = 22.dp, end = 12.dp, top = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "FORGE", color = palette.muted, fontSize = 13.sp,
                fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f)
            )
            TextButton(
                onClick = onExit,
                modifier = Modifier.semantics { contentDescription = "End workout" }
            ) { Text("×", color = palette.muted, fontSize = 23.sp) }
        }
        Column(
            Modifier.align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(
                    start = 20.dp, end = 20.dp,
                    top = if (compact) 56.dp else 82.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                LivingScenes.heading(scene),
                color = accent, fontSize = 12.sp,
                fontWeight = FontWeight.Bold, letterSpacing = 2.5.sp
            )
            Box(
                modifier = Modifier.fillMaxWidth().height(if (compact) 58.dp else 78.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    step.name,
                    color = palette.text,
                    fontSize = if (compact) 25.sp else 30.sp,
                    lineHeight = if (compact) 29.sp else 35.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
            Box(
                modifier = Modifier.fillMaxWidth().height(
                    if (tiny) 26.dp else if (compact) 38.dp else 43.dp
                ),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    step.guidance,
                    color = palette.muted,
                    fontSize = if (compact) 12.sp else 14.sp,
                    lineHeight = if (compact) 16.sp else 20.sp,
                    maxLines = if (tiny) 1 else 2,
                    textAlign = TextAlign.Center,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
        }

        // Only the ring animates its scale; its geometric center never moves.
        Box(
            Modifier.align(Alignment.Center)
                .size(dialSize)
                .scale(dialScale)
                .clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onPause()
                }
                .semantics {
                    contentDescription =
                        if (timer.running) "Pause workout timer" else "Resume workout timer"
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(Modifier.fillMaxSize()) {
                val stroke = 12.dp.toPx()
                val pad = stroke / 2f + 8.dp.toPx()
                val outer = Size(size.width - 2f * pad, size.height - 2f * pad)
                val intensityDepth = (intensity / 100f).coerceIn(0f, 1f)
                drawCircle(color = accent.copy(alpha = 0.035f + intensityDepth * 0.14f))
                drawArc(
                    color = accent.copy(alpha = 0.16f + intensityDepth * 0.12f),
                    startAngle = -90f, sweepAngle = 360f,
                    useCenter = false,
                    topLeft = Offset(pad, pad), size = outer,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
                drawArc(
                    color = accent,
                    startAngle = -90f, sweepAngle = 360f * animatedProgress,
                    useCenter = false,
                    topLeft = Offset(pad, pad), size = outer,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "%02d:%02d".format(seconds / 60, seconds % 60),
                    color = palette.text,
                    fontSize = if (tiny) 32.sp else 42.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    if (timer.running) "PAUSE" else "RESUME",
                    color = palette.muted,
                    fontSize = 11.sp, fontWeight = FontWeight.Medium
                )
            }
        }

        // A single tiny status: no navigation bars, duplicate advice or buttons.
        Row(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                .padding(
                    start = 26.dp, end = 26.dp,
                    bottom = if (compact) 18.dp else 30.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "INTENSITY  ${intensity.roundToInt()}",
                color = palette.muted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Text("SWIPE  ↔", color = palette.muted, fontSize = 11.sp)
        }
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
