package com.bonade.fakedouyin.photographalbum

import android.graphics.Bitmap

/**
 * Created by wyman
on 2019-07-07.
 */
object ImageSplitter {

    /**
     * @param xPiece 横向数量
     * @param yPiece 纵向数量
     * */
    fun split(bitmap : Bitmap,xPiece : Int,yPiece : Int) : ArrayList<ImagePiece>{
        val pieces = ArrayList<ImagePiece>(xPiece * yPiece)
        val width = bitmap.width
        val height = bitmap.height
        val pieceWidth = width / xPiece
        val pieceHeight = height / yPiece
        for(i in 0 until yPiece){
            for(j in 0 until xPiece){
                val pointX = j * pieceWidth
                val pointY = i * pieceHeight
                val bm = Bitmap.createBitmap(bitmap,pointX,pointY,pieceWidth,pieceHeight)
                val piece = ImagePiece(pointX,pointY,bm)
                pieces.add(piece)
            }
        }
        return pieces
    }
}











