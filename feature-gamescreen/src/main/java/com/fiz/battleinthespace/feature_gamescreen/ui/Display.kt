package com.fiz.battleinthespace.feature_gamescreen.ui

import android.graphics.*
import com.fiz.battleinthespace.feature_gamescreen.ui.models.*
import com.fiz.battleinthespace.repositories.BitmapRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Display @Inject constructor(private val bitmapRepository: BitmapRepository) {

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
        style = Paint.Style.STROKE
        alpha = 80
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 10F
    }

    private val paintTextHelperPlayer = Paint().apply {
        textAlign = Paint.Align.CENTER
        textSize = 30F
    }

    private val paintHelperMeteorites = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        alpha = 160
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 10F
    }

    private val paintTextHelperMeteorites = Paint().apply {
        color = Color.RED
        textAlign = Paint.Align.CENTER
        textSize = 30F
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
        if (stateGame.gameState == null) return
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
            controllerState.centerXOutsideCircle.toFloat(),
            controllerState.centerYOutsideCircle.toFloat(),
            controllerState.widthJoystick.toFloat(),
            paintOutsideCircleJoystick
        )

        canvas.drawCircle(
            controllerState.centerXInnerCircle.toFloat(),
            controllerState.centerYInnerCircle.toFloat(),
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
            paintHelperPlayer.color = it.color
            paintTextHelperPlayer.color = it.color

            canvas.save()
            canvas.translate(it.centerX, it.centerY)
            canvas.rotate(it.angle - 90)
            val path = Path()
            path.moveTo(-15f, 23f)
            path.lineTo(0f, 30f)
            path.lineTo(15f, 23f)
            canvas.drawPath(path, paintHelperPlayer)
            canvas.restore()

            canvas.drawText(
                "S",
                it.centerX,
                it.centerY,
                paintTextHelperPlayer
            )
        }

        helpersMeteoriteUi.forEach {

            canvas.save()
            canvas.translate(it.centerX, it.centerY)
            canvas.rotate(it.angle - 90)
            val path = Path()
            path.moveTo(-15f, 23f)
            path.lineTo(0f, 30f)
            path.lineTo(15f, 23f)
            canvas.drawPath(path, paintHelperMeteorites)
            canvas.restore()

            canvas.drawText(
                "M",
                it.centerX,
                it.centerY,
                paintTextHelperMeteorites
            )
        }
    }

    fun renderInfo(stateGame: ViewState, canvasInfo: Canvas) {
        drawInfo(stateGame, canvasInfo)
    }

    private fun drawInfo(stateGame: ViewState, canvasInfo: Canvas) {
        if (stateGame.gameState == null) return

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

    }
}

