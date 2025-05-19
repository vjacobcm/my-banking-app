CREATE TABLE IF NOT EXISTS shedlock (
  name VARCHAR(100),
  lock_until TIMESTAMP(3) NULL,
  locked_at TIMESTAMP(3) NULL,
  locked_by VARCHAR(255),
  PRIMARY KEY (name)
);

CREATE TABLE IF NOT EXISTS accounts (
  account_id UUID PRIMARY KEY,
  account_number VARCHAR(15) UNIQUE NOT NULL,
  account_holder VARCHAR(100) NOT NULL,
  balance DECIMAL(15,2) DEFAULT 0.00,
  is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS transactions (
  transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  debitor_account VARCHAR(15) NOT NULL,
  creditor_account VARCHAR(15) NOT NULL,
  amount DECIMAL(15,2) NOT NULL CHECK (amount>0),
  is_processed BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);