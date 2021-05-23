const main = require('puppeteer');

const url = process.argv[2];
(async () => {
    let browser = null;

    let page = null;
    try {
        browser = await main.launch({
            headless: true,
            args: ['--no-sandbox']
        });
        page = await browser.newPage();

        await page.setViewport({width: 1920, height: 5000});
        await page.goto(url, {waitUntil: 'networkidle0'});

        // await page.screenshot({path: `./${Date.now()}.png`,});

        const html = await page.content(); // serialized HTML of page DOM.

        console.log(html);
    } catch (error) {
        console.log('zfoo_error', error);
    } finally {
        if (page != null) {
            await page.close();
        }
        if (browser != null) {
            await browser.close();
        }
    }
})();
