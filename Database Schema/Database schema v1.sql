-- =====================================================
-- EXPENSE TRACKER API - DATABASE SCHEMA V2
-- =====================================================
-- This schema matches the JPA entities exactly
-- Includes all fields, constraints, and indexes
-- =====================================================

-- =====================================================
-- 1. CREATE ENUM TYPES
-- =====================================================

-- Category Type Enum
CREATE TYPE category_type AS ENUM ('SYSTEM', 'USER');

-- Predefined Category Enum
CREATE TYPE predefined_category AS ENUM (
    'GROCERIES',
    'LEISURE',
    'ELECTRONICS',
    'UTILITIES',
    'CLOTHING',
    'HEALTH',
    'OTHERS'
);

-- Budget Period Enum
CREATE TYPE budget_period AS ENUM (
    'WEEKLY',
    'MONTHLY',
    'QUARTERLY',
    'YEARLY',
    'CUSTOM'
);

-- =====================================================
-- 2. USERS TABLE
-- =====================================================

CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes for users table
CREATE INDEX idx_id ON users(id);
CREATE INDEX idx_email ON users(email);
CREATE INDEX idx_created_at ON users(created_at);

-- =====================================================
-- 3. USER_PROFILES TABLE
-- =====================================================

CREATE TABLE user_profiles (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(10) NOT NULL UNIQUE,
    display_name VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes for user_profiles table
CREATE INDEX idx_user_profiles_user_id ON user_profiles(user_id);
CREATE INDEX idx_user_profiles_display_name ON user_profiles(display_name);
CREATE INDEX idx_user_profiles_created_at ON user_profiles(created_at);

-- =====================================================
-- 4. CATEGORIES TABLE
-- =====================================================

CREATE TABLE categories (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,  -- Nullable for SYSTEM categories
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    type category_type NOT NULL,
    predefined_category predefined_category,  -- Only set for SYSTEM categories
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    
    -- Constraint: SYSTEM categories must have predefined_category set
    CONSTRAINT check_system_category 
        CHECK (
            (type = 'SYSTEM' AND predefined_category IS NOT NULL AND user_id IS NULL) OR
            (type = 'USER' AND predefined_category IS NULL AND user_id IS NOT NULL)
        )
);

-- Indexes for categories table
CREATE INDEX idx_categories_user_id ON categories(user_id);
CREATE INDEX idx_categories_name ON categories(name);
CREATE INDEX idx_categories_type ON categories(type);
CREATE INDEX idx_categories_created_at ON categories(created_at);

-- =====================================================
-- 5. EXPENSES TABLE
-- =====================================================

CREATE TABLE expenses (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    amount DECIMAL(10, 2) NOT NULL,
    expense_date DATE NOT NULL,
    category_id UUID NOT NULL REFERENCES categories(id),
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    
    -- Constraint: amount must be positive
    CONSTRAINT check_positive_amount CHECK (amount > 0)
);

-- Indexes for expenses table
CREATE INDEX idx_expenses_user_id ON expenses(user_id);
CREATE INDEX idx_expenses_category_id ON expenses(category_id);
CREATE INDEX idx_expenses_expense_date ON expenses(expense_date);
CREATE INDEX idx_expenses_created_at ON expenses(created_at);
CREATE INDEX idx_expenses_amount ON expenses(amount);

-- =====================================================
-- 6. BUDGETS TABLE
-- =====================================================

CREATE TABLE budgets (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id UUID NOT NULL REFERENCES categories(id),
    amount DECIMAL(10, 2) NOT NULL,
    period budget_period,
    start_date DATE NOT NULL,
    end_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    
    -- Constraint: amount must be positive
    CONSTRAINT check_budget_positive_amount CHECK (amount > 0),
    
    -- Constraint: end_date must be after start_date
    CONSTRAINT check_budget_dates CHECK (end_date IS NULL OR end_date >= start_date)
);

-- Indexes for budgets table
CREATE INDEX idx_budgets_user_id ON budgets(user_id);
CREATE INDEX idx_budgets_category_id ON budgets(category_id);
CREATE INDEX idx_budgets_created_at ON budgets(created_at);
CREATE INDEX idx_budgets_amount ON budgets(amount);

-- =====================================================
-- 7. SEED SYSTEM CATEGORIES
-- =====================================================

-- Insert predefined system categories
INSERT INTO categories (id, user_id, name, description, type, predefined_category, created_at, updated_at)
VALUES
    (gen_random_uuid(), NULL, 'Groceries', 'Food and grocery shopping', 'SYSTEM', 'GROCERIES', NOW(), NOW()),
    (gen_random_uuid(), NULL, 'Leisure', 'Entertainment and recreation', 'SYSTEM', 'LEISURE', NOW(), NOW()),
    (gen_random_uuid(), NULL, 'Electronics', 'Electronic devices and gadgets', 'SYSTEM', 'ELECTRONICS', NOW(), NOW()),
    (gen_random_uuid(), NULL, 'Utilities', 'Bills, electricity, water, internet', 'SYSTEM', 'UTILITIES', NOW(), NOW()),
    (gen_random_uuid(), NULL, 'Clothing', 'Clothes and accessories', 'SYSTEM', 'CLOTHING', NOW(), NOW()),
    (gen_random_uuid(), NULL, 'Health', 'Medical expenses and healthcare', 'SYSTEM', 'HEALTH', NOW(), NOW()),
    (gen_random_uuid(), NULL, 'Others', 'Miscellaneous expenses', 'SYSTEM', 'OTHERS', NOW(), NOW());

-- =====================================================
-- 8. HELPFUL QUERIES FOR VERIFICATION
-- =====================================================

-- Verify all tables created
-- SELECT table_name FROM information_schema.tables WHERE table_schema = 'public';

-- Verify all indexes
-- SELECT indexname, tablename FROM pg_indexes WHERE schemaname = 'public';

-- View system categories
-- SELECT * FROM categories WHERE type = 'SYSTEM';

-- =====================================================
-- END OF SCHEMA
-- =====================================================