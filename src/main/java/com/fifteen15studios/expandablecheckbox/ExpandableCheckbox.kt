package com.fifteen15studios.expandablecheckbox

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.ColorMatrixColorFilter
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.ContextCompat
import androidx.core.widget.CompoundButtonCompat
import java.lang.Exception
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout

class ExpandableCheckbox : ConstraintLayout {

    private val DIRECTION_DOWN = 0
    private val DIRECTION_UP = 1

    private var expanded = false

    private var checkboxLayout : LinearLayout? = null
    private var checkbox : AppCompatCheckBox? = null
    private var expanderLayout : LinearLayout? = null
    private var expander : ImageView? = null

    private var text = ""
    var textColor = Color.BLACK
    set(value) {
        try {
            checkbox?.setTextColor(value)
        }
        catch(e : Exception)
        {
            e.printStackTrace()
        }

        field = value
    }
    var childTextColor = Color.BLACK
    set(value) {
        try {
            if (getChildCheckboxCount() > 0) {
                for (i in 0 until getChildCheckboxCount()) {
                    getChildCheckboxAt(i)?.setTextColor(value)
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }

        field = value
    }

    var checkboxColor : Int? = null
    var childCheckboxColor : Int? = null

    var expanderColor : Int? = null
    set(value) {
        field = value

        try {
            changeExpanderColor(value!!)
        }
        catch (e : Exception)
        {e.printStackTrace()}
    }

    private var children = LinearLayout(context)

    private var onCheckedChangeListener : OnCheckedChangeListener? = null

    constructor(context: Context) : super(context) {
        init(null, R.styleable.CustomTheme_textStyle)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, R.styleable.CustomTheme_textStyle)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }
    /**
     * Returns whether or not the view is expanded
     */
    fun isExpanded(): Boolean {
        return expanded
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {

        val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.expandable_checkbox, this, true)

        checkboxLayout = view.findViewById(R.id.checkboxLayout)
        checkbox = view.findViewById(R.id.checkbox)
        expanderLayout = view.findViewById(R.id.expanderLayout)
        expander = view.findViewById(R.id.expander)

        children = view.findViewById(R.id.childrenLayout)

        val a = context.obtainStyledAttributes(
                attrs, R.styleable.ExpandableCheckbox, defStyle, R.style.Widget_ExpandableCheckbox)

        // Get color values
        val outValue = TypedValue()
        val theme = context.theme
        var wasResolved = theme.resolveAttribute(
                android.R.attr.textColorPrimary, outValue, true)

        // Default to theme's text color
        val defaultTextColor = if (wasResolved) {
            ContextCompat.getColor(context, outValue.resourceId)
        } else {
            // fallback color handling
            Color.BLACK
        }

        wasResolved = if(Build.VERSION.SDK_INT >= 21)
            theme.resolveAttribute(
                android.R.attr.colorPrimary, outValue, true)
        else false

        val colorPrimary = if (wasResolved) {
            ContextCompat.getColor(context, outValue.resourceId)
        } else {
            // fallback color handling
            defaultTextColor
        }

        // If checkbox text set in XML, get it
        if (a.hasValue(R.styleable.ExpandableCheckbox_text) &&
            a.getString(R.styleable.ExpandableCheckbox_text) != null) {
            text = a.getString(R.styleable.ExpandableCheckbox_text)!!
        }

        textColor =
            a.getColor(R.styleable.ExpandableCheckbox_textColor, defaultTextColor)

        childTextColor =
            a.getColor(R.styleable.ExpandableCheckbox_childTextColor, defaultTextColor)

        expanderColor =
            a.getColor(R.styleable.ExpandableCheckbox_expanderColor, defaultTextColor)

        checkboxColor =
                a.getColor(R.styleable.ExpandableCheckbox_checkboxColor, colorPrimary)

        childCheckboxColor =
                a.getColor(R.styleable.ExpandableCheckbox_childCheckboxColor, colorPrimary)

        checkbox?.isChecked = a.getBoolean(R.styleable.ExpandableCheckbox_checked, false)

        expanded = a.getBoolean(R.styleable.ExpandableCheckbox_expanded, false)

        a.recycle()

        checkbox?.text = text
        checkbox?.setTextColor(textColor)

        // Set color of expander box
        changeExpanderColor(expanderColor!!)

        // Expand/collapse on click
        expanderLayout?.setOnClickListener{
            expand(!isExpanded())
        }

        // Run listener when checked changes
        checkbox?.setOnCheckedChangeListener{buttonView, isChecked ->
            onCheckedChangeListener?.onCheckedChanged(isChecked)
        }

        // Check/uncheck all children when clicked
        checkbox?.setOnClickListener { setAllChecked(isChecked()) }

        // Set checkbox to appropriate drawable
        setCheckboxView()

        //Expand on long click
        checkboxLayout?.setOnLongClickListener{
            expand(!isExpanded())

            //return
            false
        }

        // Expand/Collapse initially. Mostly for use in XML
        if(expanded)
            expand(expanded, false)
        else
            expand(expanded, false)
    }

    /**
     * Expand or collapse the view of child checkboxes
     *
     * @param expand : If true expands the view, if false collapses the view
     * @param animate : If true will animate the expansion, if false will simply show/hide
     */
    private fun expand(expand: Boolean, animate: Boolean)
    {
        if(animate)
            toggleContents(children, expand)
        else if(expand)
            children.visibility = VISIBLE
        else
            children.visibility = GONE

        this.expanded = expand

        if(expand) {
            if (Build.VERSION.SDK_INT >= 21)
                expander?.setImageDrawable(context.getDrawable(R.drawable.expander_expanded))
            else
                expander?.setImageDrawable(resources.getDrawable(R.drawable.expander_expanded))
        }
        else {
            if (Build.VERSION.SDK_INT >= 21)
                expander?.setImageDrawable(context.getDrawable(R.drawable.expander_collapsed))
            else
                expander?.setImageDrawable(resources.getDrawable(R.drawable.expander_collapsed))
        }

        if(expanderColor != null)
            changeExpanderColor(expanderColor!!)
    }

    /**
     * Expand or collapse the view of child checkboxes
     *
     * @param expand : If true expands the view, if false collapses the view
     */
    fun expand(expand: Boolean) {
        expand(expand, true)
    }

    /**
     * Show or hide child view using a slide animation
     *
     * @param v : The view which should be animated
     */
    private fun toggleContents(v: View, show: Boolean) {
        if (!show) {
            // If already hidden, do nothing
            if(v.visibility == VISIBLE)
                slide(v.context, v, DIRECTION_UP)
        } else {
            // If already showing, do nothing
            if(v.visibility != VISIBLE)
                slide(v.context, v, DIRECTION_DOWN)
        }
    }

    interface OnCheckedChangeListener{
        /**
         * Interface that runs when the main checkbox is checked or unchecked
         *
         * @param checked : This will be true if the checkbox is checked after the change,
         *      or false if the box is not checked after the change
         */
        fun onCheckedChanged(checked: Boolean)
    }

    /**
     * Add a child checkbox. These will be hidden while the main view is collapsed and visible
     *  when the view is expanded
     *
     * @param text : The text next to the new checkbox
     * @param onCheckedChangeListener : What to do when the checkbox check changes
     */
    fun addChild(text : String, onCheckedChangeListener: CompoundButton.OnCheckedChangeListener?)
    {
        // Set text on new checkbox
        val newCheckbox = AppCompatCheckBox(context)
        newCheckbox.text = text
        newCheckbox.setTextColor(childTextColor)

        //Change Color
        val states = arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf())
        val colors = intArrayOf(childCheckboxColor!!, childCheckboxColor!!)
        CompoundButtonCompat.setButtonTintList(newCheckbox, ColorStateList(states, colors))

        // Check/Uncheck the parent checkbox when necessary
        newCheckbox.setOnCheckedChangeListener{ button, checked ->
            setCheckboxView()
            onCheckedChangeListener?.onCheckedChanged(button, checked)
        }

        children.addView(newCheckbox)
    }

