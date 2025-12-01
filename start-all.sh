#!/bin/bash

echo "🔍 Starting services one by one..."

# Kill only spring-boot (safe)
pkill -f "spring-boot" || true
sleep 3

# Essential services ONLY (manual list)
services=("users-service" "cart-service" "order-service" "payment-service" "coupon-service" "menu-service" "map-service" "mail-service" "invoice-service")

for service in "${services[@]}"; do
    if [ -d "$service" ]; then
        echo "🚀 $service..."
        cd "$service"
        nohup mvn spring-boot:run -Dspring-boot.run.profiles=dev > "../${service}.log" 2>&1 &
        cd ..
        sleep 5  # Wait for startup
        echo "✅ $service launched | tail -f ${service}.log"
    fi
done

echo "🎉 All launched! Wait 30 sec then check logs"
