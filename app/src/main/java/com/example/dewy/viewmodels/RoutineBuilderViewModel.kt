package com.example.dewy.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dewy.data.models.RoutineStep
import com.example.dewy.data.repositories.RoutineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.State
import com.example.dewy.data.models.Product
import com.example.dewy.data.models.Routine
import com.example.dewy.data.models.RoutineDay


class RoutineBuilderViewModel(
    private val repository: RoutineRepository = RoutineRepository()
) : ViewModel() {

    private val _isBuilding = mutableStateOf(false)
    val isBuilding: State<Boolean> get() = _isBuilding

    // Used to toggle day placements to make algorithm spreading product more evenly
    private var currentTwoDayStartIndex = 0
    private var currentThreeDayStartIndex = 0

    fun buildRoutine(
        type: String,
        time: String,
        steps: List<RoutineStep>,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isBuilding.value = true

                val stepNames = steps.map { it.stepName }
                val routineDays = Array(7) { dayIndex ->
                    RoutineDay(
                        dayName = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")[dayIndex],
                        steps = initializeRoutineSteps(stepNames)
                    )
                }

                // Process all steps and place products
                steps.forEachIndexed{ stepIndex, step ->
                    for (product in step.products){
                        when (product.frequency) {
                            7 -> placeOnAllDays(routineDays, product, stepIndex)
                            3 -> placeOnThreeDays(routineDays, product, stepIndex)
                            2 -> placeOnTwoDays(routineDays, product, stepIndex)
                            1 -> placeOnOneDay(routineDays, product, stepIndex)
                        }
                    }
                }

                // Delete steps with no products in them
                deleteEmptySteps(routineDays)


                val routine = Routine(
                    type = type,
                    preferredTime = time,
                    days = routineDays
                )

                val success = repository.saveRoutine(routine)
                if (success)
                    for (days in routine.days){
                        println("${days.dayName} - ${days.steps}")
                    }
                onResult(success)
            } catch (e: Exception) {
                println("Error building routine: ${e.message}")
                onResult(false)
            } finally {
                _isBuilding.value = false
            }
        }
    }

    /**
     * Dynamically initializes the list of steps for each day based on the given step names.
     */
    private fun initializeRoutineSteps(stepNames: List<String>): MutableList<RoutineStep> {
        return stepNames.map { stepName ->
            RoutineStep(
                stepName = stepName,
                products = mutableListOf()
            )
        }.toMutableList()
    }

    /**
     * Places the product on all 7 days.
     */
    private fun placeOnAllDays(routineDays: Array<RoutineDay>, product: Product, stepIndex: Int) {
        routineDays.forEach { day ->
            day.steps[stepIndex].products.add(product)
        }
    }

    /**
     * Places the product on 3 days of the week.
     */
    private fun placeOnThreeDays(routineDays: Array<RoutineDay>, product: Product,  stepIndex: Int) {
        val options = listOf(
            listOf(0, 3, 5), // Mon, Thu, Sat
            listOf(1, 4, 6), // Tue, Fri, Sun
            listOf(0, 2, 4)  // Mon, Wed, Fri (extra option)
        )

        for (i in options.indices){
            val optionIndex = (currentThreeDayStartIndex + i) % options.size
            val option = options[optionIndex]

            if (areDaysAvailableForPlacement(routineDays, option, product, stepIndex)) {
                placeProductOnDays(routineDays, option, product, stepIndex)
                break
            }
        }

        currentThreeDayStartIndex = (currentThreeDayStartIndex + 1) % options.size
    }

    /**
     * Places the product on 2 days of the week.
     */
    private fun placeOnTwoDays(routineDays: Array<RoutineDay>, product: Product, stepIndex: Int) {
        val options = listOf(
            listOf(1, 5), // Tue, Sat
            listOf(0, 3), // Mon, Thu (better spread than Mon, Fri)
            listOf(2, 6), // Wed, Sun
            listOf(0, 4)  // Mon, Fri (use sparingly)
        )


        for (i in options.indices) {
            val optionIndex = (currentTwoDayStartIndex + i) % options.size
            val option = options[optionIndex]

            if (areDaysAvailableForPlacement(routineDays, option, product, stepIndex)) {
                placeProductOnDays(routineDays, option, product, stepIndex)
                break
            }
        }

        currentTwoDayStartIndex = (currentTwoDayStartIndex + 1) % options.size
    }

    /**
     * Places the product on 1 day (choose day with least products for the given step).
     */
    private fun placeOnOneDay(routineDays: Array<RoutineDay>, product: Product, stepIndex: Int) {
        val dayWithFewestProducts = routineDays.minByOrNull { day ->
            day.steps[stepIndex].products.size
        } ?: routineDays[4]

        dayWithFewestProducts.steps[stepIndex].products.add(product)
    }

    /**
     * Places the product on the given days.
     */
    private fun placeProductOnDays(routineDays: Array<RoutineDay>, days: List<Int>, product: Product, stepIndex: Int) {
        days.forEach { dayIndex ->
            routineDays[dayIndex].steps[stepIndex].products.add(product)
        }
    }

    /**
     * Checks if product can be placed on specific days.
     */
    private fun areDaysAvailableForPlacement(
        routineDays: Array<RoutineDay>,
        days: List<Int>,
        product: Product,
        stepIndex: Int
    ): Boolean {
        return days.all { dayIndex ->
            val step = routineDays[dayIndex].steps[stepIndex]
            step.products.size < 2
        }
    }

    private fun deleteEmptySteps(routineDays: Array<RoutineDay>) {
        routineDays.forEach { day ->
            day.steps = day.steps.filter { step ->
                step.products.isNotEmpty()
            }.toMutableList()
        }
    }
}
