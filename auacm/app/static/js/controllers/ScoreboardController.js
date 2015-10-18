app.controller('ScoreboardController', ['$scope', '$http', '$routeParams', function($scope, $http, $routeParams) {
    console.log('went here');
    var cid = $routeParams.cid; 
    $http.get('/api/competitions/' + cid)
        .then(function(response) {
            $scope.competition = response.data.data.competition;
            $scope.compProblems = response.data.data.compProblems;
            $scope.teams = response.data.data.teams;
            
            for (var i = 0; i < $scope.teams.length; i++) {
                var team = $scope.teams[i];
                var solved = 0;
                var time = 0;
                for (var problem in team.problemData) {
                    if (team.problemData[problem].status == 'solved') {
                        solved++;
                        time += team.problemData[problem].problemTime;
                    }
                }
                team.solved = solved;
                team.time = time;
            }
            
            $scope.teams.sort(function(a, b) {
                if (a.solved != b.solved) {
                    return b.solved - a.solved;
                } else {
                    return a.time - b.time;
                }
            });
            
            var rank = 0;
            var prevSolved = $scope.teams[0].solved;
            var prevTime = $scope.teams[0].solved;
            for (var i = 0; i < $scope.teams.length; i++) {
                var team = $scope.teams[i];
                console.log(team);
                if (team.solved < prevSolved) {
                    rank++;
                    team.rank = rank;
                } else if (team.solved == prevSolved && team.time > prevTime) {
                    rank++;
                    team.rank = rank;
                } else {
                    team.rank = rank;
                }
                prevSolved = team.solved;
                prevTime = team.time;
            }
        },
        function(error) {
            
        });
}]);