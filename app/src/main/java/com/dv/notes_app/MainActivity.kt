package com.dv.notes_app

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.dv.notes_app.Adapter.NotesAdapter
import com.dv.notes_app.Database.NoteDatabase
import com.dv.notes_app.Models.Note
import com.dv.notes_app.Models.NoteViewModel
import com.dv.notes_app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() ,NotesAdapter.NotesClickListener , PopupMenu.OnMenuItemClickListener  {
    private lateinit var binding : ActivityMainBinding
    private lateinit var database : NoteDatabase
     lateinit var viewModel: NoteViewModel
     lateinit var adapter: NotesAdapter
     lateinit var selectedNote : Note

    private val updateNote = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->

        if(result.resultCode == Activity.RESULT_OK){

            val note = result.data?.getSerializableExtra("note")as? Note
            if(note != null){

                viewModel.updateNote(note)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inintUI()

        viewModel = ViewModelProvider(this,
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(NoteViewModel::class.java)

        viewModel.allNotes.observe(this){ list ->

            list?.let {
                adapter.updateList(list)
            }
        }

        database = NoteDatabase.getDatabase(this)
    }


    private fun inintUI() {

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, LinearLayout.VERTICAL)
        adapter = NotesAdapter(this,this)
        binding.recyclerView.adapter = adapter

        val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->

            if(result.resultCode == Activity.RESULT_OK){

                val note = result.data?.getSerializableExtra("note")as? Note
                if(note != null){
                    viewModel.insertNote(note)
                }
            }

        }

        binding.fbAddNote.setOnClickListener {

            val intent = Intent(this , AddNoteActivity::class.java)
            getContent.launch(intent)
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{

            override fun onQueryTextChange(newText: String?): Boolean {
                   if(newText != null){
                       adapter.filterList(newText)
                   }
                return true
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }
        })
    }

    override fun onItemCLicked(note: Note) {

        val intent = Intent(this@MainActivity , AddNoteActivity::class.java)
        intent.putExtra("current_note" , note)
        updateNote.launch(intent)
    }

    override fun onLongItemClicked(note: Note, cardView: CardView) {

        selectedNote = note
        popUpDisplay(cardView)
    }

    private fun popUpDisplay(cardView: CardView){
        val popup = PopupMenu(this , cardView)
        popup.setOnMenuItemClickListener(this@MainActivity)
        popup.inflate(R.menu.pop_up_menu)
        popup.show()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {

        if(item?.itemId == R.id.delete_note){

            viewModel.deleteNote(selectedNote)
            return true
        }
        return false
    }

}