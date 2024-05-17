/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.host.application.components.treewalker

class TreeEntry<T> {
    val children : HashMap<String, TreeEntry<T>> = hashMapOf()
    var wildcard : String? = null
    var type : T? = null
}

data class Result<T>(val type : T, val params : Map<String, String>)

class TreeWalker<T> {
    private var root = TreeEntry<T>()

    @Throws(IllegalStateException::class)
    fun add(path : String, obj : T) {
        val parts = path.split("/")
        var current = root
        parts.forEach { p ->
            if(p.length > 2 && p[0] == '{' && p[p.length-1] == '}') {
                val wildcard = p.substring(1, p.length - 1)
                if(current.wildcard != null) {
                    if(current.wildcard != wildcard)
                        throw IllegalStateException("Conflicting parameter name old = '${current.wildcard} nre = $wildcard")
                } else {
                    current.wildcard = wildcard
                }
                current = current.children.getOrPut("*") { TreeEntry() }
            } else
                current = current.children.getOrPut(p) { TreeEntry() }
        }
        current.type = obj
    }

    fun match(path : String) : Result<T>? {
        val params : HashMap<String,String> = hashMapOf()
        val parts = path.split("/")
        var current = root
        for (p in parts) {
            if (current.children[p] == null) {
                if (current.children["*"] == null)
                    return null
                else {
                    params[current.wildcard!!] = p
                    current = current.children["*"]!!
                }
            } else
                current = current.children[p]!!
        }
        return Result(current.type!!, params)
    }

    fun build(path : String, params : Map<String,String>) : String {
        val uri : ArrayList<String> = arrayListOf()
        val parts = path.split("/")
        for (p in parts) {
            if(p.length > 2 && p[0] == '{' && p[p.length-1] == '}') {
                val param = p.substring(1, p.length - 1)
                val value = checkNotNull(params[param])
                uri.add(value)
            } else {
                uri.add(p)
            }
        }
        return uri.joinToString("/")
    }
}

