# 🔧 Troubleshooting Guide - BookMyShow Frontend

## Issue: Blank/White Screen on http://localhost:3001

### Step 1: Check Browser Console

1. Open your browser (Chrome/Firefox/Safari)
2. Go to http://localhost:3001
3. Press **F12** (or Right-click → Inspect)
4. Click on the **Console** tab

### Step 2: Look for Debug Messages

You should see these console logs in order:

```
🚀 BookMyShow Frontend Starting...
✅ Root element found, rendering app...
📱 App component rendering...
🔐 AuthProvider initialized
🔍 Checking stored user: Not found
🌍 AppProvider initialized
📍 Selected city: Mumbai
✅ App rendered successfully!
🏠 Home component rendering
📊 Initial state: {...}
🔍 Filtering movies. Total movies: 20
  After city filter (Mumbai): XX
✅ Final filtered movies: XX
```

### Step 3: Check for Errors

**Look for RED error messages** in the console. Common errors:

#### Error 1: "Cannot find module"
```
Error: Cannot find module './components/...'
```
**Solution**: 
- Check if all files exist
- Run: `cd frontend && find src -name "*.jsx" | wc -l` (should show ~40+ files)

#### Error 2: "useApp must be used within an AppProvider"
```
Error: useApp must be used within an AppProvider
```
**Solution**: Component is trying to use `useApp()` outside the provider
- This shouldn't happen with current setup

#### Error 3: Network Error / CORS
```
Network Error
Access to XMLHttpRequest blocked by CORS
```
**Solution**:
- Backend is not running
- Start backend: `cd Book-My-Show && ./mvnw spring-boot:run`

#### Error 4: "movies is not iterable"
```
TypeError: movies is not iterable
```
**Solution**:
- Check if indianData.js exports correctly
- Run test: Open http://localhost:3001/test

---

## Step 4: Test Route

Visit: **http://localhost:3001/test**

If you see a page with "🎬 BookMyShow Frontend is Working!", then React is running!

**If test page works but home doesn't:**
- Issue is in Home component or its dependencies
- Check console for component-specific errors

**If test page also blank:**
- React is not loading at all
- Continue to Step 5

---

## Step 5: Verify Files Exist

Run these commands:

```bash
cd /Users/vineettiwari/Downloads/bookmyshow/Book-My-Show/frontend

# Check main files
ls -la src/main.jsx
ls -la src/App.jsx
ls -la index.html

# Check mock data
ls -la src/mockData/indianData.js

# Check Home page
ls -la src/pages/Home/Home.jsx
ls -la src/components/Home/Hero.jsx
ls -la src/components/Movie/MovieCard.jsx
```

All commands should show file details (not "No such file").

---

## Step 6: Check Network Tab

In browser DevTools:

1. Click **Network** tab
2. Reload page (Cmd+R or Ctrl+R)
3. Look for failed requests (red)

**Check these:**
- `main.jsx` - should load successfully (200 status)
- `App.jsx` - should load successfully
- Fonts from Google Fonts - should load

**If main.jsx shows 404:**
- Vite is not serving files correctly
- Restart: Kill terminal (Ctrl+C) and run `npm run dev` again

---

## Step 7: Hard Refresh

Sometimes browser caches cause issues:

- **Chrome/Edge**: Ctrl+Shift+R (Windows) or Cmd+Shift+R (Mac)
- **Firefox**: Ctrl+F5 (Windows) or Cmd+Shift+R (Mac)
- **Safari**: Cmd+Option+R

---

## Step 8: Restart Dev Server

1. In terminal where `npm run dev` is running
2. Press **Ctrl+C** to stop
3. Run again:

```bash
cd /Users/vineettiwari/Downloads/bookmyshow/Book-My-Show/frontend
npm run dev
```

4. Note the URL (might be :3000, :3001, or :3002)
5. Open that exact URL in browser

---

## Step 9: Clear Browser Cache & Storage

1. Open DevTools (F12)
2. Go to **Application** tab (Chrome) or **Storage** tab (Firefox)
3. Right-click on your domain
4. Select "Clear site data" or "Clear storage"
5. Reload page

---

## Step 10: Check Dependencies

```bash
cd /Users/vineettiwari/Downloads/bookmyshow/Book-My-Show/frontend

# Remove and reinstall
rm -rf node_modules package-lock.json
npm install
npm run dev
```

---

## What to Report

If still blank, please provide:

1. **Console Output** (screenshot or copy all text)
2. **Network Tab** (any red/failed requests?)
3. **Elements Tab** (is there a `<div id="root">` with content inside?)
4. **Vite Terminal Output** (the full output from `npm run dev`)
5. **Browser & Version** (Chrome 120, Firefox 121, etc.)

---

## Quick Diagnostic Commands

Run these and share the output:

```bash
cd /Users/vineettiwari/Downloads/bookmyshow/Book-My-Show/frontend

# Check if files exist
echo "Checking files..."
ls src/main.jsx && echo "✅ main.jsx exists" || echo "❌ main.jsx missing"
ls src/App.jsx && echo "✅ App.jsx exists" || echo "❌ App.jsx missing"
ls src/mockData/indianData.js && echo "✅ indianData.js exists" || echo "❌ indianData.js missing"

# Check dependencies
echo "\nChecking dependencies..."
npm list react react-dom react-router-dom 2>/dev/null | grep -E "react|react-dom|react-router-dom"

# Check node version
echo "\nNode version:"
node --version
```

---

## Common Solutions Summary

| Problem | Solution |
|---------|----------|
| Blank page, no errors | Check if test route works: /test |
| Console shows module errors | File missing, verify all files exist |
| CORS errors | Backend not running on :8080 |
| Port 3000 in use | Use the port Vite suggests (3001, etc.) |
| Old cached version | Hard refresh (Ctrl+Shift+R) |
| Stuck on loading | Check filter logic, see console logs |

---

## Expected Working State

When everything works:

1. ✅ Visit http://localhost:3001
2. ✅ See Navbar with "BookMyShow" logo
3. ✅ See Hero slider with movie images
4. ✅ See "Movies in Mumbai" heading
5. ✅ See grid of movie cards
6. ✅ See Footer at bottom
7. ✅ Console shows debug logs (no errors)

---

## Still Need Help?

Open browser console, take a screenshot showing:
- Console tab (with any errors)
- Elements tab (showing `<div id="root">` content)
- Network tab (showing loaded files)

This will help diagnose the exact issue!
