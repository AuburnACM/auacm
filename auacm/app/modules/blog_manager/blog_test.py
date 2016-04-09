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
        pass

    def testGetAll(self):
        """Test getting all the blog posts"""
        # Put some posts in the test database
        posts = self._insertTestPost(3)
        post_ids = [post.id for post in posts]

        response = json.loads(test_app.get('/api/blog').data.decode())
        post_response = response['data']

        self.assertEqual(200, response['status'])
        for (resp, post) in zip(post_response, posts):
            self._assertPostsEqual(resp, post)

        for post_id in post_ids:
            post = session.query(BlogPost).filter(BlogPost.id==post_id).first()
            session.delete(post)

    def testGetOne(self):
        """Test getting just one blog post"""
        post =  self._insertTestPost()[0]
        post_id = post.id

        response = json.loads(test_app.get('/api/blog/{}'.format(post_id))
                              .data.decode())
        post_response = response['data']

        self.assertEqual(200, response['status'])
        self._assertPostsEqual(post_response, post)

        session.delete(post)


    def testEdit(self):
        """Test editing a blog post"""
        # TODO
        pass

    def testDelete(self):
        """Test deleting a blog post"""
        # TODO
        pass

    def _assertPostsEqual(self, return_blog, create_blog):
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

    def _insertTestPost(self, num=1):
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
        ) for i in range(3)]
        for post in posts:
            post.commit_to_session(session)
        return posts
