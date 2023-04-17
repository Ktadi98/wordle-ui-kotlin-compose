// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import logic.clases.Casella
import logic.clases.Row
import logic.checkWord
import logic.clearGrid
import logic.getRandomWord
import logic.updateWords

@Composable
fun App() {
  val numRows = 6
  val numCols = 5
  val wordLength = 5
  val maxTries = 6

  var inputText by remember { mutableStateOf("") }
  var grid by remember { mutableStateOf(List(numRows) { Row(Array(numCols) { Casella("", Color.White) }) }) }
  var intent by remember { mutableStateOf(0) }
  var correctLetters by remember { mutableStateOf(0) }
  var restartButtonVisible by remember { mutableStateOf(false) }
  var stopPlay by remember { mutableStateOf(false) }
  var playerWins by remember { mutableStateOf(false) }
  var words by remember { mutableStateOf(listOf("arbol", "lunas")) }
  var targetWord by remember { mutableStateOf(getRandomWord(words)) }
  var informativeMessage by remember { mutableStateOf("") }

  MaterialTheme {

    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {

      Text(text = "WORDLE", fontWeight = FontWeight.Bold, fontSize = 25.sp)

      Text(text = informativeMessage)

      grid(grid)
      Text(text = "Introduce la palabra")
      TextField(value = inputText, onValueChange = {
        if (it.length <= wordLength) {
          inputText = it.lowercase()
        }
      })
      Row {
        if (!stopPlay && words.isNotEmpty()) {
          val onClickHandlerCheck = {
            if (intent < maxTries && !stopPlay && inputText.length == wordLength && words.isNotEmpty()) {
              val gridCopy = grid.toMutableList()
              for (i in inputText.indices) {
                correctLetters = checkWord(gridCopy[intent], inputText, targetWord)
              }
              grid = gridCopy.toList()
              inputText = ""
              intent++
              println(correctLetters)
              if (intent == maxTries || correctLetters == wordLength) {
                restartButtonVisible = true
                stopPlay = true
              }
              if (correctLetters == wordLength) {
                playerWins = true
                informativeMessage = "Has ganado!"
              }
            }
          }
          checkButton(onClickHandlerCheck, "Comprobar")
        }
        val onClickHandlerRestart = {
          restartButtonVisible = !restartButtonVisible
          intent = 0
          stopPlay = false
          playerWins = false
          val copy = grid.toMutableList()
          grid = clearGrid(copy).toList()
          words = updateWords(words, targetWord)
          if (words.isNotEmpty()) targetWord = getRandomWord(words)
          informativeMessage = if (words.isEmpty()) {
            "Ya has jugado con todas las palabras disponibles"
          } else {
            ""
          }
        }
        if (restartButtonVisible) {
          restartButton(onClickHandlerRestart, "Resetear")
        }
      }
    }
  }
}

@Composable
fun grid(grid: List<Row>) {
  for (row in grid) {
    Row(Modifier.padding(5.dp)) {
      for (casella in row.caselles) {
        Box(contentAlignment = Alignment.CenterStart) {
          Card(
            shape = RoundedCornerShape(5.dp),
            border = BorderStroke(2.dp, Color.LightGray),
            modifier = Modifier.width(60.dp).height(60.dp).padding(3.dp)
          ) {

            Text(
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 30.sp,
              textAlign = TextAlign.Center,
              text = casella.letter,
              modifier = Modifier.background(casella.color).size(60.dp).wrapContentHeight()
            )
          }
        }
      }
    }
  }
}

@Composable
fun restartButton(onClickHandler: () -> Unit, text:String) {
  Button(onClick = onClickHandler
  ) {
    Text(text)
  }
}

@Composable
fun checkButton(onClickHandler: () -> Unit, text:String) {
  Button(onClick = onClickHandler
  ) {
    Text(text)
  }
}



fun main() = application {
  Window(state = WindowState(position = WindowPosition(250.dp,10.dp),size = DpSize(1000.dp,1000.dp)),title = "Wordle_TarriasCarlos", onCloseRequest = ::exitApplication) {
    App()
  }
}



