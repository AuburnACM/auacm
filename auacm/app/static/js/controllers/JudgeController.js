app.controller('JudgeController', ['$scope', '$rootScope', '$http',
        '$routeParams', '$window',
        function($scope, $rootScope, $http, $routeParams, $window) {
    $scope.problem = '';
    $scope.submitted = [];
    $scope.python = {version: 'py'};
    $scope.problemSelected = false;
    $scope.fileSelected = false;

    if ($routeParams.problem) {
        for (var i = 0; i < $scope.problems.length; i++) {
            var problem = $scope.problems[i];
            if (problem.pid === parseInt($routeParams.problem)) {
                $scope.problem = problem;
                $scope.problemSelected = true;
                break;
            }
        }
    }

    var STATUS_NAMES = {
        compile: 'Compilation Error',
        runtime: 'Runtime Error',
        running: 'Running',
        timeout: 'Time Limit Exceeded',
        incorrect: 'Incorrect',
        correct: 'Correct'
    };

    $scope.$watch('problem', function(newValue, oldValue) {
        if (typeof newValue === 'string') {
            for (var i = 0; i < $scope.problems.length; i++) {
                var problem = $scope.problems[i];
                if (problem.name.toLowerCase().trim() ===
                        newValue.toLowerCase().trim()) {
                    $scope.problem = problem;
                    $scope.problemSelected = true;
                    return;
                }
            }
            $scope.problemSelected = false;
        } else {
            $scope.problemSelected = true;
        }
    });

    $scope.wat = function() {
        console.log('wat');
    };

    $scope.submit = function() {
        if ($scope.file.name.toLowerCase().includes('bern')) {
            $rootScope.bernitize = 'bernitdown';
        }
        var fd = new FormData();
        fd.append('pid', $scope.problem.pid);
        fd.append('file', $scope.file);
        if ($scope.file.name.endsWith('.py')) {
            fd.append('python', $scope.python.version);
        }
        var submission = {};
        var name;
        for (var i = 0; i < $scope.problems.length; i++) {
            if ($scope.problems[i].pid === $scope.problem.pid) {
                name = $scope.problems[i].name;
                break;
            }
        }
        submission.problem = name;
        submission.fileName = $scope.file.name;
        submission.status = 'uploading';
        submission.statusDescription = 'Uploading';
        $http({
            method: 'POST',
            url: '/api/submit',
            headers: {'Content-Type': undefined},
            transformRequest: angular.identity,
            data: fd
        }).then(function(response) {
            submission.submissionId = response.data.data.submissionId;
            submission.status = 'compiling';
            submission.testNum = 0;
            submission.statusDescription = 'Compiling';
            $scope.submitted.push(submission);
        }, function(response) {
            console.error(response);
        });
    };

    $scope.socket.on('status', function(event) {
        for (var i = 0; i < $scope.submitted.length; i++) {
            if (event.submissionId === $scope.submitted[i].submissionId) {
                var submitted = $scope.submitted[i];
                submitted.status = event.status;
                submitted.testNum = event.testNum;
                submitted.statusDescription = STATUS_NAMES[submitted.status];
                $scope.$apply();
                break;
            }
        }
    });

    if ($scope.problems) {
        $scope.problems.sort(function(a, b) {
            return a.name > b.name ? 1 : (a.name < b.name ? -1 : 0);
        });
    }
}]);
