app.controller('SettingsController', ['$scope', '$http', function($scope, $http) {
    $scope.changePassword = function() {
        console.log($scope.newPassword);
        if ($scope.newPassword.length < 6) {
            return; // should probably tell the user no.
        }
        if ($scope.newPassword !== $scope.confirmPassword) {
            return; // tell the user that the passwords did not match.
        }
        var request = {
            'oldPassword' : $scope.oldPassword,
            'newPassword' : $scope.newPassword
        };
        $http({
            method: 'POST',
            url: '/api/change_password',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            transformRequest: function(obj) {
                    var str = [];
                    for (var p in obj)
                        str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                    return str.join("&");
            },
            data: request
        }).then(function(response) {
            if (response.status == 200) {
                $scope.oldPassword = '';
                $scope.confirmPassword = '';
                $scope.newPassword = '';
            } else {
                // should probably tell the user that it failed somehow...
            }
        }, function(response) {
            console.log("error");
        });
    }

    $scope.createUser = function() {
        console.log($scope.newUserDisplayName);
        // Minimal error checking (should do a lot more)
        if ($scope.newUserPassword.length < 6) {
            return; // "should probably tell the user no"
        }
        if ($scope.newUserPassword !== $scope.newUserPasswordConfirm) {
            return; // "tell the user that the passwords did not match"
        }
        var request = {
            'username' : $scope.newUserName,
            'password' : $scope.newUserPassword,
            'display'  : $scope.newUserDisplayName
        };

        // Make the call to the api
        $http({
            method: 'POST',
            url: '/api/create_user',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            transformRequest: function(obj) {
                var str = [];
                for (var p in obj)
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                console.log("Request: " + str.join("&"));
                return str.join("&");
            },
            data: request
        }).then(function(response) {
            if (response.status == 200) {
                // "everything's gucci"
            } else {
                // totes not gucci
            }
        }, function(response) {
            console.log("Error in creating new user");
        });
        
    }
}]);
