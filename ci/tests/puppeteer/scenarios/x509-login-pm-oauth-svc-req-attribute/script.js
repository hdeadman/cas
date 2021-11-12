const puppeteer = require('puppeteer');
const assert = require('assert');
const cas = require('../../cas.js');
const fs = require('fs');

(async () => {
    let browser = await puppeteer.launch(cas.browserOptions());
    let page = await cas.newPage(browser);
    await page.goto("https://localhost:8443/cas/login");
    await cas.loginWith(page, "aburr", "P@ssw0rd");
    await cas.assertTicketGrantingCookie(page);
    await cas.assertInnerText(page, '#content div h2', "Log In Successful");
    const attributesldap = await cas.innerText(page, '#attribute-tab-0 table#attributesTable tbody');
    assert(attributesldap.includes("aburr"))
    assert(attributesldap.includes("someattribute"))
    assert(attributesldap.includes("ldap-dn"))
    await browser.close();

    browser = await puppeteer.launch(cas.browserOptions());
    page = await cas.newPage(browser);

    await page.setRequestInterception(true);
    let args = process.argv.slice(2);
    let config = JSON.parse(fs.readFileSync(args[0]));
    assert(config != null)

    console.log(`Certificate file: ${config.trustStoreCertificateFile}`);

    const certBuffer = fs.readFileSync(config.trustStoreCertificateFile);
    const certHeader = certBuffer.toString().replace(/\n/g, " ");

    page.on('request', request => {
        let data = {
            'method': 'GET',
            'headers': {
                ...request.headers(),
                'ssl-client-cert-from-proxy': certHeader
            },
        };
        request.continue(data);
    });

    await page.goto("https://localhost:8443/cas/login?service=https://github.com");
    await page.waitForTimeout(5000)
    await cas.assertTicketParameter(page)
    await page.goto("https://localhost:8443/cas");
    await cas.assertTicketGrantingCookie(page);

    await cas.assertInnerText(page, '#content div h2', "Log In Successful");
    await cas.assertInnerTextContains(page, "#content div p", "1234567890@college.edu");

    await cas.assertInnerTextContains(page, "#attribute-tab-0 table#attributesTable tbody", "casuserx509");
    await cas.assertInnerTextContains(page, "#attribute-tab-0 table#attributesTable tbody", "someattribute");
    await cas.assertInnerTextContains(page, "#attribute-tab-0 table#attributesTable tbody", "user-account-control");

    await browser.close();
})();
