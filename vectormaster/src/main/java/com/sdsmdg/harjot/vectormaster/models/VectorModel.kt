package com.sdsmdg.harjot.vectormaster.models

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Path

import com.sdsmdg.harjot.vectormaster.enums.TintMode

import java.util.ArrayList

class VectorModel {

    var name: String? = null

    var width: Float = 0.toFloat()
    var height: Float = 0.toFloat()

    var alpha = 1.0f

    var isAutoMirrored = false

    var tint = Color.TRANSPARENT
    var tintMode = TintMode.SCR_IN

    var viewportWidth: Float = 0.toFloat()
    var viewportHeight: Float = 0.toFloat()

    val groupModels: ArrayList<GroupModel> = ArrayList()
    val pathModels: ArrayList<PathModel> = ArrayList()
    val clipPathModels: ArrayList<ClipPathModel> = ArrayList()

    var fullpath: Path? = null

    private var scaleMatrix: Matrix? = null

    init {
        fullpath = Path()
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
                canvas.drawPath(pathModel.getScaledAndOffsetPath(offsetX, offsetY, scaleX, scaleY), pathModel.pathPaint)
                pathModel.makeStrokePaint()
                canvas.drawPath(pathModel.getScaledAndOffsetPath(offsetX, offsetY, scaleX, scaleY), pathModel.pathPaint)
            } else {
                canvas.drawPath(pathModel.getScaledAndOffsetPath(offsetX, offsetY, scaleX, scaleY), pathModel.pathPaint)
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
                canvas.drawPath(pathModel.path, pathModel.pathPaint)
                pathModel.makeStrokePaint()
                canvas.drawPath(pathModel.path, pathModel.pathPaint)
            } else {
                canvas.drawPath(pathModel.path, pathModel.pathPaint)
            }
        }
    }

    fun scaleAllPaths(scaleMatrix: Matrix) {
        this.scaleMatrix = scaleMatrix

        groupModels.forEach { it.scaleAllPaths(scaleMatrix) }

        pathModels.forEach { it.transform(scaleMatrix) }

        clipPathModels.forEach { it.path?.transform(scaleMatrix) }
    }

    fun scaleAllStrokeWidth(ratio: Float) {
        for (groupModel in groupModels) {
            groupModel.scaleAllStrokeWidth(ratio)
        }
        for (pathModel in pathModels) {
            pathModel.strokeRatio = ratio
        }
    }

    fun buildTransformMatrices() {
        for (groupModel in groupModels) {
            groupModel.buildTransformMatrix()
        }
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
}
