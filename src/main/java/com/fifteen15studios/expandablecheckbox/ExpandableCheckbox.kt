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
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.Exception

class ExpandableCheckbox : ConstraintLayout {

    companion object {
        const val SHAPE_SQUARE = 0
        const val SHAPE_STAR = 1

        private const val DIRECTION_DOWN = 0
        private const val DIRECTION_UP = 1
    }

    /**
     * Whether or not the child checkboxes are show
     */
    var expanded = false
        set(value) {
            field = value

            try{
                expand(value)
            }
            catch (e: Exception)
            {e.printStackTrace()}

            if(value) {
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

            changeExpanderColor()
        }

    private var checkboxLayout : LinearLayout? = null
    private var checkbox : AppCompatCheckBox? = null
    private var expanderLayout : LinearLayout? = null
    private var expander : ImageView? = null
    private var parent : ExpandableCheckbox? = null

    /**
     * The shape of the checkboxes. Can be set to [SHAPE_SQUARE] or [SHAPE_STAR]
     */
    var shape = SHAPE_SQUARE
        set(value) {
            if(field != value) {
                field = value
                shapeChanged = true
            }

            for(i in 0 until getChildCheckboxCount()) {
                val box = getChildCheckboxAt(i)
                if (box is ExpandableCheckbox && !box.shapeChanged) {
                    box.shape = value
                }
            }

            setCheckboxView()
        }

    private var shapeChanged = false

    /**
     * Text to be displayed next to the checkbox
     */
    var text = ""
        set(value) {
            field = value
            checkbox?.text = value
        }

    /**
     * Color of the text next to the checkbox
     */
    var textColor = Color.BLACK
        set(value) {
            if(field!=value) {
                field = value
                textColorChanged = true
            }

            if(!childTextColorChanged) {
                childTextColor = value
                childCheckboxColorChanged = false
            }

            checkbox?.setTextColor(value)
        }

    private var textColorChanged = false

    /**
     * Color of the text next to the checkbox on the children of this checkbox
     */
    // TODO: this change doesn't show in XML
    var childTextColor = Color.BLACK
        set(value) {
            if(field!=value) {
                field = value
                childTextColorChanged = true
            }

            for(i in 0 until getChildCheckboxCount())
            {
                val box = getChildCheckboxAt(i)
                if(box is ExpandableCheckbox)
                {
                    if (!box.textColorChanged)
                        box.textColor = value
                    if (!box.childTextColorChanged)
                        box.childTextColor = value
                }
            }
        }

    var childTextColorChanged = false

    /**
     * Color of the checkbox
     */
    var checkboxColor = Color.BLACK
        set(value) {
            if(field!=value) {
                field = value
                checkboxColorChanged = true
                if(!childCheckboxColorChanged)
                    childCheckboxColor = value
            }

            try {
                //Change Color
                CompoundButtonCompat.setButtonTintList(checkbox!!,
                        ColorStateList(
                                arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
                                intArrayOf(value, value)))

                setCheckboxView()
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
        }

    private var checkboxColorChanged = false

    /**
     * Color of the checkbox for the children of this checkbox
     */
    var childCheckboxColor = Color.BLACK
        set(value) {
            if(field != value) {
                field = value
                childCheckboxColorChanged = true
            }

            for(i in 0 until getChildCheckboxCount())
            {
                val box = getChildCheckboxAt(i)
                if(box is ExpandableCheckbox)
                {
                    if(!box.checkboxColorChanged)
                        box.checkboxColor = value
                    if(!box.childCheckboxColorChanged)
                        box.childCheckboxColor = value
                }
            }
        }

    private var childCheckboxColorChanged = false

    /**
     * Color of the [+] or [-] icon next to the checkbox
     */
    // TODO: doesn't show completely correctly in XML
    var expanderColor = Color.BLACK
        set(value) {
            if(field!=value) {
                field = value
                expanderColorChanged = true
                changeExpanderColor()
            }

            for(i in 0 until getChildCheckboxCount())
            {
                val box = getChildCheckboxAt(i)
                if(box is ExpandableCheckbox)
                {
                    if(!box.expanderColorChanged) {
                        box.expanderColor = value
                    }
                }
            }
        }

    private var expanderColorChanged = false

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

    /**
     * @return True if this checkbox contains other checkboxes, false otherwise
     */
    fun hasChildren(): Boolean {
        return getChildCheckboxCount() > 0
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {

        val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.expandable_checkbox, this, true)

        checkboxLayout = view.findViewById(R.id.checkboxLayout)
        checkbox = view.findViewById(R.id.checkbox)
        expanderLayout = view.findViewById(R.id.expanderLayout)
        expanderLayout?.visibility = INVISIBLE
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

        var currentA = a.getColor(R.styleable.ExpandableCheckbox_textColor, defaultTextColor)
        textColor = currentA
        if(currentA == defaultTextColor)
            textColorChanged = false

        currentA = a.getColor(R.styleable.ExpandableCheckbox_childTextColor, defaultTextColor)
        childTextColor = currentA
        if(currentA == defaultTextColor)
            childTextColorChanged = false

        currentA = a.getColor(R.styleable.ExpandableCheckbox_expanderColor, defaultTextColor)
        expanderColor = currentA
        if(currentA == defaultTextColor)
            expanderColorChanged = false

        currentA = a.getColor(R.styleable.ExpandableCheckbox_checkboxColor, colorPrimary)
        checkboxColor = currentA
        if(currentA == colorPrimary)
            checkboxColorChanged = false

        currentA = a.getColor(R.styleable.ExpandableCheckbox_childCheckboxColor, colorPrimary)
        childCheckboxColor = currentA
        if(currentA == colorPrimary)
            childCheckboxColorChanged = false

        checkbox?.isChecked = a.getBoolean(R.styleable.ExpandableCheckbox_checked, false)

        expanded = a.getBoolean(R.styleable.ExpandableCheckbox_expanded, false)

        a.recycle()

        // Expand/collapse on click
        expanderLayout?.setOnClickListener{
            expanded = !isExpanded()
        }

        // Run listener when checked changes
        checkbox?.setOnCheckedChangeListener{buttonView, isChecked ->
            onCheckedChangeListener?.onCheckedChanged(checkbox!!, isChecked)
        }

        // Check/uncheck all children when clicked
        checkbox?.setOnClickListener {
            setAllChecked(isChecked())
        }

        //Expand on long click
        checkboxLayout?.setOnLongClickListener{
            expanded = !isExpanded()

            //return
            false
        }

        // Expand/Collapse initially. Mostly for use in XML
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
    }

    /**
     * Expand or collapse the view of child checkboxes
     *
     * @param expand : If true expands the view, if false collapses the view
     */
    private fun expand(expand: Boolean) {
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
        fun onCheckedChanged(box: AppCompatCheckBox, checked: Boolean)
    }

    /**
     * Add a child checkbox. These will be hidden while the main view is collapsed and visible
     *  when the view is expanded
     *
     * @param child : Checkbox to be added
     */
    fun addChild(child: ExpandableCheckbox)
    {
        addChild(child, null)
    }

    /**
     * Add a child checkbox. These will be hidden while the main view is collapsed and visible
     *  when the view is expanded
     *
     * @param text : The text next to the new checkbox
     * @param onCheckedChangeListener : What to do when the checkbox check changes
     */
    fun addChild(text: String, onCheckedChangeListener: CompoundButton.OnCheckedChangeListener?)
    {

        val child = ExpandableCheckbox(context)
        child.text = text

        addChild(child, onCheckedChangeListener)
    }

    /**
     * Add a child checkbox. These will be hidden while the main view is collapsed and visible
     *  when the view is expanded
     *
     * @param child : Checkbox to be added
     * @param onCheckedChangeListener : What to do when the checkbox check changes
     */
    fun addChild(child: ExpandableCheckbox, onCheckedChangeListener: CompoundButton.OnCheckedChangeListener?)
    {
        child.setOnCheckedChangeListener(onCheckedChangeListener)

        expanderLayout?.visibility = View.VISIBLE

        if(!child.textColorChanged)
            child.textColor = childTextColor
        if(!child.childTextColorChanged)
            child.childTextColor = childTextColor
        if(!child.checkboxColorChanged)
            child.checkboxColor = childCheckboxColor
        if(!child.childCheckboxColorChanged)
            child.childCheckboxColor = childCheckboxColor
        if(!child.expanderColorChanged)
            child.expanderColor = expanderColor

        child.parent = this

        children.addView(child)

        child.changeExpanderColor()
    }

    /**
     * Sets actions to perform when a check is changed on a child checkbox
     *
     * @param onCheckedChangeListener : Actions to perform when checkbox clicked
     */
    fun setOnCheckedChangeListener(onCheckedChangeListener: CompoundButton.OnCheckedChangeListener?)
    {
        // Check/Uncheck the parent checkbox when necessary
        this.onCheckedChangeListener = object: OnCheckedChangeListener {
            override fun onCheckedChanged(box: AppCompatCheckBox, checked: Boolean) {
                setAllChecked(checked)
                onCheckedChangeListener?.onCheckedChanged(box, checked)
                setCheckboxView()
            }
        }
    }

    /**
     * Sets the drawable of the inside of the checkbox based on the state of child  checkboxes
     */
    private fun setCheckboxView()
    {
        if(allChildrenChecked()) {
            if (!isChecked())
                setChecked(true)
        } else if(noChildrenChecked()) {
            if (isChecked())
                setChecked(false)
        }

        if(shape == SHAPE_STAR)
        {
            checkbox?.buttonDrawable = if(allChildrenChecked() || noChildrenChecked()) {
                resources.getDrawable(R.drawable.star_fill)
            }
            else {
                resources.getDrawable(R.drawable.star_full)
            }
        }
        else {
            checkbox?.buttonDrawable = if (allChildrenChecked() || noChildrenChecked()){
                resources.getDrawable(R.drawable.checkbox_fill)
            } else {
                resources.getDrawable(R.drawable.checkbox_full)
            }
        }

        if(parent != null)
            parent!!.setCheckboxView()
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
            if((view is CheckBox && view.isChecked))
                count++
            else
                if (view is ExpandableCheckbox && view.allChildrenChecked()) {
                    //1 for the checkbox itself, plus the number of children
                    count += view.getChildCheckboxCount() + 1
                }
        }

        return if(hasChildren())
            count == getChildCheckboxCount()
        else
            isChecked()
    }

    /**
     * @return : true if no children are checked, otherwise false
     */
    private fun noChildrenChecked() : Boolean
    {
        var count = 0

        for(i in 0 until getChildCheckboxCount())
        {
            val view = getChildCheckboxAt(i)

            if (view is ExpandableCheckbox && view.noChildrenChecked()) {
                //1 for the checkbox itself, plus the number of children
                count += view.getChildCheckboxCount() + 1
            }
        }

        return if(hasChildren())
            count == getChildCheckboxCount()
        else
            !isChecked()
    }

    /**
     * @return : How many child checkboxes have been added
     */
    fun getChildCheckboxCount() : Int
    {
        var count = 0

        for(i in 0 until children.childCount) {
            val subCheckbox = children.getChildAt(i)
            if (subCheckbox is ExpandableCheckbox) {
                //1 for the checkbox itself, plus the number of children
                count += subCheckbox.getChildCheckboxCount() +1

                // To fix bug with some expanders changing and others not
                if(!subCheckbox.expanderColorChanged)
                    subCheckbox.changeExpanderColor()
            }
        }

        return count
    }

    /**
     * Gets a checkbox at a specific index
     *
     * @param : The index of the requested checkbox
     *
     * @return : The child checkbox at the desired index specific index
     */
    private fun getChildCheckboxAt(index: Int): Any? {
        if(children.getChildAt(index) is ExpandableCheckbox) {
            return children.getChildAt(index)
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
     * Set the state of the checkbox
     *
     * @param checked : If true checkbox will be checked, if false checkbox will be unchecked
     */
    fun setChecked(checked: Boolean)
    {
        //Only do this if check really changed
        if(checkbox?.isChecked != checked)
        {
            checkbox?.isChecked = checked
            onCheckedChangeListener?.onCheckedChanged(checkbox!!, checked)
            setCheckboxView()
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

        for (i in 0 until getChildCheckboxCount())
        {
            val child = children.getChildAt(i)

            if(child is ExpandableCheckbox)
            {
                child.setAllChecked(checked)
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
     */
    private fun changeExpanderColor() {

        if(Build.VERSION.SDK_INT >= 21)
            expander?.drawable?.setTint(expanderColor)
        else {
            // Use .mutate() to get a new instance of the same drawable.
            // This fixes an issue with expander colors of child items
            expander?.drawable?.mutate()!!.colorFilter = changeColor(expanderColor)
        }
    }

    /**
     * If a child view is a checkbox, add it using addChild instead of the default addView
     *
     * @param : Checkbox to add
     */
    private fun myAddView(child: View)
    {
        // If it's the parent checkbox
        if(child.parent != null && child.parent == checkboxLayout)
        {super.addView(child)}
        // If it's an expandableCheckbox
        else if(child is ExpandableCheckbox)
        {
            removeView(child)
            addChild(child)
        }
        // If it's a child checkbox
        else if(child is CheckBox) {
            removeView(child)
            addChild(child.text.toString(), null)
        }
        // Catch all
        else
            super.addView(child)
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if(child is CheckBox || child is ExpandableCheckbox)
        {
            myAddView(child)
        }
        else
            super.addView(child, index, params)
    }

    override fun addView(child: View?, width: Int, height: Int) {
        if(child is CheckBox || child is ExpandableCheckbox)
        {
            myAddView(child)
        }
        else
            super.addView(child, width, height)
    }

    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        if(child is CheckBox || child is ExpandableCheckbox)
        {
            myAddView(child)
        }
        else
            super.addView(child, params)
    }

    override fun addView(child: View?, index: Int) {
        if(child is CheckBox || child is ExpandableCheckbox)
        {
            myAddView(child)
        }
        else
            super.addView(child, index)
    }

    override fun addView(child: View?) {

        if(child is CheckBox || child is ExpandableCheckbox)
        {
            myAddView(child)
        }
        else
            super.addView(child)
    }
}