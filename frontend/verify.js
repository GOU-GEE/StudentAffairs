import { chromium } from 'playwright';

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  
  // Set a taller viewport to capture all contents!
  await page.setViewportSize({ width: 1440, height: 1300 });

  console.log('Navigating to login page...');
  await page.goto('http://localhost:5173/login');
  
  await page.waitForSelector('input[placeholder*="账号"]');
  await page.fill('input[placeholder*="账号"]', 's001');
  await page.fill('input[placeholder*="密码"]', '123456');
  await page.click('button[type="submit"]');
  
  await page.waitForURL('**/student');
  await page.goto('http://localhost:5173/student/academic');
  await page.waitForSelector('h3:has-text("课程成绩明细")');
  
  await page.waitForTimeout(2000);
  
  const screenshotPath = '/Users/goulijun/.gemini/antigravity/brain/8eaa7423-aa2a-4c85-94ae-a1fda804e804/academic-secondclass-final-v3.png';
  console.log(`Taking screenshot and saving to: ${screenshotPath}`);
  await page.screenshot({ path: screenshotPath });
  
  console.log('Done!');
  await browser.close();
})().catch(err => {
  console.error(err);
  process.exit(1);
});
