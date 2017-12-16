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

    val fullpath: Path = Path()

    private var scaleMatrix: Matrix? = null

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
        groupModels.forEach { it.scaleAllStrokeWidth(ratio) }
        pathModels.forEach { it.strokeRatio = ratio }
    }

    fun buildTransformMatrices() {
        groupModels.forEach(GroupModel::buildTransformMatrix)
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
