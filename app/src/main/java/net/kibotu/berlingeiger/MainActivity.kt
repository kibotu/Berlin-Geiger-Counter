package net.kibotu.berlingeiger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.kibotu.berlingeiger.features.GeigerViewModel
import net.kibotu.berlingeiger.ui.theme.BerlinGeigerTheme

class MainActivity : ComponentActivity() {

    private val viewModel: GeigerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BerlinGeigerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val measurements by viewModel.state.measurements

                    LazyColumn {
                        items(measurements, key = { it.date.toString() }) { item ->

                            Row(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                                Text(text = "${item.date}")
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
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BerlinGeigerTheme {
        Greeting("Android")
    }
}