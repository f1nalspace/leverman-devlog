program leverman;

{$MODE delphi}{$H+}

type
  U32 = UInt32;
  U64 = UInt64;
  S32 = Int32;

procedure HalloWelt();
begin
  WriteLn('Hallo Welt');
end;

function LengthSq(X, Y : Integer) : Integer;
var
  x2 : Integer;
begin
  x2 := X * X;
  Result := x2 + Y * Y;
end;

procedure PascalTypes;
var
  IX64 : Int64; // 64 bit integer (-9223372036854775807 to 9223372036854775807)
  UIX64 : QWord; // 64 bit unsigned integer (0 to 18446744073709551615)
  I : Integer;   // 32 bit integer (-2147483647 to 2147483647)
  UI : Cardinal; // 32 bit unsigned integer (0 to 4294967294)
  SMI : SmallInt; // 16 bit integer (-32768 to 32767)
  WI : Word; // 16 bit unsigned integer (0 to 65535)
  SHI : ShortInt; // 8 bit integer (-127 to 127)
  BY : Byte; // 8 bit unsigned integer (0 - 255)
  B : Boolean; // true, false
  F : Single; // 32bit fließkomma zahl
  D : Double; // 64bit fließkomma zahl
  S : String; // Zeichenkette
  C : Char; // 0 - 255 ASCII (byte nur als buchstabe)
begin
  WriteLn(High(QWord));
end;

begin
  PascalTypes;
  HalloWelt();
  WriteLn(LengthSq(10, 20));
  ReadLn;
end.

