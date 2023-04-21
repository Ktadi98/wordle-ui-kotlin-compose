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
import logic.*
import logic.clases.Casella
import logic.clases.Row


@Composable
fun App() {

    //Definición de constantes
    val numRows = 6
    val numCols = 5
    val wordLength = 5
    val maxTries = 6

    var inputText by remember { mutableStateOf("") } //Palabra que introduce el usuario.
    var grid by remember {
        mutableStateOf(List(numRows) {
            Row(Array(numCols) {
                Casella(
                    "",
                    Color.White
                )
            })
        })
    } //matriz de casillas donde se muestra la palabra introducida.
    var intent by remember { mutableStateOf(0) } //Numero de intento de la partida.
    var correctLetters by remember { mutableStateOf(0) } //Numero de letras que coinciden exactamente con la palabra a adivinar.
    var restartButtonVisible by remember { mutableStateOf(false) } //Mostrar o no el botón de Reinicio de partida.
    var stopPlay by remember { mutableStateOf(false) } //Indica si la partida ha finalizado.
    var words by remember { mutableStateOf(listOf("arbol", "lunas")) } //Diccionario de palabras.
    var targetWord by remember { mutableStateOf(getRandomWord(words)) } //Palabra del diccionario que hay que adivinar.
    var informativeMessage by remember { mutableStateOf("") } //Texto informativo indica si se ha ganado o perdido la partida.
    var currentLetterPosition by remember { mutableStateOf(0) } //Posición de la casilla en la fila.

    var coincidentes by remember { mutableStateOf(listOf("")) } // Lista de carácteres que coinciden exactamente con la palabra a adivinar.
    var diffs by remember { mutableStateOf(listOf("")) } // Lista de carácteres que no existen en la palabra a adivinar.

    //Teclado para introducir la palabra. Es tad ya que cambian el color de las teclas en función de la palabra introducida.
    var keyboard by remember {
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

            //Título
            Text(text = "WORDLE", fontWeight = FontWeight.Bold, fontSize = 35.sp)


            //Casillas de las palabras
            grid(grid)

            //Texto para decir si ha perdido o ha ganado la partida.
            Text(text = informativeMessage, fontSize = 30.sp)

            /*******KEYBOARD*****/
            // Pintamos teclado
            Column {
                for (row in keyboard) {
                    Row(Modifier.padding(2.dp)) {
                        for (casella in row.caselles) {
                            Box(contentAlignment = Alignment.CenterStart) {
                                //Definimos evento al pulsar una tecla del teclado
                                val event: () -> Unit

                                //Si la tecla no es la de borrar actualizamos casilla correspondiente.
                                if (casella.letter != "CA") {
                                    event = {
                                        //Comprobamos que no nos pasemos de la longitud de la palabra.
                                        if (inputText.length < wordLength && !stopPlay) {
                                            inputText += casella.letter.lowercase()
                                            grid = setGrid(
                                                grid,
                                                intent,
                                                currentLetterPosition,
                                                Casella(casella.letter, Color.LightGray)
                                            )
                                            if (currentLetterPosition < wordLength) currentLetterPosition++
                                        }
                                    }
                                    //Si la tecla es la de cancelar borramos la letra de la casilla correspondiente.
                                } else {
                                    event = {
                                        //Sólo borramos si se ha introducido almenos una letra.
                                        if (inputText.isNotEmpty() && !stopPlay) {
                                            inputText = inputText.substring(0, inputText.lastIndex)
                                            grid = setGrid(
                                                grid,
                                                intent,
                                                currentLetterPosition - 1,
                                                Casella(casella.letter, Color.White)
                                            )
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

            //Input deshabilitado por diseño de la app.
            wordInput(inputText, inputWordEvent)

            Row {
                //Definimos evento al pulsar el botón de comprobar palabra.
                val onClickHandlerCheck = {
                    //Si la partida no ha terminado y la palabra tiene la longitud válida podemos comprobar nuestro input
                    if (canPlay(intent, maxTries, stopPlay, inputText, wordLength, words)) {
                        // Actualizamos el estado de la fila de casillas correspondiente
                        val gridCopy = grid.toMutableList()
                        for (i in inputText.indices) {
                            correctLetters = checkWord(gridCopy[intent], inputText, targetWord)
                        }
                        grid = gridCopy.toList()

                        //Miramos las letras que coinciden exactamente y las que no existen en la palabra a adivinar.
                        coincidentes += difference(inputText, targetWord)[0]
                        diffs += difference(inputText, targetWord)[1]

                        //Cambiamos casillas teclado
                        keyboard = changeKeyBoard(keyboard, coincidentes, diffs)

                        inputText = ""
                        currentLetterPosition = 0
                        intent++

                        //Si la partida acaba mostramos el botón de resetear.
                        if (intent == maxTries || correctLetters == wordLength) {
                            restartButtonVisible = true
                            stopPlay = true
                        }

                        // Si el jugador gana o pierde mostramos mensaje correspondiente.
                        if (correctLetters == wordLength) {
                            informativeMessage = "Has ganado!"
                        } else if (correctLetters < wordLength && intent == maxTries) {
                            informativeMessage = "Lástima,...La palabra era $targetWord "
                        }
                    }
                }

                //Definimos evento al pulsar el botón de reset.
                val onClickHandlerRestart = {
                    restartButtonVisible = !restartButtonVisible
                    intent = 0
                    stopPlay = false

                    //Volvemos al estado inicial de las casillas y el teclado.
                    val copy = grid.toMutableList()
                    val copyKeyBoard = keyboard.toMutableList()

                    coincidentes = listOf("")
                    diffs = listOf("")
                    grid = clearGrid(copy).toList()
                    keyboard = clearKeyBoard(copyKeyBoard).toList()
                    words = updateWords(words, targetWord)

                    //Si quedan palabras por jugar , seleccionamos aleatoriamente una nueva palabra.
                    if (words.isNotEmpty()) targetWord = getRandomWord(words)

                    //Mostramos mensaje en función de las palabras que quedan por jugar
                    informativeMessage = if (words.isEmpty()) {
                        "Ya has jugado con todas las palabras disponibles"
                    } else {
                        ""
                    }
                }

                //Mostramos los botones en función de si la partida ha acabado o no.
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
    TextField(value = inputText, onValueChange = inputEvent, enabled = false)
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



