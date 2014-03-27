angular.module('project', [])
    .factory ('mintyRepo', function ($http){
        return {
            getAllBuckets: function (range) {
                return $http({method:"GET", url:"/buckets", params: {"range":range}});
            },
            getAllPayments: function (range) {
                return $http({method:"GET", url:"/payments", params: {"range":range}});
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
            getAllRules: function (range) {
                return $http({method:"GET", url:"/rules", params: {"range":range}});
            },
            getTotals: function (range) {
                return $http({method:"GET", url:"/totals", params: {"range":range}});
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
        $scope.totals = { income: 0, payments: 0};
        $scope.incomeBuckets = [];
        $scope.payments = [];
        $scope.newName = "";
        $scope.newRule = "";
        $scope.rules = [];
        $scope.showAllPayments = false;
        $scope.newPayment = {to: "", amount: ""};
        $scope.page = "summary";
        $scope.showRules = [];

        $scope.dayRange = 30;

        $scope.days = [7, 30, 90, 365];
        
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
          // mintyRepo.deletePayment(id).success(function(){
          //     $scope.updateModels();
          // }); 
        };

        $scope.createBucket = function(){
           mintyRepo.createBucket($scope.newName).success(function(){
               $scope.updateModels();
               $scope.newName = "";
           }); 
        };
        $scope.deleteBucket = function(id){
            var ask = confirm("There are rules on this bucket, are you sure you want to delete them and this?");
            if (ask) {
                var rules = $scope.getRulesForBucket(id);
                for (var i=0;i<rules.length;i++) {
                    $scope.deleteRule(rules[i].rule_id);
                }
                mintyRepo.deleteBucket(id).success(function(){
                    $scope.updateModels();
                }); 
            } 
        };
        $scope.deleteRule = function(id){
           mintyRepo.deleteRule(id).success(function(){
               $scope.updateModels();
           }); 
        };
        $scope.getAllBuckets  = function() {
            return $scope.buckets.concat($scope.incomeBuckets);
        };
        $scope.updateModels = function() {
            mintyRepo.getTotals($scope.dayRange).success (function (totals){
                $scope.totals = totals;
            });
            mintyRepo.getAllBuckets($scope.dayRange).success (function (buckets){
                $scope.buckets = buckets.filter(function(b){return b.amount <= 0;});
                $scope.incomeBuckets = buckets.filter(function(b){return b.amount > 0;});
            });
            mintyRepo.getAllPayments($scope.dayRange).success (function (payments){
                $scope.payments = payments;
            });
            mintyRepo.getAllRules($scope.dayRange).success (function (rules){
                $scope.rules = rules;
            });
        };
        $scope.updateModels();
    });

