package io.greenglass.node.sparkplug

interface BdSeqProvider {
 fun nextBdSeq(): Long
}