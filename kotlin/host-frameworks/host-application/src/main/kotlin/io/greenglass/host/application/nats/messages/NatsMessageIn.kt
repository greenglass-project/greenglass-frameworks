/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.host.application.nats.messages

import io.greenglass.host.application.nats.NatsTopic

class NatsMessageIn<T>(val topic : NatsTopic, val obj : T?)
