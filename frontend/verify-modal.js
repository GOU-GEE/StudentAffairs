import { chromium } from 'playwright';

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  
  await page.setViewportSize({ width: 1440, height: 900 });

  console.log('Navigating to login page...');
  await page.goto('http://localhost:5173/login');
  
  await page.waitForSelector('input[placeholder*="账号"]');
  await page.fill('input[placeholder*="账号"]', 's001');
  await page.fill('input[placeholder*="密码"]', '123456');
  await page.click('button[type="submit"]');
  
  await page.waitForURL('**/student');
  
  console.log('Navigating to career-plan page...');
  await page.goto('http://localhost:5173/student/career-plan');
  
  console.log('Waiting for career assessment tools to load...');
  await page.waitForSelector('h3:has-text("生涯测评工具")');
  
  console.log('Clicking the Assessment button...');
  // Click first button using robust selector
  await page.click('.border.border-outline-variant\\/20.rounded-xl.p-4.hover\\:shadow-md button');
  
  console.log('Waiting for questionnaire dialog to display...');
  await page.waitForSelector('.custom-quiz-dialog', { timeout: 10000 });
  await page.waitForTimeout(1500); // Wait for open transition
  
  const screenshotPath = '/Users/goulijun/.gemini/antigravity/brain/8eaa7423-aa2a-4c85-94ae-a1fda804e804/career-quiz-modal-v5.png';
  console.log(`Taking screenshot and saving to: ${screenshotPath}`);
  await page.screenshot({ path: screenshotPath });
  
  console.log('Done!');
  await browser.close();
})().catch(err => {
  console.error(err);
  process.exit(1);
});
