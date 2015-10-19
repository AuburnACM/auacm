app.controller('JudgeController', ['$scope', '$http',
        function($scope, $http) {
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
                console.log("success");
        }, function(response) {
                console.log("error");
        });
    };
    var socket = io.connect('http://localhost:8000/judge')
    socket.on('connect', function() {
        console.log('connected');
    });
    $scope.problems.sort(function(a, b) {
        return a.name > b.name ? 1 : (a.name < b.name ? -1 : 0);
    });
}]);
