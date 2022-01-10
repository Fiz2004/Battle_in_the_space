package com.fiz.android.battleinthespace

import kotlin.math.cos
import kotlin.math.sin

class ClGam() {
    var spaceship: Array<Spaceship> = emptyArray()
    private var scores: Array<Int> = emptyArray()
    var lifes: Array<Int> = emptyArray()
    var bullet: Array<Bullet> = emptyArray()
    var meteorites: Array<Meteorite> = emptyArray()
    var background: Array<Array<Int>> = emptyArray()
    var animationBulletDestroy: Array<AnimationBulletDestroy> = emptyArray()
    var animationSpaceshipDestroy: Array<AnimationSpaceshipDestroy> = emptyArray()
    private var respawn: Array<Respawn> = emptyArray()
    private var round: Int = 1
    val width = 800
    val height = 600

    //Кол-во просковков на рисование одного кадра
    var Raz: Int = 0
    var Raz1: Int = 0
    //Преобразует X с учетом координат
// matr:array[0..49, 0..49]of Byte;

    var Players: Int = 2

    init {
        respawn[0] = Respawn(
            X = 50,
            Y = 50,
            Angle = 0
        )
        respawn[1] = Respawn(
            X = 750,
            Y = 50,
            Angle = 180
        )
        respawn[2] = Respawn(
            X = 50,
            Y = 530,
            Angle = 0
        )
        respawn[3] = Respawn(
            X = 750,
            Y = 530,
            Angle = 180
        )

//    for n: = 0 to 49 do
//    for k: = 0 to 49 do
//    if bmp.KorMask.Canvas.Pixels[n, k] = clWhite then
//            Matr[n, k]: = 1
//    else
//    Matr[n, k]: = 0;

    }

    //Преобразует X с учетом координат
    fun PreobX(Index: Double): Double {
        var result = Index
        if (Index > width + 100)
            result = Index - width
        if (Index < 0)
            result = Index + width
        return result
    }

    //Преобразует Y с учетом координат
    fun PreobY(Index: Double): Double {
        var result = Index
        if (Index > height)
            result = Index - height
        if (Index < 0)
            result = Index + height
        return result
    }

    fun newGame() {
        spaceship = emptyArray()
        for (n in 0 until Players) {
            spaceship += Spaceship(
                CX = respawn[n].X.toDouble(),
                CY = respawn[n].Y.toDouble(),
                VX = 0.0,
                VY = 0.0,
                Angle = respawn[n].Angle.toDouble(),
                InGame = true
            )
        }

        scores = emptyArray()
        for (n in 0 until Players) {
            scores += 0
        }

        lifes = emptyArray()
        for (n in 0 until Players) {
            lifes += 2
        }

        meteorites = emptyArray()
        for (n in 0..0) {
            val angle = (0..360).shuffled().first()
            meteorites += Meteorite(
                X = 400.0,
                Y = 300.0,
                Angle = angle.toDouble(),
                VX = +cVmaxMet * cos(angle / 180 * Math.PI),
                VY = -cVmaxMet * sin(angle / 180 * Math.PI),
                Razmer = 25,
                TipRazmer = 0,
                Tip = 0
            )
        }

        round = 1

        background = emptyArray()
        for (n in 0..30) {
            var row: Array<Int> = emptyArray()
            for (k in 0..30)
                row += (0..7).shuffled().first()
            background += row
        }

        animationBulletDestroy = emptyArray()
        animationSpaceshipDestroy = emptyArray()
    }

    fun newRound() {
        round += 1

        spaceship = emptyArray()
        for (n in 0 until Players) {
            spaceship += Spaceship(
                CX = respawn[n].X.toDouble(),
                CY = respawn[n].Y.toDouble(),
                VX = 0.0,
                VY = 0.0,
                Angle = respawn[n].Angle.toDouble(),
                InGame = true
            )
        }

        lifes = emptyArray()
        for (n in 0 until Players) {
            lifes += 2
        }


        meteorites = emptyArray()
        for (n in 0..1) {
            if (n == 0) {
                val angle = (0..360).shuffled().first()
                meteorites += Meteorite(
                    X = 400.0,
                    Y = 300.0,
                    VX = +cVmaxMet * cos(angle / 180 * Math.PI),
                    VY = -cVmaxMet * sin(angle / 180 * Math.PI),
                    Angle = angle.toDouble(),
                    Razmer = 25,
                    TipRazmer = 0,
                    Tip = round - 1
                )
            }
            if (n == 1) {
                val angle = (0..360).shuffled().first()
                meteorites += Meteorite(
                    X = 400.0,
                    Y = 300.0,
                    VX = +cVmaxMet * cos(angle / 180 * Math.PI),
                    VY = -cVmaxMet * sin(angle / 180 * Math.PI),
                    Angle = angle.toDouble(),
                    Razmer = 25,
                    TipRazmer = 0,
                    Tip = round - 1
                )
            }
        }

        background = emptyArray()
        for (n in 0..30) {
            var row: Array<Int> = emptyArray()
            for (k in 0..30)
                row += (0..7).shuffled().first()
            background += row
        }
    }

