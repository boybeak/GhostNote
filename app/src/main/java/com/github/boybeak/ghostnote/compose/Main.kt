package com.github.boybeak.ghostnote.compose

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.IntState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.boybeak.ghostnote.R
import com.github.boybeak.ghostnote.vm.CreateVM
import com.github.boybeak.ghostnote.vm.MainVM

@Composable
fun StaggeredVerticalGrid(
    modifier: Modifier = Modifier,
    columns: Int = 2,
    horizontalSpacing: Dp = 0.dp,
    verticalSpacing: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val horizontalSpacingPx = horizontalSpacing.roundToPx()
        val verticalSpacingPx = verticalSpacing.roundToPx()

        // 计算每列宽度
        val availableWidth = constraints.maxWidth - (columns - 1) * horizontalSpacingPx
        val columnWidth = availableWidth / columns

        // 测量所有子项，固定宽度为列宽
        val itemConstraints = constraints.copy(
            minWidth = columnWidth,
            maxWidth = columnWidth,
            minHeight = 0
        )
        val placeables = measurables.map { it.measure(itemConstraints) }

        // 跟踪每列的高度
        val columnHeights = IntArray(columns) { 0 }
        val itemPositions = mutableListOf<Pair<Int, Int>>()

        placeables.forEach { placeable ->
            // 找到当前最短的列
            val minColumn = columnHeights.withIndex().minByOrNull { it.value }?.index ?: 0
            val currentY = columnHeights[minColumn]

            // 记录子项位置
            itemPositions.add(minColumn to currentY)

            // 更新列高度（考虑垂直间距）
            /*columnHeights[minColumn] = currentY + placeable.height +
                    if (currentY > 0) verticalSpacingPx else 0*/
            columnHeights[minColumn] = currentY + placeable.height + verticalSpacingPx
        }

        // 计算总高度
        val totalHeight = columnHeights.maxOrNull() ?: 0

        // 布局
        layout(constraints.maxWidth, totalHeight) {
            itemPositions.forEachIndexed { index, (column, y) ->
                val x = column * (columnWidth + horizontalSpacingPx)
                placeables[index].place(x, y)
            }
        }
    }
}

