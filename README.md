# ExpandableCheckbox
An expandable checkbox for Android which can contain child checkboxes

## How it works
Checkboxes can be used from Java/Kotlin code, from XML code, or a combination of both.

Clicking on the parent checkbox will check or uncheck all of the children beneath it, selecting all of the children will check the parent, deselecting all of the children will deselect the parent, and selecting some of the children (but not all) will put the parent in an in between state where it shows as fully filled in, instead of having a check mark or being completely empty.

Clicking on the expander [+] or [-] will expand or collapse the child view.

Long pressing the text of the parent checkbox will also expand or collapse the child view.

### Optional parameters
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

// Optional values
expandableCheckbox.expanded = true
expandableCheckbox.text = "Parent checkbox text"
expandableCheckbox.textColor = Color.White
expandableCheckbox.childTextColor = Color.Green
expandableCheckbox.checkboxColor = Color.BLACK
expandableCheckbox.childCheckboxColor = Color.BLUE
expandableCheckbox.expanderColor = Color.Yellow
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
    app:checkboxColor="@color/black"
    app:childCheckboxColor="@color/blue"
    app:expanderColor="@color/yellow">
    <!-- Any of the above values that start with "app:" are optional -->
    <!-- Changing the color of the expander will NOT show in Android Studio preview, but all other changes will -->
    <CheckBox
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="childCheckbox"/>
    <CheckBox
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Child2"/>
</com.fifteen15studios.expandablecheckbox.ExpandableCheckbox>
```

### Kotlin Part
```
val expandableCheckbox = findViewById<ExpandableCheckbox>(R.id.parentCheckbox)
val child1 = findViewById<Checkbox>(R.id.childCheckbox)

// Notice that the onCheckedChangeListener is NOT set on the child checkbox object. 
// This is because doing so will ruin the inbetween state of the parent checkbox 
// where some of the children are checked, and others aren't.
expandableCheckbox.setChildOnCheckedChangeListener(0, object : CompoundButton.OnCheckedChangeListener {
  override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
      if (isChecked)
      // Do something when checked
      else
      // Do something when unchecked
  }
})
```
