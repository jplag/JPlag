import type { ForgeConfig } from "@electron-forge/shared-types";
import { VitePlugin } from "@electron-forge/plugin-vite";
import { MakerSquirrel } from "@electron-forge/maker-squirrel";
import { MakerZIP } from "@electron-forge/maker-zip";
import { MakerDeb } from "@electron-forge/maker-deb";
import { MakerRpm } from "@electron-forge/maker-rpm";

const config: ForgeConfig = {
  packagerConfig: {
    // Ends up at process.resourcesPath/jplag.jar
    extraResource: ["./resources/jplag.jar"],
  },
  makers: [
    new MakerSquirrel({}),
    new MakerZIP({}, ["darwin"]),
    new MakerRpm({}),
    new MakerDeb({}),
  ],
  plugins: [
    new VitePlugin({
      build: [
        {
          entry: "src/main.ts",
          config: "vite.main.config.ts",
          target: "main",
        },
        { entry: "src/preload.ts", config: "vite.preload.config.ts", target: "preload" }
      ],
      renderer: [], // no renderer: the window loads the JPlag server URL
    }),
  ],
};

export default config;
