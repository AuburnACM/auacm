app.controller('CreateCompetitionController', ['$scope', '$http', '$location',
        '$routeParams', function($scope, $http, $location, $routeParams) {
    $scope.compProblems = [];

    if ($routeParams.cid !== undefined) {
        // if the CID was specified, we need to load the data

    } else {

    }

    $scope.defaultStartTime = function() {
        // Set up the start time field; it defaults to 10:00 tomorrow
        // TODO: come up with a better default for this, maybe.
        var date = new Date(Date.now() + (24 * 3600 * 1000));
        return (date.getMonth() + 1) + "-" + date.getDate() + "-" +
                (date.getYear() + 1900) + " 10:00";
    };

    $scope.addProblem = function(problem) {
        $scope.compProblems.push(problem);
    };
    $scope.removeProblem = function(problem) {
        $scope.compProblems.splice($scope.compProblems.indexOf(problem), 1);
    };
    $scope.disableForm = false;

    var parseDate = function() {
        // get the string from scope and split it into its parts
        var parts = $scope.startTime.split(' ');

        // The parts consists of the day and time
        var day = parts[0].split('-');
        var time = parts[1].split(':');

        // Parse the parts into Numbers so that we can get the time from it.
        var month = parseInt(day[0]) - 1;
        var dayOfMonth = parseInt(day[1]);
        var year = parseInt(day[2]);
        var hourOfDay = parseInt(time[0]);
        var minute = parseInt(time[1]);

        // return the seconds of the date that the user entered.
        return new Date(year, month, dayOfMonth, hourOfDay, minute).valueOf() /
                1000; // to seconds instead of milliseconds.
    };

    $scope.createCompetition = function() {
        $scope.disableForm = true;
        $http({
            method: 'POST',
            url: '/api/competitions/',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            transformRequest: function(obj) {
                    var str = [];
                    for(var p in obj)
                        str.push(encodeURIComponent(p) + "=" +
                                encodeURIComponent(obj[p]));
                    return str.join("&");
            },
            data: request
        }).then(function(response) {
            $scope.disableForm = false;
            $scope.compName = '';
            $scope.compTime = '';
            $scope.compLength = '';
            $scope.compProblems = [];
            $location.path('/competitions/' + response.data.data.cid);
        }, function(response) {
            $scope.disableForm = false;
            console.log("error");
        });
    };
}]);

app.directive('dateFormatValidator', [function() {
    return {
        require : 'ngModel',
        link : function($scope, element, attrs, ngModel) {
            function setIncorrectFormat(bool) {
                ngModel.$setValidity('format', !bool);
            }

            function setDatePassed(date) {
                ngModel.$setValidity('tooEarly',
                        date.valueOf() > Date.now().valueOf());
            }

            ngModel.$parsers.push(function(value) {
                // get the string from scope and split it into its parts
                var parts = value.split(' ');

                if (parts.length !== 2) {
                    setIncorrectFormat(true);
                    return value;
                }

                // The parts consists of the day and time
                var day = parts[0].split('-');
                var time = parts[1].split(':');

                if (day.length !== 3 || time.length !== 2) {
                    setIncorrectFormat(true);
                    return value;
                }

                // Parse the parts into Numbers so that we can get the time from
                // it.
                var month = parseInt(day[0]) - 1;
                var dayOfMonth = parseInt(day[1]);
                var year = parseInt(day[2]);
                var hourOfDay = parseInt(time[0]);
                var minute = parseInt(time[1]);

                setIncorrectFormat(isNaN(month) || isNaN(dayOfMonth) ||
                    isNaN(year) || isNaN(hourOfDay) || isNaN(minute));

                setDatePassed(new Date(year, month, dayOfMonth, hourOfDay,
                    minute));

                return value;
            });
        }
    };
}]);

app.directive('contestLengthValidator', [function() {
    return {
        require : 'ngModel',
        link : function($scope, element, attrs, ngModel) {
            function setIncorrectFormat(bool) {
                ngModel.$setValidity('format', !bool);
            }

            ngModel.$parsers.push(function(value) {
                // get the string from scope and split it into its parts
                var parts = value.split(':');

                if (parts.length !== 2) {
                    setIncorrectFormat(true);
                    return;
                }

                setIncorrectFormat(isNaN(parseInt(parts[0])) ||
                        isNaN(parseInt(parts[1])));

                return value;
            });
        }
    };
}]);
