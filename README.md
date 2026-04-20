# PSInema

Cinema booking system: Spring Boot backend + React admin + Expo mobile.

## Run

All three components are separate processes. Open one terminal per component.

### Backend (Spring Boot, port 8080)

```bash
./gradlew bootRun
```

Uses SQLite at `data/psinema.db` (committed with seed data from `init.sql`).
`DataInitializer` adds two login accounts on first boot:

- `admin@psinema.com` / `admin123`
- `employee@psinema.com` / `employee123`

To reseed the catalog (halls, seats, movies, screenings, snacks):

```bash
python -c "import sqlite3; sqlite3.connect('data/psinema.db').executescript(open('init.sql',encoding='utf-8').read())"
```

### Admin frontend (Vite + React, port 5173)

```bash
cd frontend-admin
npm install
npm run dev
```

### Mobile frontend (Expo)

```bash
cd frontend-mobile
npm install
npm start         # Expo dev server
# or: npm run android | npm run ios | npm run web
```

## Tests

```bash
./gradlew test
```
