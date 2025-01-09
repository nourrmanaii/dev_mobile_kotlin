package com.nour.todo.share

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nour.todo.databinding.ActivityShareTaskBinding

class ShareTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShareTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShareTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Récupérer la description transmise via l'intent
        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)

        if (sharedText != null) {
            binding.editTextDescription.setText(sharedText) // Afficher la description dans le formulaire
        }

        binding.saveButton.setOnClickListener {
            saveTask(sharedText) // Sauvegarde ou logique pour valider la tâche
        }
    }

    private fun saveTask(sharedText: String?) {
        if (sharedText.isNullOrEmpty()) {
            Toast.makeText(this, "No text shared to save", Toast.LENGTH_SHORT).show()
        } else {
            // Logique pour envoyer cette tâche comme résultat
            val taskIntent = intent.apply {
                putExtra("task", sharedText)
            }
            setResult(RESULT_OK, taskIntent)
            finish()
        }
    }
}
