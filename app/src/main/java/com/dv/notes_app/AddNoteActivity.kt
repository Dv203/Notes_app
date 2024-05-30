package com.dv.notes_app

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dv.notes_app.Models.Note
import com.dv.notes_app.databinding.ActivityAddNoteBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class AddNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var note : Note
    private var oldNote : Note? = null
    private var isUpdate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try{

            oldNote = intent.getSerializableExtra("current_note")as Note
            binding.etTitle.setText(oldNote?.title)
            binding.etNote.setText(oldNote?.note)
            isUpdate = true
        }catch (e: Exception){

            e.printStackTrace()
        }

        binding.imgCheck.setOnClickListener{

            val color = if(isUpdate){
                oldNote?.cardColor.toString()
            }else{
                randomColor().toString()
            }
            val title = binding.etTitle.text.toString()
            val note_des = binding.etNote.text.toString()

            if(title.isNotEmpty() && note_des.isNotEmpty()){

                val formatter = SimpleDateFormat("EEE , d MMM yyyy HH:mm a")

                note = if(isUpdate){
                     Note(oldNote?.id , title , note_des ,color, formatter.format(Date()))
                }else{
                     Note(null , title, note_des ,color, formatter.format(Date()))
                }
                val intent = Intent()
                intent.putExtra("note",note)
                setResult(Activity.RESULT_OK , intent)
                finish()
            }else{
                Toast.makeText(this@AddNoteActivity, "Enter data" , Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }

        binding.imgBackArrow.setOnClickListener{

            onBackPressed()
        }

    }

    fun randomColor():Int{
        val list = ArrayList<Int>()
        list.add(R.color.Color1)
        list.add(R.color.Color2)
        list.add(R.color.Color3)
        list.add(R.color.Color4)
        list.add(R.color.Color5)
        list.add(R.color.Color6)

        val random = Random(System.currentTimeMillis().toInt()).nextInt(list.size)
        return list[random]
    }
}