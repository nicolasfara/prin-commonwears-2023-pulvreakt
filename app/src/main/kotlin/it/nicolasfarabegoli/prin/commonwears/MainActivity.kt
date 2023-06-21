package it.nicolasfarabegoli.prin.commonwears

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import it.nicolasfarabegoli.pulverization.runtime.PulverizationRuntime
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds

/**
 * Main activity.
 */
class MainActivity : ComponentActivity() {
    private val bluetoothManager by lazy {
        applicationContext.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
    }
    private val isBluetoothEnabled: Boolean
        get() {
            val btAdapter = bluetoothManager.adapter ?: return false
            return btAdapter.isEnabled
        }
    private val enableBluetoothRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                RESULT_OK -> Unit // startLogic(deviceId)
                else -> askToEnableBluetooth()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val displayViewModel = DisplayViewModel()
        setContent {
            AppLauncherRow(displayViewModel)
        }
    }

    @Composable
    @Suppress("FunctionNaming")
    private fun AppLauncherRow(display: DisplayViewModel) {
        Column(modifier = Modifier.padding(all = 7.dp)) {
            Row(modifier = Modifier
                .padding(all = 7.dp)
                .align(Alignment.CenterHorizontally)
                .height(IntrinsicSize.Max)) {
                var deviceIdText by remember { mutableStateOf(TextFieldValue("")) }
                TextField(
                    value = deviceIdText,
                    onValueChange = { deviceIdText = it },
                    modifier = Modifier
                        .width(150.dp)
                        .padding(10.dp, 0.dp),
                    label = { Text("Device ID")}
                )
                Button(
                    onClick = { startLogic(deviceIdText.text, display) },
                    enabled = deviceIdText.text != "",
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Text("Start")
                }
            }
            Row(modifier = Modifier.padding(all = 7.dp)) {
                Card(modifier = Modifier
                    .padding(all = 10.dp)
                    .fillMaxWidth()
                ) {
                    Text("Distance from target",
                        color = Color.Gray,
                        fontSize = 17.sp,
                        modifier = Modifier.padding(7.dp)
                    )
                    Text("%.1f meters".format(display.currentDistance),
                        fontSize = 21.sp,
                        fontWeight = FontWeight.W700,
                        modifier = Modifier.padding(7.dp).align(CenterHorizontally)
                    )
                }
            }
            Row {
                NeighboursList(display.neighbourDistances)
            }
        }
    }

    @Composable
    @Suppress("FunctionNaming")
    private fun NeighboursList(neighbours: Map<String, Double>) {
        Column {
            neighbours.forEach { ItemRow(value = it.toPair()) }
        }
    }

    @Composable
    @Suppress("FunctionNaming")
    private fun ItemRow(value: Pair<String, Double>) {
        Card(modifier = Modifier
            .padding(all = 10.dp)
            .fillMaxWidth()) {
            Column(modifier = Modifier.padding(all = 7.dp)) {
                Text("Device ${value.first}",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.W700,
                    modifier = Modifier.padding(7.dp)
                )
                Text("Distance from target: %.1f meters".format(value.second),
                    color = Color.Gray,
                    modifier = Modifier.padding(7.dp)
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (bluetoothManager.adapter != null) {
            if (!isBluetoothEnabled) {
                askToEnableBluetooth()
            }
        } else {
            Log.e(this::class.simpleName, "This device has not bluetooth hardware")
        }
    }

    private fun startLogic(deviceId: String, displayModel: DisplayViewModel) {
        checkPermissions {
            lifecycleScope.launch {
                val conf = androidRuntimeConfig(applicationContext, displayModel)
                val pulvreaktRuntime = PulverizationRuntime(deviceId, "android", conf)
                pulvreaktRuntime.start()
            }
        }
    }

    private fun askToEnableBluetooth() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        enableBluetoothRequest.launch(enableBtIntent)
    }
}