    fun Up(Nom: Int) {
        if ((spaceship[Nom].Angle != 180.0) && (spaceship[Nom].Angle != 0.0))
            spaceship[Nom].VY -= 0.05 * sin(spaceship[Nom].Angle / 180 * Math.PI)

        if (spaceship[Nom].VY >= cVmaxKor)
            spaceship[Nom].VY = cVmaxKor.toDouble()

        if (spaceship[Nom].VY <= -cVmaxKor)
            spaceship[Nom].VY = (-cVmaxKor).toDouble()

        if ((spaceship[Nom].Angle != 90.0) && (spaceship[Nom].Angle != 270.0))
            spaceship[Nom].VX += 0.05 * cos(spaceship[Nom].Angle / 180 * Math.PI)

        if (spaceship[Nom].VX >= cVmaxKor)
            spaceship[Nom].VX = cVmaxKor.toDouble()

        if (spaceship[Nom].VX <= -cVmaxKor)
            spaceship[Nom].VX = (-cVmaxKor).toDouble()
    }

    fun Right(Nom: Int) {
        spaceship[Nom].Angle -= 5
        if (spaceship[Nom].Angle + 5 > 360)
            spaceship[Nom].Angle -= 360
    }

    fun Left(Nom: Int) {
        spaceship[Nom].Angle += 5
        if (spaceship[Nom].Angle + 5 < 0)
            spaceship[Nom].Angle += +360
    }

    fun Vystr(Nom: Int) {
        if (spaceship[Nom].InGame == true) {
            bullet += Bullet(
                X = spaceship[Nom].CX + 25 * cos(spaceship[Nom].Angle / 180 * Math.PI),
                Y = spaceship[Nom].CY - 25 * sin(spaceship[Nom].Angle / 180 * Math.PI),
                VX = +cVmaxPul * cos(spaceship[Nom].Angle / 180 * Math.PI),
                VY = -cVmaxPul * sin(spaceship[Nom].Angle / 180 * Math.PI),
                Angle = 0.0,// TODO Возможно не требуется проверить
                Put = 0.0,
                Nom = Nom
            )
        }
    }

    fun Collision1() {
        var VX1: Double
        var VX2: Double
        var VY1: Double
        var VY2: Double
        var n: Int
        var k: Int
        var z: Int
        var Nach: Int
        var Nachn: Int
        var Nachk: Int

        if (Players > 1) {
            for (n in 0..Players - 2)
                for (k in n + 1..Players - 1)
                    if (spaceship[n].InGame == true && spaceship[k].InGame == true)
                        if (((PreobX(spaceship[n].CX + 25) >= PreobX(spaceship[k].CX - 25)) &&
                                    (PreobX(spaceship[n].CX + 25) <= PreobX(spaceship[k].CX + 25)) &&

                                    (((PreobY(spaceship[n].CY + 25) >= PreobY(spaceship[k].CY - 25)) &&
                                            (PreobY(spaceship[n].CY + 25) <= PreobY(
                                                spaceship[k].CY + 25
                                            ))) ||

                                            ((PreobY(spaceship[n].CY - 25) >= PreobY(
                                                spaceship[k].CY - 25
                                            )) &&
                                                    (PreobY(spaceship[n].CY - 25) <= PreobY(
                                                        spaceship[k].CY + 25
                                                    ))))
                                    ) ||

                            ((PreobX(spaceship[n].CX - 25) <= PreobX(spaceship[k].CX + 25)) &&
                                    (PreobX(spaceship[n].CX - 25) >= PreobX(spaceship[k].CX - 25)) &&

                                    (((PreobY(spaceship[n].CY + 25) >= PreobY(spaceship[k].CY - 25)) &&
                                            (PreobY(spaceship[n].CY + 25) <= PreobY(
                                                spaceship[k].CY + 25
                                            ))) ||

                                            ((PreobY(spaceship[n].CY - 25) >= PreobY(
                                                spaceship[k].CY - 25
                                            )) &&
                                                    (PreobY(spaceship[n].CY - 25) <= PreobY(
                                                        spaceship[k].CY + 25
                                                    )))))
                        ) {
                            VX1 = spaceship[n].VX
                            VX2 = spaceship[k].VX
                            VY1 = spaceship[n].VY
                            VY2 = spaceship[k].VY
                            if (((VX1 > 0) && (VX2 < 0)) || ((VX1 < 0) && (VX2 > 0))) {
                                spaceship[n].VX = -VX1
                                spaceship[k].VX = -VX2
                            } else {
                                spaceship[n].VX = (VX1 + VX2) / 2;
                                spaceship[k].VX = (VX1 + VX2) / 2;
                                if (Math.abs(VX1) > Math.abs(VX2))
                                    spaceship[k].VX = 2 * spaceship[k].VX
                                else
                                    if (Math.abs(VX1) < Math.abs(VX2))
                                        spaceship[n].VX = 2 * spaceship[n].VX
                            }
                            if (spaceship[n].VX > cVmaxKor)
                                spaceship[n].VX = cVmaxKor.toDouble()
                            if (spaceship[k].VX > cVmaxKor)
                                spaceship[k].VX = cVmaxKor.toDouble()
                            if (spaceship[n].VX < -cVmaxKor)
                                spaceship[n].VX = -cVmaxKor.toDouble()
                            if (spaceship[k].VX < -cVmaxKor)
                                spaceship[k].VX = -cVmaxKor.toDouble()
                            if ((VY1 > 0 && VY2 < 0) || (VY1 < 0 && VY2 > 0)) {
                                spaceship[n].VY = -VY1
                                spaceship[k].VY = -VY2
                            } else {
                                spaceship[n].VY = (VY1 + VY2) / 2
                                spaceship[k].VY = (VY1 + VY2) / 2
                                if (Math.abs(VY1) > Math.abs(VY2))
                                    spaceship[k].VY *= 2
                                else
                                    if (Math.abs(VY1) < Math.abs(VY2))
                                        spaceship[n].VY *= 2
                            }
                            if (spaceship[n].VY > cVmaxKor)
                                spaceship[n].VY = cVmaxKor.toDouble()
                            if (spaceship[k].VY > cVmaxKor)
                                spaceship[k].VY = cVmaxKor.toDouble()
                            if (spaceship[n].VY < -cVmaxKor)
                                spaceship[n].VY = -cVmaxKor.toDouble()
                            if (spaceship[k].VY < -cVmaxKor)
                                spaceship[k].VY = -cVmaxKor.toDouble()
                        }
        }
    }


