# Pocket Quest Game Server

## Getting Started
Using the Game server requires some configuration:
* Execute `npm i`
* Create a `conf` directory in the project root.
* Within the Firebase console, navigate to Project Settings.
* In the 'General' tab, download the latest config file.
* Move the downloaded `google-services.json` to the `./conf` directory.
* In the 'Service Accounts` tab, download another config by clicking on
'Generate New Private Key'.
* Move this file to `./conf` and rename it to
`firebase-service-account.json`.

## Running the Game Server
Execute `npm start`
