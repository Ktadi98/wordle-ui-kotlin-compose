package logic

import androidx.compose.ui.graphics.Color
import logic.clases.Casella
import logic.clases.Row
import java.io.File
import java.io.IOException
import kotlin.system.exitProcess

fun clearGrid(grid: List<Row>): List<Row> {
    for (row in grid) {
        for (i in row.caselles.indices) {
            row.setCasella(i, Casella("", Color.White))
        }
    }
    return grid
}

fun difference(s1: String, s2: String): Array<List<String>> {
    //s1 es guessWord
    //s2 es targetWord

    val coinc = mutableListOf<String>()
    val diff = mutableListOf<String>()

    for (i in s1.indices) {
        if (s1[i] == s2[i]) coinc.add(s1[i].uppercase())
        else if (s1[i] !in s2) {
            diff.add(s1[i].uppercase())
        }
    }

    return arrayOf(coinc.toList(), diff.toList())
}

fun changeKeyBoard(keyboard: List<Row>, coincidentes: List<String>, diffs: List<String>): List<Row> {
    val copy = keyboard.toMutableList()
    for (row in copy) {
        for (casella in row.caselles) {
            if (casella.letter in coincidentes) {
                row.setCasella(row.caselles.indexOf(casella), Casella(casella.letter, Color(67, 160, 71)))
            } else if (casella.letter in diffs) {
                row.setCasella(row.caselles.indexOf(casella), Casella(casella.letter, Color.Gray))

            }
        }
    }
    return copy.toList()
}

fun clearKeyBoard(keyboard: List<Row>) :  List<Row>{
    for (row in keyboard) {
        for (i in row.caselles.indices) {
            row.setCasella(i, Casella(row.caselles[i].letter, Color.LightGray))
        }
    }
    return keyboard
}

fun getDictionary(): List<String> {
    try {
        println(System.getProperty("user.dir"))
        return File(System.getProperty("user.dir") + "/src/jvmMain/resources/paraules.txt").readLines()
    } catch (e: IOException) {
        println("No se han podido cargar las palabras del diccionario")
        exitProcess(-1)
    }
}

fun updateWords(words: List<String>, wordToRemove: String): List<String> {
    val copy = words.toMutableList()
    copy.remove(wordToRemove)
    return copy.toList()
}

fun getRandomWord(words: List<String>): String {
    return words[words.indices.random()]
}

fun setGrid(grid: List<Row>, intent: Int, currentLetterPosition: Int, casella: Casella): List<Row> {
    val copy = grid.toMutableList()
    copy[intent].setCasella(currentLetterPosition, casella)
    return copy.toList()
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