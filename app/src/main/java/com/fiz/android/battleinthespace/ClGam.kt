package com.fiz.android.battleinthespace

import android.graphics.Bitmap
import android.widget.ProgressBar
import com.fiz.tetriswithlife.*

private class ClGam(Im: Bitmap, inf: ProgressBar) {
  //Корабли
  private var Kor: Array<RecKor> = emptyArray()

  //Счет
  private var Chet: Array<Int> = emptyArray()

  //Жизни на раунд
  private var Gizn: Array<Int> = emptyArray()

  //Пули
  private var Pul: Array<RecPul> = emptyArray()

  //Ссылка на Поле где рисовать
  private var Pole: Bitmap = Im
  private var Info: ProgressBar = inf

  //Ссылка на картинки
  private var bmp: recBmp = recBmp()

  //Взрыв пули
  private var AnimKonecPul: Array<recAnimKonecPul>
  private var AnimKonecKor: Array<recAnimKonecKor>
  private var Respaun: Array<RecRespaun>//[0..3]
  private var raund: Int

  //Фон
  private var Fon: Array<Array<Int>>//[0..30,0..30]

  //Метеориты
  private var met: Array<recMet>

  //Кол-во просковков на рисование одного кадра
  private var Raz: Int
  private var Raz1: Int
  //Преобразует X с учетом координат
// matr:array[0..49, 0..49]of Byte;

  //Преобразует X с учетом координат
  fun PreobX(Index: Double): Double {
    var result = Index
    if (Index > Pole.width + 100)
      result = Index - Pole.width
    if (Index < 0)
      result = Index + Pole.width
    return result
  }

  //Преобразует Y с учетом координат
  fun PreobY(Index: Double): Double {
    var result = Index
    if (Index > Pole.height)
      result = Index - Pole.height
    if (Index < 0)
      result = Index + Pole.height
    return result
  }

  var Players: Int = 2

  init {
    var n: Int
    var k: Int
    var Vrbmp: Bitmap

    Pole: = Im;
    Info: = Inf;

    for (n in 0..8)
      bmp.Fon[n]: = TBitmap.Create;

    for (n in 0..3) {
      bmp.Kor[n]: = TBitmap.Create
      bmp.KorGizn[n]: = TBitmap.Create
    }

    for (n in 0..4) {
      bmp.KonecKor[n]: = TBitmap.Create;
      bmp.KonecKor[n].Width: = 50;
      bmp.KonecKor[n].Height: = 50;

      bmp.KonecKorMask[n]: = TBitmap.Create;
      bmp.KonecKorMask[n].Width: = 50;
      bmp.KonecKorMask[n].Height: = 50;
    }

    bmp.KorMask: = TBitmap.Create;
    bmp.KorGiznMask: = TBitmap.Create;
    bmp.Pul: = TBitmap.Create;
    bmp.PulMask: = TBitmap.Create;

    for (n in 0..2) {
      bmp.KonecPul[n]: = TBitmap.Create;
      bmp.KonecPul[n].Width: = 5;
      bmp.KonecPul[n].Height: = 5;
      bmp.KonecPulMask[n]: = TBitmap.Create;
      bmp.KonecPulMask[n].Width: = 5;
      bmp.KonecPulMask[n].Height: = 5;
    }

    for (n in 0..5) do {
      for (k in 0..4) do
        bmp.Met[n, k]: = TBitmap.Create
      bmp.MetMask[n]: = TBitmap.Create
    }

    Vrbmp: = TBitmap.Create;

    for (n in 0..7)
      bmp.Fon[n].LoadFromFile('Fon' + IntToStr(n + 1) + '.bmp');

    for (n in 0..3) {
      bmp.Kor[n].LoadFromFile('Kor' + IntToStr(n + 1) + '.bmp');
      bmp.KorGizn[n].LoadFromFile('KorGizn' + IntToStr(n + 1) + '.bmp');
    }

    Vrbmp.LoadFromFile('KonecKor.bmp');

    for n: = 0 to 4 do
    BitBlt(bmp.KonecKor[n].Canvas.Handle, 0, 0, 50, 50, Vrbmp.Canvas.Handle, n * 50, 0, srcCOPY);

    Vrbmp.LoadFromFile('KonecKorMask.bmp');

    for n: = 0 to 4 do
    BitBlt(
      bmp.KonecKorMask[n].Canvas.Handle,
      0,
      0,
      50,
      50,
      Vrbmp.Canvas.Handle,
      n * 50,
      0,
      srcCOPY
    );

    bmp.KorMask.LoadFromFile('KorMask.bmp');
    bmp.KorGiznMask.LoadFromFile('KorGiznMask.bmp');
    bmp.Pul.LoadFromFile('Pul.bmp');
    bmp.PulMask.LoadFromFile('PulMask.bmp');
    Vrbmp.LoadFromFile('KonecPul.bmp');

    for n: = 0 to 2 do
    BitBlt(bmp.KonecPul[n].Canvas.Handle, 0, 0, 5, 5, Vrbmp.Canvas.Handle, n * 5, 0, srcCOPY);
    Vrbmp.LoadFromFile('KonecPulMask.bmp');

    for n: = 0 to 2 do
    BitBlt(bmp.KonecPulMask[n].Canvas.Handle, 0, 0, 5, 5, Vrbmp.Canvas.Handle, n * 5, 0, srcCOPY);

    for n: = 0 to 3 do
    {
      bmp.MetMask[n].LoadFromFile('MetMask' + IntToStr(n) + '.bmp');
      for k: = 0 to 1 do
      bmp.Met[n, k].LoadFromFile('Met' + IntToStr(n) + IntToStr(k) + '.bmp');
    }

    Respaun[0].X: = 50;
    Respaun[0].Y: = 50;
    Respaun[0].Angle: = 0;
    Respaun[1].X: = 750;
    Respaun[1].Y: = 50;
    Respaun[1].Angle: = 180;
    Respaun[2].X: = 50;
    Respaun[2].Y: = 530;
    Respaun[2].Angle: = 0;
    Respaun[3].X: = 750;
    Respaun[3].Y: = 530;
    Respaun[3].Angle: = 180;
//    for n: = 0 to 49 do
//    for k: = 0 to 49 do
//    if bmp.KorMask.Canvas.Pixels[n, k] = clWhite then
//            Matr[n, k]: = 1
//    else
//    Matr[n, k]: = 0;

  }

