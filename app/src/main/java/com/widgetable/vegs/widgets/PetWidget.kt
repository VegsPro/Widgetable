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

class PetWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = PetWidget()
}

class PetWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                PetWidgetContent()
            }
        }
    }
}

@Composable
private fun PetWidgetContent() {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Color(0xFFFFF5F9)))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🐱",
            style = TextStyle(fontSize = 36.sp)
        )
        Spacer(GlanceModifier.height(4.dp))
        Text(
            text = "Пушистик",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = ColorProvider(Color(0xFFE05A9A))
            )
        )
        Text(
            text = "Счастлив ❤️",
            style = TextStyle(
                fontSize = 12.sp,
                color = ColorProvider(Color(0xFF666666))
            )
        )
    }
}
