package com.example.bancodequestoes

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FormularioCadastro { pergunta, resposta, tag ->
                        if (pergunta.isNotBlank() && resposta.isNotBlank() && tag.isNotBlank()) {
                            // Criação da Intent conforme a estrutura de navegação do PDF
                            val intent = Intent(this, RespostaActivity::class.java).apply {
                                putExtra("pergunta", pergunta)
                                putExtra("resposta", resposta)
                                putExtra("tag", tag)
                            }
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioCadastro(onAvancarClick: (String, String, String) -> Unit) {
    var pergunta by remember { mutableStateOf("") }
    var resposta by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Novo Banco de Questões") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = pergunta,
                onValueChange = { pergunta = it },
                label = { Text("Digite a Pergunta") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = resposta,
                onValueChange = { resposta = it },
                label = { Text("Digite a Resposta Correta") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = tag,
                onValueChange = { tag = it },
                label = { Text("Tag / Categoria (ex: História, Programação)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onAvancarClick(pergunta, resposta, tag) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Revisar e Gravar")
            }
        }
    }
}