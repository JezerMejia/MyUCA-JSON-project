package com.example.myuca_android.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myuca_android.Coordinador
import com.example.myuca_android.data.CoordinadorManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun InsertView(navController: NavController) {
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var fechaNac by remember { mutableStateOf("") }
    var titulo by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var facultad by remember { mutableStateOf("") }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    fun clearInput() {
        nombres = ""
        apellidos = ""
        fechaNac = ""
        titulo = ""
        email = ""
        facultad = ""
    }

    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            AppBar("Añadir coordinador", navController)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(value = nombres, onValueChange = { nombres = it }, label = {
                Text(text = "Nombres")
            })
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = apellidos, onValueChange = { apellidos = it }, label = {
                Text(text = "Apellidos")
            })
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = fechaNac, onValueChange = { fechaNac = it }, label = {
                Text(text = "Fecha de nacimiento")
            })
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = {
                Text(text = "Título")
            })
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = {
                    Text(text = "E-mail")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = facultad, onValueChange = { facultad = it }, label = {
                Text(text = "Facultad")
            })
            Spacer(modifier = Modifier.height(32.dp))

            Row {
                OutlinedButton(onClick = {
                    clearInput()
                }) {
                    Text("Limpiar")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = {
                    val coordinador = Coordinador(
                        0,
                        nombres,
                        apellidos,
                        LocalDate.parse(fechaNac, DateTimeFormatter.ISO_LOCAL_DATE),
                        titulo,
                        email,
                        facultad
                    )
                    val manager = CoordinadorManager(context)
                    manager.insertCoordinador(
                        coordinador,
                        {
                            coroutineScope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(
                                    "El coordinador fue añadido"
                                )
                            }
                            clearInput()
                        }, {
                            coroutineScope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(
                                    "No se pudo añadir el coordinador: $it",
                                    duration = SnackbarDuration.Long
                                )
                            }
                        })
                }) {
                    Text("Añadir")
                }
            }
        }
    }
}