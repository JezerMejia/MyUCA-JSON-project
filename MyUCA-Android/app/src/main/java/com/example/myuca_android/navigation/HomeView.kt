package com.example.myuca_android.navigation

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.myuca_android.Coordinador
import com.example.myuca_android.R
import com.example.myuca_android.data.CoordinadorManager
import com.example.myuca_android.ui.theme.MyUCAAndroidTheme
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class ConnectionState {
    WAITING,
    OK,
    ERROR,
}

@Composable
fun HomeView(navController: NavHostController, coordinadoresList: List<Coordinador> = listOf()) {
    val context = LocalContext.current

    val coordinadores = remember { mutableStateListOf<Coordinador>() }
    val dbConnected = remember { mutableStateOf(ConnectionState.WAITING) }

    var filteredList = remember {
        mutableStateOf(mutableListOf<Coordinador>())
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                coordinadores.addAll(coordinadoresList)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    fun loadData() {
        val manager = CoordinadorManager(context)

        manager.getCoordinadores({
            Log.d("MyUCA", "Coordinadores: $it")
            coordinadores.clear()
            coordinadores.addAll(it)
            dbConnected.value = ConnectionState.OK
        }, {
            dbConnected.value = ConnectionState.ERROR
        })
    }

    SideEffect {
        loadData()
    }

    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            AppBar("Coordinadores", actions = {
                IconButton(onClick = {
                    if (filteredList.value.size > 0) {
                        filteredList.value.clear()
                    } else {
                        val result = coordinadores.filter { c -> c.getAge() > 60 }
                        filteredList.value = result.toMutableStateList()
                    }
                }) {
                    Icon(
                        painterResource(R.drawable.baseline_filter_list_24),
                        contentDescription = "Filtrar"
                    )
                }
                IconButton(onClick = {
                    loadData()
                }) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Cargar")
                }
            })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("insert")
            }) {
                Icon(Icons.Filled.Add, "Añadir estudiante")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        if (dbConnected.value == ConnectionState.WAITING) {
            LoadingMessage(innerPadding)
        } else if (coordinadores.size > 0 && dbConnected.value == ConnectionState.OK) {
            if (filteredList.value.size == 0) {
                ItemList(
                    innerPadding,
                    coordinadores,
                    coordinadores,
                    navController,
                    scaffoldState
                )
            } else {
                ItemList(
                    innerPadding,
                    coordinadores,
                    filteredList.value,
                    navController,
                    scaffoldState
                )
            }
        } else if (dbConnected.value == ConnectionState.OK) {
            NoDataMessage(innerPadding)
        } else {
            ErrorMessage(innerPadding)
        }

    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun ItemList(
    innerPadding: PaddingValues,
    coordinadores: MutableList<Coordinador>,
    list: MutableList<Coordinador>,
    navController: NavController,
    scaffoldState: ScaffoldState
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    LazyColumn(
        contentPadding = innerPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items = list, key = { c -> c.id }) { coordinador ->
            val currentItem by rememberUpdatedState(coordinador)
            val dismissState: DismissState = rememberDismissState()

            if (dismissState.currentValue == DismissValue.DismissedToStart) {
                AlertDialog(
                    onDismissRequest = {
                        coroutineScope.launch {
                            dismissState.reset()
                        }
                    },
                    title = { Text("Eliminar coordinador") },
                    text = { Text("¿Está seguro/a de eliminar el coordinador \"${currentItem.getFullName()}?\"") },
                    dismissButton = {
                        TextButton(onClick = {
                            coroutineScope.launch {
                                dismissState.reset()
                            }
                        }) {
                            Text("Cancelar")
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val manager = CoordinadorManager(context)
                                manager.deleteCoordinador(
                                    currentItem.id,
                                    {
                                        coroutineScope.launch {
                                            dismissState.reset()
                                            scaffoldState.snackbarHostState.showSnackbar(
                                                "El coordinador fue eliminado",
                                            )
                                        }
                                        if (coordinadores != list) {
                                            list.remove(currentItem)
                                        }
                                        coordinadores.remove(currentItem)
                                    }, {
                                        coroutineScope.launch {
                                            scaffoldState.snackbarHostState.showSnackbar(
                                                "El coordinador no se pudo eliminar",
                                                duration = SnackbarDuration.Long
                                            )
                                            dismissState.reset()
                                        }
                                    })
                            }
                        ) {
                            Text("Eliminar")
                        }
                    }
                )
            }

            SwipeToDismiss(
                state = dismissState,
                modifier = Modifier.animateItemPlacement(),
                directions = setOf(DismissDirection.EndToStart),
                background = {
                    val color by animateColorAsState(
                        when (dismissState.targetValue) {
                            DismissValue.DismissedToStart -> Color.Red
                            else -> Color.LightGray
                        }
                    )
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(color)
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Localized description",
                        )
                    }
                },
                dismissContent = {
                    ListItem(
                        modifier = Modifier
                            .clickable {
                                navController.navigate("modify/${currentItem.id}")
                            }
                            .background(MaterialTheme.colors.background),
                        text = { Text(currentItem.getFullName()) },
                        secondaryText = { Text("${currentItem.titulo} - ${currentItem.facultad}") },
                        trailing = { Text("${currentItem.getAge()} años") }
                    )
                }
            )
        }
    }
}

@Composable
fun LoadingMessage(innerPadding: PaddingValues) {
    Column(
        Modifier
            .padding(innerPadding)
            .padding(32.dp, 12.dp)
    ) {
        Text("Cargando datos de estudiantes...")
    }
}

@Composable
fun NoDataMessage(innerPadding: PaddingValues) {
    Column(
        Modifier
            .padding(innerPadding)
            .padding(32.dp, 12.dp)
    ) {
        Text("No hay estudiantes registrados")
    }
}

@Composable
fun ErrorMessage(innerPadding: PaddingValues) {
    Column(
        Modifier
            .padding(innerPadding)
            .padding(32.dp, 12.dp)
    ) {
        Text("Error: No se pudo obtener los datos del servidor.")
        Text("Es posible que la base de datos no esté activa o haya ocurrido un error interno en el servidor.")
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    MyUCAAndroidTheme {
        HomeView(navController = rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreviewWithData() {
    val coordinadores = listOf(
        Coordinador(
            1,
            "Juan",
            "Pérez",
            LocalDate.of(2001, 6, 20),
            "Inge",
            "juan.perez@doc.uca.edu.ni",
            "CTyA"
        ),
        Coordinador(
            2,
            "Alberto",
            "Chávez",
            LocalDate.of(1956, 4, 19),
            "Ingeniero",
            "alberto.chavez@doc.uca.edu.ni",
            "CTyA"
        ),
    )
    MyUCAAndroidTheme {
        HomeView(navController = rememberNavController(), coordinadores)
    }
}
