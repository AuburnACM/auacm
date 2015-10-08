mysql -uroot < delete_database.txt
mysql -uroot < ../setup/initialize_database.txt
cd ..
echo "If server successfully starts, then test passes.  Press Ctrl+C to quit."
./run.py
