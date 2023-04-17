package logic

import androidx.compose.ui.graphics.Color
import logic.clases.Casella
import logic.clases.Row

fun clearGrid(grid: List<Row>): List<Row> {
  for (row in grid) {
    for (i in row.caselles.indices) {
      row.setCasella(i, Casella("", Color.White))
    }
  }
  return grid
}

fun updateWords(words: List<String>, wordToRemove: String): List<String> {
  var copy = words.toMutableList()
  copy.remove(wordToRemove)
  return copy.toList()
}

fun getRandomWord(words: List<String>): String {
  return words[words.indices.random()]
}

fun checkWord(row: Row, guessWord: String, randomWord: String): Int {
  var correctLetters = 0
  val remainingLetters =
    randomWord.split("").toMutableList() //Array auxiliar per decidir quines lletres seran d'un color concret.

  //Pintem lletres amb fons gris per defecte
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