  fun NewGame() {
    SetLength(Kor, Players)
    SetLength(Chet, Players)
    SetLength(Gizn, Players)

    for (n in 1..4)
      if (Players >= n) {
        Kor[n - 1].CX = Respaun[n - 1].X
        Kor[n - 1].CY = Respaun[n - 1].Y
        Kor[n - 1].Vx = 0
        Kor[n - 1].Vy = 0
        Kor[n - 1].Angle = Respaun[n - 1].Angle
        Kor[n - 1].InGame = True
        Chet[n - 1] = 0
        Gizn[n - 1] = 2
      }

    SetLength(met, 1)
    Randomize

    met[0].X: = 400
    met[0].Y: = 300
    met[0].Angle: = Random(361)
    met[0].VX: = +cVmaxMet*cos(Met[0].Angle/180*pi)
    met[0].VY: = -cVmaxMet*sin(Met[0].Angle/180*pi)
    met[0].razmer: = 25
    met[0].Tiprazmer: = 0
    met[0].Tip: = 0
    raund: = 1

    for (n in 0..30)
      for (k in 0..30)
        Fon[n, k]: = Random(8)

    AnimKonecPul: = null
    AnimKonecKor: = null
  }

  fun NewRaund() {
    raund: = Raund+1;

    SetLength(Kor, Players);
    SetLength(Chet, Players);
    SetLength(Gizn, Players);

    for (n in 1..4)
      if (Players >= n) {
        Kor[n - 1].CX = Respaun[n - 1].X
        Kor[n - 1].CY = Respaun[n - 1].Y
        Kor[n - 1].Vx = 0
        Kor[n - 1].Vy = 0
        Kor[n - 1].Angle = Respaun[n - 1].Angle
        Kor[n - 1].InGame = True
        Gizn[n - 1] = 2
      }

    SetLength(met, 2)
    Randomize
    met[0].X = 400
    met[0].Y = 300
    met[0].Angle = Random(361)
    met[0].VX = +cVmaxMet * cos(Met[0].Angle / 180 * pi)
    met[0].VY = -cVmaxMet * sin(Met[0].Angle / 180 * pi)
    met[0].razmer = 25
    met[0].Tiprazmer = 0
    met[0].Tip = raund - 1
    met[1].X = 350
    met[1].Y = 150
    met[1].Angle = Random(361)
    met[1].VX = +cVmaxMet * cos(Met[1].Angle / 180 * pi)
    met[1].VY = -cVmaxMet * sin(Met[1].Angle / 180 * pi)
    met[1].razmer = 25
    met[1].Tiprazmer = 0
    met[1].Tip = raund - 1

//    met[2].X: = 550;
//    met[2].Y: = 150;
//    met[2].Angle: = Random(361);
//    met[2].VX: = +cVmaxMet*cos(Met[2].Angle/180*pi);
//    met[2].VY: = -cVmaxMet*sin(Met[2].Angle/180*pi);
//    met[2].razmer: = 25;
//    met[2].Tiprazmer: = 0;
//    met[2].Tip: = raund;

    for (n in 0..30)
      for (k in 0..30)
        Fon[n, k]: = Random(8)
  }

