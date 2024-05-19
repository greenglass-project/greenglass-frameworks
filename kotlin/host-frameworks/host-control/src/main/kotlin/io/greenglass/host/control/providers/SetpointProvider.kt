/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.host.control.providers

import io.greenglass.host.control.sparkplug.datatypes.MetricValue
import kotlinx.coroutines.flow.SharedFlow

interface SetpointProvider {
    fun subscribeToSetpoint(instanceId : String, variableId : String) : SharedFlow<MetricValue>
}