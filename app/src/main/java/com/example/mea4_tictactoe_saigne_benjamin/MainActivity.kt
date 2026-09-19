package com.example.mea4_tictactoe_saigne_benjamin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mea4_tictactoe_saigne_benjamin.ui.theme.MEA4TicTacToeSaigneBenjaminTheme
import kotlinx.coroutines.*


//Navigation sous forme de pile :
enum class Destination(val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    GAME(Icons.Default.Person),
    ABOUT(Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MEA4TicTacToeSaigneBenjaminTheme {

                val backStack = remember { mutableStateListOf<Destination>(Destination.GAME) }
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            Destination.values().forEach { destination ->
                                val selected = backStack.last() == destination
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = {
                                        backStack.add(destination)
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = destination.icon,
                                            contentDescription = null
                                        )
                                    },
                                    label = { },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.Blue,
                                        unselectedIconColor = Color.Black
                                    )
                                )
                            }
                        }
                    }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        when (backStack.last()) {
                            Destination.GAME -> TicTacToeScreen()
                            Destination.ABOUT -> AboutScreen()
                        }
                    }
                }
            }
        }
    }
}

class TicTacToeJeu {

    var board by mutableStateOf(List(9) { 0 })
    var currentPlayer by mutableStateOf(1)
    var scoreJoueur1 by mutableStateOf(0)
    var scoreJoueur2 by mutableStateOf(0)
    var winningCells by mutableStateOf(listOf<Int>())

    fun jeu(index: Int) {
        if (board[index] != 0) return

        val newBoard = board.toMutableList()
        newBoard[index] = currentPlayer
        board = newBoard

        if (checkWin(currentPlayer)) {

            winningCells = getWinningCombination(currentPlayer)

            if (currentPlayer == 1) scoreJoueur1++ else scoreJoueur2++
            //Délai de 800 ms avant de remettre le plateau à zéro.
            //Permet au joueur de visualiser le résultat.
            CoroutineScope(Dispatchers.Main).launch {
                delay(800)
                resetBoard()
                winningCells = emptyList()
            }

            return
        }
        if (board.all { it != 0 }) {
            resetBoard()
            return
        }
        currentPlayer = if (currentPlayer == 1) 2 else 1
    }

    private fun resetBoard() {
        board = List(9) { 0 }
        currentPlayer = 1
    }

    private fun checkWin(player: Int): Boolean {
        val wins = listOf(
            listOf(0,1,2), listOf(3,4,5), listOf(6,7,8),
            listOf(0,3,6), listOf(1,4,7), listOf(2,5,8),
            listOf(0,4,8), listOf(2,4,6)
        )
        return wins.any { it.all { i -> board[i] == player } }
    }

    //Combinaison pour gagner horizontale, verticale et diagonale
    private fun getWinningCombination(player: Int): List<Int> {
        val wins = listOf(
            listOf(0,1,2), listOf(3,4,5), listOf(6,7,8),
            listOf(0,3,6), listOf(1,4,7), listOf(2,5,8),
            listOf(0,4,8), listOf(2,4,6)
        )

        return wins.firstOrNull { combo ->
            combo.all { board[it] == player }
        } ?: emptyList()
    }
}

//Premier "onglet" qu'on met sur la pile : (Affichage du plateau du jeu)
@Composable
fun TicTacToeScreen() {

    val viewModel = remember { TicTacToeJeu() }
    val board = viewModel.board

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(252, 216, 53)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Surface(
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 40.dp,
            color = Color(252, 216, 53)
        ) {
            Text(
                text = "Tic Tac Toe",
                color = Color.White,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {

            PlayerIcon(R.drawable.case_croix)

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "${viewModel.scoreJoueur1}",
                color = Color.Blue,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(24.dp))

            PlayerIcon(R.drawable.case_rond)

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "${viewModel.scoreJoueur2}",
                color = Color.Blue,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Cellule(viewModel, 0, getImage(board[0])) { viewModel.jeu(0) }
            Cellule(viewModel, 1, getImage(board[1])) { viewModel.jeu(1) }
            Cellule(viewModel, 2, getImage(board[2])) { viewModel.jeu(2) }
        }
        Row {
            Cellule(viewModel, 3, getImage(board[3])) { viewModel.jeu(3) }
            Cellule(viewModel, 4, getImage(board[4])) { viewModel.jeu(4) }
            Cellule(viewModel, 5, getImage(board[5])) { viewModel.jeu(5) }
        }
        Row {
            Cellule(viewModel, 6, getImage(board[6])) { viewModel.jeu(6) }
            Cellule(viewModel, 7, getImage(board[7])) { viewModel.jeu(7) }
            Cellule(viewModel, 8, getImage(board[8])) { viewModel.jeu(8) }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {

            Text(
                text = "C'est au tour de ",
                color = Color.Blue,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(12.dp))

            PlayerIcon(
                if (viewModel.currentPlayer == 1)
                    R.drawable.case_croix
                else
                    R.drawable.case_rond
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Attention à bien choisir ;) ",
            color = Color.Blue
        )
    }
}

// Deuxième "onglet" que l'on peux mettre sur la pile : (Affichage du a propos)
@Composable
fun AboutScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(252, 216, 53)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Crée par Saigné Benjamin\n",
                color = Color.Blue,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "MEA4",
                color = Color.Blue,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Règles :\n" +
                        "Deux joueurs jouent chacun leur tour.\n" +
                        "Ils placent X ou O.\n" +
                        "Alignez trois symboles identiques.\n" +
                        "Victoire ou match nul.",
                color = Color.Blue,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Left
            )
        }
    }
}


@Composable
fun PlayerIcon(imageRes: Int) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun Cellule(
    viewModel: TicTacToeJeu,
    index: Int,
    imageRes: Int,
    onClick: () -> Unit
) {
    val isWinning = viewModel.winningCells.contains(index)

    Box(
        modifier = Modifier
            .size(100.dp)
            .padding(4.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        if (isWinning) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(CircleShape)
                    .background(Color.Red.copy(alpha = 0.25f))
            )
        }
    }
}

fun getImage(value: Int): Int {
    return when (value) {
        1 -> R.drawable.case_croix
        2 -> R.drawable.case_rond
        else -> R.drawable.case_vide
    }
}