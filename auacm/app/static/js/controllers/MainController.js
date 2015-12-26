app.controller('MainController', ['$scope', '$http', '$route', '$window', 
        function($scope, $http, $route, $window) {
    // Intialize user fields
    $scope.username = 'placeholder';
    $scope.displayName = 'placeholder';
    $scope.isAdmin = false;

    $scope.$route = $route;

    // Make a /api/me request and set the current user
    $http.get('/api/me')
        .then(function(response) {
            $scope.username = response.data.data.username;
            $scope.displayName = response.data.data.displayName;
            $scope.isAdmin = response.data.data.isAdmin;
        },
        function(error) {
            console.log("Error getting current user in MainController");
        });

    // Get the problems to display
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
                $window.location.href = 'http://' + $window.location.host + '/login';
        }, function(response) {
            console.log("error");
        });
    };

}]);