  fun Obnov() {
    procedure ClGam . Obnov;
    procedure Ust (Ugol, X, Y:Single);
    var
            fi: Single;
    t:TXFORM;
    begin
    fi: = Ugol/180*pi;
    t.eM11: = cos(fi);
    t.eM12: = -sin(fi);
    t.eM21: = sin(fi);
    t.eM22: = cos(fi);
    t.eDx: = X;
    t.eDy: = Y;
    SetWorldTransform(Pole.Canvas.Handle, t);
    end;
    procedure Risov (Ugol, X, Y:Single;Nom:Integer);
    begin
    Ust(Ugol, x, y);
    BitBlt(Pole.Canvas.Handle, -25, -25, 50, 50, bmp.KorMask.Canvas.Handle, 0, 0, SrcPaint);
    BitBlt(Pole.Canvas.Handle, -25, -25, 50, 50, bmp.Kor[Nom].Canvas.Handle, 0, 0, SrcAnd);
    Ust(0, 0, 0);
    end;
    label
    m1, m2;
    var
    n, k, nom, nach:Integer;
    begin
    for n: = 0 to Pole.Width div 50 do
    for k: = 0 to Pole.Height div 50 do
    BitBlt(
      Pole.Canvas.Handle,
      n * 50,
      k * 50,
      50,
      50,
      bmp.Fon[Fon[n, k]].Canvas.Handle,
      0,
      0,
      SrcCopy
    );
    SetGraphicsMode(Pole.Canvas.Handle, GM_ADVANCED);
    for nom: = 0 to Players-1 do
    if Kor[Nom].InGame = True then
            begin
    Risov(Kor[Nom].Angle, Kor[Nom].CX, Kor[Nom].CY, Nom);
    if Kor[Nom].CY + 50 >= Pole.Height then
            Risov(Kor[Nom].Angle, Kor[Nom].CX, Kor[Nom].CY - Pole.Height, nom);
    if Kor[Nom].CX + 50 >= Pole.Width then
            Risov(Kor[Nom].Angle, Kor[Nom].CX - Pole.Width, Kor[Nom].CY, nom);
    if (Kor[Nom].CX + 50 >= Pole.Width) and(Kor[Nom].CY + 50 >= Pole.Height) then
            Risov(Kor[Nom].Angle, Kor[Nom].CX - Pole.Width, Kor[Nom].CY - Pole.Height, nom);
    if Kor[Nom].CX - 50 <= 0 then
            Risov(Kor[Nom].Angle, Kor[Nom].CX + Pole.Width, Kor[Nom].CY, nom);
    if Kor[Nom].CY - 50 <= 0 then
            Risov(Kor[Nom].Angle, Kor[Nom].CX, Kor[Nom].CY + Pole.Height, nom);
    if (Kor[Nom].CX - 50 <= 0) and(Kor[Nom].CY - 50 <= 0) then
            Risov(Kor[Nom].Angle, Kor[Nom].CX + Pole.Width, Kor[Nom].CY + Pole.Height, nom);
    end;
    if High(Pul) >= 0 then
            for n: = 0 to High(Pul) do
    begin
    BitBlt(
      Pole.Canvas.Handle,
      Trunc(Pul[n].X),
      Trunc(Pul[n].Y),
      5,
      5,
      bmp.PulMask.Canvas.Handle,
      0,
      0,
      SrcPaint
    );
    BitBlt(
      Pole.Canvas.Handle,
      Trunc(Pul[n].X),
      Trunc(Pul[n].Y),
      5,
      5,
      bmp.Pul.Canvas.Handle,
      0,
      0,
      SrcAnd
    );
    end;

    Nach: = 0;
    m1:
    for n: = nach to High(AnimKonecPul) do
    begin
    k: = AnimKonecPul[n].Kadr;
    if AnimKonecPul[n].Kadr = 3 then
            k: = 1;
    if AnimKonecPul[n].Kadr = 4 then
            k: = 0;
    BitBlt(
      Pole.Canvas.Handle,
      Trunc(AnimKonecPul[n].X - 2),
      Trunc(AnimKonecPul[n].Y - 2),
      5,
      5,
      bmp.KonecPulMask[k].Canvas.Handle,
      0,
      0,
      SrcPaint
    );
    BitBlt(
      Pole.Canvas.Handle,
      Trunc(AnimKonecPul[n].X - 2),
      Trunc(AnimKonecPul[n].Y - 2),
      5,
      5,
      bmp.KonecPul[k].Canvas.Handle,
      0,
      0,
      SrcAnd
    );
    if Raz = 0 then
            AnimKonecPul[n].Kadr: = AnimKonecPul[n].kadr+1;
    Raz: = Raz+1;
    if Raz > 5 then
            Raz: = 0;
    if AnimKonecPul[n].Kadr > 4 then
            begin
    for k: = n to High(AnimKonecPul)-1 do
    AnimKonecPul[k]: = AnimKonecPul[k+1];
    nach: = n;
    SetLength(AnimKonecPul, High(AnimKonecPul));
    if n > High(AnimKonecPul) then
            break;
    goto m1;
    end;
    end;

    if High(met) >= 0 then
            for n: = 0 to High(met) do
    begin
    BitBlt(
      Pole.Canvas.Handle,
      Trunc(Met[n].X),
      Trunc(Met[n].Y),
      Met[n].Razmer * 2,
      Met[n].Razmer * 2,
      bmp.metMask[Met[n].TipRazmer].Canvas.Handle,
      0,
      0,
      SrcPaint
    );
    BitBlt(
      Pole.Canvas.Handle,
      Trunc(Met[n].X),
      Trunc(Met[n].Y),
      Met[n].Razmer * 2,
      Met[n].Razmer * 2,
      bmp.met[Met[n].TipRazmer, Met[n].Tip].Canvas.Handle,
      0,
      0,
      SrcAnd
    );
    {
      BitBlt(
        Pole.Canvas.Handle,
        Trunc(Met[n].X) - Pole.Width,
        Trunc(Met[n].Y) - Pole.Height,
        50,
        50,
        bmp.metMask[Met[n].TipRazmer].Canvas.Handle,
        0,
        0,
        SrcPaint
      );
      BitBlt(
        Pole.Canvas.Handle,
        Trunc(Met[n].X) - Pole.Width,
        Trunc(Met[n].Y) - Pole.Height,
        50,
        50,
        bmp.met[Met[n].TipRazmer, Met[n].Tip].Canvas.Handle,
        0,
        0,
        SrcAnd
      );
      BitBlt(
        Pole.Canvas.Handle,
        Trunc(Met[n].X) + Pole.Width,
        Trunc(Met[n].Y) + Pole.Height,
        50,
        50,
        bmp.metMask[Met[n].TipRazmer].Canvas.Handle,
        0,
        0,
        SrcPaint
      );
      BitBlt(
        Pole.Canvas.Handle,
        Trunc(Met[n].X) + Pole.Width,
        Trunc(Met[n].Y) + Pole.Height,
        50,
        50,
        bmp.met[Met[n].TipRazmer, Met[n].Tip].Canvas.Handle,
        0,
        0,
        SrcAnd
      );
    } end;

    Pole.Canvas.Font.Size: = 12;
    Pole.Canvas.Brush.Style: = bsClear;
    Pole.Canvas.Font.Style: = [fsBold];
    if Players >= 1 then
            begin
    Info.Panels[0].Text: = '����� 1';
    Info.Panels[1].Text: = IntToStr(Chet[0]);
    Pole.Canvas.Font.Color: = clGreen;
    Pole.Canvas.TextOut(30, 30, '����� 1');
    for n: = 0 to Gizn[0] do
    begin
    BitBlt(
      Pole.Canvas.Handle,
      30 + 25 * n,
      45,
      20,
      20,
      bmp.KorGiznMask.Canvas.Handle,
      0,
      0,
      SrcPaint
    );
    BitBlt(Pole.Canvas.Handle, 30 + 25 * n, 45, 20, 20, bmp.KorGizn[0].Canvas.Handle, 0, 0, SrcAnd);
    end;
    end;
    if Players >= 2 then
            begin
    Info.Panels[2].Text: = '����� 2';
    Info.Panels[3].Text: = IntToStr(Chet[1]);
    Pole.Canvas.Font.Color: = clAqua;
    Pole.Canvas.TextOut(700, 30, '����� 2');
    for n: = 0 to Gizn[1] do
    begin
    BitBlt(
      Pole.Canvas.Handle,
      700 + 25 * n,
      45,
      20,
      20,
      bmp.KorGiznMask.Canvas.Handle,
      0,
      0,
      SrcPaint
    );
    BitBlt(
      Pole.Canvas.Handle,
      700 + 25 * n,
      45,
      20,
      20,
      bmp.KorGizn[1].Canvas.Handle,
      0,
      0,
      SrcAnd
    );
    end;
    end;
    if Players >= 3 then
            begin
    Info.Panels[4].Text: = '����� 3';
    Info.Panels[5].Text: = IntToStr(Chet[2]);
    Pole.Canvas.Font.Color: = clYellow;
    Pole.Canvas.TextOut(30, 510, '����� 3');
    for n: = 0 to Gizn[2] do
    begin
    BitBlt(
      Pole.Canvas.Handle,
      30 + 25 * n,
      525,
      20,
      20,
      bmp.KorGiznMask.Canvas.Handle,
      0,
      0,
      SrcPaint
    );
    BitBlt(
      Pole.Canvas.Handle,
      30 + 25 * n,
      525,
      20,
      20,
      bmp.KorGizn[2].Canvas.Handle,
      0,
      0,
      SrcAnd
    );
    end;
    end;
    if Players >= 4 then
            begin
    Info.Panels[6].Text: = '����� 4';
    Info.Panels[7].Text: = IntToStr(Chet[3]);
    Pole.Canvas.Font.Color: = clFuchsia;
    Pole.Canvas.TextOut(700, 510, '����� 4');
    for n: = 0 to Gizn[1] do
    begin
    BitBlt(
      Pole.Canvas.Handle,
      700 + 25 * n,
      525,
      20,
      20,
      bmp.KorGiznMask.Canvas.Handle,
      0,
      0,
      SrcPaint
    );
    BitBlt(
      Pole.Canvas.Handle,
      700 + 25 * n,
      525,
      20,
      20,
      bmp.KorGizn[3].Canvas.Handle,
      0,
      0,
      SrcAnd
    );
    end;
    end;

    Nach: = 0;
    m2:
    for n: = nach to High(AnimKonecKor) do
    begin
    BitBlt(
      Pole.Canvas.Handle,
      Trunc(AnimKonecKor[n].X - 25),
      Trunc(AnimKonecKor[n].Y - 25),
      50,
      50,
      bmp.KonecKorMask[AnimKonecKor[n].Kadr].Canvas.Handle,
      0,
      0,
      SrcPaint
    );
    BitBlt(
      Pole.Canvas.Handle,
      Trunc(AnimKonecKor[n].X - 25),
      Trunc(AnimKonecKor[n].Y - 25),
      50,
      50,
      bmp.KonecKor[AnimKonecKor[n].Kadr].Canvas.Handle,
      0,
      0,
      SrcAnd
    );
    if Raz1 = 0 then
            AnimKonecKor[n].Kadr: = AnimKonecKor[n].kadr+1;
    Raz1: = Raz1+1;
    if Raz1 > 7 then
            Raz1: = 0;
    if AnimKonecKor[n].Kadr > 4 then
            begin
    for k: = n to High(AnimKonecKor)-1 do
    AnimKonecKor[k]: = AnimKonecKor[k+1];
    nach: = n;
    SetLength(AnimKonecKor, High(AnimKonecKor));
    if n > High(AnimKonecKor) then
            break;
    goto m2;
    end;
    end;

    Pole.Refresh;
    end;

  }

