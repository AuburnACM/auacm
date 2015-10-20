app.controller('ScoreboardController', ['$scope', '$http', '$routeParams', function($scope, $http, $routeParams) {
    var cid = $routeParams.cid; 
    var genScoreboard = function() {
        for (var i = 0; i < $scope.teams.length; i++) {
            var team = $scope.teams[i];
            var solved = 0;
            var time = 0;
            for (var problem in team.problemData) {
                if (team.problemData[problem].status == 'correct') {
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
        
        var rank = 1;
        var prevSolved = $scope.teams[0].solved;
        var prevTime = $scope.teams[0].solved;
        $scope.teams[0].rank = rank;
        for (var i = 1; i < $scope.teams.length; i++) {
            var team = $scope.teams[i];
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
    }
    $http.get('/api/competitions/' + cid)
        .then(function(response) {
            $scope.competition = response.data.data.competition;
            $scope.compProblems = response.data.data.compProblems;
            $scope.teams = response.data.data.teams;
            genScoreboard();
        },
        function(error) {
            
        });
    var socket = io.connect('http://localhost:5000/judge')
    socket.on('connect', function() {
        console.log('connected');
    });
    var viewed = [];
    socket.on('status', function(event) {
        if (viewed.indexOf(event.submissionId) > -1 || event.status == 'running') {
            // The scoreboard doesn't care about the problem if it's not done or
            // if we've already seen it.
            return;
        }
        viewed.push(event.submissionId);
        for (var i = 0; i < $scope.teams.length; i++) {
            if ($scope.teams[i].users.indexOf(event.username) != -1) {
                // If the user that submitted the problem was on this team...
                var problem = $scope.teams[i].problemData[event.problemId];
                problem.submitCount++;
                if (problem.status === 'correct') {
                    // problem is already correct, so we'll skip this problem.
                    return;
                }
                if (event.status === 'correct') {
                    problem.problemTime = Math.floor((event.submitTime 
                            - $scope.competition.startTime) / 1000 / 60)
                            + (problem.submitCount - 1) * 20;
                    problem.status = 'correct';
                } else {
                    problem.status = 'incorrect';
                }
            }
        }
        genScoreboard();
        $scope.$apply();
    })
}]);