    fun Collision2() {
        var VX1: Double
        var VX2: Double
        var VY1: Double
        var VY2: Double
        var n: Int
        var k: Int
        var z: Int
        var Nach: Int
        var Nachn: Int
        var Nachk: Int
        if (bullet.size > 0) {
            Nach = 0
            for (n in 0..Players - 1)
                if (spaceship[n].InGame == true)
                    for (k in Nach..bullet.size)
                        if ((((bullet[k].X + 2 >= spaceship[n].CX - 25) && (bullet[k].X + 2 <= spaceship[n].CX + 25) &&
                                    ((bullet[k].Y + 2 >= spaceship[n].CY - 25) && (bullet[k].Y + 2 <= spaceship[n].CY + 25) ||
                                            (bullet[k].Y - 2 >= spaceship[n].CY - 25) && (bullet[k].Y - 2 <= spaceship[n].CY + 25))) ||
                                    ((bullet[k].X - 2 >= spaceship[n].CX - 25) && (bullet[k].X - 2 <= spaceship[n].CX + 25) && (
                                            (bullet[k].Y + 2 >= spaceship[n].CY - 25) && (bullet[k].Y + 2 <= spaceship[n].CY + 25) ||
                                                    (bullet[k].Y - 2 >= spaceship[n].CY - 25) && (bullet[k].Y - 2 <= spaceship[n].CY + 25)))
                                    ) &&
                            (n != bullet[k].Nom)
                        ) {
                            VX1 = spaceship[n].VX;
                            VX2 = bullet[k].VX;
                            VY1 = spaceship[n].VY;
                            VY2 = bullet[k].VY;
                            if (((VX1 > 0) && (VX2 < 0)) || ((VX1 < 0) && (VX2 > 0)))
                                spaceship[n].VX = -VX1
                            else {
                                spaceship[n].VX = (VX1 + VX2) / 2
                                spaceship[n].VX = 2 * spaceship[n].VX
                            }
                            if (spaceship[n].VX > cVmaxKor)
                                spaceship[n].VX = cVmaxKor.toDouble()
                            if (spaceship[n].VX < -cVmaxKor)
                                spaceship[n].VX = -cVmaxKor.toDouble()
                            if (((VY1 > 0) && (VY2 < 0)) || ((VY1 < 0) && (VY2 > 0)))
                                spaceship[n].VY = -VY1
                            else {
                                spaceship[n].VY = (VY1 + VY2) / 2
                                spaceship[n].VY = 2 * spaceship[n].VY
                            }
                            if (spaceship[n].VY > cVmaxKor)
                                spaceship[n].VY = cVmaxKor.toDouble()
                            if (spaceship[n].VY < -cVmaxKor)
                                spaceship[n].VY = (-cVmaxKor).toDouble()
                        }

            for (n in 0..Players - 1)
                if (spaceship[n].InGame == true)
                    for (k in Nach..bullet.size)
                        if (animationBulletDestroy.size == 0)
                            SetLength(animationBulletDestroy, 1)
                        else
                            SetLength(
                                animationBulletDestroy,
                                High(animationBulletDestroy) + 2
                            );
            animationBulletDestroy[animationBulletDestroy.size].X = bullet[k].X
            animationBulletDestroy[animationBulletDestroy.size].Y = bullet[k].Y
            animationBulletDestroy[animationBulletDestroy.size].Kadr = 0
            for (z in k..bullet.size - 1)
                bullet[z] = bullet[z + 1];
            SetLength(bullet, High(bullet));
            Nach: = k;
            goto m1;
        }
    }
}

