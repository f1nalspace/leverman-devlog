program leverman;

uses
  SysUtils,
  win32_leverman;

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
  B : Boolean; // 8-bit (false, true) KEIN NULL!!!!
begin
  b := false;
  U16 := 40 + 2;
  U8 := $AB;
  Str := 'HALLO WELT' + '_' + IntToStr(45);
  writeln(High(UInt64));
  readln();
end.

