/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.host.control.datatypes

import io.greenglass.host.control.models.variableidentifiers.EonNodeId

class NodeIdValue(val nodeName : String, val nodeId : String?) {
    constructor(n : EonNodeId) : this(
        nodeName = n.nodeName,
        nodeId = n.nodeId
    )
}
