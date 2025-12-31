CREATE SEQUENCE task_seq START 1;

CREATE TABLE task (
      id BIGINT PRIMARY KEY DEFAULT nextval('task_seq'),
      description TEXT,
      completed BOOLEAN NOT NULL DEFAULT FALSE,
      type VARCHAR(50) NOT NULL,
      user_id BIGINT,
      category_id BIGINT,

      CONSTRAINT fk_task_user
          FOREIGN KEY (user_id) REFERENCES users(id),

      CONSTRAINT fk_task_category
          FOREIGN KEY (category_id) REFERENCES category(id)
);
