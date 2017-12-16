package com.sdsmdg.harjot.vectormaster.models

import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.RectF

import com.sdsmdg.harjot.vectormaster.DefaultValues
import com.sdsmdg.harjot.vectormaster.utilities.Utils
import com.sdsmdg.harjot.vectormaster.utilities.parser.PathParser

class PathModel {

    var name: String? = null

    private var fillAlpha: Float = 0.toFloat()
    private var fillColor: Int = 0

    private var fillType: Path.FillType? = null

    var pathData: String? = null

    private var trimPathStart: Float = 0.toFloat()
    private var trimPathEnd: Float = 0.toFloat()
    private var trimPathOffset: Float = 0.toFloat()

    private var strokeAlpha: Float = 0.toFloat()
    private var strokeColor: Int = 0
    private var strokeLineCap: Paint.Cap? = null
    private var strokeLineJoin: Paint.Join? = null
    private var strokeMiterLimit: Float = 0.toFloat()
    private var strokeWidth: Float = 0.toFloat()

    internal var strokeRatio: Float = 0.toFloat()

    var isFillAndStroke = false
        private set

    // Support for trim-paths is not available

    private var originalPath: Path? = null
    var path: Path? = null
    var trimmedPath: Path? = null
    var pathPaint: Paint? = null

    private var scaleMatrix: Matrix? = null

    init {
        fillAlpha = DefaultValues.PATH_FILL_ALPHA
        fillColor = DefaultValues.PATH_FILL_COLOR
        fillType = DefaultValues.PATH_FILL_TYPE
        trimPathStart = DefaultValues.PATH_TRIM_PATH_START
        trimPathEnd = DefaultValues.PATH_TRIM_PATH_END
        trimPathOffset = DefaultValues.PATH_TRIM_PATH_OFFSET
        strokeAlpha = DefaultValues.PATH_STROKE_ALPHA
        strokeColor = DefaultValues.PATH_STROKE_COLOR
        strokeLineCap = DefaultValues.PATH_STROKE_LINE_CAP
        strokeLineJoin = DefaultValues.PATH_STROKE_LINE_JOIN
        strokeMiterLimit = DefaultValues.PATH_STROKE_MITER_LIMIT
        strokeWidth = DefaultValues.PATH_STROKE_WIDTH
        strokeRatio = DefaultValues.PATH_STROKE_RATIO

        pathPaint = Paint()
        pathPaint!!.isAntiAlias = true
        updatePaint()
    }

    fun buildPath(useLegacyParser: Boolean) {
        if (useLegacyParser) {
            originalPath = com.sdsmdg.harjot.vectormaster.utilities.legacyparser.PathParser.createPathFromPathData(pathData)
        } else {
            originalPath = PathParser.doPath(pathData)
        }
        if (originalPath != null)
            originalPath!!.fillType = fillType

        path = Path(originalPath)
    }

    fun updatePaint() {
        pathPaint!!.strokeWidth = strokeWidth * strokeRatio

        if (fillColor != Color.TRANSPARENT && strokeColor != Color.TRANSPARENT) {
            isFillAndStroke = true
        } else if (fillColor != Color.TRANSPARENT) {
            pathPaint!!.color = fillColor
            pathPaint!!.alpha = Utils.getAlphaFromFloat(fillAlpha)
            pathPaint!!.style = Paint.Style.FILL
            isFillAndStroke = false
        } else if (strokeColor != Color.TRANSPARENT) {
            pathPaint!!.color = strokeColor
            pathPaint!!.alpha = Utils.getAlphaFromFloat(strokeAlpha)
            pathPaint!!.style = Paint.Style.STROKE
            isFillAndStroke = false
        } else {
            pathPaint!!.color = Color.TRANSPARENT
        }

        pathPaint!!.strokeCap = strokeLineCap
        pathPaint!!.strokeJoin = strokeLineJoin
        pathPaint!!.strokeMiter = strokeMiterLimit
    }

