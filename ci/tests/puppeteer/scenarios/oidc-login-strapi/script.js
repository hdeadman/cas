const puppeteer = require("puppeteer");
const cas = require("../../cas.js");

(async () => {
    const browser = await puppeteer.launch(cas.browserOptions());
    const page = await cas.newPage(browser);
    await cas.gotoLogin(page);
    await cas.loginWith(page);
    await cas.assertCookie(page);
    await cas.assertPageTitle(page, "CAS - Central Authentication Service Log In Successful");
    await cas.goto(page, "http://localhost:1337/api/connect/cas");
    await page.waitForTimeout(2000);
    const id_token = await cas.assertParameter(page, "id_token");
    await cas.log(id_token);
    await browser.close();
})();
