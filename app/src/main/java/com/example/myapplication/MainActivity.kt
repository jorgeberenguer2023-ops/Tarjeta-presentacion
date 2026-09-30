package com.example.myapplication

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

    var showGithubQr by remember { mutableStateOf(false) }
    var showLinkedinQr by remember { mutableStateOf(false) }
    var showCvQr by remember { mutableStateOf(false) }
    var showProyectosQr by remember { mutableStateOf(false) }

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
                if (showGithubQr) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/jorgeberenguer2023-ops"))
                    context.startActivity(intent)
                    showGithubQr = false
                } else {
                    showGithubQr = true
                    showLinkedinQr = false
                    showCvQr = false
                    showProyectosQr = false
                }
            },
            modifier = Modifier.fillMaxWidth(fraction = 0.8f) // ocupa el 80% del ancho de pantalla
        ) {
            Text(text = "Mi Perfil de GitHub")
        }

        if (showGithubQr) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Escanea el QR de GitHub:",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            val qrBitmap = generateQRCode("https://github.com/jorgeberenguer2023-ops")
            if (qrBitmap != null) {
                Image(
                    bitmap = qrBitmap,
                    contentDescription = "Código QR de GitHub",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // BUTTON 2: Enlace a LinkedIn
        Button(
            onClick = {
                if (showLinkedinQr) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/jorge-berenguer-martin-b9442b438/"))
                    context.startActivity(intent)
                    showLinkedinQr = false
                } else {
                    showLinkedinQr = true
                    showGithubQr = false
                    showCvQr = false
                    showProyectosQr = false
                }
            },
            modifier = Modifier.fillMaxWidth(fraction = 0.8f)
        ) {
            Text(text = "Mi Perfil de LinkedIn")
        }

        if (showLinkedinQr) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Escanea el QR de LinkedIn:",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            val qrBitmap = generateQRCode("https://www.linkedin.com/in/jorge-berenguer-martin-b9442b438/")
            if (qrBitmap != null) {
                Image(
                    bitmap = qrBitmap,
                    contentDescription = "Código QR de LinkedIn",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // BUTTON 3: Descargar CV a la carpeta Descargas y mostrar/ocultar QR
        Button(
            onClick = {
                if (showCvQr) {
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
                    showCvQr = false
                } else {
                    showCvQr = true
                    showGithubQr = false
                    showLinkedinQr = false
                    showProyectosQr = false
                }
            },
            modifier = Modifier.fillMaxWidth(fraction = 0.8f)
        ) {
            Text(text = "Descargar / Ver CV")
        }

        if (showCvQr) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Escanea para descargar mi CV:",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            val qrBitmap = generateQRCode("https://raw.githubusercontent.com/jorgeberenguer2023-ops/CV/main/CV%20Jorge%20Berenguer%20Mart%C3%ADn.pdf")
            if (qrBitmap != null) {
                Image(
                    bitmap = qrBitmap,
                    contentDescription = "Código QR para descargar el CV",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // BUTTON 4: Mis Proyectos
        Button(
            onClick = {
                if (showProyectosQr) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/jorgeberenguer2023-ops/Mis-proyectos"))
                    context.startActivity(intent)
                    showProyectosQr = false
                } else {
                    showProyectosQr = true
                    showGithubQr = false
                    showLinkedinQr = false
                    showCvQr = false
                }
            },
            modifier = Modifier.fillMaxWidth(fraction = 0.8f)
        ) {
            Text(text = "Mis Proyectos")
        }

        if (showProyectosQr) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Escanea para ver Mis Proyectos:",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            val qrBitmap = generateQRCode("https://github.com/jorgeberenguer2023-ops/Mis-proyectos")
            if (qrBitmap != null) {
                Image(
                    bitmap = qrBitmap,
                    contentDescription = "Código QR para Mis Proyectos",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Fit
                )
            }
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

// Función auxiliar para generar el código QR con ZXing
fun generateQRCode(text: String, width: Int = 512, height: Int = 512): ImageBitmap? {
    return try {
        val bitMatrix = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, width, height)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
        bitmap.asImageBitmap()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}