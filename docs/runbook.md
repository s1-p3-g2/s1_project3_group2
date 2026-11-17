# Rate My Duck Runbook

## Start the project

Make sure the required environment files are present before starting the
services. From the repository root, start the backend and database with:

```bash
docker compose up
```

Leave that terminal running.

## Run the Expo app

Open a second terminal and move into the Expo application directory:

```bash
cd app
npm install
npx expo start
```

The Expo development server displays options for opening the app:

- Press `w` to open the app in a web browser.
- Press `a` to open it in an Android emulator.
- Scan the QR code with Expo Go to open it on a physical device.

For a physical device, make sure the device and computer are connected to the
same network. If the app cannot reach the backend, check the `API_URL` value in
`app/src/services/ratings-api.ts` and use an address reachable from the device
instead of `localhost`.

## Navigate to the Ratings page

The Ratings screen is the Expo Router route `/ratings`.

When running the app in a web browser, open:

```text
http://localhost:8081/ratings
```

## Navigate to the Login page
Prerequisites: Node.js and npm installed.
Open a terminal at the repository root.
Navigate to the frontend folder:


  ```sh
  cd app
  ```


Install dependencies:


  ```sh
  npm ci
  ```


Start the web frontend:


  ```sh
  npm run web
  ```


Open `http://localhost:8081/Login` in your browser.
Check the terminal for the actual port. The current integration expects port `8081`.
Keep the terminal running while using the frontend. Press `Ctrl+C` to stop it.
On Windows PowerShell, use `npm.cmd` instead of `npm` if script execution is blocked.


## Test sign-in


Start the Spring Boot backend on `http://localhost:8080`.
Ensure the backend run configuration loads the private environment file containing:


  ```dotenv
  DATABASE_URL=<PostgreSQL JDBC connection string>
  GITHUB_CLIENT_ID=<GitHub OAuth client ID>
  GITHUB_CLIENT_SECRET=<GitHub OAuth client secret>
  GOOGLE_CLIENT_ID=<Google OAuth client ID>
  GOOGLE_CLIENT_SECRET=<Google OAuth client secret>
  ```


Obtain development credentials privately from the project team. Do not commit the environment file.
In IntelliJ, select the backend run configuration that loads this file, then run `BackendApplication`.
Wait for the console to report `Started BackendApplication`.
Open `http://localhost:8081/Login` and select Google or GitHub.
After successful sign-in, the browser should redirect to `http://localhost:8081/leaderboard`.
The leaderboard is currently a blank placeholder.
Use ports `8080` and `8081`: the OAuth links and frontend redirect currently use those addresses.

When running in Expo Go or an emulator, open the `/ratings` route through the
Expo Router development URL. The page contains modules for viewing ratings,
creating a rating, updating a rating, viewing ratings by duck or user, and
viewing a duck's ranking.

## Troubleshooting

- If the Expo server is using a different port, use the port shown in the
  terminal instead of `8081`.
- If create or update requests return `403 Forbidden`, check the backend CSRF
  configuration and the browser/device request logs.
- If requests target `localhost` from a physical device, replace it with the
  computer's local network IP address.
