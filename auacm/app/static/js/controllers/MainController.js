app.controller('MainController', ['$scope', '$http', '$route', '$window',
        function($scope, $http, $route, $window) {
    // Intialize user fields
    $scope.displayName = 'placeholder';
    $scope.isAdmin = false;
    $scope.loggedIn = false;

    $scope.$route = $route;

    // Make a /api/me request and set the current user
    $http.get('/api/me')
        .then(function(response) {
            $scope.username = response.data.data.username;
            $scope.displayName = response.data.data.displayName;
            $scope.isAdmin = response.data.data.isAdmin;
            $scope.loggedIn = true;
        },
        function(error) {
            console.log("Error getting current user in MainController");
        });

    // Get the problems to display
    var getProblems = function() {
        $http.get('/api/problems')
            .then(function(response) {
                $scope.problems = response.data.data;
            },
            function(error) {

            });
    };
    getProblems();

    $http.get('/api/blog')
        .then(function(response) {
            $scope.blogPosts = response.data.data;
        },
        function(error) {

        });

    $scope.logOut = function() {
        $http.get('/api/logout').then(function(response) {
            $scope.loggedIn = false;
            getProblems();
        }, function(response) {
            console.log("error");
        });
    };

    $scope.logIn = function() {
        // Here, we have the username and password, accessible by
        //     $scope.username and $scope.password. We need to call
        //     the backend to log in.
        var request = {
            'username': $scope.username,
            'password': $scope.password
        };
        console.log(request);
        $http({
            method: 'POST',
            url: '/api/login',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            transformRequest: function(obj) {
                    var str = [];
                    for(var p in obj)
                        str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                    return str.join("&");
            },
            data: request
        }).then(function(response) {
            $scope.loggedIn = true;
            getProblems();
        }, function(response) {
            console.log("error");
        });
    };
}]);