    /**
     * Sets actions to perform when a check is changed on a child checkbox
     *
     * @param index: Which checkbox to add the listener to
     * @param onCheckedChangeListener : Actions to perform when checkbox clicked
     */
    fun setChildOnCheckedChangeListener(index: Int, onCheckedChangeListener: CompoundButton.OnCheckedChangeListener?)
    {
        getChildCheckboxAt(index)?.setOnCheckedChangeListener { button, checked ->
            setCheckboxView()
            onCheckedChangeListener?.onCheckedChanged(button, checked)
        }
    }

    /**
     * Sets the drawable of the inside of the checkbox based on the state of child  checkboxes
     */
    private fun setCheckboxView()
    {
        setChecked(allChildrenChecked())

        checkbox?.buttonDrawable = if(allChildrenChecked() || noChildrenChecked()) {
            resources.getDrawable(R.drawable.checkbox_fill)
        }
        else {
            resources.getDrawable(R.drawable.checkbox_full)
        }

        //Change Color
        CompoundButtonCompat.setButtonTintList(checkbox!!,
                ColorStateList(
                        arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
                        intArrayOf(checkboxColor!!, checkboxColor!!)))
    }

    /**
     * Sets the checked state of a child checkbox at a given index. If no checkbox is found at that
     *  index, or the index is out of range, no changes are made.
     *
     * @param index : The index of the child
     * @param checked : If true the checkbox will be checked, if false the checkbox will be unchecked
     */
    fun setChildChecked(index: Int, checked : Boolean)
    {
        try {
            (children.getChildAt(index) as CheckBox).isChecked = checked
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /**
     * Sets the checked state of a child checkbox with the given text. If no checkbox is found
     *  with that text, no changes are made.
     *
     * @param text : The text displayed on the child
     * @param checked : If true the checkbox will be checked, if false the checkbox will be unchecked
     */
    fun setChildChecked(text: String, checked : Boolean)
    {
        for(i in 0 until children.childCount)
            if((children.getChildAt(i) as CheckBox).text == text)
                (children.getChildAt(i) as CheckBox).isChecked = checked

        setCheckboxView()
    }

    /**
     * @return : true if all children are checked, otherwise false
     */
    private fun allChildrenChecked() : Boolean
    {
        var count = 0

        for(i in 0 until getChildCheckboxCount())
        {
            val view = children.getChildAt(i)
            if(view is CheckBox && view.isChecked)
                count++
        }

        return if(getChildCheckboxCount() > 0 )
            count == getChildCheckboxCount()
        else
            isChecked()
    }

    /**
     * @return : true if no children are checked, otherwise false
     */
    private fun noChildrenChecked() : Boolean
    {
        var allChecked = true
        for(i in 0 until children.childCount)
        {
            val view = children.getChildAt(i)
            if(view is CheckBox && view.isChecked)
                allChecked = false
        }

        return allChecked
    }

    /**
     * @return : How many child checkboxes have been added
     */
    fun getChildCheckboxCount() : Int
    {
        var count = 0

        for(i in 0 until children.childCount)
            if(children.getChildAt(i) is CheckBox)
                count++

        return count
    }

    /**
     * Gets a checkbox at a specific index
     *
     * @param : The index of the requested checkbox
     *
     * @return : The child checkbox at the desired index specific index
     */
    private fun getChildCheckboxAt(index: Int): CheckBox? {
        var count = 0
        for(i in 0 until children.childCount)
            if(children.getChildAt(i) is CheckBox)
            {
                count++
                if(count == index)
                    return children.getChildAt(i) as CheckBox
            }

        return null
    }

    /**
     * Starts a slide animation
     *
     * @param ctx - Context
     * @param v- View
     * @param direction - either [DIRECTION_UP] or [DIRECTION_DOWN].
     *      DIRECTION_UP will slide up to hide
     *      DIRECTION_DOWN will slide down to show
     */
    private fun slide(ctx: Context, v: View, direction: Int) {
        var a: Animation? = null

        if (direction == DIRECTION_DOWN)
            a = AnimationUtils.loadAnimation(ctx, R.anim.slide_down)
        else if (direction == DIRECTION_UP)
            a = AnimationUtils.loadAnimation(ctx, R.anim.slide_up)

        if (a != null) {
            a.reset()
            a.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    if (direction == DIRECTION_DOWN)
                        v.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(animation: Animation) {
                    if (direction == DIRECTION_UP)
                        v.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation) {
                }
            })

            v.startAnimation(a)
        }
    }

