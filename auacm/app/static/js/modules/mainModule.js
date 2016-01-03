'use strict';

// Declare app level module which depends on views, and components
var app = angular.module('mainModule',[
    'ngRoute',
    'ui.bootstrap',
    'ngMessages',
    'dndLists'
]);

// configure our routes
app.config(function($routeProvider) {
    $routeProvider
        .when('/', { // route for the problems page
            templateUrl : 'static/html/blogList.html',
            controller : 'BlogListController',
            activetab : 'blog'
        })
        .when('/blog', { // route for the problems page
            templateUrl : 'static/html/blogList.html',
            controller : 'BlogListController',
            activetab : 'blog'
        })
        .when('/blog/create', {
            templateUrl : 'static/html/createBlogPost.html',
            controller : 'CreateBlogPostController',
            activetab : 'blog'
        })
        .when('/blog/:id', {
            templateUrl : 'static/html/blogPost.html',
            controller : 'BlogPostController',
            activetab : 'blog'
        })
        .when('/problems', { // route for the problems page
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
        .when('/competitions/create', {
            templateUrl: 'static/html/editCompetition.html',
            controller: 'EditCompetitionController',
            activetab: 'competitions'
        })
        .when('/competitions/:cid/edit', {
            templateUrl: 'static/html/editCompetition.html',
            controller: 'EditCompetitionController',
            activetab: 'competitions'
        })
        .when('/competitions/:cid', { // route for the competitions page
            templateUrl : 'static/html/scoreboard.html',
            controller : 'ScoreboardController',
            activetab : 'competitions'
        })
        .when('/competitions/:cid/teams', {
            templateUrl: 'static/html/teams.html',
            controller: 'TeamController',
            activetab: 'competitions'
        })
        .when('/settings', { // route for the settings page
            templateUrl : 'static/html/settings.html',
            controller : 'SettingsController',
            activetab : 'settings'
        })
        .when('/problems/create', { // route for problem creation page
            templateUrl: 'static/html/editProblem.html',
            controller : 'EditProblemController',
            activetab  : 'settings'
        })
        .when('/problems/:pid', { // route for the problem view page
            templateUrl : 'static/html/viewProblem.html',
            controller : 'ViewProblemController',
            activetab : 'problems'
        })
        .when('/problems/:pid/edit', { // route for problem edit page
            templateUrl : 'static/html/editProblem.html',
            controller : 'EditProblemController',
            activetab : 'problems'
        });
});

app.filter('secondsToDateTime', [function() {
    return function(seconds) {
        return new Date(1970, 0, 1).setSeconds(seconds);
    };
}]);

app.filter('iif', function () {
   return function(input, trueValue, falseValue) {
        return input ? trueValue : falseValue;
   };
});

app.directive('fileModel', ['$parse', function ($parse) {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            var model = $parse(attrs.fileModel);
            var modelSetter = model.assign;

            element.bind('change', function(){
                scope.$apply(function(){
                    modelSetter(scope, element[0].files[0]);
                });
            });
        }
    };
}]);

app.directive('markdown', function () {
    var converter = new showdown.Converter();
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            element.html(converter.makeHtml(scope.$eval(attrs.markdown)  || ''));
        }
    };

});
