package com.fifteen15studios.expandablecheckboxtest

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.ScrollView
import android.widget.Toast
import com.fifteen15studios.expandablecheckbox.ExpandableCheckbox

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Use XML example
        setContentView(R.layout.activity_with_boxes)

        // Uncomment this block to use kotlin example
        /*setContentView(R.layout.activity_main)
        val view = findViewById<ScrollView>(R.id.checkboxes)

        val check1 = ExpandableCheckbox(this)
        check1.text = "Top"
        check1.textColor = Color.RED
        check1.childTextColor = Color.CYAN
        check1.childCheckboxColor = Color.MAGENTA
        val subCheck = ExpandableCheckbox(this)
        subCheck.text = "Expandable Sub"
        val subSub = ExpandableCheckbox(this)
        subSub.text = "Another Expandable Sub"
        subSub.addChild("Another Sub", null)
        subSub.childTextColor = Color.BLACK //This doesn't work. Fix this.
        subCheck.addChild(subSub)

        check1.addChild("Sub1", null)
        subCheck.addChild("Sub of sub", null)
        subCheck.addChild("Sub2 of sub", null)
        check1.addChild(subCheck)

        setListener(check1)

        view.addView(check1)*/
    }

    fun setListener(checkbox: ExpandableCheckbox?)
    {
        val listener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                Toast.makeText(this, "${buttonView.text} checked.", Toast.LENGTH_SHORT).show()
        }

        // Set checkChangedListener for top level checkbox
        checkbox?.setOnCheckedChangeListener(listener)

        // Set checkChangedListener for children
        if(checkbox != null)
            for( index in 0 .. checkbox.getChildCheckboxCount()) {
                setListener(checkbox.getChildCheckboxAt(index))
            }
    }
}
