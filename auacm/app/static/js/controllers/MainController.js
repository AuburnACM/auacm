app.controller('MainController', ['$scope', '$rootScope', '$http', '$route',
        '$window', function($scope, $rootScope, $http, $route, $window) {
    // Intialize user fields
    $scope.isAdmin = false;
    $scope.$route = $route;
    $scope.isOpen = false;

    var closeDropdown = function() {
        $scope.isOpen = false;
    };

    // Make a /api/me request and set the current user
    $scope.$watch('loggedIn', function(newVal, oldVal) {
        if (newVal) {
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
        } else {
            $scope.username = ''; // clear the username field
            $scope.password = ''; // clear the password field
            $scope.displayName = 'Log in';
            $scope.isAdmin = false;
            $scope.loggedIn = false;
        }
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
            'username': $scope.loginUsername,
            'password': $scope.password
        };
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
            $scope.failedLogin = false;
            getProblems();
            closeDropdown();
        }, function(response) {
            $scope.failedLogin = true;
        });
    };

    $scope.socket = new Socket('ws://' + $window.location.host + '/websocket');
    $rootScope.bernitize = '';
}]);
