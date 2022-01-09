package com.fiz.android.battleinthespace



unit untMain;

interface

uses
Windows, Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
Dialogs, Menus, ComCtrls, ExtCtrls, AppEvnts;

type
recKey=Record
Up:Integer;
Left:Integer;
Right:Integer;
Vystr:Integer;
end;
TfrmMain =
class(TForm)
MM: TMainMenu;
mnuFile: TMenuItem;
mnuOpt: TMenuItem;
mnuFileNewGame: TMenuItem;
N4: TMenuItem;
mnuFileClose: TMenuItem;
mnuOptOptions: TMenuItem;
stbInfo: TStatusBar;
imgPole: TImage;
tmr: TTimer;
tmrDvig: TTimer;
ApplicationEvents1: TApplicationEvents;
procedure ApplicationEvents1Message(
var Msg: tagMSG;
var Handled: Boolean);
procedure tmrDvigTimer(Sender: TObject);
procedure FormDestroy(Sender: TObject);
procedure FormShow(Sender: TObject);
procedure mnuOptOptionsClick(Sender: TObject);
procedure tmrTimer(Sender: TObject);
procedure mnuFileNewGameClick(Sender: TObject);
procedure mnuFileCloseClick(Sender: TObject);
private
Keys:array [0..3] of recKey;
KeyUp:array [0..3] of Boolean;
KeyLeft:array [0..3] of Boolean;
KeyRight:array [0..3] of Boolean;
KeyVystr:array [0..3] of Boolean;
PauseVystr:array [0..3] of Integer;
tmrPauseVystr:array [0..3] of TTimer;
Game:Boolean;
procedure EventIntervaltmrPauseVystr(Sender: TObject);
public
end;

var
        frmMain: TfrmMain;

implementation

uses untOpt, untClGam, untForm;

{ $R *.dfm }

procedure TfrmMain.mnuFileCloseClick(Sender: TObject);
begin
Close;
end;

procedure TfrmMain.mnuFileNewGameClick(Sender: TObject);
begin
Gam.NewGame;
Game:=True;
Gam.Obnov;
end;

procedure TfrmMain.tmrTimer(Sender: TObject);
var
        n: Integer;
begin
if Game=true then
begin
for n:=0 to Gam.Players-1 do
begin
if KeyUp[n]=True then
Gam.Up(n);
if KeyRight[n]=True then
Gam.Right(n);
if KeyLeft[n]=True then
Gam.Left(n);
if KeyVystr[n]=True then
begin
if PauseVystr[n]=0 then
begin
Gam.Vystr(n);
PauseVystr[n]:=500;
tmrPauseVystr[n].Interval:=PauseVystr[n];
end;
end;
end;
Gam.Obnov;
end;
end;

procedure TfrmMain.mnuOptOptionsClick(Sender: TObject);
var
        Fl: Boolean;
begin
Fl:=Game;
frmOption.ShowModal;
if Game=true then
Gam.Obnov;
Game:=Fl;
end;

procedure TfrmMain.FormShow(Sender: TObject);
var
        n: Integer;
begin
frm.ShowMODAL;
Gam:=ClGam.Create(imgPole,stbInfo);
Keys[0].Up:=87;
Keys[0].Right:=68;
Keys[0].Left:=65;
Keys[0].Vystr:=81;
Keys[1].Up:=79;
Keys[1].Right:=186;
Keys[1].Left:=75;
Keys[1].Vystr:=73;
Keys[2].Up:=89;
Keys[2].Right:=74;
Keys[2].Left:=71;
Keys[2].Vystr:=65;
Keys[3].Up:=104;
Keys[3].Right:=102;
Keys[3].Left:=100;
Keys[3].Vystr:=65;
for n:=0 to 3 do
begin
PauseVystr[n]:=0;
tmrPauseVystr[n]:=TTimer.Create(nil);
tmrPauseVystr[n].OnTimer:=EventIntervaltmrPauseVystr;
tmrPauseVystr[n].Tag:=n;
tmrPauseVystr[n].Interval:=0;
end;
end;

procedure TfrmMain.FormDestroy(Sender: TObject);
begin
Gam.Free;
end;

fun TfrmMain.tmrDvigTimer(Sender: TObject) {
  if (game == true) {
    Gam.KadrKor();
    Gam.KadrPul();
    Gam.KadrMet();
  }
}

procedure TfrmMain.EventIntervaltmrPauseVystr(Sender: TObject);
begin
PauseVystr[TTimer(Sender).Tag]:=0;
tmrPauseVystr[TTimer(Sender).Tag].Interval:=0;
end;

procedure TfrmMain.ApplicationEvents1Message(
var Msg: tagMSG;
var Handled: Boolean);
var
        n: Integer;
begin
if MSG.message=WM_KEYDOWN then
begin
if Game=true then
begin
for n:=0 to Gam.Players-1 do
begin
if MSG.wParam=Keys[n].Up then
KeyUp[n]:=True;
if MSG.wParam=Keys[n].Right then
KeyRight[n]:=True;
if MSG.wParam=Keys[n].Left then
KeyLeft[n]:=True;
if MSG.wParam=Keys[n].Vystr then
KeyVystr[n]:=True;
end;
end;
end;
if MSG.message=WM_KEYUP then
begin
if Game=true then
begin
for n:=0 to Gam.Players-1 do
begin
if MSG.wParam=Keys[n].Up then
KeyUp[n]:=False;
if MSG.wParam=Keys[n].Right then
KeyRight[n]:=False;
if MSG.wParam=Keys[n].Left then
KeyLeft[n]:=False;
if MSG.wParam=Keys[n].Vystr then
KeyVystr[n]:=False;
end;
end;
end;
end;

end.