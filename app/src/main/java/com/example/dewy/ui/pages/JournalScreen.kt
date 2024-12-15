package com.example.dewy.ui.pages

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.IDNA.Info
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.dewy.R
import com.example.dewy.data.models.Record
import com.example.dewy.viewmodels.JournalViewModel
import com.example.dewy.viewmodels.StreakViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun JournalPage(
    navHostController: NavHostController,
    journalViewModel: JournalViewModel
) {
    val toastMessage by journalViewModel.notification.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    var selectedTabIndex by remember { mutableStateOf(0) }

    Column {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
        ) {
                listOf("Weekly Record", "Track Progress").forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, style = MaterialTheme.typography.titleSmall) }
                    )
                }
            }

        when (selectedTabIndex) {
            0 -> AddRecordTab(journalViewModel)
            1 -> TrackProgressTab(journalViewModel)
        }
    }

}

@Composable
fun AddRecordTab(
    journalViewModel: JournalViewModel
) {
    val dayDifference by journalViewModel.dayDifference.collectAsState()
    if (dayDifference < -1) {
        EarlyRecord(dayDifference)
    } else {
        var isRecordSaved by remember { mutableStateOf(false) }

        if (isRecordSaved) {
            RecordSaved()
        } else {
            AddRecordForm(
                journalViewModel = journalViewModel,
                onRecordSaved = { isRecordSaved = true }
            )
        }
    }
}

@Composable
fun AddRecordForm(
    journalViewModel: JournalViewModel,
    onRecordSaved: () -> Unit
) {
    val tags by journalViewModel.tags.collectAsState()
    val context = LocalContext.current

    val currentDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
    var comment by remember { mutableStateOf("") }
    val selectedTags = remember { mutableStateListOf<String>() }
    var selectedImage by remember { mutableStateOf<Bitmap?>(null) }

    TabHeaderRow(
        text = "Time to add weekly record about your skin state!"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Date: $currentDate",
            style = MaterialTheme.typography.bodyMedium
        )

        JournalPhotoUpload(
            onImageSelected = { bitmap -> selectedImage = bitmap },
            selectedImage = selectedImage
        )

        JournalComment(
            value = comment,
            onValueChange = { newComment -> comment = newComment }
        )

        ImprovementSection(
            tags = tags!!,
            selectedTags = selectedTags
        )

        Button(
            onClick = {
                journalViewModel.addRecord(context, comment, selectedTags, selectedImage) { result ->
                    if (result) {
                        onRecordSaved()
                    }
                }
            },
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        ) {
            Text("Add record")
        }
    }
}

