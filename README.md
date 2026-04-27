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

Before starting, set the backend URL in `frontend-mobile/src/api/client.ts`:
replace `change-your-ip` in `BASE_URL` with your dev machine's LAN IP (e.g. `192.168.0.101`).
The phone and PC must be on the same network, and port 8080 must be reachable
(allow it through Windows Firewall). Use `10.0.2.2` for the Android emulator
or `localhost` for the iOS simulator.

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
