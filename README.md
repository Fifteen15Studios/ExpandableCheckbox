# ExpandableCheckbox
An expandable checkbox for Android which can contain child checkboxes.

Note that the gif below has custom values set for colors.

<img src="https://raw.githubusercontent.com/Fifteen15Studios/ExpandableCheckbox/master/2020_02_28_164500.gif?raw=true" alt="gif" width="250"> 

## How it works
Checkboxes can be used from Java/Kotlin code, from XML code, or a combination of both.

Clicking on the parent checkbox will check or uncheck all of the children beneath it, selecting all of the children will check the parent, deselecting all of the children will deselect the parent, and selecting some of the children (but not all) will put the parent in an in between state where it shows as fully filled in, instead of having a check mark or being completely empty.

Clicking on the expander [+] or [-] will expand or collapse the child view.

Long pressing the text of the parent checkbox will also expand or collapse the child view.

## Known Issues
1. ChildTextColor does not show properly in XML preview 
2. ExpanderColor does not show properly on children in XML preview

### Optional XML parameters
**checked**: Initial value of the checkbox. If true the box wil be checked, if false it will be empty (unchecked).

**Default**: False


**expanded**: The initial state of the child view. If true, the children will be shown, if false they will be hidden.

**Default**: False


**text**: The text shown next to the parent checkbox

**Default**: "" (empty String)


**textColor**: The color of the text shown next to the parent checkbox

**Default**: android.R.attr.textColorPrimary. If android.R.attr.textColorPrimary not found, default is Color.Black


**childTextColor**: The color of the text shown next to the child checkboxes

**Default**: android.R.attr.textColorPrimary. If android.R.attr.textColorPrimary not found, default is Color.Black


**checkboxColor**: The color of the box containing the parent checkbox

**Defualt**: For API 21+ - android.R.attr.colorPrimary. If android.R.attr.colorPrimary not found, or API is < 21, default is default text color


**childCheckboxColor**: The color of the box containing the child checkboxed

**Defualt**: For API 21+ - android.R.attr.colorPrimary. If android.R.attr.colorPrimary not found, or API is < 21, default is default text color


**expanderColor**: The color of the [+] or [-] drawable used to expand/collapse the child view  
NOTE: Changing this color will also change the color of expanders in child objects. You can explicitly change the color of the expander on those children, and the color set on the child object will take priority.

**Default**: Default text color

## Kotlin example
```
// Create Expandable Checkbox
val expandableCheckbox = ExpandableCheckbox(context)

// Add a child checkbox. This can be repeated to add multiple children
expandableCheckbox.addChild("child checkbox", object : CompoundButton.OnCheckedChangeListener{
    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked)
            // Do something when checked
        else
            // Do something when unchecked
    }
}

// Add an existing child
val child2 = ExpandableCheckbox(this)
child2.text = "Top"
child2.textColor = Color.RED
child2.childTextColor = Color.CYAN
child2.childCheckboxColor = Color.MAGENTA

expandableCheckbox.addChild(child2)

// Children can also have children
child2.addChild("Sub Child", null)

// Optional values
expandableCheckbox.expanded = true
expandableCheckbox.text = "Parent checkbox text"
expandableCheckbox.textColor = Color.WHITE
expandableCheckbox.childTextColor = Color.GREEN
expandableCheckbox.checkboxColor = Color.MAGENTA
expandableCheckbox.childCheckboxColor = Color.BLUE
expandableCheckbox.expanderColor = Color.YELLOW
```

## XML Example
Most of the time when using XML, you will also have some Java or Kotlin code with it. Below is an example using both Kotlin and XML

### XML Part
```
<com.fifteen15studios.expandablecheckbox.ExpandableCheckbox
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/parentCheckbox"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:text="ParentCheckbox"
    app:textColor="@color/white"
    app:childTextColor="@color/green"
    app:checkboxColor="@color/majenta"
    app:childCheckboxColor="@color/blue"
    app:expanderColor="@color/yellow">
    <!-- Any of the above values that start with "app:" are optional -->
    
    <!-- Children can be CheckBox or ExpandableCheckbox. The code will convert them all to ExpandableCheckbox -->
    <CheckBox
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="childCheckbox"
        android:id="@+id/childCheckbox"/>
    <com.fifteen15studios.expandablecheckbox.ExpandableCheckbox
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:id="@+id/Child2"
        app:text="Child2"
        app:checkboxColor="@color/red"/>
</com.fifteen15studios.expandablecheckbox.ExpandableCheckbox>
```

### Kotlin Part
```
val expandableCheckbox = findViewById<ExpandableCheckbox>(R.id.parentCheckbox)
val child1 = findViewById<Checkbox>(R.id.childCheckbox)

child1.setOnCheckedChangeListener(object : CompundButton.OnCheckedChangeListener {
  override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
      if (isChecked)
          // Do something when checked
      else
          // Do something when unchecked
  }
})
```

## Pubic functions
**addChild(child: ExpandableCheckbox)** - Adds a new child checkbox. Parameters: child: the checkbox to add

**addChild(child: ExpandableCheckbox, onCheckedChangeListener: CompoundButton.OnCheckedChangeListener?)** - Adds a new child checkbox. Parameters: child: the checkbox to add. onCheckedChangeListener: Actions to perform when the check changes for this child.

**addChild(text : String, onCheckedChangeListener: CompoundButton.OnCheckedChangeListener?)** - Adds a new child checkbox. Parameters: text: The text to display next to the child checkbox. onCheckedChangeListener: Actions to perform when the check changes for this child.

**getChildCheckbox(text: String)** - Returns the child checkbox with specific text value. Parameters: text: String to look for in each child

**getChildCheckboxAt(index: Int)** - Returns the child checkbox at a specific index. If the index is invalid, returns null. Parameters: index: Index at which to look for a child.

**getChildCheckboxCount()** - Returns the number of child checkboxes

**hasChildren()** - Returns true if it has child checkboxes, otherwise returns false

**isChecked()** - Returns true if parent is checked, flase if parent is not checked.

**isChecked(index: Int)** - Returns true if child checkbox is checked, false if child checkbox is not checked. Parameters: index: index of child to check.

**isExpanded()** - Returns true if the view is currently expanded, false if it is currently collapsed

**setAllChecked(checked : Boolean)** - Checks or unchecks all checkboxes, parent and children. Parameters: checked: If true checks the parent and all children, if false unchecks the parent and all children.

**setChecked(checked: Boolean)** - Checks or unchecks the parent checkbox and ONLY the parent checkbox. Calling this function will NOT affect the children. Parameter: If true checks the parent, if False unchecks the parent.

**setOnCheckedChangeListener(onCheckedChangeListener: CompoundButton.OnCheckedChangeListener?)** - Sets the onCheckedChangeListener for this checkbox. Paramters: onCheckedChangeListener: Actions to perform when the check changes for this checkbox.
