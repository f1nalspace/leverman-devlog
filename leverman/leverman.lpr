program leverman;

uses win32_leverman;

// Private
type
  TMyNewType = UInt32;
var
  X, Y, Z : Integer;
  MyVar : Integer;
begin
  publicMyValue := 24;
  MyVar := 42;
  WriteLn('Hallo Welt');
  WriteLn(publicMyValue);
  ReadLn;
end.

