'use strict';

// Declare app level module which depends on views, and components
var app = angular.module('mainModule',['ngRoute']);
// configure our routes
app.config(function($routeProvider) {
    $routeProvider
        .when('/', { // route for the problems page
            templateUrl : 'static/html/problems.html',
            controller : 'ProblemsController',
            activetab : 'problems'
        })
        .when('/judge', { // route for the judge page
            templateUrl : 'static/html/judge.html',
            controller : 'JudgeController',
            activetab : 'judge'
        })
        .when('/competitions', { // route for the competitions page
            templateUrl : 'static/html/competitions.html',
            controller : 'CompetitionsController',
            activetab : 'competitions'
        })
        .when('/competitions/:cid', { // route for the competitions page
            templateUrl : 'static/html/scoreboard.html',
            controller : 'ScoreboardController',
            activetab : 'competitions'
        })
});

app.filter('secondsToDateTime', [function() {
    return function(seconds) {
        return new Date(1970, 0, 1).setSeconds(seconds);
    };
}])
