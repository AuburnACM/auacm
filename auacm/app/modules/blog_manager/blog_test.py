"""Test for AUACM blog manager """

import unittest, json

from app import app, test_app
from app.util import AUACMTest
from app.modules.blog_manager.models import BlogPost
import app.database as database

session = database.session


class AUACMBlogTests(AUACMTest):
    """Test cases for the AUACM blog manager"""

    def testCreate(self):
        """Test creating a new blog post"""
        post = {
            'title': 'Test Post',
            'subtitle': 'Testy',
            'body': 'In test we trust',
            'username': self.username
        }
        self.login()

        response = json.loads(test_app.post('/api/blog', data=post)
                              .data.decode())
        self.assertEqual(200, response['status'])
        post_response = response['data']

        for key in post:
            if key == 'username':
                self.assertEqual(post[key], post_response['author'][key])
            else:
                self.assertEqual(post[key], post_response[key])

        # Delete the post from the test database
        session.flush()
        post = session.query(BlogPost).filter(
            BlogPost.id == post_response['id']).first()
        session.delete(post)
        session.commit()

    def testGetAll(self):
        """Test getting all the blog posts"""
        # Put some posts in the test database
        posts = self._insert_test_post(3)
        post_ids = [post.id for post in posts]

        response = json.loads(test_app.get('/api/blog').data.decode())
        post_response = response['data']

        self.assertEqual(200, response['status'])
        for (resp, post) in zip(post_response, posts):
            self._assert_posts_equal(resp, post)

        for post in posts:
            session.delete(post)
        session.commit()

    def testGetOne(self):
        """Test getting just one blog post"""
        post =  self._insert_test_post()[0]
        post_id = post.id

        response = json.loads(test_app.get('/api/blog/{}'.format(post_id))
                              .data.decode())
        post_response = response['data']

        self.assertEqual(200, response['status'])
        self._assert_posts_equal(post_response, post)

        session.delete(post)
        session.commit()

    def testDelete(self):
        """Test deleteing a blog post"""
        post = self._insert_test_post()[0]
        post_id = post.id
        session.expunge(post)

        response = json.loads(test_app.delete('/api/blog/{}'.format(post_id))
                              .data.decode())
        response_data = response['data']

        self.assertEqual(200, response['status'])
        self.assertIsNone(session.query(BlogPost)
                          .filter_by(id=post_id).first())

    def testEdit(self):
        """Test editing a blog post"""
        post = self._insert_test_post()[0]
        post_id = post.id
        new_body = 'This is different!'
        post_json = {
            'title': post.title,
            'subtitle': post.subtitle,
            'body': new_body,
            'username': post.username
        }
        session.expunge(post)

        response = json.loads(test_app.put('/api/blog/{}'.format(post_id),
                                           data=post_json).data.decode())
        response_data = response['data']

        self.assertEquals(200, response['status'])
        self.assertEquals(new_body, response_data['body'])

        session.delete(session.query(BlogPost).filter_by(id=post.id).first())
        session.commit()


    def _assert_posts_equal(self, return_blog, create_blog):
        """
        Assert that a JSON blog post returned from the API is equal to the
        ORM object used to create it

        :param return_blog: the blog post returned from the API
        :param create_blog: the ORM object used to create the blog post
        """
        self.assertEquals(return_blog['title'], create_blog.title)
        self.assertEquals(return_blog['subtitle'], create_blog.subtitle)
        self.assertEquals(return_blog['author']['username'],
                          create_blog.username)
        self.assertEquals(return_blog['body'], create_blog.body)

    def _insert_test_post(self, num=1):
        """
        Manually insert blog posts into the test database

        :param num: the number of posts to insert
        :returns: the list of ORM objects created
        """
        posts = [BlogPost(
            title='Test post {}'.format(i),
            subtitle='Test post {}'.format(i),
            body='Test test test',
            username=self.username
        ) for i in range(num)]

        for post in posts:
            post.commit_to_session(session)

        return posts

