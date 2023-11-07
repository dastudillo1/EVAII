package com.example.evaii

import android.content.res.Resources
import android.os.Bundle
import android.provider.Settings.Secure.getString
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.evaii.R.string
import com.example.evaii.R.string.*
import com.example.evaii.db.AppDatabase
import com.example.evaii.db.Producto
import com.example.evaii.ui.theme.EVAIITheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //ListaPorductosUI()
            AppContactosUI()
        }
    }
}

@Composable
fun ListaPorductosUI() {

    val contexto = LocalContext.current
    val (productos, setProductos)=remember{ mutableStateOf(emptyList<Producto>())}

    LaunchedEffect(productos){
        withContext(Dispatchers.IO){
            val dao = AppDatabase.getInstance(contexto).productoDao()
            setProductos(dao.findAll())
        }

    }
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ){
        items(productos){producto ->
            ProductoItemUI(producto){
                setProductos(emptyList<Producto>())

            }
        }
    }
}
@Composable
fun ProductoItemUI(producto:Producto, onSave:()->Unit = {}){
    val contexto = LocalContext.current
    val alcanceCorrutina= rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 20.dp)
    ){
        if(producto.comprado){
            Icon(
                Icons.Filled.Check,
                contentDescription = "Producto comprado",
                modifier = Modifier.clickable {
                    alcanceCorrutina.launch(Dispatchers.IO){
                        val dao = AppDatabase.getInstance(contexto).productoDao()
                        producto.comprado=false
                        dao.actualizar(producto)
                        onSave()
                    }

                }
            )
        }else{
            Icon(
                Icons.Filled.ShoppingCart,
                contentDescription = "Producto No comprado",
                modifier = Modifier.clickable {
                    alcanceCorrutina.launch(Dispatchers.IO){
                        val dao = AppDatabase.getInstance(contexto).productoDao()
                        producto.comprado=true
                        dao.actualizar(producto)
                        onSave()
                    }

                }
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text=producto.producto,
            modifier = Modifier.weight(2f)
            )
        Icon(
            Icons.Filled.Delete,
            contentDescription = "Eliminar producto",
                modifier = Modifier.clickable {
                alcanceCorrutina.launch(Dispatchers.IO){
                    val dao = AppDatabase.getInstance(contexto).productoDao()
                    dao.eliminar(producto)
                    onSave()
                }

            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProductoItemUIPreview(){
    val producto = Producto(1,"Huevos",true)
    ProductoItemUI(producto)
}

@Preview(showBackground = true)
@Composable
fun ProductoItemUIPreview1(){
    val producto1 = Producto(2,"Champinones",false)
    ProductoItemUI(producto1)

}

//-----------

enum class Accion {
    LISTAR, CREAR
}

@Composable
fun AppContactosUI() {
    //obtenemos el contexto actual
    val contexto = LocalContext.current
    //creamos una variable de estado llamada contactos y la inicializamos como una lista vacia de productos
    val (contactos, setContactos) = remember{ mutableStateOf(emptyList<Producto>() ) }
    //creamos una variable de estado llamada seleccion y la inicializamos como null
    val (seleccion, setSeleccion) = remember{mutableStateOf<Producto?>(null) }
    //creamos una variable de estado llamada accion y la inicializamos como Accion.LISTAR
    val (accion, setAccion) = remember{mutableStateOf(Accion.LISTAR) }
    //utilizamos LaunchedEffect para realizar una operación cuando cambia contactos
    LaunchedEffect(contactos){
        withContext(Dispatchers.IO){
            //ontenemos la instancia del DAO de productos de la BD
            val dao = AppDatabase.getInstance(contexto).productoDao()
            setContactos(dao.findAll())
        }
    }
    //Definimos una funcion inSave que se utilizara para cambiar la ación y limpiar la lista de contactos
    val onSave = {
        setAccion(Accion.LISTAR)
        setContactos(emptyList())
    }
    //utilizamos when para determinar que pantalla vamos a mostrar
    when(accion) {
        Accion.CREAR -> ContactoFormUI(null, onSave)
        Accion.LISTAR -> {
            ContactosListadoUI(
                contactos,
                onAdd = { setAccion(Accion.CREAR) },
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactosListadoUI(contactos:List<Producto>, onAdd:() -> Unit = {}) {
    // Se crea una variable mutable llamada "productos" usando remember para mantener su estado.
    val (productos, setProductos)=remember{ mutableStateOf(emptyList<Producto>())}
    //val btnCrear = resources.getString(R.string.crear)
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                // botón flotante extendido que se mostrará en la esquina inferior derecha.
                onClick = { onAdd() },
                icon = {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "crear"
                    )
                },

                text = {Text("Crear")}

            )
        }
    ) { contentPadding ->
        // Verifica si la lista de productos "contactos" no está vacía.
        if( contactos.isNotEmpty() ) {
            // Se crea una columna para mostrar la lista de productos y el contenido se llena en función de la disponibilidad de espacio
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // Se itera a través de la lista de productos "contactos" y se muestra cada producto en "ContactoItemUI1".
                items(contactos) { contacto ->
                    ContactoItemUI1(contacto) {
                        setProductos(emptyList<Producto>())
                        //onEdit(contacto)
                    }
                    /*ProductoItemUI(contacto){
                        setProductos(emptyList<Producto>())

                    }*/
                }
            }
            // Si la lista de productos está vacía, se muestra un mensaje indicando que no hay productos para mostrar.
        } else {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                contentAlignment = Alignment.Center
            ) {
                // Muestra el mensaje "No hay productos que mostrar" en el centro de la pantalla.
                Text("No hay productos que mostrar.")
            }
        }
    }
}

@Composable
fun ContactoItemUI1(contacto: Producto, onSave: () -> Unit= {}) {
    val contexto = LocalContext.current
    // Creamos un CoroutineScope para manejar corrutinas
    val alcanceCorrutina = rememberCoroutineScope()
    // Creamos una fila (Row) para mostrar elementos en horizontal
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 20.dp)
    ){
        // Si el producto está marcado como comprado, muestra el ícono de "Check"
        if(contacto.comprado){
            Icon(
                Icons.Filled.Check,
                contentDescription = "Producto comprado",
                modifier = Modifier.clickable {
                    alcanceCorrutina.launch(Dispatchers.IO){
                        val dao = AppDatabase.getInstance(contexto).productoDao()
                        contacto.comprado=false
                        dao.actualizar(contacto)
                        onSave()
                    }

                }
            )
        }else{
            // Si el producto no está comprado, muestra el ícono del "Carrito de compras"
            Icon(
                Icons.Filled.ShoppingCart,
                contentDescription = "Producto No comprado",
                modifier = Modifier.clickable {
                    alcanceCorrutina.launch(Dispatchers.IO){
                        val dao = AppDatabase.getInstance(contexto).productoDao()
                        contacto.comprado=true
                        dao.actualizar(contacto)
                        onSave()
                    }

                }
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text=contacto.producto,
            modifier = Modifier.weight(2f)
        )
        Icon(
            Icons.Filled.Delete,
            tint = Color.Red,
            contentDescription = "Eliminar producto",
            modifier = Modifier.clickable {
                alcanceCorrutina.launch(Dispatchers.IO){
                    val dao = AppDatabase.getInstance(contexto).productoDao()
                    dao.eliminar(contacto)
                    onSave()
                }

            }
        )
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
            Image(
                painter = painterResource(id = R.drawable.shopping),
                contentDescription = "Imagen Carro"
            )
            TextField(
                value = nombre,
                onValueChange = { setNombre(it) },
                label = {Text("Producto")}
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
}