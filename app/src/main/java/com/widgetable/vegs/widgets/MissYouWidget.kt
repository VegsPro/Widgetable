package com.widgetable.vegs.widgets

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

class MissYouWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MissYouWidget()
}

class MissYouWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                MissYouWidgetContent()
            }
        }
    }
}

@Composable
private fun MissYouWidgetContent() {
    Row(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Color(0xFFFFF0F5)))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "💕", style = TextStyle(fontSize = 28.sp))
        Spacer(GlanceModifier.width(8.dp))
        Column {
            Text(
                text = "Скучаю",
                style = TextStyle(fontSize = 11.sp, color = ColorProvider(Color(0xFF888888)))
            )
            Text(
                text = "0 раз",
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ColorProvider(Color(0xFFE05A9A)))
            )
        }
    }
}
