var config = require('semantic-release-preconfigured-conventional-commits');
config.plugins.push(
    [
        "@semantic-release/github",
        {
            "assets": [
                { "path": "app/build/**/*signed.apk" },
            ]
        }
    ],
    "@semantic-release/git",
)
module.exports = config
