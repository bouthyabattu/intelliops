.PHONY: help up down restart ps logs build dev-web dev-core dev-ai dev-realtime test clean

help:
	@echo "IntelliOps - Developer Command Center"
	@echo "--------------------------------------"
	@echo "make up           - Start all infrastructure in Docker (Postgres, Redis, Kafka, MinIO, Keycloak)"
	@echo "make down         - Stop all Docker infrastructure"
	@echo "make ps           - View status of all running containers"
	@echo "make logs         - Stream logs from all Docker containers"
	@echo "make dev-web      - Run Next.js 14 Web Frontend locally (Port 3000)"
	@echo "make dev-core     - Run Spring Boot Core Service locally (Port 8080)"
	@echo "make dev-ai       - Run FastAPI AI Service locally (Port 8000)"
	@echo "make dev-realtime - Run Node.js WebSocket Service locally (Port 4000)"
	@echo "make test         - Run test suites across services"
	@echo "make clean        - Clean build artifacts (target, node_modules, .next)"

up:
	docker compose up -d

down:
	docker compose down

restart:
	docker compose down && docker compose up -d

ps:
	docker compose ps

logs:
	docker compose logs -f

build:
	docker compose build

dev-web:
	cd apps/web && npm run dev

dev-core:
	cd apps/core-service && mvn spring-boot:run

dev-ai:
	cd apps/ai-service && uvicorn app.main:app --port 8000 --reload

dev-realtime:
	cd apps/realtime-service && npm run dev

test:
	cd apps/core-service && mvn test
	cd apps/ai-service && pytest
	cd apps/web && npm test

clean:
	rm -rf apps/web/.next apps/web/out apps/core-service/target apps/ai-service/__pycache__
