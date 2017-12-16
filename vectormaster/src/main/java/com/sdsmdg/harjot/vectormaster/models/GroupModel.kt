package com.sdsmdg.harjot.vectormaster.models


import android.graphics.Canvas
import android.graphics.Matrix

import com.sdsmdg.harjot.vectormaster.DefaultValues

import java.util.ArrayList

class GroupModel {

    var name: String? = null

    private var rotation: Float = 0.toFloat()
    var pivotX: Float = 0.toFloat()
    var pivotY: Float = 0.toFloat()
    private var scaleX: Float = 0.toFloat()
    private var scaleY: Float = 0.toFloat()
    private var translateX: Float = 0.toFloat()
    private var translateY: Float = 0.toFloat()

    private var scaleMatrix: Matrix? = null

    private var originalTransformMatrix: Matrix? = null

    private var finalTransformMatrix: Matrix? = null

    var parent: GroupModel? = null

    private val groupModels: ArrayList<GroupModel>
    private val pathModels: ArrayList<PathModel>
    private val clipPathModels: ArrayList<ClipPathModel>

    init {
        rotation = DefaultValues.GROUP_ROTATION
        pivotX = DefaultValues.GROUP_PIVOT_X
        pivotY = DefaultValues.GROUP_PIVOT_Y
        scaleX = DefaultValues.GROUP_SCALE_X
        scaleY = DefaultValues.GROUP_SCALE_Y
        translateX = DefaultValues.GROUP_TRANSLATE_X
        translateY = DefaultValues.GROUP_TRANSLATE_Y

        groupModels = ArrayList()
        pathModels = ArrayList()
        clipPathModels = ArrayList()
    }

    fun drawPaths(canvas: Canvas, offsetX: Float, offsetY: Float, scaleX: Float, scaleY: Float) {
        for (clipPathModel in clipPathModels) {
            canvas.clipPath(clipPathModel.getScaledAndOffsetPath(offsetX, offsetY, scaleX, scaleY))
        }
        for (groupModel in groupModels) {
            groupModel.drawPaths(canvas, offsetX, offsetY, scaleX, scaleY)
        }
        for (pathModel in pathModels) {
            if (pathModel.isFillAndStroke) {
                pathModel.makeFillPaint()
                canvas.drawPath(pathModel.getScaledAndOffsetPath(offsetX, offsetY, scaleX, scaleY), pathModel.pathPaint!!)
                pathModel.makeStrokePaint()
                canvas.drawPath(pathModel.getScaledAndOffsetPath(offsetX, offsetY, scaleX, scaleY), pathModel.pathPaint!!)
            } else {
                canvas.drawPath(pathModel.getScaledAndOffsetPath(offsetX, offsetY, scaleX, scaleY), pathModel.pathPaint!!)
            }
        }
    }

    fun drawPaths(canvas: Canvas) {
        for (clipPathModel in clipPathModels) {
            canvas.clipPath(clipPathModel.path)
        }
        for (groupModel in groupModels) {
            groupModel.drawPaths(canvas)
        }
        for (pathModel in pathModels) {
            if (pathModel.isFillAndStroke) {
                pathModel.makeFillPaint()
                canvas.drawPath(pathModel.path!!, pathModel.pathPaint!!)
                pathModel.makeStrokePaint()
                canvas.drawPath(pathModel.path!!, pathModel.pathPaint!!)
            } else {
                canvas.drawPath(pathModel.path!!, pathModel.pathPaint!!)
            }
        }
    }

    fun scaleAllPaths(scaleMatrix: Matrix) {
        this.scaleMatrix = scaleMatrix

        finalTransformMatrix = Matrix(originalTransformMatrix).apply {
            postConcat(scaleMatrix)
        }

        groupModels.forEach { it.scaleAllPaths(scaleMatrix) }

        pathModels.forEach { it.transform(finalTransformMatrix!!) }

        clipPathModels.forEach { it.transform(finalTransformMatrix!!) }
    }

    fun scaleAllStrokeWidth(ratio: Float) {
        groupModels.forEach { it.scaleAllStrokeWidth(ratio) }
        pathModels.forEach { it.setStrokeRatio(ratio) }
    }

    fun buildTransformMatrix() {

        originalTransformMatrix = Matrix().apply {
            postScale(scaleX, scaleY, pivotX, pivotY)
            postRotate(rotation, pivotX, pivotY)
            postTranslate(translateX, translateY)
            parent?.let { postConcat(it.originalTransformMatrix) }
        }

        groupModels.forEach { it.buildTransformMatrix() }
    }

    private fun updateAndScalePaths() {
        if (scaleMatrix != null) {
            buildTransformMatrix()
            scaleAllPaths(scaleMatrix!!)
        }
    }

    fun getGroupModelByName(name: String): GroupModel? {
        var grpModel: GroupModel? = null
        for (groupModel in groupModels) {
            if (groupModel.name == name) {
                grpModel = groupModel
                return grpModel
            } else {
                grpModel = groupModel.getGroupModelByName(name)
                if (grpModel != null)
                    return grpModel
            }
        }
        return grpModel
    }

    fun getPathModelByName(name: String): PathModel? {
        var pModel: PathModel? = null
        for (pathModel in pathModels) {
            if (pathModel.name == name) {
                return pathModel
            }
        }
        for (groupModel in groupModels) {
            pModel = groupModel.getPathModelByName(name)
            if (pModel != null && pModel.name == name)
                return pModel
        }
        return pModel
    }

    fun getClipPathModelByName(name: String): ClipPathModel? {
        var cModel: ClipPathModel? = null
        for (clipPathModel in clipPathModels) {
            if (clipPathModel.name == name) {
                return clipPathModel
            }
        }
        for (groupModel in groupModels) {
            cModel = groupModel.getClipPathModelByName(name)
            if (cModel != null && cModel.name == name)
                return cModel
        }
        return cModel
    }

    fun addGroupModel(groupModel: GroupModel) {
        groupModels.add(groupModel)
    }

    fun addPathModel(pathModel: PathModel) {
        pathModels.add(pathModel)
    }

    fun addClipPathModel(clipPathModel: ClipPathModel) {
        clipPathModels.add(clipPathModel)
    }

    fun getRotation(): Float = rotation

    fun setRotation(rotation: Float) {
        this.rotation = rotation
        updateAndScalePaths()
    }

    fun getScaleX(): Float = scaleX

    fun setScaleX(scaleX: Float) {
        this.scaleX = scaleX
        updateAndScalePaths()
    }

    fun getScaleY(): Float = scaleY

    fun setScaleY(scaleY: Float) {
        this.scaleY = scaleY
        updateAndScalePaths()
    }

    fun getTranslateX(): Float = translateX

    fun setTranslateX(translateX: Float) {
        this.translateX = translateX
        updateAndScalePaths()
    }

    fun getTranslateY(): Float = translateY

    fun setTranslateY(translateY: Float) {
        this.translateY = translateY
        updateAndScalePaths()
    }
}
