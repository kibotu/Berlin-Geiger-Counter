@file:OptIn(ExperimentalMaterial3Api::class)

package net.kibotu.berlingeiger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.datetime.toJavaInstant
import net.kibotu.berlingeiger.features.GeigerViewModel
import net.kibotu.berlingeiger.features.MeasurementsStateHolder
import net.kibotu.berlingeiger.features.ddMMyyyy
import net.kibotu.berlingeiger.ui.theme.BerlinGeigerTheme
import timber.log.Timber
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {

    private val viewModel: GeigerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            BerlinGeigerTheme {

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(text = "Today")
                            }
                        )
                    }
                ) { paddingValues ->

//                    DrawCubic(Modifier.padding(paddingValues))

                    DrawGraph(Modifier.padding(paddingValues), viewModel.state)

                    // MeasurementList(Modifier.padding(it), viewModel.state)
                }
            }
        }
    }
}


@Composable
private fun DrawGraph(modifier: Modifier, state: MeasurementsStateHolder) {

    val measurements by state.measurements

    val maxMeasurement = measurements.maxByOrNull { it.usvh }
    Timber.v("max measurement $maxMeasurement ")

    val density = LocalDensity.current.density
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = screenWidth * 3f / 4f
    val screenWidthInPx = screenWidth.value * density
    val screenHeightInPx = screenHeight.value * density

    val path = remember { Path() }

    /**
     * (0,0) ----------------- (screenWidthInPx, 0)
     * |                                    |
     * (0,screenHeightInPx) -- (screenWidthInPx,screenHeightInPx)
     */

    /**
     * x=0 -> 00:00
     * x=100% -> 24:00
     *
     * y=100 -> max(measurement)
     */

    Canvas(
        modifier = modifier
            .padding(16.dp)
            .shadow(4.dp)
            .background(Color(0xFFEFEFEF))
            .size(screenWidth, screenHeight)
    ) {

        // x-axe
        drawLine(
            color = Color.Black,
            start = Offset(0f, screenHeightInPx),
            end = Offset(screenWidthInPx, screenHeightInPx),
            strokeWidth = 2.dp.toPx()
        )

        // y-axe
        drawLine(
            color = Color.Black,
            start = Offset(0f, 0f),
            end = Offset(0f, screenHeightInPx),
            strokeWidth = 2.dp.toPx()
        )
    }
}

@Composable
fun MeasurementList(modifier: Modifier, state: MeasurementsStateHolder) {

    val measurements by state.measurements

    LazyColumn(modifier = modifier) {
        items(measurements.reversed(), key = { it.date.toString() }) { item ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Text(text = ddMMyyyy.format(item.date?.toJavaInstant()))
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "${item.cpm}")
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "${item.usvh}")
            }

            Divider(
                modifier = Modifier
                    .background(Color(0xFFF4F4F4))
                    .fillMaxWidth()
                    .height(1.dp)
            )
        }
    }
}

@Composable
fun DrawCubic(modifier: Modifier) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        val density = LocalDensity.current.density

        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp

        val screenWidthInPx = screenWidth.value * density

        // (x0, y0) is initial coordinate where path is moved with path.moveTo(x0,y0)
        var x0 by remember { mutableStateOf(0f) }
        var y0 by remember { mutableStateOf(0f) }

        /*
        Adds a cubic bezier segment that curves from the current point(x0,y0) to the
        given point (x3, y3), using the control points (x1, y1) and (x2, y2).
     */
        var x1 by remember { mutableStateOf(0f) }
        var y1 by remember { mutableStateOf(screenWidthInPx) }
        var x2 by remember { mutableStateOf(screenWidthInPx / 2) }
        var y2 by remember { mutableStateOf(0f) }

        var x3 by remember { mutableStateOf(screenWidthInPx) }
        var y3 by remember { mutableStateOf(screenWidthInPx / 2) }

        val path = remember { Path() }
        Canvas(
            modifier = Modifier
                .padding(8.dp)
                .shadow(1.dp)
                .background(Color.White)
                .size(screenWidth, screenWidth / 2)
        ) {
            path.reset()
            path.moveTo(x0, y0)
            path.cubicTo(x1 = x1, y1 = y1, x2 = x2, y2 = y2, x3 = x3, y3 = y3)


            drawPath(
                color = Color.Green,
                path = path,
                style = Stroke(
                    width = 3.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                )
            )

            // Draw Control Points on screen
            drawPoints(
                listOf(Offset(x1, y1), Offset(x2, y2)),
                color = Color.Green,
                pointMode = PointMode.Points,
                cap = StrokeCap.Round,
                strokeWidth = 40f
            )
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {

            Text(text = "X0: ${x0.roundToInt()}")
            Slider(
                value = x0,
                onValueChange = { x0 = it },
                valueRange = 0f..screenWidthInPx,
            )

            Text(text = "Y0: ${y0.roundToInt()}")
            Slider(
                value = y0,
                onValueChange = { y0 = it },
                valueRange = 0f..screenWidthInPx,
            )

            Text(text = "X1: ${x1.roundToInt()}")
            Slider(
                value = x1,
                onValueChange = { x1 = it },
                valueRange = 0f..screenWidthInPx,
            )

            Text(text = "Y1: ${y1.roundToInt()}")
            Slider(
                value = y1,
                onValueChange = { y1 = it },
                valueRange = 0f..screenWidthInPx,
            )

            Text(text = "X2: ${x2.roundToInt()}")
            Slider(
                value = x2,
                onValueChange = { x2 = it },
                valueRange = 0f..screenWidthInPx,
            )

            Text(text = "Y2: ${y2.roundToInt()}")
            Slider(
                value = y2,
                onValueChange = { y2 = it },
                valueRange = 0f..screenWidthInPx,
            )

            Text(text = "X3: ${x3.roundToInt()}")
            Slider(
                value = x3,
                onValueChange = { x3 = it },
                valueRange = 0f..screenWidthInPx,
            )

            Text(text = "Y3: ${y3.roundToInt()}")
            Slider(
                value = y3,
                onValueChange = { y3 = it },
                valueRange = 0f..screenWidthInPx,
            )
        }
    }
}