package com.example.hit


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
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


val fontSt = FontFamily(Font(com.example.hit.R.font.fredoka))

@Composable
fun StartScr(
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colorResource(R.color.pink_001),
                        colorResource(R.color.purple_001),
                    )
                )
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.nameApp),
            fontSize = 36.sp,
            letterSpacing = 1.sp,
            lineHeight = 44.sp,
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = colorResource(R.color.white_001),
            fontFamily = fontSt
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { navController.navigate(Destinations.CODE_SCREEN) },
            modifier = Modifier
                .height(50.dp)
                .width(250.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.purple_001),
                contentColor = colorResource(R.color.white_001),
            ),
            shape = RoundedCornerShape(100.dp)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.start_coding_button),
                fontFamily = fontSt,
                fontSize = 24.sp
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        Button(
            onClick = { showDialog.value = true },
            modifier = Modifier
                .height(50.dp)
                .width(250.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.purple_001),
                contentColor = colorResource(R.color.white_001),
            ),
            shape = RoundedCornerShape(100.dp)
        ) {
            Icon(Icons.Default.Info, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.about_app_button),
                fontFamily = fontSt,
                fontSize = 24.sp
            )
        }
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = {
                    Text(
                        text = stringResource(R.string.about_app_button),
                        fontFamily = fontSt
                    )
                },
                text = { Text(text = stringResource(R.string.inAbout), fontFamily = fontSt) },
                confirmButton = {
                    TextButton(onClick = { showDialog.value = false }) {
                        Text(stringResource(R.string.Ok), fontFamily = fontSt)
                    }
                }
            )
        }
    }
}