  fun Up(Nom: Int) {
    if ((Kor[Nom].Angle<> 180) && (Kor[Nom].Angle<>0))
    Kor[Nom].VY -= 0.05 * sin(Kor[Nom].Angle / 180 * PI)

    if (Kor[Nom].VY >= cVmaxKor)
      Kor[Nom].VY = cVmaxKor

    if (Kor[Nom].VY <= -cVmaxKor)
      Kor[Nom].VY = -cVmaxKor

    if ((Kor[Nom].Angle<> 90) && (Kor[Nom].Angle<>270))
    Kor[Nom].VX += 0.05 * cos(Kor[Nom].Angle / 180 * PI)

    if (Kor[Nom].VX >= cVmaxKor)
      Kor[Nom].VX = cVmaxKor

    if (Kor[Nom].VX <= -cVmaxKor)
      Kor[Nom].VX = -cVmaxKor
  }

  fun Right(Nom: Int) {
    Kor[Nom].Angle -= 5
    if (Kor[Nom].Angle + 5 > 360)
      Kor[Nom].Angle -= 360
  }

  fun Left(Nom: Int) {
    Kor[Nom].Angle += 5
    if (Kor[Nom].Angle + 5 < 0)
      Kor[Nom].Angle += +360
  }

  fun Vystr(Nom: Int) {
    if (Kor[Nom].InGame = True) {
      if High(Pul) = -1 then
              SetLength(Pul, 1)
      else
        SetLength(Pul, High(Pul) + 2)

      Pul[High(Pul)].X: = Kor[Nom].CX+25*cos(Kor[Nom].Angle/180*pi)
      Pul[High(Pul)].Y: = Kor[Nom].CY-25*sin(Kor[Nom].Angle/180*pi)
      Pul[High(Pul)].VX: = +cVmaxPul*cos(Kor[Nom].Angle/180*pi)
      Pul[High(Pul)].VY: = -cVmaxPul*sin(Kor[Nom].Angle/180*pi)
      Pul[High(Pul)].Put: = 0
      Pul[High(Pul)].Nom: = Nom
    }
  }

