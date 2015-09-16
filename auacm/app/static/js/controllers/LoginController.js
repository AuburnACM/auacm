app.controller('LoginController', ['$scope', '$http', '$window', 
        function($scope, $http, $window) {
            $scope.logIn = function() {
                // Here, we have the username and password, accessible by
                //     $scope.username and $scope.password. We need to call
                //     the backend to log in.
                console.log("this is a test");
            }
}]);
