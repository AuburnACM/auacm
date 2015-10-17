app.controller('LoginController', ['$scope', '$http', '$window', 
        function($scope, $http, $window) {
            $scope.logIn = function() {
                // Here, we have the username and password, accessible by
                //     $scope.username and $scope.password. We need to call
                //     the backend to log in.
                var request = {
                    'username': $scope.username,
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
                        $window.location.href = 'http://localhost:5000/#/';
                }, function(response) {
                        console.log("error");
                });
            }
}]);
