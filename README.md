### Local Testing

#### Prepare `.env` files

`backend/.env`
```
MODEL_URI=...
API_KEY=...
FOLDER_ID=...
```

`frontend/.env.development`
```
REACT_APP_SERVER_URL=http://localhost:8080
```

`tgbot/app/.env`
```
TGBOT_TOKEN=...
YANDEXGPT_API_KEY=...
```

All secrets can be found in development Telegram chat.

#### Launch the whole application
```
docker compose up -d --build
```
#### Stop the application
```
docker compose stop
```