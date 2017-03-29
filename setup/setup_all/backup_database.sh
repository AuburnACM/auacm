echo "Backing up database..."
mysqldump -uroot --databases acm --add-drop-database > acm.sql

echo "Backing up test database..."
mysqldump -uroot acm > acm_test.sql # Copies data, but doesn't specify database

echo "Done!"
