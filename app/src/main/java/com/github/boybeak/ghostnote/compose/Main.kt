package com.github.boybeak.ghostnote.compose

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
                            border = if (mainVM.isSelected(note)) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                            colors = if (mainVM.isSelected(note)) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)) else CardDefaults.cardColors()
                        ) {
                            Column (
                                modifier = Modifier.padding()
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
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
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
                        .background(Color.Transparent),
                    maxLines = 1,
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
                )
                HorizontalDivider()
                TextField(
                    createVM.text.value,
                    placeholder = {
                        Text("Text", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    },
                    onValueChange = {
                        createVM.text.value = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(256.dp)
                        .background(Color.Transparent),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
                )

                HorizontalDivider()
                Row {
                    /*FilledIconToggleButton(
                        checked = createVM.snap.value,

                        onCheckedChange = {
                            Log.d("aaa", "onCheckedChange it=$it")
                            createVM.snap.value = it
                        }
                    ) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "")
                    }*/
                    IconButton(
                        onClick = {
                            mainVM.dismissCreateDialog()
                        }
                    ) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "")
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