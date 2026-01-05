# 🚨 BLANK SCREEN? START HERE!

## Quick Actions (Do These First!)

### Action 1: Open Browser Console
1. Open http://localhost:3001 (or whatever port npm run dev shows)
2. Press **F12** (or Cmd+Option+I on Mac)
3. Click **Console** tab
4. **Take a screenshot or copy ALL the text**

### Action 2: Visit Test Route  
Open: **http://localhost:3001/test**

- ✅ **If you see "BookMyShow Frontend is Working!"** → React is running! Go to Action 3
- ❌ **If still blank** → React isn't loading. Share console screenshot immediately.

### Action 3: Check Test Route Console
If /test works, open console again and look for these logs when you visit **http://localhost:3001/**:

```
Expected logs (in order):
🚀 BookMyShow Frontend Starting...
✅ Root element found, rendering app...
📱 App component rendering...
🔐 AuthProvider initialized
🌍 AppProvider initialized
📍 Selected city: Mumbai
🧭 Navbar rendering
🎭 Hero component rendering
🏠 Home component rendering
```

**What's missing?** → That's where the error is!

---

## Diagnostic Steps

### Step 1: Run Diagnostic Script
```bash
cd /Users/vineettiwari/Downloads/bookmyshow/Book-My-Show
./diagnose.sh
```

Copy the entire output.

### Step 2: Check Browser Elements Tab
1. Open DevTools (F12)
2. Click **Elements** tab  
3. Press Cmd+F (Mac) or Ctrl+F (Windows)
4. Search for: `<div id="root"`
5. **Is there content inside?**
   - ✅ Yes, lots of HTML → It's a CSS issue (content exists but invisible)
   - ❌ No, empty or just whitespace → React isn't rendering

### Step 3: Check Network Tab
1. Click **Network** tab in DevTools
2. Reload page (Cmd+R or Ctrl+R)
3. Look for **RED** failed requests
4. Specifically check: `main.jsx` - Should be **200 OK**

---

## Common Issues & Instant Fixes

### Issue 1: Console Shows Nothing
**Symptoms:** Completely blank console, no logs at all

**Fix:**
```bash
# Hard refresh
Cmd+Shift+R (Mac) or Ctrl+Shift+R (Windows)

# If still nothing, clear cache:
# DevTools → Application → Clear storage → Clear site data
```

### Issue 2: Console Shows "Cannot find module './components/...'"
**Symptoms:** Red error about missing module

**Fix:**
```bash
cd /Users/vineettiwari/Downloads/bookmyshow/Book-My-Show/frontend

# Check which file is missing
ls src/components/Layout/Navbar.jsx
ls src/components/Home/Hero.jsx
ls src/mockData/indianData.js

# If any missing, report which one
```

### Issue 3: Console Shows Network Errors
**Symptoms:** CORS errors, Network Error, Failed to fetch

**Fix:**
```bash
# Check if backend is running
curl http://localhost:8080/movie/get-all-movies

# If not running, start it:
cd /Users/vineettiwari/Downloads/bookmyshow/Book-My-Show
./mvnw spring-boot:run
```

### Issue 4: Test Page Works, Home Doesn't
**Symptoms:** /test route shows content, but / is blank

**Console will show where it stops:**
- Stops at 🧭 Navbar → Issue in Navbar component
- Stops at 🎭 Hero → Issue in Hero component
- Stops at 🏠 Home → Issue in Home component
- Shows all logs but blank → CSS hiding content

**Fix:**
Check which component log is missing, report it.

### Issue 5: Movies Not Showing (Empty Grid)
**Symptoms:** Page loads but says "0 movies" or "No movies found"

**Console should show:**
```
🔍 Filtering movies. Total movies: 20
  After city filter (Mumbai): 0  ← PROBLEM HERE
```

**Fix:**
City filter is too strict. Try:
1. Change city in navbar dropdown
2. Check if generateShows() in indianData.js has data for that city

---

## What to Share for Help

Copy-paste or screenshot these:

### Priority 1: Browser Console
```
[Entire console output - include all emojis and logs]
```

### Priority 2: Test Route Result
- Does http://localhost:3001/test work? Yes/No
- Screenshot if helpful

### Priority 3: Elements Tab Check
```
In DevTools → Elements:
<div id="root">
  [Is there content here? Describe what you see]
</div>
```

### Priority 4: Network Tab
```
Any failed requests? Which files?
main.jsx status: ???
```

### Priority 5: Diagnostic Output
```bash
./diagnose.sh
[Paste entire output]
```

---

## Emergency Reset

If nothing works, nuclear option:

```bash
cd /Users/vineettiwari/Downloads/bookmyshow/Book-My-Show/frontend

# Remove everything
rm -rf node_modules package-lock.json

# Reinstall
npm install

# Start fresh
npm run dev

# Open in INCOGNITO browser
# Visit the URL shown
```

---

## Visual Debugging

### What You SHOULD See

When working correctly:

**Top of page:**
- Red navbar with "BookMyShow" logo
- City selector showing "Mumbai"
- Search bar
- Login button (or user menu if logged in)

**Hero section:**
- Large movie poster background
- Movie title and description overlay
- "Book Tickets" button
- Dots for slide navigation

**Movies section:**
- "Movies in Mumbai" heading
- "X movies" count
- Grid of movie cards with posters

**Bottom:**
- Footer with links

### What You're Seeing

Describe:
- Is navbar visible?
- Any text showing?
- Any colors/backgrounds?
- Completely white?
- Any loading spinner?

---

## Console Log Meanings

| Log | Means |
|-----|-------|
| 🚀 BookMyShow Frontend Starting... | main.jsx executing |
| ✅ Root element found | index.html loaded correctly |
| ✅ App rendered | React rendered successfully |
| 📱 App component rendering | App.jsx executing |
| 🔐 AuthProvider initialized | Auth context working |
| 🌍 AppProvider initialized | App context working |
| 📍 Selected city: Mumbai | City loaded from localStorage |
| 🧭 Navbar rendering | Navbar component loading |
| 🎭 Hero component | Hero slider loading |
| 🏠 Home component | Home page loading |
| 🔍 Filtering movies | Movie filter running |
| ✅ Final filtered movies: 20 | Filter complete, movies ready |

**If logs stop at any point, that's where the error is!**

---

## Still Stuck?

### Share These 4 Things:

1. **Console screenshot** (or copy-paste all text)
2. **Test route works?** (http://localhost:3001/test - Yes/No)
3. **Terminal output** (from npm run dev)
4. **Browser & version** (Chrome 120, Safari 17, etc.)

With these 4 pieces of information, I can identify the exact issue!

---

## Quick Wins Checklist

Try each until something works:

- [ ] Hard refresh (Cmd+Shift+R)
- [ ] Clear browser cache (DevTools → Application → Clear)
- [ ] Try incognito/private window
- [ ] Try different browser (Chrome, Firefox, Safari)
- [ ] Visit /test route
- [ ] Check console for errors
- [ ] Run ./diagnose.sh
- [ ] Restart dev server (Ctrl+C, then npm run dev)
- [ ] Check if port changed (3000 → 3001 → 3002)
- [ ] Nuclear reset (rm -rf node_modules && npm install)

---

**The debugging tools are now in place. Let's find that bug! 🐛🔍**
