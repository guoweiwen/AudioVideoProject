package com.bonade.filterlibrary.glfilter

import android.content.Context
import android.opengl.GLES11Ext
import android.opengl.GLES30
import com.bonade.filterlibrary.utils.OpenGLUtils


/**
 * 外部纹理(OES纹理)输入
 * Created by cain on 2017/7/9.
 */

class GLImageOESInputFilter @JvmOverloads constructor(
        context: Context,
          vertexShader: String = OpenGLUtils.getShaderFromAssets(context,"shader/base/vertex_oes_input.glsl")!!,
          fragmentShader: String = OpenGLUtils.getShaderFromAssets(context, "shader/base/fragment_oes_input.glsl")!!)
    : GLImageFilter(context, vertexShader, fragmentShader) {

    private var mTransformMatrixHandle: Int = 0
    private var mTransformMatrix: FloatArray? = null

    override fun getTextureType(): Int {
        return GLES11Ext.GL_TEXTURE_EXTERNAL_OES
    }

    override fun initProgramHandle() {
        super.initProgramHandle()
        mTransformMatrixHandle = GLES30.glGetUniformLocation(mProgramHandle, "transformMatrix")
    }

    override fun onDrawFrameBegin() {
        super.onDrawFrameBegin()
        GLES30.glUniformMatrix4fv(mTransformMatrixHandle, 1, false, mTransformMatrix, 0)
    }

    /**
     * 设置SurfaceTexture的变换矩阵
     * @param transformMatrix
     */
    fun setTextureTransformMatrix(transformMatrix: FloatArray) {
        mTransformMatrix = transformMatrix
    }

}
