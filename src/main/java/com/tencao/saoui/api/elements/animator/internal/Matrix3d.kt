package com.tencao.saoui.api.elements.animator.internal

/**
 * This is a barebones copy of the javax Matrix3d
 */

import com.tencao.saoui.util.*
import net.minecraft.util.math.vector.Matrix3f
import java.io.Serializable
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * A double precision floating point 3 by 3 matrix.
 * Primarily to support 3D rotations.
 *
 */
class Matrix3d
/**
 * Constructs and initializes a Matrix3d from the specified nine values.
 * @param m00 the [0][0] element
 * @param m01 the [0][1] element
 * @param m02 the [0][2] element
 * @param m10 the [1][0] element
 * @param m11 the [1][1] element
 * @param m12 the [1][2] element
 * @param m20 the [2][0] element
 * @param m21 the [2][1] element
 * @param m22 the [2][2] element
 */(
    /**
     * The first matrix element in the first row.
     */
    var m00: Double,
    /**
     * The second matrix element in the first row.
     */
    var m01: Double,
    /**
     * The third matrix element in the first row.
     */
    var m02: Double,
    /**
     * The first matrix element in the second row.
     */
    var m10: Double,
    /**
     * The second matrix element in the second row.
     */
    var m11: Double,
    /**
     * The third matrix element in the second row.
     */
    var m12: Double,
    /**
     * The first matrix element in the third row.
     */
    var m20: Double,
    /**
     * The second matrix element in the third row.
     */
    var m21: Double,
    /**
     * The third matrix element in the third row.
     */
    var m22: Double
) : Serializable, Cloneable {
    /**
     * Get the first matrix element in the first row.
     * @return Returns the m00.
     * @since vecmath 1.5
     */
    /**
     * Set the first matrix element in the first row.
     *
     * @param m00 The m00 to set.
     *
     * @since vecmath 1.5
     */
    /**
     * Get the second matrix element in the first row.
     *
     * @return Returns the m01.
     *
     * @since vecmath 1.5
     */
    /**
     * Set the second matrix element in the first row.
     *
     * @param m01 The m01 to set.
     *
     * @since vecmath 1.5
     */
    /**
     * Get the third matrix element in the first row.
     *
     * @return Returns the m02.
     *
     * @since vecmath 1.5
     */
    /**
     * Set the third matrix element in the first row.
     *
     * @param m02 The m02 to set.
     *
     * @since vecmath 1.5
     */
    /**
     * Get first matrix element in the second row.
     *
     * @return Returns the m10.
     *
     * @since vecmath 1.5
     */
    /**
     * Set first matrix element in the second row.
     *
     * @param m10 The m10 to set.
     *
     * @since vecmath 1.5
     */
    /**
     * Get second matrix element in the second row.
     *
     * @return Returns the m11.
     *
     * @since vecmath 1.5
     */
    /**
     * Set the second matrix element in the second row.
     *
     * @param m11 The m11 to set.
     *
     * @since vecmath 1.5
     */
    /**
     * Get the third matrix element in the second row.
     *
     * @return Returns the m12.
     *
     * @since vecmath 1.5
     */
    /**
     * Set the third matrix element in the second row.
     *
     * @param m12 The m12 to set.
     *
     * @since vecmath 1.5
     */
    /**
     * Get the first matrix element in the third row.
     *
     * @return Returns the m20.
     *
     * @since vecmath 1.5
     */
    /**
     * Set the first matrix element in the third row.
     *
     * @param m20 The m20 to set.
     *
     * @since vecmath 1.5
     */
    /**
     * Get the second matrix element in the third row.
     *
     * @return Returns the m21.
     *
     * @since vecmath 1.5
     */
    /**
     * Set the second matrix element in the third row.
     *
     * @param m21 The m21 to set.
     *
     * @since vecmath 1.5
     */
    /**
     * Get the third matrix element in the third row .
     *
     * @return Returns the m22.
     *
     * @since vecmath 1.5
     */
    /**
     * Set the third matrix element in the third row.
     *
     * @param m22 The m22 to set.
     *
     * @since vecmath 1.5
     */

    /**
     * Returns a string that contains the values of this Matrix3d.
     * @return the String representation
     */
    override fun toString(): String {
        return """
             $m00, $m01, $m02
             $m10, $m11, $m12
             $m20, $m21, $m22
             
        """.trimIndent()
    }

    /**
     * Sets the scale component of the current matrix by factoring
     * out the current scale (by doing an SVD) and multiplying by
     * the new scale.
     * @param scale  the new scale amount
     */
    /**
     * Performs an SVD normalization of this matrix to calculate
     * and return the uniform scale factor. If the matrix has non-uniform
     * scale factors, the largest of the x, y, and z scale factors will
     * be returned. This matrix is not modified.
     * @return the scale factor of this matrix
     */
    var scale: Double
        get() {
            val tmp_scale = DoubleArray(3) // scratch matrix
            val tmp_rot = DoubleArray(9) // scratch matrix
            getScaleRotate(tmp_scale, tmp_rot)
            return max3(tmp_scale)
        }
        set(scale) {
            val tmp_rot = DoubleArray(9) // scratch matrix
            val tmp_scale = DoubleArray(3) // scratch matrix
            getScaleRotate(tmp_scale, tmp_rot)
            m00 = tmp_rot[0] * scale
            m01 = tmp_rot[1] * scale
            m02 = tmp_rot[2] * scale
            m10 = tmp_rot[3] * scale
            m11 = tmp_rot[4] * scale
            m12 = tmp_rot[5] * scale
            m20 = tmp_rot[6] * scale
            m21 = tmp_rot[7] * scale
            m22 = tmp_rot[8] * scale
        }

    /**
     * Adds a scalar to each component of this matrix.
     * @param scalar  the scalar adder
     */
    fun add(scalar: Double) {
        m00 += scalar
        m01 += scalar
        m02 += scalar
        m10 += scalar
        m11 += scalar
        m12 += scalar
        m20 += scalar
        m21 += scalar
        m22 += scalar
    }

    /**
     * Adds a scalar to each component of the matrix m1 and places
     * the result into this.  Matrix m1 is not modified.
     * @param scalar  the scalar adder
     * @param m1  the original matrix values
     */
    fun add(scalar: Double, m1: Matrix3d) {
        m00 = m1.m00 + scalar
        m01 = m1.m01 + scalar
        m02 = m1.m02 + scalar
        m10 = m1.m10 + scalar
        m11 = m1.m11 + scalar
        m12 = m1.m12 + scalar
        m20 = m1.m20 + scalar
        m21 = m1.m21 + scalar
        m22 = m1.m22 + scalar
    }

    /**
     * Sets the value of this matrix to the matrix sum of matrices m1 and m2.
     * @param m1 the first matrix
     * @param m2 the second matrix
     */
    fun add(m1: Matrix3d, m2: Matrix3d) {
        m00 = m1.m00 + m2.m00
        m01 = m1.m01 + m2.m01
        m02 = m1.m02 + m2.m02
        m10 = m1.m10 + m2.m10
        m11 = m1.m11 + m2.m11
        m12 = m1.m12 + m2.m12
        m20 = m1.m20 + m2.m20
        m21 = m1.m21 + m2.m21
        m22 = m1.m22 + m2.m22
    }

    /**
     * Sets the value of this matrix to the sum of itself and matrix m1.
     * @param m1 the other matrix
     */
    fun add(m1: Matrix3d) {
        m00 += m1.m00
        m01 += m1.m01
        m02 += m1.m02
        m10 += m1.m10
        m11 += m1.m11
        m12 += m1.m12
        m20 += m1.m20
        m21 += m1.m21
        m22 += m1.m22
    }

    /**
     * Sets the value of this matrix to the double value of the Matrix3f
     * argument.
     * @param m1 the matrix3d to be converted to double
     */
    fun set(m1: Matrix3f) {
        m00 = m1.m00.toDouble()
        m01 = m1.m01.toDouble()
        m02 = m1.m02.toDouble()
        m10 = m1.m10.toDouble()
        m11 = m1.m11.toDouble()
        m12 = m1.m12.toDouble()
        m20 = m1.m20.toDouble()
        m21 = m1.m21.toDouble()
        m22 = m1.m22.toDouble()
    }

    /**
     * Sets the value of this matrix to the value of the Matrix3d
     * argument.
     * @param m1 the source matrix3d
     */
    fun set(m1: Matrix3d) {
        m00 = m1.m00
        m01 = m1.m01
        m02 = m1.m02
        m10 = m1.m10
        m11 = m1.m11
        m12 = m1.m12
        m20 = m1.m20
        m21 = m1.m21
        m22 = m1.m22
    }

    /**
     * Sets the values in this Matrix3d equal to the row-major
     * array parameter (ie, the first three elements of the
     * array will be copied into the first row of this matrix, etc.).
     * @param m  the double precision array of length 9
     */
    fun set(m: DoubleArray) {
        m00 = m[0]
        m01 = m[1]
        m02 = m[2]
        m10 = m[3]
        m11 = m[4]
        m12 = m[5]
        m20 = m[6]
        m21 = m[7]
        m22 = m[8]
    }

    /**
     * Sets the value of this matrix to a scale matrix with
     * the passed scale amount.
     * @param scale the scale factor for the matrix
     */
    fun set(scale: Double) {
        m00 = scale
        m01 = 0.0
        m02 = 0.0
        m10 = 0.0
        m11 = scale
        m12 = 0.0
        m20 = 0.0
        m21 = 0.0
        m22 = scale
    }

    /**
     * Returns true if all of the data members of Matrix3d m1 are
     * equal to the corresponding data members in this Matrix3d.
     * @param m1  the matrix with which the comparison is made
     * @return true or false
     */
    fun equals(m1: Matrix3d): Boolean {
        return try {
            m00 == m1.m00 && m01 == m1.m01 && m02 == m1.m02 && m10 == m1.m10 && m11 == m1.m11 && m12 == m1.m12 && m20 == m1.m20 && m21 == m1.m21 && m22 == m1.m22
        } catch (e2: NullPointerException) {
            false
        }
    }

    /**
     * Returns true if the Object t1 is of type Matrix3d and all of the
     * data members of t1 are equal to the corresponding data members in
     * this Matrix3d.
     * @param t1  the matrix with which the comparison is made
     * @return true or false
     */
    override fun equals(t1: Any?): Boolean {
        return try {
            val m2 = t1 as Matrix3d?
            m00 == m2!!.m00 && m01 == m2.m01 && m02 == m2.m02 && m10 == m2.m10 && m11 == m2.m11 && m12 == m2.m12 && m20 == m2.m20 && m21 == m2.m21 && m22 == m2.m22
        } catch (e1: ClassCastException) {
            false
        } catch (e2: NullPointerException) {
            false
        }
    }

    /**
     * Returns a hash code value based on the data values in this
     * object.  Two different Matrix3d objects with identical data values
     * (i.e., Matrix3d.equals returns true) will return the same hash
     * code value.  Two objects with different data members may return the
     * same hash value, although this is not likely.
     * @return the integer hash code value
     */
    override fun hashCode(): Int {
        var bits = 1L
        bits = 31L * bits + doubleToLongBits(m00)
        bits = 31L * bits + doubleToLongBits(m01)
        bits = 31L * bits + doubleToLongBits(m02)
        bits = 31L * bits + doubleToLongBits(m10)
        bits = 31L * bits + doubleToLongBits(m11)
        bits = 31L * bits + doubleToLongBits(m12)
        bits = 31L * bits + doubleToLongBits(m20)
        bits = 31L * bits + doubleToLongBits(m21)
        bits = 31L * bits + doubleToLongBits(m22)
        return (bits xor (bits shr 32)).toInt()
    }

    /**
     * perform SVD (if necessary to get rotational component
     */
    fun getScaleRotate(scales: DoubleArray, rots: DoubleArray) {
        val tmp = DoubleArray(9) // scratch matrix
        tmp[0] = m00
        tmp[1] = m01
        tmp[2] = m02
        tmp[3] = m10
        tmp[4] = m11
        tmp[5] = m12
        tmp[6] = m20
        tmp[7] = m21
        tmp[8] = m22
        compute_svd(tmp, scales, rots)
        return
    }

    /**
     * Creates a new object of the same class as this object.
     *
     * @return a clone of this instance.
     * @exception OutOfMemoryError if there is not enough memory.
     * @see java.lang.Cloneable
     *
     * @since vecmath 1.3
     */
    public override fun clone(): Any {
        var m1: Matrix3d? = null
        m1 = try {
            super.clone() as Matrix3d
        } catch (e: CloneNotSupportedException) {
            // this shouldn't happen, since we are Cloneable
            throw InternalError()
        }

        // Also need to create new tmp arrays (no need to actually clone them)
        return m1!!
    }

    companion object {
        // Compatible with 1.1
        const val serialVersionUID = 6837536777072402710L

        // double[]    tmp = new double[9];  // scratch matrix
        // double[]    tmp_rot = new double[9];  // scratch matrix
        // double[]    tmp_scale = new double[3];  // scratch matrix
        private const val EPS = 1.110223024E-16

        fun compute_svd(m: DoubleArray, outScale: DoubleArray, outRot: DoubleArray) {
            var i: Int
            var j: Int
            var g: Double
            var scale: Double
            val u1 = DoubleArray(9)
            val v1 = DoubleArray(9)
            val t1 = DoubleArray(9)
            val t2 = DoubleArray(9)
            val rot = DoubleArray(9)
            val e = DoubleArray(3)
            val scales = DoubleArray(3)
            var converged: Int
            var negCnt = 0
            var cs: Double
            var sn: Double
            val c1: Double
            val c2: Double
            val c3: Double
            val c4: Double
            val s1: Double
            val s2: Double
            val s3: Double
            val s4: Double
            var cl1: Double
            var cl2: Double
            var cl3: Double
            i = 0
            while (i < 9) {
                rot[i] = m[i]
                i++
            }

            // u1
            if (m[3] * m[3] < EPS) {
                u1[0] = 1.0
                u1[1] = 0.0
                u1[2] = 0.0
                u1[3] = 0.0
                u1[4] = 1.0
                u1[5] = 0.0
                u1[6] = 0.0
                u1[7] = 0.0
                u1[8] = 1.0
            } else if (m[0] * m[0] < EPS) {
                t1[0] = m[0]
                t1[1] = m[1]
                t1[2] = m[2]
                m[0] = m[3]
                m[1] = m[4]
                m[2] = m[5]
                m[3] = -t1[0] // zero
                m[4] = -t1[1]
                m[5] = -t1[2]
                u1[0] = 0.0
                u1[1] = 1.0
                u1[2] = 0.0
                u1[3] = -1.0
                u1[4] = 0.0
                u1[5] = 0.0
                u1[6] = 0.0
                u1[7] = 0.0
                u1[8] = 1.0
            } else {
                g = 1.0 / sqrt(m[0] * m[0] + m[3] * m[3])
                c1 = m[0] * g
                s1 = m[3] * g
                t1[0] = c1 * m[0] + s1 * m[3]
                t1[1] = c1 * m[1] + s1 * m[4]
                t1[2] = c1 * m[2] + s1 * m[5]
                m[3] = -s1 * m[0] + c1 * m[3] // zero
                m[4] = -s1 * m[1] + c1 * m[4]
                m[5] = -s1 * m[2] + c1 * m[5]
                m[0] = t1[0]
                m[1] = t1[1]
                m[2] = t1[2]
                u1[0] = c1
                u1[1] = s1
                u1[2] = 0.0
                u1[3] = -s1
                u1[4] = c1
                u1[5] = 0.0
                u1[6] = 0.0
                u1[7] = 0.0
                u1[8] = 1.0
            }

            // u2
            if (m[6] * m[6] < EPS) {
            } else if (m[0] * m[0] < EPS) {
                t1[0] = m[0]
                t1[1] = m[1]
                t1[2] = m[2]
                m[0] = m[6]
                m[1] = m[7]
                m[2] = m[8]
                m[6] = -t1[0] // zero
                m[7] = -t1[1]
                m[8] = -t1[2]
                t1[0] = u1[0]
                t1[1] = u1[1]
                t1[2] = u1[2]
                u1[0] = u1[6]
                u1[1] = u1[7]
                u1[2] = u1[8]
                u1[6] = -t1[0] // zero
                u1[7] = -t1[1]
                u1[8] = -t1[2]
            } else {
                g = 1.0 / sqrt(m[0] * m[0] + m[6] * m[6])
                c2 = m[0] * g
                s2 = m[6] * g
                t1[0] = c2 * m[0] + s2 * m[6]
                t1[1] = c2 * m[1] + s2 * m[7]
                t1[2] = c2 * m[2] + s2 * m[8]
                m[6] = -s2 * m[0] + c2 * m[6]
                m[7] = -s2 * m[1] + c2 * m[7]
                m[8] = -s2 * m[2] + c2 * m[8]
                m[0] = t1[0]
                m[1] = t1[1]
                m[2] = t1[2]
                t1[0] = c2 * u1[0]
                t1[1] = c2 * u1[1]
                u1[2] = s2
                t1[6] = -u1[0] * s2
                t1[7] = -u1[1] * s2
                u1[8] = c2
                u1[0] = t1[0]
                u1[1] = t1[1]
                u1[6] = t1[6]
                u1[7] = t1[7]
            }

            // v1
            if (m[2] * m[2] < EPS) {
                v1[0] = 1.0
                v1[1] = 0.0
                v1[2] = 0.0
                v1[3] = 0.0
                v1[4] = 1.0
                v1[5] = 0.0
                v1[6] = 0.0
                v1[7] = 0.0
                v1[8] = 1.0
            } else if (m[1] * m[1] < EPS) {
                t1[2] = m[2]
                t1[5] = m[5]
                t1[8] = m[8]
                m[2] = -m[1]
                m[5] = -m[4]
                m[8] = -m[7]
                m[1] = t1[2] // zero
                m[4] = t1[5]
                m[7] = t1[8]
                v1[0] = 1.0
                v1[1] = 0.0
                v1[2] = 0.0
                v1[3] = 0.0
                v1[4] = 0.0
                v1[5] = -1.0
                v1[6] = 0.0
                v1[7] = 1.0
                v1[8] = 0.0
            } else {
                g = 1.0 / sqrt(m[1] * m[1] + m[2] * m[2])
                c3 = m[1] * g
                s3 = m[2] * g
                t1[1] = c3 * m[1] + s3 * m[2] // can assign to m[1]?
                m[2] = -s3 * m[1] + c3 * m[2] // zero
                m[1] = t1[1]
                t1[4] = c3 * m[4] + s3 * m[5]
                m[5] = -s3 * m[4] + c3 * m[5]
                m[4] = t1[4]
                t1[7] = c3 * m[7] + s3 * m[8]
                m[8] = -s3 * m[7] + c3 * m[8]
                m[7] = t1[7]
                v1[0] = 1.0
                v1[1] = 0.0
                v1[2] = 0.0
                v1[3] = 0.0
                v1[4] = c3
                v1[5] = -s3
                v1[6] = 0.0
                v1[7] = s3
                v1[8] = c3
            }

            // u3
            if (m[7] * m[7] < EPS) {
            } else if (m[4] * m[4] < EPS) {
                t1[3] = m[3]
                t1[4] = m[4]
                t1[5] = m[5]
                m[3] = m[6] // zero
                m[4] = m[7]
                m[5] = m[8]
                m[6] = -t1[3] // zero
                m[7] = -t1[4] // zero
                m[8] = -t1[5]
                t1[3] = u1[3]
                t1[4] = u1[4]
                t1[5] = u1[5]
                u1[3] = u1[6]
                u1[4] = u1[7]
                u1[5] = u1[8]
                u1[6] = -t1[3] // zero
                u1[7] = -t1[4]
                u1[8] = -t1[5]
            } else {
                g = 1.0 / sqrt(m[4] * m[4] + m[7] * m[7])
                c4 = m[4] * g
                s4 = m[7] * g
                t1[3] = c4 * m[3] + s4 * m[6]
                m[6] = -s4 * m[3] + c4 * m[6] // zero
                m[3] = t1[3]
                t1[4] = c4 * m[4] + s4 * m[7]
                m[7] = -s4 * m[4] + c4 * m[7]
                m[4] = t1[4]
                t1[5] = c4 * m[5] + s4 * m[8]
                m[8] = -s4 * m[5] + c4 * m[8]
                m[5] = t1[5]
                t1[3] = c4 * u1[3] + s4 * u1[6]
                u1[6] = -s4 * u1[3] + c4 * u1[6]
                u1[3] = t1[3]
                t1[4] = c4 * u1[4] + s4 * u1[7]
                u1[7] = -s4 * u1[4] + c4 * u1[7]
                u1[4] = t1[4]
                t1[5] = c4 * u1[5] + s4 * u1[8]
                u1[8] = -s4 * u1[5] + c4 * u1[8]
                u1[5] = t1[5]
            }
            t2[0] = m[0]
            t2[1] = m[4]
            t2[2] = m[8]
            e[0] = m[1]
            e[1] = m[5]
            if (e[0] * e[0] < EPS && e[1] * e[1] < EPS) {
            } else {
                compute_qr(t2, e, u1, v1)
            }
            scales[0] = t2[0]
            scales[1] = t2[1]
            scales[2] = t2[2]

            // Do some optimization here. If scale is unity, simply return the rotation matric.
            if (almostEqual(abs(scales[0]), 1.0) &&
                almostEqual(abs(scales[1]), 1.0) &&
                almostEqual(abs(scales[2]), 1.0)
            ) {
                //  System.out.println("Scale components almost to 1.0");
                i = 0
                while (i < 3) {
                    if (scales[i] < 0.0) negCnt++
                    i++
                }
                if (negCnt == 0 || negCnt == 2) {
                    // System.out.println("Optimize!!");
                    outScale[2] = 1.0
                    outScale[1] = outScale[2]
                    outScale[0] = outScale[1]
                    i = 0
                    while (i < 9) {
                        outRot[i] = rot[i]
                        i++
                    }
                    return
                }
            }
            transpose_mat(u1, t1)
            transpose_mat(v1, t2)

            /*
	  System.out.println("t1 is \n" + t1);
	  System.out.println("t1="+t1[0]+" "+t1[1]+" "+t1[2]);
	  System.out.println("t1="+t1[3]+" "+t1[4]+" "+t1[5]);
	  System.out.println("t1="+t1[6]+" "+t1[7]+" "+t1[8]);

	  System.out.println("t2 is \n" + t2);
	  System.out.println("t2="+t2[0]+" "+t2[1]+" "+t2[2]);
	  System.out.println("t2="+t2[3]+" "+t2[4]+" "+t2[5]);
	  System.out.println("t2="+t2[6]+" "+t2[7]+" "+t2[8]);
	  */svdReorder(m, t1, t2, scales, outRot, outScale)
        }

        fun svdReorder(
            m: DoubleArray,
            t1: DoubleArray,
            t2: DoubleArray,
            scales: DoubleArray,
            outRot: DoubleArray,
            outScale: DoubleArray
        ) {
            val out = IntArray(3)
            val `in` = IntArray(3)
            val in0: Int
            val in1: Int
            val in2: Int
            var index: Int
            var i: Int
            val mag = DoubleArray(3)
            val rot = DoubleArray(9)

            // check for rotation information in the scales
            if (scales[0] < 0.0) { // move the rotation info to rotation matrix
                scales[0] = -scales[0]
                t2[0] = -t2[0]
                t2[1] = -t2[1]
                t2[2] = -t2[2]
            }
            if (scales[1] < 0.0) { // move the rotation info to rotation matrix
                scales[1] = -scales[1]
                t2[3] = -t2[3]
                t2[4] = -t2[4]
                t2[5] = -t2[5]
            }
            if (scales[2] < 0.0) { // move the rotation info to rotation matrix
                scales[2] = -scales[2]
                t2[6] = -t2[6]
                t2[7] = -t2[7]
                t2[8] = -t2[8]
            }
            mat_mul(t1, t2, rot)

            // check for equal scales case  and do not reorder
            if (almostEqual(abs(scales[0]), abs(scales[1])) &&
                almostEqual(abs(scales[1]), abs(scales[2]))
            ) {
                i = 0
                while (i < 9) {
                    outRot[i] = rot[i]
                    i++
                }
                i = 0
                while (i < 3) {
                    outScale[i] = scales[i]
                    i++
                }
            } else {
                // sort the order of the results of SVD
                if (scales[0] > scales[1]) {
                    if (scales[0] > scales[2]) {
                        if (scales[2] > scales[1]) {
                            out[0] = 0
                            out[1] = 2
                            out[2] = 1 // xzy
                        } else {
                            out[0] = 0
                            out[1] = 1
                            out[2] = 2 // xyz
                        }
                    } else {
                        out[0] = 2
                        out[1] = 0
                        out[2] = 1 // zxy
                    }
                } else { // y > x
                    if (scales[1] > scales[2]) {
                        if (scales[2] > scales[0]) {
                            out[0] = 1
                            out[1] = 2
                            out[2] = 0 // yzx
                        } else {
                            out[0] = 1
                            out[1] = 0
                            out[2] = 2 // yxz
                        }
                    } else {
                        out[0] = 2
                        out[1] = 1
                        out[2] = 0 // zyx
                    }
                }

                /*
		System.out.println("\nscales="+scales[0]+" "+scales[1]+" "+scales[2]);
		System.out.println("\nrot="+rot[0]+" "+rot[1]+" "+rot[2]);
		System.out.println("rot="+rot[3]+" "+rot[4]+" "+rot[5]);
		System.out.println("rot="+rot[6]+" "+rot[7]+" "+rot[8]);
		*/

                // sort the order of the input matrix
                mag[0] = m[0] * m[0] + m[1] * m[1] + m[2] * m[2]
                mag[1] = m[3] * m[3] + m[4] * m[4] + m[5] * m[5]
                mag[2] = m[6] * m[6] + m[7] * m[7] + m[8] * m[8]
                if (mag[0] > mag[1]) {
                    if (mag[0] > mag[2]) {
                        if (mag[2] > mag[1]) {
                            // 0 - 2 - 1
                            in0 = 0
                            in2 = 1
                            in1 = 2 // xzy
                        } else {
                            // 0 - 1 - 2
                            in0 = 0
                            in1 = 1
                            in2 = 2 // xyz
                        }
                    } else {
                        // 2 - 0 - 1
                        in2 = 0
                        in0 = 1
                        in1 = 2 // zxy
                    }
                } else { // y > x   1>0
                    if (mag[1] > mag[2]) {
                        if (mag[2] > mag[0]) {
                            // 1 - 2 - 0
                            in1 = 0
                            in2 = 1
                            in0 = 2 // yzx
                        } else {
                            // 1 - 0 - 2
                            in1 = 0
                            in0 = 1
                            in2 = 2 // yxz
                        }
                    } else {
                        // 2 - 1 - 0
                        in2 = 0
                        in1 = 1
                        in0 = 2 // zyx
                    }
                }
                index = out[in0]
                outScale[0] = scales[index]
                index = out[in1]
                outScale[1] = scales[index]
                index = out[in2]
                outScale[2] = scales[index]
                index = out[in0]
                outRot[0] = rot[index]
                index = out[in0] + 3
                outRot[0 + 3] = rot[index]
                index = out[in0] + 6
                outRot[0 + 6] = rot[index]
                index = out[in1]
                outRot[1] = rot[index]
                index = out[in1] + 3
                outRot[1 + 3] = rot[index]
                index = out[in1] + 6
                outRot[1 + 6] = rot[index]
                index = out[in2]
                outRot[2] = rot[index]
                index = out[in2] + 3
                outRot[2 + 3] = rot[index]
                index = out[in2] + 6
                outRot[2 + 6] = rot[index]
            }
        }

        fun compute_qr(s: DoubleArray, e: DoubleArray, u: DoubleArray, v: DoubleArray): Int {
            var i: Int
            var j: Int
            var converged: Boolean
            var shift: Double
            var ssmin: Double
            var ssmax: Double
            var r: Double
            val cosl = DoubleArray(2)
            val cosr = DoubleArray(2)
            val sinl = DoubleArray(2)
            val sinr = DoubleArray(2)
            val m = DoubleArray(9)
            var utemp: Double
            var vtemp: Double
            var f: Double
            var g: Double
            val MAX_INTERATIONS = 10
            val CONVERGE_TOL = 4.89E-15
            val c_b48 = 1.0
            val c_b71 = -1.0
            var first: Int
            converged = false
            first = 1
            if (abs(e[1]) < CONVERGE_TOL || abs(e[0]) < CONVERGE_TOL) converged = true
            var k = 0
            while (k < MAX_INTERATIONS && !converged) {
                shift = compute_shift(s[1], e[1], s[2])
                f = (abs(s[0]) - shift) * (d_sign(c_b48, s[0]) + shift / s[0])
                g = e[0]
                r = compute_rot(f, g, sinr, cosr, 0, first)
                f = cosr[0] * s[0] + sinr[0] * e[0]
                e[0] = cosr[0] * e[0] - sinr[0] * s[0]
                g = sinr[0] * s[1]
                s[1] = cosr[0] * s[1]
                r = compute_rot(f, g, sinl, cosl, 0, first)
                first = 0
                s[0] = r
                f = cosl[0] * e[0] + sinl[0] * s[1]
                s[1] = cosl[0] * s[1] - sinl[0] * e[0]
                g = sinl[0] * e[1]
                e[1] = cosl[0] * e[1]
                r = compute_rot(f, g, sinr, cosr, 1, first)
                e[0] = r
                f = cosr[1] * s[1] + sinr[1] * e[1]
                e[1] = cosr[1] * e[1] - sinr[1] * s[1]
                g = sinr[1] * s[2]
                s[2] = cosr[1] * s[2]
                r = compute_rot(f, g, sinl, cosl, 1, first)
                s[1] = r
                f = cosl[1] * e[1] + sinl[1] * s[2]
                s[2] = cosl[1] * s[2] - sinl[1] * e[1]
                e[1] = f

                // update u  matrices
                utemp = u[0]
                u[0] = cosl[0] * utemp + sinl[0] * u[3]
                u[3] = -sinl[0] * utemp + cosl[0] * u[3]
                utemp = u[1]
                u[1] = cosl[0] * utemp + sinl[0] * u[4]
                u[4] = -sinl[0] * utemp + cosl[0] * u[4]
                utemp = u[2]
                u[2] = cosl[0] * utemp + sinl[0] * u[5]
                u[5] = -sinl[0] * utemp + cosl[0] * u[5]
                utemp = u[3]
                u[3] = cosl[1] * utemp + sinl[1] * u[6]
                u[6] = -sinl[1] * utemp + cosl[1] * u[6]
                utemp = u[4]
                u[4] = cosl[1] * utemp + sinl[1] * u[7]
                u[7] = -sinl[1] * utemp + cosl[1] * u[7]
                utemp = u[5]
                u[5] = cosl[1] * utemp + sinl[1] * u[8]
                u[8] = -sinl[1] * utemp + cosl[1] * u[8]

                // update v  matrices
                vtemp = v[0]
                v[0] = cosr[0] * vtemp + sinr[0] * v[1]
                v[1] = -sinr[0] * vtemp + cosr[0] * v[1]
                vtemp = v[3]
                v[3] = cosr[0] * vtemp + sinr[0] * v[4]
                v[4] = -sinr[0] * vtemp + cosr[0] * v[4]
                vtemp = v[6]
                v[6] = cosr[0] * vtemp + sinr[0] * v[7]
                v[7] = -sinr[0] * vtemp + cosr[0] * v[7]
                vtemp = v[1]
                v[1] = cosr[1] * vtemp + sinr[1] * v[2]
                v[2] = -sinr[1] * vtemp + cosr[1] * v[2]
                vtemp = v[4]
                v[4] = cosr[1] * vtemp + sinr[1] * v[5]
                v[5] = -sinr[1] * vtemp + cosr[1] * v[5]
                vtemp = v[7]
                v[7] = cosr[1] * vtemp + sinr[1] * v[8]
                v[8] = -sinr[1] * vtemp + cosr[1] * v[8]
                m[0] = s[0]
                m[1] = e[0]
                m[2] = 0.0
                m[3] = 0.0
                m[4] = s[1]
                m[5] = e[1]
                m[6] = 0.0
                m[7] = 0.0
                m[8] = s[2]
                if (abs(e[1]) < CONVERGE_TOL || abs(e[0]) < CONVERGE_TOL) converged = true
                k++
            }
            if (abs(e[1]) < CONVERGE_TOL) {
                compute_2X2(s[0], e[0], s[1], s, sinl, cosl, sinr, cosr, 0)
                utemp = u[0]
                u[0] = cosl[0] * utemp + sinl[0] * u[3]
                u[3] = -sinl[0] * utemp + cosl[0] * u[3]
                utemp = u[1]
                u[1] = cosl[0] * utemp + sinl[0] * u[4]
                u[4] = -sinl[0] * utemp + cosl[0] * u[4]
                utemp = u[2]
                u[2] = cosl[0] * utemp + sinl[0] * u[5]
                u[5] = -sinl[0] * utemp + cosl[0] * u[5]

                // update v  matrices
                vtemp = v[0]
                v[0] = cosr[0] * vtemp + sinr[0] * v[1]
                v[1] = -sinr[0] * vtemp + cosr[0] * v[1]
                vtemp = v[3]
                v[3] = cosr[0] * vtemp + sinr[0] * v[4]
                v[4] = -sinr[0] * vtemp + cosr[0] * v[4]
                vtemp = v[6]
                v[6] = cosr[0] * vtemp + sinr[0] * v[7]
                v[7] = -sinr[0] * vtemp + cosr[0] * v[7]
            } else {
                compute_2X2(s[1], e[1], s[2], s, sinl, cosl, sinr, cosr, 1)
                utemp = u[3]
                u[3] = cosl[0] * utemp + sinl[0] * u[6]
                u[6] = -sinl[0] * utemp + cosl[0] * u[6]
                utemp = u[4]
                u[4] = cosl[0] * utemp + sinl[0] * u[7]
                u[7] = -sinl[0] * utemp + cosl[0] * u[7]
                utemp = u[5]
                u[5] = cosl[0] * utemp + sinl[0] * u[8]
                u[8] = -sinl[0] * utemp + cosl[0] * u[8]

                // update v  matrices
                vtemp = v[1]
                v[1] = cosr[0] * vtemp + sinr[0] * v[2]
                v[2] = -sinr[0] * vtemp + cosr[0] * v[2]
                vtemp = v[4]
                v[4] = cosr[0] * vtemp + sinr[0] * v[5]
                v[5] = -sinr[0] * vtemp + cosr[0] * v[5]
                vtemp = v[7]
                v[7] = cosr[0] * vtemp + sinr[0] * v[8]
                v[8] = -sinr[0] * vtemp + cosr[0] * v[8]
            }
            return 0
        }

        fun max(a: Double, b: Double): Double {
            return if (a > b) a else b
        }

        fun min(a: Double, b: Double): Double {
            return if (a < b) a else b
        }

        fun d_sign(a: Double, b: Double): Double {
            val x: Double = if (a >= 0) a else -a
            return if (b >= 0) x else -x
        }

        fun compute_shift(f: Double, g: Double, h: Double): Double {
            val d__1: Double
            val d__2: Double
            val fhmn: Double
            val fhmx: Double
            val c: Double
            val `as`: Double
            val at: Double
            val au: Double
            var ssmin: Double
            val fa: Double = abs(f)
            val ga: Double = abs(g)
            val ha: Double = abs(h)
            fhmn = min(fa, ha)
            fhmx = max(fa, ha)
            if (fhmn == 0.0) {
                ssmin = 0.0
                if (fhmx == 0.0) {
                } else {
                    d__1 = min(fhmx, ga) / max(fhmx, ga)
                }
            } else {
                if (ga < fhmx) {
                    `as` = fhmn / fhmx + 1.0
                    at = (fhmx - fhmn) / fhmx
                    d__1 = ga / fhmx
                    au = d__1 * d__1
                    c = 2.0 / (sqrt(`as` * `as` + au) + sqrt(at * at + au))
                    ssmin = fhmn * c
                } else {
                    au = fhmx / ga
                    if (au == 0.0) {
                        ssmin = fhmn * fhmx / ga
                    } else {
                        `as` = fhmn / fhmx + 1.0
                        at = (fhmx - fhmn) / fhmx
                        d__1 = `as` * au
                        d__2 = at * au
                        c = 1.0 / (sqrt(d__1 * d__1 + 1.0) + sqrt(d__2 * d__2 + 1.0))
                        ssmin = fhmn * c * au
                        ssmin += ssmin
                    }
                }
            }
            return ssmin
        }

        fun compute_2X2(
            f: Double,
            g: Double,
            h: Double,
            single_values: DoubleArray,
            snl: DoubleArray,
            csl: DoubleArray,
            snr: DoubleArray,
            csr: DoubleArray,
            index: Int
        ): Int {
            val c_b3 = 2.0
            val c_b4 = 1.0
            val d__1: Double
            var pmax: Int
            var temp: Double
            val swap: Boolean
            var a: Double
            var d: Double
            var l: Double
            var m: Double
            var r: Double
            var s: Double
            var t: Double
            var tsign: Double
            var fa: Double
            val ga: Double
            var ha: Double
            var ft: Double
            var ht: Double
            var mm: Double
            var gasmal: Boolean
            var tt: Double
            var clt: Double
            var crt: Double
            var slt: Double
            var srt: Double
            var ssmin: Double
            var ssmax: Double
            ssmax = single_values[0]
            ssmin = single_values[1]
            clt = 0.0
            crt = 0.0
            slt = 0.0
            srt = 0.0
            tsign = 0.0
            ft = f
            fa = abs(ft)
            ht = h
            ha = abs(h)
            pmax = 1
            swap = ha > fa
            if (swap) {
                pmax = 3
                temp = ft
                ft = ht
                ht = temp
                temp = fa
                fa = ha
                ha = temp
            }
            val gt: Double = g
            ga = abs(gt)
            if (ga == 0.0) {
                single_values[1] = ha
                single_values[0] = fa
                clt = 1.0
                crt = 1.0
                slt = 0.0
                srt = 0.0
            } else {
                gasmal = true
                if (ga > fa) {
                    pmax = 2
                    if (fa / ga < EPS) {
                        gasmal = false
                        ssmax = ga
                        ssmin = if (ha > 1.0) {
                            fa / (ga / ha)
                        } else {
                            fa / ga * ha
                        }
                        clt = 1.0
                        slt = ht / gt
                        srt = 1.0
                        crt = ft / gt
                    }
                }
                if (gasmal) {
                    d = fa - ha
                    l = if (d == fa) {
                        1.0
                    } else {
                        d / fa
                    }
                    m = gt / ft
                    t = 2.0 - l
                    mm = m * m
                    tt = t * t
                    s = sqrt(tt + mm)
                    r = if (l == 0.0) {
                        abs(m)
                    } else {
                        sqrt(l * l + mm)
                    }
                    a = (s + r) * .5
                    if (ga > fa) {
                        pmax = 2
                        if (fa / ga < EPS) {
                            gasmal = false
                            ssmax = ga
                            ssmin = if (ha > 1.0) {
                                fa / (ga / ha)
                            } else {
                                fa / ga * ha
                            }
                            clt = 1.0
                            slt = ht / gt
                            srt = 1.0
                            crt = ft / gt
                        }
                    }
                    if (gasmal) {
                        d = fa - ha
                        l = if (d == fa) {
                            1.0
                        } else {
                            d / fa
                        }
                        m = gt / ft
                        t = 2.0 - l
                        mm = m * m
                        tt = t * t
                        s = sqrt(tt + mm)
                        r = if (l == 0.0) {
                            abs(m)
                        } else {
                            sqrt(l * l + mm)
                        }
                        a = (s + r) * .5
                        ssmin = ha / a
                        ssmax = fa * a
                        t = if (mm == 0.0) {
                            if (l == 0.0) {
                                d_sign(c_b3, ft) * d_sign(c_b4, gt)
                            } else {
                                gt / d_sign(d, ft) + m / t
                            }
                        } else {
                            (m / (s + t) + m / (r + l)) * (a + 1.0)
                        }
                        l = sqrt(t * t + 4.0)
                        crt = 2.0 / l
                        srt = t / l
                        clt = (crt + srt * m) / a
                        slt = ht / ft * srt / a
                    }
                }
                if (swap) {
                    csl[0] = srt
                    snl[0] = crt
                    csr[0] = slt
                    snr[0] = clt
                } else {
                    csl[0] = clt
                    snl[0] = slt
                    csr[0] = crt
                    snr[0] = srt
                }
                if (pmax == 1) {
                    tsign = d_sign(c_b4, csr[0]) * d_sign(c_b4, csl[0]) * d_sign(c_b4, f)
                }
                if (pmax == 2) {
                    tsign = d_sign(c_b4, snr[0]) * d_sign(c_b4, csl[0]) * d_sign(c_b4, g)
                }
                if (pmax == 3) {
                    tsign = d_sign(c_b4, snr[0]) * d_sign(c_b4, snl[0]) * d_sign(c_b4, h)
                }
                single_values[index] = d_sign(ssmax, tsign)
                d__1 = tsign * d_sign(c_b4, f) * d_sign(c_b4, h)
                single_values[index + 1] = d_sign(ssmin, d__1)
            }
            return 0
        }

        fun compute_rot(f: Double, g: Double, sin: DoubleArray, cos: DoubleArray, index: Int, first: Int): Double {
            val i__1: Int
            var d__1: Double
            var d__2: Double
            var cs: Double
            var sn: Double
            var i: Int
            var scale: Double
            var count: Int
            var f1: Double
            var g1: Double
            var r: Double
            val safmn2 = 2.002083095183101E-146
            val safmx2 = 4.9947976805055876E145
            if (g == 0.0) {
                cs = 1.0
                sn = 0.0
                r = f
            } else if (f == 0.0) {
                cs = 0.0
                sn = 1.0
                r = g
            } else {
                f1 = f
                g1 = g
                scale = max(abs(f1), abs(g1))
                if (scale >= safmx2) {
                    count = 0
                    while (scale >= safmx2) {
                        ++count
                        f1 *= safmn2
                        g1 *= safmn2
                        scale = max(abs(f1), abs(g1))
                    }
                    r = sqrt(f1 * f1 + g1 * g1)
                    cs = f1 / r
                    sn = g1 / r
                    i__1 = count
                    i = 1
                    while (i <= count) {
                        r *= safmx2
                        ++i
                    }
                } else if (scale <= safmn2) {
                    count = 0
                    while (scale <= safmn2) {
                        ++count
                        f1 *= safmx2
                        g1 *= safmx2
                        scale = max(abs(f1), abs(g1))
                    }
                    r = sqrt(f1 * f1 + g1 * g1)
                    cs = f1 / r
                    sn = g1 / r
                    i__1 = count
                    i = 1
                    while (i <= count) {
                        r *= safmn2
                        ++i
                    }
                } else {
                    r = sqrt(f1 * f1 + g1 * g1)
                    cs = f1 / r
                    sn = g1 / r
                }
                if (abs(f) > abs(g) && cs < 0.0) {
                    cs = -cs
                    sn = -sn
                    r = -r
                }
            }
            sin[index] = sn
            cos[index] = cs
            return r
        }

        fun mat_mul(m1: DoubleArray, m2: DoubleArray, m3: DoubleArray) {
            val tmp = DoubleArray(9)
            tmp[0] = m1[0] * m2[0] + m1[1] * m2[3] + m1[2] * m2[6]
            tmp[1] = m1[0] * m2[1] + m1[1] * m2[4] + m1[2] * m2[7]
            tmp[2] = m1[0] * m2[2] + m1[1] * m2[5] + m1[2] * m2[8]
            tmp[3] = m1[3] * m2[0] + m1[4] * m2[3] + m1[5] * m2[6]
            tmp[4] = m1[3] * m2[1] + m1[4] * m2[4] + m1[5] * m2[7]
            tmp[5] = m1[3] * m2[2] + m1[4] * m2[5] + m1[5] * m2[8]
            tmp[6] = m1[6] * m2[0] + m1[7] * m2[3] + m1[8] * m2[6]
            tmp[7] = m1[6] * m2[1] + m1[7] * m2[4] + m1[8] * m2[7]
            tmp[8] = m1[6] * m2[2] + m1[7] * m2[5] + m1[8] * m2[8]
            var i = 0
            while (i < 9) {
                m3[i] = tmp[i]
                i++
            }
        }

        fun transpose_mat(`in`: DoubleArray, out: DoubleArray) {
            out[0] = `in`[0]
            out[1] = `in`[3]
            out[2] = `in`[6]
            out[3] = `in`[1]
            out[4] = `in`[4]
            out[5] = `in`[7]
            out[6] = `in`[2]
            out[7] = `in`[5]
            out[8] = `in`[8]
        }

        fun max3(values: DoubleArray): Double {
            return if (values[0] > values[1]) {
                if (values[0] > values[2]) values[0] else values[2]
            } else {
                if (values[1] > values[2]) values[1] else values[2]
            }
        }

        private fun almostEqual(a: Double, b: Double): Boolean {
            if (a == b) return true
            val EPSILON_ABSOLUTE = 1.0e-6
            val EPSILON_RELATIVE = 1.0e-4
            val diff = abs(a - b)
            val absA = abs(a)
            val absB = abs(b)
            val max = if (absA >= absB) absA else absB
            if (diff < EPSILON_ABSOLUTE) return true
            return diff / max < EPSILON_RELATIVE
        }
    }

    /**
     * Returns the representation of the specified floating-point
     * value according to the IEEE 754 floating-point "double format"
     * bit layout, after first mapping -0.0 to 0.0. This method is
     * identical to Double.doubleToLongBits(double) except that an
     * integer value of 0L is returned for a floating-point value of
     * -0.0. This is done for the purpose of computing a hash code
     * that satisfies the contract of hashCode() and equals(). The
     * equals() method in each vecmath class does a pair-wise "=="
     * test on each floating-point field in the class (e.g., x, y, and
     * z for a Tuple3d). Since 0.0&nbsp;==&nbsp;-0.0 returns true, we
     * must also return the same hash code for two objects, one of
     * which has a field with a value of -0.0 and the other of which
     * has a cooresponding field with a value of 0.0.
     *
     * @param d an input double precision floating-point number
     * @return the integer bits representing that floating-point
     * number, after first mapping -0.0f to 0.0f
     */
    fun doubleToLongBits(d: Double): Long {
        // Check for +0 or -0
        return if (d == 0.0) {
            0L
        } else {
            java.lang.Double.doubleToLongBits(d)
        }
    }
}
