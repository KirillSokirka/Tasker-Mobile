import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesService(private val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    fun saveData(key: String, value: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun retrieveData(key: String): String? {
        return sharedPreferences.getString(key, null)
    }
}
