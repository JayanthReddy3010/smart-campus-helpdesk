-- Smart Campus Helpdesk database schema
-- MySQL 8.0+; run this script after creating/selecting the smart_campus_helpdesk database.

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS users (
	user_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
	name VARCHAR(150) NOT NULL,
	email VARCHAR(254) NOT NULL,
	password_hash VARCHAR(255) NOT NULL,
	role ENUM('STUDENT', 'STAFF', 'ADMIN') NOT NULL DEFAULT 'STUDENT',
	department VARCHAR(150) NULL,
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (user_id),
	UNIQUE KEY uq_users_email (email)
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS categories (
	category_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
	category_name VARCHAR(100) NOT NULL,
	description VARCHAR(500) NULL,
	active BOOLEAN NOT NULL DEFAULT TRUE,
	PRIMARY KEY (category_id),
	UNIQUE KEY uq_categories_name (category_name)
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS tickets (
	ticket_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
	user_id INT UNSIGNED NOT NULL,
	category_id INT UNSIGNED NOT NULL,
	subject VARCHAR(200) NOT NULL,
	description TEXT NOT NULL,
	location VARCHAR(255) NULL,
	priority ENUM('LOW', 'MEDIUM', 'HIGH', 'URGENT') NOT NULL DEFAULT 'MEDIUM',
	status ENUM('OPEN', 'ASSIGNED', 'IN_PROGRESS', 'RESOLVED', 'CLOSED') NOT NULL DEFAULT 'OPEN',
	assigned_to INT UNSIGNED NULL,
	resolution TEXT NULL,
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	resolved_at TIMESTAMP NULL,
	PRIMARY KEY (ticket_id),
	KEY idx_tickets_user_id (user_id),
	KEY idx_tickets_category_id (category_id),
	KEY idx_tickets_assigned_to (assigned_to),
	KEY idx_tickets_status_priority (status, priority),
	KEY idx_tickets_created_at (created_at),
	CONSTRAINT fk_tickets_user
		FOREIGN KEY (user_id) REFERENCES users (user_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT,
	CONSTRAINT fk_tickets_category
		FOREIGN KEY (category_id) REFERENCES categories (category_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT,
	CONSTRAINT fk_tickets_assigned_to
		FOREIGN KEY (assigned_to) REFERENCES users (user_id)
		ON UPDATE CASCADE
		ON DELETE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS ticket_comments (
	comment_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
	ticket_id INT UNSIGNED NOT NULL,
	user_id INT UNSIGNED NOT NULL,
	comment TEXT NOT NULL,
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (comment_id),
	KEY idx_ticket_comments_ticket_id (ticket_id),
	KEY idx_ticket_comments_user_id (user_id),
	KEY idx_ticket_comments_created_at (created_at),
	CONSTRAINT fk_ticket_comments_ticket
		FOREIGN KEY (ticket_id) REFERENCES tickets (ticket_id)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
	CONSTRAINT fk_ticket_comments_user
		FOREIGN KEY (user_id) REFERENCES users (user_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS ticket_history (
	history_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
	ticket_id INT UNSIGNED NOT NULL,
	changed_by INT UNSIGNED NOT NULL,
	old_status ENUM('OPEN', 'ASSIGNED', 'IN_PROGRESS', 'RESOLVED', 'CLOSED') NULL,
	new_status ENUM('OPEN', 'ASSIGNED', 'IN_PROGRESS', 'RESOLVED', 'CLOSED') NOT NULL,
	action_description VARCHAR(500) NOT NULL,
	changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (history_id),
	KEY idx_ticket_history_ticket_id (ticket_id),
	KEY idx_ticket_history_changed_by (changed_by),
	KEY idx_ticket_history_changed_at (changed_at),
	CONSTRAINT fk_ticket_history_ticket
		FOREIGN KEY (ticket_id) REFERENCES tickets (ticket_id)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
	CONSTRAINT fk_ticket_history_changed_by
		FOREIGN KEY (changed_by) REFERENCES users (user_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- Keep databases created from an earlier version compatible with assignment status.
ALTER TABLE tickets
		MODIFY COLUMN status ENUM('OPEN', 'ASSIGNED', 'IN_PROGRESS', 'RESOLVED', 'CLOSED') NOT NULL DEFAULT 'OPEN';

ALTER TABLE ticket_history
		MODIFY COLUMN old_status ENUM('OPEN', 'ASSIGNED', 'IN_PROGRESS', 'RESOLVED', 'CLOSED') NULL,
		MODIFY COLUMN new_status ENUM('OPEN', 'ASSIGNED', 'IN_PROGRESS', 'RESOLVED', 'CLOSED') NOT NULL;

-- Development seed data only: categories contain no credentials or user accounts.
INSERT INTO categories (category_name, description)
VALUES
	('IT Support', 'Computer, network, account, and software support'),
	('Facilities', 'Buildings, classrooms, furniture, and maintenance'),
	('Hostel', 'Residence hall and accommodation-related requests'),
	('Library', 'Library facilities, equipment, and access requests'),
	('Transport', 'Campus shuttle and transportation requests')
ON DUPLICATE KEY UPDATE
	description = VALUES(description),
	active = TRUE;
