'use strict';

// Declare app level module which depends on views, and components
var app = angular.module('loginModule',[]);
app.directive('onEnter', function () {
    return function (scope, element, attrs) {
        element.bind("keydown keypress", function (event) {
            if(event.which === 13) {
                scope.$apply(function (){
                    scope.$eval(attrs.myEnter);
                });
                event.preventDefault();
            }
        });
    };
});

app.directive('markdown', function () {
    var converter = new Showdown.converter();
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var htmlText = converter.makeHtml(element.text());
            element.html(htmlText);
        }
    };
});
