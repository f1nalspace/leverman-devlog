program leverman;

{$mode delphi}

uses
  SysUtils,
  win32_leverman;

type
  T255ByteArray = array [0..255] of Byte;

var
  U8 : Byte; // 0 to 255
  S8 : ShortInt; // -127 to 127
  U16 : Word; // 0 to 65535
  S16 : SmallInt; // -32768 to 3276
  U32 : LongWord; // 0 to 4294967295
  S32 : LongInt; // -2147483648 to 2147483647
  U64 : QWord; // 0 to 18446744073709551615
  S64 : Int64; // -9223372036854775808 - 9223372036854775807
  F32 : Single; // 32-bit floating point number
  F64 : Double; // 64-bit floating point number
  C   : Char; // 8-bit buchstabe (Identisch mit Byte)
  Str : String; // 8-bit dynamisch (bis zu 2 GB)
  SStr : ShortString; // 8-bit dynamisch (256 buchstaben)
  WStr : WideString; // 16-bit dynamisch (bis zu 2 GB)
  WC : WideChar; // 16-bit (Identisch mit Word)
  B : Boolean; // 8-bit (false, true)

  Arr1DByte : array [0..9] of Byte; // 10 Byte einträge (Statisches 1D-array)
  Arr2DByte : array [0..9] of array [0..1] of Byte; // 10*2 Byte einträge (Statisches 2D-array)
  Arr2DByte_Special : array [0..9] of T255ByteArray; // 10*256 Byte einträge (Statisches 2D-array)

  DynArr1DInt : array of Integer;
  DynArr2DInt : array of array of Integer;
begin
  b := false;
  Arr1DByte[0] := 128;
  U16 := 40 + 2;
  U8 := $AB;
  Str := 'HALLO WELT' + '_' + IntToStr(45);
  writeln(IntToStr(Low(DynArr1DInt)) + ' to ' + IntToStr(High(DynArr1DInt)));
  writeln(Length(DynArr1DInt));

  SetLength(DynArr1DInt, 3);
  writeln(High(DynArr1DInt));

  // = ist equals
  if U16 = 42 then
  begin
    WriteLn('jaaaaa');
  end;

  // <> ist not equals
  if U8 <> 42 then
    WriteLn('nein :-(')
  else
    WriteLn('Warum auch immer man das semikolon weglassen muss vor dem else...');

  // AND ist and oder &&
  // Klammern sind wichtig bei verkettung von Konditionen
  if (U8 <> 42) and (B = false) then
  begin
    WriteLn('nein :-(');
    WriteLn('NOOOO');
  end
  else if true then
  begin

  end
  else
  begin

  end;

  // Schleifen
  for s32 := 0 to 9 do
  begin
    arr1DByte[S32] := 2 * s32;
    writeLn(Arr1Dbyte[S32]);
  end;

  for s32 := 9 downto 0 do
  begin
    arr1DByte[S32] := 2 * s32;
    writeLn(Arr1Dbyte[S32]);
  end;

  while true do
  begin
    break;
  end;

  repeat
    WC := WideChar(32455);
  until false;

  readln();
end.

