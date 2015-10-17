'use strict';

// Declare app level module which depends on views, and components
var app = angular.module('mainModule',['ngRoute', 'ngWebsocket']);
// configure our routes
app.config(function($routeProvider) {
    $routeProvider
        .when('/', { // route for the home page
            templateUrl : 'static/html/problems.html',
            controller : 'ProblemsController',
            activetab : 'problems'
        })
        .when('/judge', { // route for the about page
            templateUrl : 'static/html/judge.html',
            controller : 'JudgeController',
            activetab : 'judge'
        })
});