@Composable
fun MainView() {
//    val context = LocalContext.current
    val mainVM: MainVM = viewModel()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (mainVM.isSelectedMode) {
                        mainVM.deleteSelectedNotes()
                    } else {
                        mainVM.showCreateDialog()
                    }
                },
            ) {
                if (mainVM.isSelectedMode) {
                    Icon(Icons.Default.Delete, contentDescription = "")
                } else {
                    Icon(Icons.Default.Add, contentDescription = "")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (mainVM.hasNotes) {
                StaggeredVerticalGrid(
                    modifier = Modifier.padding(8.dp),
                    horizontalSpacing = 8.dp,
                    verticalSpacing = 8.dp
                ) {
                    mainVM.notes.forEach { note ->
                        Card(
                            modifier = Modifier
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = {
                                            Log.d("AAA", "onTap")
                                            if (mainVM.isSelectedMode) {
                                                mainVM.toggleSelectNote(note)
                                            }
                                        },
                                        onLongPress = {
                                            mainVM.toggleSelectNote(note)
                                            // 处理长按事件
                                        }
                                    )
                                },
                            border = if (mainVM.isSelected(note)) BorderStroke(
                                2.dp,
                                MaterialTheme.colorScheme.primary
                            ) else null
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = if (mainVM.isSelected(note)) MaterialTheme.colorScheme.primary.copy(
                                            alpha = 0.3f
                                        ) else Color(note.bgColor)
                                    )
                                    .padding()
                            ) {
                                if (note.title.isNotBlank()) {
                                    Text(
                                        note.title,
                                        modifier = Modifier.padding(8.dp),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                if (note.text.isNotBlank()) {
                                    Text(
                                        note.text,
                                        modifier = Modifier.padding(8.dp),
                                        fontSize = note.textSize.sp
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Image(
                    painter = painterResource(R.drawable.ic_ghost_outline),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(top = 120.dp)
                        .fillMaxWidth(fraction = 0.5f)
                        .aspectRatio(1f),
                    colorFilter = ColorFilter.tint(
                        color = MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.6f
                        )
                    )
                )
            }
        }


        if (mainVM.showCreateDialog.value) {
            CreateDialog()
        }
    }
}

@Composable
fun CreateDialog() {
    val context = LocalContext.current
    val mainVM: MainVM = viewModel()
    val createVM: CreateVM = viewModel()

    Dialog(onDismissRequest = {
//        mainVM.dismissCreateDialog()
    }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column {
                TextField(
                    createVM.title.value,
                    textStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    placeholder = {
                        Text(
                            "Title",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    onValueChange = {
                        createVM.title.value = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color(createVM.bgColor.intValue)),
                    maxLines = 1,
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(createVM.bgColor.intValue),
                        focusedContainerColor = Color(createVM.bgColor.intValue),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
                )
                HorizontalDivider()
                TextField(
                    createVM.text.value,
                    placeholder = {
                        Text(
                            "Text",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontSize = createVM.textSize.floatValue.sp
                        )
                    },
                    onValueChange = {
                        createVM.text.value = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(256.dp)
                        .background(Color.Transparent),
                    shape = RectangleShape,
                    textStyle = TextStyle(
                        fontSize = createVM.textSize.floatValue.sp
                    ),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(createVM.bgColor.intValue),
                        focusedContainerColor = Color(createVM.bgColor.intValue),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
                )
                AnimatedVisibility(
                    visible = createVM.showAnyConfig
                ) {
                    HorizontalDivider()
                }
                AnimatedVisibility(
                    visible = createVM.showColorPicker
                ) {
                    ColorPicker(createVM.bgColor.intValue) { color ->
                        createVM.bgColor.intValue = color
                    }
                }
                AnimatedVisibility(
                    visible = createVM.showTextSizeSlider
                ) {
                    Slider(
                        value = createVM.textSize.floatValue,
                        valueRange = 10f..24f,
                        onValueChange = {
                            createVM.textSize.floatValue = it
                        },
                        onValueChangeFinished = {
                        }
                    )
                }
                HorizontalDivider()
                Row {
                    IconButton(
                        onClick = {
                            mainVM.dismissCreateDialog()
                        }
                    ) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "")
                    }
                    FilledTonalIconToggleButton(
                        checked = createVM.showColorPicker,
                        onCheckedChange = { checked ->
                            if (checked) {
                                createVM.showColorPicker()
                            } else {
                                createVM.dismissColorPicker()
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_palette),
                            contentDescription = ""
                        )
                    }
                    FilledTonalIconToggleButton(
                        checked = createVM.showTextSizeSlider,
                        onCheckedChange = { checked ->
                            if (checked) {
                                createVM.showTextSizeSlider()
                            } else {
                                createVM.dismissTextSizeSlider()
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_format_size),
                            contentDescription = ""
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = {
                            mainVM.dismissCreateDialog()
                        }
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "")
                    }
                    IconButton(
                        onClick = {
                            createVM.create(context)
                            mainVM.dismissCreateDialog()
//                            mainVM.getNotes(context)
                        },
                        enabled = createVM.isReady
                    ) {
                        Icon(Icons.Default.Done, contentDescription = "")
                    }
                }
            }
        }

    }
}

@Composable
fun ColorPicker(color: Int, onChanged: (color: Int) -> Unit) {
    val context = LocalContext.current
    val colors = intArrayOf(
        Color.Transparent.toArgb(),
        context.getColor(android.R.color.holo_green_light),
        context.getColor(android.R.color.holo_blue_light),
        context.getColor(android.R.color.holo_orange_light),
        context.getColor(android.R.color.holo_red_light),
        context.getColor(android.R.color.holo_purple),
    )
    var cc by remember { mutableIntStateOf(color) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        colors.forEach { c ->
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable {
                        cc = c
                        onChanged.invoke(c)
                    }
                    .background(color = Color(c), shape = CircleShape)
                    .border(
                        width = if (cc == c) 2.dp else 1.dp,
                        color = if (cc == c) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (c == cc) {
                    Icon(Icons.Default.Check, contentDescription = "")
                }
            }
        }
    }
}