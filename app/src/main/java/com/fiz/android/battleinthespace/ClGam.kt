package com.fiz.android.battleinthespace

import kotlin.math.cos
import kotlin.math.sin

class ClGam() {
  var spaceships: MutableList<Spaceship> = mutableListOf()
  private var scores: MutableList<Int> = mutableListOf()
  var lifes: MutableList<Int> = mutableListOf()
  var bullets: MutableList<Bullet> = mutableListOf()
  var meteorites: MutableList<Meteorite> = mutableListOf()
  var background: MutableList<MutableList<Int>> = mutableListOf()
  var animationBulletDestroy: MutableList<AnimationBulletDestroy> = mutableListOf()
  var animationSpaceshipDestroy: MutableList<AnimationSpaceshipDestroy> = mutableListOf()
  private var respawn: MutableList<Respawn> = mutableListOf()
  private var round: Int = 1
  val width = 800
  val height = 600

  //Кол-во просковков на рисование одного кадра
  var Raz: Int = 0
  var Raz1: Int = 0

// matr:array[0..49, 0..49]of Byte;

  var Players: Int = 2

  init {
    respawn.add(
      Respawn(
        X = 50,
        Y = 50,
        Angle = 0
      )
    )
    respawn.add(
      Respawn(
        X = 750,
        Y = 50,
        Angle = 180
      )
    )
    respawn.add(
      Respawn(
        X = 50,
        Y = 530,
        Angle = 0
      )
    )
    respawn.add(
      Respawn(
        X = 750,
        Y = 530,
        Angle = 180
      )
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
    spaceships.clear()
    for (n in 0 until Players) {
      spaceships += Spaceship(
        CX = respawn[n].X.toDouble(),
        CY = respawn[n].Y.toDouble(),
        VX = 0.0,
        VY = 0.0,
        Angle = respawn[n].Angle.toDouble(),
        InGame = true
      )
    }

    scores.clear()
    for (n in 0 until Players) {
      scores += 0
    }

    lifes.clear()
    for (n in 0 until Players) {
      lifes += 2
    }

    meteorites.clear()
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

    background.clear()
    for (n in 0..30) {
      var row: MutableList<Int> = mutableListOf()
      for (k in 0..30)
        row += (0..7).shuffled().first()
      background += row
    }

    animationBulletDestroy.clear()
    animationSpaceshipDestroy.clear()
  }

  fun newRound() {
    round += 1

    spaceships.clear()
    for (n in 0 until Players) {
      spaceships += Spaceship(
        CX = respawn[n].X.toDouble(),
        CY = respawn[n].Y.toDouble(),
        VX = 0.0,
        VY = 0.0,
        Angle = respawn[n].Angle.toDouble(),
        InGame = true
      )
    }

    lifes.clear()
    for (n in 0 until Players) {
      lifes += 2
    }


    meteorites.clear()
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

    background.clear()
    for (n in 0..30) {
      var row: MutableList<Int> = mutableListOf()
      for (k in 0..30)
        row += (0..7).shuffled().first()
      background += row
    }
  }

  fun Up(Nom: Int) {
    if ((spaceships[Nom].Angle != 180.0) && (spaceships[Nom].Angle != 0.0))
      spaceships[Nom].VY -= 0.05 * sin(spaceships[Nom].Angle / 180 * Math.PI)

    if (spaceships[Nom].VY >= cVmaxKor)
      spaceships[Nom].VY = cVmaxKor.toDouble()

    if (spaceships[Nom].VY <= -cVmaxKor)
      spaceships[Nom].VY = (-cVmaxKor).toDouble()

    if ((spaceships[Nom].Angle != 90.0) && (spaceships[Nom].Angle != 270.0))
      spaceships[Nom].VX += 0.05 * cos(spaceships[Nom].Angle / 180 * Math.PI)

    if (spaceships[Nom].VX >= cVmaxKor)
      spaceships[Nom].VX = cVmaxKor.toDouble()

    if (spaceships[Nom].VX <= -cVmaxKor)
      spaceships[Nom].VX = (-cVmaxKor).toDouble()
  }

  fun Right(Nom: Int) {
    spaceships[Nom].Angle -= 5
    if (spaceships[Nom].Angle + 5 > 360)
      spaceships[Nom].Angle -= 360
  }

  fun Left(Nom: Int) {
    spaceships[Nom].Angle += 5
    if (spaceships[Nom].Angle + 5 < 0)
      spaceships[Nom].Angle += +360
  }

  fun Vystr(Nom: Int) {
    if (spaceships[Nom].InGame == true) {
      bullets += Bullet(
        X = spaceships[Nom].CX + 25 * cos(spaceships[Nom].Angle / 180 * Math.PI),
        Y = spaceships[Nom].CY - 25 * sin(spaceships[Nom].Angle / 180 * Math.PI),
        VX = +cVmaxPul * cos(spaceships[Nom].Angle / 180 * Math.PI),
        VY = -cVmaxPul * sin(spaceships[Nom].Angle / 180 * Math.PI),
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
      for (n in 0..(Players - 2))
        for (k in n + 1..(Players - 1))
          if (spaceships[n].InGame == true && spaceships[k].InGame == true)
            if (((PreobX(spaceships[n].CX + 25) >= PreobX(spaceships[k].CX - 25)) &&
                      (PreobX(spaceships[n].CX + 25) <= PreobX(spaceships[k].CX + 25)) &&

                      (((PreobY(spaceships[n].CY + 25) >= PreobY(spaceships[k].CY - 25)) &&
                              (PreobY(spaceships[n].CY + 25) <= PreobY(
                                spaceships[k].CY + 25
                              ))) ||

                              ((PreobY(spaceships[n].CY - 25) >= PreobY(
                                spaceships[k].CY - 25
                              )) &&
                                      (PreobY(spaceships[n].CY - 25) <= PreobY(
                                        spaceships[k].CY + 25
                                      ))))
                      ) ||

              ((PreobX(spaceships[n].CX - 25) <= PreobX(spaceships[k].CX + 25)) &&
                      (PreobX(spaceships[n].CX - 25) >= PreobX(spaceships[k].CX - 25)) &&

                      (((PreobY(spaceships[n].CY + 25) >= PreobY(spaceships[k].CY - 25)) &&
                              (PreobY(spaceships[n].CY + 25) <= PreobY(
                                spaceships[k].CY + 25
                              ))) ||

                              ((PreobY(spaceships[n].CY - 25) >= PreobY(
                                spaceships[k].CY - 25
                              )) &&
                                      (PreobY(spaceships[n].CY - 25) <= PreobY(
                                        spaceships[k].CY + 25
                                      )))))
            ) {
              VX1 = spaceships[n].VX
              VX2 = spaceships[k].VX
              VY1 = spaceships[n].VY
              VY2 = spaceships[k].VY
              if (((VX1 > 0) && (VX2 < 0)) || ((VX1 < 0) && (VX2 > 0))) {
                spaceships[n].VX = -VX1
                spaceships[k].VX = -VX2
              } else {
                spaceships[n].VX = (VX1 + VX2) / 2;
                spaceships[k].VX = (VX1 + VX2) / 2;
                if (Math.abs(VX1) > Math.abs(VX2))
                  spaceships[k].VX = 2 * spaceships[k].VX
                else
                  if (Math.abs(VX1) < Math.abs(VX2))
                    spaceships[n].VX = 2 * spaceships[n].VX
              }
              if (spaceships[n].VX > cVmaxKor)
                spaceships[n].VX = cVmaxKor.toDouble()
              if (spaceships[k].VX > cVmaxKor)
                spaceships[k].VX = cVmaxKor.toDouble()
              if (spaceships[n].VX < -cVmaxKor)
                spaceships[n].VX = -cVmaxKor.toDouble()
              if (spaceships[k].VX < -cVmaxKor)
                spaceships[k].VX = -cVmaxKor.toDouble()
              if ((VY1 > 0 && VY2 < 0) || (VY1 < 0 && VY2 > 0)) {
                spaceships[n].VY = -VY1
                spaceships[k].VY = -VY2
              } else {
                spaceships[n].VY = (VY1 + VY2) / 2
                spaceships[k].VY = (VY1 + VY2) / 2
                if (Math.abs(VY1) > Math.abs(VY2))
                  spaceships[k].VY *= 2
                else
                  if (Math.abs(VY1) < Math.abs(VY2))
                    spaceships[n].VY *= 2
              }
              if (spaceships[n].VY > cVmaxKor)
                spaceships[n].VY = cVmaxKor.toDouble()
              if (spaceships[k].VY > cVmaxKor)
                spaceships[k].VY = cVmaxKor.toDouble()
              if (spaceships[n].VY < -cVmaxKor)
                spaceships[n].VY = -cVmaxKor.toDouble()
              if (spaceships[k].VY < -cVmaxKor)
                spaceships[k].VY = -cVmaxKor.toDouble()
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
    val listBulletDestroy: MutableList<Bullet> = mutableListOf()
    if (bullets.size > 0) {
      for (n in 0..(Players - 1))
        if (spaceships[n].InGame == true)
          for (k in 0..(bullets.size - 1))
            if ((((bullets[k].X + 2 >= spaceships[n].CX - 25) && (bullets[k].X + 2 <= spaceships[n].CX + 25) &&
                      ((bullets[k].Y + 2 >= spaceships[n].CY - 25) && (bullets[k].Y + 2 <= spaceships[n].CY + 25) ||
                              (bullets[k].Y - 2 >= spaceships[n].CY - 25) && (bullets[k].Y - 2 <= spaceships[n].CY + 25))) ||
                      ((bullets[k].X - 2 >= spaceships[n].CX - 25) && (bullets[k].X - 2 <= spaceships[n].CX + 25) && (
                              (bullets[k].Y + 2 >= spaceships[n].CY - 25) && (bullets[k].Y + 2 <= spaceships[n].CY + 25) ||
                                      (bullets[k].Y - 2 >= spaceships[n].CY - 25) && (bullets[k].Y - 2 <= spaceships[n].CY + 25)))
                      ) &&
              (n != bullets[k].Nom)
            ) {
              VX1 = spaceships[n].VX;
              VX2 = bullets[k].VX;
              VY1 = spaceships[n].VY;
              VY2 = bullets[k].VY;
              if (((VX1 > 0) && (VX2 < 0)) || ((VX1 < 0) && (VX2 > 0)))
                spaceships[n].VX = -VX1
              else {
                spaceships[n].VX = (VX1 + VX2) / 2
                spaceships[n].VX = 2 * spaceships[n].VX
              }
              if (spaceships[n].VX > cVmaxKor)
                spaceships[n].VX = cVmaxKor.toDouble()
              if (spaceships[n].VX < -cVmaxKor)
                spaceships[n].VX = -cVmaxKor.toDouble()
              if (((VY1 > 0) && (VY2 < 0)) || ((VY1 < 0) && (VY2 > 0)))
                spaceships[n].VY = -VY1
              else {
                spaceships[n].VY = (VY1 + VY2) / 2
                spaceships[n].VY = 2 * spaceships[n].VY
              }
              if (spaceships[n].VY > cVmaxKor)
                spaceships[n].VY = cVmaxKor.toDouble()
              if (spaceships[n].VY < -cVmaxKor)
                spaceships[n].VY = (-cVmaxKor).toDouble()
              animationBulletDestroy.add(
                AnimationBulletDestroy(
                  X = bullets[k].X,
                  Y = bullets[k].Y,
                  Kadr = 0
                )
              )
              listBulletDestroy.add(bullets[k])
            }

      for (bullet in listBulletDestroy) {
        bullets.remove(bullet)
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
    val listBulletDestroy: MutableList<Bullet> = mutableListOf()

    for (n in 0..(bullets.size - 1))
      for (k in n + 1..bullets.size)
        if (((PreobX(bullets[n].X + 2) >= PreobX(bullets[k].X - 2)) &&
                  (PreobX(bullets[n].X + 2) <= PreobX(bullets[k].X + 2)) &&

                  (((PreobY(bullets[n].Y + 2) >= PreobY(bullets[k].Y - 2)) &&
                          (PreobY(bullets[n].Y + 2) <= PreobY(bullets[k].Y + 2))) ||

                          ((PreobY(bullets[n].Y - 2) >= PreobY(bullets[k].Y - 2)) &&
                                  (PreobY(bullets[n].Y - 2) <= PreobY(bullets[k].Y + 2))))
                  ) ||

          ((PreobX(bullets[n].X - 2) <= PreobX(bullets[k].X + 2)) &&
                  (PreobX(bullets[n].X - 2) >= PreobX(bullets[k].X - 2)) &&

                  (((PreobY(bullets[n].Y + 2) >= PreobY(bullets[k].Y - 2)) &&
                          (PreobY(bullets[n].Y + 2) <= PreobY(bullets[k].Y + 2))) ||

                          ((PreobY(bullets[n].Y - 2) >= PreobY(bullets[k].Y - 2)) &&
                                  (PreobY(bullets[n].Y - 2) <= PreobY(bullets[k].Y + 2)))))
        ) {
          animationBulletDestroy.add(
            AnimationBulletDestroy(
              X = bullets[k].X,
              Y = bullets[k].Y,
              Kadr = 0
            )
          )

          listBulletDestroy.add(bullets[k])
        }

    for (bullet in listBulletDestroy) {
      bullets.remove(bullet)
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

    for (n in 0..(meteorites.size - 1))
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
    val listBulletDestroy: MutableList<Bullet> = mutableListOf()
    val listMeteoritesDestroy: MutableList<Meteorite> = mutableListOf()
    if (bullets.size >= 0)
      if (meteorites.size >= 0) {
        for (n in Nachn..meteorites.size)
          for (k in Nach..bullets.size)
            if (((bullets[k].X + 2 >= meteorites[n].X - meteorites[n].Razmer) && (bullets[k].X + 2 <= meteorites[n].X + meteorites[n].Razmer) && (
                      (bullets[k].Y + 2 >= meteorites[n].Y - meteorites[n].Razmer) && (bullets[k].Y + 2 <= meteorites[n].Y + meteorites[n].Razmer) ||
                              (bullets[k].Y - 2 >= meteorites[n].Y - meteorites[n].Razmer) && (bullets[k].Y - 2 <= meteorites[n].Y + meteorites[n].Razmer))
                      ) ||
              (
                      (bullets[k].X - 2 >= meteorites[n].X - meteorites[n].Razmer) && (bullets[k].X - 2 <= meteorites[n].X + meteorites[n].Razmer) && (
                              (bullets[k].Y + 2 >= meteorites[n].Y - meteorites[n].Razmer) && (bullets[k].Y + 2 <= meteorites[n].Y + meteorites[n].Razmer) ||
                                      (bullets[k].Y - 2 >= meteorites[n].Y - meteorites[n].Razmer) && (bullets[k].Y - 2 <= meteorites[n].Y + meteorites[n].Razmer))
                      )
            ) {
              meteorites[n].Razmer = meteorites[n].Razmer - 5;
              meteorites[n].TipRazmer = meteorites[n].TipRazmer + 1;
              scores[bullets[k].Nom] =
                scores[bullets[k].Nom] + 100 * meteorites[n].TipRazmer;
              if (meteorites[n].TipRazmer > 3) {
                listMeteoritesDestroy.add(meteorites[n])

                Nachn = n

                animationBulletDestroy.add(
                  AnimationBulletDestroy(
                    X = bullets[k].X,
                    Y = bullets[k].Y,
                    Kadr = 0
                  )
                )
                listBulletDestroy.add(bullets[k])
              } else {

                meteorites.add(
                  Meteorite(
                    X = meteorites[n].X + (meteorites[n].Razmer + 10),
                    Y = meteorites[n].Y + (meteorites[n].Razmer + 10),
                    Angle = meteorites[n].Angle - 120,
                    VX = +cVmaxMet * cos(meteorites[meteorites.lastIndex].Angle / 180 * Math.PI),
                    VY = -cVmaxMet * sin(meteorites[meteorites.lastIndex].Angle / 180 * Math.PI),
                    Razmer = meteorites[n].Razmer,
                    TipRazmer = meteorites[n].TipRazmer,
                    Tip = meteorites[n].Tip,
                  )
                )

                meteorites.add(
                  Meteorite(
                    X = meteorites[n].X + (meteorites[n].Razmer + 10),
                    Y = meteorites[n].Y + (meteorites[n].Razmer + 10),
                    Angle = meteorites[n].Angle - 240,
                    VX = +cVmaxMet * cos(meteorites[meteorites.lastIndex].Angle / 180 * Math.PI),
                    VY = -cVmaxMet * sin(meteorites[meteorites.lastIndex].Angle / 180 * Math.PI),
                    Razmer = meteorites[n].Razmer,
                    TipRazmer = meteorites[n].TipRazmer,
                    Tip = meteorites[n].Tip,
                  )
                )

              }
              animationBulletDestroy.add(
                AnimationBulletDestroy(
                  X = bullets[k].X,
                  Y = bullets[k].Y,
                  Kadr = 0
                )
              )
              listBulletDestroy.add(bullets[k])
            }
      }
    for (bullet in listBulletDestroy) {
      bullets.remove(bullet)
    }

    for (n in 0..(Players - 1))
      for (k in 0..meteorites.size)
        if (spaceships[n].InGame == true)
          if (((PreobX(spaceships[n].CX + 25) >= PreobX(meteorites[k].X - meteorites[k].Razmer)) &&
                    (PreobX(spaceships[n].CX + 25) <= PreobX(meteorites[k].X + meteorites[k].Razmer)) &&

                    (((PreobY(spaceships[n].CY + 25) >= PreobY(meteorites[k].Y - meteorites[k].Razmer)) &&
                            (PreobY(spaceships[n].CY + 25) <= PreobY(meteorites[k].Y + meteorites[k].Razmer))) ||

                            ((PreobY(spaceships[n].CY - 25) >= PreobY(meteorites[k].Y - meteorites[k].Razmer)) &&
                                    (PreobY(spaceships[n].CY - 25) <= PreobY(meteorites[k].Y + meteorites[k].Razmer))))
                    ) ||

            (
                    (PreobX(spaceships[n].CX - 25) <= PreobX(meteorites[k].X + meteorites[k].Razmer)) &&
                            (PreobX(spaceships[n].CX - 25) >= PreobX(
                              meteorites[k].X - meteorites[k].Razmer
                            )) &&

                            (((PreobY(spaceships[n].CY + 25) >= PreobY(
                              meteorites[k].Y - meteorites[k].Razmer
                            )) &&
                                    (PreobY(spaceships[n].CY + 25) <= PreobY(
                                      meteorites[k].Y + meteorites[k].Razmer
                                    ))) ||

                                    ((PreobY(spaceships[n].CY - 25) >= PreobY(
                                      meteorites[k].Y - meteorites[k].Razmer
                                    )) &&
                                            (PreobY(spaceships[n].CY - 25) <= PreobY(
                                              meteorites[k].Y + meteorites[k].Razmer
                                            ))))
                    )
          ) {
            meteorites[k].Razmer = meteorites[k].Razmer - 5
            meteorites[k].TipRazmer = meteorites[k].TipRazmer + 1
            lifes[n] = lifes[n] - 1
            spaceships[n].InGame = false
            if (meteorites[k].TipRazmer > 3) {
              listMeteoritesDestroy.add(meteorites[k])
            } else {

              meteorites.add(
                Meteorite(
                  X = meteorites[k].X,
                  Y = meteorites[k].Y,
                  Angle = meteorites[k].Angle - 90,
                  VX = +cVmaxMet * cos(meteorites[meteorites.lastIndex].Angle / 180 * Math.PI),
                  VY = -cVmaxMet * sin(meteorites[meteorites.lastIndex].Angle / 180 * Math.PI),
                  Razmer = meteorites[k].Razmer,
                  TipRazmer = meteorites[k].TipRazmer,
                  Tip = meteorites[k].Tip,
                )
              )

              meteorites.add(
                Meteorite(
                  X = meteorites[k].X,
                  Y = meteorites[k].Y,
                  Angle = meteorites[k].Angle - 180,
                  VX = +cVmaxMet * cos(meteorites[meteorites.lastIndex].Angle / 180 * Math.PI),
                  VY = -cVmaxMet * sin(meteorites[meteorites.lastIndex].Angle / 180 * Math.PI),
                  Razmer = meteorites[k].Razmer,
                  TipRazmer = meteorites[k].TipRazmer,
                  Tip = meteorites[k].Tip,
                )
              )

            }

            animationSpaceshipDestroy.add(
              AnimationSpaceshipDestroy(
                X = spaceships[n].CX,
                Y = spaceships[n].CY,
                Kadr = 0,
              )
            )
          }

    for (meteorite in listMeteoritesDestroy) {
      meteorites.remove(meteorite)
    }
  }

  fun KadrKor() {
    var n: Int
    var k: Int
    var z: Int
    var nom: Int
    var Fl: Boolean
    Fl = false
    for (nom in 0..(Players - 1))
      if (lifes[nom] >= 0)
        Fl = true
    if (Fl == false) {
      newRound()
      return
    }
    Collision1();
    for (nom in 0..(Players - 1))
      if (spaceships[nom].InGame == true) {
        spaceships[nom].CY += spaceships[nom].VY
        if (spaceships[nom].CY > height)
          spaceships[nom].CY = 0.0
        if (spaceships[nom].CY < 0)
          spaceships[nom].CY = height.toDouble()
        spaceships[nom].CX = spaceships[nom].CX + spaceships[nom].VX
        if (spaceships[nom].CX > width)
          spaceships[nom].CX = 0.0
        if (spaceships[nom].CX < 0)
          spaceships[nom].CX = width.toDouble()
      } else {
        if (lifes[nom] >= 0)
          for (k in 0..3)
            if (nom == k) {
              for (z in 0..3) {
                Fl = true
                for (n in 0..(Players - 1))
                  if (n != k)
                    if ((respawn[z].X - 100 < spaceships[n].CX + 25) && (respawn[z].X + 100 > spaceships[n].CX + 25) &&
                      ((respawn[z].Y - 100 < spaceships[n].CY + 25) && (respawn[z].Y + 100 > spaceships[n].CY + 25) ||
                              (respawn[z].Y - 100 < spaceships[n].CY - 25) && (respawn[z].Y + 100 > spaceships[n].CY - 25)) ||
                      (respawn[z].X - 100 < spaceships[n].CX - 25) && (respawn[z].X + 100 > spaceships[n].CX - 25) &&
                      ((respawn[z].Y - 100 < spaceships[n].CY + 25) && (respawn[z].Y + 100 > spaceships[n].CY + 25) ||
                              (respawn[z].Y - 100 < spaceships[n].CY - 25) && (respawn[z].Y + 100 > spaceships[n].CY - 25))
                    )
                      Fl = false
                for (n in 0..(bullets.size-1))
                  if (n != nom)
                    if ((respawn[z].X - 100 < bullets[n].X + 2) && (respawn[z].X + 100 > bullets[n].X + 2) &&
                      ((respawn[z].Y - 100 < bullets[n].Y + 2) && (respawn[z].Y + 100 > bullets[n].Y + 2) ||
                              (respawn[z].Y - 100 < bullets[n].Y - 2) && (respawn[z].Y + 100 > bullets[n].Y - 2)) ||
                      (respawn[z].X - 100 < bullets[n].X - 2) && (respawn[z].X + 100 > bullets[n].X - 2) &&
                      ((respawn[z].Y - 100 < bullets[n].Y + 2) && (respawn[z].Y + 100 > bullets[n].Y + 2) ||
                              (respawn[z].Y - 100 < bullets[n].Y - 2) && (respawn[z].Y + 100 > bullets[n].Y - 2))
                    )
                      Fl = false
                for (n in 0..(meteorites.size-1))
                  if (n != nom)
                    if ((respawn[z].X - 100 < meteorites[n].X + meteorites[n].Razmer) && (respawn[z].X + 100 > meteorites[n].X + meteorites[n].Razmer) &&
                      ((respawn[z].Y - 100 < meteorites[n].Y + meteorites[n].Razmer) && (respawn[z].Y + 100 > meteorites[n].Y + meteorites[n].Razmer) ||
                              (respawn[z].Y - 100 < meteorites[n].Y - meteorites[n].Razmer) && (respawn[z].Y + 100 > meteorites[n].Y - meteorites[n].Razmer)) ||
                      (respawn[z].X - 100 < meteorites[n].X - meteorites[n].Razmer) && (respawn[z].X + 100 > meteorites[n].X - meteorites[n].Razmer) &&
                      ((respawn[z].Y - 100 < meteorites[n].Y + meteorites[n].Razmer) && (respawn[z].Y + 100 > meteorites[n].Y + meteorites[n].Razmer) ||
                              (respawn[z].Y - 100 < meteorites[n].Y - meteorites[n].Razmer) && (respawn[z].Y + 100 > meteorites[n].Y - meteorites[n].Razmer))
                    )

                      Fl = false

                if (Fl == true) {
                  spaceships[k].CX = respawn[z].X.toDouble()
                  spaceships[k].CY = respawn[z].Y.toDouble()
                  spaceships[k].VX = 0.0
                  spaceships[k].VY = 0.0
                  spaceships[k].Angle = respawn[z].Angle.toDouble()
                  spaceships[k].InGame = true
                  break
                }
              }
            }
      }
  }

  fun KadrPul() {
    var n: Int
    var k: Int
    var nach: Int

    nach = 0
    if (bullets.isNotEmpty()) {
      n = nach
      for (n in 0..(bullets.size-1)) {
        if (n > bullets.size)
          return
        for (k in 0..5) {
          bullets[n].X += bullets[n].VX
          if (bullets[n].X > width)
            bullets[n].X = 0.0
          if (bullets[n].X < 0)
            bullets[n].X = width.toDouble()
          bullets[n].Y += bullets[n].VY
          if (bullets[n].Y > height)
            bullets[n].Y = 0.0
          if (bullets[n].Y < 0)
            bullets[n].Y = height.toDouble()
          bullets[n].Put += cVmaxPul
          Collision2()
          Collision3()
          if (bullets.isEmpty())
            return
        }
      }

      var tempBullet: MutableList<Bullet> = mutableListOf()
      for (n in 0..(bullets.size - 1))
        if (bullets[n].Put <= 300)
          tempBullet += bullets[n]
      bullets = tempBullet
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
            meteorites[n].X = width.toDouble()

          meteorites[n].Y += meteorites[n].VY

          if (meteorites[n].Y > height)
            meteorites[n].Y = 0.0
          if (meteorites[n].Y < 0)
            meteorites[n].Y = height.toDouble()
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