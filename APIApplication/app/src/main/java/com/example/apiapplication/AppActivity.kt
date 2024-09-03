package com.example.apiapplication

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class AppActivity : AppCompatActivity() {

    private lateinit var spinnerBaseCurrency: Spinner
    private lateinit var spinnerTargetCurrency: Spinner
    private lateinit var etAmount: EditText
    private lateinit var btnConvert: Button
    private lateinit var tvResult: TextView
    private val apiKey = "4628b0f96ca8199ffce5977dc934126738d1be8e"
    private val TAG = "CurrencyConverter"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)

        spinnerBaseCurrency = findViewById(R.id.spinnerBaseCurrency)
        spinnerTargetCurrency = findViewById(R.id.spinnerTargetCurrency)
        etAmount = findViewById(R.id.etAmount)
        btnConvert = findViewById(R.id.btnConvert)
        tvResult = findViewById(R.id.tvResult)

        // Example list of currency codes
        val currencies = arrayOf("ZAR", "USD", "EUR", "GBP", "JPY", "AUD", "CAD", "CHF", "CNY", "INR")

        // Set up the spinners
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerBaseCurrency.adapter = adapter
        spinnerTargetCurrency.adapter = adapter

        btnConvert.setOnClickListener {
            val baseCurrency = spinnerBaseCurrency.selectedItem.toString().trim()
            val targetCurrency = spinnerTargetCurrency.selectedItem.toString().trim()
            val amount = etAmount.text.toString().trim()

            if (baseCurrency.isNotEmpty() && targetCurrency.isNotEmpty() && amount.isNotEmpty()) {
                convertCurrency(baseCurrency, targetCurrency, amount)
            } else {
                tvResult.text = "Please fill in all fields"
            }
        }
    }

    private fun convertCurrency(baseCurrency: String, targetCurrency: String, amount: String) {
        val client = OkHttpClient()

        val url = "https://api.getgeoapi.com/v2/currency/convert?api_key=$apiKey&from=$baseCurrency&to=$targetCurrency&amount=$amount&format=json"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e(TAG, "Request failed: ${e.message}")
                runOnUiThread {
                    tvResult.text = "Request failed: ${e.message}"
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val responseData = response.body?.string()
                Log.d(TAG, "Response data: $responseData")

                if (responseData != null) {
                    val jsonResponse = JSONObject(responseData)
                    val status = jsonResponse.optString("status", "error")
                    if (status == "success") {
                        val rates = jsonResponse.getJSONObject("rates")
                        val targetCurrencyData = rates.optJSONObject(targetCurrency)
                        if (targetCurrencyData != null) {
                            val rateForAmount = targetCurrencyData.optString("rate_for_amount", "N/A")

                            runOnUiThread {
                                tvResult.text = "$rateForAmount $targetCurrency"
                            }
                        } else {
                            runOnUiThread {
                                tvResult.text = "$targetCurrency"
                            }
                        }
                    } else {
                        runOnUiThread {
                            tvResult.text = "${jsonResponse.optString("message", "Unknown error")}"
                        }
                    }
                } else {
                    runOnUiThread {
                        tvResult.text = "No response data received"
                    }
                }
            }
        })
    }
}
