package com.example.elibiza

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.elibiza.ui.theme.ElIbizaTheme
import com.example.elibiza.ui.theme.yellow
import java.io.OutputStream
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val permissionRequestCode = 123 // Puedes usar cualquier número entero
        conectarse(applicationContext,permissionRequestCode)
        /*if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Si no tienes permiso BLUETOOTH, debes solicitarlo aquí.
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                permissionRequestCode // Usar el código de solicitud definido
            )
            exito = false
        } else {
            try {
                val deviceAddress = "98:DA:60:0A:42:F5"
                val bluetoothDevice: BluetoothDevice? =
                    bluetoothAdapter!!.getRemoteDevice(deviceAddress)
                //println(bluetoothAdapter!!.bondedDevices)
                val uuid =
                    UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
                bluetoothSocket =
                    bluetoothDevice!!.createRfcommSocketToServiceRecord(uuid)
                bluetoothSocket!!.connect()
                //bluetoothSocket!!.close()
                exito = true
            } catch (e: Exception) {
                exito = false
            }
        }*/
        setContent {
            ElIbizaTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    TwoSliders(applicationContext)
                }
            }
        }
    }
}

private fun conectarse(context: Context, permissionRequestCode: Int){
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Si no tienes permiso BLUETOOTH, debes solicitarlo aquí.
            ActivityCompat.requestPermissions(
                Activity(),
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                permissionRequestCode // Usar el código de solicitud definido
            )
            conexion = false
        }else{
            try {
                val deviceAddress = "98:DA:60:0A:42:F5"
                val bluetoothDevice: BluetoothDevice? =
                    bluetoothAdapter!!.getRemoteDevice(deviceAddress)
                //println(bluetoothAdapter!!.bondedDevices)
                val uuid =
                    UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
                bluetoothSocket =
                    bluetoothDevice!!.createRfcommSocketToServiceRecord(uuid)
                bluetoothSocket!!.connect()
                conexion = true
            } catch (e: Exception) {
                conexion = false
            }
        }
}

private var conexion: Boolean = false

private fun enviarMensaje(mensaje: String) {
    try {
        val outputStream: OutputStream = bluetoothSocket!!.outputStream
        outputStream.write(mensaje.toByteArray())
    } catch (e: Exception) {
        conexion = false
    }
}

private var bluetoothSocket: BluetoothSocket? = null

private val bluetoothAdapter: BluetoothAdapter? by lazy {
    BluetoothAdapter.getDefaultAdapter()
}

@Composable
fun TwoSliders(context: Context) {
    var leftSliderPosition by remember { mutableFloatStateOf(0.5f) }
    var topSliderPosition by remember { mutableFloatStateOf(90.0f) }
    var luces by remember { mutableStateOf(false) }
    var conectado by remember { mutableStateOf(conexion) }

    Row(
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Slider(
                    value = leftSliderPosition,
                    valueRange = -255f..255f,
                    onValueChange = {
                        leftSliderPosition = it
                        try {
                            enviarMensaje("A$leftSliderPosition ")
                        } catch (e: Exception) {
                            conexion = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .rotate(270F)
                        .wrapContentSize(),
                    colors = SliderDefaults.colors(
                        thumbColor = yellow, activeTrackColor = Color.Transparent
                    )
                )
            }
        }
        Box(
            modifier = Modifier
                .weight(0.25f)
                .fillMaxHeight()
                .padding(16.dp),
        ) {
            val luz = if (luces) {
                R.drawable.luces_on
            } else {
                R.drawable.luces_off
            }
            val conn = if (conexion) {
                R.drawable.verde
            } else {
                R.drawable.rojo
            }
            Image(painter = painterResource(id = luz),
                contentDescription = null,
                modifier = Modifier
                    .size(25.dp)
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .clickable {
                        luces = !luces
                        if (luces) {
                            enviarMensaje("L1 ")
                        } else {
                            enviarMensaje("L0 ")
                        }
                    })
            Image(
                painter = painterResource(id = conn),
                contentDescription = null,
                modifier = Modifier
                    .size(25.dp)
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .clickable {
                        conectarse(context,123)
                    }
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Slider(
                    value = topSliderPosition,
                    valueRange = 0f..180f,
                    steps = 19,
                    onValueChange = {
                        topSliderPosition = it
                        try {
                            enviarMensaje("G$topSliderPosition ")
                        } catch (e: Exception) {
                            conexion = false
                        }
                    }, modifier = Modifier.width(300.dp), colors = SliderDefaults.colors(
                        thumbColor = yellow, activeTrackColor = Color.Transparent
                    )
                )
            }
        }
    }
}

