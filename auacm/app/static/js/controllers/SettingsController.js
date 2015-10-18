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
                    for(var p in obj)
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
}]);