  fun Collision(Etap: Int) {
    label
    m1, m2, m3;
    var
    VX1, VX2, VY1, VY2:Real;
    n, k, z, Nach, Nachn, Nachk:Integer;

    begin
    case Etap of
    1:
    begin
    if Players > 1 then
            begin
    for n: = 0 to Players-2 do
    for k: = n+1 to Players-1 do
    if (Kor[n].InGame = True) and(Kor[k].InGame = True) then
            if ((PreobX(Kor[n].Cx + 25) >= PreobX(Kor[k].Cx - 25)) and
              (PreobX(Kor[n].Cx + 25) <= PreobX(Kor[k].Cx + 25)) and

              (((PreobY(Kor[n].CY + 25) >= PreobY(Kor[k].CY - 25)) and
                      (PreobY(Kor[n].CY + 25) <= PreobY(Kor[k].CY + 25))) or

                      ((PreobY(Kor[n].CY - 25) >= PreobY(Kor[k].CY - 25)) and
                              (PreobY(Kor[n].CY - 25) <= PreobY(Kor[k].CY + 25))))
            ) or

    ((PreobX(Kor[n].Cx - 25) <= PreobX(Kor[k].Cx + 25)) and
            (PreobX(Kor[n].Cx - 25) >= PreobX(Kor[k].Cx - 25)) and

            (((PreobY(Kor[n].CY + 25) >= PreobY(Kor[k].CY - 25)) and
                    (PreobY(Kor[n].CY + 25) <= PreobY(Kor[k].CY + 25))) or

                    ((PreobY(Kor[n].CY - 25) >= PreobY(Kor[k].CY - 25)) and
                            (PreobY(Kor[n].CY - 25) <= PreobY(Kor[k].CY + 25))))) then
            begin
    VX1: = Kor[n].Vx;
    VX2: = Kor[k].Vx;
    VY1: = Kor[n].VY;
    VY2: = Kor[k].VY;
    if ((VX1 > 0) and (VX2 < 0)) or((VX1 < 0) and (VX2 > 0)) then
            begin
    Kor[n].Vx: = -VX1;
    Kor[k].Vx: = -VX2;
    end
    else
    begin
    Kor[n].Vx: = (VX1+VX2)/2;
    Kor[k].Vx: = (VX1+VX2)/2;
    if abs(VX1) > abS(VX2) then
            Kor[k].Vx: = 2*Kor[k].Vx
    else
    if abs(VX1) < abS(VX2) then
            Kor[n].Vx: = 2*Kor[n].Vx;
    end;
    if Kor[n].Vx > cVmaxKor then
            Kor[n].Vx: = cVmaxKor;
    if Kor[k].Vx > cVmaxKor then
            Kor[k].Vx: = cVmaxKor;
    if Kor[n].Vx < -cVmaxKor then
            Kor[n].Vx: = -cVmaxKor;
    if Kor[k].Vx < -cVmaxKor then
            Kor[k].Vx: = -cVmaxKor;
    if ((VY1 > 0) and (VY2 < 0)) or((VY1 < 0) and (VY2 > 0)) then
            begin
    Kor[n].VY: = -VY1;
    Kor[k].VY: = -VY2;
    end
    else
    begin
    Kor[n].VY: = (VY1+VY2)/2;
    Kor[k].VY: = (VY1+VY2)/2;
    if abs(VY1) > abS(VY2) then
            Kor[k].VY: = 2*Kor[k].VY
    else
    if abs(VY1) < abS(VY2) then
            Kor[n].VY: = 2*Kor[n].VY;
    end;
    if Kor[n].VY > cVmaxKor then
            Kor[n].VY: = cVmaxKor;
    if Kor[k].VY > cVmaxKor then
            Kor[k].VY: = cVmaxKor;
    if Kor[n].VY < -cVmaxKor then
            Kor[n].VY: = -cVmaxKor;
    if Kor[k].VY < -cVmaxKor then
            Kor[k].VY: = -cVmaxKor;
    end;
    end;
    end;
    2:
    begin
    if High(Pul) >= 0 then
            begin
    Nach: = 0;
    m1:
    for n: = 0 to Players-1 do
    if Kor[n].InGame = True then
            for k: = Nach to High(Pul) do
    if (((Pul[k].x + 2 >= Kor[n].Cx - 25) and (Pul[k].x + 2 <= Kor[n].Cx + 25) and
              ((Pul[k].Y + 2 >= Kor[n].CY - 25) and (Pul[k].Y + 2 <= Kor[n].CY + 25) or
                      (Pul[k].Y - 2 >= Kor[n].CY - 25) and (Pul[k].Y - 2 <= Kor[n].CY + 25))) or
      ((Pul[k].x - 2 >= Kor[n].Cx - 25) and (Pul[k].x - 2 <= Kor[n].Cx + 25) and (
              (Pul[k].Y + 2 >= Kor[n].CY - 25) and (Pul[k].Y + 2 <= Kor[n].CY + 25) or
                      (Pul[k].Y - 2 >= Kor[n].CY - 25) and (Pul[k].Y - 2 <= Kor[n].CY + 25)))
    ) and
    (n<> Pul [k].Nom) then
            begin
    VX1: = Kor[n].Vx;
    VX2: = Pul[k].Vx;
    VY1: = Kor[n].VY;
    VY2: = Pul[k].VY;
    if ((VX1 > 0) and (VX2 < 0)) or((VX1 < 0) and (VX2 > 0)) then
            Kor[n].Vx: = -VX1
    else
    begin
    Kor[n].Vx: = (VX1+VX2)/2;
    Kor[n].Vx: = 2*Kor[n].Vx;
    end;
    if Kor[n].Vx > cVmaxKor then
            Kor[n].Vx: = cVmaxKor;
    if Kor[n].Vx < -cVmaxKor then
            Kor[n].Vx: = -cVmaxKor;
    if ((VY1 > 0) and (VY2 < 0)) or((VY1 < 0) and (VY2 > 0)) then
            Kor[n].VY: = -VY1
    else
    begin
    Kor[n].VY: = (VY1+VY2)/2;
    Kor[n].VY: = 2*Kor[n].VY;
    end;
    if Kor[n].VY > cVmaxKor then
            Kor[n].VY: = cVmaxKor;
    if Kor[n].VY < -cVmaxKor then
            Kor[n].VY: = -cVmaxKor;
    if High(AnimKonecPul) = -1 then
            SetLength(AnimKonecPul, 1)
    else
      SetLength(AnimKonecPul, High(AnimKonecPul) + 2);
    AnimKonecPul[High(AnimKonecPul)].X: = Pul[k].X;
    AnimKonecPul[High(AnimKonecPul)].Y: = Pul[k].Y;
    AnimKonecPul[High(AnimKonecPul)].kadr: = 0;
    for z: = k to High(Pul)-1 do
    Pul[z]: = Pul[z+1];
    SetLength(Pul, High(Pul));
    Nach: = k;
    goto m1;
    end;
    end;
    end;
    3:
    begin
    Nach: = 0;
    m2:
    for n: = Nach to High(Pul)-1 do
    for k: = n+1 to High(Pul) do
    if ((PreobX(Pul[n].x + 2) >= PreobX(Pul[k].x - 2)) and
      (PreobX(Pul[n].x + 2) <= PreobX(Pul[k].x + 2)) and

      (((PreobY(Pul[n].Y + 2) >= PreobY(Pul[k].Y - 2)) and
              (PreobY(Pul[n].Y + 2) <= PreobY(Pul[k].Y + 2))) or

              ((PreobY(Pul[n].Y - 2) >= PreobY(Pul[k].Y - 2)) and
                      (PreobY(Pul[n].Y - 2) <= PreobY(Pul[k].Y + 2))))
    ) or

    ((PreobX(Pul[n].x - 2) <= PreobX(Pul[k].x + 2)) and
            (PreobX(Pul[n].x - 2) >= PreobX(Pul[k].x - 2)) and

            (((PreobY(Pul[n].Y + 2) >= PreobY(Pul[k].Y - 2)) and
                    (PreobY(Pul[n].Y + 2) <= PreobY(Pul[k].Y + 2))) or

                    ((PreobY(Pul[n].Y - 2) >= PreobY(Pul[k].Y - 2)) and
                            (PreobY(Pul[n].Y - 2) <= PreobY(Pul[k].Y + 2))))) then
            begin
    if High(AnimKonecPul) = -1 then
            SetLength(AnimKonecPul, 1)
    else
      SetLength(AnimKonecPul, High(AnimKonecPul) + 2);
    AnimKonecPul[High(AnimKonecPul)].X: = Pul[k].X;
    AnimKonecPul[High(AnimKonecPul)].Y: = Pul[k].Y;
    AnimKonecPul[High(AnimKonecPul)].kadr: = 0;
    for z: = k to High(Pul)-1 do
    Pul[z]: = Pul[z+1];
    SetLength(Pul, High(Pul));
    for z: = n to High(Pul)-1 do
    Pul[z]: = Pul[z+1];
    SetLength(Pul, High(Pul));
    Nach: = n;
    goto m2;
    end;
    end;
    4:
    begin
    for n: = 0 to High(Met)-1 do
    for k: = n+1 to High(Met) do
    if ((PreobX(Met[n].x + Met[n].Razmer) >= PreobX(Met[k].x - Met[k].Razmer)) and
      (PreobX(Met[n].x + Met[n].Razmer) <= PreobX(Met[k].x + Met[k].Razmer)) and

      (((PreobY(Met[n].Y + Met[n].Razmer) >= PreobY(Met[k].Y - Met[k].Razmer)) and
              (PreobY(Met[n].Y + Met[n].Razmer) <= PreobY(Met[k].Y + Met[k].Razmer))) or

              ((PreobY(Met[n].Y - Met[n].Razmer) >= PreobY(Met[k].Y - Met[k].Razmer)) and
                      (PreobY(Met[n].Y - Met[n].Razmer) <= PreobY(Met[k].Y + Met[k].Razmer))))
    ) or

    ((PreobX(Met[n].x - Met[n].Razmer) <= PreobX(Met[k].x + Met[k].Razmer)) and
            (PreobX(Met[n].x - Met[n].Razmer) >= PreobX(Met[k].x - Met[k].Razmer)) and

            (((PreobY(Met[n].Y + Met[n].Razmer) >= PreobY(Met[k].Y - Met[k].Razmer)) and
                    (PreobY(Met[n].Y + Met[n].Razmer) <= PreobY(Met[k].Y + Met[k].Razmer))) or

                    ((PreobY(Met[n].Y - Met[n].Razmer) >= PreobY(Met[k].Y - Met[k].Razmer)) and
                            (PreobY(Met[n].Y - Met[n].Razmer) <= PreobY(Met[k].Y + Met[k].Razmer))))) then
            begin
    VX1: = Met[n].Vx;
    VX2: = Met[k].Vx;
    VY1: = Met[n].VY;
    VY2: = Met[k].VY;
    if ((VX1 > 0) and (VX2 < 0)) or((VX1 < 0) and (VX2 > 0)) then
            begin
    Met[n].Vx: = -VX1;
    Met[k].Vx: = -VX2;
    end
    else
    begin
    Met[n].Vx: = (VX1+VX2)/2;
    Met[k].Vx: = (VX1+VX2)/2;
    if abs(VX1) > abS(VX2) then
            Met[k].Vx: = 2*Met[k].Vx
    else
    if abs(VX1) < abS(VX2) then
            Met[n].Vx: = 2*Met[n].Vx;
    end;
    if Met[n].Vx > cVmaxMet then
            Met[n].Vx: = cVmaxMet;
    if Met[k].Vx > cVmaxMet then
            Met[k].Vx: = cVmaxMet;
    if Met[n].Vx < -cVmaxMet then
            Met[n].Vx: = -cVmaxMet;
    if Met[k].Vx < -cVmaxMet then
            Met[k].Vx: = -cVmaxMet;
    if ((VY1 > 0) and (VY2 < 0)) or((VY1 < 0) and (VY2 > 0)) then
            begin
    Met[n].VY: = -VY1;
    Met[k].VY: = -VY2;
    end
    else
    begin
    Met[n].VY: = (VY1+VY2)/2;
    Met[k].VY: = (VY1+VY2)/2;
    if abs(VY1) > abS(VY2) then
            Met[k].VY: = 2*Met[k].VY
    else
    if abs(VY1) < abS(VY2) then
            Met[n].VY: = 2*Met[n].VY;
    end;
    if Met[n].VY > cVmaxMet then
            Met[n].VY: = cVmaxMet;
    if Met[k].VY > cVmaxMet then
            Met[k].VY: = cVmaxMet;
    if Met[n].VY < -cVmaxMet then
            Met[n].VY: = -cVmaxMet;
    if Met[k].VY < -cVmaxMet then
            Met[k].VY: = -cVmaxMet;
    end;
    Nachn: = 0;
    Nachk: = 0;
    Nach: = 0;
    m3:
    if High(Pul) >= 0 then
            if High(Met) >= 0 then
                    begin

    for n: = Nachn to High(Met) do
    for k: = Nach to High(Pul) do
    if ((Pul[k].x + 2 >= Met[n].x - Met[n].Razmer) and (Pul[k].x + 2 <= Met[n].x + Met[n].Razmer) and (
              (Pul[k].Y + 2 >= Met[n].Y - Met[n].Razmer) and (Pul[k].Y + 2 <= Met[n].Y + Met[n].Razmer) or
                      (Pul[k].Y - 2 >= Met[n].Y - Met[n].Razmer) and (Pul[k].Y - 2 <= Met[n].Y + Met[n].Razmer))
    ) or
    ((Pul[k].x - 2 >= Met[n].x - Met[n].Razmer) and (Pul[k].x - 2 <= Met[n].x + Met[n].Razmer) and (
            (Pul[k].Y + 2 >= Met[n].Y - Met[n].Razmer) and (Pul[k].Y + 2 <= Met[n].Y + Met[n].Razmer) or
                    (Pul[k].Y - 2 >= Met[n].Y - Met[n].Razmer) and (Pul[k].Y - 2 <= Met[n].Y + Met[n].Razmer))) then
            begin
    Met[n].Razmer: = Met[n].Razmer-5;
    Met[n].TipRazmer: = Met[n].TipRazmer+1;
    Chet[Pul[k].Nom]: = Chet[Pul[k].Nom]+100*Met[n].TipRazmer;
    if Met[n].TipRazmer > 3 then
            begin
    for z: = n to High(Met)-1 do
    Met[z]: = Met[z+1];
    SetLength(Met, High(met));
    Nachn: = n;
    if High(AnimKonecPul) = -1 then
            SetLength(AnimKonecPul, 1)
    else
      SetLength(AnimKonecPul, High(AnimKonecPul) + 2);
    AnimKonecPul[High(AnimKonecPul)].X: = Pul[k].X;
    AnimKonecPul[High(AnimKonecPul)].Y: = Pul[k].Y;
    AnimKonecPul[High(AnimKonecPul)].kadr: = 0;
    for z: = k to High(Pul)-1 do
    Pul[z]: = Pul[z+1];
    SetLength(Pul, High(Pul));
    Nachk: = k;
    goto m3;
    end
    else
    begin
    SetLength(Met, High(Met) + 2);
    Met[High(Met)].X: = Met[n].X+(Met[n].Razmer+10);
    Met[High(Met)].Y: = Met[n].Y+(Met[n].Razmer+10);
    Met[High(Met)].Angle: = Met[n].Angle-120;
    met[High(Met)].VX: = +cVmaxMet*cos(Met[High(Met)].Angle/180*pi);
    met[High(Met)].VY: = -cVmaxMet*sin(Met[High(Met)].Angle/180*pi);
    Met[High(Met)].Razmer: = Met[n].Razmer;
    Met[High(Met)].TipRazmer: = Met[n].TipRazmer;
    Met[High(Met)].Tip: = Met[n].Tip;

    SetLength(Met, High(Met) + 2);
    Met[High(Met)].X: = Met[n].X-(Met[n].Razmer+10);
    Met[High(Met)].Y: = Met[n].Y-(Met[n].Razmer+10);
    Met[High(Met)].Angle: = Met[n].Angle-240;
    met[High(Met)].VX: = +cVmaxMet*cos(Met[High(Met)].Angle/180*pi);
    met[High(Met)].VY: = -cVmaxMet*sin(Met[High(Met)].Angle/180*pi);
    Met[High(Met)].Razmer: = Met[n].Razmer;
    Met[High(Met)].TipRazmer: = Met[n].TipRazmer;
    Met[High(Met)].Tip: = Met[n].Tip;
    end;
    if High(AnimKonecPul) = -1 then
            SetLength(AnimKonecPul, 1)
    else
      SetLength(AnimKonecPul, High(AnimKonecPul) + 2);
    AnimKonecPul[High(AnimKonecPul)].X: = Pul[k].X;
    AnimKonecPul[High(AnimKonecPul)].Y: = Pul[k].Y;
    AnimKonecPul[High(AnimKonecPul)].kadr: = 0;
    for z: = k to High(Pul)-1 do
    Pul[z]: = Pul[z+1];
    SetLength(Pul, High(Pul));
    Nachk: = k;
    goto m3;
    end;
    end;


    for n: = 0 to Players-1 do
    for k: = 0 to High(Met) do
    if Kor[n].InGame = True then
            if ((PreobX(Kor[n].Cx + 25) >= PreobX(met[k].x - Met[k].Razmer)) and
              (PreobX(Kor[n].Cx + 25) <= PreobX(met[k].x + Met[k].Razmer)) and

              (((PreobY(Kor[n].CY + 25) >= PreobY(met[k].Y - Met[k].Razmer)) and
                      (PreobY(Kor[n].CY + 25) <= PreobY(met[k].Y + Met[k].Razmer))) or

                      ((PreobY(Kor[n].CY - 25) >= PreobY(met[k].Y - Met[k].Razmer)) and
                              (PreobY(Kor[n].CY - 25) <= PreobY(met[k].Y + Met[k].Razmer))))
            ) or

    ((PreobX(Kor[n].Cx - 25) <= PreobX(met[k].x + Met[k].Razmer)) and
            (PreobX(Kor[n].Cx - 25) >= PreobX(met[k].x - Met[k].Razmer)) and

            (((PreobY(Kor[n].CY + 25) >= PreobY(met[k].Y - Met[k].Razmer)) and
                    (PreobY(Kor[n].CY + 25) <= PreobY(met[k].Y + Met[k].Razmer))) or

                    ((PreobY(Kor[n].CY - 25) >= PreobY(met[k].Y - Met[k].Razmer)) and
                            (PreobY(Kor[n].CY - 25) <= PreobY(met[k].Y + Met[k].Razmer))))) then
            begin
    Met[k].Razmer: = Met[k].Razmer-5;
    Met[k].TipRazmer: = Met[k].TipRazmer+1;
    Gizn[n]: = Gizn[n]-1;
    Kor[n].InGame: = False;
    if Met[k].TipRazmer > 3 then
            begin
    for z: = k to High(Met)-1 do
    Met[z]: = Met[z+1];
    SetLength(Met, High(met));
    end
    else
    begin
    SetLength(Met, High(Met) + 2);
    Met[High(Met)].X: = Met[k].X;
    Met[High(Met)].Y: = Met[k].Y;
    Met[High(Met)].Angle: = Met[k].Angle-90;
    met[High(Met)].VX: = +cVmaxMet*cos(Met[High(Met)].Angle/180*pi);
    met[High(Met)].VY: = -cVmaxMet*sin(Met[High(Met)].Angle/180*pi);
    Met[High(Met)].Razmer: = Met[k].Razmer;
    Met[High(Met)].TipRazmer: = Met[k].TipRazmer;
    Met[High(Met)].Tip: = Met[k].Tip;

    SetLength(Met, High(Met) + 2);
    Met[High(Met)].X: = Met[k].X;
    Met[High(Met)].Y: = Met[k].Y;
    Met[High(Met)].Angle: = Met[k].Angle-180;
    met[High(Met)].VX: = +cVmaxMet*cos(Met[High(Met)].Angle/180*pi);
    met[High(Met)].VY: = -cVmaxMet*sin(Met[High(Met)].Angle/180*pi);
    Met[High(Met)].Razmer: = Met[k].Razmer;
    Met[High(Met)].TipRazmer: = Met[k].TipRazmer;
    Met[High(Met)].Tip: = Met[k].Tip;
    end;
    if High(AnimKonecKor) = -1 then
            SetLength(AnimKonecKor, 1)
    else
      SetLength(AnimKonecKor, High(AnimKonecKor) + 2);
    AnimKonecKor[High(AnimKonecKor)].X: = Kor[n].CX;
    AnimKonecKor[High(AnimKonecKor)].Y: = Kor[n].CY;
    AnimKonecKor[High(AnimKonecKor)].kadr: = 0;
    end;
    end;
    end;
    end;

  }

