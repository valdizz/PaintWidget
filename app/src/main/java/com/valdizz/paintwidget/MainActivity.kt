package com.valdizz.paintwidget

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Activity contains [PaintWidget] and [Button] that shows or hides [PaintWidget].
 * Activity listens to width and color changes and shows Toast messages.
 *
 * @author Vlad Kornev
 */
class MainActivity : AppCompatActivity(), PaintWidget.PaintWidgetListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        paintwidget.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGrey))
        paintwidget.seekbarMaxWidth = 50
        paintwidget.defaultColorPosition = 1
        paintwidget.firstItemColor = 0
        paintwidget.setPaintWidgetListener(this)
        paintwidget.isVisible = savedInstanceState?.getBoolean(PAINTWIDGET_STATE) ?: true
        btn_show_hide_widget.setOnClickListener { paintwidget.isVisible = !paintwidget.isVisible }
    }

    override fun onChanged(width: String, color: String) {
        Toast.makeText(this, "PaintWidget: width = $width, color = $color", Toast.LENGTH_SHORT).show()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean(PAINTWIDGET_STATE, paintwidget.isVisible)
    }

    companion object {
        private const val PAINTWIDGET_STATE = "paintWidgetState"
    }
}
