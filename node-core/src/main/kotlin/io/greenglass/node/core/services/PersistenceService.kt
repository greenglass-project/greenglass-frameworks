package io.greenglass.node.core.services

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set

class PersistenceService {

    private val settings = Settings()

    //private val persistentMap : PersistentMap<String, Any> = persistentMapOf()

    fun setInt(key : String, value : Int) = settings.set(key,value)
    fun setLong(key : String, value : Long) = settings.set(key,value)
    fun setBoolean(key : String, value : Boolean) = settings.set(key,value)
    fun setDouble(key : String, value : Double) = settings.set(key,value)

    fun getInt(key : String) = settings.getIntOrNull(key)
    fun getLong(key : String) = settings.getLongOrNull(key)
    fun getBoolean(key : String) = settings.getBooleanOrNull(key)
    fun getDouble(key : String) = settings.getDoubleOrNull(key)

    fun getInt(key : String, default : Int) = settings.getIntOrNull(key) ?: default
    fun getLong(key : String, default : Long) = settings.getLongOrNull(key) ?: default
    fun getBoolean(key : String, default : Boolean) = settings.getBooleanOrNull(key) ?: default
    fun getDouble(key : String, default : Double) = settings.getDoubleOrNull(key) ?: default

    fun nextLongValue(key : String) : Long {
        val current = settings.getLong(key, -1L)
        val next = current+1
        settings.set(key, next)
        return next
    }

    companion object {
        @Volatile
        private var instance: PersistenceService? = null

        val persistence: PersistenceService
            get() {
                if (instance == null) {
                    synchronized(this) {
                        if (instance == null) {
                            instance = PersistenceService()
                        }
                    }
                }
                return instance!!
            }
    }

}
