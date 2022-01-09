package com.fiz.android.battleinthespace


unit untForm;

interface

uses
Windows, Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
Dialogs, ExtCtrls;

type
Tfrm =
class(TForm)
tmr: TTimer;
procedure FormShow(Sender: TObject);
procedure tmrTimer(Sender: TObject);
procedure FormCreate(Sender: TObject);
procedure FormClick(Sender: TObject);
private
TekRazm:Double;    //  ������� ������ ������� ������ AHF
TekPol:Integer;     //  ������� ����������� �������� ������� ������ AHF
TR1,TR2,TR3:Double;
TP1,TP2,TP3:Integer;
Kn1,Kn2,Kn3:Boolean;
Etap:Integer;          //���� ������
VirPole:TBitmap;
bmpAHF:TBitmap;
bmpBitva,bmpV,bmpKocmos:TBitmap;
bmpBukv:array[0..11]of TBitmap;
MasSpisokBukv:array[0..11]of Integer;
SMBukvX:array[0..11]of Double;
SMBukvY:array[0..11]of Double;
PoprResol:Real;
public
{
  Public declarations
}
end;

var
        frm: Tfrm;

implementation

{ $R *.dfm }

procedure CreateDLL; stdcall; external 'Loading.dll';
function GetRis(Tip:Integer;Nom:Integer=0):TBitmap;stdcall; external 'Loading.dll';
procedure FreeDLL;stdcall; external 'Loading.dll';

procedure Tfrm.FormClick(Sender: TObject);
begin
if Etap<=14 then
Etap:=14
else
Close;      //�������
end;

procedure Tfrm.FormCreate(Sender: TObject);
var
        n: Integer;
begin
PoprResol:=0.625;
TekRazm:=0.1;
TekPol:=0;
Etap:=0;
CreateDLL;
bmpAHF:=GetRis(0);
for n:=0 to 11 do
bmpBukv[n]:=GetRis(1,n);

MasSpisokBukv[0]:=0;
MasSpisokBukv[1]:=1;
MasSpisokBukv[2]:=2;
MasSpisokBukv[3]:=3;
MasSpisokBukv[4]:=4;
MasSpisokBukv[5]:=5;
MasSpisokBukv[6]:=6;
MasSpisokBukv[7]:=7;
MasSpisokBukv[8]:=8;
MasSpisokBukv[9]:=9;
MasSpisokBukv[10]:=2;
MasSpisokBukv[11]:=5;

SMBukvX[0]:=-200*PoprResol;
for n:=1 to 11 do
SMBukvX[n]:=SMBukvX[n-1]+80*PoprResol;

SMBukvY[0]:=50*PoprResol;
for n:=1 to 11 do
SMBukvY[n]:=(50+30)*PoprResol;

bmpBitva:=GetRis(2);
bmpV:=GetRis(3);
bmpKocmos:=GetRis(4);

VirPole:=TBitmap.Create;
VirPole.Width:=800;
VirPole.Height:=600;



FreeDLL;
end;

procedure Tfrm.tmrTimer(Sender: TObject);
var
n,k:Integer;
begin
if Etap=0 then
begin
if TekPol=0 then
begin
if TekRazm<=0.25 then
TekRazm:=TekRazm+0.001
else
TekRazm:=TekRazm+0.02;
if TekRazm>=1.5 then
TekPol:=1;
end
else
begin
TekRazm:=TekRazm-0.003;
if TekRazm<=1 then
begin
Etap:=1;
TekRazm:=5;
Exit;
end;
end;
PatBlt(VirPole.Canvas.Handle,0,0,800,600,BLACKNESS);
StretchBlt(VirPole.Canvas.Handle,Trunc((800-TekRazm*bmpAHF.Width*PoprResol)/2),Trunc((600-TekRazm*bmpAHF.Height*PoprResol)/2),Trunc(TekRazm*bmpAHF.Width*PoprResol),Trunc(TekRazm*bmpAHF.Height*PoprResol),
bmpAHF.Canvas.Handle,0,0,bmpAHF.Width,bmpAHF.Height,SRCCopy);
BitBlt(Canvas.Handle,0,0,800,600,VirPole.Canvas.Handle,0,0,SRCCOPY);
Exit;
end;

