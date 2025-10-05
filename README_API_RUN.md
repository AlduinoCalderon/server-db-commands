Run API + GUI locally (two terminal windows)

1) Build the project (from repo root):

   mvn -DskipTests=true package

2) Terminal A - run the GUI (JavaFX):

   mvn javafx:run

   or run the JavaFX main directly:

   java -cp target/server-db-commands-1.0.0.jar com.innovationcenter.scholarapi.gui.ScholarGuiApplication

3) Terminal B - run the API (embedded or standalone):

   Option 1: standalone API (recommended)
   java -cp target/server-db-commands-1.0.0.jar com.innovationcenter.scholarapi.api.ApiMain

   Option 2: embedded API (started from GUI)
   In the GUI, call ScholarGuiApplication.startEmbeddedApi(7000) programmatically or via a menu (not yet exposed).

Notes:
- Use .env or environment variables to configure DB connection (DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD).
- To run in Docker (API + MySQL):

    cd deploy
    docker-compose up --build

- Limits: set DB_POOL_MAX=2 to ensure each service uses at most 2 DB connections.
- Closing the GUI will stop the embedded API and close the DB connection pool.