    /**
     * Set the text of the checkbox
     *
     * @param text : The text to appear next to the checkbox
     */
    fun setText(text : String)
    {
        checkbox?.text = text
    }

    /**
     * Set the state of the checkbox
     *
     * @param checked : If true checkbox will be checked, if false checkbox will be unchecked
     */
    fun setChecked(checked: Boolean)
    {
        //Only do this is check really changed
        if(checkbox?.isChecked != checked)
        {
            checkbox?.isChecked = checked
            onCheckedChangeListener?.onCheckedChanged(checked)
        }
    }

    /**
     * Sets checkbox checked or unchecked
     *
     * @param checked : If true checkboxes will be checked, if false checkboxes will be unchecked
     */
    fun setAllChecked(checked : Boolean)
    {
        setChecked(checked)

        for (i in 0 until children.childCount)
        {
            if(children.getChildAt(i) is CheckBox)
            {
                (children.getChildAt(i) as CheckBox).isChecked = checked
            }
        }
    }

    /**
     * @return : True if the checkbox is currently checked
     *           False if it is not currently checked
     */
    fun isChecked() : Boolean
    {
        return checkbox!!.isChecked
    }

    /**
     * @param index : The index of a child checkbox
     *
     * @return : True if the child checkbox at the index is currently checked
     *           False if it is not currently checked
     */
    fun isChecked(index: Int) : Boolean
    {
        return getChildCheckboxAt(index)!!.isChecked
    }

