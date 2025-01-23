# Variables
DOCKER_COMPOSE := docker-compose
SERVICES := race_application_command_service race_application_query_service
POSTGRES_SERVICE := postgres_db
PGADMIN_SERVICE := pgadmin
CLIENT_MODULE := trail-race-organiser-client
COMMAND_SERVICE_MODULE := race-application-command-service
QUERY_SERVICE_MODULE := race-application-query-service

# Commands

## Start the development environment
up:
	@echo "Starting development environment..."
	$(DOCKER_COMPOSE) up -d

## Stop the development environment
down:
	@echo "Stopping development environment..."
	$(DOCKER_COMPOSE) down

## Restart the development environment
restart: down up

## Build the application (Java and Docker)
build:
	@echo "Building Java application..."
	./mvnw clean package -DskipTests

	@echo "Building Docker images..."
	$(DOCKER_COMPOSE) build

## Run tests
test:
	@echo "Running tests..."
	./mvnw test

## Run tests for a specific module
test-module:
	@echo "Running tests for module $(MODULE)..."
	./mvnw test -pl $(MODULE)

## Logs for all services
logs:
	@echo "Showing logs for all services..."
	$(DOCKER_COMPOSE) logs -f

## Logs for a specific service
logs-service:
	@echo "Showing logs for service $(SERVICE)..."
	$(DOCKER_COMPOSE) logs -f $(SERVICE)

## Access PostgreSQL database
psql:
	@echo "Accessing PostgreSQL database..."
	$(DOCKER_COMPOSE) exec $(POSTGRES_SERVICE) psql -U intellexi -d trail_race_organiser_db

## Access PgAdmin
pgadmin:
	@echo "Accessing PgAdmin..."
	open http://localhost:5050

## Run Angular frontend
frontend:
	@echo "Starting Angular frontend..."
	cd $(CLIENT_MODULE) && ng serve

## Clean up Docker resources
clean:
	@echo "Cleaning up Docker resources..."
	$(DOCKER_COMPOSE) down -v --remove-orphans

## Help command
help:
	@echo "Available commands:"
	@echo "  make up               - Start the development environment"
	@echo "  make down             - Stop the development environment"
	@echo "  make restart          - Restart the development environment"
	@echo "  make build            - Build the application (Java and Docker)"
	@echo "  make test             - Run all tests"
	@echo "  make test-module MODULE=<module> - Run tests for a specific module"
	@echo "  make logs             - Show logs for all services"
	@echo "  make logs-service SERVICE=<service> - Show logs for a specific service"
	@echo "  make psql             - Access PostgreSQL database"
	@echo "  make pgadmin          - Access PgAdmin"
	@echo "  make frontend         - Run Angular frontend (ng serve)"
	@echo "  make clean            - Clean up Docker resources"
	@echo "  make help             - Show this help message"

# Default command
.DEFAULT_GOAL := help