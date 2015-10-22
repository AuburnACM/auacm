app.controller('MainController', ['$scope', '$http', '$route', '$window', 
        function($scope, $http, $route, $window) {
    $scope.username = 'placeholder'
    $scope.$route = $route
    $http.get('/api/problems')
        .then(function(response) {
            $scope.problems = response.data.data;
        },
        function(error) {
            
        });
    $scope.signOut = function() {
        // Here, we have the username and password, accessible by
        //     $scope.username and $scope.password. We need to call
        //     the backend to log in.
        $http.get('/api/logout').then(function(response) {
                $window.location.href = 'http://localhost:5000/login';
        }, function(response) {
            console.log("error");
        });
    }
}]);
