program leverman;

uses SysUtils; // Include/Imports

// Einzelzeilen-Kommentar

{ Mehrzeilige
  Kommentare }

procedure HalloWelt();
begin
  WriteLn('Hallo Welt');
end;

function Add(A : Integer; B : Integer) : Integer;
begin
  Result := A + B;
  Result := B + A;
  // Kein return!
end;

function add3(a, b, C : inTegeR) : Integer;
begin
  Result := A + B + C;
  // Kein return!
end;

begin
  writelN('Hallo Welt'); // println
  HalloWelt();
  writeln(42);
  writeln(IntToStr(41 + 1));
  writeln(IntToStr(Add(41, 1)));
  writeln(IntToStr(Add3(38, 1, 3)));
  readln();
end.
