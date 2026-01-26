-- Database Initialization Script for RecOuVTeK
-- This script runs automatically when the MySQL container is first created

-- Create database if it doesn't exist (Docker will create it via MYSQL_DATABASE env var, but this is a fallback)
CREATE DATABASE IF NOT EXISTS rec_ouv_db;

-- Use the database
USE rec_ouv_db;

-- Set default character set and collation for proper UTF-8 support
ALTER DATABASE rec_ouv_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Grant all privileges to root user from any host (for development)
GRANT ALL PRIVILEGES ON rec_ouv_db.* TO 'root'@'%';
FLUSH PRIVILEGES;

-- Note: Hibernate with ddl-auto=update will create tables automatically
-- Add any additional initialization SQL below if needed
