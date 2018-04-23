# Развертывание(Docker)
* docker build -t taskombank .
* docker run -p 8000:8080 taskombank

# Main url
* Docker - http://localhost:8000
* local - http://localhost:8080

# In-memory users
* admin/password
* user/password

# Swagger
* Docker - http://localhost:8000/swagger-ui.html
* local - http://localhost:8080/swagger-ui.html

# OAuth2
* получение токена для пользователя "admin/password"

curl -X POST -vu client:password http://localhost:8080/oauth/token -H "Accept: application/json" -d "password=password&username=admin&grant_type=password&client_secret=password&client_id=client"