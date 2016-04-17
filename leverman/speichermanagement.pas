unit speichermanagement;

{$mode delphi}

interface

procedure WirLernenPointer();

implementation

// Uses kann man auch in implementation defineren!
uses
  SysUtils;

procedure WirLernenPointer();
type
  TBlubber = record
    X : Integer;
    Y : Integer;
  end;
  PBlubber = ^TBlubber;
var
  Foo : Integer;
  PointerAufFoo : ^Integer;
  AddresseVonPointerAufFoo : Integer;
  HexAdresse : String;
  BlubberPtr : PBlubber;

begin
  Foo := 42;

  // Ein Pointer ist erstmal nur eine Addresse auf einen Speicher mit 32bit oder 64bit (je nach platform)

  // 8 Byte ist ein Pointer groß in 64 bit - egal was für ein typ er ist
  writeln(SizeOf(PointerAufFoo));

  // Mit Pointer was machen

  // 0. Pointer auf etwas referenzieren lassen
  // @ liefert die adresse zurück
  PointerAufFoo := @Foo;

  // 1. Addresse ausgeben mit explitem Typecast
  AddresseVonPointerAufFoo := Integer(PointerAufFoo);
  HexAdresse := IntToHex(AddresseVonPointerAufFoo, 8);
  writeln(AddresseVonPointerAufFoo);
  writeln(HexAdresse);

  // 3. Explizite Dereferenzierung (Wert ausgeben)
  writeln(PointerAufFoo^); // 42
  Foo := 10;
  writeln(PointerAufFoo^); // 10

  // 4. Pointer wert verändern + vorheriger dereferenzierung
  PointerAufFoo^ := 12;
  writeln(PointerAufFoo^);
  writeln(foo);

  // 5. oder wir erzeugen einen neuen speicherbereich abhängig vom typ für den pointer/wert
  new(PointerAufFoo);
  // Ab hier ist foo nicht mehr relevant!!!
  writeln(IntToHex(Integer(PointerAufFoo), 8));

  // Nun haben wir ein memory leak...

  // jetzt nicht mehr, da wir den speicher mit dispose freigeben
  dispose(PointerAufFoo);

  writeln(PointerAufFoo^);

  // Wieviel applikationsspeicher können wir maximal kriegen???
  writeln(BlubberPtr^.Y);
  new(BlubberPtr);
  BlubberPtr^.Y := 5553443;
  writeln(BlubberPtr^.Y);
  dispose(BlubberPtr);
end;

procedure ArraysUndEnumerationen;
type
  // Aufzählung mit Ordinalen beginnend bei 0
  TUnsereAufzaehlung = (Eins, Zwei, Drei);
  TEigenerZaehlerEnum = (EinsNeu = 1, ZweiX = Integer(Drei)); // 1 und 2
var
  Enum1 : TUnsereAufzaehlung;
  Enum2 : set of TUnsereAufzaehlung;
  IE    : TUnsereAufzaehlung;
  Bit : Integer;
  IntSet : set of Byte;

  // Dynamisches 1D-array
  DynamischesByteArray : array of Byte; // n-Einträgen
  DynamischesTabellenArray : array of array of Byte; // n x m-Einträgen

  // Statisches 1D-array
  StatischesByteArray : array [0..9] of Byte; // 10 Einträgen
  StatischeTabelleArray : array [1..5] of array [0..99] of Boolean;

  // Pointer auf unser Statisches Byte Array
  PointerAufArray : ^Byte;
  IntArray : array [0..10] of Integer;
  PointerAUfIntArray : PInteger;
begin
  Enum1 := Eins;
  Enum1 := Zwei;
  writeln(Enum1);
  writeln(Ord(Enum1));
  Enum2 := [Eins, Drei];
  for IE := Eins to Drei do
  begin
    writeln(IE);
  end;
  for IE := Eins to Drei do
  begin
    if IE in enum2 then
      writeln(IE);
  end;
  writeln(ord(low(TEigenerZaehlerEnum)));
  Enum2 := [];
  Enum2 := Enum2 + [Zwei];

  Bit := 1 or 4;
  if (Bit and 4) = 4 then
  begin

  end;

  StatischesByteArray[0] := 10;
  StatischesByteArray[4] := 42;

  // Liefert nen Zugriffsverletzungsfehler, weil wir auf Adresse 0x0 schreiben möchten die nicht erlaubt ist
  //DynamischesByteArray[0] := 10;

  SetLength(DynamischesByteArray, 5);
  DynamischesByteArray[0] := 10;
  SetLength(DynamischesByteArray, 30);
  DynamischesByteArray[29] := 222;

  // Warum hier keine Zugriffsverletzung???
  DynamischesByteArray[40] := 122;

  // Zweidimensionales array initialisieren
  SetLength(DynamischesTabellenArray, 4, 8);

  // Referenz auf Statisches Byte Array zum ersten Eintrag
  PointerAufArray := @StatischesByteArray;
  // Ersten eintrag dereferenzieren und ausgeben
  writeln(PointerAufArray^);

  // Referenzieren auf das 4te Element und ausgeben
  PointerAufArray := @StatischesByteArray[4];
  writeln(PointerAufArray^);

  // Direkt auf einen Eintrag springen (identisch zum vorherigen - aber anderer weg dahin!)
  //PointerAufArray := @StatischesByteArray[4];
  PointerAufArray := PByte(@StatischesByteArray + 4);  // Immer 4 byte???
  // FF AA BB CC 2A EE
  writeln(PointerAufArray^);

  IntArray[0] := 34;
  IntArray[1] := 11;
  IntArray[2] := 998;

  PointerAUfIntArray := PInteger(@IntArray + SizeOf(Integer));
  writeln(PointerAUfIntArray^);

  writeln('-----');
end;

initialization
  ArraysUndEnumerationen
end.