@Composable
fun EarlyRecord(days: Int){
    InfoCard {
        Text(
            text ="A bit too early!",
            style = MaterialTheme.typography.titleSmall,)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ){
            Text(
                text = "You will be able to add\nnew weekly report in",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = "${7-days} days!",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun RecordSaved(){
    InfoCard {
        Text(
            text ="Your report was saved!",
            style = MaterialTheme.typography.titleSmall,)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ){
            Text(
                text = "You will be able to find it as well as other record you made in",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Track Progress Tab",
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun EmptyProgress(){
    InfoCard {
        Text(
            text ="No records found!",
            style = MaterialTheme.typography.titleSmall,)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ){
            Text(
                text = "You are yet to add weekly report! You can do so in",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Weekly Record Tab",
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun JournalPhotoUpload(
    onImageSelected: (Bitmap?) -> Unit,
    selectedImage: Bitmap?
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        onImageSelected(bitmap)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            onImageSelected(bitmap)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .clickable { expanded = true },
            contentAlignment = Alignment.Center
        ) {
            if (selectedImage != null) {
                Image(
                    bitmap = selectedImage.asImageBitmap(),
                    contentDescription = "Uploaded Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Add Image", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "Add Photo", 
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            offset = DpOffset(110.dp, -180.dp)
        ) {
            DropdownMenuItem(
                text = { Text("Take photo") },
                onClick = {
                    expanded = false
                    cameraLauncher.launch(null) // Directly launch the camera
                }
            )
            DropdownMenuItem(
                text = { Text("Choose from gallery") },
                onClick = {
                    expanded = false
                    galleryLauncher.launch("image/*") // Directly launch the gallery
                }
            )
            DropdownMenuItem(
                text = { Text("Cancel") },
                onClick = { expanded = false }
            )
        }
    }
}

@Composable
fun JournalComment(
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Add comment:",
            style = MaterialTheme.typography.bodyMedium
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            keyboardOptions = KeyboardOptions.Default,
            keyboardActions = KeyboardActions.Default,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ImprovementSection(
    tags: List<String>,
    selectedTags: MutableList<String>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = "I saw improvements in...", style = MaterialTheme.typography.bodyMedium)

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tags.forEach { tag ->
                val isSelected = selectedTags.contains(tag)
                ImprovementTag(
                    tag = tag,
                    isSelected = isSelected,
                    onClick = {
                        if (isSelected) {
                            selectedTags.remove(tag)
                        } else {
                            selectedTags.add(tag)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ImprovementTag(
    tag: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceDim
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface

    Text(
        text = tag,
        style = MaterialTheme.typography.labelMedium,
        color = contentColor,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
            .padding(4.dp)
            .clickable { onClick() }
    )
}

@Composable
fun TrackProgressTab(
    journalViewModel: JournalViewModel
) {
    val records by journalViewModel.records.collectAsState()

    LaunchedEffect(Unit) {
        if (records == null) {
            journalViewModel.fetchUserRecords()
        }
    }

    if (records == null) {
        LoadingSpinner()
    } else if (records!!.isEmpty()) {
        EmptyProgress()
    } else {
        TabHeaderRow("Read your records to track your skin progress!")
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 16.dp)
        ) {
            item { StartPoint() }
            items(records!!) { record ->
                Row {
                    Timeline()
                    JournalRecordCard(record, journalViewModel)
                }
            }
            item { EndPoint() }
        }
    }
}


@Composable
fun TabHeaderRow(
    text: String,
    color: Color = MaterialTheme.colorScheme.primary
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text= text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}


@Composable
fun JournalRecordCard(record: Record,journalViewModel: JournalViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val comment = record.comment
    val tags = if (record.tags.isEmpty()) "None" else record.tags.joinToString(separator = ", ")

    val (formattedDate, year) = remember(record.date) {
        val localDate = LocalDate.parse(record.date)
        val year = localDate.year.toString()
        val formattedDate = localDate.format(
            DateTimeFormatter.ofPattern("dd MMM").withLocale(Locale.getDefault())
        )
        formattedDate to year
    }

    val filePath = "${context.filesDir}/${record.image_url}"
    val bitmap = BitmapFactory.decodeFile(filePath)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceDim,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .height(250.dp)
                    .wrapContentWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.padding(top = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(formattedDate, style = MaterialTheme.typography.displaySmall)
                    Text(year, style = MaterialTheme.typography.labelLarge)
                }

                Column(
                    modifier = Modifier
                        .height(50.dp)
                        .width(120.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (record.tags.isNotEmpty()){
                        Text(
                            text = "Improvements:",
                            style = MaterialTheme.typography.labelMedium,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = tags,
                            style = MaterialTheme.typography.labelMedium,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Justify
                        )
                    }
                }

                Button(
                    onClick = { showDialog = true },
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    Text("Read record")
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.secondaryContainer
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Record photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop,
                        alpha = 0.5f
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.sample),
                        contentDescription = "Placeholder",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop,
                        alpha = 0.5f
                    )
                }
            }
        }
    }

    if (showDialog) {
        RecordDetailsDialog(
            year,
            formattedDate,
            bitmap,
            comment,
            tags,
            onDismissRequest = { showDialog = false },
            onDelete = { journalViewModel.deleteRecord(record.date) }
        )
    }
}

@Composable
fun RecordDetailsDialog(
    year: String,
    date: String,
    bitmap: Bitmap?,
    comment: String?,
    tags: String,
    onDismissRequest: () -> Unit,
    onDelete: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Record Details",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Date: $date $year",
                    style = MaterialTheme.typography.titleSmall
                )

                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Record photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.FillHeight
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.sample),
                        contentDescription = "Placeholder",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.FillHeight
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ){
                    Text(
                        text = "Comment :",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = comment ?: "No comment",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ){
                    Text(
                        text = "Improvements :",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = tags,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                ){
                    Button(
                        onClick = {
                            onDelete()
                            onDismissRequest()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Delete")
                    }
                    Button(
                        onClick = onDismissRequest,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

@Composable
fun Timeline() {
    Box(
        modifier = Modifier.width(40.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(30.dp)
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
            )
            Icon(
                painter = painterResource(R.drawable.icon_right),
                contentDescription = "Record",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 4.dp)
            )
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(215.dp)
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
            )
        }
    }
}

@Composable
fun EndPoint() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 1.5.dp)
            .offset(y = (-30).dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Icon(
            painter = painterResource(R.drawable.icon_line_end),
            contentDescription = "End point",
            tint = MaterialTheme.colorScheme.tertiaryContainer,
            modifier = Modifier.rotate(90f),
        )
        Text(
            text = "Start of recording",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier.padding(end = 3.dp, start = 3.dp)
        )
    }
}

@Composable
fun StartPoint() {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 1.5.dp)
            .offset(y = 10.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.icon_line_start),
            contentDescription = "Start point",
            tint = MaterialTheme.colorScheme.tertiaryContainer,
        )
        Text(
            text = "Newest record",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier.padding(top = 3.dp, start = 3.dp)
        )
    }
}

