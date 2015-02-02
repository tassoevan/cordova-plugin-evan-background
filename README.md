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
cordova.Evan.goHome(function() {
  console.log('success');
}, function() {
  console.log('fail');
});
```