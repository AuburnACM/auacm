sh setup_macos/macos_dependencies.sh
sh setup_all/initialize_database.sh
sh setup_all/initialize_config.sh
sh setup_all/initialize_virtualenv.sh
echo "Setup complete!  You should now be able to launch the server."
echo "To get started execute $ cd ../auacm && ./run.py to get started"
