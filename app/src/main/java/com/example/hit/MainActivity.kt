package com.example.hit

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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hit.ui.theme.HitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HitTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    StartScr(
                        onNewProject = {},
                        onMyProjects = {},
                        onAbout = {},
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun StartScr(
    onNewProject: () -> Unit,
    onMyProjects: () -> Unit,
    onAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF952CA1))
            .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Top Building on Hits",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFFFFFFFF)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onNewProject,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Создать проект")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onMyProjects,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Мои проекты")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onAbout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("О приложении")
        }
    }

}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HitTheme {
        StartScr(onNewProject = {},
            onMyProjects = {},
            onAbout = {}
        )
    }
}