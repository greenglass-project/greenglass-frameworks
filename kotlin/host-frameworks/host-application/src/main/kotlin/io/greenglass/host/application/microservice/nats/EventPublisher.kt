/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.host.application.microservice.nats

import kotlinx.coroutines.flow.StateFlow

class EventPublisher<T>(event : String) {
    val flows: HashMap<String, StateFlow<T>> = hashMapOf()



}