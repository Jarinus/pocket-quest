const {app, BrowserWindow} = require("electron");
const path = require("path");
const url = require("url");
const fs = require("fs");

const absoluteIndexHtmlLocation = path.join(__dirname, "..", "index.html");

if (!fs.existsSync(absoluteIndexHtmlLocation)) {
    console.error("Please run \"npm run build\".");

    app.quit();
}

let window;

const createWindow = function () {
    "use strict";

    window = new BrowserWindow({
        width: 800,
        height: 600
    });

    window.loadURL(url.format({
        pathname: absoluteIndexHtmlLocation,
        protocol: "file:",
        slashes: true
    }));

    window.on("closed", function () {
        window = null;
    });
};

app.on("ready", createWindow);

app.on("window-all-closed", function () {
    "use strict";
    if (process.platform !== "darwin") {
        app.quit();
    }
});

app.on("activate", function () {
    "use strict";
    if (window === null) {
        createWindow();
    }
});
