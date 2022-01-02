package bd.ac.ru.cse3162

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import bd.ac.ru.cse3162.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
  lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}