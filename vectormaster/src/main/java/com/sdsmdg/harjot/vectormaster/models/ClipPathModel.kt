package com.sdsmdg.harjot.vectormaster.models


import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import com.sdsmdg.harjot.vectormaster.utilities.legacyparser.PathParser.createPathFromPathData

import com.sdsmdg.harjot.vectormaster.utilities.parser.PathParser

class ClipPathModel {
    var name: String? = null
    var pathData: String? = null

    private var originalPath: Path? = null
    var path: Path? = null

    val clipPaint = Paint().apply {
        isAntiAlias = true
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    init {
        path = Path()
    }

    fun buildPath(useLegacyParser: Boolean) {
        if (useLegacyParser) {
            originalPath = createPathFromPathData(pathData)
        } else {
            originalPath = PathParser.doPath(pathData)
        }

        path = Path(originalPath)
    }

    fun transform(matrix: Matrix) {
        path = Path(originalPath)

        path!!.transform(matrix)
    }

    fun getScaledAndOffsetPath(offsetX: Float, offsetY: Float, scaleX: Float, scaleY: Float): Path {
        val newPath = Path(path)
        newPath.offset(offsetX, offsetY)
        newPath.transform(getScaleMatrix(newPath, scaleX, scaleY))
        return newPath
    }

    fun getScaleMatrix(srcPath: Path, scaleX: Float, scaleY: Float): Matrix {
        val scaleMatrix = Matrix()
        val rectF = RectF()
        srcPath.computeBounds(rectF, true)
        scaleMatrix.setScale(scaleX, scaleY, rectF.left, rectF.top)
        return scaleMatrix
    }
}
