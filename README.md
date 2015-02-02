Cordova Evan Plugin Background
==============================

Prevents Cordova apps running in Android devices from going to sleep while in background.

Based on [Cordova Background Plug-in](https://github.com/katzer/cordova-plugin-background-mode)

Install
-------

```sh
cordova plugin add https://github.com/tassoevan/cordova-plugin-evan-background.git
```

Usage
-----

```js
// Configure notification on background
cordova.Evan.Background.configure({
    title: "My App",
    ticker: "My App is still running",
    text: "Doing heavy tasks"
});

// Enable background mode
cordova.Evan.Background.enable();

// Disable background mode
cordova.Evan.Background.disable();
```