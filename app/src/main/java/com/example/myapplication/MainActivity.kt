package com.example.myapplication

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File

// Android la necesita; aquí casi nunca se toca nada más
// que la línea setContent { ... }.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Aplicamos el tema de colores
            MaterialTheme {
                // Surface el "lienzo" de fondo que ocupa toda la pantalla
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Aquí llamamos a NUESTRA función, la que dibuja la tarjeta
                    TarjetaPresentacion()
                }
            }
        }
    }
}

@Composable
fun TarjetaPresentacion() {
    // LocalContext: así un Composable "pide prestado" el contexto de Android
    // Lo necesitamos para poder abrir el navegador o el visor desde el botón.
    val context = LocalContext.current

    // 1. COLUMN: apila los elementos de arriba a abajo (como un flexbox vertical)
    Column(
        modifier = Modifier
            .fillMaxSize() // ocupa toda la pantalla
            .padding(all = 16.dp), // margen para que nada toque los bordes
        horizontalAlignment = Alignment.CenterHorizontally, // centra en el eje X
        verticalArrangement = Arrangement.Center // centra en el eje Y
    ) {
        // 2. IMAGE: la foto de perfil
        // Requiere un archivo 'fotomia' dentro de res/drawable
        Image(
            painter = painterResource(id = R.drawable.fotomia),
            contentDescription = "Foto de perfil de usuario", // para accesibilidad (lectores de pantalla)
            modifier = Modifier
                .size(150.dp) // tamaño fijo: 150x150
                .clip(CircleShape), // la recorta en forma de círculo
            contentScale = ContentScale.Crop // rellena el círculo sin deformar la imagen
        )

        // Hueco vacío entre la imagen y el texto
        Spacer(modifier = Modifier.height(24.dp))

        // 3. TEXT: nombre
        Text(
            text = "Jorge Berguer Martín",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        // TEXT: rol o profesión
        Text(
            // cada alumno pone el suyo
            text = "Desarrollador de DAM",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.secondary // color secundario del tema
        )

        // Hueco más grande antes del botón
        Spacer(modifier = Modifier.height(32.dp))

        // 4. BUTTON 1: Enlace a GitHub
        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/jorgeberenguer2023-ops"))
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth(fraction = 0.8f) // ocupa el 80% del ancho de pantalla
        ) {
            Text(text = "Mi Perfil de GitHub")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // BUTTON 2: Enlace a LinkedIn
        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/jorge-berenguer-martin-b9442b438/"))
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth(fraction = 0.8f)
        ) {
            Text(text = "Mi Perfil de LinkedIn")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // BUTTON 3: Descargar CV a la carpeta Descargas
        Button(
            onClick = {
                try {
                    val filename = "CV_Jorge_Berenguer.pdf"
                    val inputStream = context.resources.openRawResource(R.raw.cvingles)
                    
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val resolver = context.contentResolver
                        val contentValues = ContentValues().apply {
                            put(MediaStore.Downloads.DISPLAY_NAME, filename)
                            put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                        }
                        val uri = resolver.insert(MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), contentValues)
                        uri?.let {
                            resolver.openOutputStream(it)?.use { output ->
                                inputStream.copyTo(output)
                            }
                        }
                    } else {
                        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        downloadsDir.mkdirs()
                        val file = File(downloadsDir, filename)
                        file.outputStream().use { output ->
                            inputStream.copyTo(output)
                        }
                    }
                    
                    Toast.makeText(context, "¡CV descargado en la carpeta Descargas!", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, "Error al descargar el CV", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(fraction = 0.8f)
        ) {
            Text(text = "Descargar / Ver CV")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TarjetaPreview() {
    MaterialTheme {
        TarjetaPresentacion()
    }
}