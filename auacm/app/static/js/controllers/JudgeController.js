app.controller('JudgeController', ['$scope', '$http', function($scope, $http) {
    $scope.username = 'placeholder'
    $http.get('/api/me')
        .then(function(response) {
            $scope.username = response.data.data.displayName;
        },
        function(error) {
            
        })
    $http.get('/api/problems')
        .then(function(response) {
            $scope.problems = response.data.data;
        },
        function(error) {
            
        });
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
}]);