  fun KadrKor() {
    var
    n, k, z, Nom:Integer;
    Fl:Boolean;
    begin
    Fl: = False;
    for nom: = 0 to Players-1 do
    if Gizn[Nom] >= 0 then
            Fl: = True;
    if Fl = False then
            begin
    NewRaund;
    Exit;
    end;
    Collision(1);
    for nom: = 0 to Players-1 do
    if Kor[Nom].InGame = True then
            begin
    Kor[Nom].CY: = Kor[Nom].CY+Kor[Nom].VY;
    if Kor[Nom].CY > Pole.Height then
            Kor[Nom].CY: = 0;
    if Kor[Nom].CY < 0 then
            Kor[Nom].CY: = Pole.Height;
    Kor[Nom].CX: = Kor[Nom].CX+Kor[Nom].VX;
    if Kor[Nom].CX > Pole.Width then
            Kor[Nom].CX: = 0;
    if Kor[Nom].CX < 0 then
            Kor[Nom].CX: = Pole.Width;
    end
    else
    begin
    if Gizn[Nom] >= 0 then
            for k: = 0 to 3 do
    if Nom = k then
            begin
    for z: = 0 to 3 do
    begin
    Fl: = true;
    for n: = 0 to Players-1 do
    if n<> k then
    if (Respaun[z].X - 100 < Kor[n].CX + 25) and(Respaun[z].X + 100 > Kor[n].CX + 25) and
            ((Respaun[z].Y - 100 < Kor[n].CY + 25) and (Respaun[z].Y + 100 > Kor[n].CY + 25) or
                    (Respaun[z].Y - 100 < Kor[n].CY - 25) and (Respaun[z].Y + 100 > Kor[n].CY - 25)) or
            (Respaun[z].X - 100 < Kor[n].CX - 25) and (Respaun[z].X + 100 > Kor[n].CX - 25) and
            ((Respaun[z].Y - 100 < Kor[n].CY + 25) and (Respaun[z].Y + 100 > Kor[n].CY + 25) or
                    (Respaun[z].Y - 100 < Kor[n].CY - 25) and (Respaun[z].Y + 100 > Kor[n].CY - 25))
    then
    Fl: = False;
    for n: = 0 to High(Pul) do
    if n<> nom then
    if (Respaun[z].X - 100 < Pul[n].X + 2) and(Respaun[z].X + 100 > Pul[n].X + 2) and
            ((Respaun[z].Y - 100 < Pul[n].Y + 2) and (Respaun[z].Y + 100 > Pul[n].Y + 2) or
                    (Respaun[z].Y - 100 < Pul[n].Y - 2) and (Respaun[z].Y + 100 > Pul[n].Y - 2)) or
            (Respaun[z].X - 100 < Pul[n].X - 2) and (Respaun[z].X + 100 > Pul[n].X - 2) and
            ((Respaun[z].Y - 100 < Pul[n].Y + 2) and (Respaun[z].Y + 100 > Pul[n].Y + 2) or
                    (Respaun[z].Y - 100 < Pul[n].Y - 2) and (Respaun[z].Y + 100 > Pul[n].Y - 2))
    then
    Fl: = False;
    for n: = 0 to High(Met) do
    if n<> nom then
    if (Respaun[z].X - 100 < Met[n].X + Met[n].Razmer) and(Respaun[z].X + 100 > Met[n].X + Met[n].Razmer) and
            ((Respaun[z].Y - 100 < Met[n].Y + Met[n].Razmer) and (Respaun[z].Y + 100 > Met[n].Y + Met[n].Razmer) or
                    (Respaun[z].Y - 100 < Met[n].Y - Met[n].Razmer) and (Respaun[z].Y + 100 > Met[n].Y - Met[n].Razmer)) or
            (Respaun[z].X - 100 < Met[n].X - Met[n].Razmer) and (Respaun[z].X + 100 > Met[n].X - Met[n].Razmer) and
            ((Respaun[z].Y - 100 < Met[n].Y + Met[n].Razmer) and (Respaun[z].Y + 100 > Met[n].Y + Met[n].Razmer) or
                    (Respaun[z].Y - 100 < Met[n].Y - Met[n].Razmer) and (Respaun[z].Y + 100 > Met[n].Y - Met[n].Razmer))
    then
    Fl: = False;

    if Fl = True then
            begin
    Kor[k].CX: = Respaun[z].X;
    Kor[k].CY: = Respaun[z].Y;
    Kor[k].Vx: = 0;
    Kor[k].Vy: = 0;
    Kor[k].Angle: = Respaun[z].Angle;
    Kor[k].InGame: = True;
    Break;
    end;
    end;
    end;
    end;
    end;

  }

