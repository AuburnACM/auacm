app.controller('BlogListController', ['$scope', '$http', '$location', function($scope, $http, $location) {
    $scope.createPost = function() {
        $location.path('/blog/create');
    };
}]);
