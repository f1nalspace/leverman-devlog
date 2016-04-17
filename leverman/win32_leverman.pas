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

// Typ definitionen
type
  TU32 = LongWord; // Das ist weiterhin ein LongWord - TU32 ist somit nur nen Alias
  TU32Own = type LongWord; // Eigener 32-bit ganzzahl typ - nicht mehr direkt ein LongWord

  // struct / mehrere variablen in einem block
  // Hier benötigen wir einfach ne deklarierte variable
  // Gruppierung von Variablen
  // Speicherbereich ist abhängig vom Inhalt
  // Alles ist public!
  TMeinVector4f = record
    X, Y, Z : Single;
    W : Single;
  end;

  // Dieser record ist auf jedenfall immer 128bit groß = 32 byte
  TMein128BitTyp = record
    X, Y, Z, W : Single; // 32bit * 4
  end;

  // Wenn wir 64-bit kompilieren, dann wird delphi automatisch records auf 64 bit vergrößern wenn nötig
  TMein32BitTyp = record
    X : Single; // 32bit
  end;
  // Record wird aufgefüllt damit 64bit Padding erreichtbar - ihr müsst nicht wissen was das genau bedeutet
  TMein32BitTypMitPadding = record
    X : Single; // 32bit
    Padding : Single; // 32bit
  end;
  // Ich will kein padding - kann ich haben
  TMein32BitTypOhneAutomatischesPadding = packed record
    X : Single; // 32bit
  end;

  // Alles ist public!
  TMeinObjektTyp = object
    X, Y, Z : Single;
    W : Single;
  end;

  // Typisch für ne Klasse - wir benötigen eine Instanz!
  // Es gibt klassen - aber wir benötigen diese nicht unbedingt.
  // Klassen sind restmal nicht wichtig.
  // In Klassen gibt es alles - private, public, protected, published
  // Kommt später
  TMeineKlasse = class
    X, Y, Z : Single;
    W : Single;
  end;

implementation

var
  X: string = 'Mein text';
  Y, Z: integer; // Mehrere Zuweisungen für gleichen Typ ohne direkte initialiserung
  Mein32BitGanzZahl : TU32 = 100; // Compiler macht daraus automatisch ein LongWord
  MeineAndere32BitGanzzahl : TU32Own = 100;
  Speicher : TMeinVector4f;

procedure HalloWelt3(); forward;
procedure VariablenTypen(); forward;

procedure ZugriffaufMeinSPeicher;
begin
  Speicher.X := 5;
end;

procedure NeueWasAuchImmer;
var
  U64 : QWord;
  C : Cardinal;
begin
  U64 := High(QWord);
  Mein32BitGanzZahl := U64;
  // Warum geht das???
  MeineAndere32BitGanzzahl := U64;

  // Boolean nicht kompatibel mit Ganzzahl
  //Mein32BitGanzZahl := true;
  //MeineAndere32BitGanzzahl := true;
end;

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
  S : String; // Entweder Ansi oder WideString - abhängig von der PLatformer bzw. einer Compiler direktive
  B : Boolean; // 2^8 (False, True - kann nicht null werden!)
  D : TDateTime; // Datumstyp (2^64 - muss man hier erstmal kennen)
var
  RIchtigerU64 : UInt64; // Immer UInt für unsigned benutzen - wichtig, das kann in anderen Delphi Versionen anderst sein
  I : Int32;
var
  // Bool-Typen - Für alignment/padding gut
  BB : ByteBool; // 8-bit Bool
  WB : WordBool; // 16-bit Bool
  LB : LongBool; // 32-bit Bool
begin
  writeln(low(u32));
  writeln(low(s32));
  writeln(low(u64));
  writeln(low(s64));
  F32 := 5.0 + 3.5;
  F64 := 5000.3123;



  BB := true;
  BB := ByteBool(1);

  WB := True;

  LB := false;
end;

// By Value (Kopie wird gemacht)
function UnsereInc(Value : Integer) : Integer;
begin
  Value := 4; // Value kann  überschrieben werden
  Result := Value + 1;
end;

// Constant (Kopie ?)
function UnsereInc2(const Value : Integer) : Integer;
begin
  //Value := 4; // Value kann nicht überschrieben werden
  Result := Value + 1;
end;

// By Referenz (Keine Kopie)
// Argumente können auch direkt initialisiert werden und sind damit optional!
function UnsereInc3(var Value : Integer; By : Integer = 1) : Integer;
begin
  Value := Value + By; // Value kann direkt verändert werden, weil referenz auf Variable
  Result := Value;
end;

procedure Bedingungen();
var
  R : Boolean;
  X : Integer;
begin
  // Größer und Nicht Gleich
  R := (1 > 10) and (2 <> 10);

  // == gibt es nicht, da := gleich Zuweisung
  //if (R == true) then
  if R = true then
  begin

  end;

  if (R = true) or (4 < 10) then
    WriteLn('Bar')
  else
    Writeln('Foo');

  // Vor einem Else kein Strichpunkt
  if false then
  begin
    writeln('xxx');
    writeln('lol');
  end
  else
  begin
    R := false;
  end;

  // Geth nciht in Delphi
  //if (10) begin
  //
  //end;

  X := 42;

  // Inkrementierung von 1 auf Variablen (Ändert variable direkt)
  Inc(X);

  // Dekrementierung von 1 auf Variablen (Ändert variable direkt)
  Dec(X);

  // Freepascal unterstützt += Operatoren sofern eingegestellt in Konfiguration += (Delphi nicht!)
  X += 5;

  // Bitoperatoren
  // Rechts Verschiebung = nicht >> sondern shr
  // Links Verschiebung = nicht << sondern shl
  // And operator = nicht & sondern and
  // Or operator = nicht | sondern or
end;

procedure Schleifen();
var
  I : Integer;
begin

  // Hochzählen von 0 bis 9 exakt
  // For hat kein Step - sofern ich weiß oder doch???
  // for (i = 0; i < 10; ++i) <- Das selbe wie
  for I := 0 to 9 do
  begin

  end;

  // Runterzählen von 10 bis 1
  // For hat kein Step - sofern ich weiß oder doch???
  for I := 10 downto 1 do
  begin

  end;

  // while schleife
  I := 0;
  while i < 10 do
  begin
    // i++ geht nicht
    //I++;
    i += 1;
    //Inc(I);
  end;

  // do while schleife
  i := 1;
  repeat
    Inc(i);
  until i > 10;
end;

procedure TypDefinitionen;
begin

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