fun Collision3() {
    var VX1: Double
    var VX2: Double
    var VY1: Double
    var VY2: Double
    var n: Int
    var k: Int
    var z: Int
    var Nach: Int
    var Nachn: Int
    var Nachk: Int

    Nach = 0;
    m2:
    for (n in Nach..bullet.size - 1)
        for (k in n + 1..bullet.size)
            if ((PreobX(bullet[n].X + 2) >= PreobX(bullet[k].X - 2)) &&
                (PreobX(bullet[n].X + 2) <= PreobX(bullet[k].X + 2)) &&

                (((PreobY(bullet[n].Y + 2) >= PreobY(bullet[k].Y - 2)) &&
                        (PreobY(bullet[n].Y + 2) <= PreobY(bullet[k].Y + 2))) ||

                        ((PreobY(bullet[n].Y - 2) >= PreobY(bullet[k].Y - 2)) &&
                                (PreobY(bullet[n].Y - 2) <= PreobY(bullet[k].Y + 2))))
            ) ||

                    ((PreobX(bullet[n].X - 2) <= PreobX(bullet[k].X + 2)) &&
                            (PreobX(bullet[n].X - 2) >= PreobX(bullet[k].X - 2)) &&

                            (((PreobY(bullet[n].Y + 2) >= PreobY(bullet[k].Y - 2)) &&
                                    (PreobY(bullet[n].Y + 2) <= PreobY(bullet[k].Y + 2))) ||

                                    ((PreobY(bullet[n].Y - 2) >= PreobY(bullet[k].Y - 2)) &&
                                            (PreobY(bullet[n].Y - 2) <= PreobY(bullet[k].Y + 2))))) then
                    {
                        if High(animationBulletDestroy) = -1 then
                                SetLength(animationBulletDestroy, 1)
                        else
                            SetLength(
                                animationBulletDestroy,
                                High(animationBulletDestroy) + 2
                            );
                        animationBulletDestroy[High(animationBulletDestroy)].X: = bullet[k].X;
                        animationBulletDestroy[High(animationBulletDestroy)].Y: = bullet[k].Y;
                        animationBulletDestroy[High(animationBulletDestroy)].kadr: = 0;
                        for z: = k to High(bullet)-1 do
                        bullet[z]: = bullet[z+1];
                        SetLength(bullet, High(bullet));
                        for z: = n to High(bullet)-1 do
                        bullet[z]: = bullet[z+1];
                        SetLength(bullet, High(bullet));
                        Nach: = n;
                        goto m2;
                    }
}

