unit arrays;

{$mode delphi}

interface

procedure StatischeArrays();
procedure DynamischeArrays();

implementation

procedure StatischeArrays();
var
  A1 : array [3..9] of Int32;
  A2 : array [3..9, 1..2] of Int32;
  A3 : array [3..9] of array [1..2] of Int32;
  AAA : array ['a'..'z'] of Char;
  A4_1 : Int32; A4_2 : Int32; A4_3 : Int32; A4_4 : Int32; A4_5 : Int32;
begin
  A1[3] := 42;
  writeln(A1[3]);
  A1[0] := 42; // Der Compiler kümmert sich wohl darum...
  writeln(A1[0]);
  A2[4][1] := 11;
  A2[4,2] := 11;
  A3[4,1] := 22;
  A3[3][1] := 22;
end;

procedure DynamischeArrays();
var
  D1 : array of Int32; // Beginnen immer bei 0
  D2 : array of array of Boolean;
  D3 : array of array [0..1] of Int32;
begin
  SetLength(D1, 100);
  D1[3] := 42;
  writeln(D1[3]);
  writeln(D1[55]);
  SetLength(D1, 10);
  writeln(D1[3]);

  SetLength(D2, 10, 3);
  D2[0][3] := true;
end;

end.

