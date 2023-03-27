// By Sebastian Raaphorst, 2023.

import kotlin.math.min

object Mappings {
    // Given a list of quantized gray densities Q, produce an epimorphism f: [0,1] → Q.
    // For example, for a list of length 5, we get:
    // f: [0,0.2)    ↦ Q[0]
    //    [0.2, 0.4) ↦ Q[1]
    //    [0.4, 0.6) ↦ Q[2]
    //    [0.6, 0.8) ↦ Q[3]
    //    [0.8, 1.0] ↦ Q[4]
    // Note there is a very slight bias towards the largest value.
    fun <T> createQuantizingEpimorphism(quantizedGrays: List<T>): (Double) -> T = { d ->
        val idx = min((d * quantizedGrays.size).toInt(), quantizedGrays.size - 1)
        quantizedGrays[idx]
    }

    // Given a list of quantized gray densities Q, produce a monomorphism g: Q → [0,1] such that:
    // for q ∈ Q, g(q) is the midpoint of the subinterval [a,b) / [a,b] ⊆ [0,1] such that
    // f∘g: T → T is the identity.
    // Thus, we have, for example for a list of length 5:
    // g: Q[0] ↦ 0.1
    //    Q[1] ↦ 0.3
    //    Q[2] ↦ 0.5
    //    Q[3] ↦ 0.7
    //    Q[4] ↦ 0.9
    fun <T> createUnquantizingMonomorphism(quantizedGrays: List<T>): (T) -> Double = { t ->
        val idx = quantizedGrays.indexOf(t)
        idx / quantizedGrays.size.toDouble() + (0.5 / quantizedGrays.size)
    }

    // A mapping which has an inverse.
    // This does not have to be a bijective mapping since the size of the sets of the mapping domains does not have
    // to be equal.
    class InvertibleMapping<T, R>(val map: (T) -> R, val inverseMap: (R) -> T) {
        fun flip(): InvertibleMapping<R, T> =
            InvertibleMapping(inverseMap, map)
    }

    fun <T> createInvertibleQuantizedMapping(quantizedList: List<T>): InvertibleMapping<Double, T> =
        InvertibleMapping(
            createQuantizingEpimorphism(quantizedList),
            createUnquantizingMonomorphism(quantizedList)
        )

    // A Double to Double identity mapping.
    val DoubleIdentityMapping = InvertibleMapping<Double, Double>({it}, {it})
}
