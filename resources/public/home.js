angular.module('project', [])
    .factory ('mintyRepo', function ($http){
        return {
            getAllBuckets: function () {
                return $http.get("/getAllBuckets");
            },
            getAllPayments: function () {
                return $http.get("/getAllPayments");
            },
            deleteBucket: function (id) {
                return $http({method:"POST", url:"/bucket/delete", data: {"id":id}});
            },
            createBucket: function (name) {
                return $http({method:"POST", url:"/bucket/create", data: {"name":name}});
            }
        };
    }).controller ('MintyCtrl', function ($scope, mintyRepo) {
        $scope.buckets = [];
        $scope.payments = [];
        $scope.newName = "";
        $scope.newPayment = {to: "", amount: ""};
        $scope.createPayment = function(to, amount){
           mintyRepo.createPayment(to, amount).success(function(){
               $scope.updateBuckets();
           }); 
        };
        $scope.createBucket = function(){
           mintyRepo.createBucket($scope.newName).success(function(){
               $scope.updateBuckets();
           }); 
        };
        $scope.deleteBucket = function(id){
           mintyRepo.deleteBucket(id).success(function(){
               $scope.updateBuckets();
           }); 
        };
        $scope.updateBuckets = function() {
            mintyRepo.getAllBuckets ().success (function (buckets){
                $scope.buckets = buckets;
            });
        };
        $scope.updateBuckets();
    });

