package phil.petrik.bindingfullkotlin.data

import android.os.AsyncTask
import java.io.IOException

class RequestTask : AsyncTask<Void?, Void?, Response?> {
    private var conn: RetoolConnection
    private var params: String?
    var response: Response?
    var lastTask: (()->Unit)?
    set(value) {
        field = value
    }

    constructor(url: String, method: String, paramsJson: String?, lastTask: ()->Unit) {
        conn = RetoolConnection(url, method)
        params = paramsJson
        response = null
        this.lastTask = lastTask
    }

    constructor(url: String, method: String, paramsJson: String?) {
        conn = RetoolConnection(url, method)
        params = paramsJson
        response = null
        lastTask = null
    }

    constructor(url: String, method: String, lastTask: ()->Unit) {
        conn = RetoolConnection(url, method)
        params = null
        response = null
        this.lastTask = lastTask
    }

    constructor(url: String, method: String) {
        conn = RetoolConnection(url, method)
        params = null
        response = null
        lastTask = null
    }

    override fun onPreExecute() {}
    override fun onPostExecute(r: Response?) {
        response = r
        lastTask!!.invoke()
    }

    override fun doInBackground(vararg voids: Void?): Response? {
        val r: Response? = null
        try {
            if (params != null) {
                conn.putJSON(params)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return try {
            if (params == null) {
                return if (conn.getrequestMethod().equals("GET")) {
                    conn.getCall()
                } else conn.deleteCall()
            }
            if (conn.getrequestMethod().equals("POST")) {
                conn.postCall(params)
            } else conn.patchCall(params)
        } catch (e: IOException) {
            e.printStackTrace()
            Response(600, e.message!!)
        }
    }

}