    fun makeStrokePaint() {
        pathPaint!!.color = strokeColor
        pathPaint!!.alpha = Utils.getAlphaFromFloat(strokeAlpha)
        pathPaint!!.style = Paint.Style.STROKE
    }

    fun makeFillPaint() {
        pathPaint!!.color = fillColor
        pathPaint!!.alpha = Utils.getAlphaFromFloat(fillAlpha)
        pathPaint!!.style = Paint.Style.FILL
    }

    fun transform(matrix: Matrix) {
        scaleMatrix = matrix

        trimPath()
    }

    fun trimPath() {
        if (scaleMatrix != null) {
            if (trimPathStart == 0f && trimPathEnd == 1f && trimPathOffset == 0f) {
                path = Path(originalPath)
                path!!.transform(scaleMatrix)
            } else {
                val pathMeasure = PathMeasure(originalPath, false)
                val length = pathMeasure.length
                trimmedPath = Path()
                pathMeasure.getSegment((trimPathStart + trimPathOffset) * length, (trimPathEnd + trimPathOffset) * length, trimmedPath, true)
                path = Path(trimmedPath)
                path!!.transform(scaleMatrix)
            }
        }
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

    fun getFillAlpha(): Float {
        return fillAlpha
    }

    fun setFillAlpha(fillAlpha: Float) {
        this.fillAlpha = fillAlpha
        updatePaint()
    }

    fun getFillColor(): Int {
        return fillColor
    }

    fun setFillColor(fillColor: Int) {
        this.fillColor = fillColor
        updatePaint()
    }

    fun getFillType(): Path.FillType? {
        return fillType
    }

    fun setFillType(fillType: Path.FillType) {
        this.fillType = fillType
        if (originalPath != null)
            originalPath!!.fillType = fillType
    }

    fun getTrimPathStart(): Float {
        return trimPathStart
    }

    fun setTrimPathStart(trimPathStart: Float) {
        this.trimPathStart = trimPathStart
        trimPath()
    }

    fun getTrimPathEnd(): Float {
        return trimPathEnd
    }

    fun setTrimPathEnd(trimPathEnd: Float) {
        this.trimPathEnd = trimPathEnd
        trimPath()
    }

    fun getTrimPathOffset(): Float {
        return trimPathOffset
    }

    fun setTrimPathOffset(trimPathOffset: Float) {
        this.trimPathOffset = trimPathOffset
        trimPath()
    }

    fun getStrokeAlpha(): Float {
        return strokeAlpha
    }

    fun setStrokeAlpha(strokeAlpha: Float) {
        this.strokeAlpha = strokeAlpha
        updatePaint()
    }

    fun getStrokeColor(): Int {
        return strokeColor
    }

    fun setStrokeColor(strokeColor: Int) {
        this.strokeColor = strokeColor
        updatePaint()
    }

    fun getStrokeLineCap(): Paint.Cap? {
        return strokeLineCap
    }

    fun setStrokeLineCap(strokeLineCap: Paint.Cap) {
        this.strokeLineCap = strokeLineCap
        updatePaint()
    }

    fun getStrokeLineJoin(): Paint.Join? {
        return strokeLineJoin
    }

    fun setStrokeLineJoin(strokeLineJoin: Paint.Join) {
        this.strokeLineJoin = strokeLineJoin
        updatePaint()
    }

    fun getStrokeMiterLimit(): Float {
        return strokeMiterLimit
    }

    fun setStrokeMiterLimit(strokeMiterLimit: Float) {
        this.strokeMiterLimit = strokeMiterLimit
        updatePaint()
    }

    fun getStrokeWidth(): Float {
        return strokeWidth
    }

    fun setStrokeWidth(strokeWidth: Float) {
        this.strokeWidth = strokeWidth
        updatePaint()
    }

    fun setStrokeRatio(strokeRatio: Float) {
        this.strokeRatio = strokeRatio
        updatePaint()
    }
}
