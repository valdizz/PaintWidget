package com.valdizz.paintwidget

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.StateSet
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.paint_widget.view.*

/**
 * Custom view based on ConstaintLayout with indicator [SeekBar] and colorpicker [RadioGroup].
 * Widget has attributes:
 *  @property seekbarMaxWidth sets the seek bar maximum value
 *  @property defaultColorPosition sets the default selected color item
 *  @property firstItemColor sets the color for the first item
 *
 * @author Vlad Kornev
 */
class PaintWidget @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    var seekbarMaxWidth = 100
        set(value) {
            field = value
            initMaxWidth(seekbarMaxWidth)
    }

    var defaultColorPosition = 0
        set(value) {
            field = value
            initDefaultColorPosition(defaultColorPosition)
        }

    var firstItemColor = 0
        set(value) {
            field = value
            initFirstItemColor(firstItemColor)
        }

    private var paintWidgetListener: PaintWidgetListener? = null

    init {
        View.inflate(context, R.layout.paint_widget, this)
        initAttributes(attrs)
        initView()
    }

    private fun initAttributes(attrs: AttributeSet?) {
        val typedArray: TypedArray= context.obtainStyledAttributes(attrs, R.styleable.PaintWidget)
        seekbarMaxWidth = typedArray.getResourceId(R.styleable.PaintWidget_maxWidth, 100)
        defaultColorPosition = typedArray.getResourceId(R.styleable.PaintWidget_defaultColorPosition, 0)
        firstItemColor = typedArray.getResourceId(R.styleable.PaintWidget_firstItemColor, 0)
        typedArray.recycle()
    }

    private fun initView() {
        initRadioButton(radio_btn_color2, Color.RED.hex)
        initRadioButton(radio_btn_color3, Color.GREEN.hex)
        initRadioButton(radio_btn_color4, Color.BLUE.hex)

        initMaxWidth(seekbarMaxWidth)
        initDefaultColorPosition(defaultColorPosition)
        initFirstItemColor(firstItemColor)

        seekbar_width.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                paintWidgetListener?.onChanged(seekBar?.progress.toString(), getCheckedRadioButtonTag(radio_btn_colors))
                tv_width_value.text = seekBar?.progress.toString()
            }
        })

        radio_btn_color1.setOnClickListener {
            paintWidgetListener?.onChanged(seekbar_width.progress.toString(), it.tag.toString())
        }
        radio_btn_color2.setOnClickListener {
            paintWidgetListener?.onChanged(seekbar_width.progress.toString(), it.tag.toString())
        }
        radio_btn_color3.setOnClickListener {
            paintWidgetListener?.onChanged(seekbar_width.progress.toString(), it.tag.toString())
        }
        radio_btn_color4.setOnClickListener {
            paintWidgetListener?.onChanged(seekbar_width.progress.toString(), it.tag.toString())
        }
    }

    fun setPaintWidgetListener(context: Context) {
        if (context is PaintWidgetListener) {
            paintWidgetListener = context
        }
    }

    private fun initMaxWidth(width: Int) {
        seekbar_width.max = width
    }

    private fun initDefaultColorPosition(position: Int) {
        when (position) {
            0 -> radio_btn_color1.isChecked = true
            1 -> radio_btn_color2.isChecked = true
            2 -> radio_btn_color3.isChecked = true
            3 -> radio_btn_color4.isChecked = true
            else -> throw IllegalArgumentException("The position must be in the range 0..3")
        }
    }

    private fun initFirstItemColor(color: Int) {
        if (color >= 0 && color < Color.values().size) {
            initRadioButton(radio_btn_color1, Color.values()[color].hex)
        }
        else {
            throw IllegalArgumentException("Invalid color value")
        }
    }

    private fun createRadioButtonDrawable(color: String): StateListDrawable {
        val stateListDrawable = StateListDrawable()
        val rbCheckedDrawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setSize(25.toPx(), 25.toPx())
            setColor(android.graphics.Color.parseColor(color))
            setStroke(3.toPx(),  android.graphics.Color.parseColor("#808080"))
        }
        val rbNormalDrawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setSize(20.toPx(), 20.toPx())
            setColor(android.graphics.Color.parseColor(color))
        }
        stateListDrawable.addState(intArrayOf(android.R.attr.state_checked), rbCheckedDrawable)
        stateListDrawable.addState(StateSet.WILD_CARD, rbNormalDrawable)
        return stateListDrawable
    }

    private fun initRadioButton(rb: RadioButton, color: String) {
        rb.buttonDrawable = createRadioButtonDrawable(color)
        rb.tag = color
    }

    private fun getCheckedRadioButtonTag(rg: RadioGroup): String {
        return rg.findViewById<RadioButton>(rg.checkedRadioButtonId).tag.toString()
    }

    fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(SUPER_STATE, super.onSaveInstanceState())
        bundle.putInt(SEEKBAR_STATE, seekbar_width.progress)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            val seekbarProgress = state.getInt(SEEKBAR_STATE)
            tv_width_value.text = seekbarProgress.toString()
            val superState:Parcelable? = state.getParcelable(SUPER_STATE)
            super.onRestoreInstanceState(superState)
            return
        }
        super.onRestoreInstanceState(state)
    }

    interface PaintWidgetListener {
        fun onChanged(width: String, color: String)
    }

    companion object {
        private const val SUPER_STATE = "superState"
        private const val SEEKBAR_STATE = "seekbarProgressState"
    }
}