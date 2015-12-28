app.controller('EditCompetitionController', ['$scope', '$http', '$location',
        '$routeParams', function($scope, $http, $location, $routeParams) {
    $scope.compProblems = [];
    var competition;

    if ($routeParams.cid !== undefined) {
        // if the CID was specified, we need to load the data
        $scope.title = 'Edit Competition';
        $http.get('/api/competitions/' + $routeParams.cid)
            .then(function(response) {
                competition = response.data.data.competition;

                var date = new Date(competition.startTime * 1000);
                var startTime = '';
                startTime += (date.getMonth() + 1) + '-' + date.getDate() +
                        '-' + date.getFullYear();
                startTime += ' ' + date.getHours() + ':';
                if (date.getMinutes() < 10) {
                    startTime += '0';
                }
                startTime += date.getMinutes();

                $scope.compName = competition.name;
                $scope.startTime = startTime;
                $scope.compLength = (competition.length / 3600) + ':';
                if (competition.length % 60 < 10) {
                    $scope.compLength += '0';
                }
                $scope.compLength += competition.length % 60;

                var compProblems = response.data.data.compProblems;
                // linear search to find the problems that are in the
                //     competition.
                for (var label in compProblems) {
                    for (var i = 0; i < $scope.problems.length; i++) {
                        if ($scope.problems[i].pid ===
                                compProblems[label].pid) {
                            $scope.addProblem($scope.problems[i]);
                            break;
                        }
                    }
                }
            },
            function(error) {

            });
    } else {
        $scope.title = 'Create Competition';
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

    var getStartTimeSeconds = function() {
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

    var getLengthSeconds = function() {
        var parts = $scope.compLength.split(':');
        return parseInt(parts[0]) * 3600 + parseInt(parts[1]) * 60;
    };

    $scope.createCompetition = function() {
        $scope.disableForm = true;
        var problems = [];
        for (var i = 0; i < $scope.compProblems.length; i++) {
            problems.push({
                'label': String.fromCharCode("A".charCodeAt(0) + i),
                'pid': $scope.compProblems[i].pid
            });
        }
        var request = {
            'name': $scope.compName,
            'start_time': getStartTimeSeconds(),
            'length': getLengthSeconds(),
            'problems': angular.toJson(problems)
        };
        $http({
            method: competition === undefined ? 'POST' : 'PUT',
            url: competition === undefined ? '/api/competitions' :
                    '/api/competitions/' + competition.cid,
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
            console.error(response);
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
            function setTimeTooShort(hours, minutes) {
                ngModel.$setValidity('length', (hours === 0 && minutes > 0) ||
                        (hours > 0 && minutes >= 0));
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

                var hours = parseInt(parts[0]);
                var minutes = parseInt(parts[1]);
                setTimeTooShort(hours, minutes);

                return value;
            });
        }
    };
}]);

app.filter('indexToCharCode', [function() {
    return function(index) {
        return String.fromCharCode("A".charCodeAt(0) + index);
    };
}]);
