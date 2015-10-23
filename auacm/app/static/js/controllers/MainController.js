app.controller('MainController', ['$scope', '$http', '$route', '$window', 
        function($scope, $http, $route, $window) {
    $scope.username = 'placeholder'
    $scope.$route = $route
    $http.get('/api/me')
        .then(function(response) {
            $scope.user = response.data.data;
        },
        function(error) {
            
        });
    $http.get('/api/problems')
        .then(function(response) {
            $scope.problems = response.data.data;
        },
        function(error) {
            
        });
    $http.get('/api/blog')
        .then(function(response) {
            $scope.blogPosts = response.data.data;
        },
        function(error) {
            
        });
    $scope.signOut = function() {
        $http.get('/api/logout').then(function(response) {
                $window.location.href = 'http://localhost:5000/login';
        }, function(response) {
            console.log("error");
        });
    }
}]);
