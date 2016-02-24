app.controller('RankingController', ['$scope', '$route', '$http',
    function($scope, $route, $http) {
    $scope.display = false;
    $scope.ranks = [];

    var getRanking = function() {
        $http.get('/api/ranking')
            .then(function(response) {
                $scope.ranks = response.data.data;
                $scope.display = true;
            },
            function (error) {
                console.error(error);
            })
    };
    getRanking();
}]);
