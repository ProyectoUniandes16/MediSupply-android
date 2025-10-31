package com.uniandes.medisupply.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.uniandes.medisupply.R

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SpinnerDropdown(
    options: List<String>,
    label: String,
    onOptionSelected: (String) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            value = selectedOptionText,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = stringResource(R.string.dropdown_icon_description)
                )
            },
            modifier = Modifier.fillMaxWidth().clickable { expanded = true },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    onClick = {
                        selectedOptionText = selectionOption
                        expanded = false
                        onOptionSelected(selectionOption)
                    }
                ) {
                    Text(text = selectionOption)
                }
            }
        }
    }
}

@Preview
@Composable
fun SpinnerDropdownPreview() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val options = listOf("Option 1", "Option 2", "Option 3")
        SpinnerDropdown(
            options = options,
            label = "Select an option"
        )
        Text(text = "Selected option: $options")
        Text(text = "Selected option: $options")
        Text(text = "Selected option: $options")
    }
}
