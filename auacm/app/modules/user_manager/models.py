"""Reflection and utilities for the users database table."""

from app.database import database_base

class User(database_base):
    """Model object for entries in the users database table."""

    __tablename__ = 'users'
    is_authenticated = True
    is_active = True
    is_anonymous = False

    def get_id(self):
        """Returns the username of the user."""
        return self.username
