# 🔐 Environment Variables Setup

## Quick Setup

1. **Copy the example file:**
   ```bash
   cp .env.example .env
   ```

2. **Edit `.env` with your actual credentials:**
   ```bash
   nano .env
   # or use your preferred editor
   ```

3. **Set your environment variables:**

### Database Configuration
```properties
DB_USERNAME=springuser
DB_PASSWORD=springpass123
```

### Email Configuration (Gmail)
To get a Gmail App Password:
1. Go to [Google Account Security](https://myaccount.google.com/security)
2. Enable 2-Step Verification
3. Go to [App Passwords](https://myaccount.google.com/apppasswords)
4. Generate a new app password for "Mail"
5. Use that 16-character password

```properties
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-16-char-app-password
```

### Stripe Payment Gateway
Get your test keys from [Stripe Dashboard](https://dashboard.stripe.com/test/apikeys):

```properties
STRIPE_SECRET_KEY=sk_test_51SmK7UBMTQ3xf1vt...
STRIPE_PUBLISHABLE_KEY=pk_test_51SmK7UBMTQ3xf1vt...
```

## Running with Environment Variables

### Option 1: Using IDE (IntelliJ/Eclipse)
- Configure environment variables in Run Configuration
- Set each variable in the Environment Variables section

### Option 2: Using Terminal
```bash
# Export variables (macOS/Linux)
export DB_USERNAME=springuser
export DB_PASSWORD=springpass123
export STRIPE_SECRET_KEY=sk_test_your_key_here
export STRIPE_PUBLISHABLE_KEY=pk_test_your_key_here
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password

# Then run the application
./mvnw spring-boot:run
```

### Option 3: Using .env file with Spring Boot
Spring Boot will automatically use values from environment variables that override the defaults in `application.properties`.

## Security Notes

⚠️ **IMPORTANT:**
- Never commit `.env` file to Git
- Never commit actual API keys to GitHub
- `.env` is already in `.gitignore`
- Only commit `.env.example` with placeholder values
- Share actual credentials securely (not via Git)

## Default Values

The application uses these defaults if environment variables are not set:
- DB_USERNAME: `springuser`
- DB_PASSWORD: `springpass123`
- MAIL_USERNAME: `khanking001qwerty@gmail.com`
- MAIL_PASSWORD: (empty)
- STRIPE_SECRET_KEY: (empty)
- STRIPE_PUBLISHABLE_KEY: (empty)

## Verifying Setup

Check if environment variables are loaded:
```bash
# macOS/Linux
echo $STRIPE_SECRET_KEY

# Windows PowerShell
echo $env:STRIPE_SECRET_KEY
```

## Troubleshooting

**Problem:** Application can't find credentials
- Make sure environment variables are exported in the same terminal session
- Restart your IDE after setting environment variables
- Check for typos in variable names

**Problem:** Stripe payment fails
- Verify you're using **test keys** (start with `sk_test_` and `pk_test_`)
- Check keys don't have extra spaces or newlines
- Ensure keys are from [Stripe Test Mode](https://dashboard.stripe.com/test/apikeys)