fun Collision4() {
    var VX1: Double
    var VX2: Double
    var VY1: Double
    var VY2: Double
    var n: Int
    var k: Int
    var z: Int
    var Nach: Int
    var Nachn: Int
    var Nachk: Int

    for (n in 0..meteorites.size - 1)
        for (k in n + 1..meteorites.size)
            if (((PreobX(meteorites[n].X + meteorites[n].Razmer) >= PreobX(meteorites[k].X - meteorites[k].Razmer)) &&
                        (PreobX(meteorites[n].X + meteorites[n].Razmer) <= PreobX(meteorites[k].X + meteorites[k].Razmer)) &&

                        (((PreobY(meteorites[n].Y + meteorites[n].Razmer) >= PreobY(meteorites[k].Y - meteorites[k].Razmer)) &&
                                (PreobY(meteorites[n].Y + meteorites[n].Razmer) <= PreobY(
                                    meteorites[k].Y + meteorites[k].Razmer
                                ))) ||

                                ((PreobY(meteorites[n].Y - meteorites[n].Razmer) >= PreobY(
                                    meteorites[k].Y - meteorites[k].Razmer
                                )) &&
                                        (PreobY(meteorites[n].Y - meteorites[n].Razmer) <= PreobY(
                                            meteorites[k].Y + meteorites[k].Razmer
                                        ))))
                        ) ||

                (
                        (PreobX(meteorites[n].X - meteorites[n].Razmer) <= PreobX(
                            meteorites[k].X + meteorites[k].Razmer
                        )) &&
                                (PreobX(meteorites[n].X - meteorites[n].Razmer) >= PreobX(
                                    meteorites[k].X - meteorites[k].Razmer
                                )) &&

                                (((PreobY(meteorites[n].Y + meteorites[n].Razmer) >= PreobY(
                                    meteorites[k].Y - meteorites[k].Razmer
                                )) &&
                                        (PreobY(meteorites[n].Y + meteorites[n].Razmer) <= PreobY(
                                            meteorites[k].Y + meteorites[k].Razmer
                                        ))) ||

                                        ((PreobY(meteorites[n].Y - meteorites[n].Razmer) >= PreobY(
                                            meteorites[k].Y - meteorites[k].Razmer
                                        )) &&
                                                (PreobY(meteorites[n].Y - meteorites[n].Razmer) <= PreobY(
                                                    meteorites[k].Y + meteorites[k].Razmer
                                                ))))
                        )
            ) {
                VX1 = meteorites[n].VX
                VX2 = meteorites[k].VX
                VY1 = meteorites[n].VY
                VY2 = meteorites[k].VY
                if (((VX1 > 0) && (VX2 < 0)) || ((VX1 < 0) && (VX2 > 0))) {
                    meteorites[n].VX = -VX1
                    meteorites[k].VX = -VX2
                } else {
                    meteorites[n].VX = (VX1 + VX2) / 2;
                    meteorites[k].VX = (VX1 + VX2) / 2;
                    if (Math.abs(VX1) > Math.abs(VX2))
                        meteorites[k].VX *= 2
                    else
                        if (Math.abs(VX1) < Math.abs(VX2))
                            meteorites[n].VX *= 2;
                }
                if (meteorites[n].VX > cVmaxMet)
                    meteorites[n].VX = cVmaxMet
                if (meteorites[k].VX > cVmaxMet)
                    meteorites[k].VX = cVmaxMet
                if (meteorites[n].VX < -cVmaxMet)
                    meteorites[n].VX = -cVmaxMet
                if (meteorites[k].VX < -cVmaxMet)
                    meteorites[k].VX = -cVmaxMet
                if (((VY1 > 0) && (VY2 < 0)) || ((VY1 < 0) && (VY2 > 0))) {
                    meteorites[n].VY = -VY1
                    meteorites[k].VY = -VY2
                } else {
                    meteorites[n].VY = (VY1 + VY2) / 2
                    meteorites[k].VY = (VY1 + VY2) / 2
                    if (Math.abs(VY1) > Math.abs(VY2))
                        meteorites[k].VY *= 2
                    else
                        if (Math.abs(VY1) < Math.abs(VY2))
                            meteorites[n].VY *= 2
                }
                if (meteorites[n].VY > cVmaxMet)
                    meteorites[n].VY = cVmaxMet
                if (meteorites[k].VY > cVmaxMet)
                    meteorites[k].VY = cVmaxMet
                if (meteorites[n].VY < -cVmaxMet)
                    meteorites[n].VY = -cVmaxMet
                if (meteorites[k].VY < -cVmaxMet)
                    meteorites[k].VY = -cVmaxMet;
            }
    Nachn = 0
    Nachk = 0
    Nach = 0
    m3 :
    if (bullet.size >= 0)
        if (meteorites.size >= 0) {
            for (n in Nachn..meteorites.size)
                for (k in Nach..bullet.size)
                    if (((bullet[k].X + 2 >= meteorites[n].X - meteorites[n].Razmer) && (bullet[k].X + 2 <= meteorites[n].X + meteorites[n].Razmer) && (
                                (bullet[k].Y + 2 >= meteorites[n].Y - meteorites[n].Razmer) && (bullet[k].Y + 2 <= meteorites[n].Y + meteorites[n].Razmer) ||
                                        (bullet[k].Y - 2 >= meteorites[n].Y - meteorites[n].Razmer) && (bullet[k].Y - 2 <= meteorites[n].Y + meteorites[n].Razmer))
                                ) ||
                        (
                                (bullet[k].X - 2 >= meteorites[n].X - meteorites[n].Razmer) && (bullet[k].X - 2 <= meteorites[n].X + meteorites[n].Razmer) && (
                                        (bullet[k].Y + 2 >= meteorites[n].Y - meteorites[n].Razmer) && (bullet[k].Y + 2 <= meteorites[n].Y + meteorites[n].Razmer) ||
                                                (bullet[k].Y - 2 >= meteorites[n].Y - meteorites[n].Razmer) && (bullet[k].Y - 2 <= meteorites[n].Y + meteorites[n].Razmer))
                                )
                    ) {
                        meteorites[n].Razmer = meteorites[n].Razmer - 5;
                        meteorites[n].TipRazmer = meteorites[n].TipRazmer + 1;
                        scores[bullet[k].Nom] =
                            Chet[bullet[k].Nom] + 100 * meteorites[n].TipRazmer;
                        if (meteorites[n].TipRazmer > 3) {
                            for (z in n..meteorites.size - 1)
                                meteorites[z] = meteorite[z + 1]
                            SetLength(meteorites, High(meteorites));
                            Nachn = n
                            if (animationBulletDestroy.size === 0)
                                SetLength(animationBulletDestroy, 1)
                            else
                                SetLength(
                                    animationBulletDestroy,
                                    High(animationBulletDestroy) + 2
                                );
                            animationBulletDestroy[High(animationBulletDestroy)].X =
                                bullet[k].X
                            animationBulletDestroy[High(animationBulletDestroy)].Y =
                                bullet[k].Y
                            animationBulletDestroy[High(animationBulletDestroy)].Kadr =
                                0
                            for (z in k..bullet.size - 1)
                                bullet[z] = bullet[z + 1]
                            SetLength(bullet, High(bullet));
                            Nachk = k
                            goto m3
                        } else {
                            SetLength(meteorites, High(meteorites) + 2);
                            meteorites[High(meteorites)].X
                            :
                            = meteorites[n].X + (meteorites[n].Razmer + 10);
                            meteorites[High(meteorites)].Y =
                                meteorites[n].Y + (meteorites[n].Razmer + 10);
                            meteorites[High(meteorites)].Angle =
                                meteorites[n].Angle - 120;
                            meteorites[High(meteorites)].VX =
                                +cVmaxMet * cos(meteorites[High(meteorites)].Angle / 180 * Math.PI);
                            meteorites[High(meteorites)].VY =
                                -cVmaxMet * sin(meteorites[High(meteorites)].Angle / 180 * Math.PI);
                            meteorites[High(meteorites)].Razmer = meteorites[n].Razmer;
                            meteorites[High(meteorites)].TipRazmer =
                                meteorites[n].TipRazmer;
                            meteorites[High(meteorites)].Tip = meteorites[n].Tip;

                            SetLength(meteorites, High(meteorites) + 2);
                            meteorites[High(meteorites)].X =
                                meteorites[n].X - (meteorites[n].Razmer + 10);
                            meteorites[High(meteorites)].Y =
                                meteorites[n].Y - (meteorites[n].Razmer + 10);
                            meteorites[High(meteorites)].Angle =
                                meteorites[n].Angle - 240;
                            meteorites[High(meteorites)].VX =
                                +cVmaxMet * cos(meteorite[High(meteorites)].Angle / 180 * Math.PI);
                            meteorites[High(meteorites)].VY =
                                -cVmaxMet * sin(meteorite[High(meteorites)].Angle / 180 * Math.PI);
                            meteorites[High(meteorites)].Razmer = meteorites[n].Razmer;
                            meteorites[High(meteorites)].TipRazmer =
                                meteorites[n].TipRazmer;
                            meteorites[High(meteorites)].Tip = meteorites[n].Tip;
                        }
                        if (animationBulletDestroy.size == 0)
                            SetLength(animationBulletDestroy, 1)
                        else
                            SetLength
                        (animationBulletDestroy, High(animationBulletDestroy)+2);
                        animationBulletDestroy[animationBulletDestroy.size].X =
                            bullet[k].X
                        animationBulletDestroy[animationBulletDestroy.size].Y =
                            bullet[k].Y
                        animationBulletDestroy[animationBulletDestroy.size].Kadr = 0
                        for (z in k..bullet.size - 1)
                            bullet[z] = bullet[z + 1];
                        SetLength(bullet, High(bullet));
                        Nachk = k
                        goto m3;
                    }
        }


    for (n in 0..Players - 1)
        for (k in 0..meteorites.size)
            if (spaceship[n].InGame == true)
                if (((PreobX(spaceship[n].CX + 25) >= PreobX(meteorites[k].X - meteorites[k].Razmer)) &&
                            (PreobX(spaceship[n].CX + 25) <= PreobX(meteorites[k].X + meteorites[k].Razmer)) &&

                            (((PreobY(spaceship[n].CY + 25) >= PreobY(meteorites[k].Y - meteorites[k].Razmer)) &&
                                    (PreobY(spaceship[n].CY + 25) <= PreobY(meteorites[k].Y + meteorites[k].Razmer))) ||

                                    ((PreobY(spaceship[n].CY - 25) >= PreobY(meteorites[k].Y - meteorites[k].Razmer)) &&
                                            (PreobY(spaceship[n].CY - 25) <= PreobY(meteorites[k].Y + meteorites[k].Razmer))))
                            ) ||

                    (
                            (PreobX(spaceship[n].CX - 25) <= PreobX(meteorites[k].X + meteorites[k].Razmer)) &&
                                    (PreobX(spaceship[n].CX - 25) >= PreobX(
                                        meteorites[k].X - meteorites[k].Razmer
                                    )) &&

                                    (((PreobY(spaceship[n].CY + 25) >= PreobY(
                                        meteorites[k].Y - meteorites[k].Razmer
                                    )) &&
                                            (PreobY(spaceship[n].CY + 25) <= PreobY(
                                                meteorites[k].Y + meteorites[k].Razmer
                                            ))) ||

                                            ((PreobY(spaceship[n].CY - 25) >= PreobY(
                                                meteorites[k].Y - meteorites[k].Razmer
                                            )) &&
                                                    (PreobY(spaceship[n].CY - 25) <= PreobY(
                                                        meteorites[k].Y + meteorites[k].Razmer
                                                    ))))
                            )
                ) {
                    meteorites[k].Razmer = meteorites[k].Razmer - 5
                    meteorites[k].TipRazmer = meteorites[k].TipRazmer + 1
                    lifes[n] = lifes[n] - 1
                    spaceship[n].InGame = false
                    if (meteorites[k].TipRazmer > 3) {
                        for (z in k to meteorites.size - 1)
                            meteorites[z] = meteorites[z + 1]
                        SetLength(meteorites, High(meteorites))
                    } else {
                        SetLength(meteorites, High(meteorites) + 2)
                        meteorites[High(meteorites)].X = meteorites[k].X
                        meteorites[High(meteorites)].Y = meteorites[k].Y
                        meteorites[High(meteorites)].Angle =
                            meteorite[k].Angle - 90
                        meteorites[High(meteorites)].VX =
                            +cVmaxMet * cos(meteorites[High(meteorites)].Angle / 180 * Math.PI)
                        meteorites[High(meteorites)].VY =
                            -cVmaxMet * sin(meteorites[High(meteorites)].Angle / 180 * Math.PI)
                        meteorites[High(meteorites)].Razmer =
                            meteorite[k].Razmer
                        meteorites[High(meteorites)].TipRazmer =
                            meteorite[k].TipRazmer
                        meteorites[High(meteorites)].Tip = meteorites[k].Tip

                        SetLength(meteorites, High(meteorites) + 2)
                        meteorites[High(meteorites)].X = meteorites[k].X
                        meteorites[High(meteorites)].Y = meteorites[k].Y
                        meteorites[High(meteorites)].Angle =
                            meteorite[k].Angle - 180
                        meteorites[High(meteorites)].VX =
                            +cVmaxMet * cos(meteorites[High(meteorites)].Angle / 180 * Math.PI)
                        meteorites[High(meteorites)].VY =
                            -cVmaxMet * sin(meteorites[High(meteorites)].Angle / 180 * Math.PI)
                        meteorites[High(meteorites)].Razmer =
                            meteorites[k].Razmer
                        meteorites[High(meteorites)].TipRazmer =
                            meteorites[k].TipRazmer
                        meteorites[High(meteorites)].Tip = meteorites[k].Tip
                    }
                    if (animationSpaceshipDestroy.size == 0)
                        SetLength(AnimKonecKor, 1)
                    else
                        SetLength(AnimKonecKor, High(AnimKonecKor) + 2)
                    animationSpaceshipDestroy[animationSpaceshipDestroy.size - 1].X =
                        spaceship[n].CX
                    animationSpaceshipDestroy[animationSpaceshipDestroy.size - 1].Y =
                        spaceship[n].CY
                    animationSpaceshipDestroy[animationSpaceshipDestroy.size - 1].Kadr = 0
                }
}


