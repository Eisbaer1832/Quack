package com.polyphoneTerzen.quack

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.polyphoneTerzen.quack.navigation.AppNavigation
import com.polyphoneTerzen.quack.navigation.Topbar
import com.polyphoneTerzen.quack.ui.theme.BrainsToolboxTheme
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BrainsToolboxTheme {
                HomeScreen()
                AppNavigation()
                Topbar()
            }
        }
    }
}

data class Response(
    val message: String,
    val url: String,
)

fun fetchDuckImage(onImageFetched: (String) -> Unit) {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://random-d.uk/api/v2/random")
        .build()

    client.newCall(request).enqueue(object : okhttp3.Callback {
        override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
            if (response.isSuccessful) {
                response.body?.let { responseBody ->
                    val gson = Gson()
                    val apiResponse = gson.fromJson(responseBody.string(), Response::class.java)
                    onImageFetched(apiResponse.url)
                }
            }
        }

        override fun onFailure(call: okhttp3.Call, e: IOException) {
            println("Error: ${e.message}")
        }
    })
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val url = remember { mutableStateOf("https://random-d.uk/api/136.jpg") }
    fetchDuckImage { newUrl ->
        url.value = newUrl
    }
    Scaffold {
        Column(
            Modifier
                .padding(top = 65.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                    .horizontalScroll(rememberScrollState()),
                    model = url.value, // Use the value of the MutableState
                    contentDescription = "image of a duck",
                    contentScale = ContentScale.FillHeight
                )
            }
        }

        FAB(url) // Pass the MutableState to the FAB
    }
}

@Composable
fun FAB(url: MutableState<String>) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
    ) {
        Row(modifier = Modifier.align(Alignment.BottomEnd)) {
            LargeFloatingActionButton(onClick = {
                // Call the fetchDuckImage function and update the state
                fetchDuckImage { newUrl ->
                    url.value = newUrl
                }
            }) {
                Icon(Icons.Filled.Refresh, contentDescription = "Refresh Button")
            }
        }
    }
}
