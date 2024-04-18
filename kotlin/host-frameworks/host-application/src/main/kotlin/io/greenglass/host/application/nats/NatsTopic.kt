package io.greenglass.host.application.nats

class NatsTopic(topic : String) {

    val subscriptionTopic : String
    var subject : String? = null

    private data class ParameterDescription(val name : String, val position : Int)
    private val paramDescrs : ArrayList<ParameterDescription> = arrayListOf()
    private val parameters : HashMap<String, String> = hashMapOf()

    init {
        val st : ArrayList<String> = arrayListOf()
        topic.split(".").forEachIndexed { index, s ->
            if(s.startsWith('{') && s.endsWith('}') && s.length > 2){
                val param = s.substring(1, s.length-1)
                paramDescrs.add(ParameterDescription(param, index))
                st.add("*")
            } else {
                st.add(s)
            }
        }
        subscriptionTopic = st.joinToString(".")
    }

    fun setParameters(subject : String) {
        this.subject = subject
        val parts = subject.split(".")
        paramDescrs.forEach { pd ->
            if(pd.position < parts.size)
                parameters[pd.name] = parts[pd.position]
        }
    }

    fun stringParam(name : String) : String {
        return checkNotNull(parameters[name] as? String)
    }

    fun copyValues(outTopic : String) : String {
        val st : ArrayList<String> = arrayListOf()
        outTopic.split(".").forEachIndexed { index, s ->
            if(s.startsWith('{') && s.endsWith('}') && s.length > 2){
                val param = s.substring(1, s.length-1)
                st.add(parameters[param] as String)
            } else {
                st.add(s)
            }
        }
        return st.joinToString(".")
    }
}