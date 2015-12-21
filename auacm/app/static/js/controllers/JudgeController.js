app.controller('JudgeController', ['$scope', '$http', '$routeParams', '$window',
        function($scope, $http, $routeParams, $window) {
    $scope.pid = $routeParams.problem;
    $scope.submitted = [];
    $scope.submit = function() {
        var fd = new FormData();
        fd.append('pid', $scope.pid);
        fd.append('file', $scope.file);
        var submission = {};
        var name;
        for (var i = 0; i < $scope.problems.length; i++) {
            if ($scope.problems[i].pid === $scope.pid) {
                name = $scope.problems[i].name;
                break;
            }
        }
        submission.problem = name;
        submission.fileName = $scope.file.name;
        submission.status = 'uploading';
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
            $scope.submitted.push(submission);
        }, function(response) {
            console.log("error");
        });
    };
    var socket = io.connect('http://' + $window.location.host + '/judge');
    socket.on('connect', function() {
        console.log('connected');
    });
    socket.on('status', function(event) {
        for (var i = 0; i < $scope.submitted.length; i++) {
            if (event.submissionId === $scope.submitted[i].submissionId) {
                var submitted = $scope.submitted[i];
                submitted.status = event.status;
                submitted.testNum = event.testNum;
                $scope.$apply();
                break;
            }
        }
    });
    $scope.problems.sort(function(a, b) {
        return a.name > b.name ? 1 : (a.name < b.name ? -1 : 0);
    });
}]);