fun KadrKor() {
    var n: Int
    var k: Int
    var z: Int
    var Nom: Int
    var Fl: Boolean
    Fl: = False;
    for nom: = 0 to Players-1 do
    if lifes[Nom] >= 0 then
            Fl: = True;
    if Fl = False then
            begin
    newRound()
    Exit;
    end;
    Collision1();
    for nom: = 0 to Players-1 do
    if spaceship[Nom].InGame = True then
            begin
    spaceship[Nom].CY: = spaceship[Nom].CY+spaceship[Nom].VY;
    if spaceship[Nom].CY > Pole.Height then
            spaceship[Nom].CY: = 0;
    if spaceship[Nom].CY < 0 then
            spaceship[Nom].CY: = Pole.Height;
    spaceship[Nom].CX: = spaceship[Nom].CX+spaceship[Nom].VX;
    if spaceship[Nom].CX > Pole.Width then
            spaceship[Nom].CX: = 0;
    if spaceship[Nom].CX < 0 then
            spaceship[Nom].CX: = Pole.Width;
    end
    else
    begin
    if lifes[Nom] >= 0 then
            for k: = 0 to 3 do
    if Nom = k then
            begin
    for z: = 0 to 3 do
    begin
    Fl: = true;
    for n: = 0 to Players-1 do
    if n != k then
            if (respawn[z].X - 100 < spaceship[n].CX + 25) && (respawn[z].X + 100 > spaceship[n].CX + 25) &&
            ((respawn[z].Y - 100 < spaceship[n].CY + 25) && (respawn[z].Y + 100 > spaceship[n].CY + 25) ||
                    (respawn[z].Y - 100 < spaceship[n].CY - 25) && (respawn[z].Y + 100 > spaceship[n].CY - 25)) ||
            (respawn[z].X - 100 < spaceship[n].CX - 25) && (respawn[z].X + 100 > spaceship[n].CX - 25) &&
            ((respawn[z].Y - 100 < spaceship[n].CY + 25) && (respawn[z].Y + 100 > spaceship[n].CY + 25) ||
                    (respawn[z].Y - 100 < spaceship[n].CY - 25) && (respawn[z].Y + 100 > spaceship[n].CY - 25))
    then
    Fl: = False;
    for n: = 0 to High(bullet) do
    if n != nom then
            if (respawn[z].X - 100 < bullet[n].X + 2) && (respawn[z].X + 100 > bullet[n].X + 2) &&
            ((respawn[z].Y - 100 < bullet[n].Y + 2) && (respawn[z].Y + 100 > bullet[n].Y + 2) ||
                    (respawn[z].Y - 100 < bullet[n].Y - 2) && (respawn[z].Y + 100 > bullet[n].Y - 2)) ||
            (respawn[z].X - 100 < bullet[n].X - 2) && (respawn[z].X + 100 > bullet[n].X - 2) &&
            ((respawn[z].Y - 100 < bullet[n].Y + 2) && (respawn[z].Y + 100 > bullet[n].Y + 2) ||
                    (respawn[z].Y - 100 < bullet[n].Y - 2) && (respawn[z].Y + 100 > bullet[n].Y - 2))
    then
    Fl: = False;
    for n: = 0 to High(meteorite) do
    if n != nom then
            if ((respawn[z].X - 100 < meteorites[n].X + meteorites[n].Razmer) && (respawn[z].X + 100 > meteorites[n].X + meteorites[n].Razmer) &&
                ((respawn[z].Y - 100 < meteorites[n].Y + meteorites[n].Razmer) && (respawn[z].Y + 100 > meteorites[n].Y + meteorites[n].Razmer) ||
                        (respawn[z].Y - 100 < meteorites[n].Y - meteorites[n].Razmer) && (respawn[z].Y + 100 > meteorites[n].Y - meteorites[n].Razmer)) ||
                (respawn[z].X - 100 < meteorites[n].X - meteorites[n].Razmer) && (respawn[z].X + 100 > meteorites[n].X - meteorites[n].Razmer) &&
                ((respawn[z].Y - 100 < meteorites[n].Y + meteorites[n].Razmer) && (respawn[z].Y + 100 > meteorites[n].Y + meteorites[n].Razmer) ||
                        (respawn[z].Y - 100 < meteorites[n].Y - meteorites[n].Razmer) && (respawn[z].Y + 100 > meteorites[n].Y - meteorites[n].Razmer))
            )

                Fl = False;

    if (Fl == True) {
        spaceship[k].CX: = respawn[z].X;
        spaceship[k].CY: = respawn[z].Y;
        spaceship[k].Vx: = 0;
        spaceship[k].Vy: = 0;
        spaceship[k].Angle: = respawn[z].Angle;
        spaceship[k].InGame: = True;
        Break;
    }
    end;
    end;
    end;
}

