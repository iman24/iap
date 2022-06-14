package com.imanancin.iap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.nxtra.iap.CPD
import com.nxtra.iap.GPD

class MainActivity : AppCompatActivity() {

    private lateinit var gpd: GPD
    private lateinit var cpd: CPD
    private val PRODUCT_SKU = "PRODUCT_SKU"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn = findViewById<Button>(R.id.btnPremium)
        val isPr = findViewById<Button>(R.id.isPremium)

        isPr.setOnClickListener {
            cpd = CPD()
            cpd.setSku(PRODUCT_SKU, this)
            val alert = AlertDialog.Builder(this)
            if (cpd.status) {
                alert.apply {
                    setTitle("Premium user")
                    setMessage("Welcome back sir..")
                    setPositiveButton("OK") { _, _ -> }
                }
            } else {
                alert.apply {
                    setTitle("Halo regular user")
                    setMessage("Welcome back sir..")
                    setPositiveButton("OK") { _, _ -> }
                }
            }

            alert.show()
        }

        btn.setOnClickListener {
            gpd = GPD(applicationContext, this, PRODUCT_SKU)
            val alert = AlertDialog.Builder(this)
            if (gpd.isVerified) {
                alert.apply {
                    setTitle("Success")
                    setMessage("Sukses")
                    setPositiveButton("OK") { _, _ -> }
                }
            } else {
                alert.apply {
                    setTitle("Error")
                    setMessage("Error")
                    setPositiveButton("OK") { _, _ -> }
                }
                alert.show()

            }
        }
    }
}
