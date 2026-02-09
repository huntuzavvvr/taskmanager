ALTER TABLE task
ADD COLUMN created_at TIMESTAMP;

UPDATE task
SET created_at = now()
WHERE created_at IS NULL;

ALTER TABLE task
ALTER COLUMN created_at SET NOT NULL;