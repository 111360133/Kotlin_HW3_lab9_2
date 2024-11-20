package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.*
import kotlin.math.pow

class MainActivity : AppCompatActivity() {
    private lateinit var btnCalculate: Button
    private lateinit var edHeight: EditText
    private lateinit var edWeight: EditText
    private lateinit var edAge: EditText
    private lateinit var tvWeightResult: TextView
    private lateinit var tvFatResult: TextView
    private lateinit var tvBmiResult: TextView
    private lateinit var tvProgress: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var llProgress: LinearLayout
    private lateinit var btnBoy: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnCalculate = findViewById(R.id.btnCalculate)
        edHeight = findViewById(R.id.edHeight)
        edWeight = findViewById(R.id.edWeight)
        edAge = findViewById(R.id.edAge)
        tvWeightResult = findViewById(R.id.tvWeightResult)
        tvFatResult = findViewById(R.id.tvFatResult)
        tvBmiResult = findViewById(R.id.tvBmiResult)
        tvProgress = findViewById(R.id.tvProgress)
        progressBar = findViewById(R.id.progressBar)
        llProgress = findViewById(R.id.llProgress)
        btnBoy = findViewById(R.id.btnBoy)

        btnCalculate.setOnClickListener {
            if (!validateInputs()) return@setOnClickListener
            runProgressCoroutine()
        }
    }

    private fun validateInputs(): Boolean {
        val height = edHeight.text.toString()
        val weight = edWeight.text.toString()
        val age = edAge.text.toString()

        return when {
            height.isEmpty() -> {
                showToast(getString(R.string.error_enter_height))
                false
            }
            weight.isEmpty() -> {
                showToast(getString(R.string.error_enter_weight))
                false
            }
            age.isEmpty() -> {
                showToast(getString(R.string.error_enter_age))
                false
            }
            else -> true
        }
    }

    private fun showToast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    private fun runProgressCoroutine() {
        tvWeightResult.text = getString(R.string.result_standard_weight, "無")
        tvFatResult.text = getString(R.string.result_body_fat, "無")
        tvBmiResult.text = getString(R.string.result_bmi, "無")
        progressBar.progress = 0
        tvProgress.text = "0%"
        llProgress.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.Main).launch {
            for (progress in 0..100) {
                delay(50)
                progressBar.progress = progress
                tvProgress.text = "$progress%"
            }

            calculateResults()
            llProgress.visibility = View.GONE
        }
    }

    private fun calculateResults() {
        val height = edHeight.text.toString().toDouble()
        val weight = edWeight.text.toString().toDouble()
        val age = edAge.text.toString().toDouble()
        val bmi = weight / ((height / 100).pow(2))

        val (standWeight, bodyFat) = if (btnBoy.isChecked) {
            Pair((height - 80) * 0.7, 1.39 * bmi + 0.16 * age - 19.34)
        } else {
            Pair((height - 70) * 0.6, 1.39 * bmi + 0.16 * age - 9)
        }

        tvWeightResult.text = getString(R.string.result_standard_weight, String.format("%.2f", standWeight))
        tvFatResult.text = getString(R.string.result_body_fat, String.format("%.2f", bodyFat))
        tvBmiResult.text = getString(R.string.result_bmi, String.format("%.2f", bmi))
    }
}
