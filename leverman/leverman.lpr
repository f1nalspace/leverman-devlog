program leverman;

uses
  SysUtils,
  win32_leverman; // Include/Imports

// Einzelzeilen-Kommentar

{ Mehrzeilige
  Kommentare }

// Strg+D = Automatische Code-Formatierung

  procedure HalloWelt();
  begin
    WriteLn('Hallo Welt');
  end;

  function Add(A: integer; B: integer): integer;
  begin
    Result := A + B;
    Result := B + A;
    // Kein return!
  end;

  function add3(a, b, C: integer): integer;
  begin
    Result := A + B + C;
    // Kein return!
  end;

  function add3V(a, b, C: integer): integer;
  var
    R: integer = 100;
    // Normalerweise ist das nicht erlaubt in Delphi, der Freepascal-Compiler erlaubt das aber wohl...
  begin
    R := A + B + C;
    Result := R;
  end;

  // Kein Global/Public/Private was auch immer
  // Sondern nur hier in der Projektdatei sichtbar
var
  C: integer; // Keine Garantie das Integer mit 0 initialisiert wird
  C42: integer = 42; // Direkte zuweisung nach deklaration geht hier!

begin
  // Zuweisung immer mit :=
  C := 42;
  // C : inTegeR; (Nicht zulässig!)
  Writeln(c);
  writelN('Hallo Welt'); // println
  HalloWelt();
  writeln(42);
  writeln(IntToStr(41 + 1));
  writeln(IntToStr(Add(41, 1)));
  writeln(IntToStr(Add3(38, 1, 3)));
  HalloWelt2();
  win32_leverman.HalloWelt2();
  writeln(D42);
  //writeln(X); // Geht nicht, da in win32_leverman und nicht global
  readln();
end.
