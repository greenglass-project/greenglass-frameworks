/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.node.core.services

import java.util.prefs.Preferences

class PersistenceService {

    private val settings = Preferences.userRoot()

    fun setInt(key : String, value : Int) = settings.putInt(key,value)
    fun setLong(key : String, value : Long) = settings.putLong(key,value)
    fun setBoolean(key : String, value : Boolean) = settings.putBoolean(key,value)
    fun setDouble(key : String, value : Double) = settings.putDouble(key,value)
    fun setString(key : String, value : String) = settings.put(key,value)

    fun getInt(key : String, default : Int) = settings.getInt(key, default)
    fun getLong(key : String, default : Long) = settings.getLong(key, default)
    fun getBoolean(key : String, default : Boolean) = settings.getBoolean(key, default)
    fun getDouble(key : String, default : Double) = settings.getDouble(key, default)
    fun getString(key : String, default : String) = settings.get(key,default)

    fun nextLongValue(key : String) : Long {
        val current = settings.getLong(key, -1L)
        val next = current+1
        settings.putLong(key, next)
        return next
    }

    fun remove(key : String) = settings.remove(key)
    fun clear() = settings.clear()
}
