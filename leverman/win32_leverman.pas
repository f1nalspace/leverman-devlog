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

procedure HalloWelt3(); forward;
procedure VariablenTypen(); forward;

procedure WillHalloWeltAufrufenObwohlDarunter();
begin
  HalloWelt3();
  HalloWelt2();
end;

// Hier kommt die tatsächliche implementierung rein
// Nicht-Globale Sichtbarkeit (Nur Sichtbar in dieser Unit)
// Deklaration können hier ebenfalls gemacht werden

procedure HalloWelt2();
begin
  WriteLn('Hallo Welt 2');
  VariablenTypen;
end;

procedure HalloWelt3();
begin
  WriteLn('Hallo Welt 3');
end;

procedure VariablenTypen();
// Standard-Variablentypen = System
var
  // Ganzzahlen
  U8 : Byte; // 2^8 (0 bis 255)
  S8 : ShortInt; // 2^7 (-128 bis 127)
var
  U16 : Word; // 2^16 (0 bis 65535)
  S16 : SmallInt; // 2^15 (-32768 bis 32767)
var
  u32 : LongWord; // 2^32 (0 bis 4294967295)
  s32 : LongInt; // 2^31 (-2147483648 bis 2147483647)
var
  u64 : QWord; // 2^64 (0 bis 18446744073709551615)
  s64 : Int64; // 2^63 (-9223372036854775808 bis 9223372036854775807)
  F32 : Single; // 32bit floating point number
  F64 : Double; // 64bit floating point number
  C   : Char; // ASCII 2^8 (0 - 255)
  AStr  : AnsiString; // Array of chars - geht bis max U32 (Max signiert oder unsigniert??)
  WC  : WideChar; // 2^16 (0 - 65535)
  WStr : WideString; // Array of widechars - geht bis max U32 (Max signiert oder unsigniert??), aber nur halb so lang wie AnsiString (UNICODE 16bit)
  S : String; // Entweder Ansi oder WideString - abhängig von der Projekteinstellung
  B : Boolean; // 2^8 (False, True - kann nicht null werden!)
  D : TDateTime; // Datumstyp (2^64 - muss man hier erstmal kennen)
begin
  writeln(low(u32));
  writeln(low(s32));
  writeln(low(u64));
  writeln(low(s64));
  F32 := 5.0;
  F64 := 5000.3123;
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

