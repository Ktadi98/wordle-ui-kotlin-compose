// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import clases.Casella
import clases.Row

@Composable
@Preview
fun App() {
  val NUM_ROWS = 6
  val NUM_COLS = 5
  val WORD_LENGTH = 5
  val MAX_TRIES = 6
  val focusRequester = FocusRequester()

  var inputText by remember { mutableStateOf("") }
  var grid by remember { mutableStateOf(List(NUM_ROWS) { Row(Array<Casella>(NUM_COLS) { Casella("", Color.White) }) }) }
  var intent by remember { mutableStateOf(0) }
  var correctLetters by remember { mutableStateOf(0) }
  var restartButtonVisible by remember { mutableStateOf(false) }
  var stopPlay by remember { mutableStateOf(false) }
  var playerWins by remember { mutableStateOf(false) }
  var words by remember { mutableStateOf(listOf("arbol", "lunas")) }
  var targetWord = words[0]

  MaterialTheme {

    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {

      Text(text = "WORDLE", fontWeight = FontWeight.Bold, fontSize = 25.sp)
      if (playerWins) {
        Text(text = "ENHORABUENA HAS GANADO!")
      }

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


//                                TextField(
//                                    value = casella.letter,
//                                    onValueChange = {},
//                                    label = {
//                                        Text(
//                                            fontWeight = FontWeight.Bold,
//                                            fontSize = 30.sp,
//                                            textAlign = TextAlign.Center,
//                                            text = casella.letter
//                                        )
//                                    },
//                                    modifier = Modifier
//                                        .focusRequester(focusRequester)
//                                )


              }
            }

          }
        }
      }
      Text(text = "Introduce la palabra")
      TextField(value = inputText, onValueChange = {
        if (it.length <= WORD_LENGTH) {
          inputText = it
        }
      })
      Row {
        if (!stopPlay) {
          var onClickHandlerCheck = {
            if (intent < MAX_TRIES && !stopPlay && inputText.isNotEmpty()) {
              val gridCopy = grid.toMutableList()
              for (i in inputText.indices) {
                correctLetters = checkWord(gridCopy[intent], inputText, targetWord)
              }
              grid = gridCopy.toList()
              inputText = ""
              intent++
              println(correctLetters)
              if (intent == MAX_TRIES || correctLetters == WORD_LENGTH) {
                restartButtonVisible = true
                stopPlay = true
              }
              if (correctLetters == WORD_LENGTH) playerWins = true
            }
          }

          checkButton(onClickHandlerCheck, "Comprobar")
//          Button(
//            onClick = {
//              if (intent < MAX_TRIES && !stopPlay && inputText.isNotEmpty()) {
//                val gridCopy = grid.toMutableList()
//                for (i in inputText.indices) {
//                  correctLetters = checkWord(gridCopy[intent], inputText, targetWord)
//                }
//                grid = gridCopy.toList()
//                inputText = ""
//                intent++
//                println(correctLetters)
//                if (intent == MAX_TRIES || correctLetters == WORD_LENGTH) {
//                  restartButtonVisible = true
//                  stopPlay = true
//                }
//                if (correctLetters == WORD_LENGTH) playerWins = true
//              }
//            }
//          ) {
//            Text("Comprobar")
//          }
        }
        var onClickHandlerRestart = {
          restartButtonVisible = !restartButtonVisible
          intent = 0
          stopPlay = false
          playerWins = false
          val copy = grid.toMutableList()
          grid = clearGrid(copy).toList()
        }
        if (restartButtonVisible) {
          restartButton(onClickHandlerRestart, "Resetear")
//          Button(onClick = {
//            restartButtonVisible = !restartButtonVisible
//            intent = 0
//            stopPlay = false
//            playerWins = false
//            val copy = grid.toMutableList()
//            grid = clearGrid(copy).toList()
//          }) {
//            Text("Resetear")
//          }
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
  Window(title = "Wordle_TarriasCarlos", onCloseRequest = ::exitApplication) {
    App()
  }
}

fun clearGrid(grid: List<Row>): List<Row> {
  for (row in grid) {
    for (i in row.caselles.indices) {
      row.setCasella(i, Casella("", Color.White))
    }
  }
  return grid
}

fun checkWord(row: Row, guessWord: String, randomWord: String): Int {
  var correctLetters = 0
  val remainingLetters =
    randomWord.split("").toMutableList() //Array auxiliar per decidir quines lletres seran d'un color concret.

  //Pintem lletres amb fons gris per defecte
  val letterToColor = mutableListOf<Array<String>>()
  for (i in guessWord.indices) {
    row.setCasella(i, Casella(guessWord[i].uppercase(), Color.Gray))
  }

  //Primer comprovem les lletres que coincideixen en la mateixa posició que la paraula secreta.
  for (i in guessWord.indices) {
    if (guessWord[i] == randomWord[i]) {
      val indexOfChar = remainingLetters.indexOf(guessWord[i].toString())
      remainingLetters.removeAt(indexOfChar)
      row.setCasella(i, Casella(guessWord[i].uppercase(), Color(67, 160, 71)))
      correctLetters++
    }
  }

  //Després comprovem de les lletres que no coincideixen quines estan incloses a la paraula secreta.
  for (i in guessWord.indices) {
    if (remainingLetters.contains(guessWord[i].toString()) && guessWord[i] != randomWord[i]) {
      val indexOfChar = remainingLetters.indexOf(guessWord[i].toString())
      remainingLetters.removeAt(indexOfChar)
      row.setCasella(i, Casella(guessWord[i].uppercase(), Color(228, 168, 29)))
    }
  }

  return correctLetters
}

