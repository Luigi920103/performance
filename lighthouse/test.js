const fs = require('fs') // reports -> disk
const sleep = ms => new Promise(res => setTimeout(res, ms));
//import {setTimeout} from "node:timers/promises";
const puppeteer = require('puppeteer') // lib -> manage user behavior in Chrome
const lighthouse = require('lighthouse/lighthouse-core/fraggle-rock/api.js') // full refresh + scrolls/...

const waitTillHTMLRendered = async (page, timeout = 30000) => { //waiting for a full page load
const checkDurationMsecs = 1000; //if the page size doen't change (several checks), then the page is fully loaded
const maxChecks = timeout / checkDurationMsecs;
let lastHTMLSize = 0;
let checkCounts = 1;
let countStableSizeIterations = 0;
const minStableSizeIterations = 3;

while(checkCounts++ <= maxChecks){
let html = await page.content();
let currentHTMLSize = html.length;

let bodyHTMLSize = await page.evaluate(() => document.body.innerHTML.length);

//console.log('last: ', lastHTMLSize, ' <> curr: ', currentHTMLSize, " body html size: ", bodyHTMLSize);

if(lastHTMLSize != 0 && currentHTMLSize == lastHTMLSize)
countStableSizeIterations++;
else
countStableSizeIterations = 0; //reset the counter

if(countStableSizeIterations >= minStableSizeIterations) {
console.log("Fully Rendered Page: " + page.url());
break;
}

lastHTMLSize = currentHTMLSize;
await page.waitForTimeout(checkDurationMsecs);
}
};

async function captureReport() { //we will call the function at the end of the script

const browser = await puppeteer.launch({"headless": true,args: ['--allow-no-sandbox-job', '--allow-sandbox-debugging', '--no-sandbox', '--disable-gpu', '--disable-gpu-sandbox', '--display', '--ignore-certificate-errors', '--disable-storage-reset=true']});


//arguments in '--' for 1)opening Chrome without a window/display; 2) ignore certificate; 3) way of clicking

const page = await browser.newPage() // open new page in Chrome
await page.setViewport({"width":1920,"height":1080}) //size fullHD, change if we emulate a mobile view

const navigationPromise = page.waitForNavigation({timeout: 30000, waitUntil: ['domcontentloaded']}) // wait 30 sec for the DOMmodel to load

const flow = await lighthouse.startFlow(page, { //define a tool that will measure UI
name: 'petClinic', //set any name
configContext: {
settingsOverrides: {
throttling: {
rttMs: 40, // recommendation for Google- Ok
throughputKbps: 10240, //~ 10Mb Internet, recommendation for Google, don't write more than possible
cpuSlowdownMultiplier: 1, // 1 - use full CPU, 2 - use half of CPU
requestLatencyMs: 0, // not to change 0
downloadThroughputKbps: 0, // not to change 0
uploadThroughputKbps: 0 // not to change 0
},
throttlingMethod: "simulate", // not to change
screenEmulation: { // not to change
mobile: false, // true if we emulate a mobile view
width: 1920, // change if we emulate a mobile view
height: 1080, // change if we emulate a mobile view
deviceScaleFactor: 1, // not to change
disabled: false, // not to change
},
formFactor: "desktop", //emulation of the desktop/mobile view of Chrome
onlyCategories: ['performance'],
},
},
});

//View Links
let HomePage = 'http://localhost/';
let ThankYouPage = HomePage+'thank-you';

//Test variables
const object = "living-room-table7";
const companyFormValue = "Epam";
const nameFormValue = "Luis Ruiz";
const addressFormValue = "Calle falsa 123";
const postalFormValue = "111156";
const cityFormValue = "Bogota";
const countryFormValue = 'CO';
const stateFormValue = 'CO_DC';
const phoneFormValue = "3007777777";
const emailFormValue = "dummy00@yopmail.com";

//CSS Selectors
const tablesBtnSelector = ".page-item-13";
const tableSelector = `a[href*="/${object}"] >img`;
const addBtnSelector = `form[id*="reg_add_"]>button`;
const cartBtnSelector = `li.page_item.page-item-31`;
const placeAnOrderBtnSelector = `.form-buttons > input`;

const companyInputFormSelector = `input[name="cart_company"]`;
const nameInputFormSelector = `input[name="cart_name"]`;
const addressInputFormSelector = `input[name="cart_address"]`;
const postalInputFormSelector = `input[name="cart_postal"]`;
const cityInputFormSelector = `input[name="cart_city"]`;
const countryDropdownFormSelector = 'select[name="cart_country"]';
const stateDropdownFormSelector = 'select[name="cart_state"]';
const phoneInputFormSelector = `input[name="cart_phone"]`;
const emailInputFormSelector = `input[name="cart_email"]`;
const submitBtnFormSelector = `input[name="cart_submit"]`;

//Cold Navigations //opening links with "navigate"
await flow.navigate(HomePage, {stepName: 'Open App'});
console.log('Open the application');

const defaultViewport = page.viewport();

console.log('The default viewport size is:', defaultViewport);

await flow.navigate(async () => {
    await page.click(tablesBtnSelector)
    ,{ stepName: 'Navigate to "Tables" tab' }
  });
  console.log('Navigate to "Tables" tab');

  await flow.navigate(async () => {
    await page.click(tableSelector)
,{ stepName: 'Open a table product cart (click on a table)' }
  });
  console.log('Open a table product cart (click on a table)');

  await flow.navigate(async () => {
    await page.click(addBtnSelector)
    await sleep(2000);
    await page.click(cartBtnSelector)
,{ stepName: 'Add table to Cart (click "Add to Cart" button)'}
  });
  console.log('Add table to Cart (click "Add to Cart" button) and navigate to the cart');

  await flow.navigate(async () => {
    await page.click(placeAnOrderBtnSelector)
,{ stepName: 'Click "Place an order"' }
  });
  console.log('Click "Place an order"');

  await flow.navigate(async () => {
    await page.type(companyInputFormSelector, companyFormValue)
    await page.type(nameInputFormSelector, nameFormValue)
    await page.type(addressInputFormSelector, addressFormValue)
    await page.type(postalInputFormSelector, postalFormValue)
    await page.type(cityInputFormSelector, cityFormValue)
    await page.select(countryDropdownFormSelector, countryFormValue)
    await sleep(1000);
    await page.select(stateDropdownFormSelector, stateFormValue)
    await page.type(phoneInputFormSelector, phoneFormValue)
    await page.type(emailInputFormSelector, emailFormValue)
    await page.click(submitBtnFormSelector)
,{ stepName: 'Fill in all required fields, click "Place order"' }
  });
  // Final assert
  const currentUrl = page.url();
  if(currentUrl !== ThankYouPage) {
      throw new Error(`Current URL is ${currentUrl}, but expected ${ThankYouPage}`);
  }

  //const reportPath = __dirname + '/user-flow.report.html';
	//const reportPathJson = __dirname + '/user-flow.report.json';

	//const report = flow.generateReport();
	//const reportJson = JSON.stringify(flow.getFlowResult()).replace(/</g, '\\u003c').replace(/\u2028/g, '\\u2028').replace(/\u2029/g, '\\u2029');
	
	fs.writeFileSync('reports/report.html', await flow.generateReport());
	//fs.writeFileSync(reportPathJson, reportJson);

await browser.close(); //close Chrome
}
captureReport(); //call function