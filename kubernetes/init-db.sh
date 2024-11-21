#!/bin/bash
set -e

# Create databases
psql -U postgres -c "CREATE DATABASE orderDB;"
psql -U postgres -c "CREATE DATABASE paymentDB;"
psql -U postgres -c "CREATE DATABASE shipmentDB;"
psql -U postgres -c "CREATE DATABASE productDB;"
psql -U postgres -c "CREATE DATABASE authDB;"

echo "Databases created successfully."
