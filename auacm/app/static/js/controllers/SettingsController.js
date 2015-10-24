app.controller('SettingsController', ['$scope', '$http', function($scope, $http) {
    $scope.changePassword = function() {
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
            $scope.oldPassword = '';
            $scope.confirmPassword = '';
            $scope.newPassword = '';
        }, function(response) {
            console.log("error");
        });
    }

    // Local variables for Create User form
    $scope.newUserMatch = true;
    $scope.newUserSubmitted = false;
    $scope.failedCreate = false;

    $scope.createUser = function(newUserForm) {
        $scope.newUserSubmitted = true;
        if (!newUserForm.$valid) {
            $scope.failedCreate = true;
            return;
        }

        // Validate that the password and confirm match
        if ($scope.newUserPassword !== $scope.newUserPasswordConfirm) {
            $scope.newUserMatch = false;
            return;
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
                return str.join("&");
            },
            data: request
        }).then(function(response) {
            // Everything's gucci, so reset the form
            $scope.newUserName = '';
            $scope.newUserDisplayName = '';
            $scope.newUserPassword = '';
            $scope.newUserPasswordConfirm = '';
            $scope.newUserMatch = true;
            $scope.failedCreate = false;
        }, function(response) {
            console.log("Error in creating new user");
            $scope.failedCreate = true;
        });

        // Pristine has to be reset here for scope issues
        newUserForm.$setPristine();
    }

    $scope.getNewUserMatch = function() {
        return $scope.newUserMatch;
    }

    $scope.getNewUserSubmitted = function() {
        return $scope.newUserSubmitted;
    }

    $scope.getFailedCreate = function() {
        return $scope.failedCreate;
    }
}]);
