/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.host.control.models

import io.greenglass.host.control.models.variableidentifiers.ValueProviderIdentifier

open class SystemDefinition (
    val systemId : String,
    val name : String,
    val description : String,
    val nodes : List<EonNode>,
    val valueProviders : List<ValueProviderIdentifier>?,
    val processes : List<Process>,
) {
    companion object
}