app.controller('JudgeController', ['$scope', '$http',
        function($scope, $http) {
    $scope.submitted = [];
    $scope.submit = function() {
        var fd = new FormData();
        fd.append('pid', $scope.pid);
        fd.append('file', $scope.file);
        $http({
            method: 'POST',
            url: '/api/submit',
            headers: {'Content-Type': undefined},
            transformRequest: angular.identity,
            data: fd
        }).then(function(response) {
            var submission = {
                'submissionId' : response.data.data.submissionId,
                'status' : 'compiling',
                'testNum' : 0
            }
            $scope.submitted.push(submission);
        }, function(response) {
                console.log("error");
        });
    };
    var socket = io.connect('http://localhost:5000/judge')
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
    })
    $scope.problems.sort(function(a, b) {
        return a.name > b.name ? 1 : (a.name < b.name ? -1 : 0);
    });
}]);
