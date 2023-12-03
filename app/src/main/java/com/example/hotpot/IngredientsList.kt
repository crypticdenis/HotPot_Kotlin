package com.example.hotpot

import android.content.DialogInterface
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class IngredientsList : AppCompatActivity() {
    private lateinit var tvMeatOption: TextView
    private var selectedMeatOption = booleanArrayOf()
    private val meatList = ArrayList<Int>()
    private val meatArray = arrayOf(
        "Chicken breast", "Beef", "Minced meat",
        "Salmon", "Lamb", "Bacon", "Sausages"
    )
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ingredients_list)

        tvMeatOption = findViewById(R.id.meat_option)

        selectedMeatOption = BooleanArray(meatArray.size)

        tvMeatOption.setOnClickListener {
            val builder = AlertDialog.Builder(this@IngredientsList)

            builder.setTitle("What you got?")
            builder.setCancelable(false)
            builder.setMultiChoiceItems(meatArray, selectedMeatOption,
                DialogInterface.OnMultiChoiceClickListener { _, i, b ->
                    if (b) {
                        meatList.add(i)
                    } else {
                        meatList.remove(i)
                    }
                })

            builder.setPositiveButton("Ok") { _, _ -> }
            builder.show()
        }
    }
}
