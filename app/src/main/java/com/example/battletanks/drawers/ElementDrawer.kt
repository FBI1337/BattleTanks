package com.example.battletanks.drawers

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import com.example.battletanks.CELL_SIZE
import com.example.battletanks.R
import com.example.battletanks.binding
import com.example.battletanks.enums.Direction
import com.example.battletanks.enums.Material
import com.example.battletanks.models.Coordinate
import com.example.battletanks.models.Element


class ElementDrawer(val container: FrameLayout) {
    var currentMaterial = Material.EMPTY
    private val elementsOnContainer = mutableListOf<Element>()


    fun onTouchContainer(x: Float, y: Float) {
        val topMargin = y.toInt() - (y.toInt() % CELL_SIZE)
        val leftMargin = x.toInt() - (x.toInt() % CELL_SIZE)
        val coordinate = Coordinate(topMargin, leftMargin)
        drawView(coordinate)
    }

    fun drawView(coordinate: Coordinate) {
        val view = ImageView(container.context)
        val layoutParams = FrameLayout.LayoutParams(CELL_SIZE, CELL_SIZE)
        when (currentMaterial) {
            Material.EMPTY -> {

            }

            Material.BRICK -> view.setImageResource(R.drawable.brick)
            Material.CONCRETE -> view.setImageResource(R.drawable.concrete)
            Material.GRASS -> view.setImageResource(R.drawable.grass)
        }
        layoutParams.topMargin = coordinate.top
        layoutParams.leftMargin = coordinate.left
        val viewId = View.generateViewId()
        view.id = viewId
        view.layoutParams = layoutParams
        container.addView(view)
        elementsOnContainer.add(Element(viewId, currentMaterial, coordinate))
    }

    fun move(myTank: View, direction: Direction) {
        val layoutParams = myTank.layoutParams as FrameLayout.LayoutParams
        val currentCoordinate = Coordinate(layoutParams.topMargin, layoutParams.leftMargin)
        when (direction) {
            Direction.UP -> {
                myTank.rotation = 0f
                (myTank.layoutParams as FrameLayout.LayoutParams).topMargin += -CELL_SIZE
            }

            Direction.DOWN -> {
                myTank.rotation = 180f
                (myTank.layoutParams as FrameLayout.LayoutParams).topMargin += CELL_SIZE
            }

            Direction.LEFT -> {
                myTank.rotation = 270f
                (myTank.layoutParams as FrameLayout.LayoutParams).leftMargin -= CELL_SIZE
            }

            Direction.RIGHT -> {
                myTank.rotation = 90f
                (myTank.layoutParams as FrameLayout.LayoutParams).leftMargin += CELL_SIZE
            }
        }
        val nextCoordinate = Coordinate(layoutParams.topMargin, layoutParams.leftMargin)
        if (checkTankCanMoveThrounghBorder(
                nextCoordinate,
                myTank
            ) && checkTankCanMoveThrounghBorder(nextCoordinate)
        ){
            binding.container.removeView(myTank)
            binding.container.addView(myTank)
        } else {
            (myTank.layoutParams as FrameLayout.LayoutParams).topMargin = currentCoordinate.top
            (myTank.layoutParams as FrameLayout.LayoutParams).leftMargin = currentCoordinate.left
        }

    }

    private fun getElementByCoordinates(coordinate: Coordinate) =
        elementsOnContainer.firstOrNull { it.coordinate == coordinate }

    private fun checkTankCanMoveThrounghMaterial(coordinate: Coordinate): Boolean {
        getTankCoordinates(coordinate).forEach {
            val element = getElementByCoordinates(it)
            if (element != null && !element.material.tankConGoThrough) {
                return false
            }
        }
        return true
    }

    private fun checkTankCanMoveThrounghBorder(nextCoordinate: Coordinate, myTank: View): Boolean {
        return nextCoordinate.top >= 0 &&
                nextCoordinate.top + myTank.height <= binding.container.height  &&
                nextCoordinate.left >= 0 &&
                nextCoordinate.left + myTank.width <= binding.container.width
    }

    private fun getTankCoordinates(topLeftCoordinate: Coordinate): List<Coordinate> {
        val coordinateList = mutableListOf<Coordinate>()
        coordinateList.add(topLeftCoordinate)
        coordinateList.add(Coordinate(topLeftCoordinate.top + CELL_SIZE, topLeftCoordinate.left))
        coordinateList.add(Coordinate(topLeftCoordinate.top, topLeftCoordinate.left + CELL_SIZE))
        coordinateList.add(
            Coordinate(
                topLeftCoordinate.top + CELL_SIZE,
                topLeftCoordinate.left + CELL_SIZE
            )
        )
        return coordinateList
    }

}