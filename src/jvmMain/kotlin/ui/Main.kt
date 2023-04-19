// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import logic.setGrid

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
  //var playerWins by remember { mutableStateOf(false) }
  var words by remember { mutableStateOf(listOf("arbol", "lunas")) }
  var targetWord by remember { mutableStateOf(getRandomWord(words)) }
  var informativeMessage by remember { mutableStateOf("") }
  var currentLetterPosition by remember { mutableStateOf(0) }
  //El keyboard pot ser estat si mirem el joc original.
  val keyboard by remember {
    mutableStateOf(
      listOf(
        Row(
          arrayOf
            (
            Casella("Q", Color.LightGray),
            Casella("W", Color.LightGray),
            Casella("E", Color.LightGray),
            Casella("R", Color.LightGray),
            Casella("T", Color.LightGray),
            Casella("Y", Color.LightGray),
            Casella("U", Color.LightGray),
            Casella("I", Color.LightGray),
            Casella("O", Color.LightGray),
            Casella("P", Color.LightGray),
          )
        ),
        Row(
          arrayOf(
            Casella("A", Color.LightGray),
            Casella("S", Color.LightGray),
            Casella("D", Color.LightGray),
            Casella("F", Color.LightGray),
            Casella("G", Color.LightGray),
            Casella("H", Color.LightGray),
            Casella("J", Color.LightGray),
            Casella("K", Color.LightGray),
            Casella("L", Color.LightGray),
            Casella("Ñ", Color.LightGray),
          )
        ),
        Row(
          arrayOf(
            Casella("Z", Color.LightGray),
            Casella("X", Color.LightGray),
            Casella("C", Color.LightGray),
            Casella("V", Color.LightGray),
            Casella("B", Color.LightGray),
            Casella("N", Color.LightGray),
            Casella("M", Color.LightGray),
            Casella("CA", Color.LightGray)
          )
        )
      )
    )
  }


  MaterialTheme {

    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {

      Text(text = "WORDLE", fontWeight = FontWeight.Bold, fontSize = 25.sp)
      Text(text = informativeMessage)

      grid(grid)

      /*******KEYBOARD*****/
      Column {
        for (row in keyboard) {
          Row(Modifier.padding(2.dp)) {
            for (casella in row.caselles) {
              Box(contentAlignment = Alignment.CenterStart) {
                val event: () -> Unit
                if (casella.letter != "CA") {
                  event = {
                    if (inputText.length < wordLength && !stopPlay) {
                      inputText += casella.letter.lowercase()
//                      val copy = grid.toMutableList()
//                      copy[intent].setCasella(currentLetterPosition, Casella(casella.letter, Color.LightGray))
//                      grid = copy.toList()
                      grid = setGrid(grid, intent, currentLetterPosition, Casella(casella.letter, Color.LightGray))
                      if (currentLetterPosition < wordLength) currentLetterPosition++
                    }
                  }
                } else {
                  event = {
                    if (inputText.isNotEmpty() && !stopPlay) {
                      inputText = inputText.substring(0, inputText.lastIndex)
//                      val copy = grid.toMutableList()
//                      copy[intent].setCasella(currentLetterPosition - 1, Casella("", Color.White))
//                      grid = copy.toList()
                      grid = setGrid(grid, intent, currentLetterPosition - 1, Casella(casella.letter, Color.White))
                      if (currentLetterPosition > 0) currentLetterPosition--
                    }
                  }
                }
                keyBoardKey(event, casella.letter, casella.color)
              }
            }
          }
        }
      }
      /*******KEYBOARD*****/

      val inputWordEvent = { it: String ->
        if (it.length <= wordLength) {
          inputText = it.lowercase()
        }
      }

      wordInput(inputText, inputWordEvent)

      Row {
        val onClickHandlerCheck = {
          if (intent < maxTries && !stopPlay && inputText.length == wordLength && words.isNotEmpty()) {
            val gridCopy = grid.toMutableList()
            for (i in inputText.indices) {
              correctLetters = checkWord(gridCopy[intent], inputText, targetWord)
            }
            grid = gridCopy.toList()
            inputText = ""
            currentLetterPosition = 0
            intent++

            if (intent == maxTries || correctLetters == wordLength) {
              restartButtonVisible = true
              stopPlay = true
            }
            if (correctLetters == wordLength) {
             // playerWins = true
              informativeMessage = "Has ganado!"
            } else if (correctLetters < wordLength && intent == maxTries) {
              informativeMessage = "Lástima,...La palabra era $targetWord "
            }
          }
        }
        val onClickHandlerRestart = {
          restartButtonVisible = !restartButtonVisible
          intent = 0
          stopPlay = false
         // playerWins = false
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
        if (!stopPlay && words.isNotEmpty()) {
          customButton(onClickHandlerCheck, "Comprobar")
        }
        if (restartButtonVisible) {
          customButton(onClickHandlerRestart, "Resetear")
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
fun wordInput(inputText: String, inputEvent: (String) -> Unit) {
  //Text(text = "Introduce la palabra")
  TextField(value = inputText, onValueChange = inputEvent, enabled=false)
}

@Composable
fun keyBoardKey(event: () -> Unit, letter: String, color: Color) {
  Button(
    shape = RoundedCornerShape(3.dp),
    modifier = Modifier.width(50.dp).height(60.dp).padding(3.dp),
    colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray),
    onClick = event
  ) {
    Text(
      color = Color.Black,
      fontWeight = FontWeight.Bold,
      fontSize = 15.sp,
      textAlign = TextAlign.Center,
      text = letter,
      modifier = Modifier.background(color).size(50.dp).wrapContentHeight()
    )
  }
}

@Composable
fun customButton(onClickHandler: () -> Unit, text: String) {
  Button(
    onClick = onClickHandler
  ) {
    Text(text)
  }
}

fun main() = application {
  Window(
    state = WindowState(position = WindowPosition(250.dp, 10.dp), size = DpSize(1000.dp, 1000.dp)),
    title = "Wordle_TarriasCarlos",
    onCloseRequest = ::exitApplication
  ) {
    App()
  }
}



