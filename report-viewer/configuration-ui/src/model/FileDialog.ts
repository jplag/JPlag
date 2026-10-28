declare global {
  interface Window {
    electronAPI?: {
      openDialog(options?: {
        directory?: boolean;
        multiple?: boolean;
        filters?: { name: string; extensions: string[] }[];
      }): Promise<string[]>;
    };
  }
}

export async function getFolderName(): Promise<string | null> {
  if (window.electronAPI) {
    const [path] = await window.electronAPI.openDialog({ directory: true });
    return path ?? null;
  }
  return '123';
}