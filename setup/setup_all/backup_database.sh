echo "Backing up database..."
sudo mysqldump -uroot --databases acm --add-drop-database > acm_bak.sql

echo "Backing up test database..."
sudo mysqldump -uroot acm > acm_test.sql # Copies data, but doesn't specify database

echo "Done!"
