#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "postgres" <<EOSQL
CREATE DATABASE user_svc_db;
EOSQL