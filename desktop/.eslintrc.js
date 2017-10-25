module.exports = {
    "env": {
        "browser": true,
        "node": true,
    },
    "extends": [
        "eslint:recommended",
        "plugin:react/recommended"
    ],
    "plugins": [
        "react"
    ],
    "parserOptions": {
        "sourceType": "module",
        "ecmaFeatures": {
            "jsx": true
        }
    }
};