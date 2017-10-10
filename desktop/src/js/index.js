const Elm = require('../elm/PocketQuest.elm');

require('../scss/index.scss');

const node = document.getElementById('app');
Elm.PocketQuest.embed(node);
