angular.module('project', [])
    .factory ('mintyRepo', function ($http){
        return {
            getAllBuckets: function () {
                return $http.get("/getAllBuckets");
            },
            getAllPayments: function () {
                return $http.get("/getAllPayments");
            },
            createBucket: function (name) {
                return $http({method:"POST",
                              url:"/bucket/create",
                              data: {"name":name}});
            }
        };
    }).controller ('MintyCtrl', function ($scope, mintyRepo) {
        $scope.buckets = [];
        $scope.payments = [];
        $scope.newName = "";
        $scope.createBucket = function(){
           mintyRepo.createBucket($scope.newName).success(function(){
               $scope.updateBuckets();
           }); 
        };
        $scope.updateBuckets = function() {
            mintyRepo.getAllBuckets ().success (function (buckets){
                $scope.buckets = buckets;
            });
            //mintyRepo.getAllPayments ().success (function (ps){
            //    $scope.payments= ps;
            //});
        };
        $scope.updateBuckets();
    });

