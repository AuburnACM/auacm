app.controller('ScoreboardController', ['$scope', '$http', '$routeParams', function($scope, $http, $routeParams) {
    console.log('went here');
    var cid = $routeParams.cid; 
    $http.get('/api/competitions/' + cid)
        .then(function(response) {
            $scope.compProblems = response.data.data.compProblems;
            $scope.teams = response.data.data.teams;
        },
        function(error) {
            
        });
}]);