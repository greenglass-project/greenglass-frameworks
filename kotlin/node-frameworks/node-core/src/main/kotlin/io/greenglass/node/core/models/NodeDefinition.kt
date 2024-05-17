/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.node.core.models

import io.greenglass.node.sparkplug.datatypes.MetricDefinition
import kotlinx.serialization.Serializable

@Serializable
class NodeDefinition (
    val type : String,
    val name : String,
    val description : String,
    val image : String,
    val metrics : List<MetricDefinition>,
    val drivers : List<Driver>
)
