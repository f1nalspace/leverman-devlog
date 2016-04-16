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

end.

