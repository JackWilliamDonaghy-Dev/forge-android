package dev.jackdonaghy.forge

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ForgeScreen() }
    }
}

@Composable
private fun ThemeSelector(selected: ForgeTheme, palette: ForgePalette, onChange: (ForgeTheme) -> Unit) {
    Text("APPEARANCE", color = palette.muted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        ForgeTheme.entries.chunked(2).forEach { themes ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                themes.forEach { theme ->
                    val swatch = theme.palette()
                    val isSelected = selected == theme
                    Surface(
                        modifier = Modifier.weight(1f).clickable { onChange(theme) },
                        shape = RoundedCornerShape(14.dp),
                        color = swatch.surface,
                        border = BorderStroke(
                            if (isSelected) 2.dp else 1.dp,
                            if (isSelected) palette.accent else palette.outline
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(color = swatch.accent, shape = RoundedCornerShape(50), modifier = Modifier.size(14.dp)) {}
                            Text(theme.title, color = swatch.text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
                if (themes.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ForgeScreen(vm: ForgeViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    val palette = state.theme.palette()
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val coach = remember(context) { VoiceCoach(context) }

    DisposableEffect(coach) { onDispose { coach.close() } }
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) vm.pauseOnBackground()
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
    DisposableEffect(state.plan) {
        val activity = context as? Activity
        if (state.plan != null) activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose { activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
    }
    LaunchedEffect(vm, coach) {
        vm.cues.collect { cue ->
            coach.play(cue, vm.state.value.speak, vm.state.value.beep)
        }
    }

    val colors = if (palette.isLight) {
        lightColorScheme(
            primary = palette.accent, onPrimary = palette.onAccent,
            background = palette.background, onBackground = palette.text,
            surface = palette.surface, onSurface = palette.text,
            outline = palette.outline
        )
    } else {
        darkColorScheme(
            primary = palette.accent, onPrimary = palette.onAccent,
            background = palette.background, onBackground = palette.text,
            surface = palette.surface, onSurface = palette.text,
            outline = palette.outline
        )
    }

    MaterialTheme(colorScheme = colors) {
        Scaffold(containerColor = palette.background) { outer ->
            Column(
                Modifier.fillMaxSize().padding(outer).verticalScroll(rememberScrollState())
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text("FORGE", color = palette.accent, fontSize = 24.sp, fontWeight = FontWeight.Black)
                Text("BODYWEIGHT TRAINING", fontSize = 12.sp, color = palette.muted)
                HorizontalDivider(color = palette.outline)
                if (state.plan == null) {
                    Text("Choose your session.", fontSize = 27.sp, fontWeight = FontWeight.Bold)
                    Text("${state.sessions} completed workouts  ·  ${state.minutes} minutes trained")
                    Plans.all.forEach { plan ->
                        Card(colors = CardDefaults.cardColors(containerColor = palette.surface)) {
                            Column(
                                Modifier.fillMaxWidth().padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(9.dp)
                            ) {
                                Text(plan.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                Text("${plan.durationLabel}  ·  ${plan.level}", color = palette.accent)
                                Button(onClick = { vm.start(plan) }, modifier = Modifier.fillMaxWidth()) {
                                    Text("Start ${plan.name}")
                                }
                            }
                        }
                    }
                    Card(colors = CardDefaults.cardColors(containerColor = palette.surface)) {
                        Column(Modifier.fillMaxWidth().padding(15.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Spoken coaching", Modifier.weight(1f))
                                Switch(checked = state.speak, onCheckedChange = vm::setSpeak)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Beep countdown", Modifier.weight(1f))
                                Switch(checked = state.beep, onCheckedChange = vm::setBeep)
                            }
                        }
                    }
                    ThemeSelector(state.theme, palette, vm::setTheme)
                } else {
                    val plan = state.plan!!
                    val timer = state.timer!!
                    if (timer.completed) {
                        Text("Session complete!", fontSize = 29.sp, fontWeight = FontWeight.Bold)
                        Text("Great work. Recover and hydrate.")
                        Button(onClick = vm::leave) { Text("Back to workouts") }
                    } else {
                        val step = plan.steps[timer.index]
                        Text("${plan.name} · ${step.phase.name.lowercase()}", color = palette.accent)
                        Text(step.name, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        Text(step.guidance, color = palette.muted)
                        Spacer(Modifier.height(15.dp))
                        Box(Modifier.fillMaxWidth().height(185.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = { (1f - timer.remainingMs.toFloat() / (step.seconds * 1000f)).coerceIn(0f, 1f) },
                                modifier = Modifier.size(180.dp),
                                strokeWidth = 10.dp,
                                trackColor = palette.surface
                            )
                            val seconds = (timer.remainingMs + 999L) / 1000L
                            Text("%02d:%02d".format(seconds / 60, seconds % 60),
                                fontSize = 40.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("Step ${timer.index + 1} of ${plan.steps.size}")
                        Button(onClick = vm::playPause, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                            Text(if (timer.running) "Pause" else "Resume")
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(onClick = vm::previous, modifier = Modifier.weight(1f)) { Text("Previous") }
                            OutlinedButton(onClick = vm::skip, modifier = Modifier.weight(1f)) { Text("Skip") }
                        }
                        plan.steps.getOrNull(timer.index + 1)?.let { next ->
                            Text("UP NEXT", color = palette.accent, fontSize = 12.sp)
                            Text(next.name)
                        }
                        TextButton(onClick = vm::leave, modifier = Modifier.fillMaxWidth()) { Text("End workout") }
                    }
                }
                Text("Exercise in a cool space; hydrate and stop if dizzy or overheated.",
                    color = palette.muted, fontSize = 12.sp)
            }
        }
    }
}