if (Etap>0)and(Etap<14) then
for n:=0 to Etap-1 do
begin
TekRazm:=TekRazm-0.1;
if TekRazm<=1 then
begin
Etap:=Etap+1;
if Etap>13 then
begin
Etap:=14;
tmr.Interval:=3000;
end;
TekRazm:=5;
Exit;
end;
PatBlt(VirPole.Canvas.Handle,0,0,800,600,BLACKNESS);
StretchBlt(VirPole.Canvas.Handle,Trunc((800-bmpAHF.Width*PoprResol)/2),Trunc((600-bmpAHF.Height*PoprResol)/2),Trunc(bmpAHF.Width*PoprResol),Trunc(bmpAHF.Height*PoprResol),
bmpAHF.Canvas.Handle,0,0,bmpAHF.Width,bmpAHF.Height,SRCCopy);
for k:=0 to Etap-2 do
StretchBlt(VirPole.Canvas.Handle,Trunc(SmBukvX[k]+(800-bmpAHF.Width*PoprResol)/2),Trunc(SmBukvY[k]+bmpAHF.Height*PoprResol+(600-bmpAHF.Height*PoprResol)/2),Trunc(bmpBukv[k].Width*PoprResol),Trunc(bmpBukv[k].Height*PoprResol),
bmpBukv[k].Canvas.Handle,0,0,bmpBukv[k].Width,bmpBukv[k].Height,SRCCopy);
if Etap<13 then
StretchBlt(VirPole.Canvas.Handle,Trunc(SmBukvX[n]+(800-TekRazm*bmpAHF.Width)/2),Trunc(SmBukvY[n]+bmpAHF.Height*PoprResol+(600-TekRazm*bmpAHF.Height)/2),Trunc(TekRazm*bmpBukv[n].Width),Trunc(TekRazm*bmpBukv[n].Height),
bmpBukv[n].Canvas.Handle,0,0,bmpBukv[n].Width,bmpBukv[n].Height,SRCCopy);
BitBlt(Canvas.Handle,0,0,800,600,VirPole.Canvas.Handle,0,0,SRCCOPY);
end;

if Etap=14 then
begin
tmr.Interval:=1;
TR1:=50*PoprResol;
TR2:=700*PoprResol;
TR3:=50*PoprResol;
TP1:=0;
TP2:=0;
TP3:=0;
Kn1:=False;
Kn2:=False;
Kn3:=False;
Etap:=15
end;

if Etap=15 then
begin
if TP1=0 then
begin
TR1:=TR1+15*PoprResol;
if TR1>=640*PoprResol then
begin
TP1:=1;
end;
end
else
begin
TR1:=TR1-15*PoprResol;
if TR1<=460*PoprResol then
begin
Kn1:=True;
TR1:=460*PoprResol;
end;
end;
PatBlt(VirPole.Canvas.Handle,0,0,800,600,BLACKNESS);
StretchBlt(VirPole.Canvas.Handle,Trunc(TR1),600 div 6,Trunc(bmpBitva.Width*PoprResol),Trunc(bmpBitva.Height*PoprResol),
bmpBitva.Canvas.Handle,0,0,bmpBitva.Width,bmpBitva.Height,SRCCopy);



if TP2=0 then
begin
TR2:=TR2-15*PoprResol;
if TR2<=300*PoprResol then
begin
TP2:=1;
end;
end
else
begin
TR2:=TR2+15*PoprResol;
if TR2>=620*PoprResol then
begin
Kn2:=True;
TR2:=620*PoprResol;
end;
end;
StretchBlt(VirPole.Canvas.Handle,Trunc(TR2),600 div 6+Trunc(50*PoprResol+bmpBitva.Height*PoprResol),Trunc(bmpV.Width*PoprResol),Trunc(bmpV.Height*PoprResol),
bmpV.Canvas.Handle,0,0,bmpV.Width,bmpV.Height,SRCCopy);



if TP3=0 then
begin
TR3:=TR3+15*PoprResol;
if TR3>=640*PoprResol  then
begin
TP3:=1;
end;
end
else
begin
TR3:=TR3-15*PoprResol;
if TR3<=460*PoprResol  then
begin
Kn3:=True;
TR3:=460*PoprResol;
end;
end;
StretchBlt(VirPole.Canvas.Handle,Trunc(TR3),600 div 6+Trunc(100*PoprResol+bmpBitva.Height*PoprResol+bmpV.Height*PoprResol),Trunc(bmpKocmos.Width*PoprResol),Trunc(bmpKocmos.Height*PoprResol),
bmpKocmos.Canvas.Handle,0,0,bmpKocmos.Width,bmpKocmos.Height,SRCCopy);
BitBlt(Canvas.Handle,0,0,800,600,VirPole.Canvas.Handle,0,0,SRCCOPY);
end;

if Etap=16 then
Close;

If (Kn1=True) and (Kn2=True) and (Kn3=True) then
begin
Etap:=16;
tmr.Interval:=3000;
Exit;
end;
end;

procedure Tfrm.FormShow(Sender: TObject);
begin
tmr.Interval:=1;
end;


end.
