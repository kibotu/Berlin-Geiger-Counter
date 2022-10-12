@file:OptIn(ExperimentalMaterial3Api::class)

package net.kibotu.berlingeiger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.datetime.toJavaInstant
import net.kibotu.berlingeiger.features.GeigerViewModel
import net.kibotu.berlingeiger.features.MeasurementsStateHolder
import net.kibotu.berlingeiger.features.ddMMyyyy
import net.kibotu.berlingeiger.ui.theme.BerlinGeigerTheme

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
                ) {
                    MeasurementList(Modifier.padding(it), viewModel.state)
                }
            }
        }
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
