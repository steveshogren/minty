angular.module('project', [])
    .factory ('mintyRepo', function ($http){
        return {
            getAllBuckets: function () {
                return $http.get("/getAllBuckets");
            },
            getAllPayments: function () {
                return $http.get("/payments");
            },
            deleteBucket: function (id) {
                return $http({method:"POST", url:"/bucket/delete", data: {"id":id}});
            },
            createBucket: function (name) {
                return $http({method:"POST", url:"/bucket/create", data: {"name":name}});
            },
            createPayment: function (amount, to, date) {
                return $http({method:"POST", url:"/payment/create", data: {"amount":amount, "paid_to":to, "date": date}});
            },
            deletePayment: function (id) {
                return $http({method:"POST", url:"/payment/delete", data: {"id":id}});
            },
            getAllRules: function () {
                return $http.get("/rule/getAll");
            },
            deleteRule: function (id) {
                return $http({method:"POST", url:"/rule/delete", data: {"id":id}});
            },
            createRule: function (regex, bid) {
                return $http({method:"POST", url:"/rule/create", data: {"regex":regex, "bucket_id":bid}});
            }
        };
    }).controller ('MintyCtrl', function ($scope, mintyRepo) {
        $scope.buckets = [];
        $scope.payments = [];
        $scope.newName = "";
        $scope.newRule = "";
        $scope.rules = [];
        $scope.showAllPayments = false;
        $scope.newPayment = {to: "", amount: ""};
        $scope.getPayments = function() {
            if ($scope.showAllPayments) {
                return $scope.payments;
            } 
            return $scope.payments.filter(function(p){return p.bucket_id == null;});
        };
        $scope.getRulesForBucket = function(bid) {
            return $scope.rules.filter(function(r){return r.bucket_id === bid;});
        };
        $scope.togglePayments = function() {
            return $scope.showAllPayments = !$scope.showAllPayments;
        };
        $scope.createRule = function(regex, bid){
           mintyRepo.createRule(regex, bid).success(function(){
               $scope.updateModels();
               $scope.newRule = "";
           }); 
        };
        $scope.createPayment = function(amount, to, date){
           mintyRepo.createPayment(amount, to, date).success(function(){
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
        $scope.deleteRule = function(id){
           mintyRepo.deleteRule(id).success(function(){
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
            mintyRepo.getAllRules ().success (function (rules){
                $scope.rules = rules;
            });
        };
        $scope.updateModels();
    });

