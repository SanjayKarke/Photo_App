package com.karkisanjay.photoapp

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.karkisanjay.photoapp.MainActivity.Companion.listNumber
import com.karkisanjay.photoapp.MainActivity.Companion.showDialog
import com.karkisanjay.photoapp.ui.theme.PhotoAppTheme
import kotlin.math.absoluteValue

class MainActivity : ComponentActivity() {

    companion object {
        var listNumber: MutableState<String?> = mutableStateOf(null)
        var showDialog = mutableStateOf(false)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhotoAppTheme {
                MainContent()

            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun MainContent() {

    var selectedImages by remember { mutableStateOf(listOf<Uri>()) }
    val context = LocalContext.current


    val ipLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetMultipleContents()) {
            selectedImages = if (it.size > 2) {
                it.take(2)
            } else {
                it
            }
        }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(modifier = Modifier.fillMaxSize(),
            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        if (listNumber.value.isNullOrEmpty()) {
                            Toast.makeText(
                                context,
                                "Please insert list size first.",
                                Toast.LENGTH_LONG
                            ).show()

                        } else {
                            ipLauncher.launch("image/*")

                        }
                    }) {

                        Text(text = "Select Images")

                    }

                    TextButton(onClick = {
                        showDialog.value = true
                    }) {
                        Text(text = listNumber.value ?: "Insert Number")
                    }
                }

            }) { innerPadding ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {

                if (selectedImages.isNotEmpty() && selectedImages.size >= 2) {
                    LazyVerticalGrid(columns = GridCells.Fixed(3)) {
                        listNumber.value?.let {
                            try {
                                items(count = it.toInt()) {

                                    if (isTriangularNumber(it.absoluteValue + 1)) {
                                        Image(
                                            painter = rememberImagePainter(data = selectedImages.first()),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(200.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Image(
                                            painter = rememberImagePainter(data = selectedImages.last()),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(200.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                    }


                                }
                            } catch (e: Exception) {
                                println(e)
                            }

                        }
                    }
                } else {
                    Text(text = "Please Select At least 2 Images")
                }


            }
            if (showDialog.value) {
                NumberDialog()
            }
        }
    }
}

@Composable
fun NumberDialog() {
    val contexts = LocalContext.current

    Dialog(onDismissRequest = {
        showDialog.value = false
    }, properties = DialogProperties(dismissOnClickOutside = false, dismissOnBackPress = true)) {

        Surface {
            Box(modifier = Modifier.size(250.dp), contentAlignment = Alignment.Center) {
                Column {
                    OutlinedTextField(
                        value = listNumber.value ?: "", onValueChange = {
                                listNumber.value = it

                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Button(onClick = {
                        showDialog.value = false
                        if(listNumber.value.isNullOrEmpty()){
                            listNumber.value = null

                        }
                    }) {
                        Text(text = "Done")
                    }
                }
            }
        }


    }


}

fun isTriangularNumber(x: Int): Boolean {
    if (x < 0) return false

    val discriminant = 1 + 8 * x
    val sqrtDiscriminant = Math.sqrt(discriminant.toDouble()).toInt()

    if (sqrtDiscriminant * sqrtDiscriminant != discriminant) {
        return false
    }

    return (-1 + sqrtDiscriminant) % 2 == 0
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    PhotoAppTheme {
        MainContent()
    }
}