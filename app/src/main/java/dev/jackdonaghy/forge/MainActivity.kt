package dev.jackdonaghy.forge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

/** Lab presentation only; v1 workout engine is unchanged. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { LivingInterfaceRoot() }
    }
}
