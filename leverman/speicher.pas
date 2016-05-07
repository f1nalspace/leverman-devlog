unit speicher;

{$mode delphi}

interface

// Heap
var
  GlobaleVariable : Int32;

procedure meinSpeicher;

implementation

uses
  SysUtils;

procedure meinSpeicher;
// Stack
var
  Foo : Int32;     // 4 byte
  Bar : Boolean32; // 4 byte
  SA  : array [1..128] of Byte; // 128 byte
  ZeigerAufFoo : ^Int32; // Zeiger auf irgendeinen Speicherbereich von 4 byte größe - da Int32
  // Jeder Zeiger egal welcher Typ hat immer in 64-bit 8 byte
  Arr : array [0..9] of Int32;
  ZeigerAufInt32 : PInt32; // ^Int32;
  ZeigerAufInt16 : PInt16; // ^Int16;
  ZeigerAufByte  : PByte; // ^Byte;
begin
  Foo := 42;
  Bar := false;

  // Gibt Speicheraddresse von Foo aus
  writeln(Int64(Addr(Foo)));
  writeln(IntTOHex(Int64(Addr(Foo)), 8));

  // Referenzieren (Zeiger auf die Adresse von Foo)
  ZeigerAufFoo := @Foo;
  // Deferenzieren
  ZeigerAufFoo^ := 24; // -> Foo
  WriteLn(Foo);
  WriteLn(ZeigerAufFoo^);

  // Gibt Speicheraddresse von ZeigerAufFoo aus
  writeln(IntTOHex(Int64(Addr(ZeigerAufFoo)), 8));
  // Gibt Speicheraddresse von ZeigerAufFoo aus
  writeln(IntTOHex(Int64(Addr(ZeigerAufFoo^)), 8));

  Arr[0] := 33;
  Arr[1] := 42;
  writeln(Arr[1]);
  ZeigerAufInt32 := @Arr; // Referenziert auf den ersten Eintrag im Array - index 0 (Der Kompiler macht das [0] automatisch)
  ZeigerAufInt32 := @Arr[0]; // Referenziert auf den ersten Eintrag im Array - index 0
  ZeigerAufByte := @Arr[0];
  ZeigerAufInt16 := @Arr[0];
  WriteLn(ZeigerAufInt32[1]); // Deferenzierung passiert hier automatisch - wenn wir mit dem Index arbeiten
  WriteLn(PInt32(ZeigerAufInt32 + 1)^);
  WriteLn(PInt16(ZeigerAufInt16 + 2)^);
  WriteLn(PInt32(ZeigerAufByte + 4)^);
end;

end.

