package com.example.evaii

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.evaii.db.AppDatabase
import com.example.evaii.db.Producto
import com.example.evaii.ui.theme.EVAIITheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextField
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppContactosUI()
        }
    }
}

/*enum class Accion {
    LISTAR, CREAR, EDITAR
}

@Composable
fun AppContactosUI() {
    val contexto = LocalContext.current
    val (contactos, setContactos) = remember{ mutableStateOf(
        emptyList<Producto>() ) }
    val (seleccion, setSeleccion) = remember{
        mutableStateOf<Producto?>(null) }
    val (accion, setAccion) = remember{
        mutableStateOf(Accion.LISTAR) }
    LaunchedEffect(contactos) {
        withContext(Dispatchers.IO) {
            val db = AppDatabase.getInstance( contexto )
            setContactos( db.productoDao().getAll() )
            Log.v("AppContactosUI", "LaunchedEffect()")
        }
    }
    val onSave = {
        setAccion(Accion.LISTAR)
        setContactos(emptyList())
    }
    when(accion) {
        Accion.CREAR -> ContactoFormUI(null, onSave)
        Accion.EDITAR -> ContactoFormUI(seleccion, onSave)
        else -> ContactosListadoUI(
            contactos,
            onAdd = { setAccion( Accion.CREAR ) },
            onEdit = { contacto ->
                setSeleccion(contacto)
                setAccion( Accion.EDITAR)
            }
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactosListadoUI(contactos:List<Producto>, onAdd:() -> Unit = {},
                       onEdit:(c:Producto) -> Unit = {}) {
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onAdd() },
                icon = {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "agregar"
                    )
                },
                text = { Text("Agregar") }
            )
        }
    ) { contentPadding ->
        if( contactos.isNotEmpty() ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(contactos) { contacto ->
                    ContactoItemUI(contacto) {
                        onEdit(contacto)
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay contactos guardados.")
            }
        }
    }
}

@Composable
fun ContactoItemUI(contacto:Producto, onClick:() -> Unit = {}) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Spacer(modifier = Modifier.width(20.dp))
        /*Image(
            painter = painterResource(id = R.drawable.account_box),
            contentDescription = "Imagen Contacto"
        )*/
        Spacer(modifier = Modifier.width(20.dp))
        Column() {
            Text(contacto.producto, fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp)
            //Text(contacto.comprado)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactoFormUI(c:Producto?, onSave:()->Unit = {}){
    val contexto = LocalContext.current
    val (nombre, setNombre) = remember { mutableStateOf(
        c?.producto ?: "" ) }
    /*val (telefono, setTelefono) = remember { mutableStateOf(
        c?.telefono ?: "" ) }*/
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { SnackbarHost( snackbarHostState) }
    ) {paddingValues ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            /*Image(
                painter = painterResource(id = R.drawable.account_box),
                contentDescription = "Imagen de usuario")*/
            TextField(
                value = nombre,
                onValueChange = { setNombre(it) },
                label = {Text("Nombre")}
            )
            Spacer(modifier = Modifier.height(10.dp))
            /*TextField(
                value = telefono,
                onValueChange = { setTelefono(it) },
                label = {Text("Teléfono")}
            )*/
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = {
                coroutineScope.launch(Dispatchers.IO) {
                    val dao = AppDatabase.getInstance( contexto
                    ).productoDao()
                    val contacto = Producto(c?.uid ?: 0, nombre,
                        false)
                    if( contacto.uid > 0) {
                        dao.update(contacto)
                    } else {
                        dao.insert(contacto)
                    }
                    snackbarHostState.showSnackbar("Se ha guardado el producto ${contacto.producto}")
                    onSave()
                }
            }) {
                var textoGuardar = "Crear"
                if(c?.uid ?:0 > 0) {
                    textoGuardar = "Guardar"
                }
                Text(textoGuardar)
            }
            if(c?.uid ?:0 > 0) {
                Button(onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        val dao =
                            AppDatabase.getInstance(contexto).productoDao()
                        snackbarHostState.showSnackbar("Eliminando el producto ${c?.producto}")
                        if( c != null) {
                            dao.eliminar(c)
                        }
                        onSave()
                    }
                }) {
                    Text("Eliminar")
                }
            }
        }
    }
}*/


