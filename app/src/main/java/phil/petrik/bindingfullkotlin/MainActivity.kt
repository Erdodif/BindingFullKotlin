package phil.petrik.bindingfullkotlin

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import phil.petrik.bindingfullkotlin.data.RequestTask
import phil.petrik.bindingfullkotlin.databinding.ActivityMainBinding
import phil.petrik.bindingfullkotlin.data.Film
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.film = Film.emptyFilm()
        binding.buttonNew.setOnClickListener {
            binding.film = Film.emptyFilm()
            binding.layoutFilmEditor.visibility = View.VISIBLE
            binding.layoutFilmInspector.visibility = View.GONE
        }
        binding.buttonSync.setOnClickListener { setFilms() }
        binding.buttonSync.callOnClick()
        binding.buttonClose.setOnClickListener {
            binding.layoutFilmInspector.visibility = View.GONE
        }
        binding.buttonCloseEditor.setOnClickListener {
            binding.layoutFilmEditor.visibility = View.GONE
        }
        binding.buttonAlter.setOnClickListener {
            binding.layoutFilmEditor.visibility = View.VISIBLE
            binding.layoutFilmInspector.visibility = View.GONE
        }
        binding.buttonSend.setOnClickListener { sendFilm(binding.film!!) }
    }

    private fun sendFilm(film: Film) {
        if (film.id != null) {
            sendFilm(film, "PATCH")
            return
        }
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialog.setTitle("Módosítás")
        alertDialog.setMessage("Elvégzi a módosításokat?")
        alertDialog.setPositiveButton(
            "Igen"
        ) { _, _ -> sendFilm(film, "POST") }
        alertDialog.setNegativeButton("Nem") { _, _ ->
            binding.layoutFilmEditor.visibility = View.GONE
            binding.film = Film.emptyFilm()
        }
        alertDialog.show()
    }

    private fun sendFilm(film: Film, method: String) {
        Log.d("FilmJSON", film.toJson())
        try {
            val requestTask = RequestTask(
                "/film" + if (film.id == null) "" else "/" + film.id.toString(),
                method,
                film.toJson()
            )
            requestTask.lastTask = lastTask@{
                var toastText = "módosítás"
                if (method == "POST") {
                    toastText = "felvétel"
                }
                if (requestTask.response!!.code < 300) {
                    Toast.makeText(this@MainActivity, "Sikeres $toastText", Toast.LENGTH_SHORT)
                        .show()
                    binding.layoutFilmEditor.visibility = View.GONE
                    return@lastTask
                }
                Log.d(
                    "Hívás / " + requestTask.response!!.code,
                    requestTask.response!!.content
                )
                Toast.makeText(this@MainActivity, "Sikertelen $toastText", Toast.LENGTH_SHORT)
                    .show()
            }
            requestTask.execute()
            setFilms()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun setFilm(id: Int) {
        binding.layoutFilmInspector.visibility = View.VISIBLE
        binding.layoutFilmEditor.visibility = View.GONE
        try {
            val requestTask = RequestTask("/film/$id", "GET")
            requestTask.lastTask = {
                val gson = Gson()
                val content: String = requestTask.response!!.content
                Log.d("Hívás / " + requestTask.response!!.code, content)
                binding.film = gson.fromJson(content, Film::class.java)
            }
            requestTask.execute()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun deleteFilm(id: Int) {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialog.setTitle("Törlés")
        alertDialog.setMessage("Biztos törölni szeretné?")
        alertDialog.setPositiveButton("Igen") { _, _ ->
            try {
                val requestTask = RequestTask("/film/$id", "DELETE")
                requestTask.lastTask = lastTask@{
                    if (requestTask.response!!.code < 300) {
                        Toast.makeText(this@MainActivity, "Sikeresen törölve!", Toast.LENGTH_SHORT)
                            .show()
                        return@lastTask
                    }
                    Log.d(
                        "Hívás / " + requestTask.response!!.code,
                        requestTask.response!!.content
                    )
                    Toast.makeText(this@MainActivity, "Sikertelen törlés!", Toast.LENGTH_SHORT)
                        .show()
                }
                requestTask.execute()
                setFilms()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        alertDialog.setNegativeButton("Nem", null)
        alertDialog.show()
    }

    private fun setFilms() {
        try {
            val requestTask = RequestTask("/film", "GET")
            requestTask.lastTask = {
                binding.layoutFilms.removeAllViews()
                val gson = Gson()
                val content: String = requestTask.response!!.content
                val filmek: Array<Film> =
                    gson.fromJson(content, Array<Film>::class.java)
                Log.d("Hívás / " + requestTask.response!!.code, "FilmCount: " + filmek.size)
                for (film in filmek) {
                    binding.layoutFilms.addView(createFilmButton(film))
                }
            }
            requestTask.execute()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun createFilmButton(film: Film): MaterialButton {
        val buttonFilm = MaterialButton(this@MainActivity)
        buttonFilm.text = film.cim
        val lp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        buttonFilm.layoutParams = lp
        buttonFilm.setOnClickListener { setFilm(film.id!!) }
        buttonFilm.setOnLongClickListener {
            deleteFilm(film.id!!)
            true
        }
        return buttonFilm
    }
}