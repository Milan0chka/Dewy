package com.example.dewy.ui.pages

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.dewy.Screen
import com.example.dewy.data.models.Product
import com.example.dewy.data.models.Routine
import com.example.dewy.data.models.RoutineStep
import com.example.dewy.viewmodels.RoutineBuilderViewModel
import com.example.dewy.viewmodels.RoutineViewModel
import java.util.Calendar

//todo if added frequncy onn step >7 informarn than man wtf ypu are doing
@SuppressLint("RememberReturnType")
@Composable
fun RoutineBuilderScreen(
    navController: NavController,
    routineType: String,
    routineViewModel: RoutineViewModel,
    routineBuilderViewModel: RoutineBuilderViewModel
) {
    val isBuilding by routineBuilderViewModel.isBuilding

    var currentPart by remember { mutableIntStateOf(0) }
    val preferredTime = remember { mutableStateOf("") }

    val steps = remember {
        mutableStateListOf(
            RoutineStep(stepName = "Cleanse"),
            RoutineStep(stepName = "Hydrate"),
            RoutineStep(stepName = "Treat"),
            RoutineStep(stepName = "Moisturize")
        ).apply {
            if (routineType == "Morning") {
                add(RoutineStep(stepName = "SPF"))
            } else if (routineType == "Evening") {
                add(1, RoutineStep(stepName = "Exfoliate"))
            }
        }
    }

    Column{
        StepIndicator(currentPart)
        if (!isBuilding){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 10.dp)
            ) {
                when (currentPart) {
                    0 -> RoutineDescription(
                        routineType = routineType,
                        onNext = { currentPart++ }
                    )
                    1 -> PreferredTime(
                        preferredTime = preferredTime.value,
                        onTimeChange = { preferredTime.value = it },
                        onNext = { currentPart++ },
                        onBack = { currentPart-- }
                    )
                    in 2..6 -> StepForm(
                        index = currentPart-1,
                        step = steps[currentPart-2],
                        onNext = { products ->
                            steps[currentPart-2].products = products.toMutableList()
                            currentPart++ },
                        onBack = { currentPart-- }
                    )
                    7 -> RoutineSummary(
                        routineName = routineType,
                        preferredTime = preferredTime.value,
                        steps = steps,
                        onBack = { currentPart-- },
                        onFinish = { steps ->

                            routineBuilderViewModel.buildRoutine(
                                routineType,
                                preferredTime.value,
                                steps){ success ->
                                if (success){
                                    routineViewModel.fetchRoutines()
                                    currentPart++
                                } else
                                    currentPart+=2
                            }
                        }
                    )
                    8 -> RoutineBuild { navController.navigate(Screen.Routines.route) }
                    9 -> RoutineFailed { navController.navigate(Screen.Routines.route) }
                }
            }
        } else {
            RoutineIsBuilding()
        }
    }


}

@Composable
fun StepIndicator(currentIndex: Int, totalSteps: Int = 8) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(8f)
        ) {
            for (i in 1..totalSteps) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .border(
                            shape = CircleShape,
                            width = 2.dp,
                            color = if (i == currentIndex + 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceDim
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = i.toString(),
                        color = if (i == currentIndex+1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceDim,
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                if (i < totalSteps) {
                    Row(
                        modifier = Modifier
                            .height(2.dp)
                            .weight(1f)
                            .padding(horizontal = 5.dp)
                            .background(MaterialTheme.colorScheme.surfaceDim)
                    ){}
                }
            }
        }
    }
}

@Composable
fun FormButton(
    onClick: () -> Unit,
    text: String
){
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun FormNavigation(
    onBack: () -> Unit,
    onNext: () -> Unit
){
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        FormButton(onBack, "Back")
        FormButton(onNext, "Next")
    }
}

