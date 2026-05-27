package com.example.bancodequestoes.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "BancoQuestoes.db"
        private const val DATABASE_VERSION = 1

        // Definição da tabela e das colunas
        const val TABLE_QUESTOES = "questoes"
        const val COL_ID = "id"
        const val COL_PERGUNTA = "pergunta"
        const val COL_RESPOSTA = "resposta"
        const val COL_TAG = "tag"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_QUESTOES (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PERGUNTA TEXT NOT NULL,
                $COL_RESPOSTA TEXT NOT NULL,
                $COL_TAG TEXT NOT NULL
            )
        """.trimIndent()
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_QUESTOES")
        onCreate(db)
    }

    // Método para persistir a questão no banco (Equivalente ao addPessoa do PDF)
    fun addQuestao(pergunta: String, resposta: String, tag: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_PERGUNTA, pergunta)
            put(COL_RESPOSTA, resposta)
            put(COL_TAG, tag)
        }
        val result = db.insert(TABLE_QUESTOES, null, values)
        db.close()
        return result != -1L
    }

    // Método para listar todas as questões guardadas
    fun getAllQuestoes(): List<Map<String, String>> {
        val listaQuestoes = mutableListOf<Map<String, String>>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_QUESTOES ORDER BY $COL_ID DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val questao = mapOf(
                    "id" to cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)).toString(),
                    "pergunta" to cursor.getString(cursor.getColumnIndexOrThrow(COL_PERGUNTA)),
                    "resposta" to cursor.getString(cursor.getColumnIndexOrThrow(COL_RESPOSTA)),
                    "tag" to cursor.getString(cursor.getColumnIndexOrThrow(COL_TAG))
                )
                listaQuestoes.add(questao)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return listaQuestoes
    }
}