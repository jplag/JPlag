import { app, BrowserWindow, dialog, ipcMain } from "electron";
import { spawn, ChildProcessWithoutNullStreams } from "node:child_process";
import path from "node:path";
import log from "electron-log/main";

const JPLAG_ARGS: string[] = process.argv.slice(process.defaultApp ? 2 : 1);

let win: BrowserWindow | null = null;
let jarProcess: ChildProcessWithoutNullStreams | null = null;

ipcMain.handle("dialog:open", async (event, options = {}) => {
  const url = event.senderFrame?.url ?? "";
  if (!url.startsWith("http://localhost:")) {
    throw new Error("Not allowed");
  }

  const parent = BrowserWindow.fromWebContents(event.sender) ?? undefined;
  const properties: Array<"openFile" | "openDirectory" | "multiSelections"> = [
    options.directory ? "openDirectory" : "openFile",
  ];
  if (options.multiple) properties.push("multiSelections");

  const result = await dialog.showOpenDialog(parent!, {
    properties,
    filters: options.filters,
  });
  return result.canceled ? [] : result.filePaths;
});

function getJarPath(): string {
  return app.isPackaged
    ? path.join(process.resourcesPath, "jplag.jar")
    : path.join(app.getAppPath(), "resources", "jplag.jar");
}

function createWindow(): void {
  win = new BrowserWindow({
  width: 800,
  height: 600,
  useContentSize: true,
  webPreferences: {
    preload: path.join(__dirname, "preload.cjs"), // .cjs, as the Vite plugin requires
    contextIsolation: true,
    nodeIntegration: false,
  },
});
  win.setMenuBarVisibility(false);
  win.title = "JPlag";
  win.on("closed", () => {
    win = null;
  });

  const jarPath = getJarPath();
  log.info(`Starting JPlag server from: ${jarPath}`);/*
  jarProcess = spawn("java", ["-jar", jarPath, ...JPLAG_ARGS]);

  // stdout: look for the server URL and load it
  jarProcess.stdout.on("data", (data: Buffer) => {
    const urlMatch = data.toString().match(/http:\/\/localhost:[0-9]+/);
    if (urlMatch && win) {
      void win.loadURL(urlMatch[0]);
    }
  });

  jarProcess.stderr.on("data", (data: Buffer) => {
    log.error(`stderr: ${data.toString()}`);
    app.quit();
  });*/

  if (win) {
    void win.loadURL('http://localhost:5173')
  }
}

app.whenReady().then(createWindow);

app.on("window-all-closed", () => {
  if (process.platform !== "darwin") app.quit();
});

app.on("will-quit", () => {
  jarProcess?.kill();
});
