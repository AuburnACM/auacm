app.controller('BlogPostController', ['$scope', '$http', '$routeParams',
        function($scope, $http, $routeParams) {
    var id = $routeParams.id;
    for (var i = 0; i < $scope.blogPosts.length; i++) {
        var post = $scope.blogPosts[i];
        if (post.id == id) {
            $scope.post = post;
            break;
        }
    }
}]);
