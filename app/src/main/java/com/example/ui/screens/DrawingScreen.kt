package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.SavedDrawingEntity
import com.example.data.model.KidsDataProvider

data class DrawPath(
    val path: Path,
    val color: Color,
    val strokeWidth: Float
)

val kidPalette = listOf(
    Color(0xFFFF1744), // Red
    Color(0xFFFF9100), // Orange
    Color(0xFFFFEA00), // Yellow
    Color(0xFF00E676), // Green
    Color(0xFF00B0FF), // Blue
    Color(0xFF651FFF), // Purple
    Color(0xFFFF4081), // Pink
    Color(0xFF8D6E63), // Brown
    Color(0xFF212121), // Black
    Color(0xFFFFFFFF), // White / Eraser
    Color(0xFF00E5FF), // Cyan
    Color(0xFF76FF03)  // Lime
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingScreen(
    savedDrawings: List<SavedDrawingEntity>,
    onBackClick: () -> Unit,
    onSaveArtwork: (String, String) -> Unit,
    onDeleteArtwork: (Int) -> Unit,
    onSpeak: (String) -> Unit
) {
    val paths = remember { mutableStateListOf<DrawPath>() }
    var renderTrigger by remember { mutableIntStateOf(0) }
    var currentColor by remember { mutableStateOf(kidPalette[0]) }
    var currentStrokeWidth by remember { mutableFloatStateOf(16f) }
    var isEraser by remember { mutableStateOf(false) }
    var isFillMode by remember { mutableStateOf(false) }
    var bgColor by remember { mutableStateOf(Color.White) }

    var selectedTemplateIndex by remember { mutableStateOf(0) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showGalleryDialog by remember { mutableStateOf(false) }
    var artworkTitle by remember { mutableStateOf("My Little Masterpiece") }

    val templates = KidsDataProvider.coloringPages

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = { Text(text = "Drawing & Coloring", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (paths.isNotEmpty()) {
                                paths.removeAt(paths.size - 1)
                                renderTrigger++
                            }
                        },
                        modifier = Modifier.testTag("undo_button")
                    ) {
                        Icon(Icons.Default.Undo, contentDescription = "Undo", tint = Color.White)
                    }

                    IconButton(
                        onClick = { showGalleryDialog = true },
                        modifier = Modifier.testTag("gallery_button")
                    ) {
                        Icon(Icons.Default.Image, contentDescription = "My Gallery", tint = Color.White)
                    }

                    IconButton(
                        onClick = { showSaveDialog = true },
                        modifier = Modifier.testTag("save_artwork_button")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save Artwork", tint = Color(0xFFFFD600))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Template selector bar
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (selectedTemplateIndex == -1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                selectedTemplateIndex = -1
                                paths.clear()
                                bgColor = Color.White
                                renderTrigger++
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(text = "✨ Blank Canvas", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                items(templates.indices.toList()) { idx ->
                    val tmpl = templates[idx]
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (selectedTemplateIndex == idx) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                selectedTemplateIndex = idx
                                paths.clear()
                                renderTrigger++
                                onSpeak("Coloring page ${tmpl.title}")
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(text = "${tmpl.svgIcon} ${tmpl.title}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            // Canvas drawing area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(bgColor)
                    .pointerInput(isFillMode, isEraser, currentColor, currentStrokeWidth) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                if (isFillMode) {
                                    bgColor = currentColor
                                    renderTrigger++
                                } else {
                                    val newPath = Path().apply { moveTo(offset.x, offset.y) }
                                    paths.add(
                                        DrawPath(
                                            path = newPath,
                                            color = if (isEraser) bgColor else currentColor,
                                            strokeWidth = if (isEraser) currentStrokeWidth * 2 else currentStrokeWidth
                                        )
                                    )
                                    renderTrigger++
                                }
                            },
                            onDrag = { change, _ ->
                                if (!isFillMode && paths.isNotEmpty()) {
                                    paths.last().path.lineTo(change.position.x, change.position.y)
                                    renderTrigger++
                                }
                            }
                        )
                    }
                    .testTag("drawing_canvas")
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Read renderTrigger state to subscribe Canvas recomposition to drag ticks
                    @Suppress("UNUSED_VARIABLE")
                    val trigger = renderTrigger

                    // Draw preset template outline if selected
                    if (selectedTemplateIndex in templates.indices) {
                        // Draw central outline
                        drawCircle(
                            color = Color.LightGray,
                            radius = size.minDimension / 3f,
                            center = center,
                            style = Stroke(width = 8f)
                        )
                    }

                    // Render user paths
                    paths.forEach { drawPath ->
                        drawPath(
                            path = drawPath.path,
                            color = drawPath.color,
                            style = Stroke(
                                width = drawPath.strokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
            }

            // Palette & Tools Toolbar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Color Palette Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(kidPalette) { color ->
                            val isSelected = !isEraser && currentColor == color
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        currentColor = color
                                        isEraser = false
                                        isFillMode = false
                                    }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tools Row (Brush, Eraser, Fill Bucket, Brush Sizes)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Brush Tool
                            IconButton(
                                onClick = {
                                    isEraser = false
                                    isFillMode = false
                                },
                                modifier = Modifier
                                    .background(
                                        if (!isEraser && !isFillMode) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                        CircleShape
                                    )
                                    .testTag("tool_brush")
                            ) {
                                Icon(Icons.Default.Brush, contentDescription = "Brush", tint = MaterialTheme.colorScheme.primary)
                            }

                            // Fill Tool
                            IconButton(
                                onClick = {
                                    isFillMode = true
                                    isEraser = false
                                },
                                modifier = Modifier
                                    .background(
                                        if (isFillMode) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                        CircleShape
                                    )
                                    .testTag("tool_fill")
                            ) {
                                Icon(Icons.Default.FormatColorFill, contentDescription = "Fill Bucket", tint = MaterialTheme.colorScheme.primary)
                            }

                            // Clear Canvas
                            IconButton(
                                onClick = {
                                    paths.clear()
                                    bgColor = Color.White
                                },
                                modifier = Modifier.testTag("tool_clear")
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Clear All", tint = Color.Red)
                            }
                        }

                        // Brush size presets
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(8f, 16f, 28f).forEach { size ->
                                Surface(
                                    shape = CircleShape,
                                    color = if (currentStrokeWidth == size) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .clickable { currentStrokeWidth = size }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Box(
                                            modifier = Modifier
                                                .size((size / 2).dp)
                                                .background(
                                                    if (currentStrokeWidth == size) Color.White else Color.Gray,
                                                    CircleShape
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Save Artwork Dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save Your Artwork 🎨", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Give a title to your creation and earn 20 bonus coins! 🪙")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = artworkTitle,
                        onValueChange = { artworkTitle = it },
                        label = { Text("Artwork Name") },
                        modifier = Modifier.fillMaxWidth().testTag("artwork_title_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSaveDialog = false
                        onSaveArtwork(artworkTitle, "path_count:${paths.size}")
                    },
                    modifier = Modifier.testTag("confirm_save_artwork")
                ) {
                    Text("Save & Earn Coins 🪙")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Gallery Dialog
    if (showGalleryDialog) {
        AlertDialog(
            onDismissRequest = { showGalleryDialog = false },
            title = { Text("My Artwork Gallery 🖼️", fontWeight = FontWeight.Bold) },
            text = {
                if (savedDrawings.isEmpty()) {
                    Text("No saved drawings yet! Draw something cool and tap Save.")
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        savedDrawings.forEach { drawing ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = drawing.title, fontWeight = FontWeight.Bold)
                                        Text(text = "Created artwork", fontSize = 12.sp, color = Color.Gray)
                                    }
                                    IconButton(onClick = { onDeleteArtwork(drawing.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showGalleryDialog = false }) { Text("Close") }
            }
        )
    }
}
