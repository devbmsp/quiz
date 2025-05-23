package question.skill.quiz

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.compose.ui.unit.sp
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import question.skill.quiz.ui.theme.QuizTheme

// Defina as constantes e a classe de dados
const val ENTRY_DELIMITER = "|"
const val FIELD_DELIMITER = ","

data class LeaderboardEntryClass(val name: String, val score: Int)

class LeaderboardScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizTheme {
                LeaderboardContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardContent() {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("leaderboard", Context.MODE_PRIVATE)
    val leaderboard = remember { loadLeaderboard(sharedPreferences) }

    val sortedLeaderboard = leaderboard.sortedByDescending { it.score }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.background_image),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Leaderboard",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .fillMaxWidth()
                )

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(sortedLeaderboard) { entry ->
                        Text(
                            text = "${entry.name}: ${entry.score} pontos",
                            fontSize = 20.sp,
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val intent = Intent(context, MainMenuScreen::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                ) {
                    Text(text = "Voltar ao Início", fontSize = 20.sp)
                }
            }
        }
    }
}

fun loadLeaderboard(sharedPreferences: SharedPreferences): List<LeaderboardEntry> {
    val leaderboardData = sharedPreferences.getString("leaderboard_data", "") ?: ""
    if (leaderboardData.isEmpty()) {
        return emptyList()
    }

    val entries = leaderboardData.split(ENTRY_DELIMITER)
    return entries.mapNotNull { entry ->
        val fields = entry.split(FIELD_DELIMITER)
        if (fields.size == 2) {
            val name = fields[0]
            val score = fields[1].toIntOrNull() ?: return@mapNotNull null
            LeaderboardEntry(name, score)
        } else {
            null
        }
    }
}