  fun KadrPul() {
    label
    m1;
    var
    n, k, Nach:Integer;
    begin
    Nach: = 0;
    m1:
    if High(Pul)<> - 1 then
            begin
    n: = nach;
    While(n <= High(Pul)) do
      begin
    if n > High(Pul) then
            Exit;
    for k: = 0 to 5 do
    begin
    Pul[n].X: = Pul[n].X+Pul[n].VX;
    if Pul[n].X > Pole.Width then
            Pul[n].X: = 0;
    if Pul[n].X < 0 then
            Pul[n].X: = Pole.Width;
    Pul[n].Y: = Pul[n].Y+Pul[n].VY;
    if Pul[n].Y > Pole.Height then
            Pul[n].Y: = 0;
    if Pul[n].Y < 0 then
            Pul[n].Y: = Pole.Height;
    Pul[n].Put: = Pul[n].Put+cVmaxPul;
    Collision(2);
    Collision(3);
    if High(Pul) = -1 then
            Exit;
    end;
    if Pul[n].Put > 300 then
            begin
    for k: = n to High(Pul)-1 do
    Pul[k]: = Pul[k+1];
    SetLength(Pul, High(Pul));
    Nach: = n+1;
    goto m1;
    end;
    n: = n+1;
    end;
    end;
    end;

  }

  fun KadrMet() {
    var n: Int
    var k: Int

    if (High(Met)<> - 1) {
      for (n in 0..High(Met)) {
        if (n > High(Met))
          return
        for (k in 0..2) {
          Met[n].X += Met[n].VX

          if (Met[n].X > Pole.Width)
            Met[n].X = 0

          if (Met[n].X < 0)
            Met[n].X = Pole.Width

          Met[n].Y += Met[n].VY

          if (Met[n].Y > Pole.Height)
            Met[n].Y = 0
          if (Met[n].Y < 0)
            Met[n].Y = Pole.Height
          Collision(4)
          if (n > High(Met))
            return
        }
      }
    } else
      Newraund()
  }

  var
          Gam: ClGam;

}