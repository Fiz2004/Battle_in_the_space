package com.fiz.battleinthespace.feature_gamescreen.ui

import android.graphics.*
import com.fiz.battleinthespace.feature_gamescreen.data.repositories.BitmapRepository
import com.fiz.battleinthespace.feature_gamescreen.ui.models.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Display @Inject constructor(val bitmapRepository: BitmapRepository) {

    private val paintBitmap: Paint = Paint()

    private val paintOutsideCircleJoystick = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        alpha = 80
        strokeWidth = 6F
    }

    private val paintInnerCircleJoystick = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.FILL
        alpha = 40
        strokeWidth = 30F
    }

    private val paintHelperPlayer = Paint().apply {
        style = Paint.Style.FILL
        alpha = 80
        strokeWidth = 30F
    }

    private val paintHelperMeteorites = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
        alpha = 160
        strokeWidth = 20F
    }

    private val paintRound = Paint().apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
    }

    private val paintPlayer = Paint().apply {
        textAlign = Paint.Align.LEFT
    }

    fun render(
        stateGame: ViewState,
        canvas: Canvas
    ) {
        drawBackground(stateGame.gameState.backgroundsUi, canvas)
        drawActors(
            stateGame.gameState.spaceshipsUi,
            stateGame.gameState.spaceshipsFlyUi,
            stateGame.gameState.bulletsUi,
            stateGame.gameState.meteoritesUi, canvas
        )
        drawAnimationDestroys(
            stateGame.gameState.bulletsAnimationsDestroyUi,
            stateGame.gameState.spaceShipsAnimationsDestroyUi,
            canvas
        )
        drawJoystick(stateGame.controllerState, canvas)
        drawHelper(
            stateGame.gameState.helpersPlayerUi,
            stateGame.gameState.helpersMeteoriteUi,
            canvas
        )
    }

    private fun drawBackground(backgroundsUi: List<BackgroundUi>, canvas: Canvas) {
        canvas.drawColor(Color.BLACK)

        backgroundsUi.forEach {
            canvas.drawBitmap(
                bitmapRepository.bmpBackground[it.value],
                it.src,
                it.dst,
                paintBitmap
            )
        }
    }

    private fun drawActors(
        spaceshipsUi: List<SpriteUi>,
        spaceshipsFlyUi: List<SpriteUi>,
        bulletsUi: List<SpriteUi>,
        meteoritesUi: List<MeteoriteSpriteUi>,
        canvas: Canvas
    ) {

        spaceshipsUi.forEach {
            drawFrame(
                bitmapRepository.bmpSpaceship[it.value],
                it.centerX,
                it.centerY,
                it.src,
                it.dst,
                it.angle,
                canvas
            )
        }

        spaceshipsFlyUi.forEach {
            drawFrame(
                bitmapRepository.bmpSpaceshipFly[it.value],
                it.centerX,
                it.centerY,
                it.src,
                it.dst,
                it.angle,
                canvas
            )
        }

        bulletsUi.forEach {
            drawFrame(
                bitmapRepository.bmpWeapon[it.value],
                it.centerX,
                it.centerY,
                it.src,
                it.dst,
                it.angle,
                canvas
            )
        }

        meteoritesUi.forEach {
            drawFrame(
                bitmapRepository.bmpMeteorites[it.view][it.viewSize],
                it.centerX,
                it.centerY,
                it.src,
                it.dst,
                it.angle,
                canvas
            )
        }
    }

    private fun drawFrame(
        bmp: Bitmap,
        x: Float,
        y: Float,
        src: Rect,
        dst: RectF,
        angle: Float,
        canvas: Canvas
    ) {
        canvas.save()
        canvas.translate(x, y)
        canvas.rotate(angle)
        canvas.drawBitmap(bmp, src, dst, paintBitmap)
        canvas.restore()
    }

    private fun drawAnimationDestroys(
        bulletsSpriteUi: List<SpriteUi>,
        spaceShipsSpriteUi: List<SpriteUi>, canvas: Canvas
    ) {
        bulletsSpriteUi.forEach {
            drawFrame(
                bitmapRepository.bmpBulletDestroy[it.value],
                it.centerX,
                it.centerY,
                it.src,
                it.dst,
                it.angle,
                canvas
            )
        }

        spaceShipsSpriteUi.forEach {
            drawFrame(
                bitmapRepository.bmpSpaceshipDestroy[it.value],
                it.centerX,
                it.centerY,
                it.src,
                it.dst,
                it.angle,
                canvas
            )
        }
    }

    private fun drawJoystick(
        controllerState: ControllerState?,
        canvas: Canvas
    ) {
        controllerState ?: return

        canvas.drawCircle(
            controllerState.centerXOutsideCircle,
            controllerState.centerYOutsideCircle,
            controllerState.widthJoystick,
            paintOutsideCircleJoystick
        )

        canvas.drawCircle(
            controllerState.centerXInnerCircle,
            controllerState.centerYInnerCircle,
            30F,
            paintInnerCircleJoystick
        )
    }

    private fun drawHelper(
        helpersPlayerUi: List<HelperPlayerUi>,
        helpersMeteoriteUi: List<HelperMeteoritesUi>,
        canvas: Canvas
    ) {
        helpersPlayerUi.forEach {
            paintHelperPlayer.color = it.value

            canvas.drawCircle(
                it.centerX,
                it.centerY,
                it.radius,
                paintHelperPlayer
            )
        }

        helpersMeteoriteUi.forEach {
            canvas.drawCircle(
                it.centerX,
                it.centerY,
                it.radius,
                paintHelperMeteorites
            )
        }
    }

    fun renderInfo(stateGame: ViewState, canvasInfo: Canvas, FPS: Int) {
        drawInfo(stateGame, canvasInfo, FPS)
    }

    private fun drawInfo(stateGame: ViewState, canvasInfo: Canvas, FPS: Int) {
        canvasInfo.drawColor(Color.BLACK)

        stateGame.gameState.textRoundInfoUi.also {
            paintRound.textSize = it.textSize
            paintRound.color = it.color
            canvasInfo.drawText(
                it.value,
                it.x,
                it.y,
                paintRound
            )
        }

        stateGame.gameState.textsInfoUi.forEach {
            paintPlayer.textSize = it.textSize
            paintPlayer.color = it.color
            canvasInfo.drawText(
                it.value,
                it.x,
                it.y,
                paintPlayer
            )
        }

        stateGame.gameState.infoUi.forEach {
            canvasInfo.drawBitmap(
                bitmapRepository.bmpSpaceshipLife[it.value], it.src,
                it.dst,
                paintBitmap
            )
        }

        canvasInfo.drawText(
            FPS.toString(),
            0F,
            66F,
            paintPlayer
        )
    }
}