@Composable
fun RoutineDescription(
    routineType: String,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Let`s build ${routineType.lowercase()} routine!",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = if (routineType == "Morning") "Morning skincare routine is generally lighter. It is more moisturizing and protecting, rather than treating!\nIn the end, it is recommended to apply SPF. "
            else "Evening routine is usually more heavy. It prioritizes treatments like exfoliation and active ingredients to address specific concerns, promoting skin renewal while you sleep",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 25.sp
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)){
            Text(
                text = "Instructions:",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                lineHeight = 20.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Add all products you are planing to use during the week and specify their frequency of use.\nDewy will then analyze your products, and build balanced weekly skincare routine.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                lineHeight = 20.sp,
            )

            FormButton(onNext, "Start")
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun PreferredTime(
    preferredTime: String,
    onTimeChange: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }
    val calendar = remember { Calendar.getInstance() }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Preferred Time",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Choose time when you would usually perform this routine. We will be sending you reminders, so that you won`t miss it!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .aspectRatio(1f)
                .clickable { showTimePicker = true },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceDim,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Time",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(50.dp)
                )
                Text(
                    text = if (preferredTime.isEmpty()) "Click to select time" else "Selected Time:\n$preferredTime",
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            if (showTimePicker) {
                TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hour)
                        calendar.set(Calendar.MINUTE, minute)
                        val formattedTime = String.format("%02d:%02d", hour, minute)
                        onTimeChange(formattedTime)
                        showTimePicker = false
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).apply {
                    setOnCancelListener { showTimePicker = false }
                    setOnDismissListener { showTimePicker = false }
                }.show()
            }
        }

        FormNavigation(
            onBack = onBack,
            onNext = {
                if(preferredTime.isNotEmpty())
                    onNext()
                else
                    Toast.makeText(context, "Enter time before continuing.",Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun StepForm(
    index: Int,
    step: RoutineStep,
    onNext: (List<Product>) -> Unit,
    onBack: () -> Unit
) {
    val productList = remember(step.stepName) { step.products.toMutableStateList() }
    var showDialog by remember { mutableStateOf(false) }

    val desc = when (step.stepName) {
        "Cleanse" -> "Cleansing removes dirt, oil, and impurities from your skin, preventing clogged pores and breakouts."
        "Hydrate" -> "Hydrating restores moisture to your skin, improving its texture and elasticity while preventing dryness."
        "Treat" -> "Treating targets specific skin concerns, such as acne or dark spots, using active ingredients to address them effectively."
        "Moisturize" -> "Moisturizing locks in hydration and protects your skin barrier, preventing dryness and irritation."
        "Exfoliate" -> "Exfoliating removes dead skin cells, promoting a smoother complexion and preventing dullness and clogged pores."
        "SPF" -> "Applying SPF protects your skin from harmful UV rays, preventing sunburn, premature aging, and skin cancer."
        else -> "Unknown step"
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Skincare step #$index: ${step.stepName}",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onBackground
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Product list:", style = MaterialTheme.typography.bodyLarge)

                productList.forEachIndexed { i, product ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove",
                            tint = MaterialTheme.colorScheme.onError,
                            modifier = Modifier
                                .size(25.dp)
                                .padding(5.dp)
                                .background(MaterialTheme.colorScheme.error, CircleShape)
                                .clickable { productList.removeAt(i)  }
                        )
                    }
                }

                Button(
                    onClick = {
                        showDialog = true  // Correctly set the dialog visibility
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text("Add Product")
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            FormButton(onBack, "Back")
            if (productList.isEmpty()) {
                FormButton({ onNext(productList.toList()) }, "Skip step")
            } else {
                FormButton({ onNext(productList.toList()) }, "Next")
            }
        }
    }

    // Product dialog to add a new product
    if (showDialog) {
        ProductDialog(
            onDismissRequest = { showDialog = false },
            onAddProduct = { newProduct ->
                productList.add(newProduct) // Add product to the correct list
                showDialog = false // Close the dialog
            }
        )
    }
}

@Composable
fun ProductDialog(
    onDismissRequest: () -> Unit,
    onAddProduct: (Product) -> Unit
) {
    val context = LocalContext.current
    var productName by remember { mutableStateOf("") }
    val availableIngredients = listOf(
        "Hyaluronic Acid", "Niacinamide", "Ceramides", "Peptides",
        "Glycolic Acid", "Panthenol", "Salicylic Acid", "Vitamin C",
        "Centella", "Retinol")
    val selectedIngredients = remember { mutableStateListOf<String>() }
    val frequencies = listOf("Once a week", "Twice a week", "Thrice a week", "Every day")
    var selectedFrequency by remember { mutableStateOf<String?>(null) }

    // State for dropdowns
    var isIngredientDropdownExpanded by remember { mutableStateOf(false) }
    var isFrequencyDropdownExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.onBackground
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Add New Product", style = MaterialTheme.typography.titleSmall)

                BasicTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .border(1.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (productName.isEmpty()) {
                                Text(
                                    text = "Product Name",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                            innerTextField()
                        }
                    }
                )


                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            text = "Active Ingredients:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Button(
                                onClick = { isIngredientDropdownExpanded = true },
                                shape = MaterialTheme.shapes.small,
                                contentPadding = PaddingValues(1.dp)
                            ) {
                                Text(text = "Select")
                            }

                            DropdownMenu(
                                expanded = isIngredientDropdownExpanded,
                                onDismissRequest = { isIngredientDropdownExpanded = false }
                            ) {
                                availableIngredients.forEach { ingredient ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = ingredient,
                                                    modifier = Modifier.weight(1f),
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                                val isSelected = ingredient in selectedIngredients
                                                Box(
                                                    modifier = Modifier
                                                        .padding(start= 4.dp)
                                                        .size(16.dp)
                                                        .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent, CircleShape)
                                                )
                                            }
                                        },
                                        onClick = {
                                            if (ingredient in selectedIngredients) {
                                                selectedIngredients.remove(ingredient)
                                            } else {
                                                selectedIngredients.add(ingredient)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                    Text(
                        text = if (selectedIngredients.isEmpty()) "None" else selectedIngredients.joinToString(", "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        overflow = TextOverflow.Ellipsis,
                        minLines = 2
                    )
                }


                Column(){
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            text = "Frequency of use: ",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Button(
                                onClick = { isFrequencyDropdownExpanded = true },
                                shape = MaterialTheme.shapes.small,
                                contentPadding = PaddingValues(2.dp)
                            ) {
                                Text(text = "Select ")
                            }

                            DropdownMenu(
                                expanded = isFrequencyDropdownExpanded,
                                onDismissRequest = { isFrequencyDropdownExpanded = false }
                            ) {
                                frequencies.forEach { frequency ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = frequency,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        },
                                        onClick = {
                                            selectedFrequency = frequency
                                            isFrequencyDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    Text(
                        text = selectedFrequency?: "Not chosen" ,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        overflow = TextOverflow.Ellipsis,
                        minLines = 2
                    )
                }


                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onDismissRequest,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceDim,
                            contentColor = MaterialTheme.colorScheme.onBackground
                        )
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            if (productName.isNotEmpty() && selectedFrequency != null) {
                                onAddProduct(
                                    Product(
                                        name = productName,
                                        activeIngredients = selectedIngredients,
                                        frequency = when (selectedFrequency) {
                                            "Once a week" -> 1
                                            "Twice a week" -> 2
                                            "Thrice a week" -> 3
                                            else -> 7
                                        }
                                    )
                                )
                            } else {
                                Toast.makeText(context, "Please enter product name and frequency of use.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

@Composable
fun RoutineSummary(
    routineName: String,
    preferredTime: String,
    steps: List<RoutineStep>,
    onBack: () -> Unit,
    onFinish: (List<RoutineStep>) -> Unit
) {
    var routineFlag = false
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "$routineName routine summary",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Preferred time: $preferredTime",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            steps.forEach { step ->
                if (step.products.isNotEmpty()) {
                    routineFlag = true
                    Text(
                        text = "Step: ${step.stepName}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    step.products.forEach { product ->
                        Text(
                            text = "Product: ${product.name} x ${product.frequency}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            if (!routineFlag) {
                Text(
                    text = "No steps filled.",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = onBack) {
                Text("Edit")
            }
            Button(onClick = {
                val filteredSteps = steps.filter { step -> step.products.isNotEmpty() }
                if (filteredSteps.isNotEmpty()) {
                    onFinish(filteredSteps)
                } else {
                    Toast.makeText(context, "Routine is empty! Add information to steps before finishing.", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Finish")
            }
        }
    }
}



@Composable
fun RoutineBuild(
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Your routine is build!",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Well done! Now you can perform build routine at any time and receive tips as you go.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 25.sp
        )

        FormButton(onFinish, "Finish")
    }
}

@Composable
fun RoutineFailed(
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Your routine is build failed!",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Sadly, there was error while building your routine. Please repeat build later.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 25.sp
        )

        FormButton(onFinish, "Return to routine page")
    }
}

@Composable
fun RoutineIsBuilding() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Your routine is building!",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Please wait.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 25.sp
        )
        LoadingSpinner()
    }
}




