from flask import db #change this?


class Problem(db.Model):

    id = db.Column(db.String, primary_key=True)
    name = db.Column(db.String(80))
    contest = db.Column(db.String(80))
    difficulty = db.Column(db.String(80))
    description_url = db.Column(db.String(80))
