package com.mehrpol

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews

class BestIpWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val text = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_TEXT, "No saved IP") ?: "No saved IP"
        appWidgetIds.forEach { id -> updateWidget(context, appWidgetManager, id, text) }
    }

    companion object {
        private const val PREFS = "mehrpol_widget"
        private const val KEY_TEXT = "best_ip_text"

        fun update(context: Context, text: String) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_TEXT, text)
                .apply()
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, BestIpWidgetProvider::class.java))
            ids.forEach { id -> updateWidget(context, manager, id, text) }
        }

        private fun updateWidget(context: Context, manager: AppWidgetManager, id: Int, text: String) {
            val intent = Intent(context, MainActivity::class.java)
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, flags)
            val views = RemoteViews(context.packageName, R.layout.best_ip_widget).apply {
                setTextViewText(R.id.widget_best_ip, text)
                setOnClickPendingIntent(R.id.widget_root, pendingIntent)
            }
            manager.updateAppWidget(id, views)
        }
    }
}
