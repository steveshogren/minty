angular.module('project', ['angularCharts'])
    .factory ('mintyRepo', function ($http){
        return {
            bucketsUrl: "/buckets",
            rulesUrl: "/rules",
            totalsUrl: "/totals",
            paymentsUrl: "/payments",
            gett: function(url, range) {
                return $http({method:"GET", url:url, params: {"range":range}});
            },
            deleteBucket: function (id) {
                return $http({method:"POST", url:"/bucket/delete", data: {"id":id}});
            },
            createBucket: function (name) {
                return $http({method:"POST", url:"/bucket/create", data: {"name":name}});
            },
            deleteRule: function (id) {
                return $http({method:"POST", url:"/rule/delete", data: {"id":id}});
            },
            import: function () {
                return $http({method:"POST", url:"/import", data: {}});
            },
            createRule: function (regex, bid) {
                return $http({method:"POST", url:"/rule/create", data: {"regex":regex, "bucket_id":bid}});
            }
        };
    }).controller ('MintyCtrl', function ($scope, mintyRepo) {
        $scope.paymentShape = ["bucket_id", "amount"];

        $scope.isType = function(o, type) {
            return _.reduce(type, function(accum, key){
                return accum && (key in o);
            }, true);
        };
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
        $scope.sumedIncomePayments = function() {
            return ($scope.totals.income + $scope.totals.payments).toFixed(2);
        };

        $scope.import = function() {
            $scope.blankModels();
            mintyRepo.import().success(function(){
                $scope.dayRangeChange();
            }); 
        };
        $scope.blankModels = function() {
            $scope.rules = [];
            $scope.buckets = [];
            $scope.incomeBuckets = [];
            $scope.totals = { income: 0, payments: 0};
            $scope.payments = [];
        };
        $scope.getPayments = function() {
            if ($scope.showAllPayments) {
                return $scope.payments;
            } 
            return $scope.payments.filter(function(p){return p.bucket_id == null;});
        };
        $scope.getRulesForBucket = function(bid) {
            var bucketRules = $scope.rules.filter(function(r){return r.bucket_id === bid;});
            return _.sortBy(bucketRules, function(b) {return b.amount});
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
        $scope.createBucket = function(){
           mintyRepo.createBucket($scope.newName).success(function(){
               $scope.updateModels();
               $scope.newName = "";
           }); 
        };
        $scope.deleteBucket = function(id){
            var rules = $scope.getRulesForBucket(id);
            var msg = (rules.length > 0)
                ? "There are rules on this bucket, delete this rule and buckets?"
                : "Are you sure you want to delete this bucket?";
            var ask = confirm(msg);
            if (ask) {
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
        $scope.getAllBuckets = function() {
            return $scope.buckets.concat($scope.incomeBuckets);
        };
        $scope.dayRangeChange = function() {
            $scope.blankModels();
            $scope.updateModels();
        };
        $scope.updateModels = function() {
            mintyRepo.gett(mintyRepo.totalsUrl, $scope.dayRange).success (function (totals){
                $scope.totals = totals;
            });
            mintyRepo.gett(mintyRepo.bucketsUrl, $scope.dayRange).success (function (buckets){
                $scope.buckets = buckets.filter(function(b){return b.amount <= 0;});
                $scope.incomeBuckets = buckets.filter(function(b){return b.amount > 0;});
            });
            mintyRepo.gett(mintyRepo.paymentsUrl, $scope.dayRange).success (function (payments){
                $scope.payments = payments;
            });
            mintyRepo.gett(mintyRepo.paymentsUrl, $scope.dayRange).success (function (rules){
                $scope.rules = rules;
            });
        };
        $scope.updateModels();
    });

