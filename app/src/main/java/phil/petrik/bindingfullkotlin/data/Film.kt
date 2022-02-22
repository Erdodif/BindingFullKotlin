package phil.petrik.bindingfullkotlin.data

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import phil.petrik.bindingfullkotlin.BR

class Film(
    id: Int?,
    cim: String?,
    kategoria: String?,
    hossz: Int?,
    ertekeles: Int?
) :
    BaseObservable() {

    @get:Bindable
    @field:Expose(serialize = false) var id: Int? = null
    set (value){
        field = value
        notifyPropertyChanged(BR.id)
    }

    @get:Bindable
    @field:Expose(serialize = false) var cim: String? = null
    set (value){
        field = value
        notifyPropertyChanged(BR.cim)
    }

    @get:Bindable
    @field:Expose(serialize = false) var kategoria: String? = null
    set (value){
        field = value
        notifyPropertyChanged(BR.kategoria)
    }

    @get:Bindable
    @field:Expose(serialize = false) var hossz: Int? = null
    set (value){
        field = value
        notifyPropertyChanged(BR.hossz)
    }

    @get:Bindable
    @field:SerializedName("ertekels")
    @field:Expose(serialize = false) var ertekeles: Int? = null
    set (value){
        field = value
        notifyPropertyChanged(BR.ertekeles)
    }


    @get:Bindable
    var hosszString: String
        get() = if (hossz == null) "" else hossz.toString()
        set(hossz) {
            try {
                this.hossz = hossz.toInt()
            } catch (e: Exception) {
                this.hossz = 0
            }
            notifyPropertyChanged(BR.hosszString)
        }

    @get:Bindable
    var ertekelesString: String
        get() = if (ertekeles == null) "" else ertekeles.toString()
        set(ertekels) {
            try {
                ertekeles = ertekels.toInt()
            } catch (e: Exception) {
                ertekeles = 0
            }
            notifyPropertyChanged(BR.ertekelesString)
        }


    fun setHossz(hossz: Int) {
        this.hossz = hossz
        notifyPropertyChanged(BR.hosszString)
    }

    fun setErtekeles(ertekeles: Int) {
        this.ertekeles = ertekeles
        notifyPropertyChanged(BR.ertekelesString)
    }

    init {
        this.id = id
        this.cim = cim
        this.kategoria = kategoria
        this.hossz = hossz
        this.ertekeles = ertekeles
    }

    override fun toString(): String {
        return ("id:" + id + ", cim:" + cim + ", kategoria:" + kategoria
                + ", hossz:" + hossz + ", ertekeles:" + ertekeles)
    }

    fun toJson(): String {
        val gson: Gson = GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
        return gson.toJson(this)
    }

    companion object {
        fun emptyFilm(): Film {
            return Film(null, null, null, null, null)
        }
    }
}