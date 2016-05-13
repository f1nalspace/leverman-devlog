unit win32_lman;

{$mode delphi}

interface

procedure Win32EntryPoint(AppInstance, PrevInstance: TFPResourceHMODULE;
  CmdLine: PChar; CmdShow: integer);

implementation

uses
  Windows;

function Win32WindowProc(Window: HWND; Msg: UInt; wP: WPARAM;
  lP: LPARAM): LRESULT; stdcall;
begin
  Result := DefWindowProc(Window, Msg, wP, lP);
end;

procedure Win32EntryPoint(AppInstance, PrevInstance: TFPResourceHMODULE;
  CmdLine: PChar; CmdShow: integer);
var
  WindowClass: TWNDCLASS;
  Window: HWND;
  Running: boolean;
  Msg : TMsg;
begin
  FillChar(WindowClass, SizeOf(WindowClass), #0);
  WindowClass.lpszClassName := 'Leverman_Window';
  WindowClass.lpfnWndProc := @Win32WindowProc;
  WindowClass.hInstance := AppInstance;

  if RegisterClass(@WindowClass) <> 0 then
  begin
    Window := CreateWindowEx(0, WindowClass.lpszClassName, 'Leverman',
      WS_OVERLAPPEDWINDOW or WS_VISIBLE, CW_USEDEFAULT, CW_USEDEFAULT,
      CW_USEDEFAULT, CW_USEDEFAULT, 0, 0, AppInstance, nil);
    if Window <> 0 then
    begin
      Running := True;
      while Running do
      begin
        while PeekMessage(@Msg, Window, 0, 0, PM_REMOVE) do
        begin
          if Msg.message = WM_QUIT then
            Running := false;
          TranslateMessage(@Msg);
          DispatchMessage(@Msg);
        end;
      end;
    end;
  end;
end;

end.
