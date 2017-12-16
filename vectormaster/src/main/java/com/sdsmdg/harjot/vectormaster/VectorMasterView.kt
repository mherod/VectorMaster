package com.sdsmdg.harjot.vectormaster

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.View

import com.sdsmdg.harjot.vectormaster.models.ClipPathModel
import com.sdsmdg.harjot.vectormaster.models.GroupModel
import com.sdsmdg.harjot.vectormaster.models.PathModel
import com.sdsmdg.harjot.vectormaster.models.VectorModel
import com.sdsmdg.harjot.vectormaster.utilities.Utils

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException

import java.io.IOException
import java.lang.Float.*
import java.util.Stack

interface IVectorMasterView {
    fun getGroupModelByName(name: String): GroupModel?
    fun findPathModelByName(name: String): PathModel?
    fun findClipPathModelByName(name: String): ClipPathModel?
}

class VectorMasterView : View, IVectorMasterView {

    private var vectorModel: VectorModel? = null

    private var resID = -1
    private var useLegacyParser = true

    private lateinit var xpp: XmlPullParser

    private var scaleMatrix: Matrix? = null

    internal var width = 0
    internal var height = 0

    private var scaleRatio: Float = 0.toFloat()
    private var strokeRatio: Float = 0.toFloat()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet) {

        val a = context.obtainStyledAttributes(attrs, R.styleable.VectorMasterView)
        val N = a.indexCount
        (0 until N)
                .map { a.getIndex(it) }
                .forEach { index ->
                    when (index) {
                        R.styleable.VectorMasterView_vector_src -> {
                            resID = a.getResourceId(index, -1)
                        }
                        R.styleable.VectorMasterView_use_legacy_parser -> {
                            useLegacyParser = a.getBoolean(index, false)
                        }
                    }
                }
        a.recycle()

        buildVectorModel()

    }

    private fun buildVectorModel() {

        if (resID == -1) {
            vectorModel = null
            return
        }

        xpp = context.resources.getXml(resID)

        var tempPosition: Int
        var pathModel = PathModel()
        vectorModel = VectorModel()
        var groupModel = GroupModel()
        var clipPathModel = ClipPathModel()
        val groupModelStack = Stack<GroupModel>()

        try {
            var event = xpp.eventType
            while (event != XmlPullParser.END_DOCUMENT) {
                val name = xpp.name
                when (event) {
                    XmlPullParser.START_TAG -> when (name) {
                        "vector" -> {
                            tempPosition = getAttrPosition(xpp, "viewportWidth")
                            vectorModel!!.viewportWidth = if (tempPosition != -1) parseFloat(xpp.getAttributeValue(tempPosition)) else DefaultValues.VECTOR_VIEWPORT_WIDTH

                            tempPosition = getAttrPosition(xpp, "viewportHeight")
                            vectorModel!!.viewportHeight = if (tempPosition != -1) parseFloat(xpp.getAttributeValue(tempPosition)) else DefaultValues.VECTOR_VIEWPORT_HEIGHT

                            tempPosition = getAttrPosition(xpp, "alpha")
                            vectorModel!!.alpha = if (tempPosition != -1) parseFloat(xpp.getAttributeValue(tempPosition)) else DefaultValues.VECTOR_ALPHA

                            tempPosition = getAttrPosition(xpp, "name")
                            vectorModel!!.name = if (tempPosition != -1) xpp.getAttributeValue(tempPosition) else null

                            tempPosition = getAttrPosition(xpp, "width")
                            vectorModel!!.width = if (tempPosition != -1) Utils.getFloatFromDimensionString(xpp.getAttributeValue(tempPosition)) else DefaultValues.VECTOR_WIDTH

                            tempPosition = getAttrPosition(xpp, "height")
                            vectorModel!!.height = if (tempPosition != -1) Utils.getFloatFromDimensionString(xpp.getAttributeValue(tempPosition)) else DefaultValues.VECTOR_HEIGHT
                        }
                        "path" -> {
                            pathModel = PathModel()

                            tempPosition = getAttrPosition(xpp, "name")
                            pathModel.name = if (tempPosition != -1) xpp.getAttributeValue(tempPosition) else null

                            tempPosition = getAttrPosition(xpp, "fillAlpha")
                            pathModel.setFillAlpha(if (tempPosition != -1) parseFloat(xpp.getAttributeValue(tempPosition)) else DefaultValues.PATH_FILL_ALPHA)

                            tempPosition = getAttrPosition(xpp, "fillColor")
                            pathModel.setFillColor(if (tempPosition != -1) Utils.getColorFromString(xpp.getAttributeValue(tempPosition)) else DefaultValues.PATH_FILL_COLOR)

                            tempPosition = getAttrPosition(xpp, "fillType")
                            pathModel.setFillType(if (tempPosition != -1) Utils.getFillTypeFromString(xpp.getAttributeValue(tempPosition)) else DefaultValues.PATH_FILL_TYPE)

                            tempPosition = getAttrPosition(xpp, "pathData")
                            pathModel.pathData = if (tempPosition != -1) xpp.getAttributeValue(tempPosition) else null

                            tempPosition = getAttrPosition(xpp, "strokeAlpha")
                            pathModel.setStrokeAlpha(if (tempPosition != -1) parseFloat(xpp.getAttributeValue(tempPosition)) else DefaultValues.PATH_STROKE_ALPHA)

                            tempPosition = getAttrPosition(xpp, "strokeColor")
                            pathModel.setStrokeColor(if (tempPosition != -1) Utils.getColorFromString(xpp.getAttributeValue(tempPosition)) else DefaultValues.PATH_STROKE_COLOR)

                            tempPosition = getAttrPosition(xpp, "strokeLineCap")
                            pathModel.setStrokeLineCap(if (tempPosition != -1) Utils.getLineCapFromString(xpp.getAttributeValue(tempPosition)) else DefaultValues.PATH_STROKE_LINE_CAP)

                            tempPosition = getAttrPosition(xpp, "strokeLineJoin")
                            pathModel.setStrokeLineJoin(if (tempPosition != -1) Utils.getLineJoinFromString(xpp.getAttributeValue(tempPosition)) else DefaultValues.PATH_STROKE_LINE_JOIN)

                            tempPosition = getAttrPosition(xpp, "strokeMiterLimit")
                            pathModel.setStrokeMiterLimit(if (tempPosition != -1) parseFloat(xpp.getAttributeValue(tempPosition)) else DefaultValues.PATH_STROKE_MITER_LIMIT)

                            tempPosition = getAttrPosition(xpp, "strokeWidth")
                            pathModel.setStrokeWidth(if (tempPosition != -1) parseFloat(xpp.getAttributeValue(tempPosition)) else DefaultValues.PATH_STROKE_WIDTH)

                            tempPosition = getAttrPosition(xpp, "trimPathEnd")
                            pathModel.setTrimPathEnd(if (tempPosition != -1) parseFloat(xpp.getAttributeValue(tempPosition)) else DefaultValues.PATH_TRIM_PATH_END)

                            tempPosition = getAttrPosition(xpp, "trimPathOffset")
                            pathModel.setTrimPathOffset(if (tempPosition != -1) parseFloat(xpp.getAttributeValue(tempPosition)) else DefaultValues.PATH_TRIM_PATH_OFFSET)

                            tempPosition = getAttrPosition(xpp, "trimPathStart")
                            pathModel.setTrimPathStart(if (tempPosition != -1) parseFloat(xpp.getAttributeValue(tempPosition)) else DefaultValues.PATH_TRIM_PATH_START)

                            pathModel.buildPath(useLegacyParser)
                        }
                        "group" -> {
                            groupModel = GroupModel()

                            tempPosition = getAttrPosition(xpp, "name")
                            groupModel.name = if (tempPosition != -1) xpp.getAttributeValue(tempPosition) else null

                            tempPosition = getAttrPosition(xpp, "pivotX")
                            groupModel.pivotX = if (tempPosition != -1) parseFloat(xpp.getAttributeValue(tempPosition)) else DefaultValues.GROUP_PIVOT_X

                            tempPosition = getAttrPosition(xpp, "pivotY")
                            groupModel.pivotY = if (tempPosition != -1) parseFloat(xpp.getAttributeValue(tempPosition)) else DefaultValues.GROUP_PIVOT_Y

                            tempPosition = getAttrPosition(xpp, "rotation")
                            groupModel.setRotation(if (tempPosition != -1) parseFloat(xpp.getAttributeValue(tempPosition)) else DefaultValues.GROUP_ROTATION)

                            tempPosition = getAttrPosition(xpp, "scaleX")
                            groupModel.setScaleX(if (tempPosition != -1) parseFloat(xpp.getAttributeValue(tempPosition)) else DefaultValues.GROUP_SCALE_X)

                            tempPosition = getAttrPosition(xpp, "scaleY")
                            groupModel.setScaleY(if (tempPosition != -1) parseFloat(xpp.getAttributeValue(tempPosition)) else DefaultValues.GROUP_SCALE_Y)

                            tempPosition = getAttrPosition(xpp, "translateX")
                            groupModel.setTranslateX(if (tempPosition != -1) parseFloat(xpp.getAttributeValue(tempPosition)) else DefaultValues.GROUP_TRANSLATE_X)

                            tempPosition = getAttrPosition(xpp, "translateY")
                            groupModel.setTranslateY(if (tempPosition != -1) parseFloat(xpp.getAttributeValue(tempPosition)) else DefaultValues.GROUP_TRANSLATE_Y)

                            groupModelStack.push(groupModel)
                        }
                        "clip-path" -> {
                            clipPathModel = ClipPathModel()

                            tempPosition = getAttrPosition(xpp, "name")
                            clipPathModel.name = if (tempPosition != -1) xpp.getAttributeValue(tempPosition) else null

                            tempPosition = getAttrPosition(xpp, "pathData")
                            clipPathModel.pathData = if (tempPosition != -1) xpp.getAttributeValue(tempPosition) else null

                            clipPathModel.buildPath(useLegacyParser)
                        }
                    }

                    XmlPullParser.END_TAG -> when (name) {
                        "path" -> {
                            if (groupModelStack.size == 0) {
                                vectorModel!!.addPathModel(pathModel)
                            } else {
                                groupModelStack.peek().addPathModel(pathModel)
                            }
                            vectorModel?.fullpath.addPath(pathModel.path)
                        }
                        "clip-path" -> if (groupModelStack.size == 0) {
                            vectorModel?.addClipPathModel(clipPathModel)
                        } else {
                            groupModelStack.peek().addClipPathModel(clipPathModel)
                        }
                        "group" -> {
                            val topGroupModel = groupModelStack.pop()
                            when {
                                groupModelStack.size == 0 -> {
                                    topGroupModel.parent = null
                                    vectorModel?.addGroupModel(topGroupModel)
                                }
                                else -> {
                                    topGroupModel.parent = groupModelStack.peek()
                                    groupModelStack.peek().addGroupModel(topGroupModel)
                                }
                            }
                        }
                        "vector" -> vectorModel!!.buildTransformMatrices()
                    }
                }
                event = xpp.next()
            }
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun getAttrPosition(xpp: XmlPullParser, attrName: String): Int =
            (0 until xpp.attributeCount).firstOrNull { xpp.getAttributeName(it) == attrName }
                    ?: -1

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w != 0 && h != 0) {
            width = w
            height = h

            buildScaleMatrix()
            scaleAllPaths()
            scaleAllStrokes()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        width = canvas.width
        height = canvas.height

        vectorModel?.let {
            alpha = it.alpha
            it.drawPaths(canvas)
        }
    }

    private fun buildScaleMatrix() {

        scaleMatrix = Matrix()

        scaleMatrix?.postTranslate(
                width / 2 - vectorModel!!.viewportWidth / 2,
                height / 2 - vectorModel!!.viewportHeight / 2
        )

        val widthRatio = width / vectorModel!!.viewportWidth
        val heightRatio = height / vectorModel!!.viewportHeight

        val ratio = widthRatio.coerceAtMost(heightRatio)

        scaleRatio = ratio

        scaleMatrix?.postScale(ratio, ratio, (width / 2).toFloat(), (height / 2).toFloat())
    }

    private fun scaleAllPaths() {
        scaleMatrix?.let { vectorModel?.scaleAllPaths(it) }
    }

    private fun scaleAllStrokes() {
        val width1 = vectorModel!!.width
        val height1 = vectorModel!!.height
        strokeRatio = width / width1.coerceAtMost(height / height1)
        vectorModel?.scaleAllStrokeWidth(strokeRatio)
    }

    override fun getGroupModelByName(name: String): GroupModel? {
        var gModel: GroupModel?
        for (groupModel in vectorModel!!.groupModels) {
            if (groupModel.name == name) {
                return groupModel
            } else {
                gModel = groupModel.getGroupModelByName(name)
                if (gModel != null)
                    return gModel
            }
        }
        return null
    }

    override fun findPathModelByName(name: String) = vectorModel?.run {

        pathModels.firstOrNull { it.name == name } ?:
                groupModels
                        .mapNotNull { it.getPathModelByName(name) }
                        .takeWhile { it.name == name }
                        .firstOrNull()
    }

    override fun findClipPathModelByName(name: String) = vectorModel?.run {

        clipPathModels.firstOrNull { it.name == name } ?:
                groupModels
                        .mapNotNull { it.getClipPathModelByName(name) }
                        .takeWhile { it.name == name }
                        .firstOrNull()
    }

    fun update() {
        invalidate()
    }
}