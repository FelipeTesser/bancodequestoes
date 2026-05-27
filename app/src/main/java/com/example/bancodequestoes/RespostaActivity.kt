package com.example.bancodequestoes

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bancodequestoes.db.DBHelper

class RespostaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Recuperação dos valores enviados pela MainActivity
        val pergunta = intent.getStringExtra("pergunta") ?: ""
        val resposta = intent.getStringExtra("resposta") ?: ""
        val tag = intent.getStringExtra("tag") ?: ""

        val dbHelper = DBHelper(this)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Estado reativo que lê as linhas da tabela SQLite nativa
                    var listaQuestoes by remember { mutableStateOf(dbHelper.getAllQuestoes()) }

                    TelaConfirmacao(
                        pergunta = pergunta,
                        resposta = resposta,
                        tag = tag,
                        listaQuestoes = listaQuestoes,
                        onConfirmar = {
                            // Grava efetivamente no SQLite nativo
                            val sucesso = dbHelper.addQuestao(pergunta, resposta, tag)
                            if (sucesso) {
                                Toast.makeText(this, "Questão gravada!", Toast.LENGTH_SHORT).show()
                                // Atualiza a lista no ecrã de forma dinâmica
                                listaQuestoes = dbHelper.getAllQuestoes()
                            } else {
                                Toast.makeText(this, "Erro ao gravar no banco.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onVoltar = {
                            finish() // Executa o encerramento da Atividade retornando à MainActivity
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaConfirmacao(
    pergunta: String,
    resposta: String,
    tag: String,
    listaQuestoes: List<Map<String, String>>,
    onConfirmar: () -> Unit,
    onVoltar: () -> Unit
) {
    var ativo by remember { mutableStateOf(true) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Confirmar e Listar") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Visualização dos dados vindos da Intent
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Dados da Nova Questão:", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Pergunta: $pergunta")
                    Text("Resposta: $resposta")
                    Text("Tag: #$tag", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }

            // Botões de comando
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = onVoltar, modifier = Modifier.weight(1f)) {
                    Text("Voltar")
                }
                Button(
                    onClick = {
                        onConfirmar()
                        ativo = false // Desativa o botão para evitar duplicações acidentais
                    },
                    enabled = ativo,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Gravar no Banco")
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Consulta direta e exibição do Banco de Dados
            Text("Questões Salvas no SQLite (${listaQuestoes.size}):", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listaQuestoes) { item ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F5F9), shape = RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("ID: ${item["id"]}", fontSize = 11.sp, color = Color.DarkGray)
                                Text(
                                    text = "#${item["tag"]}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Questão: ${item["pergunta"]}", fontWeight = FontWeight.SemiBold)
                            Text(text = "Resposta: ${item["resposta"]}", color = Color(0xFF1B5E20))
                        }
                    }
                }
            }
        }
    }
}