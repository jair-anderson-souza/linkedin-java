-- Initialize LinkedIn Clone MySQL Database

-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS linkedin_core;
USE linkedin_core;

-- Set timezone
SET time_zone = '+00:00';

-- Create a sample admin user (optional)
-- This will be handled by migrations and seeds instead

-- Set proper character set and collation
ALTER DATABASE linkedin_core CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
