app.controller('CreateCompetitionController', ['$scope', '$http', '$location',
        '$routeParams',
        function($scope, $http, $location, $routeParams) {
    if ($routeParams.cid != undefined) {
        // if the CID was specified, we need to load the data
        
    } else {
        
    }
    $scope.disableForm = false;
    $scope.createCompetition = function() {
        $scope.disableForm = true;
        $http({
            method: 'POST',
            url: '/api/competitions/',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            transformRequest: function(obj) {
                    var str = [];
                    for(var p in obj)
                        str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                    return str.join("&");
            },
            data: request
        }).then(function(response) {
            $scope.disableForm = false;
            $scope.compName = '';
            $scope.compTime = '';
            $scope.compLength = '';
            $scope.compProblems = [];
            $location.path('/competitions/' + response.data.data.cid);
        }, function(response) {
            $scope.disableForm = false;
            console.log("error");
        });
    };
}]);