fun KadrPul() {
    var n: Int
    var k: Int
    var nach: Int

    nach = 0
    if (bullet.isNotEmpty()) {
        n = nach
        for (n in 0..bullet.size) {
            if (n > bullet.size)
                return
            for (k in 0..5) {
                bullet[n].X += bullet[n].VX
                if (bullet[n].X > width)
                    bullet[n].X = 0.0
                if (bullet[n].X < 0)
                    bullet[n].X = width.toDouble()
                bullet[n].Y += bullet[n].VY
                if (bullet[n].Y > height)
                    bullet[n].Y = 0.0
                if (bullet[n].Y < 0)
                    bullet[n].Y = height.toDouble()
                bullet[n].Put += cVmaxPul
                Collision2()
                Collision3()
                if (bullet.isEmpty())
                    return
            }
        }

        var tempBullet: Array<Bullet> = emptyArray()
        for (n in 0..bullet.size - 1)
            if (bullet[n].Put <= 300)
                tempBullet += bullet[n]
        bullet = tempBullet
    }
}

fun KadrMet() {
    if (meteorites.isNotEmpty()) {
        for (n in meteorites.indices) {
            if (n > meteorites.size)
                return
            for (k in 0..2) {
                meteorites[n].X += meteorites[n].VX

                if (meteorites[n].X > width)
                    meteorites[n].X = 0.0

                if (meteorites[n].X < 0)
                    meteorites[n].X = width

                meteorites[n].Y += meteorites[n].VY

                if (meteorites[n].Y > height)
                    meteorites[n].Y = 0.0
                if (meteorites[n].Y < 0)
                    meteorites[n].Y = height
                Collision4()
                if (n > meteorites.size)
                    return
            }
        }
    } else {
        newRound()
    }
}

}