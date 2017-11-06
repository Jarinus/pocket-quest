import * as admin from 'firebase-admin';
import * as fs from 'fs';
const firebaseConfigurationURL = 'conf/google-services.json';
const firebaseServiceAccountURL = '../../conf/firebase-service-account.json';
const firebaseServiceAccount = require(firebaseServiceAccountURL);

export function init() {
    const configurationData = readConfigurationFile(firebaseConfigurationURL);

    if (!firebaseServiceAccount || !configurationData) {
        throw new Error('Firebase configuration file not found at: ' + firebaseServiceAccountURL);
    }

    admin.initializeApp({
        credential: admin.credential.cert(firebaseServiceAccount),
        databaseURL: extractDatabaseURL(configurationData)
    });
}

/**
 * @param {string} relativeConfigurationURL
 */
function readConfigurationFile(relativeConfigurationURL) {
    const configurationFile = fs.readFileSync(relativeConfigurationURL, 'utf8');
    return JSON.parse(configurationFile);
}

/**
 * @param {object} data The configuration data
 * @param {object} data.project_info The configuration data's project info
 * @param {string} data.project_info.firebase_url The configuration data's project Firebase URL
 */
function extractDatabaseURL(data) {
    if (!data.project_info || !data.project_info.firebase_url) {
        throw new Error('Invalid firebase configuration');
    }

    return data.project_info.firebase_url;
}


