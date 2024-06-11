package org.example.infrastructure

import org.apache.commons.math3.ml.clustering.Clusterable


class DoubleArrayWrapper(private val points: DoubleArray) : Clusterable {
    override fun getPoint(): DoubleArray {
        return points
    }
}