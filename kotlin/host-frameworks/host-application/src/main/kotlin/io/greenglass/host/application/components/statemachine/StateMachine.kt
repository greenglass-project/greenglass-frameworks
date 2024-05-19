
/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.host.application.components.statemachine

import kotlinx.coroutines.flow.*
import io.github.oshai.kotlinlogging.KotlinLogging

/*fun Flow<Event>.eventToValue(vararg events : Pair<Event, Any>) : Flow<MetricValue> =
    transform { e ->
        events.toMap().get(e).takeIf { it != null }.let { emit(MetricValue(it!!)) }
    }*/

open class Event
open class State

class StateMachine<T : State>(initialState : T) {

    private data class Transition<T>(val state : T, val event : Event?)
    private val states : HashMap<T, HashMap<Event, Transition<T>>> = hashMapOf()
    private val subscriptions : HashMap<Event, ArrayList<MutableSharedFlow<Event>>>  = hashMapOf()
    private val logger = KotlinLogging.logger {}

    private var state = initialState

    fun setTransition(state : T, event : Event, newState : T, transitionEvent : Event? =  null ) : StateMachine<T> {
        val stateContext = states.getOrPut(state) { hashMapOf() }
        stateContext[event] = Transition(newState, transitionEvent)
        return this
    }

    fun subscribe(vararg events : Event) : SharedFlow<Event> {
        val flow = MutableSharedFlow<Event>()
        events.forEach { e ->
            val subscribers = subscriptions.getOrPut(e) { arrayListOf()}
            subscribers.add(flow)
        }
        return flow.asSharedFlow()
    }

    suspend fun changeState(event : Event) {
        val transition = states[state]!!.get(event)
        if (transition != null) {
            state = transition.state
            if(transition.event != null)
                subscriptions[transition.event]?.forEach { s -> s.emit(transition.event) }
        } else {
            logger.warn { "No transition found for state ${state::class.simpleName} event ${event::class.simpleName}"  }
        }
    }
}

