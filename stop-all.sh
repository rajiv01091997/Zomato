#!/bin/bash

echo "🛑 Stopping all Spring Boot services..."

# Kill all spring boot processes
pkill -f "spring-boot" || true
pkill -f "mvn.*spring-boot" || true

sleep 3

echo "✅ All services stopped!"
