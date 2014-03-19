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
            },
            createPayment: function (amount, to) {
                return $http({method:"POST", url:"/payment/create", data: {"amount":amount, "paid_to":to}});
            },
            deletePayment: function (id) {
                return $http({method:"POST", url:"/payment/delete", data: {"id":id}});
            },
            createRule: function (regex) {
                return $http({method:"POST", url:"/rule/create", data: {"regex":regex}});
            }
        };
    }).controller ('MintyCtrl', function ($scope, mintyRepo) {
        $scope.buckets = [];
        $scope.payments = [];
        $scope.newName = "";
        $scope.newRule = "";
        $scope.newPayment = {to: "", amount: ""};

        $scope.createRule = function(regex){
           mintyRepo.createRule(regex).success(function(){
               $scope.updateModels();
               $scope.newRule = "";
           }); 
        };
        $scope.createPayment = function(amount, to){
           mintyRepo.createPayment(amount, to).success(function(){
               $scope.updateModels();
               $scope.newPayment = {to: "", amount: ""};
           }); 
        };
        $scope.deletePayment = function(id){
           mintyRepo.deletePayment(id).success(function(){
               $scope.updateModels();
           }); 
        };
        $scope.createBucket = function(){
           mintyRepo.createBucket($scope.newName).success(function(){
               $scope.updateModels();
               $scope.newName = "";
           }); 
        };
        $scope.deleteBucket = function(id){
           mintyRepo.deleteBucket(id).success(function(){
               $scope.updateModels();
           }); 
        };
        $scope.updateModels = function() {
            mintyRepo.getAllBuckets ().success (function (buckets){
                $scope.buckets = buckets;
            });
            mintyRepo.getAllPayments ().success (function (payments){
                $scope.payments = payments;
            });
        };
        $scope.updateModels();
    });

