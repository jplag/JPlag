import { contextBridge, ipcRenderer } from "electron";

export interface OpenDialogOptions {
  directory?: boolean;
  multiple?: boolean;
  filters?: { name: string; extensions: string[] }[];
}

contextBridge.exposeInMainWorld("electronAPI", {
  openDialog: (options: OpenDialogOptions = {}): Promise<string[]> =>
    ipcRenderer.invoke("dialog:open", options),
});