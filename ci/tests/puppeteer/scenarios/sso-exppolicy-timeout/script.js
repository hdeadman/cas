const puppeteer = require('puppeteer');
const cas = require('../../cas.js');

(async () => {
    const browser = await puppeteer.launch(cas.browserOptions());
    const page = await cas.newPage(browser);
    await cas.goto(page, "https://localhost:8443/cas/login");
    await cas.loginWith(page);
    await cas.assertCookie(page);

    for (let i = 0; i < 3; i++) {
        await cas.log(`Attempt #${i}: waiting for timeout to complete...`);
        await page.waitForTimeout(1000);
        await cas.goto(page, "https://localhost:8443/cas/login?service=https://apereo.github.io");
        await cas.assertTicketParameter(page);
        await cas.goto(page, "https://localhost:8443/cas/login");
        await cas.assertCookie(page);
    }
    await page.waitForTimeout(4000);
    await cas.goto(page, "https://localhost:8443/cas/login");
    await cas.assertCookie(page, false);
    await browser.close();
})();