    /**
     * The actions in this listener will occur when the checkbox changes from checked to unchecked
     *  or vice versa
     *
     *  @param listener : contains actions
     */
    fun setCheckedChangeListener(listener: OnCheckedChangeListener)
    {
        onCheckedChangeListener = listener
    }

    /**
     * Get a color filter to change the color of something
     *
     * @param color : the new color to use
     *
     * @return: color filter for the object
     */
    private fun changeColor(color: Int) : ColorMatrixColorFilter
    {
        val red = (color and 0xFF0000) / 0xFFFF
        val green = (color and 0xFF00) / 0xFF
        val blue = color and 0xFF

        val matrix = floatArrayOf(0f, 0f, 0f, 0f, red.toFloat(), 0f, 0f, 0f, 0f, green.toFloat(), 0f, 0f, 0f, 0f, blue.toFloat(), 0f, 0f, 0f, 1f, 0f)

        return ColorMatrixColorFilter(matrix)
    }

    /**
     * Change the color of the expander drawable
     *
     * @param color : The new color to make the expander
     */
    private fun changeExpanderColor(color: Int) {
        expander?.drawable?.colorFilter = changeColor(color)
    }

    /**
     * If a child view is a checkbox, add is using addChild instead of the default addView
     *
     * @param : Checkbox to add
     */
    private fun myAddView(child: CheckBox)
    {
        // If it's the parent checkbox
        if(child.parent != null && child.parent == checkboxLayout)
        {super.addView(child)}
        // If it's a child checkbox
        else {
            removeView(child)
            addChild(child.text.toString(), null)
        }
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if(child is CheckBox)
        {
            myAddView(child)
        }
        else
            super.addView(child, index, params)
    }

    override fun addView(child: View?, width: Int, height: Int) {
        if(child is CheckBox)
        {
            myAddView(child)
        }
        else
            super.addView(child, width, height)
    }

    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        if(child is CheckBox)
        {
            myAddView(child)
        }
        else
            super.addView(child, params)
    }

    override fun addView(child: View?, index: Int) {
        if(child is CheckBox)
        {
            myAddView(child)
        }
        else
            super.addView(child, index)
    }

    override fun addView(child: View?) {

        if(child is CheckBox)
        {
            myAddView(child)
        }
        else
            super.addView(child)
    }
}