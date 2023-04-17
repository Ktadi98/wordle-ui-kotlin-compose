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
    val focusRequester = FocusRequester()

    var inputText by remember { mutableStateOf("") }
    var grid by remember { mutableStateOf(List(NUM_ROWS) { Row(Array<Casella>(NUM_COLS) { Casella("", Color.Black) }) }) }
    var intent by remember { mutableStateOf(0) }

    //TODO cargar palabras (es estado)


    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "La palabra del día", fontWeight = FontWeight.Bold, fontSize = 25.sp)
            for (row in grid) {
                Row(Modifier.padding(5.dp)) {
                    for (casella in row.caselles) {
                        Box(contentAlignment = Alignment.Center) {
                            Card(
                                shape = RoundedCornerShape(5.dp),
                                border = BorderStroke(2.dp, Color.LightGray),
                                modifier = Modifier.width(60.dp).height(60.dp).padding(3.dp)
                            ) {
                                Text(
                                  fontWeight = FontWeight.Bold, fontSize=30.sp, textAlign = TextAlign.Center, text = casella.letter, modifier = Modifier.focusRequester(focusRequester)
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
            Text(text="Introdueix la paraula")
            TextField(value = inputText, onValueChange = {
                if (it.length <= 5) {
                    inputText = it
                }
            })
            Button(onClick = {
                var gridCopy = grid.toMutableList()
                for (i in inputText.indices) {
                   gridCopy[intent].setCasella(i, Casella(inputText[i].toString(), Color.Black))
                }
                grid = gridCopy.toList()
                inputText = ""
                intent++
                if (intent > 5) {

                    intent = 0
                }
            }, ){
                Text("Añadir")
            }
        }

    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
