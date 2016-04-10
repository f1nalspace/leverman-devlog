unit win32_leverman;

{Kommentar}

{$mode delphi}

interface

uses
  // Andere units
  SysUtils;

// Globale Sichtbarkeit bis implementation!
// Deklaration von Globale Methoden, Typen, Klassen
// oder Globale Variablen

// Deklaration für unsere hallowelt2 methode
procedure HalloWelt2();

var
  D: integer;
  D42: integer = 42;

implementation

var
  X: string = 'Mein text';
  Y, Z: integer; // Mehrere Zuweisungen für gleichen Typ ohne direkte initialiserung

// HalloWelt3 Geht nicht - Deklaration nur in interface!
procedure WillHalloWeltAufrufenObwohlDarunter();
begin
  //HalloWelt3();
  HalloWelt2();
end;

// Hier kommt die tatsächliche implementierung rein
// Nicht-Globale Sichtbarkeit (Nur Sichtbar in dieser Unit)
// Deklaration können hier ebenfalls gemacht werden

procedure HalloWelt2();
begin
  WriteLn('Hallo Welt 2');
end;

procedure HalloWelt3();
begin
  WriteLn('Hallo Welt 3');
end;

// Optionalen Initialisierung und Finalisierung block (Ähnlich wie Static Initialize)
// Meistens verwendet um CPU geschichten umzustellen und wieder zurückzustellen (FPU-Fehlerkorrektur)
// Ohne begin/end block!
initialization
  WriteLn('Unit beginnt');

finalization
  WriteLn('Unit hört auf');

  // Schliesst unit ab
end.

