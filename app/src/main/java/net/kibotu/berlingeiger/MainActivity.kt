package net.kibotu.berlingeiger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import net.kibotu.berlingeiger.features.GeigerViewModel
import net.kibotu.berlingeiger.ui.theme.BerlinGeigerTheme
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : ComponentActivity() {

    private val viewModel: GeigerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.loadDay(formatter.format(Clock.System.now().toJavaInstant()))

        setContent {
            BerlinGeigerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
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

var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    .withLocale(Locale.GERMANY)
    .withZone(ZoneId.systemDefault())
