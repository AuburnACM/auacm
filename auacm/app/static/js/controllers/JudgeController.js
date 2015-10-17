app.controller('JudgeController', ['$scope', '$http', 'socket',
        function($scope, $http, socket) {
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
    
    var socket1 = io('http://localhost/judge');
    socket1.on('connect', function() {
        console.log('connected');
    }); 
}]);
