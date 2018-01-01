CREATE TABLE IF NOT EXISTS 'blog_posts' (
  'id' INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
  'title' VARCHAR(255) NOT NULL,
  'subtitle' VARCHAR(255) NOT NULL,
  'post_time' INT UNSIGNED NOT NULL,
  'body' TEXT NOT NULL,
  'username' VARCHAR(32) NOT NULL
) CHARACTER SET = utf8mb4;

CREATE TABLE IF NOT EXISTS 'comp_names' (
  'cid' INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
  'name' VARCHAR(32) NOT NULL,
  'start' INT UNSIGNED NOT NULL,
  'stop' INT UNSIGNED NOT NULL,
  'closed' BOOLEAN NOT NULL
) CHARACTER SET = utf8mb4;

CREATE TABLE IF NOT EXISTS 'comp_problems' (
  'id' INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
  'cid' INT UNSIGNED NOT NULL,
  'pid' INT DEFAULT NULL,
  'label' VARCHAR(2) NOT NULL
) CHARACTER SET = utf8mb4;

CREATE TABLE IF NOT EXISTS 'comp_users' (
  'id' INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
  'cid' INT UNSIGNED NOT NULL,
  'username' VARCHAR(32) NOT NULL,
  'team' VARCHAR(32) NOT NULL
) CHARACTER SET = utf8mb4;

CREATE TABLE IF NOT EXISTS 'problem_data' (
  'pid' INT NOT NULL PRIMARY KEY DEFAULT 0,
  'time_limit' INT DEFAULT NULL,
  'description' TEXT,
  'input_desc' TEXT,
  'output_desc' TEXT
) CHARACTER SET = utf8mb4;

CREATE TABLE IF NOT EXISTS 'problem_solved' (
  'pid' INT NOT NULL DEFAULT 0,
  'username' VARCHAR(255) NOT NULL,
  'submit_time' INT UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY ('username', 'pid')
) CHARACTER SET = utf8mb4;

CREATE TABLE IF NOT EXISTS 'problems' (
  'pid' INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  'shortname' VARCHAR(32) NOT NULL DEFAULT '',
  'name' VARCHAR(255) NOT NULL,
  'appeared' VARCHAR(255) NOT NULL,
  'difficulty' VARCHAR(8) NOT NULL,
  'added' INT UNSIGNED NOT NULL,
  'comp_release' INT UNSIGNED DEFAULT NULL,
  KEY 'comp_release' ('comp_release')
) CHARACTER SET = utf8mb4;

CREATE TABLE IF NOT EXISTS 'sample_cases' (
  'pid' INT NOT NULL DEFAULT 0,
  'case_num' INT NOT NULL DEFAULT 0,
  'input' TEXT,
  'output' TEXT,
  PRIMARY KEY ('pid', 'case_num')
) CHARACTER SET = utf8mb4;

CREATE TABLE IF NOT EXISTS 'submits' (
  'job' INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
  'pid' INT DEFAULT NULL,
  'username' VARCHAR(32),
  'shortname' VARCHAR(32),
  'submit_time' INT UNSIGNED NOT NULL,
  'auto_id' BOOLEAN NOT NULL,
  'file_type' VARCHAR(4) NOT NULL,
  'result' VARCHAR(8) DEFAULT NULL
) CHARACTER SET = utf8mb4;

CREATE TABLE IF NOT EXISTS 'users' (
  'username' VARCHAR(32) NOT NULL PRIMARY KEY,
  'passw' VARCHAR(255) DEFAULT NULL,
  'display' VARCHAR(32) NOT NULL,
  'admin' BOOLEAN NOT NULL,
  FULLTEXT KEY username ('username')
) CHARACTER SET = utf8mb4;

ALTER TABLE comp_problems ADD CONSTRAINT comp_problems_ibfk_1 FOREIGN KEY (pid) REFERENCES problems (pid);

ALTER TABLE comp_problems ADD CONSTRAINT comp_problems_ibfk_2 FOREIGN KEY (cid) REFERENCES comp_names (cid);

ALTER TABLE comp_users ADD CONSTRAINT comp_users_ibfk_1 FOREIGN KEY (cid) REFERENCES comp_names (cid);

ALTER TABLE 'problem_data' ADD CONSTRAINT comp_data_ibfk_1 FOREIGN KEY ('pid') REFERENCES 'problems' ('pid');

ALTER TABLE 'problems' ADD CONSTRAINT problems_ibfk_1 FOREIGN KEY ('comp_release') REFERENCES 'comp_names' ('cid');

ALTER TABLE 'sample_cases' ADD CONSTRAINT sample_cases_ibfk_1 FOREIGN KEY ('pid') REFERENCES 'problems' ('pid');
