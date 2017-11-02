const scheduler = require('node-schedule');

scheduler.scheduleJob('*/1 * * * * *', () => {
    console.log('game tick');
});
