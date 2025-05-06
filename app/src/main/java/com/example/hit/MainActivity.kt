package com.example.hit

import android.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hit.ui.theme.HitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HitTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "start") {
        composable("start") {
            StartScr(
                onMyProjects = { navController.navigate("code") },
                onAbout = { showDialog.value = true }
            )
        }
        composable("code") {
            CodeScreen()
        }
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("О приложении") },
            text = { Text("Это простое приложение для визуального кодинга.") },
            confirmButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text("OK")
                }
            }
        )
    }
}

val showDialog = mutableStateOf(false)

@Composable
fun CodeScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Экран кодинга 🚀",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@Composable
fun StartScr(
    onMyProjects: () -> Unit,
    onAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF952CA1),
                        Color(0xFF6D60F8)
                    )
                )
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bilding grils \uD83D\uDCA1",
            fontSize = 36.sp,
            letterSpacing = 1.sp,
            lineHeight = 44.sp,
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFFFFFFFF)
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = onMyProjects,
            modifier = Modifier.height(50.dp).width(250.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4143E3),
                contentColor = Color(0xFFFFFFFF)
            ),
            shape = RoundedCornerShape(100.dp)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Начать кодить")
        }

        Spacer(modifier = Modifier.height(25.dp))

        Button(
            onClick = onAbout,
            modifier = Modifier.height(50.dp).width(250.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4143E3),
                contentColor = Color(0xFFFFFFFF)
            ),
            shape = RoundedCornerShape(100.dp)
        ) {
            Icon(Icons.Default.Info, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("О приложении")
        }
    }

}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HitTheme {
        StartScr(onMyProjects = {}, onAbout = {})
    }
}