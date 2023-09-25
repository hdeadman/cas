const puppeteer = require('puppeteer');
const assert = require('assert');
const cas = require('../../cas.js');

(async () => {
    const browser = await puppeteer.launch(cas.browserOptions());
    const page = await cas.newPage(browser);
    await cas.gotoLogin(page);
    await cas.loginWith(page);
    await cas.goto(page, "https://localhost:8443/cas/actuator/health");
    await page.waitForTimeout(1000);
    await cas.doGet("https://localhost:8443/cas/actuator/health",
        res => {
            assert(res.data.components.redis !== null);
            assert(res.data.components.memory !== null);
            assert(res.data.components.ping !== null);

            assert(res.data.components.redis.status !== null);
            assert(res.data.components.redis.details !== null);

        }, error => {
            throw error;
        }, { 'Content-Type': "application/json" });
    await browser.close();
})();
