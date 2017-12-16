package com.sdsmdg.harjot.vectormaster


import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path

object DefaultValues {

    var PATH_ATTRIBUTES = arrayOf(
            "name",
            "fillAlpha",
            "fillColor",
            "fillType",
            "pathData",
            "strokeAlpha",
            "strokeColor",
            "strokeLineCap",
            "strokeLineJoin",
            "strokeMiterLimit",
            "strokeWidth"
    )

    val PATH_FILL_COLOR = Color.TRANSPARENT
    val PATH_STROKE_COLOR = Color.TRANSPARENT
    val PATH_STROKE_WIDTH = 0.0f
    val PATH_STROKE_ALPHA = 1.0f
    val PATH_FILL_ALPHA = 1.0f
    val PATH_STROKE_LINE_CAP: Paint.Cap = Paint.Cap.BUTT
    val PATH_STROKE_LINE_JOIN: Paint.Join = Paint.Join.MITER
    val PATH_STROKE_MITER_LIMIT = 4.0f
    val PATH_STROKE_RATIO = 1.0f
    // WINDING fill type is equivalent to NON_ZERO
    val PATH_FILL_TYPE: Path.FillType = Path.FillType.WINDING
    val PATH_TRIM_PATH_START = 0.0f
    val PATH_TRIM_PATH_END = 1.0f
    val PATH_TRIM_PATH_OFFSET = 0.0f

    val VECTOR_VIEWPORT_WIDTH = 0.0f
    val VECTOR_VIEWPORT_HEIGHT = 0.0f
    val VECTOR_WIDTH = 0.0f
    val VECTOR_HEIGHT = 0.0f
    val VECTOR_ALPHA = 1.0f

    val GROUP_ROTATION = 0.0f
    val GROUP_PIVOT_X = 0.0f
    val GROUP_PIVOT_Y = 0.0f
    val GROUP_SCALE_X = 1.0f
    val GROUP_SCALE_Y = 1.0f
    val GROUP_TRANSLATE_X = 0.0f
    val GROUP_TRANSLATE_Y = 0.0f

}
