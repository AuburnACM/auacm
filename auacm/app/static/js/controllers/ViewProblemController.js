app.controller('ViewProblemController', ['$scope', '$route', '$routeParams', '$http', '$window',
        function($scope, $route, $routeParams, $http, $window) {
    $scope.pid = $routeParams.pid;

    $http.get('/api/problems/' + $scope.pid)
        .then(function(response) {
            $scope.current_prob = response.data.data;
        }, function(error) {
            console.log(error.data.status + ': ' + error.data.error);
            if (error.status == 404) {
                $window.location.href = 'http://' + $window.location.host +
                        '/#/404';
            }
        }
    );
}]);
